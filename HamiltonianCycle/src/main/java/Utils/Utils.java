package Utils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ForkJoinTask;

public class Utils {
    public static String getFirstOrNull(List<ForkJoinTask<String>> results) {
        return results.stream()
                .map(ForkJoinTask::join)
                .filter(Objects::nonNull)
                .findFirst().orElse(null);
    }
}
