<template>
<div class="m-content">
  <h3>欢迎来到千山暮雪的博客</h3>
  <div class="block">
    <el-avatar :size="50" :src="user.avatar"></el-avatar>
    <div>{{user.username}}</div>
  </div>
  <div class="macation">

    <span><el-link type="info" href="/blogs">主页</el-link></span>

    <el-divider direction="vertical" v-if="hasLogin"></el-divider>
    <span><el-link type="success" href="/blog/add" v-if="hasLogin">发表博客</el-link></span>

    <el-divider direction="vertical"></el-divider>
    <span v-if="!hasLogin"><el-link type="primary" href="/login">登录</el-link></span>

    <span v-if="hasLogin"><el-link type="danger" @click="logout">退出</el-link></span>
  </div>
</div>
</template>

<script>
export default {
  name: "Header.vue",
  data() {
    return {
      user :{
        username : "游客",
        avatar: "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
      },
      hasLogin : false
    }
  },
  methods:{
    logout(){
      const _this = this;
      _this.$axios.get("/logout",{
        headers:{
          "Authorization": _this.$store.getters.getToken
        }
      }).then(res=>{
        _this.$store.commit("REMOVE_USER_INFO")
        _this.$router.push("/login")
      })
    }
  },
  created() {
    if(this.$store.getters.getUser.username){
      this.user.username = this.$store.getters.getUser.username
      this.user.avatar = this.$store.getters.getUser.avatar

      this.hasLogin = true
    }
  }
}
</script>

<style scoped>
  .m-content{
    text-align: center;
    margin: 0 auto;
    max-width: 960px;
  }

  .macation{
    margin: 10px 0;
  }
</style>