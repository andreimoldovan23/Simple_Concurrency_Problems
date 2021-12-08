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

    public static Integer getMaxCof() {
        return Integer.parseInt((String) props.get("polynomial.max.coefficient"));
    }

    public static Integer getRank() {
        return Integer.parseInt((String) props.get("polynomial.rank"));
    }

    public static Integer getXValue() {
        return Integer.parseInt((String) props.get("polynomial.value"));
    }

    public static Integer getNrThreads() {
        return Integer.parseInt((String) props.get("threads"));
    }

    public static String getApproach() {
        return (String) props.get("program.approach");
    }

    public static String getThreadsApproach() {
        return (String) props.get("program.threads.approach");
    }
}
