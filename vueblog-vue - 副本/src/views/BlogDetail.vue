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

    </div>
    <Bottom></Bottom>
  </div>
</template>

<script>
import Bottom from "../components/Bottom.vue"
import Header from "../components/Header.vue"
//导入github-markdown样式后class用markdown-body即可使用
import "github-markdown-css/github-markdown.css"

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
      ownBlog: false
    }
  },
  methods: {
    deleteBlog() {
      this.$confirm('此操作将永久删除文章,是否继续?', '提示', {
        distinguishCancelAndClose: true,
        confirmButtonText: '是',
        cancelButtonText: '否'
      })
          .then(() => {
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
          })
          .catch(action => {
            this.$message({
              type: 'info',
              message: action === 'cancel'
                  ? '放弃删除'
                  : '停留在当前页面'
            })
          });


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

      console.log(blog)
      _this.blog.content = result

      if (_this.$store.getters.getUser !== null &&
          this.$store.getters.getUser.id === blog.userId) {
        _this.ownBlog = true
      }

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

</style>