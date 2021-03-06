/*
 * Copyright (c) 2018. Phasmid Software
 */

package edu.neu.coe.info6205.util;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import edu.neu.coe.info6205.sort.simple.InsertionSort;
import edu.neu.coe.info6205.union_find.WQU;
import edu.neu.coe.info6205.union_find.WQUPC2;

import static edu.neu.coe.info6205.util.Utilities.formatWhole;

/**
 * This class implements a simple Benchmark utility for measuring the running time of algorithms.
 * It is part of the repository for the INFO6205 class, taught by Prof. Robin Hillyard
 * <p>
 * It requires Java 8 as it uses function types, in particular, UnaryOperator&lt;T&gt; (a function of T => T),
 * Consumer&lt;T&gt; (essentially a function of T => Void) and Supplier&lt;T&gt; (essentially a function of Void => T).
 * <p>
 * In general, the benchmark class handles three phases of a "run:"
 * <ol>
 *     <li>The pre-function which prepares the input to the study function (field fPre) (may be null);</li>
 *     <li>The study function itself (field fRun) -- assumed to be a mutating function since it does not return a result;</li>
 *     <li>The post-function which cleans up and/or checks the results of the study function (field fPost) (may be null).</li>
 * </ol>
 * <p>
 * Note that the clock does not run during invocations of the pre-function and the post-function (if any).
 *
 * @param <T> The generic type T is that of the input to the function f which you will pass in to the constructor.
 */
public class Benchmark_Timer<T> implements Benchmark<T> {

    /**
     * Calculate the appropriate number of warmup runs.
     *
     * @param m the number of runs.
     * @return at least 2 and at most m/10.
     */
    static int getWarmupRuns(int m) {
        return Integer.max(2, Integer.min(10, m / 10));
    }

    /**
     * Run function f m times and return the average time in milliseconds.
     *
     * @param supplier a Supplier of a T
     * @param m        the number of times the function f will be called.
     * @return the average number of milliseconds taken for each run of function f.
     */
    @Override
    public double runFromSupplier(Supplier<T> supplier, int m) {
        logger.info("Begin run: " + description + " with " + formatWhole(m) + " runs");
        // Warmup phase
        final Function<T, T> function = t -> {
            fRun.accept(t);
            return t;
        };
        new Timer().repeat(getWarmupRuns(m), supplier, function, fPre, null);

        // Timed phase
        return new Timer().repeat(m, supplier, function, fPre, fPost);
    }

    /**
     * Constructor for a Benchmark_Timer with option of specifying all three functions.
     *
     * @param description the description of the benchmark.
     * @param fPre        a function of T => T.
     *                    Function fPre is run before each invocation of fRun (but with the clock stopped).
     *                    The result of fPre (if any) is passed to fRun.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     *                    When you create a lambda defining fRun, you must return "null."
     * @param fPost       a Consumer function (i.e. a function of T => Void).
     */
    public Benchmark_Timer(String description, UnaryOperator<T> fPre, Consumer<T> fRun, Consumer<T> fPost) {
        this.description = description;
        this.fPre = fPre;
        this.fRun = fRun;
        this.fPost = fPost;
    }

    /**
     * Constructor for a Benchmark_Timer with option of specifying all three functions.
     *
     * @param description the description of the benchmark.
     * @param fPre        a function of T => T.
     *                    Function fPre is run before each invocation of fRun (but with the clock stopped).
     *                    The result of fPre (if any) is passed to fRun.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     */
    public Benchmark_Timer(String description, UnaryOperator<T> fPre, Consumer<T> fRun) {
        this(description, fPre, fRun, null);
    }

    /**
     * Constructor for a Benchmark_Timer with only fRun and fPost Consumer parameters.
     *
     * @param description the description of the benchmark.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     *                    When you create a lambda defining fRun, you must return "null."
     * @param fPost       a Consumer function (i.e. a function of T => Void).
     */
    public Benchmark_Timer(String description, Consumer<T> fRun, Consumer<T> fPost) {
        this(description, null, fRun, fPost);
    }

    /**
     * Constructor for a Benchmark_Timer where only the (timed) run function is specified.
     *
     * @param description the description of the benchmark.
     * @param f           a Consumer function (i.e. a function of T => Void).
     *                    Function f is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     */
    public Benchmark_Timer(String description, Consumer<T> f) {
        this(description, null, f, null);
    }

//    public static void main(String[] args) {
//        GenerateIntegers generator = new GenerateIntegers();
//
//        int num = 500;
//        int runtime = 5;
//        int doubleTime = 5;
//        if (args.length > 0) num = Integer.parseInt(args[0]);
//
//        // random, ordered, partially-ordered and reverse-ordered
//        Integer[] random = generator.generateRandomArray(GenerateIntegers.Ordering.RANDOM, num);
//        Integer[] ordered = generator.generateRandomArray(GenerateIntegers.Ordering.ORDERED, num);
//        Integer[] partOrdered = generator.generateRandomArray(GenerateIntegers.Ordering.PARTORDERED, num);
//        Integer[] reverse = generator.generateRandomArray(GenerateIntegers.Ordering.REVERSE, num);
////        System.out.println("random array" + Arrays.toString(random));
////        System.out.println("ordered array" + Arrays.toString(ordered));
////        System.out.println("part ordered array" + Arrays.toString(partOrdered));
////        System.out.println("reverse array" + Arrays.toString(reverse));
//
//        if (args.length > 1) runtime = Integer.parseInt(args[1]); // number of times the sort function will be called
//        if (args.length > 2) doubleTime = Integer.parseInt(args[2]); // doubling times of running
//
//        InsertionSort sorter = new InsertionSort();
//        Consumer<Integer[]> consumer =  array -> sorter.sort(array, 0, array.length);
//        Benchmark_Timer benchmark = new Benchmark_Timer(InsertionSort.DESCRIPTION, consumer);
//
//        for (int i = 0; i < doubleTime; i++) {
//            int doublingTime = runtime * (int)Math.pow(2, i);
//            double timeRandom = benchmark.run(random, doublingTime);
//            double timeOrdered = benchmark.run(ordered, doublingTime);
//            double timePartOrdered = benchmark.run(partOrdered, doublingTime);
//            double timeReverse = benchmark.run(reverse, doublingTime);
//
//            System.out.println(("Average time of " + doublingTime + " times of insertion sort (random) array of "
//                    + num + " numbers in " + timeRandom + " ms"));
//            System.out.println(("Average time of " + doublingTime + " times of insertion sort (ordered) array of " +
//                    num + " numbers in " + timeOrdered + " ms"));
//            System.out.println(("Average time of " + doublingTime + " times of insertion sort (partially-ordered) array of " +
//                    num + " numbers in " + timePartOrdered + " ms"));
//            System.out.println(("Average time of " + doublingTime + " times of insertion sort (reverse) array of " +
//                    num + " numbers in " + timeReverse + " ms"));
//            System.out.println();
//        }
//    }

    /**
     * Main for WQU
     * @param args
     */
    public static void main(String[] args) {
        int num = 10000;
        int runtime = 10;
        int doubleTime = 11;
        if (args.length > 0) num = Integer.parseInt(args[0]);

        if (args.length > 1) runtime = Integer.parseInt(args[1]); // number of times the sort function will be called
        if (args.length > 2) doubleTime = Integer.parseInt(args[2]); // doubling times of running

        UnaryOperator<WQU> unaryOperator = n -> WQU.reinitialize(n);
        Consumer<WQU> consumer = n -> WQU.count(n);
        Benchmark_Timer benchmark = new Benchmark_Timer(WQU.DESCRIPTION, unaryOperator, consumer);

        for (int i = 0; i < doubleTime; i++) {
            int inputNum = num * (int)Math.pow(2, i);
            double time = benchmark.run(new WQU(inputNum), runtime);

            System.out.println(("Average time of connect " + inputNum + " sites by Weighted Quick Union is "
                    + time + " ms"));
        }
    }

    /**
     * Main for WQUPC2
     * @param args
     */
//    public static void main(String[] args) {
//        int num = 10000;
//        int runtime = 10;
//        int doubleTime = 11;
//        if (args.length > 0) num = Integer.parseInt(args[0]);
//
//        if (args.length > 1) runtime = Integer.parseInt(args[1]); // number of times the sort function will be called
//        if (args.length > 2) doubleTime = Integer.parseInt(args[2]); // doubling times of running
//
//        UnaryOperator<WQUPC2> unaryOperator = n -> WQUPC2.reinitialize(n);
//        Consumer<WQUPC2> consumer = n -> WQUPC2.count(n);
//        Benchmark_Timer benchmark = new Benchmark_Timer(WQUPC2.DESCRIPTION, unaryOperator, consumer);
//
//        for (int i = 0; i < doubleTime; i++) {
//            int inputNum = num * (int)Math.pow(2, i);
//            double time = benchmark.run(new WQUPC2(inputNum), runtime);
//
//            System.out.println(("Average time of connect " + inputNum + " sites by Weighted Quick Union is "
//                    + time + " ms"));
//        }
//    }

    private final String description;
    private final UnaryOperator<T> fPre;
    private final Consumer<T> fRun;
    private final Consumer<T> fPost;

    final static LazyLogger logger = new LazyLogger(Benchmark_Timer.class);
}
