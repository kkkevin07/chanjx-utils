package com.chanjx.offer;

import org.junit.Test;

/**
 * @author 陈俊雄
 * @since 2020/11/25
 **/
public class Offer {
    @Test
    public void independentVariable() {
        int i = 1;
        i = i++;
        int j = i++;
        int k = i + ++i * i++;

        System.out.println("i = " + i);
        System.out.println("j = " + j);
        System.out.println("k = " + k);
    }

    @Test
    public void stringPool01() {
        final String str1 = new StringBuilder("chan").append("jx").toString();
        System.out.println(str1);
        System.out.println(str1.intern());
    }
}
