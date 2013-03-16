package jp.nicovideo.live.data;

/**
 * Timeshift精査用データ.
 *
 * @author mozz401
 *
 */
/**
 * @author a
 *
 */
public final class TimeShiftCheckerData {

    /**
     * 前回処理した放送ID.
     */
    private String prevId;

    /**
     * 前回処理したコメント番号.
     */
    private int prevNo;

    /**
     * 放送終了コメントフラグ(true=放送終了コメントが存在する, false=放送終了コメントが存在しない).
     */
    private boolean isDisconnect;

    /**
     * prevIdを取得する.
     *
     * @return prevId
     */
    public String getPrevId() {
        return prevId;
    }

    /**
     * prevIdを設定する.
     *
     * @param prevId セットする prevId
     */
    public void setPrevId(String prevId) {
        this.prevId = prevId;
    }

    /**
     * prevNoを取得する.
     *
     * @return prevNo
     */
    public int getPrevNo() {
        return prevNo;
    }

    /**
     * prevNoを設定する.
     *
     * @param prevNo セットする prevNo
     */
    public void setPrevNo(int prevNo) {
        this.prevNo = prevNo;
    }

    /**
     * isDisconnectを取得する.
     *
     * @return isDisconnect(true=放送終了コメントが存在する, false=放送終了コメントが存在しない)
     */
    public boolean isDisconnect() {
        return isDisconnect;
    }

    /**
     * isDisconnectを設定する.
     *
     * @param isDisconnect セットする isDisconnect
     */
    public void setDisconnect(boolean isDisconnect) {
        this.isDisconnect = isDisconnect;
    }

}
