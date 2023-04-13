package team.floracore.common.plugin.classpath;

import team.floracore.common.loader.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;

public class JarInJarClassPathAppender implements ClassPathAppender {
    private final JarInJarClassLoader classLoader;

    public JarInJarClassPathAppender(ClassLoader classLoader) {
        if (!(classLoader instanceof JarInJarClassLoader)) {
            throw new IllegalArgumentException("Loader is not a JarInJarClassLoader: " + classLoader.getClass().getName());
        }
        this.classLoader = (JarInJarClassLoader) classLoader;
    }

    @Override
    public void addJarToClasspath(Path file) {
        try {
            this.classLoader.addJarToClasspath(file.toUri().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        this.classLoader.deleteJarResource();
        try {
            this.classLoader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}