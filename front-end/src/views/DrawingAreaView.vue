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
                // 画笔工具逻辑
                this.drawing = true;
                this.ctx.beginPath();
                this.ctx.moveTo(x, y);
                this.currentLine = [{ x, y }]; // 初始化当前线条坐标
            } else if (this.tool === 'eraser') {
                // 橡皮擦工具逻辑
                this.drawing = true;
                this.ctx.beginPath();
                this.ctx.moveTo(x, y);
            } else if (['rectangle', 'circle', 'line'].includes(this.tool)) {
                // 矩形、圆形、线条工具逻辑
                this.drawing = true;
                const newObject = {
                    type: this.tool,
                    x,
                    y,
                    color: this.selectedColor,
                    ...(this.tool === 'line' ? { x2: x, y2: y } : { width: 0, height: 0 }),
                    ...(this.tool === 'circle' ? { radius: 0 } : {}),
                };
                this.objects.push(newObject);
                this.selectedObject = newObject;
            } else if (this.tool === 'selector') {
                // 选择工具逻辑
                if (this.selectedObject) {
                    const handleIndex = this.isOverResizeHandle(x, y, this.selectedObject);
                    if (handleIndex !== -1) {
                        // 点击调整点，进入调整大小模式
                        this.isResizing = true;
                        this.resizeHandleIndex = handleIndex; // 保存调整点索引
                        return;
                    }
                }

                // 检查是否点击了对象
                this.selectedObject = this.objects.find((obj) => this.isInsideObject(x, y, obj));
                if (this.selectedObject) {
                    // 点击对象，进入拖动模式
                    this.isDragging = true;
                    this.dragStart = { x, y };
                } else {
                    // 点击空白区域，取消选中
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
            } else if (this.drawing && this.selectedObject) {
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
            this.ctx.clearRect(x - 5, y - 5, 10, 10);
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

            const initialWidth = object.width || 1; // 确保 width 不为 0
            const initialHeight = object.height || 1;

            // 根据调整点索引改变对象的大小
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

            // 防止宽度和高度为负值
            if (object.width < 0) {
                object.width = Math.abs(object.width);
                object.x -= object.width;
            }
            if (object.height < 0) {
                object.height = Math.abs(object.height);
                object.y -= object.height;
            }

            const scaleX = object.width / initialWidth;
            const scaleY = object.height / initialHeight;

            // 调整对象内容
            this.scaleObjectContent(object, scaleX, scaleY);

            this.redrawCanvas();
        },
        scaleObjectContent(object, scaleX, scaleY) {
            if (object.type === 'rectangle' || object.type === 'image') {
                // 矩形和图片：已通过宽高变化完成缩放，不需要额外处理
                return;
            }

            if (object.type === 'circle') {
                // 圆形：半径按比例缩放
                object.radius *= Math.max(scaleX, scaleY); // 保持比例
            } else if (object.type === 'line') {
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
                this.addLineToObjects(); // 將線條轉換為物件
            }
            this.drawing = false;

            // 停止拖動狀態，但保留選取的物件
            if (this.tool === 'selector') {
                this.isResizing = false; // 停止调整大小
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
                    type: 'line', // 線條物件
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

            if (object.type === 'line' && object.path) {
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
            } else if (object.type === 'rectangle') {
                // 繪製矩形
                this.ctx.fillRect(object.x, object.y, object.width, object.height);
            } else if (object.type === 'circle') {
                // 繪製圓形
                this.ctx.beginPath();
                this.ctx.arc(object.x, object.y, object.radius, 0, Math.PI * 2);
                this.ctx.fill();
            } else if (object.type === 'text') {
                // 繪製文字
                this.ctx.font = object.font;
                this.ctx.textBaseline = 'top';
                this.ctx.fillText(
                    object.text,
                    object.x,
                    object.y
                );
            } else if (object.type === 'image') {
                // 繪製圖片
                if (object.img.complete) {
                    this.ctx.drawImage(
                        object.img,
                        object.x,
                        object.y,
                        object.width,
                        object.height
                    );
                } else {
                    // 確保圖片加載完成後再繪製
                    object.img.onload = () => {
                        this.ctx.drawImage(
                            object.img,
                            object.x,
                            object.y,
                            object.width,
                            object.height
                        );
                    };
                }
            }
        },
        drawSelection(object) {
            this.ctx.strokeStyle = 'blue';
            this.ctx.lineWidth = 2;

            // 绘制选取框
            if (object.type === 'rectangle' || object.type === 'image') {
                this.ctx.strokeRect(object.x, object.y, object.width, object.height);
                this.drawCornerHandles(object);
            } else if (object.type === 'circle') {
                this.ctx.beginPath();
                this.ctx.arc(object.x, object.y, object.radius, 0, Math.PI * 2);
                this.ctx.stroke();
                this.drawCornerHandles(object); // 为圆心添加调整点
            } else if (object.type === 'line') {
                this.ctx.strokeRect(object.x, object.y, object.width, object.height);
                this.drawCornerHandles(object);
            } else if (object.type === 'text') {
                const metrics = this.ctx.measureText(object.text);
                const width = metrics.width;
                const height = parseInt(object.font, 10); // 从字体提取高度
                this.ctx.strokeRect(object.x, object.y - height, width, height);
                this.drawCornerHandles({ ...object, width, height }); // 处理文字大小
            }
        },
        drawCornerHandles(object) {
            const corners = [
                { x: object.x, y: object.y }, // 左上角
                { x: object.x + object.width, y: object.y }, // 右上角
                { x: object.x, y: object.y + object.height }, // 左下角
                { x: object.x + object.width, y: object.y + object.height }, // 右下角
            ];

            this.ctx.fillStyle = 'white';
            this.ctx.strokeStyle = 'black';
            corners.forEach(corner => {
                this.ctx.beginPath();
                this.ctx.arc(corner.x, corner.y, 5, 0, Math.PI * 2); // 半径为5的白点
                this.ctx.fill();
                this.ctx.stroke();
            });
        },
        drawResizeHandle(x, y) {
            this.ctx.fillStyle = 'red';
            this.ctx.fillRect(
                x - this.resizeHandleSize / 2,
                y - this.resizeHandleSize / 2,
                this.resizeHandleSize,
                this.resizeHandleSize
            );
        },
        isInsideObject(x, y, object) {
            if (object.type === 'rectangle') {
                return x >= object.x && x <= object.x + object.width && y >= object.y && y <= object.y + object.height;
            } else if (object.type === 'circle') {
                const dx = x - object.x;
                const dy = y - object.y;
                return dx * dx + dy * dy <= object.radius * object.radius;
            } else if (object.type === 'line' && object.path) {
                const { x: minX, y: minY, width, height } = object;
                return x >= minX && x <= minX + width && y >= minY && y <= minY + height;
            } else if (object.type === 'text') {
                const metrics = this.ctx.measureText(object.text);
                const width = metrics.width;
                const height = parseInt(object.font, 10);
                return x >= object.x && x <= object.x + width && y >= object.y - height && y <= object.y;
            } else if (object.type === 'image') {
                // 图片的选取逻辑
                return x >= object.x && x <= object.x + object.width && y >= object.y && y <= object.y + object.height;
            }
            return false;
        },
        isOverResizeHandle(x, y, object) {
            const corners = [
                { x: object.x, y: object.y }, // 左上角
                { x: object.x + object.width, y: object.y }, // 右上角
                { x: object.x, y: object.y + object.height }, // 左下角
                { x: object.x + object.width, y: object.y + object.height }, // 右下角
            ];

            const handleRadius = 5; // 白点的半径
            return corners.findIndex(corner => {
                const dx = x - corner.x;
                const dy = y - corner.y;
                return dx * dx + dy * dy <= handleRadius * handleRadius;
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
                };
                this.objects.push(newText);
                this.redrawCanvas();
            }
        },
        moveObject(object, dx, dy) {
            object.x += dx;
            object.y += dy;
            if (object.type === 'line' && object.path) {
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
            const watermarkText = prompt("請輸入浮水印文字：", "Watermark"); // 提供彈窗讓用戶輸入浮水印文字
            if (watermarkText) {
                const fontSize = 40;
                this.ctx.font = `${fontSize}px Arial`;
                this.ctx.fillStyle = "rgba(175, 175, 175, 0.5)";
                this.ctx.textAlign = "right";
                this.ctx.textBaseline = "bottom";

                // 計算浮水印文字的位置（右下角）
                const x = this.canvas.width - 10; // 右邊留 10px
                const y = this.canvas.height - 10; // 底部留 10px

                // 在畫布上繪製浮水印文字
                this.ctx.fillText(watermarkText, x, y);

                // 將浮水印作為物件保存
                const watermarkObject = {
                    type: "text",
                    x,
                    y: y - fontSize, // 浮水印的頂部位置
                    text: watermarkText,
                    color: "rgba(0, 0, 0, 0.5)",
                    font: `${fontSize}px Arial`,
                };
                this.objects.push(watermarkObject);
            }
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

<style>
.drawing-app {
    display: flex;
    gap: 20px;
    font-family: Arial, sans-serif;
}

.canvas-container {
    border: 2px solid #000;
    width: 800px;
    height: 600px;
}

.drawing-canvas {
    display: block;
    width: 100%;
    height: 100%;
}

.tools {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.tool-buttons,
.shape-buttons {
    display: flex;
    gap: 10px;
}

.colors {
    display: grid;
    grid-template-columns: repeat(4, 30px);
    gap: 5px;
    margin-top: 10px;
}

.color {
    width: 30px;
    height: 30px;
    border-radius: 50%;
    cursor: pointer;
    border: 1px solid #ccc;
}

.post-area {
    flex: 1;
    padding: 20px;
    max-width: 600px;
    margin-left: 20px;
    background-color: #fff;
    /* Background for AddPostView area */
}

.inputBox {
    width: 100%;
    margin: 20px 0;
}

.inputBox textarea {
    width: 350px;
    height: 275px;
    background-color: rgba(255, 255, 255, 0.8);
    border: 2px solid black;
    border-radius: 10px;
    padding: 15px;
    font-size: 16px;
    color: #333;
    outline: none;
    resize: none;
}

.inputBox textarea::placeholder {
    color: #aaa;
}

.title-box {
    border: 2px solid black;
    /* Black border */
    padding: 10px;
    /* Padding inside the box */
    display: inline-block;
    /* Ensure the box fits the content */
}
</style>
