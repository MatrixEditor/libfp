package com.example.exampleapp;

public class Main
{

    private static String field1 = "ExampleApplicationClass";

    public void func3(String s) {
        field1 = s;
    }

    public void func4(int i, MainActivity mainActivity) {
        func3(field1 + " " + i);
    }

    public void func(String s) {
    }

    public ExampleApplicationClass func2(String s) {
        return null;
    }
}
