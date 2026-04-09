package com.rabbiter.oes.mapper;

import com.rabbiter.oes.entity.ExamRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ExamRoomMapper {
    // 查询所有考场
    List<ExamRoom> findAll();

    // 根据ID查询考场
    ExamRoom findById(@Param("roomId") Integer roomId);

    // 查询可用考场
    List<ExamRoom> findAvailableRooms(@Param("capacity") Integer capacity, @Param("date") String date, @Param("session") String session);

    // 添加考场
    int insert(ExamRoom examRoom);

    // 更新考场
    int update(ExamRoom examRoom);

    // 删除考场
    int deleteById(@Param("roomId") Integer roomId);

    // 更新考场可用座位数
    int updateAvailableSeats(@Param("roomId") Integer roomId, @Param("change") Integer change);

    // 查询考场使用情况
    int getRoomUsage(@Param("roomId") Integer roomId, @Param("date") String date, @Param("session") String session);
}