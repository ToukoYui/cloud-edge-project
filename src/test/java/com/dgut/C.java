package com.dgut;

import com.dgut.model.entity.Pod;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class C {
    public static int a ;
    static {
        int b = 10;
        System.out.println("静态方法被调用a = " + b);
    }

    public static String getStr(){
        return "hello";
    }

    @Test
    public void t1(){
        int len = "".length();
        System.out.println("len = " + len);
    }

    @Test
    public void t2(){
        Pod pod = new Pod();
        pod.setImage(new ArrayList<String>());
        List<String> image = pod.getImage();
        pod.getImage().add("abc");
        image.add("ert");



        System.out.println(pod.getImage());
    }
}
