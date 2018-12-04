package cn.openread.test.simple;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * 图中展示了XMLConfigBuilder为了组装出Configuration对象所作出的努力，配备了至少六个基本工具。本文的重点，就是分析这六个工具的作用。
 * <p>
 * 1.DefaultObjectFactory 反射创建对象工厂类,通过反射，动态创建一个类，并且赋予属性
 * 2.Resources IO读取;包含class,xml
 * 3.Reflector、Invoker、ReflectorFactory;获取一个类的属性，方法，构造器
 * 4. XPath、EntityResolver 解析XML的工具
 *
 * @author simon
 * @create 2018-05-21 下午6:22
 **/
@SuppressWarnings("ALL")
public class SimpleTest2 {
    private final Log log = LogFactory.getLog(SimpleTest2.class);

    private SqlSession sqlSession = null;

    /**
     * ObjectFactory :反射创建对象工厂类。
     * <p>
     * 创建简单对象
     */
    @Test
    public void test1() {
        ObjectFactory objectFactory = new DefaultObjectFactory();
        List<String> list = objectFactory.create(ArrayList.class);
        list.add("apple");
        System.out.println(list);
    }

    /**
     * 1.ObjectFactory :反射创建对象工厂类。
     * 创建带有初始化参数的对象
     */
    @Test
    public void test2() {
        ObjectFactory objectFactory = new DefaultObjectFactory();

        List<Class<?>> constructorArgTypes = new ArrayList<>();
        constructorArgTypes.add(Long.class);
        constructorArgTypes.add(String.class);
        constructorArgTypes.add(String.class);
        constructorArgTypes.add(Long.class);

        List<Object> constructorArgs = new ArrayList<>();
        constructorArgs.add(10L);
        constructorArgs.add(new String("simon"));
        constructorArgs.add(new String("simon spec"));
        constructorArgs.add(24L);

        Person person = objectFactory.create(Person.class, constructorArgTypes, constructorArgs);

        System.out.println(person);

    }

    /**
     * 2.Reflector、Invoker、ReflectorFactory
     */
    @Test
    public void test3() throws InvocationTargetException, IllegalAccessException {
        //sept 1.利用反射工厂类构造出来一个对象
        ObjectFactory objectFactory = new DefaultObjectFactory();
        Person person = objectFactory.create(Person.class);

        //sept 2.反射出来一个类所有的属性和get,set方法,并且进行缓存
//        Reflector reflector = new Reflector(Person.class);
        //和上面sept2 等效(进行多线程同步处理),默认开启发射类的缓存,避免一个类，多次重复反射
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Reflector reflector = reflectorFactory.findForClass(Person.class);

        //sept 3.MethodInvoker 实现-> Invoker 根据属性获取set方法
        Invoker invoker = reflector.getSetInvoker("id");

        //sept 4.调用set 方法
        invoker.invoke(person, new Object[]{20l});

        //sept 5.调用get 方法
        invoker = reflector.getGetInvoker("id");

        System.out.println("studId=" + invoker.invoke(person, null));

    }

    /**
     * 3.XPath、EntityResolver
     * <?xml version="1.0" encoding="UTF-8"?>
     * <!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
     * <configuration>
     * <settings>
     * <setting name="defaultExecutorType" value="REUSE" />
     * <setting name="defaultStatementTimeout" value="25000" />
     * </settings>
     * <mappers>
     * <mapper resource="com/mybatis3/mappers/StudentMapper.xml" />
     * <mapper resource="com/mybatis3/mappers/TeacherMapper.xml" />
     * </mappers>
     * </configuration>
     * <p>
     * XPath，是针对Xml的“正则表达式”。
     * <p>
     * 我们使用XPath技术，来编写一个小例子。
     */
    @Test
    public void test4() throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {


        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setValidating(false);

        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        //如果使用上面的代码运行XPath，程序将像蜗牛一样缓慢，问题原因是Xml内部的
        //JDK会使用网络，去上面这个地址下载dtd文件，并解析，所以慢的像蜗牛（和网络环境有关）。
        //加入下面这句话，程序瞬间快如闪电。XMLMapperEntityResolver是Mybatis针对EntityResolver的实现类，它从本地环境去寻找dtd文件，而不是去网络上下载，所以，速度飞快。
        builder.setEntityResolver(new XMLMapperEntityResolver());

        InputSource inputSource = new InputSource(Resources.getResourceAsStream("mybatis-config.xml"));

        Document document = builder.parse(inputSource);

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();
        String value = (String) xpath.evaluate("/configuration/settings/setting[@name='defaultStatementTimeout']/@value", document, XPathConstants.STRING);

        System.out.println("defaultStatementTimeout=\"" + value + "\"");

        Node node = (Node) xpath.evaluate("/configuration/mappers/mapper[1]", document, XPathConstants.NODE);
        NamedNodeMap attributeNodes = node.getAttributes();
        for (int i = 0; i < attributeNodes.getLength(); i++) {
            Node n = attributeNodes.item(i);
            System.out.println(n.getNodeName() + "=\"" + n.getNodeValue() + "\"");
        }


    }


    public static class Person {
        private Long id;
        private String name;
        private String spec;
        private Long age;

        public Person() {
            this.name = "default";
        }


        public Person(Long id, String name, String spec, Long age) {
            this.id = id;
            this.name = name;
            this.spec = spec;
            this.age = age;
        }

        @Override
        public String toString() {
            return JSON.toJSONString(this, SerializerFeature.DisableCircularReferenceDetect);
        }


        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSpec() {
            return spec;
        }

        public void setSpec(String spec) {
            this.spec = spec;
        }

        public Long getAge() {
            return age;
        }

        public void setAge(Long age) {
            this.age = age;
        }
    }
}
