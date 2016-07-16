package mangaCrawler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Document;

public abstract class ChapterDownloader implements IChapterDownloader {
    protected final int numRetryDownloadPage = 2;
    protected String linkPrefix = null;

    protected Document doc = null;
    protected String chapterLink = null;
    protected Map<Integer, String> imageOrderToLinkMap = null;

    protected ChapterDownloader(String chapterLink, String linkPrefix) {
        this.chapterLink = chapterLink;
        this.linkPrefix = linkPrefix;

        if (this.linkPrefix == null || this.chapterLink == null)
            return;

        if (!this.chapterLink.contains(this.linkPrefix))
            return;

        this.imageOrderToLinkMap = new HashMap<Integer, String>();
    }

    protected ChapterDownloader(String linkPrefix) {
        this.linkPrefix = linkPrefix;

        if (this.linkPrefix == null)
            return;

        this.imageOrderToLinkMap = new HashMap<Integer, String>();
    }

    public boolean setChapterLink(String chapterLink) {
        this.chapterLink = chapterLink;

        if (this.chapterLink == null || this.linkPrefix == null)
            return false;

        if (!this.chapterLink.contains(this.linkPrefix))
            return false;

        return true;
    }

    // Map between the image order in the chapter and its link
    public Map<Integer, String> getImageLinks() {
        return this.imageOrderToLinkMap;
    }

    public boolean crawlImageLinks() {
        return this.crawlImageLinks(false, null, null);
    }

    public abstract boolean crawlImageLinks(boolean downloadImageInParser, String chapDirectory, Set<Integer> existingImagesOrder);
}
