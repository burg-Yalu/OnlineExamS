package com.rabbiter.oes.serviceimpl;

import com.rabbiter.oes.entity.*;
import com.rabbiter.oes.mapper.JudgeQuestionMapper;
import com.rabbiter.oes.mapper.MultiQuestionMapper;
import com.rabbiter.oes.mapper.PaperMapper;
import com.rabbiter.oes.service.IntelligentPaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IntelligentPaperServiceImpl implements IntelligentPaperService {

    @Autowired
    private MultiQuestionMapper multiQuestionMapper;

    @Autowired
    private JudgeQuestionMapper judgeQuestionMapper;

    @Autowired
    private PaperMapper paperMapper;

    @Override
    public Map<String, Object> generateIntelligentPaper(Map<String, Object> conditions) {
        Integer examCode = (Integer) conditions.get("examCode");
        Integer totalScore = (Integer) conditions.getOrDefault("totalScore", 100);
        Map<String, Integer> questionTypeCount = (Map<String, Integer>) conditions.get("questionTypeCount");
        String[] knowledgePoints = (String[]) conditions.get("knowledgePoints");
        String difficulty = (String) conditions.getOrDefault("difficulty", "all");
        String strategy = (String) conditions.getOrDefault("strategy", "balanced");

        Map<String, Object> result = new HashMap<>();
        List<PaperManage> paperQuestions = new ArrayList<>();

        // 根据不同策略进行组卷
        switch (strategy) {
            case "knowledge":
                paperQuestions = generateByKnowledge(examCode, questionTypeCount, knowledgePoints, difficulty);
                break;
            case "difficulty":
                paperQuestions = generateByDifficulty(examCode, questionTypeCount, difficulty);
                break;
            case "random":
            default:
                paperQuestions = generateRandom(examCode, questionTypeCount, difficulty);
                break;
        }

        // 计算总分
        int actualScore = paperQuestions.size() * 10; // 每题默认10分

        result.put("paperQuestions", paperQuestions);
        result.put("actualScore", actualScore);
        result.put("totalScore", totalScore);
        result.put("strategy", strategy);
        result.put("examCode", examCode);

        return result;
    }

    @Override
    public Map<String, Object> generatePaperByKnowledgeAndType(Integer examCode, Map<String, Integer> questionTypeCount, String[] knowledgePoints) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("examCode", examCode);
        conditions.put("questionTypeCount", questionTypeCount);
        conditions.put("knowledgePoints", knowledgePoints);
        conditions.put("strategy", "knowledge");

        return generateIntelligentPaper(conditions);
    }

    @Override
    public Map<String, Object> generatePaperByDifficulty(Integer examCode, Map<String, Integer> difficultyCount) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("examCode", examCode);
        conditions.put("difficultyCount", difficultyCount);
        conditions.put("strategy", "difficulty");

        return generateIntelligentPaper(conditions);
    }

    @Override
    public Map<String, Object> generateRandomPaper(Integer examCode, Integer totalScore, Map<String, Integer> questionTypeRatio) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("examCode", examCode);
        conditions.put("totalScore", totalScore);
        conditions.put("questionTypeRatio", questionTypeRatio);
        conditions.put("strategy", "random");

        return generateIntelligentPaper(conditions);
    }

    @Override
    public List<Map<String, Object>> getAvailableQuestions(Map<String, Object> conditions) {
        String subject = (String) conditions.get("subject");
        String questionType = (String) conditions.get("questionType");
        String difficulty = (String) conditions.get("difficulty");
        String knowledgePoint = (String) conditions.get("knowledgePoint");

        List<Map<String, Object>> questions = new ArrayList<>();

        // 查询选择题
        if ("multi".equals(questionType) || "all".equals(questionType)) {
            List<MultiQuestion> multiQuestions = multiQuestionMapper.findByConditions(subject, difficulty, knowledgePoint);
            multiQuestions.forEach(q -> {
                Map<String, Object> question = new HashMap<>();
                question.put("questionId", q.getQuestionId());
                question.put("subject", q.getSubject());
                question.put("question", q.getQuestion());
                question.put("optionA", q.getAnswerA());
                question.put("optionB", q.getAnswerB());
                question.put("optionC", q.getAnswerC());
                question.put("optionD", q.getAnswerD());
                question.put("answer", q.getRightAnswer());
                question.put("difficulty", q.getLevel());
                question.put("knowledgePoint", q.getSection());
                question.put("questionType", "multi");
                questions.add(question);
            });
        }

        // 查询判断题
        if ("judge".equals(questionType) || "all".equals(questionType)) {
            List<JudgeQuestion> judgeQuestions = judgeQuestionMapper.findByConditions(subject, difficulty, knowledgePoint);
            judgeQuestions.forEach(q -> {
                Map<String, Object> question = new HashMap<>();
                question.put("questionId", q.getQuestionId());
                question.put("subject", q.getSubject());
                question.put("question", q.getQuestion());
                question.put("answer", q.getAnswer());
                question.put("score", q.getScore());
                question.put("difficulty", q.getLevel());
                question.put("knowledgePoint", q.getSection());
                question.put("questionType", "judge");
                questions.add(question);
            });
        }

        return questions;
    }

    @Override
    public Map<String, Object> generatePaperPreview(Integer paperId) {
        // 这里需要从数据库获取试卷的所有题目
        List<PaperManage> paperQuestions = paperMapper.findByPaperId(paperId);

        Map<String, Object> preview = new HashMap<>();
        preview.put("paperQuestions", paperQuestions);
        preview.put("paperStats", estimatePaperStats(paperQuestions));

        return preview;
    }

    @Override
    @Transactional
    public boolean saveGeneratedPaper(Integer examCode, List<PaperManage> paperQuestions) {
        // 生成新的paperId
        Integer newPaperId = generateNewPaperId();

        // 保存试卷题目
        for (PaperManage paperQuestion : paperQuestions) {
            paperQuestion.setPaperId(newPaperId);
            paperMapper.add(paperQuestion);
        }

        // 更新exam_manage表的paperId
        return updateExamPaperId(examCode, newPaperId) > 0;
    }

    @Override
    public List<Map<String, Object>> getPaperGenerationHistory(Integer examCode) {
        // 查询考试的所有试卷
        List<PaperManage> papers = paperMapper.findByExamCode(examCode);

        return papers.stream().map(paper -> {
            Map<String, Object> history = new HashMap<>();
            history.put("paperId", paper.getPaperId());
            history.put("questionType", paper.getQuestionType());
            history.put("questionId", paper.getQuestionId());
            history.put("createTime", new Date());
            return history;
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> estimatePaperStats(List<PaperManage> paperQuestions) {
        Map<String, Object> stats = new HashMap<>();

        // 统计各题型数量 (questionType: 1=选择题, 2=填空题, 3=判断题)
        Map<String, Long> typeCount = paperQuestions.stream()
                .collect(Collectors.groupingBy(
                    p -> {
                        Integer qt = p.getQuestionType();
                        if (qt == 1) return "选择题";
                        else if (qt == 2) return "填空题";
                        else return "判断题";
                    },
                    Collectors.counting()
                ));

        // 计算平均难度（使用questionId的数字位模拟难度）
        double avgDifficulty = paperQuestions.stream()
                .mapToInt(PaperManage::getQuestionId)
                .average()
                .orElse(0.0);

        // 预估考试用时（每题2分钟）
        int estimatedTime = paperQuestions.size() * 2;

        // 总分（每题10分）
        int totalScore = paperQuestions.size() * 10;

        stats.put("typeCount", typeCount);
        stats.put("avgDifficulty", avgDifficulty);
        stats.put("totalScore", totalScore);
        stats.put("estimatedTime", estimatedTime);

        return stats;
    }

    // 根据知识点组卷
    private List<PaperManage> generateByKnowledge(Integer examCode, Map<String, Integer> questionTypeCount, String[] knowledgePoints, String difficulty) {
        List<PaperManage> questions = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : questionTypeCount.entrySet()) {
            String questionType = entry.getKey();
            int count = entry.getValue();

            for (int i = 0; i < count; i++) {
                // 从指定知识点中随机选择题目
                Map<String, Object> conditions = new HashMap<>();
                conditions.put("questionType", questionType);
                conditions.put("difficulty", difficulty);
                conditions.put("knowledgePoint", knowledgePoints[i % knowledgePoints.length]);

                List<Map<String, Object>> availableQuestions = getAvailableQuestions(conditions);
                if (!availableQuestions.isEmpty()) {
                    Map<String, Object> selectedQuestion = availableQuestions.get(new Random().nextInt(availableQuestions.size()));
                    PaperManage paperManage = convertToPaperManage(selectedQuestion);
                    questions.add(paperManage);
                }
            }
        }

        return questions;
    }

    // 根据难度组卷
    private List<PaperManage> generateByDifficulty(Integer examCode, Map<String, Integer> questionTypeCount, String difficulty) {
        List<PaperManage> questions = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : questionTypeCount.entrySet()) {
            String questionType = entry.getKey();
            int count = entry.getValue();

            Map<String, Object> conditions = new HashMap<>();
            conditions.put("questionType", questionType);
            conditions.put("difficulty", difficulty);

            List<Map<String, Object>> availableQuestions = getAvailableQuestions(conditions);

            // 随机选择指定数量的题目
            Collections.shuffle(availableQuestions);
            for (int i = 0; i < Math.min(count, availableQuestions.size()); i++) {
                PaperManage paperManage = convertToPaperManage(availableQuestions.get(i));
                questions.add(paperManage);
            }
        }

        return questions;
    }

    // 随机组卷
    private List<PaperManage> generateRandom(Integer examCode, Map<String, Integer> questionTypeCount, String difficulty) {
        List<PaperManage> questions = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : questionTypeCount.entrySet()) {
            String questionType = entry.getKey();
            int count = entry.getValue();

            Map<String, Object> conditions = new HashMap<>();
            conditions.put("questionType", questionType);
            if (!"all".equals(difficulty)) {
                conditions.put("difficulty", difficulty);
            }

            List<Map<String, Object>> availableQuestions = getAvailableQuestions(conditions);

            // 随机选择题目
            Collections.shuffle(availableQuestions);
            for (int i = 0; i < Math.min(count, availableQuestions.size()); i++) {
                PaperManage paperManage = convertToPaperManage(availableQuestions.get(i));
                questions.add(paperManage);
            }
        }

        return questions;
    }

    // 转换为PaperManage对象
    private PaperManage convertToPaperManage(Map<String, Object> question) {
        PaperManage paperManage = new PaperManage();
        String questionType = (String) question.get("questionType");
        // 转换题型: "multi"->1, "judge"->3, "fill"->2
        int typeValue = 1; // 默认选择题
        if ("judge".equals(questionType)) {
            typeValue = 3;
        } else if ("fill".equals(questionType)) {
            typeValue = 2;
        }
        paperManage.setQuestionType(typeValue);
        paperManage.setQuestionId((Integer) question.get("questionId"));
        return paperManage;
    }

    // 生成新的paperId
    private Integer generateNewPaperId() {
        // 这里应该查询数据库获取最大的paperId并加1
        return 1000 + new Random().nextInt(9000);
    }

    // 更新考试的paperId
    private int updateExamPaperId(Integer examCode, Integer paperId) {
        // 这里应该调用examManageMapper更新examCode对应的paperId
        return 1;
    }
}