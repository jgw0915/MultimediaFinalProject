<template>
  <div>
    <div class="profile-header">
      <div class="cover-photo">
        <img :src="userProfile.profileCover" alt="Cover Photo" />
        <button class="edit-cover-button" @click="triggerFileInput('cover')">Edit Cover Photo</button>
      </div>
      <div class="profile-info">
        <div class="profile-picture-container">
          <img class="profile-picture" :src="userProfile.profileImage" alt="Profile Picture" />
          <button class="edit-profile-picture" @click="triggerFileInput('profile')">
            <span class="camera-icon">📷</span>
          </button>
          <input type="file" ref="fileInput" style="display: none" accept="image/*" @change="handleFileChange" />
          <div class="profile-details">
            <h2>{{ userProfile.nickname }}</h2>
          </div>
        </div>
      </div>
    </div>
    <div class="posts">
      <h3>Posts</h3>
      <div class="post" v-for="post in posts" :key="post.id">
        <div class="post-header">
          <div class="post-user-info">
            <img class="post-user-picture" :src="post.userPicture" alt="User Picture" />
            <div>
              <h4>{{ post.user }}</h4>
              <span>{{ post.time }}</span>
            </div>
          </div>
          <div class="post-options">
            <button class="post-options-button" @click="togglePostOptions(post.id)">...</button>
            <div v-if="post.showOptions" class="post-options-menu">
              <button @click="editPost(post.id, post.content, post.contentImage)">Edit</button>
              <button @click="deletePost(post.id)">Delete</button>
            </div>
          </div>
        </div>
        <p class="post-content">{{ post.content }}</p>
        <img v-if="post.contentImage" :src="post.contentImage" alt="Post Image" class="post-image" />
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      userProfile: {
        nickname: "",
        profileImage: "",
        profileCover: "",
        email: "",
      },
      posts: [],
      uploadTarget: "",
    };
  },
  methods: {
    async fetchUserProfileAndPosts() {
      const email = this.userProfile.email;
      if (!email) {
        console.error("User email is empty");
        return;
      }
      const url = `/api/posts/getByEmail?email=${email}`;
      try {
        const response = await fetch(url);
        if (response.ok) {
          const data = await response.json();
          this.posts = data.map((post) => ({
            id: post.id,
            user: post.author.nickname,
            userPicture: post.author.profileImage,
            time: new Date(post.updatedAt).toLocaleString(),
            content: post.contentText,
            contentImage: post.contentImage,
            showOptions: false,
          }));
        } else {
          console.error("Failed to fetch posts:", await response.text());
        }
      } catch (error) {
        console.error("Error fetching posts:", error);
      }
    },
    triggerFileInput(target) {
      this.uploadTarget = target;
      this.$refs.fileInput.click();
    },

    handleFileChange(event) {
      const file = event.target.files[0];
      if (file) {
        this.uploadProfilePicture(file);
      }
    },

    async uploadProfilePicture(file) {
      const formData = new FormData();
      formData.append('image', file);
      formData.append('email', this.userProfile.email);
      let url = "";
      if (this.uploadTarget === "cover") {
        url = "/api/saveProfileCover";
      } else {
        url = "/api/saveProfileImage";
      }
      try {
        const response = await fetch(url, {
          method: 'POST',
          body: formData,
        });

        if (response.ok) {
          const newPictureUrl = await response.text();
          if (this.uploadTarget === "cover") {
            this.userProfile.profileCover = newPictureUrl;
          } else {
            this.userProfile.profileImage = newPictureUrl;
          }
          alert('Profile picture updated successfully!');
        } else {
          const errorMessage = await response.text();
          console.error(errorMessage);
          alert(`Error: ${errorMessage}`);
        }
      } catch (error) {
        console.error('Error uploading profile picture:', error);
        alert('An error occurred while uploading the profile picture.');
      }
    },
    editPost(postId, content, image) {
      this.$router.push({
        path: "/editPost",
        query: {
          id: postId,
          content: content,
          image: image,
        },
      });
    },
    deletePost(postId) {
      if (confirm("Are you sure you want to delete this post?")) {
        fetch(`/api/posts/delete/${postId}`, {
          method: "DELETE",
        })
          .then((response) => {
            if (response.ok) {
              this.posts = this.posts.filter((post) => post.id !== postId);
              alert(`Post with ID: ${postId} deleted successfully!`);
            } else {
              return response.text().then((text) => {
                throw new Error(`Failed to delete post: ${text}`);
              });
            }
          })
          .catch((error) => {
            console.error("Error deleting post:", error);
            alert("An error occurred while deleting the post.");
          });
      }
    },
    togglePostOptions(postId) {
      this.posts = this.posts.map((post) => {
        if (post.id === postId) {
          return { ...post, showOptions: !post.showOptions };
        }
        return { ...post, showOptions: false };
      });
    },
    async fetchUserProfile(userEmail) {
      try {
        if (!userEmail) throw new Error("User email cannot be empty");

        const response = await fetch(`/api/getUser?userEmail=${userEmail}`);
        if (!response.ok) throw new Error(`API error: ${response.statusText}`);

        const userProfile = await response.json();
        this.userProfile = userProfile;
        this.fetchUserProfileAndPosts();
      } catch (error) {
        console.error("Failed to fetch user profile:", error.message);
      }
    },
  },
  created() {
    this.fetchUserProfile(this.$route.query.email);
  },
};
</script>

<style scoped>
body {
  font-family: Arial, sans-serif;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.profile-header {
  text-align: center;
  padding: 20px;
}

.cover-photo {
  position: relative;
}

.cover-photo img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-bottom: 1px solid #ddd;
}

.edit-cover-button {
  position: absolute;
  bottom: 10px;
  right: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 5px;
  cursor: pointer;
}

.profile-info {
  margin-top: -50px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
}

.profile-picture-container {
  position: relative;
  height: 180px;
}

.profile-picture {
  width: 125px;
  height: 125px;
  border-radius: 50%;
  border: 3px solid white;
  position: relative;
  z-index: 1;
}

.edit-profile-picture {
  position: absolute;
  bottom: 0;
  right: 0;
  background-color: transparent;
  border: none;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 2;
}

.camera-icon {
  font-size: 20px;
  margin-bottom: 110px;
  margin-right: 5px;
}

.profile-details h2 {
  margin: 0;
}

.profile-details p {
  margin: 0;
  color: #666;
}

.posts {
  margin: 20px auto;
  width: 60%;
}

.post-image {
  max-width: 100%;
  border-radius: 5px;
  margin-top: 10px;
}

.posts h3 {
  text-align: left;
  margin-bottom: 10px;
}

.post {
  border: 1px solid #ddd;
  padding: 15px;
  border-radius: 5px;
  margin-bottom: 15px;
  background-color: #f9f9f9;
}

.post-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.post-user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.post-user-info div {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  line-height: 0.001;
  gap: 1px;
  margin-bottom: 15px;
}

.post-user-picture {
  width: 50px;
  height: 50px;
  border-radius: 50%;
}

.post-options {
  position: relative;
  margin-top: 10px;
}

.post-options-button {
  background-color: transparent;
  border: none;
  font-size: 20px;
  cursor: pointer;
}

.post-options-menu {
  position: absolute;
  top: 20px;
  right: 0;
  background: white;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  z-index: 10;
}

.post-options-menu button {
  display: block;
  width: 100%;
  background: none;
  border: none;
  padding: 10px;
  text-align: left;
  cursor: pointer;
}

.post-options-menu button:hover {
  background: #f0f0f0;
}

.post-content {
  margin: 0;
}
</style>
