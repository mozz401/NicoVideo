package jp.nicovideo.live.data;

/**
 * ニコ生放送メタデータ.
 *
 * @author mozz401
 *
 */
public final class LiveMetaData {

    /**
     * 放送ID.
     */
    private String id;

    /**
     * 放送日時.
     */
    private String date;

    /**
     * 放送タイトル.
     */
    private String title;

    /**
     * idを取得する.
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * idを設定する.
     *
     * @param id
     *            セットするid
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * dateを取得する.
     *
     * @return date
     */
    public String getDate() {
        return date;
    }

    /**
     * dateを設定する.
     *
     * @param date
     *            セットするdate
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * titleを取得する.
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * titleを設定する.
     *
     * @param title
     *            セットするtitle
     */
    public void setTitle(String title) {
        this.title = title;
    }

}
