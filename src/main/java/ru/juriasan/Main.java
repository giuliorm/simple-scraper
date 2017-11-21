package ru.juriasan;

import ru.juriasan.domain.Data;
import ru.juriasan.util.CLI;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        try {
            CLI cli = new CLI(args);
            List<String> urls = cli.getUrls().stream().distinct().collect(Collectors.toList());
            int threads = 10;
            int queryInterval = 500;
            if (urls.size() < threads)
                threads = urls.size();
            int urlsPerThreads = urls.size() / threads;
            List<Producer> producers = new ArrayList<>();
            List<Consumer> consumers = new ArrayList<>();
            int urlInd = 0;
            Iterator<String> urlsIterator = urls.iterator();
            while(urlInd < urls.size()) {
                String[] subUrls = new String[urlsPerThreads];
                subUrls = urls.subList(urlInd, urlInd + urlsPerThreads).toArray(subUrls);
                Producer p = new Producer(urlInd / urlsPerThreads, queryInterval, subUrls);
                producers.add(p);
                urlInd = urlInd + urlsPerThreads;
                Consumer c = new Consumer(urlInd / urlsPerThreads, queryInterval,
                        new HashSet<String>(cli.getWords()), p);
                consumers.add(c);
            }
            ExecutorService producersExecutor = Executors.newFixedThreadPool(threads);
            ExecutorService consumersExecutor = Executors.newFixedThreadPool(threads);
            for (Producer p : producers)
                producersExecutor.execute(p);
            for (Consumer c : consumers)
                consumersExecutor.execute(c);
            while(true);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
