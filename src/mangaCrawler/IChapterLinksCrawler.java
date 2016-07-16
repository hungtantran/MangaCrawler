package mangaCrawler;

import helper.ChapterLink;

public interface IChapterLinksCrawler {
    // Crawl the information including manga name, chapter name, chapter link, etc...
    public boolean crawl();

    // Return the name of the manage
    public String getMangaName();

    // Return the next chapter with its link enclosed in a ChapterLink object
    // Function returns null if there is no next chapter.
    public ChapterLink getNext();
}
