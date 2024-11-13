<template>
  <div class="body">
    <div class="wrapper">
      <form id="loginForm" @submit.prevent="handleLoginForm">
        <h1>Login</h1>
        <div class="inputBox">
          <input type="text" placeholder="Email" v-model="email" />
        </div>
        <div class="inputBox">
          <input
            :type="passwordVisible ? 'text' : 'password'"
            placeholder="Password"
            id="password"
            v-model="password"
          />
          <i
            :class="
              passwordVisible ? 'bx bx-show eye-icon' : 'bx bx-hide eye-icon'
            "
            @click="showPassword"
          ></i>
        </div>
        <button type="submit" class="button">Login</button>
        <div class="registerLink">
          <p>Don't have an account? <a href="register">Register</a></p>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  name: "LoginPage",
  data() {
    return {
      email: "",
      password: "",
      passwordVisible: false,
    };
  },
  methods: {
    handleLoginForm() {
      const formData = {
        email: this.email,
        password: this.password,
      };
      console.log(formData);
    },
    showPassword() {
      this.passwordVisible = !this.passwordVisible;
    },
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

.wrapper {
  width: 420px;
  background: rgba(144, 189, 231, 0.479);
  border: 2px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(20px);
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
  color: #fff;
  border-radius: 10px;
  padding: 30px 40px;
}

.wrapper h1 {
  font-size: 36px;
  text-align: center;
}

.wrapper .inputBox {
  width: 100%;
  height: 50px;
  margin: 30px 0;
}

.inputBox input {
  width: 100%;
  height: 100%;
  background-color: transparent;
  border: none;
  outline: none;
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: 40px;
  font-size: 16px;
  color: white;
  padding: 20px 45px 20px 20px;
}

.inputBox input::placeholder {
  color: white;
}

.inputBox i {
  font-size: 20px;
  position: absolute;
  top: 39.5%;
  right: 55px;
}

.wrapper .remeber-forget {
  display: flex;
  justify-content: space-between;
  font-size: 14.5px;
  margin: -15px 0 15px;
}

.remeber-forget label input {
  accent-color: #fff;
  margin-right: 3px;
}

.remeber-forget a:hover {
  text-decoration: underline;
  color: white;
}

.wrapper .button {
  margin: 3px;
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

.wrapper .registerLink {
  font-size: 14.5px;
  text-align: center;
  margin: 20px 0 15px;
}

.registerLink p a {
  color: #fff;
  text-decoration: none;
  font-weight: 600;
}

.registerLink p a:hover {
  text-decoration: underline;
}
</style>