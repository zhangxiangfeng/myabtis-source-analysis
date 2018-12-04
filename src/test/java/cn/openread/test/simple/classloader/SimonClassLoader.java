package cn.openread.test.simple.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * 自定义类加载器,实现加载自己制定目录的资源
 */
public class SimonClassLoader extends ClassLoader {
    private String loadPath;

    public SimonClassLoader(String loadPath) {
        this.loadPath = loadPath;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        try {
            if (null == name) {
                throw new Exception("name is null");
            }

            if (null == loadPath) {
                throw new Exception("loadPath is null");
            }

            return new FileInputStream(new File(loadPath + File.separator + name));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
