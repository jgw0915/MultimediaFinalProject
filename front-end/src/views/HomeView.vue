<template>
  <div>
    <div class="header">
      <div class="title">{{ title }}</div>
      <div class="nav">{{ nav }}</div>
    </div>
    <div class="main">
      <div class="left-sidebar">
        <p>{{ copyrightMessage1 }}</p>
        <p>{{ copyrightMessage2 }}</p>
      </div>
      <div class="content">
        <div class="scrollable">
          <div class="post" v-for="post in posts" :key="post.id">
            <div class="user-info">
              <strong>{{ post.user }}</strong> <span>{{ post.time }}</span>
            </div>
            <p>{{ post.text }}</p>
            <img :src="post.image" alt="Post image" />
            <div class="interactions">
              <button @click="likePost(post.id)">Like ({{ post.likes }})</button>
              <button>Comment ({{ post.comments }})</button>
              <button>Download ({{ post.downloads }})</button>
            </div>
          </div>
        </div>
      </div>
      <div class="right-sidebar">
        <div @click="toUserDetail" class="user-profile">
          <img :src="userProfile.avatar" alt="User Avatar" />
          <p>{{ userProfile.name }}</p>
        </div>
        <div class="actions">
          <button @click="enterDrawingArea">Enter Drawing Area</button>
          <button @click="collectFeedback">User Feedback Collection</button>
        </div>
      </div>
    </div>
    <button class="floating-button" @click="addNewPost">+</button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      title: "Pixel Lab",
      nav: "",
      copyrightMessage1: "Respect other’s copyrights!",
      copyrightMessage2: "Please cite the source when quote!",
      posts: [
        {
          id: 1,
          user: "Meow",
          time: "2 hours",
          text: "I draw a cute kitty, come interact with me!",
          image: require("@/assets/kitty.png"),
          likes: 100,
          comments: 10,
          downloads: 500,
        },
      ],
      userProfile: {
        name: "Meow",
        avatar: require("@/assets/kitty.png"),
      },
    };
  },
  methods: {
    likePost(id) {
      const post = this.posts.find((post) => post.id === id);
      if (post) post.likes++;
    },
    enterDrawingArea() {
      this.$router.push("/drawingArea")
    },
    collectFeedback() {
      this.$router.push("/userFeedback")
    },
    toUserDetail() {
      this.$router.push("/about")
    },
    addNewPost() {
      this.$router.push("/addPostView")
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

.header {
  background-color: #f5f5f5;
  padding: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #ddd;
}

.header .title {
  font-size: 24px;
  font-weight: bold;
}

.header .nav {
  font-size: 14px;
  color: #888;
}

.main {
  display: flex;
  padding: 20px;
}

.left-sidebar {
  width: 20%;
  padding: 10px;
  color: red;
  font-weight: bold;
  border-right: 1px solid #ddd;
}

.content {
  width: 60%;
  padding: 10px;
}

.scrollable {
  max-height: 80vh;
  overflow-y: auto;
}

.content .post {
  border: 1px solid #ddd;
  padding: 15px;
  margin-bottom: 20px;
  border-radius: 5px;
  background-color: #f9f9f9;
}

.content .post img {
  max-width: 100%;
  height: auto;
  border-radius: 5px;
}

.content .post .interactions {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}

.content .post .interactions button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 5px;
  cursor: pointer;
}

.right-sidebar {
  width: 20%;
  padding: 10px;
  border-left: 1px solid #ddd;
}

.right-sidebar .user-profile {
  text-align: center;
  margin-bottom: 20px;
}

.right-sidebar .user-profile img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
}

.right-sidebar .actions button {
  display: block;
  width: 100%;
  margin-bottom: 10px;
  padding: 10px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.floating-button {
  position: fixed;
  bottom: 20px;
  right: 20px;
  background-color: #007bff;
  color: white;
  border: none;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  font-size: 24px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
</style>