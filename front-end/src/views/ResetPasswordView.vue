<template>
    <div class="body">
        <div class="wrapper">
            <form id="resetPasswordForm" @submit.prevent="handleResetPasswordForm">
                <h1>Reset Password</h1>

                <div class="inputBox">
                    <input :type="passwordVisible ? 'text' : 'password'" id="password" placeholder="New Password"
                        v-model="password" />
                </div>
                <div class="inputBox">
                    <input :type="passwordVisible ? 'text' : 'password'" id="comfirmPassword"
                        placeholder="Comfirm Password" v-model="comfirmPassword" />
                    <i :class="passwordVisible ? 'bx bx-show eye-icon' : 'bx bx-hide eye-icon'
                        " @click="showPassword"></i>
                </div>

                <button type="submit" class="button">Send to reset password</button>
            </form>
        </div>
    </div>
</template>

<script>
export default {
    name: "RegisterPage",
    data() {
        return {
            password: "",
            comfirmPassword: "",
            passwordVisible: false,
        };
    },
    methods: {
        handleResetPasswordForm() {
            const formData = {
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
    top: 57%;
    right: 55px;
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
</style>