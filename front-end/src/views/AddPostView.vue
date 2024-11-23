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
            userProfile: {
                email: "",
                nickname: "",
                profileImage: "",
                profileCover: "",
            },
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
        async submitPost() {
            if (this.postContent.trim() === '') {
                alert('請輸入貼文內容！');
                return;
            }
            if (!this.uploadedImage) {
                alert('請上傳一張圖片作為封面！');
                return;
            }

            let newPictureUrl = '';

            try {
                const imageFormData = new FormData();
                imageFormData.append('image', this.uploadedImage);
                const imageResponse = await fetch("/api/getImageUrl", {
                    method: 'POST',
                    body: imageFormData,
                });

                if (imageResponse.ok) {
                    newPictureUrl = await imageResponse.text();
                } else {
                    const errorMessage = await imageResponse.text();
                    console.error(errorMessage);
                    alert(`圖片上傳失敗：${errorMessage}`);
                    return;
                }
            } catch (error) {
                console.error('圖片上傳過程中發生錯誤：', error);
                alert('圖片上傳過程中發生錯誤，請稍後再試！');
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
            this.resetImageUpload();
        },
    },
    created() {
        this.userProfile = this.$route.query;
    },
};
</script>

<style scoped>
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
