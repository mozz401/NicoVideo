package jp.nicovideo.live.task;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.nicovideo.live.common.NicoVideoConst;
import jp.nicovideo.live.handler.CommentCreatorHandler;
import jp.nicovideo.live.server.LiveMetaDataList;
import jp.nicovideo.live.server.NicoVideoFacade;
import jp.nicovideo.live.task.checker.CommentChecker;
import jp.nicovideo.live.util.FileList;
import jp.nicovideo.live.util.Merger;
import jp.nicovideo.live.util.NicoVideoUtil;

/**
 * コメントファイルを生成する.
 *
 * @author mozz401
 *
 */
public class CommentCreator {

    /**
     * コメントファイルを生成する.
     *
     * @param args
     *            プログラムの引数
     */
    public static void main(String[] args) {
        mkdir(NicoVideoConst.DIR_OUT);
        copy(NicoVideoConst.FILE_TEMPLATE, NicoVideoConst.FILE_COMMENT);
        create();
        merge();
        check(NicoVideoConst.FILE_COMMENT);
    }

    /**
     * ディレクトリを作成する.
     *
     * @param dir
     *            作成するディレクトリ
     */
    private static void mkdir(String dir) {
        if (NicoVideoUtil.mkdir(dir)) {
            System.out.println("------------------- 出力ディレクトリ作成完了 -------------------");
        }
    }

    /**
     * ファイルをコピーする.
     *
     * @param from
     *            コピー元ファイルのパス
     * @param to
     *            コピー先ファイルのパス
     */
    private static void copy(String from, String to) {
        NicoVideoUtil.copy(from, to);
        System.out.println("------------------- テンプレートコピー完了 -------------------");
    }

    /**
     * コメントファイルを生成する.
     */
    private static void create() {
        List<File> list = FileList.getFileList(NicoVideoConst.DIR_TARGET, ".xml");
        LiveMetaDataList liveMetaDataList = NicoVideoFacade.getLiveMetaDataList();
        Pattern idRegex = Pattern.compile(".*(lv\\d+).*");

        for (Iterator<File> ite = list.iterator(); ite.hasNext();) {
            String path = ite.next().getPath();
            Matcher idMatcher = idRegex.matcher(path);

            if (idMatcher.matches()) {
                System.out.println(path);
                NicoVideoUtil.parseXml(path, new CommentCreatorHandler(liveMetaDataList
                        .getData(idMatcher.group(1))));
            }
        }

        System.out.println("------------------ コメントファイル生成完了 ------------------");
    }

    /**
     * 重複コメントをマージする.
     */
    private static void merge() {
        Merger.merge();
        System.out.println("----------------------- マージ処理完了 -----------------------");
    }

    /**
     * コメントファイルを精査する.
     *
     * @param path
     *            ファイルのパス
     */
    private static void check(String path) {
        CommentChecker.check(path);
    }

}
