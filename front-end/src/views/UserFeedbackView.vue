<template>
    <div class="body">
        <div class="wrapper">
            <h1>Write any feedbacks to improve the platform!</h1>
            <div class="inputBox">
                <textarea v-model="feedback" placeholder="Write your feedback here..."></textarea>
            </div>
            <button class="button" @click="submitFeedback">Submit</button>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            feedback: '',
        };
    },
    methods: {
        submitFeedback() {
            if (this.feedback.trim() === '') {
                alert('Please enter your feedback before submitting!');
            } else {
                console.log('Feedback submitted:', this.feedback);
                alert('Thank you for your feedback!');
                this.$router.push('/');
                this.feedback = '';
            }
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
    color: #000;
    border-radius: 10px;
    padding: 30px 40px;
    text-align: center;
}

.wrapper h1 {
    font-size: 24px;
    margin-bottom: 20px;
    color: #333;
}

.inputBox {
    width: 100%;
    margin: 20px 0;
}

.inputBox textarea {
    width: 100%;
    height: 150px;
    background-color: rgba(255, 255, 255, 0.8);
    border: 2px solid rgba(255, 255, 255, 0.2);
    border-radius: 10px;
    padding: 15px;
    font-size: 16px;
    color: #333;
    outline: none;
    resize: none;
}

.inputBox textarea::placeholder {
    color: #aaa;
}

.wrapper .button {
    margin-top: 15px;
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