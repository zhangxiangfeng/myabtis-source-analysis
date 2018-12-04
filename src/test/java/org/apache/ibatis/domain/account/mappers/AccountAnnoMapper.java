package org.apache.ibatis.domain.account.mappers;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.domain.account.Account;

import java.util.List;

public interface AccountAnnoMapper {


    @Select({
            "select * from test_table"
    })
    List<Account> selectAll();
}
