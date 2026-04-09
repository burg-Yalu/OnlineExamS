package com.rabbiter.oes.controller;

import com.rabbiter.oes.entity.ApiResult;
import com.rabbiter.oes.entity.ExamRoom;
import com.rabbiter.oes.service.ExamRoomService;
import com.rabbiter.oes.util.ApiResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room")
public class ExamRoomController {

    @Autowired
    private ExamRoomService examRoomService;

    // 获取所有考场
    @GetMapping("/list")
    public ApiResult getAllExamRooms() {
        List<ExamRoom> rooms = examRoomService.getAllExamRooms();
        return ApiResultHandler.buildApiResult(200, "查询成功", rooms);
    }

    // 获取考场详情
    @GetMapping("/{roomId}")
    public ApiResult getExamRoomById(@PathVariable Integer roomId) {
        ExamRoom room = examRoomService.getExamRoomById(roomId);
        if (room != null) {
            return ApiResultHandler.buildApiResult(200, "查询成功", room);
        }
        return ApiResultHandler.buildApiResult(404, "考场不存在", null);
    }

    // 创建考场
    @PostMapping("/create")
    public ApiResult createExamRoom(@RequestBody ExamRoom examRoom) {
        int result = examRoomService.createExamRoom(examRoom);
        if (result > 0) {
            return ApiResultHandler.buildApiResult(200, "创建成功", examRoom);
        }
        return ApiResultHandler.buildApiResult(500, "创建失败", null);
    }

    // 更新考场信息
    @PutMapping("/update")
    public ApiResult updateExamRoom(@RequestBody ExamRoom examRoom) {
        int result = examRoomService.updateExamRoom(examRoom);
        if (result > 0) {
            return ApiResultHandler.buildApiResult(200, "更新成功", examRoom);
        }
        return ApiResultHandler.buildApiResult(500, "更新失败", null);
    }

    // 删除考场
    @DeleteMapping("/{roomId}")
    public ApiResult deleteExamRoom(@PathVariable Integer roomId) {
        int result = examRoomService.deleteExamRoom(roomId);
        if (result > 0) {
            return ApiResultHandler.buildApiResult(200, "删除成功", null);
        }
        return ApiResultHandler.buildApiResult(500, "删除失败", null);
    }

    // 批量生成座位
    @PostMapping("/{roomId}/seats")
    public ApiResult generateSeats(@PathVariable Integer roomId, @RequestParam Integer rows, @RequestParam Integer cols) {
        int result = examRoomService.generateSeats(roomId, rows, cols);
        if (result > 0) {
            return ApiResultHandler.buildApiResult(200, "座位生成成功", null);
        }
        return ApiResultHandler.buildApiResult(500, "座位生成失败", null);
    }

    // 获取可用考场列表
    @GetMapping("/available")
    public ApiResult getAvailableRooms(@RequestParam Integer capacity, @RequestParam String date, @RequestParam String session) {
        List<ExamRoom> rooms = examRoomService.getAvailableRooms(capacity, date, session);
        return ApiResultHandler.buildApiResult(200, "查询成功", rooms);
    }

    // 获取考场使用统计
    @GetMapping("/{roomId}/usage")
    public ApiResult getRoomUsageStats(@PathVariable Integer roomId, @RequestParam String date, @RequestParam String session) {
        int usage = examRoomService.getRoomUsageStats(roomId, date, session);
        return ApiResultHandler.buildApiResult(200, "查询成功", usage);
    }
}