package com.gnicks.features;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Stream {

    public static void main(String[] args) {

        int[] numbers = new int[30];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = new Random().nextInt();
        }

        IntSummaryStatistics intSummaryStatistics = Arrays.stream(numbers).sorted().filter(n -> n > 0 && n % 2 != 0).summaryStatistics();
        System.out.println(intSummaryStatistics.getAverage());

        Arrays.stream(numbers).sorted().filter((number) -> number > 1 );

        Runnable aNew = Stream::new;
    }

}
