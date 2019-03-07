package de.reikodd.meinwidget;


import java.util.Random;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;


public class Java8 {

    public static String JavaVersion() {


        return System.getProperty("java.version");
        //1.8.0_152-release
    }


    public static int LambdaTest() {

        IntFunction<IntUnaryOperator> curriedAdd = a -> (b -> (a + b));

        IntUnaryOperator addTwo = curriedAdd.apply(2);

        return addTwo.applyAsInt(12); //prints 14

    }

    public static int Random()
    {
        Random rn = new Random();
        int nid = rn.nextInt(9999 - 3333 + 1) + 3333;
        return nid;
    }
}
