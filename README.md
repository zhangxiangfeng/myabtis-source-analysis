MYBATIS Data Mapper Framework
=============================

[![Build Status](https://travis-ci.org/mybatis/mybatis-3.svg?branch=master)](https://travis-ci.org/mybatis/mybatis-3)
[![Maven central](https://maven-badges.herokuapp.com/maven-central/org.mybatis/mybatis/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.mybatis/mybatis)
[![Apache 2](http://img.shields.io/badge/license-Apache%202-red.svg)](http://www.apache.org/licenses/LICENSE-2.0)

![mybatis](http://mybatis.github.io/images/mybatis-logo.png)


`The MyBatis data mapper framework makes it easier to use a relational database with object-oriented applications. MyBatis couples objects with stored procedures or SQL statements using a XML descriptor or annotations. Simplicity is the biggest advantage of the MyBatis data mapper over object relational mapping tools.`


**`译文:`**MyBatis数据映射器框架使关系数据库与面向对象的应用程序的使用变得更加容易。 MyBatis使用XML描述符或注释将对象与存储过程或SQL语句结合在一起。 简单性是MyBatis数据映射器相对于对象关系映射工具的最大优势。

Essentials
----------

* [See the docs](http://mybatis.github.io/mybatis-3)
* [Download Latest](https://github.com/mybatis/mybatis-3/releases)


------------

### 0.起步

```
Reader reader = Resources.getResourceAsReader("cn/openread/test/simple/xml/MapperConfig.xml");

SqlSession sqlSession = SqlSessionManager.newInstance(reader).openSession();

AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);

List<Account> accounts = accountMapper.selectAll();
log.debug("查询结果:" + JSON.toJSONString(accounts));
```
### 1.深入分析起步读取
**分析下面这一句**
```
Reader reader = Resources.getResourceAsReader("cn/openread/test/simple/xml/MapperConfig.xml");
```

### 2.深入分析创建SqlSession
**分析下面这一句**
```
SqlSession sqlSession = SqlSessionManager.newInstance(reader).openSession();
```
### 3.深入执行接口获取
**分析下面这一句**
```
AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);
```
### 4.深入分析SQL结果执行和收集
**分析下面这一句**
```
List<Account> accounts = accountMapper.selectAll();
```
