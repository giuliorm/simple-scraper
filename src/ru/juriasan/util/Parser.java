package ru.juriasan.util;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;

public class Parser {

    private Map<String, Integer> wordsCount;
    private Map<Character, Integer> symbolsCount;
    private char[] html;
    //currentCharIndex
    private int ch = 0;
    private static final char NODE_BEGIN = '<';
    private static final char NODE_END ='>';
    private static final char EOF = '0';
    private static final char EXCLAMATION = '!';
    private static final char SLASH = '/';
    private static final char DASH = '-';

    private <K> Map<K, Integer> buildCountMap(Collection<K> values) {
        Map<K, Integer> map = new HashMap<>();
        if (values == null)
            return map;

        for (K value : values) {
            map.put(value, 0);
        }
        return map;
    }

    private <K,V> Map<K, V> cloneMap(Map<K,V> old) {
        try {
            Map<K, V> newMap = old.getClass().newInstance();
            newMap.putAll(old);
            return newMap;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public void Parser(String html, Set<String> words, Set<Character> symbols) {
        this.html = html.toCharArray();
        this.wordsCount = buildCountMap(words);
        this.symbolsCount = buildCountMap(symbols);
    }

    public Map<String, Integer> getWordsCount() {
        return cloneMap(wordsCount);
    }

    public Map<Character, Integer> getSymbolsCount() {
        return cloneMap(symbolsCount);
    }

    private boolean isLetter(char letter) {
        return 'a' <=  letter && letter <= 'z' || 'A' <= letter && letter <= 'Z';
    }

    private void invalidDocument() throws Exception {
        throw new Exception("Document is invalid");
    }

    private void parseComment() throws Exception {
        ch++;
        if (html[ch] != DASH || html[ch + 1] != DASH)
            invalidDocument();
        while(true) {
            switch (html[ch]) {
                case EOF: invalidDocument();
                case NODE_END:
                    if (html[ch - 1] != DASH || html[ch - 2] != DASH)
                        invalidDocument();
                    else {
                        ch++;
                        return;
                    }
                default: ch++;
            }
        }
    }

    private void parseNodeName(boolean end) throws Exception {
        if (html[ch] != NODE_BEGIN)
            invalidDocument();
        ch++;
        while(true) {
            switch(html[ch]) {
                case EXCLAMATION:
                    parseComment();
                    break;
                case NODE_BEGIN:
                    if (!end || html[ch + 1] != SLASH)
                        invalidDocument();
                case EOF:
                 invalidDocument();
                case NODE_END:
                    if (html[ch - 1] != NODE_BEGIN) { ch++; return; } else invalidDocument();
                default: ch++; break;
            }
        }
    }

    private StringBuilder handleChar(StringBuilder word) throws Exception {
        if (isLetter(html[ch]))
            word.append(html[ch]);
        else {
            String w = word.toString();
            word = new StringBuilder();
            if (wordsCount.containsKey(w)) {
                wordsCount.put(w, wordsCount.get(w) + 1);
            }
        }
        if (symbolsCount.containsKey(html[ch]))
            symbolsCount.put(html[ch], symbolsCount.get(html[ch]) + 1);
        else
            symbolsCount.put(html[ch], 0);
        return word;
    }

    private void parseNode() throws Exception {
        parseNodeName(false);
        StringBuilder word = new StringBuilder();
        while(true) {
            switch (html[ch]) {
                case NODE_BEGIN:
                    if (html[ch + 1] == SLASH) {
                        parseNodeName(true);
                        return;
                    } else parseNode();
                    break;
                default: {
                    word = handleChar(word);
                    ch++;
                } break;
            }
        }
    }

    public void parse() throws Exception {
        if (html.length == 0 || html[ch] == EOF)
            return;
        StringBuilder word = new StringBuilder();
        while(html[ch] != EOF) {
            switch (html[ch]) {
                case NODE_BEGIN: parseNode(); break;
                default: {
                    word = handleChar(word);
                    ch++;
                } break;
            }
        }
    }
}
