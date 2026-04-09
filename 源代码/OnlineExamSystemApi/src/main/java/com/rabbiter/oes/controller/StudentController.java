package com.rabbiter.oes.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbiter.oes.entity.ApiResult;
import com.rabbiter.oes.entity.Student;
import com.rabbiter.oes.serviceimpl.StudentServiceImpl;
import com.rabbiter.oes.util.ApiResultHandler;
import com.rabbiter.oes.util.PdfExportUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class StudentController {

    @Autowired
    private StudentServiceImpl studentService;

    @GetMapping("/students/{page}/{size}/{name}/{grade}/{tel}/{institute}/{major}/{clazz}")
    public ApiResult findAll(@PathVariable Integer page, @PathVariable Integer size,
                             @PathVariable  String name, @PathVariable String grade,
                             @PathVariable String tel, @PathVariable String institute,
                             @PathVariable String major, @PathVariable String clazz) {
        Page<Student> studentPage = new Page<>(page,size);
        IPage<Student> res = studentService.findAll(
                studentPage, name, grade, tel, institute, major, clazz
        );
        return  ApiResultHandler.buildApiResult(200,"分页查询所有学生",res);
    }

    @GetMapping("/student/{studentId}")
    public ApiResult findById(@PathVariable("studentId") Integer studentId) {
        Student res = studentService.findById(studentId);
        if (res != null) {
        return ApiResultHandler.buildApiResult(200,"请求成功",res);
        } else {
            return ApiResultHandler.buildApiResult(404,"查询的用户不存在",null);
        }
    }

    @DeleteMapping("/student/{studentId}")
    public ApiResult deleteById(@PathVariable("studentId") Integer studentId) {
        return ApiResultHandler.buildApiResult(200,"删除成功",studentService.deleteById(studentId));
    }

    @PutMapping("/studentPWD")
    public ApiResult updatePwd(@RequestBody Student student) {
        studentService.updatePwd(student);
        return ApiResultHandler.buildApiResult(200,"密码更新成功",null);
    }
    @PutMapping("/student")
    public ApiResult update(@RequestBody Student student) {
        int res = studentService.update(student);
        if (res != 0) {
            return ApiResultHandler.buildApiResult(200,"更新成功",res);
        }
        return ApiResultHandler.buildApiResult(400,"更新失败",res);
    }

    @PostMapping("/student")
    public ApiResult add(@RequestBody Student student) {
        int res = studentService.add(student);
        if (res == 1) {
            return ApiResultHandler.buildApiResult(200,"添加成功",null);
        }else {
            return ApiResultHandler.buildApiResult(400,"添加失败",null);
        }
    }

    /**
     * 导出学生列表PDF
     */
    @GetMapping("/students/pdf")
    public void exportStudentsPdf(
            @RequestParam(defaultValue = "@") String name,
            @RequestParam(defaultValue = "@") String grade,
            @RequestParam(defaultValue = "@") String tel,
            @RequestParam(defaultValue = "@") String institute,
            @RequestParam(defaultValue = "@") String major,
            @RequestParam(defaultValue = "@") String clazz,
            HttpServletResponse response) {
        try {
            System.out.println("开始导出PDF...");
            // 获取全部数据（不分页）
            Page<Student> studentPage = new Page<>(1, 9999);
            IPage<Student> pageData = studentService.findAll(
                    studentPage, name, grade, tel, institute, major, clazz
            );
            List<Student> students = pageData.getRecords();
            System.out.println("查询到学生数量: " + students.size());

            // 转换为Map列表
            List<Map<String, Object>> data = students.stream().map(student -> {
                Map<String, Object> map = new HashMap<>();
                map.put("studentId", student.getStudentId());
                map.put("studentName", student.getStudentName());
                map.put("institute", student.getInstitute());
                map.put("major", student.getMajor());
                map.put("grade", student.getGrade());
                map.put("clazz", student.getClazz());
                map.put("sex", student.getSex());
                map.put("tel", student.getTel());
                return map;
            }).collect(Collectors.toList());

            System.out.println("生成PDF文档...");
            PDDocument doc = PdfExportUtil.exportStudentList(data, "Student List");

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=students_list.pdf");
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
}
