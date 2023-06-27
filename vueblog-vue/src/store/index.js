import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    token:localStorage.getItem('token'),
    userInfo:JSON.parse(sessionStorage.getItem('userInfo'))
  },
  getters: {
    // get
    getUser: state => {
      return state.userInfo
    },
    getToken: state => {
      return state.token
    }
  },
  mutations: {
    // set
    SET_TOKEN: (state, token) =>{
      state.token = token
      // 浏览器关闭时,存在浏览器本地
      localStorage.setItem("token",token)
    },
    SET_USER_INFO: (state, userInfo) =>{
      state.userInfo = userInfo
      // session里无法直接存JSON数据,所以需要将JSON进行序列化
      // sessionStorage.setItem("userInfo",userInfo)
      sessionStorage.setItem("userInfo",JSON.stringify(userInfo))
    },
    REMOVE_USER_INFO: (state) =>{
      state.token = ""
      state.userInfo = {}
      localStorage.setItem("token","")
      sessionStorage.setItem("userInfo",JSON.stringify(""))
    }
  },
  actions: {
  },
  modules: {
  }
})
