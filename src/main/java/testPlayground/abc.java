package testPlayground;

import java.util.ArrayList;
import java.util.List;

public class abc implements abcinterface {
    final int field1;
    final List<Integer> list;

    abc() {
        field1 = 1;
        list = new ArrayList<>();
    }

    void test() {
        System.out.println("abc");
    }
}
