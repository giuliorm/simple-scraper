package ru.juriasan.util.parser;

import ru.juriasan.util.parser.index.Index;
import ru.juriasan.util.parser.index.NodeIndex;

public class InMemoryParser extends Parser {

    private char[] html;
    private int ch = 0;


    /*private <K> Map<K, Integer> buildCountMap(Collection<K> values) {
        Map<K, Integer> map = new HashMap<K, Integer>();
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
    } */
    public InMemoryParser(String html, String... voidElements) {
        super(voidElements);
        this.html = html.toCharArray();
        this.voidElements.add("area");
        this.voidElements.add("base");
        this.voidElements.add("br");
        this.voidElements.add("col");
        this.voidElements.add("embed");
        this.voidElements.add("hr");
        this.voidElements.add("img");
        this.voidElements.add("input");
        this.voidElements.add("keygen");
        this.voidElements.add("link");
        this.voidElements.add("menuitem");
        this.voidElements.add("meta");
        this.voidElements.add("param");
        this.voidElements.add("source");
        this.voidElements.add("track");
        this.voidElements.add("wbr");
    }

    /*public Map<String, Integer> getWordsCount() {
        return cloneMap(wordsCount);
    }

    public Map<Character, Integer> getSymbolsCount() {
        return cloneMap(symbolsCount);
    } */

    @Override
    protected long position() {
        return ch;
    }

    @Override
    protected char prev() {
        return this.html[ch - 1];
    }
    @Override
    protected char next() {
        return this.html[ch + 1];
    }
    @Override
    protected char current() {
        return this.html[ch];
    }
    @Override
    protected void move() {
        ch++;
    }

    @Override
    protected boolean end() {
        return ch >= html.length;
    }

    private boolean indexIsValid(int index) {
        return index >=0 && index <= html.length - 1;
    }
    public String getNodeText(NodeIndex node) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Index text : node.getText()) {
            int index = (int)text.getStart();
            int end = (int)text.getEnd();
            if (indexIsValid(index) && indexIsValid(end))
                while(index < end) {
                    stringBuilder.append(html[index]);
                }
        }
        return stringBuilder.toString();
    }
    /*private void parseComment() throws Exception {
        move();
        boolean stop = false;
        if (html[ch] != DASH || html[ch + 1] != DASH)
            invalidDocument();
        while(!stop || ch < html.length) {
            switch (html[ch]) {
                case NODE_END:
                        move();
                        stop = true;
                        break;
                default:
                    move();
                    break;
            }
        }
        if (!stop) invalidDocument();
    }

    private void parseSingleNode() throws Exception {
        boolean stop = false;
        ch++;
        if (html[ch] == NODE_END)
            invalidDocument();
        while(!stop || ch < html.length) {
            switch (html[ch]) {
                case NODE_END:
                    stop = true;
                    break;
                default:
                    ch++;

            }
        }
        if (!stop) invalidDocument();
    }*/



    /*private void countSymbol() throws Exception {
        if (symbolsCount.containsKey(html[ch]))
            symbolsCount.put(html[ch], symbolsCount.get(html[ch]) + 1);
        else
            symbolsCount.put(html[ch], 1);
    }*/

    /*private StringBuilder handleNotLetterOrDigit(StringBuilder word) throws  Exception {
        String w = word.toString().toLowerCase();
        word = new StringBuilder();
        Integer value = wordsCount.getOrDefault(w, null);
        if (words.size() == 0 || words.contains(w)) {
            wordsCount.put(w, value == null ? 1 : value + 1);
        }
        return word;
    }

    private StringBuilder handleLetterOrDigit(StringBuilder word) throws Exception {
        word.append(html[ch]);
        return word;
    }

    private StringBuilder gatherSymbol(StringBuilder word) throws Exception {
        countSymbol();
        if (isLetterOrDigit(html[ch]))
            return handleLetterOrDigit(word);
        else
            return handleNotLetterOrDigit(word);
    }*/

    //returns node contents


    /*public void parse() throws Exception {
        if (html.length == 0)
            return;
        StringBuilder word = new StringBuilder();
        while(ch < html.length) {
            switch (html[ch]) {
                case NODE_BEGIN: parseNode(); break;
                default: {
                    if (isLetterOrDigit(html[ch]))
                        word = handleLetterOrDigit(word);
                    else
                        word = handleNotLetterOrDigit(word);
                    countSymbol();
                    ch++;
                } break;
            }
        }
        if (word.length() > 0)
            handleNotLetterOrDigit(word);
    }*/
}
