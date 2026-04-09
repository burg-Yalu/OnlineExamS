package com.rabbiter.oes.mapper;

import com.rabbiter.oes.entity.ExamSeat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ExamSeatMapper {
    // 查询考场所有座位
    List<ExamSeat> findByRoomId(@Param("roomId") Integer roomId);

    // 查询可用座位
    List<ExamSeat> findAvailableSeats(@Param("roomId") Integer roomId, @Param("arrangeId") Integer arrangeId);

    // 批量添加座位
    int insertBatch(@Param("roomId") Integer roomId, @Param("rows") Integer rows, @Param("cols") Integer cols);

    // 删除考场所有座位
    int deleteByRoomId(@Param("roomId") Integer roomId);

    // 更新座位状态
    int updateSeatStatus(@Param("seatId") Integer seatId, @Param("status") String status);
}