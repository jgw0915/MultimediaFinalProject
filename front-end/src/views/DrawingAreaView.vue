<template>
    <div class="drawing-app">
        <div class="canvas-container">
            <canvas ref="canvas" class="drawing-canvas" @mousedown="startDrawing" @mousemove="draw"
                @mouseup="stopDrawing" @mouseleave="stopDrawing"></canvas>
        </div>

        <div class="tools">
            <div class="tool-buttons">
                <button @click="selectTool('pencil')">✏️ 畫筆</button>
                <button @click="selectTool('eraser')">🧹 橡皮擦</button>
                <button @click="addText">🔤 文字</button>
            </div>

            <div class="shape-buttons">
                <button @click="drawShape('circle')">⚪ 圓形</button>
                <button @click="drawShape('rectangle')">▭ 矩形</button>
                <button @click="drawShape('line')">➖ 線條</button>
            </div>

            <div class="colors">
                <label v-for="color in colors" :key="color" :style="{ backgroundColor: color }" class="color"
                    @click="selectColor(color)"></label>
            </div>

            <input type="file" accept="image/*" @change="selectImage" />
            <button @click="downloadCanvas">下載</button>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            canvas: null,
            ctx: null,
            drawing: false,
            tool: 'pencil',
            selectedColor: '#000000',
            colors: ['#000000', '#FF0000', '#00FF00', '#0000FF', '#FFFF00', '#FF00FF', '#00FFFF', '#FFFFFF'],
        };
    },
    methods: {
        initializeCanvas() {
            this.canvas = this.$refs.canvas;
            this.ctx = this.canvas.getContext('2d');
            this.canvas.width = 800;
            this.canvas.height = 600;
            this.ctx.fillStyle = "#FFFFFF";
            this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);
        },
        startDrawing(event) {
            if (this.tool === 'pencil' || this.tool === 'eraser') {
                this.drawing = true;
                this.ctx.beginPath();
                const rect = this.canvas.getBoundingClientRect();
                this.ctx.moveTo(event.clientX - rect.left, event.clientY - rect.top);
            }
        },
        draw(event) {
            if (this.drawing) {
                const rect = this.canvas.getBoundingClientRect();
                const x = event.clientX - rect.left;
                const y = event.clientY - rect.top;

                if (this.tool === 'pencil') {
                    this.ctx.strokeStyle = this.selectedColor;
                    this.ctx.lineWidth = 2;
                    this.ctx.lineTo(x, y);
                    this.ctx.stroke();
                } else if (this.tool === 'eraser') {
                    this.ctx.clearRect(x - 5, y - 5, 10, 10);
                }
            }
        },
        stopDrawing() {
            this.drawing = false;
            this.ctx.closePath();
        },
        selectTool(tool) {
            this.tool = tool;
        },
        selectColor(color) {
            this.selectedColor = color;
            this.ctx.strokeStyle = color;
        },
        drawShape(shape) {
            const rect = this.canvas.getBoundingClientRect();
            const x = rect.width / 4;
            const y = rect.height / 4;

            this.ctx.fillStyle = this.selectedColor;

            if (shape === 'circle') {
                this.ctx.beginPath();
                this.ctx.arc(x + 100, y + 100, 50, 0, 2 * Math.PI);
                this.ctx.fill();
            } else if (shape === 'rectangle') {
                this.ctx.fillRect(x, y, 150, 100);
            } else if (shape === 'line') {
                this.ctx.beginPath();
                this.ctx.moveTo(x, y);
                this.ctx.lineTo(x + 200, y + 100);
                this.ctx.stroke();
            }
        },
        addText() {
            const text = prompt('輸入文字:');
            if (text) {
                const rect = this.canvas.getBoundingClientRect();
                const x = rect.width / 2;
                const y = rect.height / 2;

                this.ctx.font = '20px Arial';
                this.ctx.fillStyle = this.selectedColor;
                this.ctx.fillText(text, x, y);
            }
        },
        selectImage(event) {
            const file = event.target.files[0];
            if (file) {
                const img = new Image();
                img.onload = () => {
                    this.ctx.drawImage(img, 0, 0, this.canvas.width, this.canvas.height);
                };
                img.src = URL.createObjectURL(file);
            }
        },
        downloadCanvas() {
            const link = document.createElement('a');
            link.download = 'drawing.png';
            link.href = this.canvas.toDataURL();
            link.click();
        },
    },
    mounted() {
        this.initializeCanvas();
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
</style>