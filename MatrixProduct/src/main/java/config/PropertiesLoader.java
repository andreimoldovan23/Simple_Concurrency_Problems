package config;

import java.io.IOException;
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

    public static Integer getMatrixSize() {
        return Integer.valueOf(props.getProperty("matrix.size"));
    }

    public static Integer getNumberTasks() {
        return Integer.valueOf(props.getProperty("tasks.number"));
    }

    public static Integer getThreadPoolSize() {
        return Integer.valueOf(props.getProperty("thread.pool.size"));
    }

    public static Integer getMinMatrixValue() {
        return Integer.valueOf(props.getProperty("min.val"));
    }

    public static Integer getMaxMatrixValue() {
        return Integer.valueOf(props.getProperty("max.val"));
    }

    public static Boolean isSimpleThreadsApproach() {
        return props.get("program.approach").equals("threads");
    }

    public static Boolean isThreadPoolApproach() {
        return props.get("program.approach").equals("threadpool");
    }

    public static void printProperties() {
        log.trace("Matrix size - {}", getMatrixSize());
        log.trace("Number tasks - {}", getNumberTasks());
        log.trace("Min matrix value - {}", getMinMatrixValue());
        log.trace("Max matrix value - {}", getMaxMatrixValue());
        log.trace("Approach - {}", props.getProperty("program.approach"));

        if (isThreadPoolApproach()) log.trace("Thread pool size - {}", getThreadPoolSize());
    }
}
