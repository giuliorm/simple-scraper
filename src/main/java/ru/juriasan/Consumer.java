package ru.juriasan;

import ru.juriasan.domain.Data;
import ru.juriasan.domain.Page;
import ru.juriasan.util.Parser;

import java.util.*;

public class Consumer implements Runnable {

    private long queryInterval;
    private int threadNumber;
    private List<Producer> subscriptions;
    private Set<String> words;

    public Consumer(int threadNumber, long queryIntervalMillis,
                    Set<String> words,
                    Producer... subscriptions) {
        this.threadNumber = threadNumber;
        this.queryInterval = queryIntervalMillis;
        this.subscriptions = new LinkedList<>(Arrays.asList(subscriptions));
        this.words = words;
    }

    public synchronized void subscribe(Producer producer) {
        this.subscriptions.add(producer);
    }

    public synchronized void unsubscribe(Producer producer) {
        this.subscriptions.remove(producer);
    }

    private Data handlePage(Page page) {
        try {
            String pageData = page.getData();
            Parser parser = new Parser(pageData, words);
            parser.parse();
            return new Data(page.getUrl(), parser.getWordsCount(), parser.getSymbolsCount());
        }
        catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void run()  {
        try {
            while (true) {
                for (Producer producer : this.subscriptions) {
                    Set<Page> pages = producer.getPages();
                    for (Page page : pages) {
                        Data d = handlePage(page);
                        if (d != null) {
                            System.out.println(String.format("Consumer thread %d: ------%s-------",
                                    this.threadNumber, d.getUrl()));
                            for(String word : d.getWordCount().keySet())
                                System.out.print(String.format("%s: %d, ", word, d.getWordCount().get(word)));
                            System.out.println();
                            //for(Character c : d.getSymbolsCount().keySet())
                           //     System.out.print(String.format("%c: %d, ", c, d.getSymbolsCount().get(c)));
                        }
                        else
                            System.out.println(String.format("Consumer thread %d: data from url %s is null",
                                    this.threadNumber, page.getUrl()));
                    }
                    Thread.sleep(queryInterval);
                }

            }
        }
        catch (InterruptedException ex) {
            System.out.println(String.format("Consumer thread %d is interrupted and exiting.", this.threadNumber));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
