package edu.neu.coe.info6205.util;

import java.util.Random;

public class GenerateIntegers {

    public enum Ordering {RANDOM, ORDERED, PARTORDERED, REVERSE};

    GenerateIntegers(){
        super();
    }

    public Integer[] generateRandomArray(Ordering ordering, int num){
        Integer[] list = new Integer[num];
        Random random = new Random();
        int i = 0;

        switch(ordering) {
            case RANDOM:
                for (i = 0; i < num; i++)
                {
                    list[i] = random.nextInt(10000);
                }
                break;
            case ORDERED:
                for (i = 0; i < num; i++)
                    list[i] = i;
                break;
            case PARTORDERED:
                int orderNum = random.nextInt(10);
                for (i = 0; i < orderNum; i++){
                    list[i] = i;
                }
                for (i = orderNum; i < num; i++){
                    list[i] = random.nextInt(10000);
                }
                break;
            case REVERSE:
                for (i = 0; i < num; i++) {
                    list[i] = num - i;
                }
                break;
            default:
                // do nothing
        }
        return list;
    }
}
