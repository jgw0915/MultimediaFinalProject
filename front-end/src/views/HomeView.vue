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
              <button @click="likePost(post.id, true)">Like ({{ post.likes }})</button>
              <button @click="toggleComments(post.id)">Comment ({{ post.comments }})</button>
              <button @click="downloadImage(post.id)">Download ({{ post.downloads }})</button>
            </div>

            <!-- 留言區 -->
            <div v-if="post.showComments" class="comments-section">
              <h4>All comments</h4>
              <div class="add-comment">
                <input v-model="post.newCommentText" type="text" placeholder="Write a comment..." />
                <button @click="addComment(post.id, post.newCommentText)">Submit</button>
              </div>
              <div v-for="(comment, commentIndex) in post.commentList" :key="comment.id" class="comment">
                <div class="comment-header">
                  <img :src="comment.avatar" alt="User Avatar" />
                  <div>
                    <strong>{{ comment.nickname }}</strong>
                    <span>{{ comment.time }}</span>
                  </div>
                </div>
                <p>{{ comment.text }}</p>
                <div class="comment-actions">
                  <button @click="likeComment(post.id, comment, commentIndex)">Like ({{ comment.likes
                    }})</button>
                  <button @click="toggleReplies(comment.id, post.id)">Reply ({{ comment.replies.length
                    }})</button>
                </div>
                <div class="add-reply">
                  <input v-model="post.replyInput[commentIndex]" type="text" placeholder="Write a reply..." />
                  <button @click="addReply(post.id, post.replyInput[commentIndex], commentIndex)">Submit</button>
                </div>

                <!-- 回覆區 -->
                <div v-if="comment.showReplies" class="replies">
                  <div v-for="(reply, replyIndex) in comment.replies" :key="reply.id" class="reply">
                    <div class="reply-header">
                      <img :src="reply.avatar" alt="User Avatar" />
                      <div>
                        <strong>{{ reply.nickname }}</strong>
                        <span>{{ reply.time }}</span>
                      </div>
                    </div>
                    <p>{{ reply.text }}</p>
                    <div class="reply-actions">
                      <button @click="likeReply(post.id, commentIndex, replyIndex, reply)">Like ({{
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
          <button class="collectFeedbackBtn" @click="collectFeedback">User Feedback Collection</button>
        </div>
      </div>
    </div>
    <button class="floating-button" @click="enterDrawingArea">+</button>
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
      posts: [],
      userProfile: {
        profileImage: "https://via.placeholder.com/80",
        nickname: "Anonymous",
        profileCover: "https://via.placeholder.com/300x150",
        email: "",
      },
    };
  },
  methods: {
    async addReply(postId, replyText, commentIndex) {
      if (!replyText) {
        alert("請輸入文字!");
        return;
      }
      try {
        const response = await fetch(`/api/posts/${postId}/reply?commentId=${commentIndex}`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            user: {
              profileImage: this.userProfile.profileImage,
              nickname: this.userProfile.nickname,
              profileCover: this.userProfile.profileCover,
              email: this.userProfile.email,
            },
            text: replyText,
          }),
        });
        if (!response.ok) {
          throw new Error(`API Error: ${response.statusText}`);
        } else {
          await this.fetchAllPosts();
        }
      } catch (error) {
        console.error("Failed to add reply:", error.message);
        alert("Failed to add reply. Please try again.");
      }
    },
    async addComment(postId, commentText) {
      if (!commentText) {
        alert("請輸入文字!");
        return;
      }
      try {
        const response = await fetch(`/api/posts/${postId}/comments`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            user: {
              profileImage: this.userProfile.profileImage,
              nickname: this.userProfile.nickname,
              profileCover: this.userProfile.profileCover,
              email: this.userProfile.email,
            },
            text: commentText,
            replies: "",
          }),
        });
        if (!response.ok) {
          throw new Error(`API Error: ${response.statusText}`);
        } else {
          await this.fetchAllPosts();
        }
      } catch (error) {
        console.error("Failed to add comment:", error.message);
        alert("Failed to add comment. Please try again.");
      }
    },
    async fetchUserProfile() {
      try {
        const userEmail = localStorage.getItem("userEmail");
        if (!userEmail) throw new Error("User email is not set in localStorage");

        const response = await fetch(`/api/getUser?userEmail=${userEmail}`);
        if (!response.ok) throw new Error(`API error: ${response.statusText}`);

        const userProfile = await response.json();
        this.userProfile = userProfile;
      } catch (error) {
        console.error("Failed to fetch user profile:", error.message);
      }
    },
    async fetchAllPosts() {
      try {
        const response = await fetch(`/api/posts/getAll`);
        if (!response.ok) throw new Error(`API error: ${response.statusText}`);
        const data = await response.json();

        this.posts = data.map(post => ({
          id: post.id,
          user: post.author.nickname || "Anonymous",
          time: new Date(post.createdAt).toLocaleString(),
          avatar: post.author.profileImage || "https://via.placeholder.com/50",
          text: post.contentText,
          image: post.contentImage,
          likes: post.likes,
          downloads: post.downloads,
          comments: post.comments.length,
          replyInput: post.comments.reduce((acc, comment) => {
            acc[comment.id] = ""; // 初始化每個 comment 的 replyText 為空
            return acc;
          }, {}),
          commentList: post.comments.map(comment => ({
            id: comment.id,
            avatar: comment.user.profileImage || "https://via.placeholder.com/50",
            nickname: comment.user.nickname || "Anonymous",
            text: comment.text,
            time: new Date(comment.createdAt).toLocaleString(),
            likes: comment.likes || 0,
            replies: comment.replies
              ? comment.replies.map(reply => ({
                id: reply._id,
                avatar: reply.user._profileImage || "https://via.placeholder.com/50",
                nickname: reply.user._nickname || "Anonymous",
                text: reply.text,
                time: new Date(reply.createdAt).toLocaleString(),
                likes: reply.likes || 0,
              }))
              : [],
            showReplies: true,
          })),
          showComments: true,
        }));


      } catch (error) {
        console.error("Failed to fetch all posts:", error.message);
      }
    },
    async likePost(id, increase) {
      try {
        const response = await fetch(`/api/posts/like/${id}?increase=${increase}`, {
          method: 'PUT',
        });

        if (response.ok) {
          const post = this.posts.find((post) => post.id === id);
          if (post) {
            if (increase) {
              post.likes++;
            } else {
              post.likes--;
            }
          }
          console.log('Post updated successfully');
        } else {
          const errorText = await response.text();
          console.error('Error updating post:', errorText);
          alert(`Error: ${errorText}`);
        }
      } catch (error) {
        console.error('Network error:', error);
        alert('Failed to update post due to a network error.');
      }
    },
    async downloadImage(postId) {
      const post = this.posts.find((p) => p.id === postId);
      if (post && post.image) {
        const link = document.createElement("a");
        link.href = post.image;
        link.download = `post_${postId}.png`;
        link.click();
        link.remove();
        try {
          const response = await fetch(`/api/posts/download/${postId}`, {
            method: 'PUT',
          });

          if (response.ok) {
            post.download++;

            console.log('Post updated successfully');
          } else {
            const errorText = await response.text();
            console.error('Error updating post:', errorText);
            alert(`Error: ${errorText}`);
          }
        } catch (error) {
          console.error('Network error:', error);
          alert('Failed to update post due to a network error.');
        }
      }
    },
    toggleComments(postId) {
      const post = this.posts.find((p) => p.id === postId);
      if (post) post.showComments = !post.showComments;
    },
    async likeComment(postId, comment, commentIndex) {
      try {
        const response = await fetch(`/api/posts/likeComment/${postId}?commentId=${commentIndex}`, {
          method: 'PUT',
        });

        if (response.ok) {
          comment.likes++
          console.log('Post updated successfully');
        } else {
          const errorText = await response.text();
          console.error('Error updating post:', errorText);
          alert(`Error: ${errorText}`);
        }
      } catch (error) {
        console.error('Network error:', error);
        alert('Failed to update post due to a network error.');
      }
    },
    toggleReplies(commentId, postId) {
      const post = this.posts.find((p) => p.id === postId);
      const comment = post?.commentList.find((c) => c.id === commentId);
      if (comment) comment.showReplies = !comment.showReplies;
    },
    async likeReply(postId, commentIndex, replyIndex, reply) {
      try {
        const response = await fetch(`/api/posts/likeReply/${postId}?commentId=${commentIndex}&replyId=${replyIndex}`, {
          method: 'PUT',
        });

        if (response.ok) {
          reply.likes++
          console.log('Post updated successfully');
        } else {
          const errorText = await response.text();
          console.error('Error updating post:', errorText);
          alert(`Error: ${errorText}`);
        }
      } catch (error) {
        console.error('Network error:', error);
        alert('Failed to update post due to a network error.');
      }
    },
    enterDrawingArea() {
      this.$router.push({
        path: "/drawingArea",
        query: {
          nickname: this.userProfile.nickname,
          profileImage: this.userProfile.profileImage,
          profileCover: this.userProfile.profileCover,
          email: this.userProfile.email,
        },
      });
    },
    collectFeedback() {
      this.$router.push("/userFeedback");
    },
    toUserDetail() {
      this.$router.push({
        path: '/profile',
        query: {
          email: this.userProfile.email,
        },
      }
      );
    },
    addNewPost() {
      this.$router.push({
        path: "/addPostView",
        query: {
          nickname: this.userProfile.nickname,
          profileImage: this.userProfile.profileImage,
          profileCover: this.userProfile.profileCover,
          email: this.userProfile.email,
        },
      });
    },
  },
  async created() {
    if (localStorage.getItem("isLogin") == "false") {
      this.$router.push("/login")
    }
    await this.fetchUserProfile();
    await this.fetchAllPosts();
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
  width: 60%;

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