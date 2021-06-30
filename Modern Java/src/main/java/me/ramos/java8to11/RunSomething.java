package me.ramos.java8to11;

@FunctionalInterface
public interface RunSomething {

    // 함수형 인터페이스: 추상 메소드가 무조건 1개. 다른 메소드들의 존재 여부는 중요하지 않음.
    void doIt();
    
    // 인터페이스 안에 다음과 같이 static 메소드, default 메소드 정의 가능
    static void printName() {
        System.out.println("Song");
    }

    default void printAge() {
        System.out.println("26");
    }
}
