package com.rabbiter.oes.mapper;

import com.rabbiter.oes.entity.PaperManage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PaperMapper {
    @Select("select paperId, questionType,questionId from paper_manage")
    List<PaperManage> findAll();

    @Select("select paperId, questionType,questionId from paper_manage where paperId = #{paperId}")
    List<PaperManage> findById(Integer paperId);

    @Insert("insert into paper_manage(paperId,questionType,questionId) values " +
            "(#{paperId},#{questionType},#{questionId})")
    int add(PaperManage paperManage);

    @Delete("delete from paper_manage where paperId = #{paperId} and questionType = #{type} and questionId = #{questionId}")
    void delete(@Param("paperId") String paperId, @Param("type") String type, @Param("questionId") String questionId);

    /**
     * 根据试卷id删除题目关联
     *
     * @param paperId 试卷id
     */
    @Delete("DELETE FROM paper_manage WHERE paperId = #{paperId}")
    void deleteByPaperId(@Param("paperId") Integer paperId);

    /**
     * 根据考试编号查询试卷
     */
    @Select("select * from paper_manage where paperId in (select paperId from exam_manage where examCode = #{examCode})")
    List<PaperManage> findByExamCode(@Param("examCode") Integer examCode);

    /**
     * 根据试卷ID查询所有题目
     */
    @Select("select * from paper_manage where paperId = #{paperId}")
    List<PaperManage> findByPaperId(@Param("paperId") Integer paperId);

    /**
     * 查询考试的最大试卷ID
     */
    @Select("select max(paperId) from paper_manage where paperId in (select paperId from exam_manage)")
    Integer getMaxPaperId();
}
