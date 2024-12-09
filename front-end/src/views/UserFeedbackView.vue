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
/* General body styling */
.body {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background-color: #f5f5f5;
    font-family: 'Arial', sans-serif;
    color: #333;
}

/* Wrapper container */
.wrapper {
    width: 100%;
    max-width: 600px;
    padding: 30px;
    background-color: #fff;
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    text-align: center;
}

/* Heading styling */
.wrapper h1 {
    font-size: 24px;
    margin-bottom: 20px;
    color: #007bff;
}

/* Input box styling */
.inputBox {
    margin-bottom: 20px;
}

.inputBox textarea {
    width: 80%;
    height: 150px;
    padding: 15px;
    border: 1px solid #ccc;
    border-radius: 8px;
    font-size: 16px;
    resize: none;
    background-color: #f9f9f9;
    transition: border-color 0.3s ease-in-out, box-shadow 0.3s ease-in-out;
}

.inputBox textarea:focus {
    border-color: #007bff;
    box-shadow: 0 0 8px rgba(0, 123, 255, 0.25);
    outline: none;
}

/* Button styling */
.button {
    padding: 10px 20px;
    font-size: 16px;
    font-weight: bold;
    color: white;
    background-color: #007bff;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s ease-in-out, transform 0.2s ease-in-out;
}

.button:hover {
    background-color: #0056b3;
    transform: scale(1.05);
}

.button:active {
    background-color: #003d82;
    transform: scale(1);
}

/* Responsive design */
@media (max-width: 768px) {
    .wrapper {
        padding: 20px;
    }

    .wrapper h1 {
        font-size: 20px;
    }

    .inputBox textarea {
        font-size: 14px;
        height: 120px;
    }

    .button {
        font-size: 14px;
        padding: 8px 15px;
    }
}
</style>