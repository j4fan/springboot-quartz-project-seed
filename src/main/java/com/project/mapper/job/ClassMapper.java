package com.project.mapper.job;

import com.project.domin.dto.ClassFullNames;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author fanqingyuan
 * init class name of jobs into database
 */

@Repository
public interface ClassMapper {

    @Select("truncate data_stat.qrtz_class_name")
    void truncateNames();

    /**
     * 插入Job继承类类名
     *
     * @param nameList
     */
    void insertClassNames(@Param("nameList") List<ClassFullNames> nameList);

    @Select("select class_full_name,class_simple_name from data_stat.qrtz_class_name")
    List<ClassFullNames> getClassNames();


}
