package com.yyk;

import java.util.*;

/**
 * @author tk
 */
public class TestRandom {
    public static void main(String[] args) {
        Set<Integer> set = new HashSet<>();
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            // set.add(random.nextInt(15) + 9);
            set.add(random.nextInt(61));
        }

        for (Integer integer : set) {
            System.out.println(integer);
        }
    }
}
