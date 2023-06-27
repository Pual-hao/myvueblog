import axios from 'axios'
import Element from 'element-ui'
import router from './router'
import store from './store'


axios.defaults.baseURL = "http://localhost:8081"

// 前置拦截
axios.interceptors.request.use(config => {
    return config
})

// 后置拦截,Login.vue里调用$axios.post的res已经被拦截下来
axios.interceptors.response.use(response => {
        let res = response.data;
        // 这个是Result层的状态信息
        if (res.code === 200) {
            return response
        } else {
            Element.Message.error("这是一条错误的提示消息", {duration: 1*1000})

            //返回一个异常提示就不会继续往下走了 不+的话 res=>的里面 还是会继续走的
            return Promise.reject(response.data.msg)

        }
    },
    // 处理当Response为异常时的情况
    error => {
        //console.log(error)
        if(error.response.data){
            error.message = error.response.data.msg
        }

        // 无权限
        // 这个是Response的状态----即状态HttpStatus.UNAUTHORIZED
        if(error.response.status === 401){
            store.commit("REMOVE_USER_INFO")
            router.push("/login")
        }

        Element.Message.error(error.message, {duration: 1*1000})
        return Promise.reject(error)
    }
)