<!--
 * @Description:
 * @Author:
 * @Date: 2024-03-08 20:38:49
-->
<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'App',
  created() {
    var token = this.$cookies.get("rb_token");
    var role = this.$cookies.get("rb_role");
    if(token == null || token == "" || role == null || role == null) {
      this.$router.push({path: '/' }) //跳转到首页
    }
  }
}
</script>

<style>

.icon-r-yes {
  display: none;
}

  ul {
  list-style: none;
}
a {
  text-decoration: none;
}
* {
  margin: 0;
  padding: 0;
}
#app {
  font-family: "Microsoft YaHei", "Helvetica", "Tahoma", "Geneva", "Arial", sans-serif;
  background-color: #eee;
}

/* 打印样式 */
@media print {
  /* 隐藏不需要打印的元素 */
  .topbar,
  .left_side,
  .toolbar,
  .pagination,
  .el-pagination,
  .el-button,
  .el-form,
  .el-dialog,
  .el-dialog__wrapper {
    display: none !important;
  }

  /* 打印时的表格样式 */
  .el-table {
    border: 1px solid #000 !important;
  }

  .el-table th,
  .el-table td {
    border: 1px solid #000 !important;
    color: #000 !important;
  }

  .el-table__body-wrapper {
    overflow: visible !important;
  }

  /* 确保表格宽度适应纸张 */
  .el-table__body {
    width: 100% !important;
  }

  /* 调整容器样式 */
  .table,
  .exam,
  .all {
    padding: 0 !important;
    margin: 0 !important;
  }

  /* 隐藏滚动条 */
  ::-webkit-scrollbar {
    display: none !important;
  }

  /* 打印时背景色 */
  body {
    background-color: #fff !important;
  }

  #app {
    background-color: #fff !important;
  }
}
</style>
