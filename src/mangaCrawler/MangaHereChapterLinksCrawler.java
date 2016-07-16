package mangaCrawler;

import helper.ChapterLink;
import helper.CrawlerType;

import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MangaHereChapterLinksCrawler extends ChapterLinksCrawler {
    public MangaHereChapterLinksCrawler(String listLink) {
        super(listLink, "http://www.mangahere.co/");
    }

    public CrawlerType.CRAWLER_TYPE getType() {
        return CrawlerType.CRAWLER_TYPE.MANGAHERE;
    }

    // Parse the manga name
    protected boolean parseMangaName() {
        if (this.listLink == null || this.doc == null)
            return false;

        Elements mangaNameElems = this.doc.select("h1");

        if (mangaNameElems.size() != 1)
            return false;

        this.mangaName = mangaNameElems.get(0).text();
        System.out.println("Manga name : " + this.mangaName);

        return true;
    }

    // Parse the chapter links
    protected boolean parseLink() {
        if (this.listLink == null || this.doc == null)
            return false;

        Elements listingElems = this.doc.select("div[class=detail_list]");

        // Return if the page has no listing
        if (listingElems.size() != 1) {
            System.out.println("No listing element");
            return false;
        }

        Element listingElem = listingElems.get(0);
        Elements chapterElems = listingElem.select("li");

        if (chapterElems.size() == 0)
            return false;

        int numChapters = chapterElems.size();

        // Iterate through each state listed in craiglist in reverse order because
        // newest chapter appears first but we want to crawl oldest chapter first
        for (int i = numChapters - 1; i >= 0; --i) {
            Elements chapterLinkNameElements = chapterElems.get(i).select("a");

            if (chapterLinkNameElements.size() <= 0)
                continue;

            String chapterLink = chapterLinkNameElements.get(0).attr("href")
                    .toString();

            try {
                URL chapterUrl = new URL(this.linkPrefix);
                URL absoluteChapterUrl = new URL(chapterUrl, chapterLink);

                String chapterName = chapterLinkNameElements.get(0).text();
                Elements extraNameElems = chapterElems.get(i).select("span[class=mr6]");
                if (extraNameElems.size() > 0)
                {
                    chapterName += " " + extraNameElems.get(0).text();
                }

                ChapterLink chapter = new ChapterLink(chapterName, absoluteChapterUrl.toString());


                this.chaptersList.add(chapter);
                System.out.println(chapterName + " : " + absoluteChapterUrl);
            } catch (MalformedURLException e) {
                continue;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        // MangaHere_ListpageParser crawler = new MangaHere_ListpageParser(
        // "http://www.mangahere.co/manga/koi_kaze/");
        //
        // if (crawler.parseInfo()) {
        //
        // }
    }
}
