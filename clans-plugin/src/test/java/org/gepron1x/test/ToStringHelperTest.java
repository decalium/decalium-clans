package org.gepron1x.test;


public class ToStringHelperTest {
    public static void main(String[] args) {
        String test = "minec_raft:te_st/344.";
        char[] chars = test.toCharArray();
        byte[] bytes = new byte[chars.length];
        for(int i = 0; i < chars.length; i++) {
            bytes[i] = (byte) chars[i];
            System.out.println();
        }


        char[] chars1 = new char[bytes.length];
        for(int i = 0; i < chars1.length; i++) {
            chars1[i] = (char) bytes[i];
        }

        System.out.println(new String(chars1));
    }
}
