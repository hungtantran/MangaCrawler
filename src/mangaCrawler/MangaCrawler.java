package mangaCrawler;

import helper.ChapterLink;
import helper.CrawlerType;
import helper.Helper;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MangaCrawler extends Thread {
    private static final char[] fileNameInvalidChar = { '\\', '/', ':', '*', '?', '<', '>', '|' };
    private static int waitTimeShort = 500;
    private static int waitTimeLong = 1000;

    private String rootDir = null;

    public MangaCrawler() {
    }

    public boolean setDownloadDirectory(String dir) {
        if (dir == null) {
            System.out.println("Must provide non-null download directory");
            return false;
        }

        File downloadDir = new File(dir);
        if (!downloadDir.exists()) {
            System.out.println("Must provide existing directory");
            return false;
        }

        if (!downloadDir.isDirectory()) {
            System.out.println("Must provide valid directory path");
            return false;
        }

        this.rootDir = dir;

        return true;
    }

    // Decide what type of crawler needed
    protected static CrawlerType.CRAWLER_TYPE populateType(String mangaLink) {
        if (mangaLink == null) {
            return CrawlerType.CRAWLER_TYPE.EMPTYTYPE;
        }

        if (mangaLink.contains("http://www.mangareader.net/")) {
            return CrawlerType.CRAWLER_TYPE.MANGAREADER;
        } else if (mangaLink.contains("http://truyen.vnsharing.net/")) {
            return CrawlerType.CRAWLER_TYPE.VNSHARING;
        } else if (mangaLink.contains("http://www.mangahere.co/")) {
            return CrawlerType.CRAWLER_TYPE.MANGAHERE;
        } else if (mangaLink.contains("http://pururin.com/")) {
            return CrawlerType.CRAWLER_TYPE.PURURIN;
        }

        return CrawlerType.CRAWLER_TYPE.EMPTYTYPE;
    }

    protected static IChapterLinksCrawler getLinkCrawler(String mangaLink, CrawlerType.CRAWLER_TYPE type) {
        if (mangaLink == null || type == CrawlerType.CRAWLER_TYPE.EMPTYTYPE) {
            return null;
        }

        switch (type) {
            case MANGAREADER:
                return new MangaReaderChapterLinksCrawler(mangaLink);

            case VNSHARING:
                return new VnSharingChapterLinksCrawler(mangaLink);

            case MANGAHERE:
                return new MangaHereChapterLinksCrawler(mangaLink);

            case PURURIN:
                return new PururinChapterLinksCrawler(mangaLink);

            default:
                return null;
        }
    }

    protected static IChapterDownloader getChapterDownloader(String mangaLink, CrawlerType.CRAWLER_TYPE type) {
        if (mangaLink == null || type == CrawlerType.CRAWLER_TYPE.EMPTYTYPE) {
            return null;
        }

        switch (type) {
            case MANGAREADER:
                return new MangaReaderChapterDownloader("http://www.mangareader.net/");

            case VNSHARING:
                return new VnSharingChapterDownloader("http://truyen.vnsharing.net/");

            case MANGAHERE:
                return new MangaHereChapterDownloader("http://www.mangahere.co/");

            case PURURIN:
                return new PururinChapterDownloader("http://pururin.com/");

            default:
                return null;
        }
    }

    protected static boolean isDownloadImageInParser(CrawlerType.CRAWLER_TYPE type) {
        if (type == CrawlerType.CRAWLER_TYPE.EMPTYTYPE) {
            return false;
        }

        switch (type) {
            case MANGAREADER:
                return true;

            case VNSHARING:
                return true;

            case MANGAHERE:
                return true;

            case PURURIN:
                return true;

            default:
                return false;
        }
    }

    protected static String typeName(CrawlerType.CRAWLER_TYPE type) {
        switch (type) {
            case MANGAREADER:
                return "MangaReader";

            case VNSHARING:
                return "Vnsharing";

            case MANGAHERE:
                return "Mangahere";

            case PURURIN:
                return "Pururin";

            default:
                return "";
        }
    }

    // Sanitize the filename
    public static String cleanFileName(String fileName) {
        if (fileName == null) {
            return null;
        }

        char emptyChar = ' ';
        for (int i = 0; i < MangaCrawler.fileNameInvalidChar.length; i++) {
            fileName.replace(MangaCrawler.fileNameInvalidChar[i], emptyChar);
        }

        return fileName;
    }

    private static boolean downloadChapter(
            IChapterDownloader chapterDownloader,
            boolean downloadImageInParser,
            String mangaDirectory,
            ChapterLink chapterLink,
            boolean skipExistingDir)
    {
        if (chapterLink == null) {
            return false;
        }

        String chapterName = chapterLink.getChapterName();
        String link = chapterLink.getLink();

        chapterName = MangaCrawler.cleanFileName(chapterName);
        chapterName = Helper.sanitizeFileDirectoryName(chapterName);
        if (chapterName == null) {
            System.out.println("Can't sanitize chapter name " + chapterLink.getChapterName());
            return false;
        }

        String chapterDirectory = mangaDirectory + "\\" + chapterName;

        // Create the chapter subfolder
        File chapterDir = new File(chapterDirectory);
        boolean dirExist = chapterDir.exists();
        if (!dirExist || !skipExistingDir) {
            if (!dirExist)
            {
                if (!chapterDir.mkdir())
                {
                    System.out.println("Subdirectory: " + chapterDirectory + " created");
                    return false;
                }
            }
            else
            {
                System.out.println("Redownload subdirectory: " + chapterDirectory);
            }

            if (!chapterDownloader.setChapterLink(link)) {
                System.out.println(link + " is invalid");
                return false;
            }

            Set<Integer> existingImagesOrder = new HashSet<Integer>();
            File[] files = new File(chapterDirectory).listFiles();
            int max = 0;
            for (File file : files)
            {
                String fileName = file.getName();
                fileName = fileName.substring(0, fileName.lastIndexOf('.'));

                try {
                    int curImageOrder = Integer.parseInt(fileName);

                    existingImagesOrder.add(curImageOrder);
                    if (curImageOrder > max)
                    {
                        max = curImageOrder;
                    }
                } catch (Exception e)
                {
                    System.out.println(fileName + " is not formatted according to standard");
                }
            }

            if (max > 0 && max == existingImagesOrder.size())
            {
                return false;
            }

            if (!chapterDownloader.crawlImageLinks(downloadImageInParser, chapterDirectory, existingImagesOrder)) {
                System.out.println("Can't parse images from link " + link);
                return false;
            }

            if (!downloadImageInParser) {
                // Iterate through all the images and download them to the sub-directory
                Map<Integer, String> imageOrderToLinkMap = chapterDownloader.getImageLinks();

                for (Map.Entry<Integer, String> imageEntry : imageOrderToLinkMap.entrySet()) {
                    Integer imageOrder = imageEntry.getKey();
                    // Skip existing image
                    if (existingImagesOrder.contains(imageOrder))
                    {
                        continue;
                    }

                    String imageLink = imageEntry.getValue();

                    if (!Helper.downloadAndStoreImage(imageOrder, imageLink, chapterDirectory)) {
                        continue;
                    }

                    Helper.threadWait(MangaCrawler.waitTimeShort);
                }
            }

            Helper.threadWait(MangaCrawler.waitTimeLong);
        } else {
            System.out.println("Subdirectory: " + chapterDirectory + " already exists");
            return false;
        }

        return true;
    }

    public boolean crawl(String mangaLink, boolean skipExistingDir) {
        if (this.rootDir == null) {
            System.out.println("Must provide download directory");
            return false;
        }

        if (mangaLink == null) {
            System.out.println("Must provide non null link");
            return false;
        }

        // Decide the type of the crawler
        CrawlerType.CRAWLER_TYPE mangaType = MangaCrawler.populateType(mangaLink);
        if (mangaType == CrawlerType.CRAWLER_TYPE.EMPTYTYPE) {
            System.out.println("Unsupported manga link type");
            return false;
        }

        // Instantiate the crawler object with the corresponding type
        IChapterLinksCrawler chapterLinksCrawler = MangaCrawler.getLinkCrawler(mangaLink, mangaType);
        IChapterDownloader chapterDownloader = MangaCrawler.getChapterDownloader(mangaLink, mangaType);
        boolean downloadImageInParser = MangaCrawler.isDownloadImageInParser(mangaType);

        if (chapterLinksCrawler == null || chapterDownloader == null) {
            System.out.println("Unsupported manga link type");
            return false;
        }

        if (!chapterLinksCrawler.crawl()) {
            return false;
        }

        String mangaName = chapterLinksCrawler.getMangaName();

        mangaName = MangaCrawler.cleanFileName(mangaName);
        mangaName = Helper.sanitizeFileDirectoryName(mangaName);
        if (mangaName == null) {
            System.out.println("Manganame unsupport: " + chapterLinksCrawler.getMangaName());
            return false;
        }

        String mangaDirectory = this.rootDir + MangaCrawler.typeName(mangaType);
        String lastString = "" + mangaDirectory.charAt(mangaDirectory.length() - 1);
        if (lastString != File.separator) {
            mangaDirectory += File.separator;
        }
        mangaDirectory += mangaName;

        System.out.println("Dir = " + mangaDirectory);

        // Create the manga parent folder
        File mangaDir = new File(mangaDirectory);
        if (!mangaDir.exists() && mangaDir.mkdirs()) {
            System.out.println("Directory: " + mangaDirectory + " created");
        } else if (!mangaDir.exists()) {
            System.out.println("Fail to create directory " + mangaDirectory);
            return false;
        } else if (mangaDir.exists()) {
            System.out.println("Directory: " + mangaDirectory + " already existed");
        }

        Helper.threadWait(MangaCrawler.waitTimeLong);

        // Iterate through all the chapter, create a directory for each and
        // download all the images to that directory
        while (true) {
            ChapterLink chapterLink = chapterLinksCrawler.getNext();

            // There is no more chapter to download
            if (chapterLink == null) {
                break;
            }

            System.out.println("Start download chapter: " + chapterLink.getChapterName() + " from link: " + chapterLink.getLink());
            boolean downloadResult = downloadChapter(chapterDownloader, downloadImageInParser, mangaDirectory, chapterLink, skipExistingDir);

            if (!downloadResult) {
                continue;
            }
        }

        return true;
    }
}
