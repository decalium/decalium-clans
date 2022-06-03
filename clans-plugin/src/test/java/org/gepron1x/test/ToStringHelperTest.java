package org.gepron1x.test;


import java.util.List;

public class ToStringHelperTest {



    public static void main(String[] args) {
        List<Integer> list1 = List.of(1);
        List<Integer> list2 = List.of(2);

        for(int i = 0; i < list1.size(); i++) {
            System.out.println(list1.get(i) + list2.get(i));
        }
    }

}
