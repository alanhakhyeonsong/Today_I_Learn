package me.ramos.java8to11;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class App {

    public static void main(String[] args) {
        Greeting greeting = new Greeting();
        UnaryOperator<String> hello = greeting::hello;
        System.out.println(hello.apply("Ramos"));

        UnaryOperator<String> hi = Greeting::hi;
        System.out.println(hi.apply("Kroos"));

        Supplier<Greeting> newGreeting = Greeting::new;
        newGreeting.get();

        // 인자를 받는 생성자를 참조
        Function<String, Greeting> ronaldoGreeting = Greeting::new;
        Greeting ronaldo = ronaldoGreeting.apply("Ronaldo");
        System.out.println(ronaldo.getName());

        // 인자를 받지 않는 생성자를 참조
        Supplier<Greeting> newGreeting1 = Greeting::new;


        String[] names = {"Cristiano", "Zidane", "Sergio"};
        /* 다음 메소드를 람다형태로 바꿀 수 있다.
        Arrays.sort(names, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return 0;
            }
        });*/
        // 임의 객체의 인스턴스 메소드 참조
        Arrays.sort(names, String::compareToIgnoreCase);
        System.out.println(Arrays.toString(names));
    }
}
