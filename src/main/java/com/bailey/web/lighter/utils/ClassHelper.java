package com.bailey.web.lighter.utils;

import com.bailey.web.lighter.WebLighterConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;

/**
 * Class搜索工具类
 * <p>按条件搜索Class, 同时缓存这些Class的信息. 此后可使用如下方法获得满足条件的Class集合</p>
 * <pre>
 * - getAllClasses()            : 返回所有类的集合
 * - getClassesByAnnotation()   : 返回标注了指定注解的类集合
 * - getSubClasses()            : 返回指定 超类的子类 或 实现了指定接口的类 组成的集合
 * </pre>
 *
 * @author Bailey
 */
public class ClassHelper {
    private String packageName;
    private boolean isRecursive;
    private boolean isIncludeJar;

    // 是否已经搜索并缓存过所有类
    private boolean isSearched = false;

    // 所有类集合缓存
    private List<Class<?>> allClasses = new ArrayList<>();

    /**
     * @param packageName  包名, <em>packageName = "" 时将搜索所有package下的Class</em>
     * @param isRecursive  是否深度遍历子包
     * @param isIncludeJar 是否遍历JAR包中的文件
     */
    public ClassHelper(String packageName, boolean isRecursive, boolean isIncludeJar) {
        this.packageName = packageName;
        this.isRecursive = isRecursive;
        this.isIncludeJar = isIncludeJar;
    }

    /**
     * 使用默认值构造ClassHelper
     * <p>进行Class搜索时将搜索所有package下的Class, <strong>但忽略jar中的Class</strong>.</p>
     *
     * @see #ClassHelper(String, boolean, boolean)
     */
    public ClassHelper() {
        this("", true, false);
    }

    /**
     * 获得符合搜索条件的所有类
     *
     * @return 符合搜索条件的所有类
     */
    public List<Class<?>> getAllClasses() {
        if (!isSearched) {
            try {
                searchClass();
            } catch (Exception e) {
                LoggerFactory.getLogger(WebLighterConfig.LIB_NAME).warn("An exception occurred while searching for a RequestHandler, some RequestHandlers may be missed.");
            }
        }
        return allClasses;
    }

    /**
     * 获得有指定注解的类
     *
     * @param annotationClass 注解类
     * @return 标注了指定注解的类集合
     */
    public List<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotationClass) {
        List<Class<?>> classes = new ArrayList<>();
        List<Class<?>> allClasses = getAllClasses();
        for (Class<?> cls : allClasses) {
            if (cls.isAnnotationPresent(annotationClass)) {
                classes.add(cls);
            }
        }
        return classes;
    }

    /**
     * 获得指定类的所有子类, 或实现了某一接口的所有子类
     *
     * @param superClass 超类/接口
     * @param <T>        超类/接口
     * @return superClass的子类或实现了superClass接口的类
     */

    public <T> List<Class<T>> getSubClasses(Class<T> superClass) {
        List<Class<T>> classes = new ArrayList<>();
        List<Class<?>> allClasses = getAllClasses();
        for (Class<?> cls : allClasses) {
            if (superClass.isAssignableFrom(cls) && !superClass.equals(cls)) {
                classes.add((Class<T>) cls);
            }
        }
        return classes;
    }

    /**
     * 搜索所有类
     */
    private void searchClass() throws IOException {
        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packageName.replaceAll("\\.", "/"));
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if (url != null) {
                switch (url.getProtocol()) {
                    case "jar":     // 添加jar中的class
                        if (!isIncludeJar) continue;
                        Enumeration<JarEntry> jarEntries = ((JarURLConnection) url.openConnection()).getJarFile().entries();
                        while (jarEntries.hasMoreElements()) {
                            String jarEntryName = jarEntries.nextElement().getName();
                            if (jarEntryName.endsWith(".class")) {
                                String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                                if (isRecursive || packageName.equals(className.substring(0, className.lastIndexOf(".")))) {
                                    addClass(className);
                                }
                            }
                        }
                        break;
                    case "file":     // 添加src下的class
                        addClass(url.getPath(), packageName);
                        break;
                }
            }
        }
    }

    /**
     * 将指定路径下所有类添加到类集合缓存 <em>allClasses</em>
     *
     * @param packageName 包名
     * @param packagePath 文件夹路径
     */
    private void addClass(String packagePath, String packageName) {
        // packagePath路径下所有的class文件 或 文件夹
        File[] files = new File(packagePath).listFiles((file) -> file.isDirectory() || (file.isFile() && file.getName().endsWith(".class")));
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {        // 文件
                    String fileName = file.getName();
                    String className = fileName.substring(0, fileName.lastIndexOf("."));
                    if (StringUtils.isNotEmpty(packageName)) {
                        className = packageName + "." + className;
                    }
                    addClass(className);
                } else if (isRecursive) {    // 文件夹
                    String subPackName = file.getName();
                    String subPackagePath = (StringUtils.isEmpty(packagePath)) ? subPackName : (packagePath + "/" + subPackName);
                    String subPackageName = (StringUtils.isEmpty(packageName)) ? subPackName : (packageName + "." + subPackName);
                    addClass(subPackagePath, subPackageName);
                }
            }
        }
    }

    private void addClass(String className) {
        try {
            allClasses.add(Class.forName(className));
        } catch (Exception e) {
            LoggerFactory.getLogger(WebLighterConfig.LIB_NAME).warn("Can't analyze the class, it has been ignored: " + className);
        }
    }
}
