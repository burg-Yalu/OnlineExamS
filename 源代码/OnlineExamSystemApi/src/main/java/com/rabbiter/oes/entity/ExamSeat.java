package com.rabbiter.oes.entity;

//考场座位实体类
public class ExamSeat {
    private Integer seatId;
    private Integer roomId;
    private String seatNumber;
    private Integer rowNumber;
    private Integer columnNumber;
    private String status;

    public Integer getSeatId() {
        return seatId;
    }

    public void setSeatId(Integer seatId) {
        this.seatId = seatId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public Integer getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(Integer columnNumber) {
        this.columnNumber = columnNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ExamSeat{" +
                "seatId=" + seatId +
                ", roomId=" + roomId +
                ", seatNumber='" + seatNumber + '\'' +
                ", rowNumber=" + rowNumber +
                ", columnNumber=" + columnNumber +
                ", status='" + status + '\'' +
                '}';
    }
}