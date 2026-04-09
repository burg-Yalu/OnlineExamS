package com.rabbiter.oes.entity;

import java.util.Date;

//考试安排实体类
public class ExamArrange {
    private Integer arrangeId;
    private Integer examCode;
    private Integer roomId;
    private String arrangeDate;
    private String session;
    private String startTime;
    private String endTime;
    private Integer totalSeats;
    private Integer assignedSeats;
    private String status;
    private Date createTime;
    private Date updateTime;

    public Integer getArrangeId() {
        return arrangeId;
    }

    public void setArrangeId(Integer arrangeId) {
        this.arrangeId = arrangeId;
    }

    public Integer getExamCode() {
        return examCode;
    }

    public void setExamCode(Integer examCode) {
        this.examCode = examCode;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getArrangeDate() {
        return arrangeDate;
    }

    public void setArrangeDate(String arrangeDate) {
        this.arrangeDate = arrangeDate;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Integer getAssignedSeats() {
        return assignedSeats;
    }

    public void setAssignedSeats(Integer assignedSeats) {
        this.assignedSeats = assignedSeats;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "ExamArrange{" +
                "arrangeId=" + arrangeId +
                ", examCode=" + examCode +
                ", roomId=" + roomId +
                ", arrangeDate='" + arrangeDate + '\'' +
                ", session='" + session + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", totalSeats=" + totalSeats +
                ", assignedSeats=" + assignedSeats +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}