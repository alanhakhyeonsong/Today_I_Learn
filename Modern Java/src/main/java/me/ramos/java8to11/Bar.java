package me.ramos.java8to11;

public interface Bar extends Doo {
    /* Bar를 구현하는 클래스에서
    * printNameUpperCase()를 사용하고 싶지 않다면
    * 다음과 같이 추상메소드로 둔다.
    */
    void printNameUpperCase();

}
