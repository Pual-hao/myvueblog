<template>
  <div>
    <Header></Header>
    <div class="block">
      <el-timeline>
        <el-timeline-item :timestamp="blog.created" placement="top" v-for="blog in blogs">
          <el-card>
            <router-link :to="{name:'BlogDetail',params:{blogId:blog.id}}">
              <h4>{{ blog.title }}</h4>
            </router-link>
            <p>{{ blog.description }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-pagination class="mpage"
                     background
                     layout="prev, pager, next"
                     :current-page="this.currentPage"
                     :page-size="this.pageSize"
                     :total="this.total"
                     @current-change="page"
      >
      </el-pagination>

    </div>
    <Bottom></Bottom>
  </div>
</template>

<script>
import Header from "../components/Header.vue";
import Bottom from "../components/Bottom.vue";

export default {
  name: "Blogs",
  components: {Header, Bottom},
  data() {
    return {
      blogs: {},
      currentPage: 1,
      total: 0,
      pageSize: 5
    }
  },
  methods: {
    page(currentPage) {
      const _this = this;
      this.$axios.get('/blogs?currentPage=' + currentPage).then(res => {
        console.log(res)
        _this.blogs = res.data.data.records
        _this.currentPage = res.data.data.current
        _this.total = res.data.data.total
        _this.pageSize = res.data.data.size
      })
    }
  },
  created() {
    this.page(1)
  }
}

</script>

<style scoped>
.mpage {
  margin: 0 auto;
  text-align: center;
}

</style>