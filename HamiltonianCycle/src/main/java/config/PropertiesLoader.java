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
            InputStream inputStream = PropertiesLoader.class.getClassLoader().getResourceAsStream(propsFile);
            props.load(inputStream);
            if (inputStream != null) inputStream.close();
            return props;
        } catch (Exception ioe) {
            log.error("Error while reading properties file");
            throw new RuntimeException();
        }
    }

    public static String getMatrixFile() {
        return props.get("base.path") + (String) props.get("adjacency.file");
    }
}
