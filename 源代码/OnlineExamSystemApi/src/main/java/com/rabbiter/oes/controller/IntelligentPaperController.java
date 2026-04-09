package com.rabbiter.oes.controller;

import com.rabbiter.oes.entity.ApiResult;
import com.rabbiter.oes.entity.PaperManage;
import com.rabbiter.oes.service.IntelligentPaperService;
import com.rabbiter.oes.util.ApiResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/intelligent-paper")
public class IntelligentPaperController {

    @Autowired
    private IntelligentPaperService intelligentPaperService;

    // 智能组卷
    @PostMapping("/generate")
    public ApiResult generateIntelligentPaper(@RequestBody Map<String, Object> conditions) {
        try {
            Map<String, Object> result = intelligentPaperService.generateIntelligentPaper(conditions);
            return ApiResultHandler.buildApiResult(200, "智能组卷成功", result);
        } catch (Exception e) {
            return ApiResultHandler.buildApiResult(500, "智能组卷失败：" + e.getMessage(), null);
        }
    }

    // 根据知识点和题型组卷
    @PostMapping("/generate-by-knowledge")
    public ApiResult generatePaperByKnowledge(@RequestParam Integer examCode,
                                           @RequestBody Map<String, Object> params) {
        try {
            Map<String, Integer> questionTypeCount = (Map<String, Integer>) params.get("questionTypeCount");
            String[] knowledgePoints = (String[]) params.get("knowledgePoints");

            Map<String, Object> result = intelligentPaperService.generatePaperByKnowledgeAndType(
                examCode, questionTypeCount, knowledgePoints);

            return ApiResultHandler.buildApiResult(200, "组卷成功", result);
        } catch (Exception e) {
            return ApiResultHandler.buildApiResult(500, "组卷失败：" + e.getMessage(), null);
        }
    }

    // 根据难度组卷
    @PostMapping("/generate-by-difficulty")
    public ApiResult generatePaperByDifficulty(@RequestParam Integer examCode,
                                             @RequestBody Map<String, Object> params) {
        try {
            Map<String, Integer> difficultyCount = (Map<String, Integer>) params.get("difficultyCount");

            Map<String, Object> result = intelligentPaperService.generatePaperByDifficulty(
                examCode, difficultyCount);

            return ApiResultHandler.buildApiResult(200, "组卷成功", result);
        } catch (Exception e) {
            return ApiResultHandler.buildApiResult(500, "组卷失败：" + e.getMessage(), null);
        }
    }

    // 随机组卷
    @PostMapping("/generate-random")
    public ApiResult generateRandomPaper(@RequestParam Integer examCode,
                                       @RequestParam Integer totalScore,
                                       @RequestBody Map<String, Integer> questionTypeRatio) {
        try {
            Map<String, Object> result = intelligentPaperService.generateRandomPaper(
                examCode, totalScore, questionTypeRatio);

            return ApiResultHandler.buildApiResult(200, "随机组卷成功", result);
        } catch (Exception e) {
            return ApiResultHandler.buildApiResult(500, "随机组卷失败：" + e.getMessage(), null);
        }
    }

    // 获取可用题目列表
    @GetMapping("/available-questions")
    public ApiResult getAvailableQuestions(@RequestParam String subject,
                                         @RequestParam(required = false) String questionType,
                                         @RequestParam(required = false) String difficulty,
                                         @RequestParam(required = false) String knowledgePoint) {
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("subject", subject);
            conditions.put("questionType", questionType);
            conditions.put("difficulty", difficulty);
            conditions.put("knowledgePoint", knowledgePoint);

            List<Map<String, Object>> questions = intelligentPaperService.getAvailableQuestions(conditions);
            return ApiResultHandler.buildApiResult(200, "查询成功", questions);
        } catch (Exception e) {
            return ApiResultHandler.buildApiResult(500, "查询失败：" + e.getMessage(), null);
        }
    }

    // 获取题目统计信息
    @GetMapping("/question-stats")
    public ApiResult getQuestionStats(@RequestParam String subject,
                                     @RequestParam(required = false) String difficulty,
                                     @RequestParam(required = false) String knowledgePoint) {
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("subject", subject);
            conditions.put("difficulty", difficulty);
            conditions.put("knowledgePoint", knowledgePoint);

            List<Map<String, Object>> questions = intelligentPaperService.getAvailableQuestions(conditions);

            // 统计信息
            Map<String, Object> stats = new HashMap<>();

            // 按题型统计
            Map<String, Long> typeStats = new HashMap<>();
            // 按难度统计
            Map<String, Long> difficultyStats = new HashMap<>();
            // 按知识点统计
            Map<String, Long> knowledgeStats = new HashMap<>();

            for (Map<String, Object> question : questions) {
                String qType = (String) question.get("questionType");
                String qDiff = (String) question.get("difficulty");
                String qKnowledge = (String) question.get("knowledgePoint");

                typeStats.put(qType, typeStats.getOrDefault(qType, 0L) + 1);
                difficultyStats.put(qDiff, difficultyStats.getOrDefault(qDiff, 0L) + 1);
                knowledgeStats.put(qKnowledge, knowledgeStats.getOrDefault(qKnowledge, 0L) + 1);
            }

            stats.put("typeStats", typeStats);
            stats.put("difficultyStats", difficultyStats);
            stats.put("knowledgeStats", knowledgeStats);
            stats.put("totalCount", questions.size());

            return ApiResultHandler.buildApiResult(200, "统计成功", stats);
        } catch (Exception e) {
            return ApiResultHandler.buildApiResult(500, "统计失败：" + e.getMessage(), null);
        }
    }

    // 预览生成的试卷
    @GetMapping("/preview/{paperId}")
    public ApiResult previewPaper(@PathVariable Integer paperId) {
        try {
            Map<String, Object> preview = intelligentPaperService.generatePaperPreview(paperId);
            return ApiResultHandler.buildApiResult(200, "预览成功", preview);
        } catch (Exception e) {
            return ApiResultHandler.buildApiResult(500, "预览失败：" + e.getMessage(), null);
        }
    }

    // 保存生成的试卷
    @PostMapping("/save")
    public ApiResult saveGeneratedPaper(@RequestParam Integer examCode,
                                       @RequestBody List<PaperManage> paperQuestions) {
        try {
            boolean success = intelligentPaperService.saveGeneratedPaper(examCode, paperQuestions);
            if (success) {
                return ApiResultHandler.buildApiResult(200, "保存成功", null);
            } else {
                return ApiResultHandler.buildApiResult(500, "保存失败", null);
            }
        } catch (Exception e) {
            return ApiResultHandler.buildApiResult(500, "保存失败：" + e.getMessage(), null);
        }
    }

    // 获取组卷历史
    @GetMapping("/history/{examCode}")
    public ApiResult getPaperGenerationHistory(@PathVariable Integer examCode) {
        try {
            List<Map<String, Object>> history = intelligentPaperService.getPaperGenerationHistory(examCode);
            return ApiResultHandler.buildApiResult(200, "查询成功", history);
        } catch (Exception e) {
            return ApiResultHandler.buildApiResult(500, "查询失败：" + e.getMessage(), null);
        }
    }

    // 试卷难度预估
    @PostMapping("/estimate-stats")
    public ApiResult estimatePaperStats(@RequestBody List<PaperManage> paperQuestions) {
        try {
            Map<String, Object> stats = intelligentPaperService.estimatePaperStats(paperQuestions);
            return ApiResultHandler.buildApiResult(200, "预估成功", stats);
        } catch (Exception e) {
            return ApiResultHandler.buildApiResult(500, "预估失败：" + e.getMessage(), null);
        }
    }

    // 获取智能组卷模板
    @GetMapping("/template")
    public ApiResult getPaperTemplate() {
        Map<String, Object> template = new HashMap<>();

        // 各题型分值模板
        Map<String, Integer> questionTemplate = new HashMap<>();
        questionTemplate.put("multi", 40);  // 选择题40分
        questionTemplate.put("judge", 30);  // 判断题30分
        questionTemplate.put("fill", 30);   // 填空题30分

        // 难度分布模板
        Map<String, Integer> difficultyTemplate = new HashMap<>();
        difficultyTemplate.put("easy", 30);   // 简单30%
        difficultyTemplate.put("medium", 50); // 中等50%
        difficultyTemplate.put("hard", 20);    // 困难20%

        template.put("questionTemplate", questionTemplate);
        template.put("difficultyTemplate", difficultyTemplate);
        template.put("knowledgeTemplate", Arrays.asList("第一章", "第二章", "第三章"));

        return ApiResultHandler.buildApiResult(200, "获取成功", template);
    }
}