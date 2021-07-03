package me.ramos.java8to11;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class practice06 {

    public static void main(String[] args) {
        /*
        * Stream? => 연속된 데이터를 처리하는 오퍼레이션의 흐름
        * 데이터를 담고 있는 저장소(컬렉션)이 아님.
        * 스트림 파이프라인 == 0 또는 다수의 중개 오퍼레이션 + 한 개의 종료 오퍼레이션으로 구성.
        * 손쉬운 병렬처리 가능.
        * 중개 오퍼레이션: Stream을 리턴
        * 종료 오퍼레이션: Stream을 리턴 X
         */

        List<String> names = new ArrayList<>();
        names.add("Ronaldo");
        names.add("Modric");
        names.add("Benzema");
        names.add("Kroos");
        names.add("Ramos");

        // parallelStream()을 사용하면 멀티 스레드로 병렬처리함. (로그를 보면 각기 다른 스레드를 사용하고 있음)
        // 다만 반드시 속도가 빨라진다는 보장 X(데이터가 방대하게 많을 경우가 아니고선 오히려 더 느릴 수 있다.)
        List<String> collect = names.parallelStream().map((s) -> {
            System.out.println(s + " " + Thread.currentThread().getName());
            return s.toUpperCase();
        }).collect(Collectors.toList());
        collect.forEach(System.out::println);

        System.out.println();
        System.out.println("============");
        System.out.println();

        // stream()은 단일 스레드로 처리함.
        List<String> collect1 = names.stream().map((s) -> {
            System.out.println(s + " " + Thread.currentThread().getName());
            return s.toUpperCase();
        }).collect(Collectors.toList());
        collect.forEach(System.out::println);
    }
}
