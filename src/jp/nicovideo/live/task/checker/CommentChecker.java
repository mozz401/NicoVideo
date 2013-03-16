package jp.nicovideo.live.task.checker;

import jp.nicovideo.live.common.NicoVideoConst;
import jp.nicovideo.live.handler.CommentCheckerHandler;
import jp.nicovideo.live.util.NicoVideoUtil;

/**
 * コメントデータのXMLファイルを精査する.
 *
 * @author mozz401
 *
 */
public class CommentChecker {

    /**
     * コメントデータのXMLファイルを精査する.
     *
     * @param args
     *            プログラムの引数
     */
    public static void main(String[] args) {
        check(NicoVideoConst.FILE_COMMENT);
    }

    /**
     * コメントデータのXMLファイルを精査する.
     *
     * @param path
     *            ファイルのパス
     */
    public static void check(String path) {
        NicoVideoUtil.parseXml(path, new CommentCheckerHandler());
        System.out.println("------------------- コメントデータ精査完了 -------------------");
    }

}
