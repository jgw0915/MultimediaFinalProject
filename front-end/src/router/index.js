import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import ForgotPasswordView from '../views/ForgotPasswordView.vue'
import ResetPasswordView from '../views/ResetPasswordView.vue'
import ProfileView from '../views/ProfileView.vue'
import HomeView from '../views/HomeView.vue'
import DrawingAreaView from '../views/DrawingAreaView.vue'
import UserFeedbackView from '../views/UserFeedbackView.vue'
import AddPostView from '../views/AddPostView.vue'
import EditPostView from '../views/EditPostView.vue'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: LoginView
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView
  },
  {
    path: '/forgotPassword',
    name: 'forgotPassword',
    component: ForgotPasswordView
  },
  {
    path: '/resetPassword',
    name: 'resetPassword',
    component: ResetPasswordView
  },
  {
    path: '/profile',
    name: 'profile',
    component: ProfileView
  },
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/drawingArea',
    name: 'drawingArea',
    component: DrawingAreaView
  },
  {
    path: '/userFeedback',
    name: 'userFeedback',
    component: UserFeedbackView
  },
  {
    path: '/addPostView',
    name: 'addPostView',
    component: AddPostView
  },
  {
    path: '/editPost',
    name: 'editPost',
    component: EditPostView
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
