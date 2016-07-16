package mangaCrawler;

import java.util.Set;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import helper.NetworkingFunctions;
import helper.Helper;

public class MangaHereChapterDownloader extends ChapterDownloader {
    public MangaHereChapterDownloader(String chapterLink) {
        super(chapterLink, "http://www.mangahere.co/");
    }

    private boolean isLastPageOfChapter(String nextPageLink) {
        if (nextPageLink == null)
        {
            return true;
        }

        boolean lastPage = !nextPageLink.contains("http://www.mangahere.co/");

        return lastPage;
    }

    public boolean crawlImageLinks(boolean downloadImageInParser, String chapDir, Set<Integer> existingImagesOrder) {
        if (this.chapterLink == null || this.linkPrefix == null) {
            return false;
        }

        if (downloadImageInParser && chapDir == null) {
            return false;
        }

        int page = 0;
        String nextPageLink = this.chapterLink;
        while (true) {
            page++;

            // Download the listing page html content
            this.doc = NetworkingFunctions.downloadHtmlContent(nextPageLink, this.numRetryDownloadPage);

            if (this.doc == null) {
                break;
            }

            Elements readImgElems = this.doc.select("section[class=read_img]");
            if (readImgElems.size() != 1) {
                break;
            }

            Element readImgElem = readImgElems.get(0);
            Elements nextPageElems = readImgElem.select("a");
            Elements imageLinkElems = readImgElem.select("img");
            if (nextPageElems.size() != 1 || imageLinkElems.size() != 1) {
                break;
            }

            String imageLink = imageLinkElems.get(0).attr("src");
            nextPageLink = nextPageElems.get(0).attr("href");

            System.out.println(page + " : " + imageLink);
            this.imageOrderToLinkMap.put(page, imageLink);

            if (downloadImageInParser) {
                if (existingImagesOrder != null && existingImagesOrder.contains(page)) {
                    continue;
                }

                Helper.downloadAndStoreImage(page, imageLink, chapDir);
            }

            // If this is the last page, break out
            if (isLastPageOfChapter(nextPageLink)) {
                break;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        // MangaHere_ChapterpageParser crawler = new
        // MangaHere_ChapterpageParser(
        // "http://www.mangahere.co/manga/koi_kaze/v05/c034/");
        //
        // if (crawler.parseImageLinks()) {
        //
        // }
    }
}
