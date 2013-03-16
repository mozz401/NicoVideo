package jp.nicovideo.live.server;

/**
 * サーバーアクセスのファサード.
 *
 * @author mozz401
 *
 */
public class NicoVideoFacade {
    /**
     * ニコ生メタデータのリスト.
     */
    private static LiveMetaDataList list = new LiveMetaDataCache();

    /**
     * コンストラクタ.
     */
    private NicoVideoFacade() {
    }

    public static LiveMetaDataList getLiveMetaDataList() {
        return list;
    }
}
