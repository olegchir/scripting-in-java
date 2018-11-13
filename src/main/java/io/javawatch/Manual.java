package io.javawatch;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.tools.*;


public class Manual {

    private static final String SOURCE = `
    package io.javawatch;
    public class HelloHabr {
        public String get() {
             return "Hello Habr!";
        }
    };`;
    public static final String CNAME = "io.javawatch.HelloHabr";
    private static String CODE_CACHE_DIR = "C:/temp/codeCache";


    public static void main(String[] args) throws Exception {
        /* 1 */
        RuntimeSource file = RuntimeSource.create(); //SimpleJavaFileObject
        /* 2 */
        compile(Collections.singletonList(file));
        /* 3 */
        String result = run();
        /* 4 */ /* ??? */
        /* 5 */ /* PROFIT! */
        System.out.println(result);
    }


    public static void compile(List<RuntimeSource> files) throws IOException {
        File ccDir = new File(CODE_CACHE_DIR);
        if (ccDir.exists()) {
            FileUtils.deleteDirectory(ccDir);
            FileUtils.forceMkdir(ccDir);
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        Logger c = new Logger();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(c, Locale.ENGLISH, null);
        Iterable options = Arrays.asList("-d", CODE_CACHE_DIR,
                "--release=12", "--enable-preview", "-Xlint:preview");

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager,
                c, options, null,
                files);

        if (task.call()) {
            System.out.println("compilation ok");
        }
    }


    public static String run() throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, NoSuchMethodException, InvocationTargetException,
            MalformedURLException {

        // Загрузка класса
        File ccDir = new File(CODE_CACHE_DIR);
        ClassLoader loader = new URLClassLoader(new URL[]{ccDir.toURL()});
        var clаss = loader.loadClass("io.javawatch.HelloHabr"); // Java 10

        // Запуск метода рефлекшеном
        Object instance = clаss.getConstructor().newInstance(); // Java 9
//        Object instance = clаss.newInstance();
        Method thisMethod = clаss.getDeclaredMethod("get");
        Object result = thisMethod.invoke(instance);

        return (String) result;
    }


    public static class Logger implements DiagnosticListener<javax.tools.JavaFileObject> {
        public void report(Diagnostic<? extends javax.tools.JavaFileObject> diagnostic) {

            System.out.println("line in the source: " + diagnostic.getLineNumber());
            System.out.println("diagnostic code: " + diagnostic.getCode());
            System.out.println("message: "
                    + diagnostic.getMessage(Locale.ENGLISH));
            System.out.println("associated object: " + diagnostic.getSource());
            System.out.println("===");
        }
    }


    public static class RuntimeSource extends SimpleJavaFileObject {
        private String contents = null;

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors)
                throws IOException {
            return contents;
        }

        private static RuntimeSource create() throws Exception {
            return new RuntimeSource(CNAME, SOURCE);
        }

        public RuntimeSource(String className, String contents) throws Exception {
            super(buildURI(className), Kind.SOURCE);
            this.contents = contents;
        }

        public static URI buildURI(String className) {
            // io.javawatch.HelloHabr ->
            // string:///io/javawatch/HelloHabr.java
            URI uri = URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension);
            System.out.println(uri);
            return uri;
        }
    }


}