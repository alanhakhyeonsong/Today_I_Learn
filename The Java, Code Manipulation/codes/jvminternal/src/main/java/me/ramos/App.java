package me.ramos;

import java.util.List;

public class App {

    static String myName;

    static {
        myName = "ramos";
    }

    private String foo = "bar";

    public static void main(String[] args) {
        System.out.println(App.class.getClassLoader());
        System.out.println(List.class.getClassLoader());
        System.out.println(App.class.getSuperclass());
    }
}
