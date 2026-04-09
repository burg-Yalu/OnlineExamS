package com.rabbiter.oes.mapper;

import com.rabbiter.oes.entity.ExamStudentRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ExamStudentRoomMapper {
    // 查询学生考场分配
    List<ExamStudentRoom> findByStudentId(@Param("studentId") Integer studentId);

    // 查询考试的所有分配
    List<ExamStudentRoom> findByExamCode(@Param("examCode") Integer examCode);

    // 添加分配
    int insert(ExamStudentRoom examStudentRoom);

    // 批量分配座位
    int insertBatch(@Param("list") List<ExamStudentRoom> list);

    // 更新分配状态
    int updateStatus(@Param("assignId") Integer assignId, @Param("status") String status);

    // 查询考场中的学生列表
    List<ExamStudentRoom> findStudentsInRoom(@Param("examCode") Integer examCode, @Param("roomId") Integer roomId);

    // 删除学生分配
    int deleteByStudentId(@Param("studentId") Integer studentId, @Param("examCode") Integer examCode);
    /**
     * 根据考试编号删除所有学生分配记录
     */
    int deleteByExamCode(@Param("examCode") Integer examCode);

    // 检查学生是否已分配
    int checkStudentAssigned(@Param("studentId") Integer studentId, @Param("examCode") Integer examCode);
}