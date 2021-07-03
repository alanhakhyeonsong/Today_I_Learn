package me.ramos.java8to11;

import java.util.*;

public class practice05 {

    public static void main(String[] args) {
        List<String> name = new ArrayList<>();
        name.add("Ronaldo");
        name.add("Modric");
        name.add("Benzema");
        name.add("Kroos");
        name.add("Ramos");
        /*
        name.forEach(s -> {
            System.out.println(s);
        });
        * 위 코드를 다음과 같이 줄여서 작성 가능함.
         */
        name.forEach(System.out::println);
        System.out.println("=========================");
        System.out.println();
        /* 동일한 기능임
        for (String n : name) {
            System.out.println(n);
        }
         */

        // 순회하다 다음 것이 없을 경우 false 출력
        Spliterator<String> spliterator = name.spliterator();
        // 절반으로 나눠서 출력(순서x)
        Spliterator<String> spliterator1 = spliterator.trySplit();
        while (spliterator.tryAdvance(System.out::println));
        System.out.println("=========================");
        while (spliterator1.tryAdvance(System.out::println));

        System.out.println("=========================");
        System.out.println();

        // Collection의 기본 메소드(stream(), removeIf())
        long r = name.stream().map(String::toUpperCase)
                .filter(s -> s.startsWith("R"))
                .count();
        System.out.println(r);

        System.out.println("=========================");
        System.out.println();
        System.out.println("=========================");

        name.removeIf(s -> s.startsWith("B"));
        name.forEach(System.out::println);

        System.out.println("=========================");
        System.out.println();
        System.out.println("=========================");

        // Comparator의 기본 메소드
        name.sort(String::compareToIgnoreCase); // 역순일 땐 reversed(), 조건 추가 시 thenComparing()
        name.forEach(System.out::println);

    }
}
