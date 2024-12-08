<template>
    <div>
        <h1>Edit Post</h1>
        <form @submit.prevent="updatePost">
            <div>
                <label for="content">Content:</label>
                <textarea id="content" v-model="post.content" placeholder="Edit your post content" rows="4"></textarea>
            </div>
            <div>
                <label for="image">Image:</label>
                <input type="file" @change="handleFileChange" accept="image/*" />
                <div v-if="previewImage">
                    <p>Preview:</p>
                    <img :src="previewImage" alt="Image Preview" style="max-width: 100%; height: auto;" />
                </div>
            </div>
            <button type="submit">Save Changes</button>
        </form>
        <button @click="$router.push('/')" type="button">Cancel</button>
    </div>
</template>


<script>
export default {
    data() {
        return {
            post: {
                id: '',
                content: '',
                image: '',
            },
            previewImage: '',
            updatedFile: null,
        };
    },
    methods: {
        handleFileChange(event) {
            const file = event.target.files[0];
            if (file) {
                this.updatedFile = file;
                this.previewImage = URL.createObjectURL(file);
            }
        },
        async updatePost() {
            try {

                const formData = {
                    "contentText": this.post.content,
                    "contentImage": this.previewImage,
                };

                if (this.updatedFile) {
                    try {
                        const imageFormData = new FormData();
                        imageFormData.append('image', this.updatedFile);
                        const imageResponse = await fetch("/api/getImageUrl", {
                            method: 'POST',
                            body: imageFormData,
                        });

                        if (imageResponse.ok) {
                            formData.contentImage = await imageResponse.text();
                        } else {
                            const errorMessage = await imageResponse.text();
                            console.error(errorMessage);
                            alert(`Image upload failed: ${errorMessage}`);
                            return;
                        }
                    } catch (error) {
                        console.error('An error occurred during the image upload process:', error);
                        alert('An error occurred during the image upload process. Please try again later!');
                        return;
                    }
                }

                const response = await fetch(`/api/posts/update/${this.post.id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(formData),
                });

                if (response.ok) {
                    alert('Post updated successfully!');
                    this.$router.push('/');
                } else {
                    const errorMessage = await response.text();
                    alert(`Failed to update post: ${errorMessage}`);
                }
            } catch (error) {
                console.error('Error updating post:', error);
                alert('An error occurred while updating the post.');
            }
        },
    },
    created() {
        this.post = this.$route.query;
        this.previewImage = this.post.image;
    },
};
</script>


<style scoped>
form {
    max-width: 600px;
    margin: auto;
    display: flex;
    flex-direction: column;
    gap: 15px;
}

textarea {
    width: 100%;
    padding: 10px;
    font-size: 16px;
    border: 1px solid #ddd;
    border-radius: 5px;
}

button {
    padding: 10px 20px;
    font-size: 16px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
}

button:hover {
    background-color: #0056b3;
}
</style>