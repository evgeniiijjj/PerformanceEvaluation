import java.util.Map;
import java.util.concurrent.Callable;

public class Reader implements Callable<String> {
    private final Map<Integer, Integer> map;
    private final int from;
    private final int to;

    public Reader(Map<Integer, Integer> map, int from, int to) {
        this.map = map;
        this.from = from;
        this.to = to;
    }

    @Override
    public String call() {
        String name = Thread.currentThread().getName();
        int i = from;
        for ( ; i < to; i++) {
            map.get(i);
        }
        return String.format("Поток %s, прочитано %d чисел", name, i + 1 - from);
    }
}
