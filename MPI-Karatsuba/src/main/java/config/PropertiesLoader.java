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
            InputStream is = PropertiesLoader.class.getClassLoader().getResourceAsStream(propsFile);
            props.load(is);
            if (is != null) is.close();
            return props;
        } catch (Exception e) {
            log.error("Error encountered while reading properties file");
            throw new RuntimeException();
        }
    }

    public static Integer getRank() {
        return Integer.parseInt((String) props.get("polynomial.rank"));
    }

    public static Integer getValue() {
        return Integer.parseInt((String) props.get("polynomial.value"));
    }

    public static Integer getMaxCoefficient() {
        return Integer.parseInt((String) props.get("polynomial.max.coefficient"));
    }

    public static Integer getNumberWorkers() {
        return Integer.parseInt((String) props.get("program.workers"));
    }

    public static String getApproach() {
        return (String) props.get("program.approach");
    }
}
