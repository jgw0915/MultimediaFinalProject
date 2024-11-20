<template>
    <div class="body">
        <div class="wrapper">
            <h1>Create post</h1>
            <div class="inputBox">
                <textarea v-model="postContent" placeholder="分享您的心情或故事吧！"></textarea>
            </div>
            <div class="inputBox">
                <label class="uploadLabel">上傳封面圖片：</label>
                <input type="file" @change="handleFileUpload" accept="image/*" class="fileInput" />
                <p v-if="uploadedImageName" class="fileInfo">已選擇圖片：{{ uploadedImageName }}</p>
                <div v-if="uploadedImagePreview" class="imagePreview">
                    <img :src="uploadedImagePreview" alt="預覽圖片" />
                </div>
            </div>
            <button class="button" @click="submitPost">發佈</button>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            postContent: '', // 貼文內容
            uploadedImage: null, // 上傳的圖片檔案
            uploadedImagePreview: null, // 圖片預覽 URL
            uploadedImageName: '', // 圖片檔案名稱
        };
    },
    methods: {
        handleFileUpload(event) {
            const file = event.target.files[0];
            if (file) {
                if (file.type.startsWith('image/')) {
                    this.uploadedImage = file;
                    this.uploadedImageName = file.name;

                    // 使用 FileReader 生成圖片預覽
                    const reader = new FileReader();
                    reader.onload = (e) => {
                        this.uploadedImagePreview = e.target.result;
                    };
                    reader.readAsDataURL(file);
                } else {
                    alert('請上傳有效的圖片格式！');
                    this.resetImageUpload();
                }
            }
        },
        resetImageUpload() {
            this.uploadedImage = null;
            this.uploadedImagePreview = null;
            this.uploadedImageName = '';
        },
        submitPost() {
            if (this.postContent.trim() === '') {
                alert('請輸入貼文內容！');
            } else if (!this.uploadedImage) {
                alert('請上傳一張圖片作為封面！');
            } else {
                console.log('貼文內容：', this.postContent);
                console.log('上傳的圖片：', this.uploadedImage.name);
                alert('貼文已成功發佈！');
                this.resetForm();
            }
        },
        resetForm() {
            this.postContent = '';
            this.resetImageUpload();
        },
    },
};
</script>

<style scoped>
/* 保持您的原始設計，新增的樣式保持簡潔 */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

.body {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background-color: rgba(144, 189, 231, 0.479);
    background-size: cover;
    background-position: center;
}

.wrapper {
    /* width: 800px;
    height: 620px;
    background: rgba(144, 189, 231, 0.479);
    border: 2px solid rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(20px);
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
    color: #000;
    border-radius: 10px;
    padding: 30px 40px;
    text-align: center; */
}

.wrapper h1 {
    font-size: 24px;
    margin-bottom: 20px;
    color: #333;
}

.inputBox {
    width: 100%;
    margin: 20px 0;
}

.inputBox textarea {
    width: 700px;
    height: 200px;
    background-color: rgba(255, 255, 255, 0.8);
    border: 2px solid rgba(255, 255, 255, 0.2);
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

.fileInput {
    width: 100%;
    margin-top: 10px;
    font-size: 14px;
}

.fileInfo {
    margin-top: 10px;
    font-size: 14px;
    color: #555;
}

.imagePreview {
    margin-top: 15px;
    text-align: center;
}

.imagePreview img {
    max-width: 600px;
    max-height: 600px;
    border-radius: 10px;
    border: 2px solid rgba(255, 255, 255, 0.2);
    object-fit: cover;
}

.wrapper .button {
    margin-top: 15px;
    width: 100%;
    height: 45px;
    background-color: #fff;
    border: none;
    outline: none;
    border-radius: 40px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    cursor: pointer;
    font-size: 16px;
    color: #333;
    font-weight: 600;
    text-align: center;
    transition: 0.3s;
}

.wrapper .button:hover {
    background-color: dimgrey;
    color: #fff;
}
</style>
