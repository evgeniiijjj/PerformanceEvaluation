import java.util.*;
import java.util.concurrent.*;

public class PerformanceEvaluation {
    static Map<Integer, Integer> sychronizedMap = Collections.synchronizedMap(new HashMap<>());
    static Map<Integer, Integer> concurrentMap = new ConcurrentHashMap<>();
    static int numbersArraySize1 = 20_000_000;
    static int numbersArraySize2 = 10_000_000;
    static int numbersArraySize3 = 1000_000;
    static int numbersArraySize4 = 100_000;
    static int numbersArraySize5 = 10_000;

    public static void main(String[] args) {
        System.out.println("Сравнения производительности кода многопоточной записи и чтения с synchronized map и concurrent map");
        System.out.println();
        compare(numbersArraySize1);
        System.out.println();
        compare(numbersArraySize2);
        System.out.println();
        compare(numbersArraySize3);
        System.out.println();
        compare(numbersArraySize4);
        System.out.println();
        compare(numbersArraySize5);
    }

    static void compare(int numbersArraySize) {
        System.out.printf("Запись в map массива целых чисел размера %,d, и чтение из map:\n", numbersArraySize);
        int[] array = generateArray(numbersArraySize);
        long time1 = evaluatePerformance(sychronizedMap, array);
        System.out.printf("synchronized map - затрачено времени %d млс.;\n", time1);
        long time2 = evaluatePerformance(concurrentMap, array);
        if (time2 == 0) time2 = 1;
        System.out.printf("concurrent map - затрачено времени %d млс.;\n", time2);
        System.out.printf("Итого код с concurrent map производительней в %s раза\n", time1 * 100 / time2 / 100.0);
    }

    static int[] generateArray(int numbersArraySize) {
        Random rnd = new Random();
        int[] array = new int[numbersArraySize];
        for (int i = 0; i < numbersArraySize; i++) {
            array[i] = rnd.nextInt(100);
        }
        return array;
    }

    static long evaluatePerformance(Map<Integer, Integer> map, int[] array) {
        int threadsNum = Runtime.getRuntime().availableProcessors();
        int numbersArraySize = array.length;

        ExecutorService service = Executors.newFixedThreadPool(threadsNum);
        BlockingQueue<Callable<String>> queue = new LinkedBlockingQueue<>();

        int threadsNumHalf = threadsNum / 2;
        int part = numbersArraySize / threadsNumHalf;
        for (int i = 0; i < threadsNum; i++) {
            int from = i % threadsNumHalf * part;
            int to = (i % threadsNumHalf + 1) * part;
            if (numbersArraySize - to < part) to = numbersArraySize;
            if (i < threadsNumHalf) {
                queue.add(new Writer(map, array, from, to));
            } else {
                queue.add(new Reader(map, from, to));
            }
        }

        long start = System.currentTimeMillis();
        try {
            List<Future<String>> futures = service.invokeAll(queue);
            while (!futures.isEmpty()) {
                Iterator<Future<String>> it = futures.iterator();
                while (it.hasNext()) {
                    Future<String> future = it.next();
                    if (future.isDone()) {
                        String result = future.get();
                        //System.out.println(result);
                        it.remove();
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        long finish = System.currentTimeMillis();

        service.shutdown();

        return finish - start;
    }
}
