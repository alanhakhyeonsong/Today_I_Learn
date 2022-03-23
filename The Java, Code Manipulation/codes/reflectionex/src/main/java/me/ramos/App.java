package me.ramos;

import java.lang.reflect.Modifier;
import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        Class<Book> bookClass = Book.class;

        Arrays.stream(bookClass.getFields()).forEach(System.out::println); // public 필드만 가져온다.
        System.out.println("=====");
        System.out.println();

        Arrays.stream(bookClass.getDeclaredFields()).forEach(System.out::println); // 모든 필드 다 가져온다.
        System.out.println("=====");
        System.out.println();

        Book book = new Book();
        Arrays.stream(bookClass.getDeclaredFields()).forEach(f -> {
            try {
                f.setAccessible(true); // 접근 지정자 제한 해제
                System.out.printf("%s %s\n", f, f.get(book)); // 필드 값까지 가져오기
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        System.out.println("=====");
        System.out.println();

        Arrays.stream(bookClass.getMethods()).forEach(System.out::println); // 모든 메소드 가져오기
        System.out.println("=====");
        System.out.println();

        Arrays.stream(bookClass.getDeclaredConstructors()).forEach(System.out::println); // 생성자 가져오기
        System.out.println("=====");
        System.out.println();

        System.out.println(MyBook.class.getSuperclass()); // 부모 클래스 가져오기
        System.out.println("=====");
        System.out.println();

        Arrays.stream(MyBook.class.getInterfaces()).forEach(System.out::println); // 인터페이스 가져오기
        System.out.println("=====");
        System.out.println();

        Arrays.stream(Book.class.getDeclaredFields()).forEach(f -> { // 구체적인 정보 가져오기
            int modifiers = f.getModifiers();
            System.out.println(f);
            System.out.println(Modifier.isPrivate(modifiers));
            System.out.println(Modifier.isStatic(modifiers));
        });
    }
}
