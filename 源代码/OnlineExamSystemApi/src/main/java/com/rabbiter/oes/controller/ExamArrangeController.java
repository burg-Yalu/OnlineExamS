package com.rabbiter.oes.controller;

import com.rabbiter.oes.entity.ApiResult;
import com.rabbiter.oes.entity.ExamArrange;
import com.rabbiter.oes.entity.ExamStudentRoom;
import com.rabbiter.oes.service.ExamArrangeService;
import com.rabbiter.oes.util.ApiResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/arrange")
public class ExamArrangeController {

    @Autowired
    private ExamArrangeService examArrangeService;

    // 获取所有编排记录
    @GetMapping("/list")
    public ApiResult getAllExamArranges() {
        List<ExamArrange> arranges = examArrangeService.getAllExamArranges();
        return ApiResultHandler.buildApiResult(200, "查询成功", arranges);
    }

    // 根据考试编号获取编排
    @GetMapping("/exam/{examCode}")
    public ApiResult getExamArrangesByExamCode(@PathVariable Integer examCode) {
        List<ExamArrange> arranges = examArrangeService.getExamArrangesByExamCode(examCode);
        return ApiResultHandler.buildApiResult(200, "查询成功", arranges);
    }

    // 创建编排
    @PostMapping("/create")
    public ApiResult createExamArrange(@RequestBody ExamArrange examArrange) {
        int result = examArrangeService.createExamArrange(examArrange);
        if (result > 0) {
            return ApiResultHandler.buildApiResult(200, "创建成功", examArrange);
        }
        return ApiResultHandler.buildApiResult(500, "创建失败", null);
    }

    // 更新编排
    @PutMapping("/update")
    public ApiResult updateExamArrange(@RequestBody ExamArrange examArrange) {
        int result = examArrangeService.updateExamArrange(examArrange);
        if (result > 0) {
            return ApiResultHandler.buildApiResult(200, "更新成功", examArrange);
        }
        return ApiResultHandler.buildApiResult(500, "更新失败", null);
    }

    // 删除编排
    @DeleteMapping("/{arrangeId}")
    public ApiResult deleteExamArrange(@PathVariable Integer arrangeId) {
        int result = examArrangeService.deleteExamArrange(arrangeId);
        if (result > 0) {
            return ApiResultHandler.buildApiResult(200, "删除成功", null);
        }
        return ApiResultHandler.buildApiResult(500, "删除失败", null);
    }

    // 自动编排考场
    @PostMapping("/auto-arrange")
    public ApiResult autoArrangeExam(@RequestParam Integer examCode,
                                   @RequestParam Integer studentCount,
                                   @RequestParam String date,
                                   @RequestParam String session) {
        boolean result = examArrangeService.autoArrangeExam(examCode, studentCount, date, session);
        if (result) {
            return ApiResultHandler.buildApiResult(200, "自动编排成功", null);
        }
        return ApiResultHandler.buildApiResult(500, "自动编排失败", null);
    }

    // 手动编排考场
    @PostMapping("/manual-arrange")
    public ApiResult manualArrangeExam(@RequestParam Integer examCode,
                                      @RequestParam Integer roomId,
                                      @RequestParam String date,
                                      @RequestParam String session,
                                      @RequestParam Integer totalSeats) {
        boolean result = examArrangeService.manualArrangeExam(examCode, roomId, date, session, totalSeats);
        if (result) {
            return ApiResultHandler.buildApiResult(200, "手动编排成功", null);
        }
        return ApiResultHandler.buildApiResult(500, "手动编排失败", null);
    }

    // 获取考试的所有分配学生
    @GetMapping("/assigned-students/{examCode}")
    public ApiResult getAssignedStudents(@PathVariable Integer examCode) {
        List<ExamStudentRoom> students = examArrangeService.getAssignedStudents(examCode);
        return ApiResultHandler.buildApiResult(200, "查询成功", students);
    }

    // 批量分配座位
    @PostMapping("/batch-assign-seats")
    public ApiResult batchAssignSeats(@RequestParam Integer examCode,
                                     @RequestParam Integer roomId,
                                     @RequestBody List<Integer> studentIds) {
        boolean result = examArrangeService.batchAssignSeats(examCode, roomId, studentIds);
        if (result) {
            return ApiResultHandler.buildApiResult(200, "座位分配成功", null);
        }
        return ApiResultHandler.buildApiResult(500, "座位分配失败", null);
    }

    // 取消编排
    @PostMapping("/cancel/{arrangeId}")
    public ApiResult cancelArrange(@PathVariable Integer arrangeId) {
        boolean result = examArrangeService.cancelArrange(arrangeId);
        if (result) {
            return ApiResultHandler.buildApiResult(200, "取消编排成功", null);
        }
        return ApiResultHandler.buildApiResult(500, "取消编排失败", null);
    }

    // 检查编排冲突
    @GetMapping("/check-conflict")
    public ApiResult checkArrangeConflict(@RequestParam Integer roomId,
                                         @RequestParam String date,
                                         @RequestParam String session,
                                         @RequestParam(required = false) Integer excludeArrangeId) {
        boolean hasConflict = examArrangeService.checkArrangeConflict(roomId, date, session, excludeArrangeId);
        return ApiResultHandler.buildApiResult(200, "查询成功", !hasConflict);
    }
}