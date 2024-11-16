<template>
  <div>
    <div class="profile-header">
      <div class="cover-photo">
        <img :src="coverPhoto" alt="Cover Photo" />
        <button class="edit-cover-button" @click="editCoverPhoto">Edit Cover Photo</button>
      </div>
      <div class="profile-info">
        <div class="profile-picture-container">
          <img class="profile-picture" :src="profilePicture" alt="Profile Picture" />
          <button class="edit-profile-picture" @click="editProfilePicture">
            <span class="camera-icon">📷</span>
          </button>
          <div class="profile-details">
            <h2>{{ userName }}</h2>
            <p>{{ userBio }}</p>
          </div>
        </div>
      </div>
    </div>
    <div class="posts">
      <h3>Posts</h3>
      <div class="post" v-for="post in posts" :key="post.id">
        <div class="post-header">
          <div class="post-user-info">
            <img class="post-user-picture" :src="profilePicture" alt="User Picture" />
            <div>
              <h4>{{ post.user }}</h4>
              <span>{{ post.time }}</span>
            </div>
          </div>
          <div class="post-options">
            <button class="post-options-button" @click="togglePostOptions(post.id)">...</button>
            <div v-if="post.showOptions" class="post-options-menu">
              <button @click="editPost(post.id)">Edit</button>
              <button @click="deletePost(post.id)">Delete</button>
            </div>
          </div>
        </div>
        <p class="post-content">{{ post.content }}</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      coverPhoto: require("@/assets/kitty.png"),
      profilePicture: require("@/assets/kitty.png"),
      userName: "Meow",
      userBio: "Using large photos, cameras can help update.",
      posts: [
        {
          id: 1,
          user: "Meow",
          time: "2 hours ago",
          content: "This is a post content example.",
          showOptions: false,
        },
      ],
    };
  },
  methods: {
    editCoverPhoto() {
      alert("Edit cover photo functionality");
    },
    editProfilePicture() {
      alert("Edit profile picture functionality");
    },
    editPost(postId) {
      alert(`Editing post with ID: ${postId}`);
    },
    deletePost(postId) {
      this.posts = this.posts.filter((post) => post.id !== postId);
      alert(`Deleted post with ID: ${postId}`);
    },
    togglePostOptions(postId) {
      this.posts = this.posts.map((post) => {
        if (post.id === postId) {
          return { ...post, showOptions: !post.showOptions };
        }
        return { ...post, showOptions: false };
      });
    },
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
  margin-right: 220px;
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
