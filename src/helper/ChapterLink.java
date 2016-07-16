package helper;

public class ChapterLink {
    private String chapterName = null;
    private String link = null;

    public ChapterLink() {
    }

    public ChapterLink(String chapterName, String link) {
        this.chapterName = chapterName;
        this.link = link;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
