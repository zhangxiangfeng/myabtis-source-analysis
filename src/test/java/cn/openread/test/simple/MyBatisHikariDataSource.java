package cn.openread.test.simple;

import com.alibaba.fastjson.JSON;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Mybatis 搭配 HikariDataSource
 *
 * @author simon
 * @create 2018-05-22 下午7:08
 **/
public class MyBatisHikariDataSource extends HikariDataSource implements DataSourceFactory {
    private static final Log log = LogFactory.getLog(MyBatisHikariDataSource.class);

    @Override
    public void setProperties(Properties props) {
        log.trace("MyBatisHikariDataSource 读取到的配置属性值:" + JSON.toJSONString(props));
        HikariConfig config = new HikariConfig(props);
        HikariDataSource ds = new HikariDataSource(config);
        super.setDataSource(ds);
    }

    @Override
    public DataSource getDataSource() {
        return super.getDataSource();
    }
}
