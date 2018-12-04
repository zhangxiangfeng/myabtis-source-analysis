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
package org.apache.ibatis;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/***
 * 测试用户到基础数据
 * */
public abstract class BaseDataTest {

    public static final String ACCOUNT_PROPERTIES = "org/apache/ibatis/databases/account/account-mysql.properties";
    public static final String ACCOUNT_DDL = "org/apache/ibatis/databases/account/account-mysql-schema.sql";
    public static final String ACCOUNT_DATA = "org/apache/ibatis/databases/account/account-mysql-dataload.sql";

    /**
     * 下面配置为mybatis官方测试,废弃处理,我们用自己写的测试案例代码进行测试,感知会更加好一点
     */
    @Deprecated
    public static final String BLOG_PROPERTIES = "org/apache/ibatis/databases/blog/blog-derby.properties";
    @Deprecated
    public static final String BLOG_DDL = "org/apache/ibatis/databases/blog/blog-derby-schema.sql";
    @Deprecated
    public static final String BLOG_DATA = "org/apache/ibatis/databases/blog/blog-derby-dataload.sql";

    @Deprecated
    public static final String JPETSTORE_PROPERTIES = "org/apache/ibatis/databases/jpetstore/jpetstore-hsqldb.properties";
    @Deprecated
    public static final String JPETSTORE_DDL = "org/apache/ibatis/databases/jpetstore/jpetstore-hsqldb-schema.sql";
    @Deprecated
    public static final String JPETSTORE_DATA = "org/apache/ibatis/databases/jpetstore/jpetstore-hsqldb-dataload.sql";

    public static UnpooledDataSource createUnpooledDataSource(String resource) throws IOException {
        Properties props = Resources.getResourceAsProperties(resource);
        UnpooledDataSource ds = new UnpooledDataSource();
        ds.setDriver(props.getProperty("driver"));
        ds.setUrl(props.getProperty("url"));
        ds.setUsername(props.getProperty("username"));
        ds.setPassword(props.getProperty("password"));
        return ds;
    }

    public static PooledDataSource createPooledDataSource(String resource) throws IOException {
        Properties props = Resources.getResourceAsProperties(resource);
        PooledDataSource ds = new PooledDataSource();
        ds.setDriver(props.getProperty("driver"));
        ds.setUrl(props.getProperty("url"));
        ds.setUsername(props.getProperty("username"));
        ds.setPassword(props.getProperty("password"));
        return ds;
    }

    public static HikariDataSource createHikariPooledDataSource(String resource) throws IOException {
        Properties props = Resources.getResourceAsProperties(resource);
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(props.getProperty("driver"));
        config.setJdbcUrl(props.getProperty("url"));
        config.setUsername(props.getProperty("username"));
        config.setPassword(props.getProperty("password"));

        return new HikariDataSource(config);
    }

    public static void runScript(DataSource ds, String resource) throws IOException, SQLException {
        Connection connection = ds.getConnection();
        try {
            ScriptRunner runner = new ScriptRunner(connection);
//            runner.setSendFullScript(true);
            runner.setAutoCommit(true);
            runner.setStopOnError(false);
            runner.setLogWriter(null);
            runner.setErrorLogWriter(null);
            runScript(runner, resource);
        } finally {
            connection.close();
        }
    }

    public static void runScript(ScriptRunner runner, String resource) throws IOException, SQLException {
        Reader reader = Resources.getResourceAsReader(resource);
        try {
            runner.runScript(reader);
        } finally {
            reader.close();
        }
    }

    /**
     * 废弃这种数据库测试
     */
    @Deprecated
    public static DataSource createBlogDataSource() throws IOException, SQLException {
        DataSource ds = createUnpooledDataSource(ACCOUNT_PROPERTIES);
        runScript(ds, ACCOUNT_DDL);
        runScript(ds, ACCOUNT_DATA);
        return ds;
    }

    public static DataSource createAccountDataSource() throws IOException, SQLException {
        DataSource ds = createHikariPooledDataSource(ACCOUNT_PROPERTIES);
        runScript(ds, ACCOUNT_DDL);
        runScript(ds, ACCOUNT_DATA);
        return ds;
    }

    /**
     * 废弃这种数据库测试
     */
    @Deprecated
    public static DataSource createJPetstoreDataSource() throws IOException, SQLException {
        DataSource ds = createUnpooledDataSource(JPETSTORE_PROPERTIES);
        runScript(ds, JPETSTORE_DDL);
        runScript(ds, JPETSTORE_DATA);
        return ds;
    }
}
