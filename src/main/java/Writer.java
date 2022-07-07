import java.util.Map;
import java.util.concurrent.Callable;

public class Writer implements Callable<String> {
    private final Map<Integer, Integer> map;
    private final int[] nums;
    private final int from;
    private final int to;

    public Writer(Map<Integer, Integer> map, int[] nums, int from, int to) {
        this.map = map;
        this.nums = nums;
        this.from = from;
        this.to = to;
    }

    @Override
    public String call() {
        String name = Thread.currentThread().getName();
        int i = from;
        for ( ; i < to; i++) {
            map.put(i, nums[i]);
        }
        return String.format("Поток %s, записано %d чисел", name, i + 1 - from);
    }
}
