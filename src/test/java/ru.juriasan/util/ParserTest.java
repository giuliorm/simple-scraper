package ru.juriasan.util;


import org.junit.Ignore;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.juriasan.Consumer;
import ru.juriasan.Producer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ParserTest {

    @Ignore
    public void consumerTest() {
        Producer p = new Producer(0, 500, "http://cnn.com");
        new Thread(p).start();
        Consumer c = new Consumer(0, 200, new HashSet<String>() {{ this.add ("value"); }}, p);
        c.run();
    }

    @Test
    public void realTest() {
        StringBuilder html = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new FileReader("src/test/resources/real.html"))) {
            String line;
            while((line = r.readLine()) != null)
                html.append(line);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
        Parser p = new Parser(html.toString(), new HashSet<String>() );
        try {
            p.parse();
            for( String word : p.getWordsCount().keySet())
                System.out.println(String.format("%s: %d", word, p.getWordsCount().get(word)));
            for (Character c : p.getSymbolsCount().keySet())
                System.out.print(String.format("%c: %d,", c, p.getSymbolsCount().get(c)));
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void noTagTest() {
        String html = "value";
        Parser p = new Parser(html, new HashSet<String>() {{ this.add ("value"); }});
        try {
            p.parse();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        Map<String, Integer> wordsExpected = new HashMap<String, Integer>();
        wordsExpected.put("value", 1);
        Assert.assertEquals(wordsExpected, p.getWordsCount());
        Map<Character, Integer> expectedSymbols = new HashMap<Character, Integer>();
        expectedSymbols.put('v', 1);
        expectedSymbols.put('a', 1);
        expectedSymbols.put('l', 1);
        expectedSymbols.put('u', 1);
        expectedSymbols.put('e', 1);
        Assert.assertEquals(p.getSymbolsCount(), expectedSymbols);
    }

    @Test
    public void twoClosingOuterTagsTest() {
        String html = "</html><t>value t1</t>value node1</html>";
        Parser p = new Parser(html, new HashSet<String>() {{ this.add ("value"); }});
        try {
            p.parse();
            Assert.fail();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void missingOpeningOuterTagTest() {
        String html = "<t>value t1</t>value node1</html>";
        Parser p = new Parser(html, new HashSet<String>() {{ this.add ("value"); }});
        try {
            p.parse();
            Assert.fail();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void parserValidDocTest() {
        String html = "<html><node1><t>value t1</t>value node1</node1></html>";
        Parser p = new Parser(html, new HashSet<String>() {{ this.add ("value"); }});
        try {
            p.parse();
        }
        catch (Exception ex) {
            Assert.fail();
        }
        Map<Character, Integer> symbols = p.getSymbolsCount();
        Map<String, Integer> words = p.getWordsCount();

        Map<Character, Integer> expectedSymbols = new HashMap<Character, Integer>();
        expectedSymbols.put('v', 2);
        expectedSymbols.put('a', 2);
        expectedSymbols.put('l', 2);
        expectedSymbols.put('u', 2);
        expectedSymbols.put('e', 3);
        expectedSymbols.put(' ', 2);
        expectedSymbols.put('1', 2);
        expectedSymbols.put('n', 1);
        expectedSymbols.put('o', 1);
        expectedSymbols.put('d', 1);
        expectedSymbols.put('t', 1);
        Assert.assertEquals(symbols, expectedSymbols);
        Map<String, Integer> wordsExpected = new HashMap<String, Integer>();
        wordsExpected.put("value", 2);
        //wordsExpected.put("t1", 1);
        // wordsExpected.put("node1", 1);
        Assert.assertEquals(words, wordsExpected);
    }

}
