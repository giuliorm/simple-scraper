package ru.juriasan;

import ru.juriasan.domain.Data;
import ru.juriasan.util.CLI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        try {
            CLI cli = new CLI(args);
            List<String> urls = cli.getUrls();
            int threads = 10;
            int queryInterval = 500;
            if (urls.size() < threads)
                threads = urls.size();
            int urlsPerThreads = urls.size() / threads;
            List<Producer> producers = new ArrayList<>();
            List<Consumer> consumers = new ArrayList<>();
            int urlInd = 0;
            while(urlInd < urls.size()) {
                String[] suburls = urls.subList(urlInd, urlInd + urlsPerThreads - 1).toArray(new String[urlsPerThreads]);
                Producer p = new Producer(urlInd / urlsPerThreads, queryInterval, suburls);
                producers.add(p);
                urlInd = urlInd + urlsPerThreads;
                Consumer c = new Consumer(urlInd / urlsPerThreads, queryInterval,
                        new HashSet<>(cli.getWords()), p);
                consumers.add(c);
            }
            ExecutorService producersExecutor = Executors.newFixedThreadPool(threads);
            ExecutorService consumersExecutor = Executors.newFixedThreadPool(threads);
            for (Producer p : producers)
                producersExecutor.execute(p);
            for (Consumer c : consumers)
                consumersExecutor.execute(c);
            while(true);
            /*while(true) {
                for (Consumer c : consumers)
                    for (Data data : c.getData()) {
                        System.out.println(String.format("------- %s -------", data.getUrl()));
                        for(String word : data.getWordCount().keySet()) {
                            System.out.println(String.format("    %s: %d", word, data.getWordCount().get(word)));
                        }
                    }
            }*/
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
