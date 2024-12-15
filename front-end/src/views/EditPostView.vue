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
        <button @click="$router.push('/home')" type="button">Cancel</button>
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
/* General styling for the container */
div {
    font-family: 'Arial', sans-serif;
    color: #333;
}

/* Center the form and add padding */
form {
    max-width: 600px;
    margin: 50px auto;
    padding: 20px;
    border: 1px solid #ddd;
    border-radius: 10px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    background-color: #f9f9f9;
    display: flex;
    flex-direction: column;
    gap: 20px;
}

/* Header styling */
h1 {
    text-align: center;
    font-size: 28px;
    color: #007bff;
    margin-bottom: 20px;
}

/* Labels and inputs styling */
label {
    font-weight: bold;
    font-size: 16px;
    color: #555;
}

textarea,
input[type="file"] {
    width: 100%;
    padding: 10px;
    font-size: 14px;
    border: 1px solid #ccc;
    border-radius: 5px;
    background-color: #fff;
    resize: none;
    transition: border-color 0.3s ease-in-out;
}

textarea:focus,
input[type="file"]:focus {
    border-color: #007bff;
    outline: none;
}

/* Image preview container */
img {
    border: 1px solid #ddd;
    border-radius: 5px;
    margin-top: 10px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* Buttons styling */
button {
    padding: 12px 20px;
    font-size: 16px;
    font-weight: bold;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s ease-in-out, transform 0.2s ease-in-out;
}

button[type="submit"] {
    background-color: #007bff;
    color: white;
}

button[type="submit"]:hover {
    background-color: #0056b3;
}

button[type="button"] {
    background-color: #6c757d;
    color: white;
}

button[type="button"]:hover {
    background-color: #5a6268;
}

button:hover {
    transform: scale(1.05);
}

/* Responsive design for mobile */
@media (max-width: 768px) {
    form {
        padding: 15px;
    }

    textarea,
    input[type="file"] {
        font-size: 14px;
    }

    button {
        font-size: 14px;
        padding: 10px 15px;
    }
}
</style>