import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
public class AmazonTest {
    public static void main(String[] args) {
        try {
            Document doc = Jsoup.connect("https://www.amazon.in/s?k=iphone+15")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .get();
            System.out.println("Success! Title: " + doc.title());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
