package cn.openread.test.simple;

import cn.openread.test.simple.classloader.SimonClassLoader;
import com.alibaba.fastjson.JSON;
import com.github.javafaker.Faker;
import org.apache.ibatis.domain.account.Account;
import org.apache.ibatis.domain.account.mappers.AccountAnnoMapper;
import org.apache.ibatis.domain.account.mappers.AccountMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

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
    /**
     * 多线程插入100W条数据
     */

    static volatile AtomicInteger nums = new AtomicInteger(0);
    private SqlSessionManager sqlSessionManager = null;

    @Before
    public void before() throws IOException {
        //step 1.读取配置文件
        Reader reader = Resources.getResourceAsReader("cn/openread/test/simple/xml/MapperConfig.xml");
        sqlSession = SqlSessionManager.newInstance(reader).openSession(ExecutorType.BATCH);
//        new SqlSessionFactoryBuilder().build(reader);
    }

    //    @Before
    public void beforeForClassLoader() throws IOException {
        //step 1.自定义类加载器
        SimonClassLoader classLoader = new SimonClassLoader("D:\\Applications\\project\\labs\\myabtis-source-analysis\\lib");

        //step 2.设置默认的编码
        Resources.setCharset(Charset.forName("UTF-8"));

        //step 3.读取资源文件
        Reader reader = Resources.getResourceAsReader(classLoader, "MapperConfig.xml");

        //step 4.解析读取的文件流 && new一个SqlManager
        sqlSessionManager = SqlSessionManager.newInstance(reader);
        sqlSession = sqlSessionManager.openSession(ExecutorType.BATCH);
    }

    @Test
    public void testBatchXMLInsert() throws IOException {

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try {
            CountDownLatch countDownLatch = new CountDownLatch(8);
            Long start = System.currentTimeMillis();
            for (int i = 0; i < 8; i++) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);
                        List<Account> accounts = new ArrayList<>();
                        for (int i = 0; i < 1000; i++) {
                            Faker faker = new Faker(Locale.CHINA);
                            accounts.add(new Account(faker.name().fullName(), faker.number().randomNumber()));
                        }
                        accountMapper.batchInsert(accounts);
                        countDownLatch.countDown();
                        log.debug(Thread.currentThread().getName() + " is ok ");
                    }
                });
            }
            countDownLatch.await();

            Long end = System.currentTimeMillis();
            log.warn("all thread is finish ! 用时:{}" + String.valueOf(((end - start) / 1000)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            sqlSession.commit();
            sqlSession.close();
            executorService.shutdown();
        }
    }

    @Test
    public void testSelectById() throws IOException {
        try {
            AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);
            Account account = accountMapper.selectById(50002L);
            log.debug("查询结果:" + JSON.toJSONString(account));
//            sqlSession.commit();
            account = accountMapper.selectById(50002L);
            log.debug("查询结果:" + JSON.toJSONString(account));

            sqlSession.commit();
            account = accountMapper.selectById(50002L);
            log.debug("查询结果:" + JSON.toJSONString(account));

        } finally {
//            sqlSession.close();
        }
    }

    @Test
    public void testBatch100WAnnoInsert() throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try {
            Long start = System.currentTimeMillis();
            for (int i = 0; i < 100; i++) {
                sqlSession = sqlSessionManager.openSession(ExecutorType.SIMPLE);
                AccountAnnoMapper accountMapper = sqlSession.getMapper(AccountAnnoMapper.class);
                List<Account> accounts = new ArrayList<>();
                //一次添加多少数据
                int one_times = 100;
                for (int b = 0; b < one_times; b++) {
                    Faker faker = new Faker(Locale.ENGLISH);
                    accounts.add(new Account(faker.name().fullName(), faker.number().randomNumber()));
                }
                accountMapper.batchInsert(accounts);
                sqlSession.commit(true);
                nums.addAndGet(one_times);
                log.warn(Thread.currentThread().getName() + " is ok  nums：" + nums.get());
            }

            Long end = System.currentTimeMillis();
            log.warn("all thread is finish ! 用时: " + String.valueOf(((end - start) / 1000)));
        } finally {
            executorService.shutdown();
        }
    }

    @Test
    public void testBatchAnnoInsert() throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1000);
            Long start = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        AccountAnnoMapper accountMapper = sqlSession.getMapper(AccountAnnoMapper.class);
                        List<Account> accounts = new ArrayList<>();
                        for (int i = 0; i < 1000; i++) {
                            Faker faker = new Faker(Locale.CHINA);
                            accounts.add(new Account(faker.name().fullName(), faker.number().randomNumber()));
                        }
                        accountMapper.batchInsert(accounts);
                        countDownLatch.countDown();
                        log.debug(Thread.currentThread().getName() + " is ok ");
                    }
                });
            }
            countDownLatch.await();

            Long end = System.currentTimeMillis();
            log.warn("all thread is finish ! 用时:{}" + String.valueOf(((end - start) / 1000)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            sqlSession.commit();
            sqlSession.close();
            executorService.shutdown();
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
