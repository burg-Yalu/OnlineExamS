package com.rabbiter.oes.entity;

import java.util.Date;

//考试学生考场分配实体类
public class ExamStudentRoom {
    private Integer assignId;
    private Integer examCode;
    private Integer studentId;
    private Integer roomId;
    private Integer seatId;
    private Date assignTime;
    private String status;

    public Integer getAssignId() {
        return assignId;
    }

    public void setAssignId(Integer assignId) {
        this.assignId = assignId;
    }

    public Integer getExamCode() {
        return examCode;
    }

    public void setExamCode(Integer examCode) {
        this.examCode = examCode;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getSeatId() {
        return seatId;
    }

    public void setSeatId(Integer seatId) {
        this.seatId = seatId;
    }

    public Date getAssignTime() {
        return assignTime;
    }

    public void setAssignTime(Date assignTime) {
        this.assignTime = assignTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ExamStudentRoom{" +
                "assignId=" + assignId +
                ", examCode=" + examCode +
                ", studentId=" + studentId +
                ", roomId=" + roomId +
                ", seatId=" + seatId +
                ", assignTime=" + assignTime +
                ", status='" + status + '\'' +
                '}';
    }
}