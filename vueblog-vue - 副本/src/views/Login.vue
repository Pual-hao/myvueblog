<template>
  <el-container>
    <el-header>
      <img class="mlogo"
           src="https://cdn.jsdelivr.net/gh/Pual-hao/myPicGoBed@main/img/202306262149795.jpg"
           alt="">
    </el-header>
    <el-main>
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="ruleForm.username"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="ruleForm.password"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm('ruleForm')">登录</el-button>
          <el-button @click="resetForm('ruleForm')">重置</el-button>
        </el-form-item>
      </el-form>
    </el-main>
    <Bottom></Bottom>
  </el-container>

</template>

<script>
import Bottom from "../components/Bottom.vue";

export default {
  name: "Login",
  components: {Bottom},
  data() {
    return {
      ruleForm: {
        username: '',
        password: ''
      },
      rules: {
        username: [
          {required: true, message: '请输入用户名', trigger: 'blur'},
          {min: 3, max: 15, message: '长度在 3 到 15 个字符', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'change'}
        ]
      }
    };
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          const _this = this;
          //alert('submit!');
          // post的Url填写后端的PostMapping里的地址,端口号要填写后端项目的端口号
          this.$axios.post('/login', this.ruleForm).then(res => {
            // console.log(res.headers)
            console.log(res)
            const jwt = res.headers['authorization']
            const userInfo = res.data.data

            // 将jwt与userInfo传入store中
            // 使用commit接口将数据传入store中
            // 把数据共享至store
            _this.$store.commit("SET_TOKEN",jwt)
            _this.$store.commit("SET_USER_INFO",userInfo)

            //获取token和getUser
            // console.log(_this.$store.getters.getToken)
            // console.log(_this.$store.getters.getUser)

            // 跳转到页面
            _this.$router.push("/blogs")
          })
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    }
  }
}

</script>

<style scoped>
.el-header, .el-footer {
  background-color: #B3C0D1;
  color: #333;
  text-align: center;
  line-height: 60px;
}

.el-aside {
  background-color: #D3DCE6;
  color: #333;
  text-align: center;
  line-height: 200px;
}

.el-main {
//background-color: #E9EEF3; color: #333; text-align: center;
  line-height: 160px;
}

body > .el-container {
  margin-bottom: 40px;
}

.el-container:nth-child(5) .el-aside,
.el-container:nth-child(6) .el-aside {
  line-height: 260px;
}

.el-container:nth-child(7) .el-aside {
  line-height: 320px;
}

.mlogo {
  height: 60%;
  margin-top: 10px;
}

.demo-ruleForm {
  max-width: 50%;
  margin: 0 auto;
}
</style>