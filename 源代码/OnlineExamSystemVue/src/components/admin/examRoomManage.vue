<template>
  <div>
    <!-- 顶部导航 -->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item>首页</el-breadcrumb-item>
      <el-breadcrumb-item>考场管理</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 工具栏 -->
    <div style="margin-top: 15px;">
      <el-button type="primary" @click="showAddRoomDialog">新增考场</el-button>
      <el-button type="success" @click="showGenerateSeatsDialog">批量生成座位</el-button>
    </div>

    <!-- 考场列表 -->
    <el-table :data="roomList" border style="width: 100%; margin-top: 20px;">
      <el-table-column prop="roomId" label="考场ID" width="80"></el-table-column>
      <el-table-column prop="roomName" label="考场名称" width="150"></el-table-column>
      <el-table-column prop="roomLocation" label="位置" width="200"></el-table-column>
      <el-table-column prop="roomCapacity" label="容量" width="80"></el-table-column>
      <el-table-column prop="availableSeats" label="可用座位" width="100"></el-table-column>
      <el-table-column prop="equipment" label="设备" width="150"></el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : scope.row.status === '1' ? 'warning' : 'danger'">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述"></el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" @click="editRoom(scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="deleteRoom(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑考场对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="50%">
      <el-form :model="currentRoom" :rules="rules" ref="roomForm" label-width="100px">
        <el-form-item label="考场名称" prop="roomName">
          <el-input v-model="currentRoom.roomName"></el-input>
        </el-form-item>
        <el-form-item label="考场位置" prop="roomLocation">
          <el-input v-model="currentRoom.roomLocation"></el-input>
        </el-form-item>
        <el-form-item label="考场容量" prop="roomCapacity">
          <el-input-number v-model="currentRoom.roomCapacity" :min="1"></el-input-number>
        </el-form-item>
        <el-form-item label="考场设备">
          <el-input v-model="currentRoom.equipment"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="currentRoom.status">
            <el-option label="可用" value="0"></el-option>
            <el-option label="使用中" value="1"></el-option>
            <el-option label="维护中" value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input type="textarea" v-model="currentRoom.description"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveRoom">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 生成座位对话框 -->
    <el-dialog title="生成座位" :visible.sync="seatDialogVisible" width="40%">
      <el-form :model="seatForm" label-width="120px">
        <el-form-item label="考场">
          <el-select v-model="seatForm.roomId" disabled>
            <el-option :label="selectedRoom.roomName" :value="selectedRoom.roomId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排数">
          <el-input-number v-model="seatForm.rows" :min="1" :max="20"></el-input-number>
        </el-form-item>
        <el-form-item label="每排列数">
          <el-input-number v-model="seatForm.cols" :min="1" :max="30"></el-input-number>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="seatDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="generateSeats">生 成</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      roomList: [],
      dialogVisible: false,
      seatDialogVisible: false,
      dialogTitle: '',
      currentRoom: {
        roomId: null,
        roomName: '',
        roomLocation: '',
        roomCapacity: 30,
        availableSeats: 0,
        equipment: '',
        status: '0',
        description: ''
      },
      selectedRoom: {},
      seatForm: {
        roomId: null,
        rows: 5,
        cols: 6
      },
      rules: {
        roomName: [{ required: true, message: '请输入考场名称', trigger: 'blur' }],
        roomLocation: [{ required: true, message: '请输入考场位置', trigger: 'blur' }],
        roomCapacity: [{ required: true, message: '请输入考场容量', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.loadRoomList()
  },
  methods: {
    loadRoomList() {
      this.$axios.get('/api/room/list').then(response => {
        this.roomList = response.data.data
      })
    },
    getStatusText(status) {
      const statusMap = {
        '0': '可用',
        '1': '使用中',
        '2': '维护中'
      }
      return statusMap[status] || '未知'
    },
    showAddRoomDialog() {
      this.dialogTitle = '新增考场'
      this.currentRoom = {
        roomId: null,
        roomName: '',
        roomLocation: '',
        roomCapacity: 30,
        availableSeats: 0,
        equipment: '',
        status: '0',
        description: ''
      }
      this.dialogVisible = true
    },
    editRoom(row) {
      this.dialogTitle = '编辑考场'
      this.currentRoom = { ...row }
      this.dialogVisible = true
    },
    saveRoom() {
      this.$refs.roomForm.validate((valid) => {
        if (valid) {
          const url = this.currentRoom.roomId ? '/api/room/update' : '/api/room/create'
          this.$axios.post(url, this.currentRoom).then(response => {
            if (response.data.code === 200) {
              this.$message.success('保存成功')
              this.dialogVisible = false
              this.loadRoomList()
            } else {
              this.$message.error(response.data.message)
            }
          })
        }
      })
    },
    deleteRoom(row) {
      this.$confirm('确认删除该考场吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios.delete(`/api/room/${row.roomId}`).then(response => {
          if (response.data.code === 200) {
            this.$message.success('删除成功')
            this.loadRoomList()
          } else {
            this.$message.error(response.data.message)
          }
        })
      })
    },
    showGenerateSeatsDialog() {
      if (!this.selectedRoom.roomId) {
        this.$message.warning('请先选择一个考场')
        return
      }
      this.seatForm = {
        roomId: this.selectedRoom.roomId,
        rows: 5,
        cols: 6
      }
      this.seatDialogVisible = true
    },
    generateSeats() {
      this.$axios.post(`/api/room/${this.seatForm.roomId}/seats?rows=${this.seatForm.rows}&cols=${this.seatForm.cols}`).then(response => {
        if (response.data.code === 200) {
          this.$message.success('座位生成成功')
          this.seatDialogVisible = false
          this.loadRoomList()
        } else {
          this.$message.error(response.data.message)
        }
      })
    }
  }
}
</script>