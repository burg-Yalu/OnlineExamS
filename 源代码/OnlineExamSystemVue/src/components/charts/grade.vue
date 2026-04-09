// 成绩统计页面
<template>
  <div id="grade">
    <div class="toolbar">
      <el-button type="success" @click="exportExcel" icon="el-icon-download">导出Excel</el-button>
      <el-button type="warning" @click="exportPDF" icon="el-icon-document">导出PDF</el-button>
      <el-button type="info" @click="exportChartImage" icon="el-icon-picture">导出图表</el-button>
    </div>
    <div ref="box" class="box"></div>
    <div class="score-table" v-if="!isNull">
      <h3>成绩明细</h3>
      <el-table :data="scoreList" border style="width: 100%">
        <el-table-column prop="answerDate" label="考试日期" width="120"></el-table-column>
        <el-table-column prop="subject" label="课程名称" width="180"></el-table-column>
        <el-table-column prop="etScore" label="考试成绩" width="100"></el-table-column>
        <el-table-column prop="score" label="试卷满分" width="100"></el-table-column>
        <el-table-column label="是否及格" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.ptScore == 1 ? 'success' : 'danger'">
              {{ scope.row.ptScore == 1 ? '及格' : '不及格' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="notFound" v-if="isNull">
      <i class="iconfont icon-LC_icon_tips_fill"></i>
      <span>该考生未参加考试</span>
    </div>
  </div>
</template>

<script>
import { exportToExcel } from '@/utils/exportExcel'

export default {
  name: "grade",
  data() {
    return {
      isNull: false,
      tableDataX: [],
      tableDataY: [],
      chartInstance: null,
      scoreList: [], // 成绩列表
      studentId: null
    };
  },
  mounted() {
    this.studentId = this.$route.query.studentId;
    this.score();
  },
  methods: {
    score() {
      this.$axios(`/api/score/${this.studentId}`).then((res) => {
        if (res.data.code == 200) {
          let rootData = res.data.data;
          this.scoreList = rootData;

          rootData.forEach((element, index) => {
            var insertIntervalString = (
              originStr,
              disNum = 10,
              insertStr = "\n"
            ) =>
              originStr.replace(
                new RegExp("(.{" + disNum + "})", "g"),
                "$1" + insertStr
              );
            var subject = insertIntervalString(element.subject, 3, "\n");
            this.tableDataX.push(subject);
            this.tableDataY.push(element.etScore);
          });

          let boxDom = this.$refs["box"];
          let scoreCharts = this.$echarts.init(boxDom);
          this.chartInstance = scoreCharts;
          let option = {
            title: {
              text: '学生成绩统计',
              left: 'center'
            },
            xAxis: {
              type: "category",
              data: this.tableDataX,
              axisLabel: {
                interval: 0,
                rotate: 30
              }
            },
            yAxis: {
              type: "value",
              name: '分数'
            },
            series: [
              {
                data: this.tableDataY,
                type: "bar",
                itemStyle: {
                  normal: {
                    label: { show: true, position: 'top' },
                    color: function(params) {
                      var colorList = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de'];
                      return colorList[params.dataIndex % colorList.length];
                    }
                  }
                },
              },
            ],
          };
          scoreCharts.setOption(option);
        } else {
          this.isNull = true;
        }
      });
    },
    // 导出图表为图片
    exportChartImage() {
      if (this.chartInstance) {
        const url = this.chartInstance.getDataURL({
          type: 'png',
          pixelRatio: 2,
          backgroundColor: '#fff'
        });
        const a = document.createElement('a');
        a.href = url;
        a.download = '成绩统计图表.png';
        a.click();
        this.$message({ message: '图表图片导出成功', type: 'success' });
      } else {
        this.$message({ message: '暂无图表数据', type: 'warning' });
      }
    },
    // 导出Excel
    exportExcel() {
      if (this.scoreList.length === 0) {
        this.$message({ message: '暂无成绩数据', type: 'warning' });
        return;
      }
      const columns = [
        { prop: 'answerDate', label: '考试日期' },
        { prop: 'subject', label: '课程名称' },
        { prop: 'etScore', label: '考试成绩' },
        { prop: 'score', label: '试卷满分' },
        { prop: 'ptScoreText', label: '是否及格' }
      ];
      // 添加文字描述
      const data = this.scoreList.map(item => ({
        ...item,
        ptScoreText: item.ptScore == 1 ? '及格' : '不及格'
      }));
      exportToExcel(data, columns, '学生成绩表');
      this.$message({ message: 'Excel导出成功', type: 'success' });
    },
    // 导出PDF
    exportPDF() {
      if (this.scoreList.length === 0) {
        this.$message({ message: '暂无成绩数据', type: 'warning' });
        return;
      }
      // 直接访问后端API
      window.open(`http://localhost:9201/score/pdf/${this.studentId}`, '_blank');
      this.$message({ message: 'PDF导出成功', type: 'success' });
    }
  },
};
</script>

<style lang="less" scoped>
#grade {
  position: relative;
  padding: 20px;
  .toolbar {
    padding: 10px 0;
    margin-bottom: 20px;
  }
  .box {
    width: 700px;
    height: 400px;
    margin-bottom: 30px;
  }
  .score-table {
    h3 {
      margin-bottom: 15px;
      color: #333;
    }
  }
  .notFound {
    position: absolute;
    top: 0px;
    left: 0px;
  }
}
</style>