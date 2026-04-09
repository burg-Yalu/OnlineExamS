<!--
 * @Description:
 * @Author:
 * @Date: 2024-03-08 20:38:49
-->
<template>
    <div class="index">
        <div  style="font-size: 35px;">
            <span style="color: #000000">您好 : 很高兴遇见你, {{ user.userName }}, 欢迎您登录在线考试系统</span>
        </div>
        <div style="margin-top: 20px; font-size: 24px;" >
            <img
                :src="require('@/assets/bg11.jpg')"
                style="margin-top: 10px; width: 60vw"
            />
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            user: {
                //用户信息
                userName: null,
                userId: null,
            },
        };
    },
    created() {
        this.getUserInfo();
    },
    methods: {
        getUserInfo() {
            //获取用户信息
            let userName = this.$cookies.get("cname");
            let userId = this.$cookies.get("cid");
            this.user.userName = userName;
            this.user.userId = userId;
        },
    },
};
</script>


<style lang="less" scoped>




.index {
    margin-left: 70px;
    .hello {
        font-size: 28px;
        color: #000000;
    }
    .msg {
        .title {
            font-size: 16px;
            color: #000;
            margin-top: 20px;
            margin-left: 10px;
        }
        ul {
            display: flex;
            flex-direction: column;
            width: 200px;
            overflow: hidden;
        }
        li {
            margin-top: 10px;
            font-size: 14px;
            color: lightcoral;
            cursor: pointer;
            display: inline-block;
        }
    }
}
</style>

