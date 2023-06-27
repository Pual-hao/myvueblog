<template>
  <div>
    <Header></Header>
    <div class="mblog">
      <h2>{{ blog.title }}</h2>

      <router-link :to="{name:'BlogEdit',params:{blogId:blog.id}}">
        <el-button type="primary" icon="el-icon-edit" v-if="ownBlog"></el-button>
      </router-link>
      <el-button type="primary" icon="el-icon-delete" v-if="ownBlog" @click="deleteBlog"></el-button>
      <el-divider></el-divider>

      <div class="markdown-body" v-html="blog.content"></div>

      <el-divider></el-divider>
      <el-timeline>
        <el-timeline-item :timestamp="comment.created" placement="top" v-for="comment in comments">
          <el-button type="danger" v-if="ownBlog" @click="deleteComment(comment.id)" icon="el-icon-delete" circle></el-button>
          <el-card>
            <p class="markdown-body" v-html="comment.description"></p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-pagination class="m-page"
                     background
                     layout="prev, pager, next"
                     :current-page="this.currentPage"
                     :page-size="this.pageSize"
                     :total="this.total"
                     @current-change="page"
      >
      </el-pagination>

      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
        <el-form-item label="评论" prop="description">
          <mavon-editor v-model="ruleForm.description"></mavon-editor>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm('ruleForm')">发表评论</el-button>
          <el-button @click="resetForm('ruleForm')">重置</el-button>
        </el-form-item>
      </el-form>
      <Bottom></Bottom>
    </div>

  </div>
</template>

<script>
import Bottom from "../components/Bottom.vue"
import Header from "../components/Header.vue"
//导入github-markdown样式后class用markdown-body即可使用
import "github-markdown-css/github-markdown.css"
import MarkdownIt from "markdown-it";

export default {
  name: "BlogDetail.vue",
  components: {Bottom, Header},
  data() {
    return {
      blog: {
        id: "",
        title: "",
        content: ""
      },
      ruleForm: {
        description: ""
      },
      ownBlog: false,
      comments: {},
      currentPage: 1,
      total: 0,
      pageSize: 8,
      rules: {
        description: [
          {required: true, message: '请输入内容', trigger: 'blur'}
        ]
      }
    }
  },
  methods: {
    deleteBlog() {
      this.$confirm('此操作将永久删除文章,是否继续?', '提示', {
        distinguishCancelAndClose: true,
        confirmButtonText: '是',
        cancelButtonText: '否'
      }).then(() => {
        const _this = this
        const blogId = _this.$route.params.blogId
        this.$axios.post('/blog/' + blogId, null, {
          headers: {
            "Authorization": _this.$store.getters.getToken
          }
        }).then(res => {
          if (res.data.data === "文章删除成功") {
            this.$notify({
              title: '成功',
              message: '成功删除该文章',
              type: 'success'
            });
            _this.$router.push('/blogs')
          }
        })
      }).catch(action => {
        this.$message({
          type: 'info',
          message: action === 'cancel'
              ? '放弃删除'
              : '停留在当前页面'
        })
      });
    },
    deleteComment(commentId) {
      this.$confirm('此操作将永久删除评论,是否继续?', '提示', {
        distinguishCancelAndClose: true,
        confirmButtonText: '是',
        cancelButtonText: '否'
      }).then(() => {
        const _this = this;
        this.$axios.post('/comments/delete/' + commentId, null, {
          headers: {
            "Authorization": _this.$store.getters.getToken
          }
        }).then(res => {
          if (res.data.data === "评论删除成功") {
            this.$message('评论删除成功');
          }
          this.$router.go(0)
        })
      }).catch(action => {
        this.$message({
          type: 'info',
          message: action === 'cancel'
              ? '放弃删除'
              : '停留在当前页面'
        })
      })
    }

    ,
    page(currentPage) {
      const _this = this;
      this.$axios.get('/comments/ ' + _this.blog.id + '?currentPage=' + currentPage)
          .then(res => {
            console.log(res)
            _this.comments = res.data.data.records
            _this.currentPage = res.data.data.current
            _this.total = res.data.data.total
            _this.pageSize = res.data.data.size
            var MarkdownIt = require("markdown-it")
            var md = new MarkdownIt()
            for (let i = 0; i < _this.comments.length; i++) {
              _this.comments[i].description = md.render(_this.comments[i].description)
            }
          })
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          const _this = this
          this.$axios.post('/comments/' + this.blog.id, this.ruleForm).then(res => {
            console.log(res)

            this.$alert('操作成功', '提示', {
              confirmButtonText: '确定',
              callback: action => {
                this.$router.go(0)
              }
            });

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
  ,
  created() {
    const _this = this
    const blogId = this.$route.params.blogId
    this.$axios.get('/blog/' + blogId, {
      headers: {
        "Authorization": _this.$store.getters.getToken
      }
    }).then(res => {
      const blog = res.data.data
      _this.blog.id = blog.id
      _this.blog.title = blog.title

      var MarkdownIt = require("markdown-it")
      var md = new MarkdownIt()
      var result = md.render(blog.content)

      //console.log(blog)
      _this.blog.content = result

      if (_this.$store.getters.getUser !== null &&
          this.$store.getters.getUser.id === blog.userId) {
        _this.ownBlog = true
      }
      //初始化评论
      this.page(1)
    })
  }
}
</script>

<style scoped>
.mblog {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  width: 100%;
  height: 700px;
  padding: 20px 15px;
}

.m-page {
  margin: 0 auto;
  text-align: center;
}

.demo-ruleForm {
  margin: 0 auto;
  text-align: center;
}
</style>