<template>
  <div>
    <!-- 顶部导航 -->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item>首页</el-breadcrumb-item>
      <el-breadcrumb-item>考场编排</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 查询条件 -->
    <div style="margin-top: 15px;">
      <el-form :inline="true" :model="queryForm" class="demo-form-inline">
        <el-form-item label="考试编号">
          <el-input v-model="queryForm.examCode" placeholder="请输入考试编号"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchArranges">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="showAutoArrangeDialog">自动编排</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 编排列表 -->
    <el-table :data="arrangeList" border style="width: 100%; margin-top: 20px;">
      <el-table-column prop="arrangeId" label="编排ID" width="80"></el-table-column>
      <el-table-column prop="examCode" label="考试编号" width="100"></el-table-column>
      <el-table-column prop="roomName" label="考场" width="150"></el-table-column>
      <el-table-column prop="arrangeDate" label="考试日期" width="120"></el-table-column>
      <el-table-column prop="session" label="场次" width="80">
        <template slot-scope="scope">
          {{ getSessionText(scope.row.session) }}
        </template>
      </el-table-column>
      <el-table-column prop="startTime" label="开始时间" width="100"></el-table-column>
      <el-table-column prop="endTime" label="结束时间" width="100"></el-table-column>
      <el-table-column prop="totalSeats" label="总座位" width="100"></el-table-column>
      <el-table-column prop="assignedSeats" label="已分配" width="100"></el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="300" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" @click="viewStudents(scope.row)">查看学生</el-button>
          <el-button size="mini" type="primary" @click="showManualArrangeDialog(scope.row)">手动编排</el-button>
          <el-button size="mini" type="danger" @click="cancelArrange(scope.row)" v-if="scope.row.status === '0'">取消</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 自动编排对话框 -->
    <el-dialog title="自动编排考场" :visible.sync="autoDialogVisible" width="50%">
      <el-form :model="autoArrangeForm" :rules="autoRules" ref="autoForm" label-width="120px">
        <el-form-item label="考试编号" prop="examCode">
          <el-input v-model="autoArrangeForm.examCode" type="number"></el-input>
        </el-form-item>
        <el-form-item label="考生人数" prop="studentCount">
          <el-input v-model="autoArrangeForm.studentCount" type="number"></el-input>
        </el-form-item>
        <el-form-item label="考试日期" prop="date">
          <el-date-picker v-model="autoArrangeForm.date" type="date" placeholder="选择日期" format="yyyy-MM-dd" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item label="考试场次" prop="session">
          <el-select v-model="autoArrangeForm.session">
            <el-option label="上午" value="1"></el-option>
            <el-option label="下午" value="2"></el-option>
            <el-option label="晚上" value="3"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="autoDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="autoArrangeExam">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 手动编排对话框 -->
    <el-dialog title="手动编排考场" :visible.sync="manualDialogVisible" width="50%">
      <el-form :model="manualArrangeForm" :rules="manualRules" ref="manualForm" label-width="120px">
        <el-form-item label="考试编号" prop="examCode">
          <el-input v-model="manualArrangeForm.examCode" disabled></el-input>
        </el-form-item>
        <el-form-item label="选择考场" prop="roomId">
          <el-select v-model="manualArrangeForm.roomId" placeholder="请选择考场" filterable>
            <el-option
              v-for="room in availableRooms"
              :key="room.roomId"
              :label="`${room.roomName} (${room.roomCapacity}座)`"
              :value="room.roomId">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="考试日期" prop="date">
          <el-date-picker v-model="manualArrangeForm.date" type="date" placeholder="选择日期" format="yyyy-MM-dd" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item label="考试场次" prop="session">
          <el-select v-model="manualArrangeForm.session">
            <el-option label="上午" value="1"></el-option>
            <el-option label="下午" value="2"></el-option>
            <el-option label="晚上" value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="座位数量" prop="totalSeats">
          <el-input-number v-model="manualArrangeForm.totalSeats" :min="1" :max="100"></el-input-number>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="manualDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="manualArrangeExam">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 查看学生对话框 -->
    <el-dialog title="考生列表" :visible.sync="studentDialogVisible" width="70%">
      <el-table :data="studentList" border>
        <el-table-column prop="studentId" label="学号" width="100"></el-table-column>
        <el-table-column prop="studentName" label="姓名" width="120"></el-table-column>
        <el-table-column prop="seatNumber" label="座位号" width="100"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStudentStatusType(scope.row.status)">
              {{ getStudentStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      arrangeList: [],
      queryForm: {
        examCode: ''
      },
      autoDialogVisible: false,
      manualDialogVisible: false,
      studentDialogVisible: false,
      autoArrangeForm: {
        examCode: '',
        studentCount: '',
        date: '',
        session: '1'
      },
      manualArrangeForm: {
        examCode: '',
        roomId: '',
        date: '',
        session: '1',
        totalSeats: 30
      },
      autoRules: {
        examCode: [{ required: true, message: '请输入考试编号', trigger: 'blur' }],
        studentCount: [{ required: true, message: '请输入考生人数', trigger: 'blur' }],
        date: [{ required: true, message: '请选择考试日期', trigger: 'change' }],
        session: [{ required: true, message: '请选择考试场次', trigger: 'change' }]
      },
      manualRules: {
        examCode: [{ required: true, message: '请输入考试编号', trigger: 'blur' }],
        roomId: [{ required: true, message: '请选择考场', trigger: 'change' }],
        date: [{ required: true, message: '请选择考试日期', trigger: 'change' }],
        session: [{ required: true, message: '请选择考试场次', trigger: 'change' }],
        totalSeats: [{ required: true, message: '请输入座位数量', trigger: 'blur' }]
      },
      availableRooms: [],
      studentList: [],
      currentArrange: {}
    }
  },
  created() {
    this.loadArrangeList()
  },
  methods: {
    loadArrangeList() {
      this.$axios.get('/api/arrange/list').then(response => {
        this.arrangeList = response.data.data
      })
    },
    searchArranges() {
      this.$axios.get('/api/arrange/list', {
        params: this.queryForm
      }).then(response => {
        this.arrangeList = response.data.data
      })
    },
    getSessionText(session) {
      const sessionMap = {
        '1': '上午',
        '2': '下午',
        '3': '晚上'
      }
      return sessionMap[session] || session
    },
    getStatusText(status) {
      const statusMap = {
        '0': '待分配',
        '1': '已分配',
        '2': '进行中',
        '3': '已结束'
      }
      return statusMap[status] || '未知'
    },
    getStatusType(status) {
      const typeMap = {
        '0': 'info',
        '1': 'success',
        '2': 'warning',
        '3': 'danger'
      }
      return typeMap[status] || 'info'
    },
    showAutoArrangeDialog() {
      this.autoArrangeForm = {
        examCode: '',
        studentCount: '',
        date: '',
        session: '1'
      }
      this.autoDialogVisible = true
    },
    autoArrangeExam() {
      this.$refs.autoForm.validate((valid) => {
        if (valid) {
          this.$axios.post('/api/arrange/auto-arrange', {
            examCode: this.autoArrangeForm.examCode,
            studentCount: this.autoArrangeForm.studentCount,
            date: this.autoArrangeForm.date,
            session: this.autoArrangeForm.session
          }).then(response => {
            if (response.data.code === 200) {
              this.$message.success('自动编排成功')
              this.autoDialogVisible = false
              this.loadArrangeList()
            } else {
              this.$message.error(response.data.message)
            }
          })
        }
      })
    },
    showManualArrangeDialog(row) {
      this.currentArrange = row
      this.manualArrangeForm = {
        examCode: row.examCode,
        roomId: '',
        date: '',
        session: '1',
        totalSeats: 30
      }
      this.loadAvailableRooms()
      this.manualDialogVisible = true
    },
    loadAvailableRooms() {
      const date = this.manualArrangeForm.date
      const session = this.manualArrangeForm.session
      if (date && session) {
        this.$axios.get('/api/room/available', {
          params: {
            capacity: this.manualArrangeForm.totalSeats,
            date: date,
            session: session
          }
        }).then(response => {
          this.availableRooms = response.data.data
        })
      }
    },
    manualArrangeExam() {
      this.$refs.manualForm.validate((valid) => {
        if (valid) {
          this.$axios.post('/api/arrange/manual-arrange', {
            examCode: this.manualArrangeForm.examCode,
            roomId: this.manualArrangeForm.roomId,
            date: this.manualArrangeForm.date,
            session: this.manualArrangeForm.session,
            totalSeats: this.manualArrangeForm.totalSeats
          }).then(response => {
            if (response.data.code === 200) {
              this.$message.success('手动编排成功')
              this.manualDialogVisible = false
              this.loadArrangeList()
            } else {
              this.$message.error(response.data.message)
            }
          })
        }
      })
    },
    viewStudents(row) {
      this.currentArrange = row
      this.$axios.get(`/api/arrange/assigned-students/${row.examCode}`).then(response => {
        this.studentList = response.data.data
        this.studentDialogVisible = true
      })
    },
    cancelArrange(row) {
      this.$confirm('确认取消该编排吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios.post(`/api/arrange/cancel/${row.arrangeId}`).then(response => {
          if (response.data.code === 200) {
            this.$message.success('取消成功')
            this.loadArrangeList()
          } else {
            this.$message.error(response.data.message)
          }
        })
      })
    },
    getStudentStatusText(status) {
      const statusMap = {
        '0': '待考试',
        '1': '考试中',
        '2': '已交卷',
        '3': '缺考'
      }
      return statusMap[status] || '未知'
    },
    getStudentStatusType(status) {
      const typeMap = {
        '0': 'info',
        '1': 'warning',
        '2': 'success',
        '3': 'danger'
      }
      return typeMap[status] || 'info'
    }
  }
}
</script>