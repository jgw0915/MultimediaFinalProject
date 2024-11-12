<template>
  <div class="body">
    <div class="wrapper">
      <form id="loginForm" @submit.prevent="handleRegisterForm">
        <h1>Register</h1>

        <div class="inputBox">
          <input type="text" placeholder="Email" v-model="email" />
        </div>
        <div class="inputBox">
          <input type="text" id="name" placeholder="Name" v-model="name" />
        </div>
        <div class="inputBox">
          <input
            type="text"
            id="phoneNumber"
            placeholder="PhoneNumber"
            v-model="phoneNumber"
          />
        </div>
        <div class="inputBox">
          <input
            :type="passwordVisible ? 'text' : 'password'"
            id="password"
            placeholder="Create Password"
            v-model="password"
          />
        </div>
        <div class="inputBox">
          <input
            :type="passwordVisible ? 'text' : 'password'"
            id="comfirmPassword"
            placeholder="Comfirm Password"
            v-model="comfirmPassword"
          />
          <i
            :class="
              passwordVisible ? 'bx bx-show eye-icon' : 'bx bx-hide eye-icon'
            "
            @click="showPassword"
          ></i>
        </div>

        <button type="submit" class="button">Register</button>

        <div class="registerLink">
          <p>Already have an account? <a href="login">Login</a></p>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  name: "RegisterPage",
  data() {
    return {
      email: "",
      password: "",
      comfirmPassword: "",
      phoneNumber: "",
      name: "",
      passwordVisible: false,
      isMerchant: false,
    };
  },
  methods: {
    handleRegisterForm() {
      let role = "user";
      if (this.isMerchant) role = "merchant";

      // const passwordRequirements =
      // /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{12,72}$/;

      // if (!passwordRequirements.test(this.password)) {
      //   alert(
      //     "密碼需要介於12與72字元且包含「大寫字母 + 小寫字母 + 數字 + 特殊符號」"
      //   );
      //   return;
      // }

      if (this.password != this.comfirmPassword) {
        alert("密碼不一致!");
        return;
      }

      const formData = {
        email: this.email,
        password: this.password,
        phoneNumber: this.phoneNumber,
        name: this.name,
        role: role,
      };

      fetch("/auth/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formData),
      })
        .then((response) => {
          if (response.ok) {
            alert("註冊成功");
            this.password = "";
            this.comfirmPassword = "";
            window.location.href = "login";
          } else {
            alert("使用者已存在");
            console.log("註冊失败");
          }
        })
        .catch((error) => {
          console.error("錯誤:", error);
        });
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
  /* background: url(../images/360_F_355607062_zYMS8jaz4SfoykpWz5oViRVKL32IabTP.jpg); */
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
  top: 54.5%;
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