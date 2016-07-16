package mangaCrawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import helper.NetworkingFunctions;
import helper.Helper;

public class MangaReaderChapterDownloader extends ChapterDownloader {
    public MangaReaderChapterDownloader(String chapterLink) {
        super(chapterLink, "http://www.mangareader.net/");
    }

    private boolean isLastPageOfChapter(String htmlContent) {
        if (htmlContent == null)
            return true;

        // Parse out the link of the next page image. If there is no link, this
        // is the last page of the chapter
        String imageLink = Helper.extractSubString(htmlContent, "document['pu'] = '", "'");

        return (imageLink == null || imageLink.length() == 0) ? true : false;
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

            String htmlContent = this.doc.outerHtml();
            nextPageLink = Helper.extractSubString(htmlContent, "document['nl'] = '", "'");

            try {
                URL chapterUrl = new URL(this.linkPrefix);
                URL absoluteNextPageUrl = new URL(chapterUrl, nextPageLink);
                nextPageLink = absoluteNextPageUrl.toString();
            } catch (MalformedURLException e) {
                break;
            }

            // Parse out the image link in the current page
            Elements imageDivElems = this.doc.select("div[id=imgholder]");
            if (imageDivElems.size() != 1) {
                break;
            }

            Element imageDivElem = imageDivElems.get(0);
            Elements imageElems = imageDivElem.select("img");

            if (imageElems.size() != 1) {
                break;
            }

            String imageLink = imageElems.get(0).attr("src").trim();
            imageLink = Helper.formatUrl(imageLink);

            System.out.println(page + " : " + imageLink);
            this.imageOrderToLinkMap.put(page, imageLink);

            if (downloadImageInParser) {
                if (existingImagesOrder != null && existingImagesOrder.contains(page)) {
                    continue;
                }

                Helper.downloadAndStoreImage(page, imageLink, chapDir);
            }

            // If this is the last page, break out
            if (isLastPageOfChapter(htmlContent)) {
                break;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        // MangaReader_ChapterpageParser crawler = new
        // MangaReader_ChapterpageParser(
        // "http://www.mangareader.net/711-34090-1/unbalance-x-unbalance/chapter-4.html");
        //
        // if (crawler.parseImageLinks()) {
        //
        // }
    }
}
