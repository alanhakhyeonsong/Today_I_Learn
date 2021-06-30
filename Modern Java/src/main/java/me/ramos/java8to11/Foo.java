package me.ramos.java8to11;

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
    }
}
