package mangaCrawler;

public class Main {
    public static void main(String[] args) {
        MangaCrawler crawler = new MangaCrawler();
        crawler.setDownloadDirectory("D:\\OneDrive\\Books\\");

        String[] downloads = {
                "http://www.mangareader.net/one-piece",
                "http://www.mangareader.net/bleach"
        };

        for (int i = 0; i < downloads.length; ++i)
        {
            if (!crawler.crawl(downloads[i], false /* skipExistingDir */)) {
                continue;
            }
        }
    }
}
