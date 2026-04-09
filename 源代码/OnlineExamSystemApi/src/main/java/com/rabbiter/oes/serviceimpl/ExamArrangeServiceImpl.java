package com.rabbiter.oes.serviceimpl;

import com.rabbiter.oes.entity.ExamArrange;
import com.rabbiter.oes.entity.ExamRoom;
import com.rabbiter.oes.entity.ExamSeat;
import com.rabbiter.oes.entity.ExamStudentRoom;
import com.rabbiter.oes.mapper.ExamArrangeMapper;
import com.rabbiter.oes.mapper.ExamRoomMapper;
import com.rabbiter.oes.mapper.ExamSeatMapper;
import com.rabbiter.oes.mapper.ExamStudentRoomMapper;
import com.rabbiter.oes.service.ExamArrangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExamArrangeServiceImpl implements ExamArrangeService {

    @Autowired
    private ExamArrangeMapper examArrangeMapper;

    @Autowired
    private ExamRoomMapper examRoomMapper;

    @Autowired
    private ExamSeatMapper examSeatMapper;

    @Autowired
    private ExamStudentRoomMapper examStudentRoomMapper;

    @Override
    public List<ExamArrange> getAllExamArranges() {
        return examArrangeMapper.findAll();
    }

    @Override
    public List<ExamArrange> getExamArrangesByExamCode(Integer examCode) {
        return examArrangeMapper.findByExamCode(examCode);
    }

    @Override
    @Transactional
    public int createExamArrange(ExamArrange examArrange) {
        return examArrangeMapper.insert(examArrange);
    }

    @Override
    @Transactional
    public int updateExamArrange(ExamArrange examArrange) {
        return examArrangeMapper.update(examArrange);
    }

    @Override
    @Transactional
    public int deleteExamArrange(Integer arrangeId) {
        return examArrangeMapper.deleteById(arrangeId);
    }

    @Override
    @Transactional
    public boolean autoArrangeExam(Integer examCode, Integer studentCount, String date, String session) {
        // 检查是否已经有编排
        int totalSeats = examArrangeMapper.getTotalSeatsByExam(examCode);
        if (totalSeats >= studentCount) {
            return false;
        }

        // 查找可用考场
        List<ExamRoom> availableRooms = examRoomMapper.findAvailableRooms(studentCount, date, session);
        if (availableRooms.isEmpty()) {
            return false;
        }

        // 自动编排
        int result = examArrangeMapper.autoArrangeExam(examCode, studentCount, date, session);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean manualArrangeExam(Integer examCode, Integer roomId, String date, String session, Integer totalSeats) {
        // 检查时间冲突
        if (checkArrangeConflict(roomId, date, session, null)) {
            return false;
        }

        // 创建编排记录
        ExamArrange examArrange = new ExamArrange();
        examArrange.setExamCode(examCode);
        examArrange.setRoomId(roomId);
        examArrange.setArrangeDate(date);
        examArrange.setSession(session);
        examArrange.setTotalSeats(totalSeats);
        examArrange.setAssignedSeats(0);
        examArrange.setStatus("0");

        return examArrangeMapper.insert(examArrange) > 0;
    }

    @Override
    public List<ExamStudentRoom> getAssignedStudents(Integer examCode) {
        return examStudentRoomMapper.findByExamCode(examCode);
    }

    @Override
    @Transactional
    public boolean batchAssignSeats(Integer examCode, Integer roomId, List<Integer> studentIds) {
        // 获取考试编排信息
        List<ExamArrange> arrangements = examArrangeMapper.findByExamCode(examCode);
        if (arrangements.isEmpty()) {
            return false;
        }

        ExamArrange arrangement = arrangements.get(0);

        // 检查座位是否足够
        List<ExamSeat> availableSeats = examSeatMapper.findAvailableSeats(roomId, arrangement.getArrangeId());
        if (availableSeats.size() < studentIds.size()) {
            return false;
        }

        // 分配座位
        List<ExamStudentRoom> assignments = new ArrayList<>();
        for (int i = 0; i < studentIds.size(); i++) {
            ExamStudentRoom assignment = new ExamStudentRoom();
            assignment.setExamCode(examCode);
            assignment.setStudentId(studentIds.get(i));
            assignment.setRoomId(roomId);
            assignment.setSeatId(availableSeats.get(i).getSeatId());
            assignment.setStatus("0");
            assignments.add(assignment);
        }

        // 批量插入
        int result = examStudentRoomMapper.insertBatch(assignments);

        // 更新已分配座位数
        if (result > 0) {
            examArrangeMapper.updateAssignedSeats(arrangement.getArrangeId(), studentIds.size());
            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public boolean cancelArrange(Integer arrangeId) {
        // 1. 先根据 arrangeId 获取 examCode
        ExamArrange examArrange = examArrangeMapper.selectById(arrangeId);
        if (examArrange == null) {
            return false;
        }
        Integer examCode = examArrange.getExamCode();

        // 2. 删除该场考试的所有学生分配（重点：调用正确的方法）
        examStudentRoomMapper.deleteByExamCode(examCode);

        // 3. 删除考试安排
        return examArrangeMapper.deleteById(arrangeId) > 0;
    }

    @Override
    public boolean checkArrangeConflict(Integer roomId, String date, String session, Integer excludeArrangeId) {
        int conflictCount = examArrangeMapper.checkTimeConflict(roomId, date, session, excludeArrangeId);
        return conflictCount > 0;
    }
}