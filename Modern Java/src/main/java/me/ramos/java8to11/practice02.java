package me.ramos.java8to11;

import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class practice02 {
    public static void main(String[] args) {
        // BinaryOperator는 타입 통일, BiFunction은 타입을 일일이 다 써줘야 함.
        BinaryOperator<Integer> sum = (a, b) -> a + b;
        System.out.println(sum.apply(20, 30));

        Supplier<Integer> get10 = () -> 10;

        UnaryOperator<Integer> plus10 = (i) -> i + 10;
        UnaryOperator<Integer> multiply = (i) -> i * 2;
        System.out.println(plus10.andThen(multiply).apply(2));
    }
}
