package mangaCrawler;

import helper.ChapterLink;
import helper.CrawlerType;
import helper.Helper;

import org.jsoup.select.Elements;

public class PururinChapterLinksCrawler extends ChapterLinksCrawler {
    public PururinChapterLinksCrawler(String listLink) {
        super(listLink, "http://pururin.com/");
    }

    public CrawlerType.CRAWLER_TYPE getType() {
        return CrawlerType.CRAWLER_TYPE.MANGAHERE;
    }

    // Parse the manga name
    protected boolean parseMangaName() {
        if (this.listLink == null || this.doc == null) {
            return false;
        }

        Elements mangaNameElems = this.doc.select("h1[class=otitle]");

        if (mangaNameElems.size() != 1) {
            return false;
        }

        this.mangaName = Helper.sanitizeFileDirectoryName(mangaNameElems.get(0).text());
        System.out.println("Manga name : " + this.mangaName);

        return true;
    }

    // Parse the chapter links
    protected boolean parseLink() {
        if (this.listLink == null || this.doc == null) {
            return false;
        }

        ChapterLink chapter = new ChapterLink(this.mangaName, this.listLink);

        this.chaptersList.add(chapter);
        System.out.println(this.mangaName + " : " + this.listLink);

        return true;
    }

    public static void main(String[] args) {
//		PururinChapterLinksCrawler crawler = new PururinChapterLinksCrawler("");
//
//		if (crawler.crawl()) {
//
//		}
    }
}
