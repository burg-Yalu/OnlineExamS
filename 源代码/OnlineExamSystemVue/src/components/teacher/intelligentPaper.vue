<template>
  <div>
    <!-- 顶部导航 -->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item>首页</el-breadcrumb-item>
      <el-breadcrumb-item>智能组卷</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 组卷参数设置 -->
    <el-card class="box-card" style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>智能组卷设置</span>
      </div>

      <el-form :inline="true" :model="paperForm" class="demo-form-inline">
        <el-form-item label="考试科目">
          <el-select v-model="paperForm.subject" placeholder="请选择科目" @change="loadQuestionStats">
            <el-option label="计算机网络" value="计算机网络"></el-option>
            <el-option label="数据库理论" value="数据库理论"></el-option>
            <el-option label="数据结构" value="数据结构"></el-option>
            <el-option label="Java程序设计" value="Java程序设计"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="总分">
          <el-input-number v-model="paperForm.totalScore" :min="10" :max="200" @change="calculateScore"></el-input-number>
        </el-form-item>

        <el-form-item label="组卷策略">
          <el-select v-model="paperForm.strategy">
            <el-option label="均衡组卷" value="balanced"></el-option>
            <el-option label="知识点组卷" value="knowledge"></el-option>
            <el-option label="难度组卷" value="difficulty"></el-option>
            <el-option label="随机组卷" value="random"></el-option>
          </el-select>
        </el-form-item>
      </el-form>

      <!-- 题型设置 -->
      <div class="question-type-settings" v-if="paperForm.strategy !== 'difficulty'">
        <h3>题型设置</h3>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="type-card">
              <div slot="header">
                <span>选择题</span>
                <el-tag size="small" type="primary">每题2分</el-tag>
              </div>
              <el-slider v-model="paperForm.questionTypes.multi" :min="0" :max="50" @change="calculateScore"></el-slider>
              <div class="score-display">总分: {{ getMultiScore }}分</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="type-card">
              <div slot="header">
                <span>判断题</span>
                <el-tag size="small" type="success">每题2分</el-tag>
              </div>
              <el-slider v-model="paperForm.questionTypes.judge" :min="0" :max="30" @change="calculateScore"></el-slider>
              <div class="score-display">总分: {{ getJudgeScore }}分</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="type-card">
              <div slot="header">
                <span>填空题</span>
                <el-tag size="small" type="warning">每题2分</el-tag>
              </div>
              <el-slider v-model="paperForm.questionTypes.fill" :min="0" :max="30" @change="calculateScore"></el-slider>
              <div class="score-display">总分: {{ getFillScore }}分</div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 知识点设置 -->
      <div class="knowledge-settings" v-if="paperForm.strategy === 'knowledge'">
        <h3>知识点设置</h3>
        <el-select v-model="paperForm.knowledgePoints" multiple placeholder="请选择知识点">
          <el-option v-for="point in knowledgePoints"
                     :key="point"
                     :label="point"
                     :value="point">
          </el-option>
        </el-select>
      </div>

      <!-- 难度设置 -->
      <div class="difficulty-settings" v-if="paperForm.strategy === 'difficulty'">
        <h3>难度设置</h3>
        <el-row>
          <el-col :span="8">
            <el-card>
              <div slot="header">
                <span>简单题</span>
                <el-tag size="small" type="success">20%</el-tag>
              </div>
              <el-input-number v-model="paperForm.difficulty.easy" :min="0" :max="100"></el-input-number>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card>
              <div slot="header">
                <span>中等题</span>
                <el-tag size="small" type="warning">50%</el-tag>
              </div>
              <el-input-number v-model="paperForm.difficulty.medium" :min="0" :max="100"></el-input-number>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card>
              <div slot="header">
                <span>困难题</span>
                <el-tag size="small" type="danger">30%</el-tag>
              </div>
              <el-input-number v-model="paperForm.difficulty.hard" :min="0" :max="100"></el-input-number>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <div style="margin-top: 20px; text-align: center;">
        <el-button type="primary" size="large" @click="generatePaper" :disabled="!canGenerate">
          开始智能组卷
        </el-button>
        <el-button size="large" @click="resetForm">重置</el-button>
      </div>
    </el-card>

    <!-- 组卷结果 -->
    <el-card class="box-card" style="margin-top: 20px;" v-if="paperResult">
      <div slot="header" class="clearfix">
        <span>组卷结果</span>
        <el-button style="float: right" type="text" @click="previewPaper">预览试卷</el-button>
      </div>

      <el-row :gutter="20">
        <el-col :span="12">
          <h3>试卷信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="考试科目">{{ paperForm.subject }}</el-descriptions-item>
            <el-descriptions-item label="总分">{{ actualScore }}分</el-descriptions-item>
            <el-descriptions-item label="选择题数量">{{ paperForm.questionTypes.multi }}题</el-descriptions-item>
            <el-descriptions-item label="判断题数量">{{ paperForm.questionTypes.judge }}题</el-descriptions-item>
            <el-descriptions-item label="填空题数量">{{ paperForm.questionTypes.fill }}题</el-descriptions-item>
            <el-descriptions-item label="预估用时">{{ estimatedTime }}分钟</el-descriptions-item>
          </el-descriptions>
        </el-col>
        <el-col :span="12">
          <h3>题目统计</h3>
          <div ref="chart" style="width: 100%; height: 200px;"></div>
        </el-col>
      </el-row>

      <div style="margin-top: 20px; text-align: center;">
        <el-button type="success" @click="savePaper">保存试卷</el-button>
        <el-button @click="regeneratePaper">重新组卷</el-button>
        <el-button type="danger" @click="clearResult">清空结果</el-button>
      </div>
    </el-card>

    <!-- 题目预览对话框 -->
    <el-dialog title="试卷预览" :visible.sync="previewDialogVisible" width="80%">
      <div v-for="(question, index) in previewQuestions" :key="index" style="margin-bottom: 20px;">
        <h3>第{{ index + 1 }}题 {{ getQuestionTypeName(question.questionType) }}</h3>
        <div v-html="question.question"></div>

        <div v-if="question.questionType === 'multi'">
          <el-radio-group v-model="question.userAnswer">
            <el-radio label="A">A. {{ question.optionA }}</el-radio>
            <el-radio label="B">B. {{ question.optionB }}</el-radio>
            <el-radio label="C">C. {{ question.optionC }}</el-radio>
            <el-radio label="D">D. {{ question.optionD }}</el-radio>
          </el-radio-group>
        </div>

        <div v-if="question.questionType === 'judge'">
          <el-radio-group v-model="question.userAnswer">
            <el-radio label="A">正确</el-radio>
            <el-radio label="B">错误</el-radio>
          </el-radio-group>
        </div>

        <div v-if="question.questionType === 'fill'">
          <el-input v-model="question.userAnswer" placeholder="请填写答案"></el-input>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      paperForm: {
        subject: '',
        totalScore: 100,
        strategy: 'balanced',
        questionTypes: {
          multi: 20,
          judge: 15,
          fill: 15
        },
        knowledgePoints: [],
        difficulty: {
          easy: 20,
          medium: 50,
          hard: 30
        }
      },
      paperResult: null,
      previewDialogVisible: false,
      previewQuestions: [],
      questionStats: null,
      knowledgePoints: ['第一章 网络基础', '第二章 TCP/IP', '第三章 应用层', '第四章 网络安全', '第五章 网络管理']
    }
  },
  computed: {
    getMultiScore() {
      return this.paperForm.questionTypes.multi * 2
    },
    getJudgeScore() {
      return this.paperForm.questionTypes.judge * 2
    },
    getFillScore() {
      return this.paperForm.questionTypes.fill * 2
    },
    actualScore() {
      return this.getMultiScore + this.getJudgeScore + this.getFillScore
    },
    estimatedTime() {
      return Math.ceil(this.actualScore / 20) // 每分钟20分
    },
    canGenerate() {
      return this.paperForm.subject && this.actualScore > 0
    }
  },
  mounted() {
    this.initChart()
  },
  methods: {
    loadQuestionStats() {
      this.$axios.get('/api/intelligent-paper/question-stats', {
        params: {
          subject: this.paperForm.subject
        }
      }).then(response => {
        this.questionStats = response.data.data
        this.initChart()
      })
    },
    calculateScore() {
      // 分数计算逻辑已在computed中处理
    },
    generatePaper() {
      const params = {
        examCode: 20240001, // 应该从考试信息获取
        totalScore: this.paperForm.totalScore,
        strategy: this.paperForm.strategy
      }

      if (this.paperForm.strategy === 'knowledge') {
        params.questionTypeCount = this.paperForm.questionTypes
        params.knowledgePoints = this.paperForm.knowledgePoints
      } else if (this.paperForm.strategy === 'difficulty') {
        // 转换为题目数量
        const totalCount = Math.floor(this.paperForm.totalScore / 2)
        params.difficultyCount = {
          easy: Math.floor(totalCount * 0.2),
          medium: Math.floor(totalCount * 0.5),
          hard: Math.floor(totalCount * 0.3)
        }
      } else {
        params.questionTypeCount = this.paperForm.questionTypes
      }

      this.$axios.post('/api/intelligent-paper/generate', params).then(response => {
        this.paperResult = response.data.data
        this.$message.success('组卷成功！')
        this.initChart()
      })
    },
    regeneratePaper() {
      this.generatePaper()
    },
    savePaper() {
      this.$axios.post('/api/intelligent-paper/save', {
        examCode: 20240001,
        paperQuestions: this.paperResult.paperQuestions
      }).then(response => {
        if (response.data.code === 200) {
          this.$message.success('试卷保存成功！')
          this.clearResult()
        } else {
          this.$message.error(response.data.message)
        }
      })
    },
    previewPaper() {
      this.previewQuestions = this.paperResult.paperQuestions
      this.previewDialogVisible = true
    },
    clearResult() {
      this.paperResult = null
      this.previewQuestions = []
    },
    resetForm() {
      this.paperForm = {
        subject: '',
        totalScore: 100,
        strategy: 'balanced',
        questionTypes: {
          multi: 20,
          judge: 15,
          fill: 15
        },
        knowledgePoints: [],
        difficulty: {
          easy: 20,
          medium: 50,
          hard: 30
        }
      }
      this.clearResult()
    },
    getQuestionTypeName(type) {
      const typeMap = {
        'multi': '选择题',
        'judge': '判断题',
        'fill': '填空题'
      }
      return typeMap[type] || type
    },
    initChart() {
      // 这里可以使用ECharts绘制题目分布图
      // this.$nextTick(() => {
      //   const chart = this.$echarts.init(this.$refs.chart)
      //   // 配置图表选项
      // })
    }
  }
}
</script>

<style scoped>
.question-type-settings,
.knowledge-settings,
.difficulty-settings {
  margin-top: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.type-card {
  height: 200px;
}

.score-display {
  text-align: center;
  margin-top: 10px;
  font-weight: bold;
  color: #409eff;
}

h3 {
  margin-bottom: 15px;
  color: #303133;
}

.el-card {
  border-radius: 8px;
}
</style>