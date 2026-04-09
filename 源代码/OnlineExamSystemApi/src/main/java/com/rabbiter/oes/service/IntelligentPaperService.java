package com.rabbiter.oes.service;

import com.rabbiter.oes.entity.PaperManage;
import java.util.List;
import java.util.Map;

public interface IntelligentPaperService {
    // 智能组卷：根据条件自动生成试卷
    Map<String, Object> generateIntelligentPaper(Map<String, Object> conditions);

    // 根据知识点和题型组卷
    Map<String, Object> generatePaperByKnowledgeAndType(Integer examCode, Map<String, Integer> questionTypeCount, String[] knowledgePoints);

    // 根据难度组卷
    Map<String, Object> generatePaperByDifficulty(Integer examCode, Map<String, Integer> difficultyCount);

    // 随机组卷
    Map<String, Object> generateRandomPaper(Integer examCode, Integer totalScore, Map<String, Integer> questionTypeRatio);

    // 查询可用题目
    List<Map<String, Object>> getAvailableQuestions(Map<String, Object> conditions);

    // 生成试卷预览
    Map<String, Object> generatePaperPreview(Integer paperId);

    // 保存生成的试卷
    boolean saveGeneratedPaper(Integer examCode, List<PaperManage> paperQuestions);

    // 获取组卷历史
    List<Map<String, Object>> getPaperGenerationHistory(Integer examCode);

    // 预估试卷难度和用时
    Map<String, Object> estimatePaperStats(List<PaperManage> paperQuestions);
}