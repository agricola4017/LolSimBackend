package Functions;

import java.util.List;

public class Functions {

    public static int randomNumberCustom(int min, int max) {
        return (int)(Math.random() * (max - min + 1))+min;
    }
    public static int randomNumber0to100() {
        return randomNumberCustom(0, 100);
    }

    public static boolean rollPercentile(int percentile) {
        return randomNumber0to100() > 100 - percentile;
    }

    public static boolean rollPercentile(double percentile) {
        return randomNumber0to100() > 100 - percentile;
    }

    public static boolean rollGreaterThanPercentile(int bound, int percentile) {
        return randomNumberCustom(bound, 100) > percentile;
    }

    public static <T> String flattenListString(List<T> list) {
        String ret = "";
        for (T o : list) {
            ret += o.toString() + "\n";
        }
        return ret;
    }
}
