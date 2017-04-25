package com.hunsh.qqbot.util;

/**
 * @Author : Edward Jia
 * @Description :
 * @Date : 17/4/25
 * @Version :
 */
public class Utils {

    public static int hash33(String s) {
        int e = 0;
        int n = s.length();

        for(int i = 0; n > i; ++i) {
            e += (e << 5) + s.charAt(i);
        }

        return 2147483647 & e;
    }
}
