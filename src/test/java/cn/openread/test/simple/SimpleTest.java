package cn.openread.test.simple;

import cn.openread.test.simple.classloader.SimonClassLoader;
import com.alibaba.fastjson.JSON;
import org.apache.ibatis.domain.account.Account;
import org.apache.ibatis.domain.account.mappers.AccountAnnoMapper;
import org.apache.ibatis.domain.account.mappers.AccountMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;

/**
 * test
 *
 * @author simon
 * @create 2018-05-21 下午6:22
 **/
@SuppressWarnings("ALL")
public class SimpleTest {
    private final Log log = LogFactory.getLog(SimpleTest.class);

    private SqlSession sqlSession = null;

    @Before
    public void before() throws IOException {
        //step 1.读取配置文件
        Reader reader = Resources.getResourceAsReader("cn/openread/test/simple/xml/MapperConfig.xml");
        sqlSession = SqlSessionManager.newInstance(reader).openSession();
//        new SqlSessionFactoryBuilder().build(reader);
    }

    //    @Before
    public void beforeForClassLoader() throws IOException {
        //step 1.自定义类加载器
        SimonClassLoader classLoader = new SimonClassLoader("C:\\SortWare\\simon-projects\\labs\\mybatis-3\\lib");

        //step 2.设置默认的编码
        Resources.setCharset(Charset.forName("UTF-8"));

        //step 3.读取资源文件
        Reader reader = Resources.getResourceAsReader(classLoader, "MapperConfig.xml");

        //step 4.解析读取的文件流 && new一个SqlManager
        SqlSessionManager sqlSessionManager = SqlSessionManager.newInstance(reader);
        sqlSession = sqlSessionManager.openSession();
    }

    @Test
    public void testSelectById() throws IOException {
        try {
            AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);
            Account account = accountMapper.selectById(10002L);
            log.debug("查询结果:" + JSON.toJSONString(account));
//            sqlSession.commit();
            account = accountMapper.selectById(10002L);
            log.debug("查询结果:" + JSON.toJSONString(account));

            sqlSession.commit();
            account = accountMapper.selectById(10002L);
            log.debug("查询结果:" + JSON.toJSONString(account));

        } finally {
//            sqlSession.close();
        }
    }

    @Test
    public void testSelectAllByXMLQuery() throws IOException {
        try {
            AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);
            List<Account> accounts = accountMapper.selectAll();
            log.debug("查询结果:" + JSON.toJSONString(accounts));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSelectAllByAnnoQuery() throws IOException {
        try {
            //1.通过如下方式
            //sqlSession.getConfiguration().getMapperRegistry().addMapper(AccountAnnoMapper.class);

            //2.通过如下方式
            //<mapper class="org.apache.ibatis.domain.account.mappers.AccountAnnoMapper"/>
            AccountAnnoMapper accountMapper = sqlSession.getMapper(AccountAnnoMapper.class);
            List<Account> accounts = accountMapper.selectAll();
            log.debug("查询结果:" + JSON.toJSONString(accounts));
        } finally {
            sqlSession.close();
        }
    }

    @Ignore
    @Test
    public void testLogColor() {
        log.trace("这是一条 trace 信息");
        log.debug("这是一条 debug 信息");
        log.warn("这是一条 warn 信息");
        log.error("这是一条 error 信息");
    }
}
