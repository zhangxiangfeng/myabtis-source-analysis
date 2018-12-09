package org.apache.ibatis.domain.account.mappers;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.domain.account.Account;

import java.util.List;

public interface AccountAnnoMapper {


    @Select({
            "select * from test_table"
    })
    List<Account> selectAll();
//
//    @Select({
//            "select * from test_table where tableId=#{tableId}"
//    })
//    List<Account> selectAll(Integer tableId);


    @Insert({
            "<script>",
            "insert into test_table (name , blance)",
            "values",
            "<foreach collection='params' item='item' index='index' separator=','>",
            "(#{item.name,jdbcType=VARCHAR}, #{item.blance,jdbcType=BIGINT})",
            "</foreach>",
            "</script>"
    })
    void batchInsert(@Param(value = "params") List<Account> params);
}
