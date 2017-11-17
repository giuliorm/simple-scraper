package ru.juriasan.util;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Counter {

    public static Map<String, Integer> countWords(Set<String> words) {
        return count(words);
    }

    public static Map<Character, Integer> countSymbols(Set<Character> symbols) {
        return count(symbols);
    }

    public static  <T> Map<T, Integer> count(Iterable<T> values) {
        Map<T, Integer> map = new HashMap<>();
        for (T value : values) {
             if (map.containsKey(value)) {
                 Integer cnt = map.get(value);
                 map.put(value, cnt + 1);
             }
             else map.put(value, 0);
        }
        return map;
    }
}
