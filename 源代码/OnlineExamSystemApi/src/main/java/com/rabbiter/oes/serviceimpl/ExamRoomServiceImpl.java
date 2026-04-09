package com.rabbiter.oes.serviceimpl;

import com.rabbiter.oes.entity.ExamRoom;
import com.rabbiter.oes.entity.ExamSeat;
import com.rabbiter.oes.mapper.ExamRoomMapper;
import com.rabbiter.oes.mapper.ExamSeatMapper;
import com.rabbiter.oes.service.ExamRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExamRoomServiceImpl implements ExamRoomService {

    @Autowired
    private ExamRoomMapper examRoomMapper;

    @Autowired
    private ExamSeatMapper examSeatMapper;

    @Override
    public List<ExamRoom> getAllExamRooms() {
        return examRoomMapper.findAll();
    }

    @Override
    public ExamRoom getExamRoomById(Integer roomId) {
        return examRoomMapper.findById(roomId);
    }

    @Override
    @Transactional
    public int createExamRoom(ExamRoom examRoom) {
        return examRoomMapper.insert(examRoom);
    }

    @Override
    @Transactional
    public int updateExamRoom(ExamRoom examRoom) {
        return examRoomMapper.update(examRoom);
    }

    @Override
    @Transactional
    public int deleteExamRoom(Integer roomId) {
        return examRoomMapper.deleteById(roomId);
    }

    @Override
    @Transactional
    public int generateSeats(Integer roomId, Integer rows, Integer cols) {
        // 先删除旧座位
        examSeatMapper.deleteByRoomId(roomId);

        // 生成座位列表
        List<ExamSeat> seats = new ArrayList<>();
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= cols; j++) {
                ExamSeat seat = new ExamSeat();
                seat.setRoomId(roomId);
                seat.setSeatNumber(String.format("%02d-%02d", i, j));
                seat.setRowNumber(i);
                seat.setColumnNumber(j);
                seat.setStatus("0");
                seats.add(seat);
            }
        }

        // 批量插入
        return examSeatMapper.insertBatch(roomId, rows, cols);
    }

    @Override
    public List<ExamRoom> getAvailableRooms(Integer capacity, String date, String session) {
        return examRoomMapper.findAvailableRooms(capacity, date, session);
    }

    @Override
    public int getRoomUsageStats(Integer roomId, String date, String session) {
        return examRoomMapper.getRoomUsage(roomId, date, session);
    }
}