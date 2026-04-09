<template>
    <div class="score">
        <br />
        <div class="total">
            <div class="look">
                <i class="el-icon-a-061" style="font-size: 32px;"> 本次考试成绩 </i>
            </div>
            <div class="toolbar">
                <el-button type="success" @click="exportReportPDF" icon="el-icon-document">导出成绩单PDF</el-button>
            </div>
            <div class="show">
                <div class="number" :class="{ border: isTransition }">
                    <span>{{ score }}</span>
                    <span>分数</span>
                </div>
            </div>
            <ul class="time">
                <li class="start">
                    <span>开始时间</span> <span>{{ startTime }}</span>
                </li>
                <li class="end">
                    <span>结束时间</span> <span>{{ endTime }}</span>
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            isTransition: false, //是否渲染完成
            score: 0, //总分
            startTime: null, //考试开始时间
            endTime: null, //考试结束时间
            subject: null, //考试科目
        };
    },
    created() {
        this.transiton();
        this.getScore();
    },
    methods: {
        transiton() {
            //一秒后过渡
            setTimeout(() => {
                this.isTransition = true;
            }, 1000);
        },
        getScore() {
            let score = this.$route.query.score;
            let startTime = this.$route.query.startTime;
            let endTime = this.$route.query.endTime;
            let subject = this.$route.query.subject;
            let studentName = this.$cookies.get("cname");
            let studentId = this.$cookies.get("cid");
            this.score = score;
            this.startTime = startTime;
            this.endTime = endTime;
            this.subject = subject;
            this.studentName = studentName;
            this.studentId = studentId;
        },
        // 导出成绩单PDF（调用后端API）
        exportReportPDF() {
            const params = new URLSearchParams({
                studentId: this.studentId || '',
                score: this.score || 0,
                startTime: this.startTime || '',
                endTime: this.endTime || '',
                subject: this.subject || '考试',
                studentName: this.studentName || ''
            });
            window.open(`/api/score/report/pdf?${params.toString()}`, '_blank');
            this.$message({ message: '成绩单PDF导出成功', type: 'success' });
        },
    },
};
</script>

<style lang="less" scoped>
.show {
    display: flex;
    justify-content: center;
    align-items: center;
    img {
        width: 160px;
        height: 160px;
    }
    .img1Transform {
        opacity: 1 !important;
        transform: translateX(30px) !important;
        transition: all 0.6s ease !important;
    }
    .img2Transform {
        opacity: 1 !important;
        transform: translateX(-30px) !important;
        transition: all 0.6s ease !important;
    }
    .img1 {
        margin-top: 70px;
        opacity: 0;
        transform: translateX(0px);
        transition: all 0.6s ease;
    }
    .img2 {
        margin-top: 30px;
        opacity: 0;
        transform: translateX(0px);
        transition: all 0.6s ease;
    }
}
.time {
    padding: 0px 70px;
    li {
        display: flex;
        justify-content: space-around;
        padding: 10px;
        margin: 20px 0px;
    }
    li:nth-child(1) {
        background-color: #fcf8e3;
    }
    li:nth-child(2) {
        background-color: #e9f5e9;
    }
}
.border {
    border: 6px solid #36aafd !important;
    transition: all 2s ease;
    width: 160px !important;
    height: 160px !important;
    transform: rotate(360deg) !important;
    opacity: 1 !important;
}
.score {
    max-width: 800px;
    margin: 0 auto;
    .title {
        margin: 60px 0px 30px 0px;
        display: flex;
        justify-content: center;
        align-items: center;
        flex-direction: column;
        .name {
            font-size: 26px;
            color: inherit;
            font-weight: 500;
        }
        .description {
            font-size: 14px;
            color: #888;
        }
    }
    .total {
        border: 1px solid #dbdbdb;
        background-color: #fff;
        padding: 40px;
        .look {
            border-bottom: 1px solid #dbdbdb;
            padding: 0px 0px 14px 14px;
            color: #36aafd;
        }
        .toolbar {
            padding: 10px 0;
        }
        .number {
            opacity: 0;
            border: 6px solid #fff;
            transform: rotate(0deg);
            display: flex;
            justify-content: center;
            align-items: center;
            flex-direction: column;
            margin: 0 auto;
            width: 160px;
            height: 160px;
            border-radius: 50%;
            margin-top: 80px;
            margin-bottom: 20px;
            transition: all 1s ease;

            span:nth-child(1) {
                font-size: 36px;
                font-weight: 600;
            }
            span:nth-child(2) {
                font-size: 14px;
            }
        }
    }
}
</style>

