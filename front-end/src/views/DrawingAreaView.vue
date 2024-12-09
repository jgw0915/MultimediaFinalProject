<template>
    <div class="drawing-app">
        <div>
            <div class="canvas-container">
                <canvas ref="canvas" class="drawing-canvas" @mousedown="startDrawing" @mousemove="draw"
                    @mouseup="stopDrawing" @mouseleave="stopDrawing"></canvas>
            </div>
        </div>
        <div class="tools">
            <div class="tool-buttons">
                <button @click="selectTool('pencil')">✏️ Pencil</button>
                <button @click="selectTool('eraser')">🧹 Eraser</button>
                <button @click="addText">🔤 Add Text</button>
                <button @click="selectTool('selector')">👆 Selector</button>
                <button @click="addWatermark">🌊 Add Watermark</button>
                <button @click="extractWatermark">🕵️ Extract Watermark</button>
            </div>

            <div class="shape-buttons">
                <button @click="selectTool('circle')">⚪ Circle</button>
                <button @click="selectTool('rectangle')">▭ Rectangle</button>
                <button @click="selectTool('line')">➖ Line</button>
            </div>

            <div class="colors">
                <label v-for="color in colors" :key="color" :style="{ backgroundColor: color }" class="color"
                    @click="selectColor(color)"></label>
            </div>

            <input type="file" accept="image/*" @change="selectImage" />
            <button @click="downloadCanvas">Download</button>
            <div class="inputBox">
                <textarea v-model="postContent" placeholder="Share your thoughts or story!"></textarea>
            </div>
            <button class="button" @click="submitPost">Post</button>
        </div>
    </div>
</template>

<script>

export default {
    data() {
        return {
            userProfile: {
                email: "",
                nickname: "",
                profileImage: "",
                profileCover: "",
            },
            postContent: '', // 貼文內容
            canvas: null,
            ctx: null,
            drawing: false,
            tool: 'pencil', // 預設工具為畫筆
            selectedColor: '#000000',
            colors: ['#000000', '#FF0000', '#00FF00', '#0000FF', '#FFFF00', '#FF00FF', '#00FFFF', '#FFFFFF'],
            objects: [], // 用於管理畫布上的物件
            selectedObject: null, // 目前被選中的物件
            isDragging: false, // 是否正在拖動
            dragStart: { x: 0, y: 0 }, // 拖動開始點
            currentLine: [], // 當前畫筆繪製的座標路徑
        };
    },
    methods: {
        initializeCanvas() {
            this.canvas = this.$refs.canvas;
            this.ctx = this.canvas.getContext('2d');
            this.canvas.width = 800;
            this.canvas.height = 600;
            this.clearCanvas();
        },
        clearCanvas() {
            this.ctx.fillStyle = "#FFFFFF";
            this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);
        },
        redrawCanvas() {
            this.clearCanvas();
            this.objects.forEach((object) => {
                this.drawObject(object);
                console.log(object);
            });
            if (this.selectedObject) {
                this.drawSelection(this.selectedObject);
            }
        },
        startDrawing(event) {
            const { x, y } = this.getMousePosition(event);

            if (this.tool === 'pencil') {
                this.drawing = true;
                this.ctx.beginPath();
                this.ctx.moveTo(x, y);
                this.currentLine = [{ x, y }];
            } else if (this.tool === 'eraser') {
                this.drawing = true;
                this.ctx.beginPath();
                this.ctx.moveTo(x, y);
            } else if (this.tool === 'line') {
                // 初始化 line 物件
                this.drawing = true;
                const newLine = {
                    type: 'line',
                    x, // 起始點 x
                    y, // 起始點 y
                    x2: x, // 終點 x（初始化為起點）
                    y2: y, // 終點 y（初始化為起點）
                    color: this.selectedColor,
                };
                this.objects.push(newLine);
                this.selectedObject = newLine;
            } else if (['rectangle', 'circle'].includes(this.tool)) {
                this.drawing = true;
                const newObject = {
                    type: this.tool,
                    x,
                    y,
                    color: this.selectedColor,
                    ...(this.tool === 'circle' ? { radius: 0 } : { width: 0, height: 0 }),
                };
                this.objects.push(newObject);
                this.selectedObject = newObject;
            } else if (this.tool === 'selector') {
                if (this.selectedObject) {
                    const handleIndex = this.isOverResizeHandle(x, y, this.selectedObject);
                    if (handleIndex !== -1) {
                        this.isResizing = true;
                        this.resizeHandleIndex = handleIndex; // 保存點擊的是起始點還是終點
                        return;
                    }
                }

                this.selectedObject = this.objects.find((obj) => this.isInsideObject(x, y, obj));
                if (this.selectedObject) {
                    this.isDragging = true;
                    this.dragStart = { x, y };
                } else {
                    this.selectedObject = null;
                }
                this.redrawCanvas();
            }
        },
        draw(event) {
            const { x, y } = this.getMousePosition(event);

            if (this.tool === 'pencil' && this.drawing) {
                this.drawWithPencil(x, y);
            } else if (this.tool === 'eraser' && this.drawing) {
                this.eraseAt(x, y);
            } else if (this.tool === 'line' && this.drawing && this.selectedObject) {
                // 更新線條的終點
                this.selectedObject.x2 = x;
                this.selectedObject.y2 = y;
                this.redrawCanvas();
            }else if (this.drawing && this.selectedObject) {
                this.resizeObject(x, y);
            } else if (this.tool === 'selector') {
                if (this.isDragging && this.selectedObject) {
                    this.dragObject(x, y);
                } else if (this.isResizing && this.selectedObject) {
                    this.resizeSelectedObject(x, y);
                }
            }
        },
        drawWithPencil(x, y) {
            this.ctx.lineTo(x, y);
            this.ctx.strokeStyle = this.selectedColor;
            this.ctx.lineWidth = 2;
            this.ctx.stroke();
            this.currentLine.push({ x, y }); // 记录当前线条的坐标
        },
        eraseAt(x, y) {
            // 清除畫布指定位置的像素
            this.ctx.clearRect(x - 5, y - 5, 10, 10);

            // 遍歷 objects 列表，檢查是否有物件被橡皮擦碰到
            this.objects = this.objects.filter(object => {
                if (object.type === 'rectangle') {
                    // 矩形物件的碰撞檢測
                    return !(x >= object.x && x <= object.x + object.width && y >= object.y && y <= object.y + object.height);
                } else if (object.type === 'circle') {
                    // 圓形物件的碰撞檢測
                    const dx = x - object.x;
                    const dy = y - object.y;
                    return dx * dx + dy * dy > object.radius * object.radius;
                } else if (object.type === 'line') {
                    // 線條物件的碰撞檢測
                    const distance = this.getDistanceToLineSegment(
                        { x: object.x, y: object.y },
                        { x: object.x2, y: object.y2 },
                        { x, y }
                    );
                    return distance > 5; // 判斷是否橡皮擦與線條相交
                } else if (object.type === 'text') {
                    // 文字物件的碰撞檢測
                    this.ctx.font = object.font;
                    const metrics = this.ctx.measureText(object.text);
                    const width = metrics.width;
                    const height = parseInt(object.font, 10); // 提取文字高度
                    return !(x >= object.x && x <= object.x + width && y >= object.y - height && y <= object.y);
                } else if (object.type === 'image') {
                    // 圖片物件的碰撞檢測
                    return !(x >= object.x && x <= object.x + object.width && y >= object.y && y <= object.y + object.height);
                }else if (object.type === 'pencil') {
                    // Pencil 物件的碰撞檢測
                    return !object.path.some((point, index) => {
                        if (index === 0) return false; // 第一個點無法形成線段
                        const prevPoint = object.path[index - 1];
                        const distance = this.getDistanceToLineSegment(
                            { x: prevPoint.x, y: prevPoint.y },
                            { x: point.x, y: point.y },
                            { x, y }
                        );
                        return distance <= 5; // 判斷橡皮擦是否接近線段
                    });
                }
                return true; // 預設保留其他未知類型的物件
            });

            // 重新繪製畫布
            this.redrawCanvas();
        },
        resizeObject(x, y) {
            if (this.selectedObject.type === 'rectangle') {
                this.selectedObject.width = x - this.selectedObject.x;
                this.selectedObject.height = y - this.selectedObject.y;
            } else if (this.selectedObject.type === 'circle') {
                const dx = x - this.selectedObject.x;
                const dy = y - this.selectedObject.y;
                this.selectedObject.radius = Math.sqrt(dx * dx + dy * dy);
            } else if (this.selectedObject.type === 'line') {
                this.selectedObject.x2 = x;
                this.selectedObject.y2 = y;
            }
            this.redrawCanvas();
        },
        dragObject(x, y) {
            const dx = x - this.dragStart.x;
            const dy = y - this.dragStart.y;
            this.moveObject(this.selectedObject, dx, dy);
            this.dragStart = { x, y };
            this.redrawCanvas();
        },
        resizeSelectedObject(x, y) {
            const object = this.selectedObject;

            if (object.type === 'circle') {
                // 如果是圓形，調整半徑
                const dx = x - object.x;
                const dy = y - object.y;
                object.radius = Math.sqrt(dx * dx + dy * dy); // 根據鼠標位置調整半徑
            } else if (object.type === 'rectangle' || object.type === 'image' || object.type === 'pencil') {
                const initialWidth = object.width || 1; // 確保 width 不為 0
                const initialHeight = object.height || 1;

                // 根據調整點索引改變物件的大小
                if (this.resizeHandleIndex === 0) { // 左上角
                    const dx = object.x - x;
                    const dy = object.y - y;

                    object.x = x;
                    object.y = y;
                    object.width += dx;
                    object.height += dy;
                } else if (this.resizeHandleIndex === 1) { // 右上角
                    const dy = object.y - y;

                    object.y = y;
                    object.width = x - object.x;
                    object.height += dy;
                } else if (this.resizeHandleIndex === 2) { // 左下角
                    const dx = object.x - x;

                    object.x = x;
                    object.width += dx;
                    object.height = y - object.y;
                } else if (this.resizeHandleIndex === 3) { // 右下角
                    object.width = x - object.x;
                    object.height = y - object.y;
                }

                // 如果寬度為負，調整 X 座標，鏡像顯示
                if (object.width < 0) {
                    object.width = Math.abs(object.width);
                    object.x -= object.width;
                    this.mirrorObject(object, 'horizontal'); // 水平鏡像
                }

                // 如果高度為負，調整 Y 座標，鏡像顯示
                if (object.height < 0) {
                    object.height = Math.abs(object.height);
                    object.y -= object.height;
                    this.mirrorObject(object, 'vertical'); // 垂直鏡像
                }

                const scaleX = object.width / initialWidth;
                const scaleY = object.height / initialHeight;

                // 調整物件內容
                this.scaleObjectContent(object, scaleX, scaleY);
            } else if (object.type === 'line') {
                if (this.resizeHandleIndex === 0) {
                    // 調整起始點
                    object.x = x;
                    object.y = y;
                } else if (this.resizeHandleIndex === 1) {
                    // 調整終點
                    object.x2 = x;
                    object.y2 = y;
                }
                this.redrawCanvas();
            }else if (object.type === 'text') {
                // 文字的縮放
                console.log('object.font = '+object.font);
                const initialFontSize = parseInt(object.font, 10);

                if (this.resizeHandleIndex === 0 || this.resizeHandleIndex === 2) { // 左邊縮放
                    object.x = x;
                }
                if (this.resizeHandleIndex === 1 || this.resizeHandleIndex === 3) { // 右邊縮放
                    object.width = x - object.x;
                }
                if (this.resizeHandleIndex === 0 || this.resizeHandleIndex === 1) { // 上邊縮放
                    object.y = y + initialFontSize;
                }
                if (this.resizeHandleIndex === 2 || this.resizeHandleIndex === 3) { // 下邊縮放
                    object.height = y - object.y;
                }

                const scaleFactor = Math.min(
                    Math.abs(object.width / (object.text.length * initialFontSize)),
                    Math.abs(object.height / initialFontSize)
                );

                const newFontSize = Math.max(10,initialFontSize * scaleFactor); // 防止文字過小
                object.font = `${newFontSize}px Arial`;
            }

            // 防止縮放到負值
            if (object.radius < 0) {
                object.radius = 0;
            }

            // 更新物件並重繪畫布
            this.redrawCanvas();
        },
        mirrorObject(object, direction) {
            if (direction === 'horizontal') {
                // 水平鏡像：對 X 軸進行翻轉
                if (object.type === 'rectangle' || object.type === 'image') {
                    // 矩形和圖片：無需額外處理，座標已調整
                } else if (object.type === 'pencil') {
                    // 線條：反轉 x1 和 x2
                    const x2 = object.x + object.width;
                    object.x = x2;
                    object.width = -object.width;
                }
            } else if (direction === 'vertical') {
                // 垂直鏡像：對 Y 軸進行翻轉
                if (object.type === 'rectangle' || object.type === 'image') {
                    // 矩形和圖片：無需額外處理，座標已調整
                } else if (object.type === 'pencil') {
                    // 線條：反轉 y1 和 y2
                    const y2 = object.y + object.height;
                    object.y = y2;
                    object.height = -object.height;
                }
            }
        },
        scaleObjectContent(object, scaleX, scaleY) {
            if (object.type === 'rectangle' || object.type === 'image') {
                // 矩形和图片：已通过宽高变化完成缩放，不需要额外处理
                return;
            }

            if (object.type === 'circle') {
                // 圆形：半径按比例缩放
                object.radius *= Math.max(scaleX, scaleY); // 保持比例
            } else if (object.type === 'pencil') {
                // 线条：缩放路径中的每个点
                object.path = object.path.map(point => ({
                    x: object.x + (point.x - object.x) * scaleX,
                    y: object.y + (point.y - object.y) * scaleY,
                }));
            } else if (object.type === 'text') {
                // 文字：调整字体大小
                const fontSize = parseInt(object.font, 10); // 提取字体大小
                const newFontSize = fontSize * Math.min(scaleX, scaleY);
                object.font = `${newFontSize}px Arial`;
            }
        },
        stopDrawing() {
            if (this.drawing && this.tool === 'pencil') {
                this.ctx.closePath();
                this.addLineToObjects();
            } else if (this.drawing && this.tool === 'line') {
                // 結束繪製線條
                this.selectedObject = null; // 清除選中狀態
            }
            this.drawing = false;

            if (this.tool === 'selector') {
                this.isResizing = false;
                this.isDragging = false;
                this.redrawCanvas();
            }

            this.currentLine = [];
        },
        addLineToObjects() {
            if (this.currentLine.length > 1) {
                const xValues = this.currentLine.map((point) => point.x);
                const yValues = this.currentLine.map((point) => point.y);
                const minX = Math.min(...xValues);
                const maxX = Math.max(...xValues);
                const minY = Math.min(...yValues);
                const maxY = Math.max(...yValues);

                const lineObject = {
                    type: 'pencil', // 線條物件
                    path: [...this.currentLine], // 完整的路徑
                    x: minX,
                    y: minY,
                    width: maxX - minX,
                    height: maxY - minY,
                    color: this.selectedColor,
                };
                this.objects.push(lineObject);
                this.redrawCanvas();
            }
        },
        drawObject(object) {
            this.ctx.strokeStyle = object.color;
            this.ctx.fillStyle = object.color;

            if (object.type === 'line') {
                // 繪製線條
                this.ctx.beginPath();
                this.ctx.moveTo(object.x, object.y);
                this.ctx.lineTo(object.x2, object.y2);
                this.ctx.stroke();
            } else if (object.type === 'rectangle') {
                this.ctx.fillRect(object.x, object.y, object.width, object.height);
            } else if (object.type === 'circle') {
                this.ctx.beginPath();
                this.ctx.arc(object.x, object.y, object.radius, 0, Math.PI * 2);
                this.ctx.fill();
            } else if (object.type === 'text') {
                this.ctx.font = object.font;
                this.ctx.fillText(object.text, object.x, object.y);
            } else if (object.type === 'image') {
                if (object.img.complete) {
                    this.ctx.drawImage(
                        object.img,
                        object.x,
                        object.y,
                        object.width,
                        object.height
                    );
                }
            }else if (object.type === 'pencil' && object.path) {
                // 繪製線條
                this.ctx.beginPath();
                object.path.forEach((point, index) => {
                    if (index === 0) {
                        this.ctx.moveTo(point.x, point.y);
                    } else {
                        this.ctx.lineTo(point.x, point.y);
                    }
                });
                this.ctx.stroke();
            }
        },
        drawSelection(object) {
            this.ctx.strokeStyle = 'blue';
            this.ctx.lineWidth = 2;

            // 這裡將圓形的選取框替換為正方形，大小為圓的直徑
            if (object.type === 'circle') {
                const sideLength = object.radius * 2; // 計算正方形的邊長，等於圓的直徑
                const topLeftX = object.x - object.radius;
                const topLeftY = object.y - object.radius;

                // 繪製正方形選取框
                this.ctx.strokeRect(topLeftX, topLeftY, sideLength, sideLength);
                this.drawCornerHandles(object); // 繪製角落的調整點
            }
            // 如果是其他物件類型，保留原有邏輯
            else if (object.type === 'rectangle' || object.type === 'image') {
                this.ctx.strokeRect(object.x, object.y, object.width, object.height);
                this.drawCornerHandles(object);
            }
            else if (object.type === 'pencil') {
                this.ctx.strokeRect(object.x, object.y, object.width, object.height);
                this.drawCornerHandles(object);
            }
            else if (object.type === 'text') {
                // 獲取文字的寬度和高度
                this.ctx.font = object.font;
                const metrics = this.ctx.measureText(object.text);
                const textWidth = metrics.width;
                const fontSize = parseInt(object.font, 10); // 從字體大小中解析高度

                // 設定選取框的外圍
                const padding = 5; // 增加選取框的額外間距
                const startX = object.x - padding;
                const startY = object.y - fontSize - padding; // 注意文字的基準線位置
                const width = textWidth + 2 * padding;
                const height = fontSize + 2 * padding;

                // 繪製選取框
                this.ctx.strokeRect(startX, startY, width, height);

                // 繪製角落的調整點
                const corners = [
                    { x: startX, y: startY }, // 左上角
                    { x: startX + width, y: startY }, // 右上角
                    { x: startX, y: startY + height }, // 左下角
                    { x: startX + width, y: startY + height }, // 右下角
                ];

                this.ctx.fillStyle = 'white';
                this.ctx.strokeStyle = 'black';
                corners.forEach(corner => {
                    this.ctx.beginPath();
                    this.ctx.arc(corner.x, corner.y, 5, 0, Math.PI * 2); // 半徑為 5 的白點
                    this.ctx.fill();
                    this.ctx.stroke();
                });
            } else if (object.type === 'line') {
                // 繪製線條的起始點和終點的白點
                this.ctx.fillStyle = 'white';
                this.ctx.strokeStyle = 'black';
                const radius = 5;

                // 起始點
                this.ctx.beginPath();
                this.ctx.arc(object.x, object.y, radius, 0, Math.PI * 2);
                this.ctx.fill();
                this.ctx.stroke();

                // 終點
                this.ctx.beginPath();
                this.ctx.arc(object.x2, object.y2, radius, 0, Math.PI * 2);
                this.ctx.fill();
                this.ctx.stroke();
            }
        },
        drawCornerHandles(object) {
            let corners = [];

            // 如果是圓形，我們將根據圓心來定位正方形的角落
            if (object.type === 'circle') {
                corners = [{ x: object.x, y: object.y }];
            } else {
                // 原本的處理邏輯
                corners = [
                    { x: object.x, y: object.y }, // 左上角
                    { x: object.x + object.width, y: object.y }, // 右上角
                    { x: object.x, y: object.y + object.height }, // 左下角
                    { x: object.x + object.width, y: object.y + object.height }, // 右下角
                ];
            }

            this.ctx.fillStyle = 'white';
            this.ctx.strokeStyle = 'black';
            corners.forEach(corner => {
                this.ctx.beginPath();
                this.ctx.arc(corner.x, corner.y, 5, 0, Math.PI * 2); // 半徑為 5 的白點
                this.ctx.fill();
                this.ctx.stroke();
            });
        },
        isInsideObject(x, y, object) {
            if (object.type === 'rectangle' || object.type === 'pencil') {
                // 確保位置和大小為正值
                const startX = Math.min(object.x, object.x + object.width);
                const startY = Math.min(object.y, object.y + object.height);
                const endX = Math.max(object.x, object.x + object.width);
                const endY = Math.max(object.y, object.y + object.height);

                return x >= startX && x <= endX && y >= startY && y <= endY;
            } else if (object.type === 'circle') {
                const dx = x - object.x;
                const dy = y - object.y;
                return dx * dx + dy * dy <= object.radius * object.radius;
            } else if (object.type === 'line') {
                // 檢查點是否接近線條
                const distance = this.getDistanceToLineSegment(
                    { x: object.x, y: object.y }, // 起始點
                    { x: object.x2, y: object.y2 }, // 終點
                    { x, y } // 滑鼠點擊的座標
                );
                return distance <= 5; // 判斷距離是否在 5 像素內
            } else if (object.type === 'text') {
                const metrics = this.ctx.measureText(object.text);
                const width = metrics.width;
                const height = parseInt(object.font, 10);
                return x >= object.x && x <= object.x + width && y >= object.y - height && y <= object.y;
            } else if (object.type === 'image') {
                return x >= object.x && x <= object.x + object.width && y >= object.y && y <= object.y + object.height;
            }
            return false;
        },
        getDistanceToLineSegment(start, end, point) {
            const { x: x1, y: y1 } = start;
            const { x: x2, y: y2 } = end;
            const { x: px, y: py } = point;

            // 計算線段的平方長度
            const lineLengthSquared = (x2 - x1) ** 2 + (y2 - y1) ** 2;

            if (lineLengthSquared === 0) {
                // 線段的起點和終點重合
                return Math.sqrt((px - x1) ** 2 + (py - y1) ** 2);
            }

            // 計算點在線段上的投影比例（t 的範圍是 [0, 1]）
            let t = ((px - x1) * (x2 - x1) + (py - y1) * (y2 - y1)) / lineLengthSquared;
            t = Math.max(0, Math.min(1, t)); // 限制 t 的範圍在 [0, 1]

            // 計算投影點的座標
            const projectionX = x1 + t * (x2 - x1);
            const projectionY = y1 + t * (y2 - y1);

            // 返回點到投影點的距離
            return Math.sqrt((px - projectionX) ** 2 + (py - projectionY) ** 2);
        },
        isOverResizeHandle(x, y, object) {
            const radius = 5; // 角點的檢測半徑
            let corners = [];

            if (object.type === 'line') {
                // 處理線條的起始點和終點
                const startPoint = { x: object.x, y: object.y };
                const endPoint = { x: object.x2, y: object.y2 };

                const dx1 = x - startPoint.x;
                const dy1 = y - startPoint.y;
                if (dx1 * dx1 + dy1 * dy1 <= radius * radius) {
                    return 0; // 起始點的索引
                }

                const dx2 = x - endPoint.x;
                const dy2 = y - endPoint.y;
                if (dx2 * dx2 + dy2 * dy2 <= radius * radius) {
                    return 1; // 終點的索引
                }
                return -1; // 未點中角點
            } else if (object.type === 'text') {
                // 處理文字的外框角點
                const padding = 5; // 文字選取框的額外間距
                this.ctx.font = object.font;
                const metrics = this.ctx.measureText(object.text);
                const textWidth = metrics.width;
                const fontSize = parseInt(object.font, 10);

                const startX = object.x - padding;
                const startY = object.y - fontSize - padding;
                const width = textWidth + 2 * padding;
                const height = fontSize + 2 * padding;

                corners = [
                    { x: startX, y: startY }, // 左上角
                    { x: startX + width, y: startY }, // 右上角
                    { x: startX, y: startY + height }, // 左下角
                    { x: startX + width, y: startY + height }, // 右下角
                ];
            } else {
                // 處理其他矩形類型的物件（如矩形或圖片）
                corners = [
                    { x: object.x, y: object.y }, // 左上角
                    { x: object.x + object.width, y: object.y }, // 右上角
                    { x: object.x, y: object.y + object.height }, // 左下角
                    { x: object.x + object.width, y: object.y + object.height }, // 右下角
                ];
            }

            // 檢測滑鼠是否點中角點
            return corners.findIndex(corner => {
                const dx = x - corner.x;
                const dy = y - corner.y;
                return dx * dx + dy * dy <= radius * radius;
            });
        },
        addText() {
            const text = prompt('輸入文字:');
            if (text) {
                const x = this.canvas.width / 2;
                const y = this.canvas.height / 2;
                const newText = {
                    type: 'text',
                    x,
                    y,
                    text,
                    color: this.selectedColor,
                    font: '20px Arial',
                    fontSize: 20,
                };
                this.objects.push(newText);
                this.redrawCanvas();
            }
        },
        moveObject(object, dx, dy) {
            object.x += dx;
            object.y += dy;
            if (object.type === 'pencil' && object.path) {
                object.path.forEach((point) => {
                    point.x += dx;
                    point.y += dy;
                });
            }
        },
        getMousePosition(event) {
            const rect = this.canvas.getBoundingClientRect();
            return {
                x: event.clientX - rect.left,
                y: event.clientY - rect.top,
            };
        },
        selectTool(tool) {
            this.tool = tool;
            if (tool !== 'selector') {
                this.selectedObject = null; // 切換工具時清除選取物件
            }
            this.redrawCanvas(); // 切換工具時更新畫布
        },
        selectColor(color) {
            this.selectedColor = color;
        },
        selectImage(event) {
            const file = event.target.files[0];
            if (file) {
                const img = new Image();
                img.onload = () => {
                    // 获取图片的实际宽高
                    const imgWidth = img.width;
                    const imgHeight = img.height;

                    // 在 canvas 上绘制图片
                    this.ctx.drawImage(img, 0, 0, imgWidth, imgHeight);
                    console.log('Image loaded:', img);

                    // 将图片对象存入 objects 数组
                    const imageObject = {
                        type: 'image',
                        x: 0,
                        y: 0,
                        width: imgWidth,
                        height: imgHeight,
                        img: img, // 保存图片实例
                    };
                    this.objects.push(imageObject);
                    // 重新绘制画布
                    this.redrawCanvas();
                };

                // 确保文件 URL 被正确加载
                img.src = URL.createObjectURL(file);

                // 打印日志以确认加载
                console.log('Image loading started:', file.name);
            }
        },
        addWatermark() {
            const watermarkText = prompt("請輸入浮水印文字：", "InvisibleWatermark");
            if (watermarkText) {
                const canvasData = this.ctx.getImageData(0, 0, this.canvas.width, this.canvas.height);
                const pixels = canvasData.data;

                // 將浮水印文字轉為位元資料
                const watermarkBits = watermarkText
                    .split("")
                    .map((char) => char.charCodeAt(0).toString(2).padStart(8, "0"))
                    .join("");

                // 將浮水印長度也嵌入畫布（使用 32 位表示）
                const lengthBits = watermarkBits.length.toString(2).padStart(32, "0");

                // 將長度位元和浮水印位元組合
                const fullBits = lengthBits + watermarkBits;

                // 嵌入位元到畫布像素的紅色通道
                let bitIndex = 0;
                for (let i = 0; i < pixels.length; i += 4) {
                    if (bitIndex < fullBits.length) {
                        pixels[i] = (pixels[i] & 0xfe) | parseInt(fullBits[bitIndex], 10); // 僅修改紅色通道的最低位
                        bitIndex++;
                    } else {
                        break;
                    }
                }

                // 更新畫布資料
                this.ctx.putImageData(canvasData, 0, 0);
                alert("不可見浮水印已成功嵌入！");
            }
        },
        extractWatermark() {
            const canvasData = this.ctx.getImageData(0, 0, this.canvas.width, this.canvas.height);
            const pixels = canvasData.data;

            // 提取前 32 位作為長度信息
            let lengthBits = "";
            for (let i = 0; i < 32; i++) {
                lengthBits += pixels[i * 4] & 1; // 提取紅色通道的最低位
            }

            const watermarkLength = parseInt(lengthBits, 2); // 將長度位元轉換為數字

            // 根據長度提取浮水印位元
            let watermarkBits = "";
            for (let i = 0; i < watermarkLength; i++) {
                watermarkBits += pixels[(i + 32) * 4] & 1; // 跳過長度信息的位元
            }

            // 將位元組合為文字
            let watermarkText = "";
            for (let i = 0; i < watermarkBits.length; i += 8) {
                const byte = watermarkBits.slice(i, i + 8);
                watermarkText += String.fromCharCode(parseInt(byte, 2));
            }

            alert(`提取的浮水印內容：${watermarkText}`);
        },
        downloadCanvas() {
            const link = document.createElement('a');
            link.download = 'drawing.png';
            link.href = this.canvas.toDataURL();
            link.click();
        },
        async submitPost() {
            if (this.postContent.trim() === '') {
                alert('請輸入貼文內容！');
                return;
            }

            let newPictureUrl = '';

            this.addWatermark();

            try {
                // 將 canvas 轉換為 blob
                const blob = await new Promise(resolve => {
                    this.canvas.toBlob(resolve, 'image/png');
                });

                const imageFormData = new FormData();
                imageFormData.append('image', blob, 'drawing.png'); // 添加 blob 並指定檔名

                const imageResponse = await fetch("/api/getImageUrl", {
                    method: 'POST',
                    body: imageFormData,
                });

                if (imageResponse.ok) {
                    newPictureUrl = await imageResponse.text();
                } else {
                    const errorMessage = await imageResponse.text();
                    console.error(errorMessage);
                    alert(`Image upload failed:${errorMessage}`);
                    return;
                }
            } catch (error) {
                console.error('An error occurred during the image upload process:', error);
                alert('An error occurred during the image upload process. Please try again later!');
                return;
            }

            if (this.userProfile === null) {
                alert('請先登入！');
                return;
            }

            try {
                const postFormData = {
                    "contentText": this.postContent,
                    "contentImage": newPictureUrl,
                    "author": this.userProfile,
                    "likes": 0,
                    "downloads": 0,
                    "comments": []
                };

                const postResponse = await fetch('/api/posts/add', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(postFormData),
                });

                if (postResponse.ok) {
                    const result = await postResponse.text();
                    alert('貼文已成功發佈！');
                    console.log('伺服器返回：', result);
                    this.resetForm();
                    this.$router.push('/');
                } else {
                    const errorMessage = await postResponse.text();
                    console.error('貼文發佈失敗：', errorMessage);
                    alert(`貼文發佈失敗：${errorMessage}`);
                }
            } catch (error) {
                console.error('貼文發佈過程中發生錯誤：', error);
                alert('發佈貼文時發生錯誤，請稍後再試！');
            }
        },
        resetForm() {
            this.postContent = '';
            this.clearCanvas();
        },
    },
    mounted() {
        this.initializeCanvas();
    },
    created() {
        this.userProfile = this.$route.query;
    },
};
</script>

<style scoped>
body {
    font-family: 'Arial', sans-serif;
    margin: 0;
    padding: 0;
    background-color: #f9f9f9;
    color: #333;
}

.drawing-app {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 20px;
    padding: 20px;
}

.canvas-container {
    border: 2px dashed #aaa;
    background-color: #fff;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    border-radius: 8px;
    width: 800px;
    height: 600px;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
}

.drawing-canvas {
    display: block;
    width: 100%;
    height: 100%;
}

.tools {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
    background-color: #ffffff;
    padding: 15px;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.tool-buttons,
.shape-buttons {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
}

button {
    background-color: #007bff;
    color: #fff;
    border: none;
    border-radius: 4px;
    padding: 10px 15px;
    font-size: 14px;
    font-weight: bold;
    cursor: pointer;
    transition: all 0.3s ease;
}

button:hover {
    background-color: #0056b3;
    transform: translateY(-2px);
}

button:active {
    background-color: #003f7f;
    transform: translateY(1px);
}

.colors {
    display: grid;
    grid-template-columns: repeat(4, 40px);
    gap: 10px;
}

.color {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    cursor: pointer;
    border: 2px solid transparent;
    transition: border 0.2s ease;
}

.color:hover {
    border-color: #000;
}

.post-area {
    flex: 1;
    padding: 20px;
    max-width: 600px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    margin-top: 20px;
}

.inputBox {
    width: 100%;
    margin: 20px 0;
}

.inputBox textarea {
    width: 100%;
    height: 150px;
    background-color: rgba(255, 255, 255, 0.9);
    border: 1px solid #ccc;
    border-radius: 8px;
    padding: 10px;
    font-size: 16px;
    color: #333;
    outline: none;
    resize: none;
    box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
}

.inputBox textarea:focus {
    border-color: #007bff;
    box-shadow: inset 0 2px 4px rgba(0, 123, 255, 0.3);
}

.inputBox textarea::placeholder {
    color: #aaa;
}

button.download {
    background-color: #28a745;
    color: #fff;
}

button.download:hover {
    background-color: #218838;
}

button.download:active {
    background-color: #1e7e34;
}

button.button {
    background-color: #17a2b8;
    color: #fff;
}

button.button:hover {
    background-color: #138496;
}

button.button:active {
    background-color: #0f6674;
}

.title-box {
    border: 2px solid black;
    padding: 10px;
    border-radius: 8px;
    background-color: #fff;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    font-weight: bold;
    font-size: 18px;
}
</style>
