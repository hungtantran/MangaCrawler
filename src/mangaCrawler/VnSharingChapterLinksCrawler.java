package mangaCrawler;

import helper.ChapterLink;
import helper.CrawlerType;

import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class VnSharingChapterLinksCrawler extends ChapterLinksCrawler {
    public VnSharingChapterLinksCrawler(String listLink) {
        super(listLink, "http://truyen.vnsharing.net/");
    }

    public CrawlerType.CRAWLER_TYPE getType() {
        return CrawlerType.CRAWLER_TYPE.VNSHARING;
    }

    // Parse the manga name
    protected boolean parseMangaName() {
        if (this.listLink == null || this.doc == null)
            return false;

        Elements mangaNameElems = this.doc.select("a[class=bigchar]");

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

        Elements listingElems = this.doc.select("table[class=listing]");

        // Return if the page has no listing
        if (listingElems.size() != 1) {
            System.out.println("No listing element");
            return false;
        }

        Element listingElem = listingElems.get(0);
        Elements chapterElems = listingElem.select("tr");

        if (chapterElems.size() == 0)
            return false;

        int numChapters = chapterElems.size();

        // Iterate through each state listed in craiglist
        for (int i = 0; i < numChapters; i++) {
            Elements chapterLinkNameElements = chapterElems.get(i).select("a");

            if (chapterLinkNameElements.size() <= 0)
                continue;

            String chapterLink = chapterLinkNameElements.get(0).attr("href")
                    .toString();

            try {
                URL chapterUrl = new URL(this.linkPrefix);
                URL absoluteChapterUrl = new URL(chapterUrl, chapterLink);
                String chapterName = chapterLinkNameElements.get(0).text();

                ChapterLink chapter = new ChapterLink(chapterName, absoluteChapterUrl.toString());

                this.chaptersList.add(chapter);
            } catch (MalformedURLException e) {
                continue;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        // VnSharing_ListpageParser crawler = new VnSharing_ListpageParser(
        // "http://truyen.vnsharing.net/Truyen/Ai-Kora");
        //
        // if (crawler.parseInfo()) {
        //
        // }
    }
}
