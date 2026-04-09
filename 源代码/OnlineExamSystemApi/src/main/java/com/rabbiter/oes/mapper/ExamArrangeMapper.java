package com.rabbiter.oes.mapper;

import com.rabbiter.oes.entity.ExamArrange;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ExamArrangeMapper {
    // 查询所有编排记录
    List<ExamArrange> findAll();

    // 根据考试编号查询编排
    List<ExamArrange> findByExamCode(@Param("examCode") Integer examCode);

    // 根据考场和日期查询编排
    List<ExamArrange> findByRoomAndDate(@Param("roomId") Integer roomId, @Param("date") String date);

    // 添加编排
    int insert(ExamArrange examArrange);

    // 更新编排
    int update(ExamArrange examArrange);

    // 删除编排
    int deleteById(@Param("arrangeId") Integer arrangeId);

    // 更新已分配座位数
    int updateAssignedSeats(@Param("arrangeId") Integer arrangeId, @Param("increment") int increment);

    // 自动编排考场
    int autoArrangeExam(@Param("examCode") Integer examCode, @Param("studentCount") Integer studentCount, @Param("date") String date, @Param("session") String session);

    // 查询考试的所有座位分配情况
    int getTotalSeatsByExam(@Param("examCode") Integer examCode);

    // 检查时间冲突
    int checkTimeConflict(@Param("roomId") Integer roomId, @Param("date") String date, @Param("session") String session, @Param("excludeArrangeId") Integer excludeArrangeId);

    // 根据ID查询编排
    ExamArrange selectById(@Param("arrangeId") Integer arrangeId);
}