package testPlayground;

import static Functions.Functions.randomNumberSlowFast;

import Functions.ExternalAPICallUtility;

public class test {

    public static void main(String[] args) {
    //ZSystem.out.println(ExternalAPICallUtility.generateName());
        int[] bucket = new int[4];
        for (int i = 0; i < 100; i++) {
            int val = randomNumberSlowFast(0, 100);
            System.out.print(val + ", ");
            if (val < 25) {
                bucket[0]++;
            } else if (val < 50) {
                bucket[1]++;
            } else if (val < 75) {
                bucket[2]++;
            } else {
                bucket[3]++;
            }
        }
        System.out.println();
        for (int buck : bucket) {
            System.out.println(buck);
        }
    }   
}
