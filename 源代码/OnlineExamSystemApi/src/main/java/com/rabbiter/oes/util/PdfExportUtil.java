package com.rabbiter.oes.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * PDF导出工具类
 */
public class PdfExportUtil {

    // 横向A4页面尺寸 (宽595，高842)
    private static final PDRectangle A4_LANDSCAPE = new PDRectangle(595, 842);

    /**
     * 加载中文字体
     */
    private static PDType0Font loadChineseFont(PDDocument doc) throws IOException {
        // 尝试加载Windows系统字体
        File fontFile = new File("C:/Windows/Fonts/simhei.ttf");
        if (fontFile.exists()) {
            return PDType0Font.load(doc, fontFile);
        }
        fontFile = new File("C:/Windows/Fonts/msyh.ttc"); // 微软雅黑
        if (fontFile.exists()) {
            return PDType0Font.load(doc, fontFile);
        }
        fontFile = new File("C:/Windows/Fonts/simsun.ttc"); // 宋体
        if (fontFile.exists()) {
            return PDType0Font.load(doc, fontFile);
        }
        return null;
    }

    /**
     * 导出成绩表为PDF
     */
    public static PDDocument exportScoreTable(List<Map<String, Object>> scores, String title, String filename) throws IOException {
        PDDocument doc = new PDDocument();
        PDType0Font chineseFont = loadChineseFont(doc);

        PDPage page = new PDPage(A4_LANDSCAPE);
        doc.addPage(page);

        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();
        float margin = 50;
        float yStart = pageHeight - margin;

        PDPageContentStream contentStream = new PDPageContentStream(doc, page);

        // 绘制标题
        if (chineseFont != null) {
            contentStream.setFont(chineseFont, 18);
        } else {
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
        }
        contentStream.beginText();
        contentStream.newLineAtOffset((pageWidth - title.length() * 9) / 2, yStart - 20);
        contentStream.showText(title);
        contentStream.endText();

        // 表格参数
        float tableY = yStart - 50;
        float rowHeight = 25;
        float cellMargin = 10;

        String[] headers = {"考试日期", "课程名称", "考试分数", "是否及格"};
        float[] colWidths = {100, 200, 80, 80};
        float tableWidth = colWidths[0] + colWidths[1] + colWidths[2] + colWidths[3];
        float tableX = (pageWidth - tableWidth) / 2;

        // 绘制表头背景
        contentStream.setNonStrokingColor(Color.LIGHT_GRAY);
        contentStream.addRect(tableX, tableY - rowHeight, tableWidth, rowHeight);
        contentStream.fill();

        // 绘制表头文字
        contentStream.setNonStrokingColor(Color.BLACK);
        if (chineseFont != null) {
            contentStream.setFont(chineseFont, 12);
        } else {
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        }

        contentStream.beginText();
        contentStream.newLineAtOffset(tableX + cellMargin, tableY - rowHeight + 8);
        for (int i = 0; i < headers.length; i++) {
            contentStream.showText(headers[i]);
            contentStream.newLineAtOffset(colWidths[i], 0);
        }
        contentStream.endText();

        // 绘制数据行
        tableY -= rowHeight;
        int rowNum = 0;
        for (Map<String, Object> score : scores) {
            if (tableY - rowHeight < margin) {
                contentStream.close();
                page = new PDPage(A4_LANDSCAPE);
                doc.addPage(page);
                contentStream = new PDPageContentStream(doc, page);
                tableY = pageHeight - margin - 50;
            }

            if (rowNum % 2 == 0) {
                contentStream.setNonStrokingColor(new Color(245, 245, 245));
                contentStream.addRect(tableX, tableY - rowHeight, tableWidth, rowHeight);
                contentStream.fill();
            }

            contentStream.setStrokingColor(Color.GRAY);
            float currentX = tableX;
            for (float width : colWidths) {
                contentStream.addRect(currentX, tableY - rowHeight, width, rowHeight);
                currentX += width;
            }
            contentStream.stroke();

            contentStream.setNonStrokingColor(Color.BLACK);
            if (chineseFont != null) {
                contentStream.setFont(chineseFont, 10);
            } else {
                contentStream.setFont(PDType1Font.HELVETICA, 10);
            }

            String answerDate = score.get("answerDate") != null ? score.get("answerDate").toString() : "";
            String subject = score.get("subject") != null ? score.get("subject").toString() : "";
            String etScore = score.get("etScore") != null ? score.get("etScore").toString() : "0";
            String ptScore = score.get("ptScore") != null ? (score.get("ptScore").toString().equals("1") ? "及格" : "不及格") : "不及格";

            contentStream.beginText();
            contentStream.newLineAtOffset(tableX + cellMargin, tableY - rowHeight + 8);
            contentStream.showText(truncateText(answerDate, 12));
            contentStream.newLineAtOffset(colWidths[0], 0);
            contentStream.showText(truncateText(subject, 25));
            contentStream.newLineAtOffset(colWidths[1], 0);
            contentStream.showText(etScore);
            contentStream.newLineAtOffset(colWidths[2], 0);
            contentStream.showText(ptScore);
            contentStream.endText();

            tableY -= rowHeight;
            rowNum++;
        }

        contentStream.setNonStrokingColor(Color.GRAY);
        contentStream.setFont(PDType1Font.HELVETICA, 8);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, margin - 10);
        contentStream.showText("Export: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
        contentStream.endText();

        contentStream.close();
        return doc;
    }

    /**
     * 导出学生列表为PDF
     */
    public static PDDocument exportStudentList(List<Map<String, Object>> students, String title) throws IOException {
        PDDocument doc = new PDDocument();
        PDType0Font chineseFont = loadChineseFont(doc);

        PDPage page = new PDPage(A4_LANDSCAPE);
        doc.addPage(page);

        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();
        float margin = 50;
        float yStart = pageHeight - margin;

        PDPageContentStream contentStream = new PDPageContentStream(doc, page);

        // 绘制标题
        if (chineseFont != null) {
            contentStream.setFont(chineseFont, 18);
        } else {
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
        }
        contentStream.beginText();
        contentStream.newLineAtOffset((pageWidth - title.length() * 9) / 2, yStart - 20);
        contentStream.showText(title);
        contentStream.endText();

        float tableY = yStart - 50;
        float rowHeight = 25;
        float cellMargin = 10;

        String[] headers = {"学号", "姓名", "学院", "专业", "年级", "班级", "性别", "联系方式"};
        float[] colWidths = {60, 80, 100, 100, 50, 50, 40, 80};
        float tableWidth = 0;
        for (float w : colWidths) tableWidth += w;
        float tableX = (pageWidth - tableWidth) / 2;

        // 绘制表头
        contentStream.setNonStrokingColor(Color.LIGHT_GRAY);
        contentStream.addRect(tableX, tableY - rowHeight, tableWidth, rowHeight);
        contentStream.fill();

        contentStream.setNonStrokingColor(Color.BLACK);
        if (chineseFont != null) {
            contentStream.setFont(chineseFont, 10);
        } else {
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        }

        contentStream.beginText();
        contentStream.newLineAtOffset(tableX + cellMargin, tableY - rowHeight + 8);
        for (int i = 0; i < headers.length; i++) {
            contentStream.showText(headers[i]);
            if (i < headers.length - 1) {
                contentStream.newLineAtOffset(colWidths[i], 0);
            }
        }
        contentStream.endText();

        // 绘制数据行
        tableY -= rowHeight;
        for (int i = 0; i < students.size(); i++) {
            if (tableY - rowHeight < margin) {
                contentStream.close();
                page = new PDPage(A4_LANDSCAPE);
                doc.addPage(page);
                contentStream = new PDPageContentStream(doc, page);
                tableY = pageHeight - margin - 50;
            }

            if (i % 2 == 0) {
                contentStream.setNonStrokingColor(new Color(245, 245, 245));
                contentStream.addRect(tableX, tableY - rowHeight, tableWidth, rowHeight);
                contentStream.fill();
            }

            contentStream.setStrokingColor(Color.GRAY);
            float currentX = tableX;
            for (float width : colWidths) {
                contentStream.addRect(currentX, tableY - rowHeight, width, rowHeight);
                currentX += width;
            }
            contentStream.stroke();

            contentStream.setNonStrokingColor(Color.BLACK);
            if (chineseFont != null) {
                contentStream.setFont(chineseFont, 8);
            } else {
                contentStream.setFont(PDType1Font.HELVETICA, 8);
            }

            Map<String, Object> student = students.get(i);
            String[] values = {
                student.get("studentId") != null ? student.get("studentId").toString() : "",
                student.get("studentName") != null ? truncateText(student.get("studentName").toString(), 10) : "",
                student.get("institute") != null ? truncateText(student.get("institute").toString(), 12) : "",
                student.get("major") != null ? truncateText(student.get("major").toString(), 12) : "",
                student.get("grade") != null ? student.get("grade").toString() : "",
                student.get("clazz") != null ? student.get("clazz").toString() : "",
                student.get("sex") != null ? student.get("sex").toString() : "",
                student.get("tel") != null ? student.get("tel").toString() : ""
            };

            contentStream.beginText();
            contentStream.newLineAtOffset(tableX + cellMargin, tableY - rowHeight + 8);
            for (int j = 0; j < values.length; j++) {
                contentStream.showText(values[j]);
                if (j < values.length - 1) {
                    contentStream.newLineAtOffset(colWidths[j], 0);
                }
            }
            contentStream.endText();

            tableY -= rowHeight;
        }

        contentStream.close();
        return doc;
    }

    /**
     * 导出成绩报告单为PDF
     */
    public static PDDocument exportScoreReport(Map<String, Object> scoreData) throws IOException {
        PDDocument doc = new PDDocument();
        PDType0Font chineseFont = loadChineseFont(doc);

        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();
        float centerX = pageWidth / 2;
        float margin = 50;

        PDPageContentStream contentStream = new PDPageContentStream(doc, page);

        // 标题
        if (chineseFont != null) {
            contentStream.setFont(chineseFont, 22);
        } else {
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 22);
        }
        contentStream.beginText();
        contentStream.newLineAtOffset(centerX - 80, pageHeight - margin - 30);
        contentStream.showText("考试成绩报告单");
        contentStream.endText();

        // 分隔线
        contentStream.setLineWidth(1);
        contentStream.moveTo(margin, pageHeight - margin - 50);
        contentStream.lineTo(pageWidth - margin, pageHeight - margin - 50);
        contentStream.stroke();

        // 学生信息
        float y = pageHeight - margin - 80;
        if (chineseFont != null) {
            contentStream.setFont(chineseFont, 12);
        } else {
            contentStream.setFont(PDType1Font.HELVETICA, 12);
        }

        if (scoreData.get("studentId") != null) {
            contentStream.beginText();
            contentStream.newLineAtOffset(margin + 20, y);
            contentStream.showText("学号: " + scoreData.get("studentId").toString());
            contentStream.endText();
            y -= 20;
        }
        if (scoreData.get("studentName") != null) {
            contentStream.beginText();
            contentStream.newLineAtOffset(margin + 20, y);
            contentStream.showText("姓名: " + scoreData.get("studentName").toString());
            contentStream.endText();
            y -= 20;
        }
        if (scoreData.get("subject") != null) {
            contentStream.beginText();
            contentStream.newLineAtOffset(margin + 20, y);
            contentStream.showText("考试科目: " + scoreData.get("subject").toString());
            contentStream.endText();
            y -= 30;
        }

        // 成绩框
        contentStream.setNonStrokingColor(new Color(240, 240, 240));
        contentStream.addRect(margin + 20, y - 20, pageWidth - 2 * margin - 40, 40);
        contentStream.fill();

        contentStream.setNonStrokingColor(new Color(0, 100, 200));
        if (chineseFont != null) {
            contentStream.setFont(chineseFont, 16);
        } else {
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
        }
        contentStream.beginText();
        contentStream.newLineAtOffset(centerX - 60, y);
        String scoreText = "本次考试成绩: " + (scoreData.get("score") != null ? scoreData.get("score").toString() : "0") + " 分";
        contentStream.showText(scoreText);
        contentStream.endText();

        y -= 60;

        // 时间信息
        contentStream.setNonStrokingColor(Color.BLACK);
        if (chineseFont != null) {
            contentStream.setFont(chineseFont, 12);
        } else {
            contentStream.setFont(PDType1Font.HELVETICA, 12);
        }
        if (scoreData.get("startTime") != null) {
            contentStream.beginText();
            contentStream.newLineAtOffset(margin + 20, y);
            contentStream.showText("开始时间: " + scoreData.get("startTime").toString());
            contentStream.endText();
            y -= 20;
        }
        if (scoreData.get("endTime") != null) {
            contentStream.beginText();
            contentStream.newLineAtOffset(margin + 20, y);
            contentStream.showText("结束时间: " + scoreData.get("endTime").toString());
            contentStream.endText();
            y -= 30;
        }

        // 及格状态
        int scoreValue = scoreData.get("score") != null ? Integer.parseInt(scoreData.get("score").toString()) : 0;
        boolean passed = scoreValue >= 60;
        if (chineseFont != null) {
            contentStream.setFont(chineseFont, 14);
        } else {
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        }
        contentStream.beginText();
        contentStream.newLineAtOffset(centerX - 40, y);
        if (passed) {
            contentStream.setNonStrokingColor(new Color(0, 150, 0));
            contentStream.showText("成绩合格");
        } else {
            contentStream.setNonStrokingColor(new Color(200, 0, 0));
            contentStream.showText("成绩不合格");
        }
        contentStream.endText();

        // 页脚
        contentStream.setNonStrokingColor(Color.GRAY);
        if (chineseFont != null) {
            contentStream.setFont(chineseFont, 10);
        } else {
            contentStream.setFont(PDType1Font.HELVETICA, 10);
        }
        contentStream.beginText();
        contentStream.newLineAtOffset(centerX - 50, margin);
        contentStream.showText("导出时间: " + new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        contentStream.endText();

        contentStream.close();
        return doc;
    }

    private static String truncateText(String text, int maxChars) {
        if (text == null) return "";
        if (text.length() > maxChars) {
            return text.substring(0, maxChars) + "...";
        }
        return text;
    }

    /**
     * 导出所有学生成绩报表（学生信息 + 成绩）
     * @param data 学生列表，每个学生包含成绩列表
     * @param title 标题
     * @return PDDocument对象
     */
    @SuppressWarnings("unchecked")
    public static PDDocument exportAllStudentsScoreReport(List<Map<String, Object>> data, String title) throws IOException {
        PDDocument doc = new PDDocument();
        PDType0Font chineseFont = loadChineseFont(doc);
        PDType1Font helvetica = PDType1Font.HELVETICA;
        PDType1Font helveticaBold = PDType1Font.HELVETICA_BOLD;

        float margin = 40;
        float pageWidth = PDRectangle.A4.getWidth();
        float pageHeight = PDRectangle.A4.getHeight();

        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(doc, page);

        float currentY = pageHeight - margin;

        // 绘制主标题
        if (chineseFont != null) {
            contentStream.setFont(chineseFont, 20);
        } else {
            contentStream.setFont(helveticaBold, 20);
        }
        contentStream.beginText();
        contentStream.newLineAtOffset((pageWidth - title.length() * 10) / 2, currentY);
        contentStream.showText(title);
        contentStream.endText();
        currentY -= 30;

        // 遍历每个学生
        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> student = data.get(i);

            // 检查是否需要新页面
            if (currentY < 200) {
                contentStream.close();
                page = new PDPage(PDRectangle.A4);
                doc.addPage(page);
                contentStream = new PDPageContentStream(doc, page);
                currentY = pageHeight - margin;
            }

            // 绘制学生信息区域背景
            contentStream.setNonStrokingColor(new Color(230, 240, 250));
            contentStream.addRect(margin, currentY - 80, pageWidth - 2 * margin, 80);
            contentStream.fill();

            // 绘制学生基本信息
            if (chineseFont != null) {
                contentStream.setFont(chineseFont, 12);
            } else {
                contentStream.setFont(helveticaBold, 12);
            }
            contentStream.setNonStrokingColor(Color.BLACK);

            String studentId = student.get("studentId") != null ? student.get("studentId").toString() : "";
            String studentName = student.get("studentName") != null ? student.get("studentName").toString() : "";
            String institute = student.get("institute") != null ? student.get("institute").toString() : "";
            String major = student.get("major") != null ? student.get("major").toString() : "";
            String grade = student.get("grade") != null ? student.get("grade").toString() : "";
            String clazz = student.get("clazz") != null ? student.get("clazz").toString() : "";

            contentStream.beginText();
            contentStream.newLineAtOffset(margin + 10, currentY - 20);
            contentStream.showText("学号: " + studentId + "    姓名: " + studentName + "    学院: " + institute);
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(margin + 10, currentY - 40);
            contentStream.showText("专业: " + major + "    年级: " + grade + "    班级: " + clazz);
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(margin + 10, currentY - 60);
            contentStream.showText("--------------------------------------------------------------------------------------------------");
            contentStream.endText();

            currentY -= 90;

            // 获取该学生的成绩列表
            List<Map<String, Object>> scores = (List<Map<String, Object>>) student.get("scores");

            if (scores == null || scores.isEmpty()) {
                // 无成绩
                if (chineseFont != null) {
                    contentStream.setFont(chineseFont, 10);
                } else {
                    contentStream.setFont(helvetica, 10);
                }
                contentStream.setNonStrokingColor(Color.GRAY);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 10, currentY);
                contentStream.showText("暂无考试成绩记录");
                contentStream.endText();
                currentY -= 30;
            } else {
                // 绘制成绩表头
                if (chineseFont != null) {
                    contentStream.setFont(chineseFont, 11);
                } else {
                    contentStream.setFont(helveticaBold, 11);
                }
                contentStream.setNonStrokingColor(Color.DARK_GRAY);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 10, currentY);
                contentStream.showText("考试科目");
                contentStream.newLineAtOffset(150, 0);
                contentStream.showText("考试成绩");
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText("满分");
                contentStream.newLineAtOffset(80, 0);
                contentStream.showText("及格状态");
                contentStream.newLineAtOffset(80, 0);
                contentStream.showText("考试日期");
                contentStream.endText();
                currentY -= 5;

                // 绘制表头下划线
                contentStream.setStrokingColor(Color.LIGHT_GRAY);
                contentStream.setLineWidth(1);
                contentStream.moveTo(margin + 10, currentY);
                contentStream.lineTo(pageWidth - margin - 10, currentY);
                contentStream.stroke();
                currentY -= 20;

                // 绘制每条成绩
                if (chineseFont != null) {
                    contentStream.setFont(chineseFont, 10);
                } else {
                    contentStream.setFont(helvetica, 10);
                }
                contentStream.setNonStrokingColor(Color.BLACK);

                for (Map<String, Object> score : scores) {
                    // 检查是否需要新页面
                    if (currentY < 80) {
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        doc.addPage(page);
                        contentStream = new PDPageContentStream(doc, page);
                        currentY = pageHeight - margin;

                        // 重新绘制表头
                        if (chineseFont != null) {
                            contentStream.setFont(chineseFont, 11);
                        } else {
                            contentStream.setFont(helveticaBold, 11);
                        }
                        contentStream.setNonStrokingColor(Color.DARK_GRAY);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(margin + 10, currentY);
                        contentStream.showText("考试科目");
                        contentStream.newLineAtOffset(150, 0);
                        contentStream.showText("考试成绩");
                        contentStream.newLineAtOffset(100, 0);
                        contentStream.showText("满分");
                        contentStream.newLineAtOffset(80, 0);
                        contentStream.showText("及格状态");
                        contentStream.newLineAtOffset(80, 0);
                        contentStream.showText("考试日期");
                        contentStream.endText();
                        currentY -= 25;
                    }

                    String subject = score.get("subject") != null ? score.get("subject").toString() : "";
                    String etScore = score.get("etScore") != null ? score.get("etScore").toString() : "0";
                    String ptScore = score.get("ptScore") != null ? (score.get("ptScore").toString().equals("1") ? "及格" : "不及格") : "-";
                    String answerDate = score.get("answerDate") != null ? score.get("answerDate").toString() : "-";

                    // 判断是否及格，改变颜色
                    boolean isPass = "及格".equals(ptScore);
                    if (isPass) {
                        contentStream.setNonStrokingColor(new Color(0, 128, 0)); // 绿色
                    } else {
                        contentStream.setNonStrokingColor(new Color(200, 0, 0)); // 红色
                    }

                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin + 10, currentY);
                    contentStream.showText(truncateText(subject, 20));
                    contentStream.newLineAtOffset(150, 0);
                    contentStream.showText(etScore);
                    contentStream.newLineAtOffset(100, 0);
                    contentStream.showText("100"); // 满分，固定100
                    contentStream.newLineAtOffset(80, 0);
                    contentStream.showText(ptScore);
                    contentStream.newLineAtOffset(80, 0);
                    contentStream.showText(answerDate);
                    contentStream.endText();

                    currentY -= 22;
                }
            }

            // 学生之间添加分隔线
            currentY -= 20;
            contentStream.setStrokingColor(new Color(180, 180, 180));
            contentStream.setLineWidth(0.5f);
            contentStream.moveTo(margin, currentY);
            contentStream.lineTo(pageWidth - margin, currentY);
            contentStream.stroke();
            currentY -= 15;
        }

        // 绘制页脚
        contentStream.setNonStrokingColor(Color.GRAY);
        if (chineseFont != null) {
            contentStream.setFont(chineseFont, 9);
        } else {
            contentStream.setFont(helvetica, 9);
        }
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, 30);
        contentStream.showText("导出时间: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
                + "    共 " + data.size() + " 名学生");
        contentStream.endText();

        contentStream.close();
        return doc;
    }
}