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
              <img :src="post.avatar" alt="User Avatar" class="user-avatar" />
              <div class="user-details">
                <strong>{{ post.user }}</strong>
                <span class="time">{{ post.time }}</span>
              </div>
            </div>
            <p>{{ post.text }}</p>
            <img :src="post.image" alt="Post image" />
            <div class="interactions">
              <button @click="likePost(post.id)">Like ({{ post.likes }})</button>
              <button @click="toggleComments(post.id)">Comment ({{ post.comments }})</button>
              <button @click="downloadImage(post.id)">Download ({{ post.downloads }})</button>
            </div>

            <!-- 留言區 -->
            <div v-if="post.showComments" class="comments-section">
              <h4>All comments</h4>
              <div v-for="comment in post.commentList" :key="comment.id" class="comment">
                <div class="comment-header">
                  <img :src="comment.avatar" alt="User Avatar" />
                  <div>
                    <strong>{{ comment.nickname }}</strong>
                    <span>{{ comment.time }}</span>
                  </div>
                </div>
                <p>{{ comment.text }}</p>
                <div class="comment-actions">
                  <button @click="likeComment(post.id, comment.id)">Like ({{ comment.likes
                    }})</button>
                  <button @click="toggleReplies(comment.id, post.id)">Reply ({{ comment.replies.length
                    }})</button>
                </div>
                <!-- 回覆區 -->
                <div v-if="comment.showReplies" class="replies">
                  <div v-for="reply in comment.replies" :key="reply.id" class="reply">
                    <div class="reply-header">
                      <img :src="reply.avatar" alt="User Avatar" />
                      <div>
                        <strong>{{ reply.nickname }}</strong>
                        <span>{{ reply.time }}</span>
                      </div>
                    </div>
                    <p>{{ reply.text }}</p>
                    <div class="reply-actions">
                      <button @click="likeReply(post.id, comment.id, reply.id)">Like ({{
                        reply.likes }})</button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="right-sidebar">
        <div @click="toUserDetail" class="user-profile">
          <img :src="userProfile.profileImage" alt="User Avatar" />
          <p>{{ userProfile.nickname }}</p>
        </div>
        <div class="actions">
          <button class="enterDrawingAreaBtn" @click="enterDrawingArea">Enter Drawing Area</button>
          <button class="collectFeedbackBtn" @click="collectFeedback">User Feedback Collection</button>
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
      copyrightMessage2: "Please cite the source when quoting!",
      posts: [
        {
          id: 1,
          user: "Meow",
          time: "2 hours ago",
          avatar: require("@/assets/kitty.png"),
          text: "I draw a cute kitty, come interact with me!",
          image: require("@/assets/kitty.png"),
          likes: 100,
          comments: 10,
          downloads: 500,
          showComments: false,
          commentList: [
            {
              id: 1,
              avatar: require("@/assets/kitty.png"),
              nickname: "Ragdoll",
              text: "The kitty is super cute!",
              time: "1 hour ago",
              likes: 5,
              replies: [
                {
                  id: 11,
                  avatar: require("@/assets/kitty.png"),
                  nickname: "Maine Coon Cat",
                  text: "Meow!",
                  time: "1 hour ago",
                  likes: 2,
                },
              ],
            },
            {
              id: 2,
              avatar: require("@/assets/kitty.png"),
              nickname: "British Shorthair",
              text: "Give me more kitties!",
              time: "1 hour ago",
              likes: 10,
              replies: [
                {
                  id: 21,
                  avatar: require("@/assets/kitty.png"),
                  nickname: "American Shorthair",
                  text: "Everyone, come check out the kitty I drew!",
                  time: "30 minutes ago",
                  likes: 4,
                },
              ],
            },
          ],
        },
      ],
      userProfile: {
        nickname: "",
        profileImage: "",
        profileCover: "",
      },
    };
  },
  methods: {
    async fetchUserProfile() {
      try {
        // 從 localStorage 獲取 userEmail
        const userEmail = localStorage.getItem("userEmail");
        if (!userEmail) throw new Error("User email is not set in localStorage");

        // 調用後端 API
        const response = await fetch(`/api/getUser?userEmail=${userEmail}`);
        const result = await response.text();

        // 解析後端返回的數據
        const userProfile = this.parseUserProfile(result);
        if (userProfile) {
          this.userProfile.nickname = userProfile.nickname;
          this.userProfile.profileImage = userProfile.profileImage;
          this.userProfile.profileCover = userProfile.profileCover;
        }
      } catch (error) {
        console.error("Failed to fetch user profile:", error.message);
      }
    },
    parseUserProfile(responseText) {
      const regex = /User\{id='.*?', email='.*?', nickname='(.*?)', password='.*?', profileImage='(.*?)', profileCover='(.*?)'\}/;
      const match = responseText.match(regex);
      if (match) {
        return {
          nickname: match[1],
          profileImage: match[2],
          profileCover: match[3],
        };
      }
      return null;
    },
    likePost(id) {
      const post = this.posts.find((post) => post.id === id);
      if (post) post.likes++;
    },
    downloadImage(postId) {
      const post = this.posts.find((p) => p.id === postId);
      if (post && post.image) {
        // 創建一個隱藏的 a 標籤
        const link = document.createElement("a");
        link.href = post.image; // 設置圖片的 URL
        link.download = `post_${postId}.png`; // 設置下載文件名
        link.click(); // 模擬點擊觸發下載
        link.remove(); // 清理 DOM 元素
      }
    },
    toggleComments(postId) {
      const post = this.posts.find((p) => p.id === postId);
      if (post) post.showComments = !post.showComments;
    },
    likeComment(postId, commentId) {
      const post = this.posts.find((p) => p.id === postId);
      const comment = post?.commentList.find((c) => c.id === commentId);
      if (comment) comment.likes++;
    },
    toggleReplies(commentId, postId) {
      const post = this.posts.find((p) => p.id === postId);
      const comment = post?.commentList.find((c) => c.id === commentId);
      if (comment) comment.showReplies = !comment.showReplies;
    },
    likeReply(postId, commentId, replyId) {
      const post = this.posts.find((p) => p.id === postId);
      const comment = post?.commentList.find((c) => c.id === commentId);
      const reply = comment?.replies.find((r) => r.id === replyId);
      if (reply) reply.likes++;
    },
    enterDrawingArea() {
      this.$router.push("/drawingArea");
    },
    collectFeedback() {
      this.$router.push("/userFeedback");
    },
    toUserDetail() {
      this.$router.push({
        path: '/profile',
        query: {
          nickname: this.userProfile.nickname,
          profileImage: this.userProfile.profileImage,
          profileCover: this.userProfile.profileCover,
        },
      }
      );
    },
    addNewPost() {
      this.$router.push("/addPostView");
    },
  },
  async created() {
    await this.fetchUserProfile();
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
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.right-sidebar .user-profile img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin-right: 10px;
  /* 增加間距 */
}

.right-sidebar .user-profile p {
  font-size: 1.2em;
  font-weight: bold;
  margin: 0;
  /* 去掉預設間距 */
}

.right-sidebar .actions {
  display: flex;
  flex-direction: column;
  /* 將按鈕垂直排列 */
  align-items: center;
  /* 對齊到中間 */
}

.right-sidebar .actions button {
  width: 300px;
  /* 讓按鈕寬度適應內容 */
  padding: 10px;
  margin-bottom: 10px;
  text-align: center;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.right-sidebar .actions .enterDrawingAreaBtn {
  font-size: 3em;
  height: 300px;
  width: 60%;
}

.right-sidebar .actions .collectFeedbackBtn {
  margin-top: 50px;
  font-size: 1.2em;

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

.header {
  background-color: #f5f5f5;
  padding: 10px;
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  border-bottom: 1px solid #ddd;
}

.main {
  padding: 20px;
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
  margin-top: 10px;
}

.content .post .interactions button {
  margin-right: 10px;
  padding: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.comments-section {
  margin-top: 10px;
  padding: 10px;
  border-top: 1px solid #ddd;
  background-color: #f9f9f9;
}

.comment,
.reply {
  margin-bottom: 20px;
  padding-bottom: 10px;
}

.comment-header,
.reply-header {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
}

.comment-header img,
.reply-header img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.comment-actions,
.reply-actions {
  margin-top: 5px;
}

.replies {
  margin-left: 20px;
  padding-left: 10px;
  border-left: 2px solid #ddd;
}

.user-info {
  display: flex;
  align-items: flex-start;
  margin-bottom: 10px;
}

.user-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  margin-right: 10px;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.user-details strong {
  font-size: 16px;
  color: #333;
}

.user-details .time {
  font-size: 12px;
  color: #888;
  margin-top: 5px;
}
</style>