package as.concrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

public class CallableCrawler implements Callable<List<String>> {
    private static String startUrl;

    public CallableCrawler(String startUrl) {
        this.startUrl = startUrl;
    }

    @Override
    public List<String> call() throws Exception {
        URL url = new URL(startUrl);
        URLConnection con = url.openConnection();
        con.setRequestProperty("User-Agent", "ConCrawler/0.1 Mozilla/5.0");
        List<String> result = new LinkedList<>();
        String contentType = con.getContentType();
        if (contentType != null && con.getContentType().startsWith("text/html")) {
            BufferedInputStream is = null;
            try {
                is = new BufferedInputStream(con.getInputStream());
                Document doc = Jsoup.parse(is, null, startUrl);
                Elements links = doc.select("a[href]");
                for (Element e : links) {
                    String linkString = e.absUrl("href");
                    if (linkString.startsWith("http")) {
                        result.add(linkString);
                    }
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                if (is instanceof Stream) {
                    is.close();
                }
            }
        }
        return result;
    }
}
