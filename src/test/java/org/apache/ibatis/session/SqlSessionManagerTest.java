/**
 * Copyright 2009-2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ibatis.session;

import com.alibaba.fastjson.JSON;
import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.domain.account.Account;
import org.apache.ibatis.domain.account.mappers.AccountMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.Reader;
import java.util.List;

import static org.junit.Assert.*;

public class SqlSessionManagerTest extends BaseDataTest {

    private final static Log log = LogFactory.getLog(SqlSessionManagerTest.class);

    private static SqlSessionManager manager;

    @BeforeClass
    public static void setup() throws Exception {
        final String resource = "cn/openread/test/simple/xml/MapperConfig.xml";
        final Reader reader = Resources.getResourceAsReader(resource);
        manager = SqlSessionManager.newInstance(reader);
    }

    /**
     * 为了测试mapper匹配不存在的异常
     */
    @Test
    public void shouldThrowExceptionIfMappedStatementDoesNotExistAndSqlSessionIsOpen() throws Exception {
        try {
            manager.startManagedSession();
            manager.selectList("ThisStatementDoesNotExist");
            fail("Expected exception to be thrown due to statement that does not exist.");
        } catch (PersistenceException e) {
            assertTrue(e.getMessage().contains("does not contain value for ThisStatementDoesNotExist"));
        } finally {
            manager.close();
        }
    }

    /**
     * 测试commit后查询
     */
    @Test
    public void shouldCommitInsertedAuthor() throws Exception {
        try {
            manager.startManagedSession();
            AccountMapper mapper = manager.getMapper(AccountMapper.class);
            Account account = new Account();
            account.setName("张海峰");
            account.setBlance(600L);
            log.debug("插入前:" + JSON.toJSONString(account));
            mapper.insertAuthorReturnPrimaryKey(account);
            log.debug("插入后:" + JSON.toJSONString(account));
            manager.commit();
            Account newAccount = mapper.selectById(account.getTableId());
            log.debug("查询后:" + JSON.toJSONString(newAccount));
        } catch (Exception e) {
            log.debug("Exception : " + e.getMessage());
        } finally {
            manager.close();
        }
    }

    @After
    public void testSelectAll() {
        try {
            manager.startManagedSession();
            AccountMapper mapper = manager.getMapper(AccountMapper.class);
            List<Account> accounts = mapper.selectAll();
            log.debug(JSON.toJSONString(accounts));
        } finally {
            manager.close();
        }
    }

    @Test
    public void shouldRollbackInsertedAuthor() throws Exception {
        try {
            manager.startManagedSession(false);
            manager.getConnection().getAutoCommit();
            AccountMapper mapper = manager.getMapper(AccountMapper.class);
            Account account = new Account();
            account.setName("张海峰");
            account.setBlance(600L);
            log.debug("插入前:" + JSON.toJSONString(account));
            mapper.insertAuthorReturnPrimaryKey(account);
            manager.rollback();
            log.debug("插入后:" + JSON.toJSONString(account));
            Account actual = mapper.selectById(account.getTableId());
            log.debug("查询后:" + JSON.toJSONString(actual));
        } finally {
            manager.close();
        }
    }

    @Test
    public void shouldImplicitlyRollbackInsertedAuthor() throws Exception {
        manager.startManagedSession();
        AccountMapper mapper = manager.getMapper(AccountMapper.class);
        Account account = new Account();
        account.setName("张海峰");
        account.setBlance(600L);

        mapper.insertAuthorReturnPrimaryKey(account);
        manager.close();
        Account actual = mapper.selectById(account.getTableId());
        assertNull(actual);
    }

}
