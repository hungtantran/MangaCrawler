package mangaCrawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import helper.Helper;
import helper.NetworkingFunctions;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PururinChapterDownloader extends ChapterDownloader {
    private final int numRetryDownloads = 2;
    private final int threadWait = 3000;

    public PururinChapterDownloader(String chapterLink) {
        super(chapterLink, "http://pururin.com/");
    }

    public boolean crawlImageLinks(boolean downloadImageInParser, String chapDir, Set<Integer> existingImagesOrder) {
        if (this.chapterLink == null || this.linkPrefix == null) {
            return false;
        }

        if (downloadImageInParser && chapDir == null) {
            return false;
        }

        System.out.println("Chapter directory = " + chapDir);

        String galleryLink = this.chapterLink.replaceAll("/gallery/", "/thumbs/");

        Document galleryDoc = NetworkingFunctions.downloadHtmlContent(galleryLink, this.numRetryDownloads);
        Elements thumblistElems = galleryDoc.select("ul[class=thumblist]");

        if (thumblistElems.size() != 1) {
            System.out.println("The number of thumbnail list is " +thumblistElems.size()  + " unexpected.");
            return false;
        }

        Element thumblistElem = thumblistElems.get(0);
        Elements thumbnailElems = thumblistElem.select("li");

        int page = 0;
        for (int i = 0; i < thumbnailElems.size(); ++i) {
            page++;

            Elements thumbnailLinks = thumbnailElems.get(i).select("a");

            if (thumbnailLinks.size() != 1) {
                System.out.println("The number of link in thumbnail elem is " + thumbnailLinks.size() + " unexpected.");
                continue;
            }

            // Find the imagePageLink = http://pururin.com/view/16911/042/lucrecia-v_43.html
            Element thumbnailLink = thumbnailLinks.get(0);

            URL imagePageLink = null;
            try {
                imagePageLink = new URL(this.linkPrefix);
                imagePageLink = new URL(imagePageLink, thumbnailLink.attr("href"));
            } catch (MalformedURLException e) {
                continue;
            }

            Document imagePageDoc = NetworkingFunctions.downloadHtmlContent(imagePageLink.toString(), this.numRetryDownloads);
            Elements imageElems = imagePageDoc.select("img[class=b]");

            if (imageElems.size() != 1) {
                System.out.println("The number of image elem in image page " + imageElems.size() + " unexpected.");
                continue;
            }

            // Find imageLink = http://pururin.com/f/d5ec76d7e458239a1de5f95d01b81a41a105bd8d/lucrecia-v-43.jpg
            URL imageLink = null;
            try {
                imageLink = new URL(this.linkPrefix);
                imageLink = new URL(imageLink, imageElems.get(0).attr("src"));
            } catch (MalformedURLException e) {
                continue;
            }

            System.out.println("Download Image Link " + imageLink.toString());
            if (downloadImageInParser) {
                if (existingImagesOrder != null && existingImagesOrder.contains(page)) {
                    continue;
                }

                Helper.downloadAndStoreImage(page, imageLink.toString(), chapDir);
            }

            Helper.threadWait(this.threadWait);
        }

        return true;
    }

    public static void main(String[] args) {
//		PururinChapterDownloader crawler = new PururinChapterDownloader("");
//
//		if (crawler.crawlImageLinks()) {
//
//		}
    }
}
