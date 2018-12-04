package cn.openread.test.close;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 简单测试
 *
 * @author simon
 * @create 2018-05-21 上午10:44
 **/
public class SimpleTest {

    /**
     * 文件流
     * <p>
     * 读取一行
     */
    static String readFirstLineFromFile(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            return br.readLine();
        }
    }

    /**
     * 文件流
     * <p>
     * 读取全部
     */
    static List<String> readMutlLineFromFile(String path) throws IOException {
        List<String> all = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            while (br.ready()) {
                all.add(br.readLine());
            }
        }
        return all;
    }

    // JDK1.7中的写法，利用AutoCloseable接口
    // 代码更精练、完全

    /**
     * 声明资源时要分析好资源关闭顺序,先声明的后关闭
     * 在try-with-resource中也可以有catch与finally块。
     * 只是catch与finally块是在处理完try-with-resource后才会执行。
     * <p>
     * 一些会占用操作系统资源的对象（如文件、socket句柄等）都会实现Closeable接口。
     * 调用close()方法，jvm就会释放给操作系统。
     * 一般来讲，即便不调用，进程结束后操作系统也会回收。但是像运行在tomcat等容器中的web项目代码，
     * 项目停了但tomcat没停，会有资源泄露的风险。
     **/
    @Test
    public void testCloseable() {
        /**
         * 新增特性适用于带资源的try语句( try-with-resources block )。
         * 形如try(资源类对象的声明){可能有异常抛出的语句块}catch{}。
         * 即便没有finally，圆括号中的资源也会按声明的顺序逆序close()。这个由jvm实现。
         * */
        try (Resource res = new Resource();
             ResourceOther resOther = new ResourceOther();) {
            res.doSome();
            resOther.doSome();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testReadFile() throws IOException {
        String path = "C:\\SortWare\\simon-projects\\labs\\mybatis-3\\src\\test\\java\\cn\\openread\\test\\close\\SimpleTest.java";
        System.out.println(readFirstLineFromFile(path));
        readMutlLineFromFile(path).forEach(System.out::println);
    }

    /**
     * Runnable与Callable
     * <p>
     * 相同点：
     * <p>
     * 两者都是接口；（废话）
     * 两者都可用来编写多线程程序；
     * 两者都需要调用Thread.start()启动线程；
     * <p>
     * 不同点：
     * <p>
     * 两者最大的不同点是：实现Callable接口的任务线程能返回执行结果；而实现Runnable接口的任务线程不能返回结果；
     * Callable接口的call()方法允许抛出异常；而Runnable接口的run()方法的异常只能在内部消化，不能继续上抛；
     * <p>
     * Callable接口支持返回执行结果，此时需要调用FutureTask.get()方法实现，此方法会阻塞主线程直到获取‘将来’结果；当不调用此方法时，主线程不会阻塞！
     */
    @Test
    public void testCallable() throws Exception {
        Callable<String> callable = new CallableImpl("my callable test!");
        FutureTask<String> task = new FutureTask<>(callable);
        long beginTime = System.currentTimeMillis();
        // 创建线程
        new Thread(task).start();
        // 调用get()阻塞主线程，反之，线程不会阻塞
        String result = task.get();
        long endTime = System.currentTimeMillis();
        System.out.println("hello : " + result);
        System.out.println("cast : " + (endTime - beginTime) / 1000 + " second!");
    }
}

class CallableImpl implements Callable<String> {

    private String acceptStr;

    public CallableImpl(String acceptStr) {
        this.acceptStr = acceptStr;
    }

    @Override
    public String call() throws Exception {
        // 任务阻塞 1 秒
        Thread.sleep(1000);
        return this.acceptStr + " append some chars and return it!";
    }
}

class Resource implements Closeable {
    void doSome() {
        System.out.println("do something");
    }

    @Override
    public void close() {
        System.out.println("resource closed");
    }
}

class ResourceOther implements Closeable {
    void doSome() {
        System.out.println("do something other");
    }

    @Override
    public void close() {
        System.out.println("other resource closed");
    }
}