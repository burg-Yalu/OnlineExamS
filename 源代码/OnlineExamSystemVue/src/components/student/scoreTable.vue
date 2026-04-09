//显示学生成绩
<template>
  <div class="table">
    <div class="toolbar">
      <el-button type="success" @click="exportExcel" icon="el-icon-download">导出Excel</el-button>
      <el-button type="warning" @click="exportPDF" icon="el-icon-document">导出PDF</el-button>
      <el-button type="info" @click="printData" icon="el-icon-printer">打印</el-button>
    </div>
    <p class="top-title"> 我的分数</p>
    <section class="content-el">
      <el-table ref="filterTable" :data="score" v-loading="loading">
        <el-table-column
          prop="answerDate"
          label="考试日期"
          sortable
          column-key="answerDate"
          :filters="filter"
          :filter-method="filterHandler">
        </el-table-column>
        <el-table-column
          prop="subject"
          label="课程名称"
          width="340"
          filter-placement="bottom-end">
          <template slot-scope="scope">
            <el-tag>{{scope.row.subject}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="etScore" label="考试分数" width="140"></el-table-column>
        <el-table-column label="是否及格" width="140">
          <template slot-scope="scope">
            <el-tag :type="scope.row.ptScore == 1 ? 'success' : 'danger'" style="font-size: 18px;">
              <i class="iconfont icon-r-yes" v-if="scope.row.ptScore == 1" style="font-size: 24px;"></i>
              <i class="iconfont icon-r-no" v-if="scope.row.ptScore != 1" style="font-size: 24px;"></i>
              {{scope.row.ptScore == 1 ? "及格" : "不及格"}}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-row type="flex" justify="center" align="middle" class="pagination">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.current"
          :page-sizes="[4,6,8,10]"
          :page-size="pagination.size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total">
        </el-pagination>
      </el-row>
    </section>
  </div>
</template>

<script>
import { exportToExcel, printPage } from '@/utils/exportExcel'

export default {
  data() {
    return {
      pagination: { //分页后的留言列表
        current: 1, //当前页
        total: null, //记录条数
        size: 10 //每页条数
      },
      loading: false, //加载标识符
      score: [], //学生成绩
      filter: null //过滤参数
    }
  },
  created() {
    this.getScore()
    this.loading = true //数据加载则遮罩表格
  },
  methods: {
    getScore() {
      let studentId = this.$cookies.get("cid")
      this.$axios(`/api/score/${this.pagination.current}/${this.pagination.size}/${studentId}`).then(res => {
        if(res.data.code == 200) {
          this.loading = false //数据加载完成去掉遮罩
          this.score = res.data.data.records
          this.pagination = {...res.data.data}
          let mapVal = this.score.map((element,index) => { //通过map得到 filter:[{text,value}]形式的数组对象
            let newVal = {}
            newVal.text = element.answerDate
            newVal.value = element.answerDate
            return newVal
          })
          let hash = []
          const newArr = mapVal.reduce((item, next) => { //对新对象进行去重操作
            hash[next.text] ? '' : hash[next.text] = true && item.push(next);
            return item
          }, []);
          this.filter = newArr
        }
      })
    },
    //改变当前记录条数
    handleSizeChange(val) {
      this.pagination.size = val
      this.getScore()
    },
    //改变当前页码，重新发送请求
    handleCurrentChange(val) {
      this.pagination.current = val
      this.getScore()
    },
    formatter(row, column) {
      return row.address;
    },
    filterTag(value, row) {
      return row.tag === value;
    },
    filterHandler(value, row, column) {
      const property = column["property"];
      return row[property] === value;
    },
    // 导出Excel
    exportExcel() {
      // 获取全部成绩数据
      let studentId = this.$cookies.get("cid");
      this.$axios(`/api/score/${studentId}`).then(res => {
        if (res.data.code == 200) {
          const data = res.data.data;
          const columns = [
            { prop: 'answerDate', label: '考试日期' },
            { prop: 'subject', label: '课程名称' },
            { prop: 'etScore', label: '考试分数' },
            { prop: 'ptScore', label: '是否及格' }
          ];
          // 转换及格状态
          const formattedData = data.map(item => ({
            ...item,
            ptScore: item.ptScore == 1 ? '及格' : '不及格'
          }));
          exportToExcel(formattedData, columns, '我的成绩');
          this.$message({ message: '导出成功', type: 'success' });
        }
      });
    },
    // 导出PDF（调用后端API）
    exportPDF() {
      let studentId = this.$cookies.get("cid");
      // 直接下载PDF文件
      window.open(`/api/score/pdf/${studentId}`, '_blank');
      this.$message({ message: 'PDF导出成功', type: 'success' });
    },
    // 打印
    printData() {
      printPage('我的成绩');
    }
  }
};
</script>

<style lang="less" scoped>
.pagination {
  padding-top: 20px;
}
.table {
  width: 980px;
  margin: 0 auto;
  .top-title {
    margin: 20px;
  }
  .content-el {
    background-color: #fff;
    padding: 20px;
  }
}
</style>
