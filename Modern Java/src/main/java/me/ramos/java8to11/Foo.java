package me.ramos.java8to11;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Foo {

    public static void main(String[] args) {
        /* 기존: 익명 내부 클래스 anonymous inner class
        RunSomething runSomething = new RunSomething() {
            @Override
            public void doIt() {

            }
        };*/
        // modern: 인터페이스가 하나인 경우 다음과 같이 람다 표현식으로 줄여쓸 수 있다.
        RunSomething runSomething = () -> System.out.println("Hello");
        /* 한 줄이 아닐 경우 다음과 같이 사용
        RunSomething runSomething1 = () -> {
            System.out.println("Hello");
            System.out.println("Lambda");
        };*/

        runSomething.doIt();

        Plus10 plus10 = new Plus10();
        System.out.println(plus10.apply(1));
        // Plus10 클래스를 없애고 다음과 같이 람다 표현식으로 변경 가능
        Function<Integer, Integer> plus10Lambda = (i) -> i + 10;
        System.out.println(plus10Lambda.apply(10));

        // compose: 매개변수부터 실행 후 전체 연산
        Function<Integer, Integer> multiply2 = (i) -> i * 2;
        Function<Integer, Integer> multiply2AndPlus10 = plus10Lambda.compose(multiply2);
        System.out.println(multiply2AndPlus10.apply(2));

        // andThen: 순차적으로 연산
        System.out.println(plus10Lambda.andThen(multiply2).apply(4));

        Consumer<Integer> printT = (i) -> System.out.println(i);
        printT.accept(10);

        Supplier<Integer> get10 = () -> 10;
        System.out.println(get10.get());

        Predicate<String> startsWithRamos = (s) -> s.startsWith("Ramos");
        Predicate<Integer> isEven = (i) -> i%2 == 0;


    }
}
