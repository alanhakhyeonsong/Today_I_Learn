package me.ramos.java8to11;

public class DefaultDoo implements Doo{

    String name;

    public DefaultDoo(String name) {
        this.name = name;
    }

    @Override
    public void printName() {
        System.out.println(this.name);
    }

    @Override
    public String getName() {
        return this.name;
    }
}
