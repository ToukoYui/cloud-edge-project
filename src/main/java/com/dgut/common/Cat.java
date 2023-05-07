package com.dgut.common;

public class Cat {
    public char choice(int x) {
        if (x < 0) {
            return 'A';
        }else if (x>0 && x<=60){
            return 'B';
        }else if (x>60 && x<=90){
            return  'C';
        }
        return 'D';
    }
}
