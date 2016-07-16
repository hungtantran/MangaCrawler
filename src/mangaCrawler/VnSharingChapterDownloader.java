package mangaCrawler;

import helper.NetworkingFunctions;

import java.util.Set;

import helper.Helper;

public class VnSharingChapterDownloader extends ChapterDownloader {
    public VnSharingChapterDownloader(String chapterLink) {
        super(chapterLink, "http://truyen.vnsharing.net/");
    }

    public boolean crawlImageLinks(boolean downloadImageInParser, String chapDir, Set<Integer> existingImagesOrder) {
        if (this.chapterLink == null || this.linkPrefix == null) {
            return false;
        }

        if (downloadImageInParser && chapDir == null) {
            return false;
        }

        // Download the listing page html content
        this.doc = NetworkingFunctions.downloadHtmlContent(this.chapterLink,
                this.numRetryDownloadPage);

        if (this.doc == null)
            return false;

        String htmlContent = this.doc.outerHtml();
        String imagePrefix = "lstImages.push(\"";

        int page = 1;
        while (true) {
            int startIndex = htmlContent.indexOf(imagePrefix);
            if (startIndex == -1) {
                break;
            }

            htmlContent = htmlContent.substring(startIndex + imagePrefix.length());

            int endIndex = htmlContent.indexOf("\"");
            if (endIndex == -1) {
                break;
            }

            String imageLink = htmlContent.substring(0, endIndex);
            imageLink = Helper.formatUrl(imageLink);

            System.out.println(page + " : " + imageLink);
            this.imageOrderToLinkMap.put(page, imageLink);

            if (downloadImageInParser) {
                if (existingImagesOrder != null && existingImagesOrder.contains(page)) {
                    continue;
                }

                Helper.downloadAndStoreImage(page, imageLink, chapDir);
            }

            page++;
        }

        return true;
    }

    public static void main(String[] args) {
        // VnSharing_ChapterpageParser crawler = new
        // VnSharing_ChapterpageParser(
        // "http://truyen.vnsharing.net/Truyen/midori-no-hibi-tiep-/-VNS_davidtran94--Midori-no-Hibi---v01-c001?id=8368");
        //
        // if (crawler.parseImageLinks()) {
        //
        // }
    }
}
