package as.concrawler;

import java.util.*;
import java.util.concurrent.*;

public class ParCrawler implements Crawler {
    private static final ExecutorService exec = Executors.newFixedThreadPool(10);
    private static final int MAX_VISITS = 20;

    @Override

    public List<String> crawl(String startURL) {
        Queue<String> urlsToVisit = new LinkedList<>();
        HashSet<String> visitedUrls = new HashSet<>();
        CompletionService<List<String>> inbox = new ExecutorCompletionService<List<String>>(exec);
        urlsToVisit.add(startURL);

        while (visitedUrls.size() < MAX_VISITS && !urlsToVisit.isEmpty()) {
            for(String url : urlsToVisit){
                visitedUrls.add(url);
                inbox.submit(new CallableCrawler(url));
            }
            urlsToVisit.clear();
            try {
                inbox.take().get().forEach((url) -> {
                    if (!urlsToVisit.contains(url) && !visitedUrls.contains(url)) urlsToVisit.add(url);
                });
                Future<List<String>> future;

                while((future = inbox.poll()) != null){
                    future.get().forEach((url) ->{
                        if (!urlsToVisit.contains(url) && !visitedUrls.contains(url)) urlsToVisit.add(url);
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>(visitedUrls);
    }
}
