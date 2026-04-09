<template>
  <div class="container">
    <div style="width: 25%; height: 45%; display: flex;background-color: #ffffff">
      <div style="flex: 1;width: 50%; padding: 40px;display: flex;flex-direction: column;justify-content: center;" >
        <div style="text-align: center; font-size: 30px; margin-bottom: 40px; color: #000000;font-size: 30px">在线考试系统</div>

        <el-form :label-position="labelPosition"  :model="formLabelAlign">
        <el-form-item >
          <el-input v-model.number="formLabelAlign.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item >
          <el-input v-model="formLabelAlign.password" placeholder="请输入密码"
                    type="password"></el-input>
        </el-form-item>
        <div >
          <el-button type="primary" style="width: 100%;margin-top: 30px" @click="login()"> 登录</el-button>
        </div>
      </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
export default {
    name: "login",
    data() {
        return {
            role: 2,
            labelPosition: "left",
            formLabelAlign: {
                username: "",
                password: "",
            },
        };
    },
    methods: {
        //用户登录请求后台处理
        login() {
            if (
                this.formLabelAlign.username == undefined ||
                this.formLabelAlign.username == ""
            ) {
                this.$message("请输入用户名");
                return;
            }
            if (
                !/^\d+$/.test(this.formLabelAlign.username) ||
                this.formLabelAlign.username.toString().length > 10
            ) {
                this.$message("用户名有误");
                return;
            }
            if (this.formLabelAlign.password == "") {
                this.$message("请输入密码");
                return;
            }
            this.$axios({
                url: `/api/login`,
                method: "post",
                data: {
                    ...this.formLabelAlign,
                },
            })
                .then((res) => {
                    let resData = res.data.data;
                    if (resData != null) {
                        switch (resData.role) {
                            case "0": //管理员
                                this.$cookies.set("cname", resData.adminName);
                                this.$cookies.set("cid", resData.adminId);
                                this.$cookies.set("role", 0);
                                this.$router.push({ path: "/index" }); //跳转到首页
                                break;
                            case "1": //教师
                                this.$cookies.set("cname", resData.teacherName);
                                this.$cookies.set("cid", resData.teacherId);
                                this.$cookies.set("role", 1);
                                this.$router.push({ path: "/index" }); //跳转到教师用户
                                break;
                            case "2": //学生
                                this.$cookies.set("cname", resData.studentName);
                                this.$cookies.set("cid", resData.studentId);
                                this.$router.push({ path: "/student" });
                                break;
                        }
                    }
                    if (resData == null) {
                        //错误提示
                        this.$message({
                            showClose: true,
                            type: "error",
                            message: "用户名或者密码错误",
                        });
                    }
                })
                .catch((e) => {
                    console.log(e);
                    if (
                        e.response == undefined ||
                        e.response.data == undefined
                    ) {
                        this.$message({
                            showClose: true,
                            message: e,
                            type: "error",
                            duration: 5000,
                        });
                    } else {
                        this.$message({
                            showClose: true,
                            message: e.response.data,
                            type: "error",
                            duration: 5000,
                        });
                    }
                });
        },
        clickTag(key) {
            this.role = key;
        },
    },
    computed: mapState(["userInfo"]),
    mounted() { },
};
</script>

<style lang="less" scoped>

.container {
  height: 100vh;
  overflow: hidden;
  //background-color: #0195ff;
  background-image: url("../../assets/bg6.jpg");
  background-size: 100% 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}


.remind {
    border-radius: 4px;
    padding: 10px 20px;
    display: flex;
    position: fixed;
    right: 20px;
    bottom: 50%;
    flex-direction: column;
    color: #606266;
    background-color: #fff;
    border-left: 4px solid #409eff;
    box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19);
}

//.container {
//    margin-bottom: 32px;
//}

.title-icon {
    font-size: 44px;
    margin-right: 10px;
}

.container .el-radio-group {
    margin: 30px 0px;
}

a:link {
    color: #ff962a;
    text-decoration: none;
}

#login {
    font-size: 14px;
    color: #000;
    background-color: #fff;
}

#login .bg {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    overflow-y: auto;
    height: 100%;
    background: url("../../assets/img/77771.jpg") center top / cover no-repeat;
    background-color: #b6bccdd1 !important;
}

#login .main-container {
    display: flex;
    justify-content: center;
    align-items: center;
}

#login .main-container .top {
    margin-top: 100px;
    font-size: 30px;
    color: #ff962a;
    display: flex;
    justify-content: center;
}

#login .top .icon-kaoshi {
    font-size: 80px;
}

#login .top .title {
    margin-top: 20px;
}

#login .bottom {
    display: flex;
    justify-content: center;
    background-color: #fff;
    border-radius: 5px;
    box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19);
}

#login .bottom .title {
    text-align: center;
    font-size: 30px;
}

.bottom .container .title {
    margin: 30px 0px;
}

.bottom .submit .row-login {
    width: 100%;
    margin: 20px 0px 10px 0px;
    padding: 15px 20px;
    background-color: rgb(133, 174, 191);
    border-color: rgb(133, 174, 191);
    color: white
}

.bottom .submit {
    display: flex;
    justify-content: center;
}

.footer {
    margin-top: 50px;
    text-align: center;
}

.footer .msg1 {
    font-size: 18px;
    color: #fff;
    margin-bottom: 15px;
}

.footer .msg2 {
    font-size: 14px;
    color: #e3e3e3;
    margin-top: 70px;
}

.bottom .options {
    margin-bottom: 40px;
    color: #ff962a;
    display: flex;
    justify-content: space-between;
}

.bottom .options>a {
    color: #ff962a;
}

.bottom .options .register span:nth-child(1) {
    color: #8c8c8c;
}
</style>
