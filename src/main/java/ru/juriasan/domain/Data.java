package ru.juriasan.domain;

import java.util.Map;

public class Data {

    private String url;
    private Map<String, Integer> words;
    private Map<Character, Integer> symbols;

    public Data(String url, Map<String, Integer> words, Map<Character, Integer> symbols) {
        this.url = url;
        this.words = words;
        this.symbols = symbols;
    }

    public Map<String, Integer> getWordCount() {
        try {
            return cloneMap(words);
        }
        catch (Exception ex) {
            return null;
        }
    }

    public Map<Character, Integer> getSymbolsCount() {
        try {
            return cloneMap(symbols);
        }
        catch (Exception ex) {
            return null;
        }
    }

    public String getUrl() {
        return this.url;
    }

    private <K,V> Map<K, V> cloneMap(Map<K,V> old) throws Exception {
        Map<K,V> newMap = old.getClass().newInstance();
        newMap.putAll(old);
        return newMap;
    }
}
