package com.example.demo;

/**
 * Created by janseny on 2018/12/12.
 */
public enum  TestEnum {
    SPRING, SUMMER,AUTUMN,WINTER;

    public static void main(String[] args){

        switch (TestEnum.SPRING){
            case AUTUMN:
                System.out.println("秋天");
            case SPRING:
                System.out.println("春天");
                break;
            case SUMMER:
                System.out.println("夏天");
                break;
            case WINTER:
                System.out.println("冬天");
                break;
            default:
                System.out.println("默认");
        }
    }
}
