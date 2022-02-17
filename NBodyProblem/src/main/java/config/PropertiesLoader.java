package config;

import java.io.InputStream;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertiesLoader {
    private static final String propsFile = "app.properties";
    private static final Properties props = loadProperties();

    private static Properties loadProperties() {
        try {
            Properties props = new Properties();
            InputStream resourceStream = PropertiesLoader.class.getClassLoader().getResourceAsStream(propsFile);
            props.load(resourceStream);
            if (resourceStream != null) resourceStream.close();
            return props;
        } catch (Exception ioe) {
            log.error("Error loading properties from resource file - {}", propsFile);
            throw new RuntimeException();
        }
    }

    public static String getInputFileName() {
        return (String) props.get("input");
    }

    public static Integer getNrTasks() {
        return Integer.parseInt((String) props.get("tasks"));
    }

    public static Integer getNrThreads() {
        return Integer.parseInt((String) props.get("threads"));
    }
}
