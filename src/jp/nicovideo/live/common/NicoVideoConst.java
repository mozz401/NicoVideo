package jp.nicovideo.live.common;

import jp.nicovideo.live.util.NicoVideoProp;

/**
 * 定数.
 *
 * @author mozz401
 *
 */
public final class NicoVideoConst {

    /**
     * コンストラクタ.
     */
    private NicoVideoConst() {
    }

    /**
     * 設定ファイルキー
     *
     * @author mozz_401
     *
     */
    private enum NicoVideoConfig {
        target, archives, kakoroku
    }

    /** ニコニコ動画のプレミアムユーザーを表す値 */
    public static final String PREMIUM = "3";

    /** 生放送履歴ページURL */
    public static final String URL_LIVE_ARCHIVES = "http://com.nicovideo.jp/live_archives/"
            + NicoVideoProp.getProperty(NicoVideoConfig.archives.name());

    /** コメントデータを抽出する対象ディレクトリ */
    public static final String DIR_TARGET = NicoVideoProp.getProperty(NicoVideoConfig.target.name());

    /** kakorokuRecorderの録画ディレクトリ */
    public static final String DIR_KAKOROKU = NicoVideoProp.getProperty(NicoVideoConfig.kakoroku.name());

    /** 出力ディレクトリ */
    public static final String DIR_OUT = "out/";

    /** コメントファイル */
    public static final String FILE_COMMENT = DIR_OUT + "comment.xml";

    /** リソースディレクトリ */
    public static final String DIR_RESOURCE = "resource/";

    /** テンプレートファイル */
    public static final String FILE_TEMPLATE = DIR_RESOURCE + "template.xml";

    /** 設定ファイル */
    public static final String FILE_CONFIG = DIR_RESOURCE + "config.properties";

}
