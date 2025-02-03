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

    public static int randomNumberSlowFast(int min, int max) {
        // Generate a random value in the range [0, 1)
        double randomValue = Math.random();
    
        // Scale the random value to the desired range
        int result;
    
        if (randomValue <= 0.7) { // 80% of the time use slow growth
            // Slow growth: Linear scaling for the lower 80%
            result = (int) ((0.35 * (min + Math.log(1+randomValue) * (max - min))));
        } else if (randomValue < 0.98) {
            result = (int) (0.65 * (min +  Math.log(1+randomValue*randomNumberCustom(1,2)) * (max - min)));
        }
        else { // 20% of the time use fast growth
            // Fast growth: Exponential scaling for the upper 20%
            double minResult = (int)(0.75 * (max-min) + min);
            result = (int) (minResult + (int)(Math.random() * 0.25*(max-min)));
        }
    
        // Ensure the result is within the specified min and max
        return Math.min(max, Math.max(min, result));
    }

    public static <T> String flattenListString(List<T> list) {
        String ret = "";
        for (T o : list) {
            ret += o.toString() + "\n";
        }
        return ret;
    }
}
