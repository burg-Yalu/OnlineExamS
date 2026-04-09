package com.rabbiter.oes.service;

import com.rabbiter.oes.entity.ExamArrange;
import com.rabbiter.oes.entity.ExamStudentRoom;
import java.util.List;

public interface ExamArrangeService {
    // 获取所有编排记录
    List<ExamArrange> getAllExamArranges();

    // 根据考试编号获取编排
    List<ExamArrange> getExamArrangesByExamCode(Integer examCode);

    // 创建编排
    int createExamArrange(ExamArrange examArrange);

    // 更新编排
    int updateExamArrange(ExamArrange examArrange);

    // 删除编排
    int deleteExamArrange(Integer arrangeId);

    // 自动编排考场
    boolean autoArrangeExam(Integer examCode, Integer studentCount, String date, String session);

    // 手动编排考场
    boolean manualArrangeExam(Integer examCode, Integer roomId, String date, String session, Integer totalSeats);

    // 获取考试的所有分配学生
    List<ExamStudentRoom> getAssignedStudents(Integer examCode);

    // 批量分配座位
    boolean batchAssignSeats(Integer examCode, Integer roomId, List<Integer> studentIds);

    // 取消编排
    boolean cancelArrange(Integer arrangeId);

    // 检查编排冲突
    boolean checkArrangeConflict(Integer roomId, String date, String session, Integer excludeArrangeId);
}