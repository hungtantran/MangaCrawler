package helper;

import java.text.Normalizer;
import java.text.Normalizer.Form;

public class Helper {
    public static final char[] invalidFilenameChar = { '<', '>', ':', '"', '/',
            '\\', '|', '?', '*', '\1', '\2', '\3', '\4', '\5', '\6', '\7',
            '\t', '\10', '\11', '\12', '\13', '\14', '\15', '\16', '\17',
            '\20', '\21', '\22', '\23', '\24', '\25', '\26', '\27',
            '\30', '\31' };

    // Remove all the accent from a string
    public static String removeAccents(String text) {
        if (text == null) {
            return null;
        }

        text = text.replaceAll("[^\\p{ASCII}]", "");

        return Normalizer.normalize(text, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    // Format url (for e.g: " " => "%20")
    public static String formatUrl(String url) {
        if (url == null)
            return null;

        url = url.replaceAll(" ", "%20");

        return url;
    }

    // Put the current thread for wait for xxx milisec
    public static void threadWait(long milisec) {
        try {
            Thread.sleep(milisec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String extractSubString(String htmlContent, String prefix,
                                          String surfix) {
        if (htmlContent == null)
            return null;

        int nextPageImageIndex = htmlContent.indexOf(prefix);
        String imageLink = null;

        if (nextPageImageIndex != -1) {
            imageLink = htmlContent.substring(nextPageImageIndex
                    + prefix.length());

            nextPageImageIndex = imageLink.indexOf(surfix);
            if (nextPageImageIndex == -1)
                return null;

            imageLink = imageLink.substring(0, nextPageImageIndex);
        } else
            return null;

        imageLink = imageLink.trim();
        return imageLink;
    }

    // Download image and store it in folder
    public static boolean downloadAndStoreImage(Integer imageOrder,
                                                String imageLink, String chapterDirectory) {
        if (imageOrder == null || imageLink == null || chapterDirectory == null)
            return false;

        String imageLocation = chapterDirectory + "\\" + imageOrder + ".jpg";

        System.out.println("Download " + imageLink + " to " + imageLocation);
        if (!NetworkingFunctions.downloadImage(imageLink, imageLocation)) {
            System.out.println("Download fails");
            return false;
        }

        System.out.println("Download succeeds");
        return true;
    }

    // Clean up a file name
    public static String sanitizeFileDirectoryName(String fileName) {
        if (fileName == null)
            return null;

        // Trim white space
        fileName = fileName.trim();
        fileName = Helper.removeAccents(fileName);

        // Trim ending periods
        while (fileName.length() > 0 && fileName.charAt(fileName.length()-1) == '.') {
            fileName = fileName.substring(0, fileName.length()-1);
        }

        // File name can't be 0-length
        if (fileName.length() == 0)
            return null;

        int numInvalidChar = Helper.invalidFilenameChar.length;
        // Empty char
        char replaceChar = ' ';

        for (int i = 0; i < numInvalidChar; i++) {
            fileName = fileName.replace(Helper.invalidFilenameChar[i], replaceChar);
        }

        return fileName.trim();
    }
}
