package com.lexor.qbsa.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.Collections;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import org.jvnet.hk2.annotations.Service;

@Singleton
@Service
public class ConfigHelper {

    public static final String PROPERTIES_FILE = "application.properties";
    public static Properties properties = null;

    @PostConstruct
    public void init() {
        this.getProperties();
    }
    public Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
            try (InputStream inputStream = getFileFromResourceAsStream(PROPERTIES_FILE)) {
                properties.load(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return properties;
    }
    
    private InputStream getFileFromResourceAsStream(String fileName) throws URISyntaxException {

        // The class loader that loaded the class
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null) {
            String jarPath = getClass().getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()
                .getPath();
            URI uri = URI.create("jar:file:" + jarPath);
            try {
                FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap());
                inputStream = classLoader.getResourceAsStream(fs.getPath(fileName).toString());
                return inputStream;
            } catch (IOException e) {
                throw new IllegalArgumentException("file not found! " + fileName);
            }
        } else {
            return inputStream;
        }
    }
}
