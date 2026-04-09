package com.rabbiter.oes.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbiter.oes.entity.ApiResult;
import com.rabbiter.oes.entity.Score;
import com.rabbiter.oes.entity.Student;
import com.rabbiter.oes.serviceimpl.ScoreServiceImpl;
import com.rabbiter.oes.serviceimpl.StudentServiceImpl;
import com.rabbiter.oes.util.ApiResultHandler;
import com.rabbiter.oes.util.PdfExportUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ScoreController {
    @Autowired
    private ScoreServiceImpl scoreService;

    @Autowired
    private StudentServiceImpl studentService;

    @GetMapping("/scores")
    public ApiResult findAll() {
        List<Score> res = scoreService.findAll();
        return ApiResultHandler.buildApiResult(200,"查询所有学生成绩",res);
    }

    @GetMapping("/score/{page}/{size}/{studentId}")
    public ApiResult findById(@PathVariable("page") Integer page, @PathVariable("size") Integer size, @PathVariable("studentId") Integer studentId) {
        Page<Score> scorePage = new Page<>(page, size);
        IPage<Score> res = scoreService.findById(scorePage, studentId);
        return ApiResultHandler.buildApiResult(200, "根据ID查询成绩", res);
    }

    @GetMapping("/score/{studentId}")
    public ApiResult findById(@PathVariable("studentId") Integer studentId) {
        List<Score> res = scoreService.findById(studentId);
        if (!res.isEmpty()) {
            return ApiResultHandler.buildApiResult(200, "根据ID查询成绩", res);
        } else {
            return ApiResultHandler.buildApiResult(400, "ID不存在", res);
        }
    }

    @PostMapping("/score")
    public ApiResult add(@RequestBody Score score) {
        int res = scoreService.add(score);
        if (res == 0) {
            return ApiResultHandler.buildApiResult(400,"成绩添加失败",res);
        }else {
            return ApiResultHandler.buildApiResult(200,"成绩添加成功",res);
        }
    }

    @GetMapping("/scores/{examCode}")
    public ApiResult findByExamCode(@PathVariable("examCode") Integer examCode) {
        List<Score> scores = scoreService.findByExamCode(examCode);
        return ApiResultHandler.buildApiResult(200,"查询成功",scores);
    }

    /**
     * 导出学生成绩表PDF（包含学生信息和成绩）
     */
    @GetMapping("/scores/pdf")
    public void exportAllStudentsScorePdf(HttpServletResponse response) {
        try {
            System.out.println("开始导出所有学生成绩PDF...");

            // 获取所有学生
            Page<Student> studentPage = new Page<>(1, 9999);
            IPage<Student> studentData = studentService.findAll(studentPage, "@", "@", "@", "@", "@", "@");
            List<Student> students = studentData.getRecords();

            // 获取所有成绩
            List<Score> allScores = scoreService.findAll();

            // 按学生ID分组成绩
            Map<Integer, List<Score>> scoresByStudent = allScores.stream()
                    .collect(Collectors.groupingBy(Score::getStudentId));

            // 构建PDF数据
            List<Map<String, Object>> pdfData = new ArrayList<>();
            for (Student student : students) {
                Map<String, Object> studentDataMap = new HashMap<>();
                studentDataMap.put("studentId", student.getStudentId());
                studentDataMap.put("studentName", student.getStudentName());
                studentDataMap.put("institute", student.getInstitute());
                studentDataMap.put("major", student.getMajor());
                studentDataMap.put("grade", student.getGrade());
                studentDataMap.put("clazz", student.getClazz());

                // 获取该学生的成绩
                List<Score> studentScores = scoresByStudent.getOrDefault(student.getStudentId(), new ArrayList<>());
                List<Map<String, Object>> scoreList = new ArrayList<>();
                for (Score score : studentScores) {
                    Map<String, Object> scoreMap = new HashMap<>();
                    scoreMap.put("subject", score.getSubject());
                    scoreMap.put("etScore", score.getEtScore());
                    scoreMap.put("ptScore", score.getPtScore());
                    scoreMap.put("answerDate", score.getAnswerDate());
                    scoreList.add(scoreMap);
                }
                studentDataMap.put("scores", scoreList);
                pdfData.add(studentDataMap);
            }

            System.out.println("查询到学生数量: " + students.size() + ", 总成绩记录: " + allScores.size());
            System.out.println("生成PDF文档...");

            PDDocument doc = PdfExportUtil.exportAllStudentsScoreReport(pdfData, "学生成绩报表");

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=all_students_score_report.pdf");
            OutputStream out = response.getOutputStream();
            doc.save(out);
            doc.close();
            out.flush();
            System.out.println("PDF导出成功!");
        } catch (Exception e) {
            System.err.println("PDF导出失败: " + e.getMessage());
            e.printStackTrace();
            try {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("PDF导出失败: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 导出学生成绩PDF
     * @param studentId 学生ID
     * @param response HTTP响应
     */
    @GetMapping("/score/pdf/{studentId}")
    public void exportScorePdf(@PathVariable("studentId") Integer studentId, HttpServletResponse response) {
        try {
            List<Score> scores = scoreService.findById(studentId);
            // 转换为Map列表
            List<Map<String, Object>> data = scores.stream().map(score -> {
                Map<String, Object> map = new HashMap<>();
                map.put("answerDate", score.getAnswerDate());
                map.put("subject", score.getSubject());
                map.put("etScore", score.getEtScore());
                map.put("ptScore", score.getPtScore());
                return map;
            }).collect(Collectors.toList());

            PDDocument doc = PdfExportUtil.exportScoreTable(data, "学生成绩表", "成绩表");

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=score_" + studentId + ".pdf");
            OutputStream out = response.getOutputStream();
            doc.save(out);
            doc.close();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出成绩报告单PDF
     */
    @GetMapping("/score/report/pdf")
    public void exportScoreReportPdf(
            @RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) Integer score,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String studentName,
            HttpServletResponse response) {
        try {
            Map<String, Object> scoreData = new HashMap<>();
            scoreData.put("studentId", studentId);
            scoreData.put("score", score);
            scoreData.put("startTime", startTime);
            scoreData.put("endTime", endTime);
            scoreData.put("subject", subject);
            scoreData.put("studentName", studentName);

            PDDocument doc = PdfExportUtil.exportScoreReport(scoreData);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=score_report.pdf");
            OutputStream out = response.getOutputStream();
            doc.save(out);
            doc.close();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
