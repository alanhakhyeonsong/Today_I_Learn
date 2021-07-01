package me.ramos.java8to11;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class practice03 {
    public static void main(String[] args) {
        practice03 practice03 = new practice03();
        practice03.run();
    }

    private void run() {
        // Java 8 부턴 final 생략 가능. 다음 변수가 사실상 final 일 때.
        // 따라서 아래 세 영역에서 다음 변수를 참조 가능함.
        int baseNumber = 10;

        // 로컬 클래스
        class LocalClass {
            void printBaseNumber() {
                // 다음은 스코프로 같은 이름의 변수를 가린 꼴(쉐도잉)
                int baseNumber = 11;
                System.out.println(baseNumber); // 11
            }
        }

        // 익명 클래스
        Consumer<Integer> integerConsumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                System.out.println(baseNumber);
            }
            /* 다음과 같이 인자를 baseNumber로 만들면 기존과 다른 새로운 baseNumber임.
            이 또한 쉐도잉의 문제이다.
            @Override
            public void accept(Integer baseNumber) {
                System.out.println(baseNumber);
            }
             */
        };

        // 람다
        // 람다에선 쉐도잉 불가함. 같은 이름의 변수를 정의할 수 없다.
        IntConsumer printInt = (i) -> System.out.println(i + baseNumber);
        printInt.accept(10);
        /* 다음은 effective final이 아니므로 사용 불가.
        baseNumber++;
         */
    }
}
