package com.dgut;

public class C {
    public static int a ;
    static {
        int b = 10;
        System.out.println("静态方法被调用a = " + b);
    }

    public static String getStr(){
        return "hello";
    }
}
