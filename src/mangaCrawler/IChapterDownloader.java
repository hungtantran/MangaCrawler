package mangaCrawler;

import java.util.Map;
import java.util.Set;

public interface IChapterDownloader {
    // Set what chapter link to download
    public boolean setChapterLink(String chapterLink);

    // Return a map between the image order in the chapter and its link
    public Map<Integer, String> getImageLinks();

    // Crawl the image links
    public boolean crawlImageLinks();

    // Crawl the image links
    public boolean crawlImageLinks(boolean downloadImageInParser, String chapDirectory, Set<Integer> existingImagesOrder);
}
