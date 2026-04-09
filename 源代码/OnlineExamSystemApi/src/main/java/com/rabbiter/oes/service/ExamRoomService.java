package com.rabbiter.oes.service;

import com.rabbiter.oes.entity.ExamRoom;
import java.util.List;

public interface ExamRoomService {
    // 获取所有考场
    List<ExamRoom> getAllExamRooms();

    // 根据ID获取考场
    ExamRoom getExamRoomById(Integer roomId);

    // 创建考场
    int createExamRoom(ExamRoom examRoom);

    // 更新考场信息
    int updateExamRoom(ExamRoom examRoom);

    // 删除考场
    int deleteExamRoom(Integer roomId);

    // 批量生成座位
    int generateSeats(Integer roomId, Integer rows, Integer cols);

    // 获取可用考场列表
    List<ExamRoom> getAvailableRooms(Integer capacity, String date, String session);

    // 获取考场使用统计
    int getRoomUsageStats(Integer roomId, String date, String session);
}