package org.gepron1x.test;

import com.google.common.base.MoreObjects;

public class ToStringHelperTest {
    public static void main(String[] args) {
        System.out.println(MoreObjects.toStringHelper("arr").addValue(1).addValue(2).addValue(3));
    }
}
