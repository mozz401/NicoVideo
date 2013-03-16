package jp.nicovideo.live.task.checker;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import jp.nicovideo.live.common.NicoVideoConst;
import jp.nicovideo.live.handler.TimeShiftCheckerHandler;
import jp.nicovideo.live.util.FileList;
import jp.nicovideo.live.util.NicoVideoUtil;

/**
 * TimeshiftのXMLファイルを精査する.
 * kakorokuRecorderでDLしたXMLファイルはコメントが欠落していたり妥当なXMLでない可能性があるため.
 *
 * @author mozz401
 *
 */
public class TimeShiftChecker {

    /**
     * TimeshiftのXMLファイルを精査する.
     *
     * @param args プログラムの引数
     */
    public static void main(String[] args) {
        List<File> list = FileList.getFileList(NicoVideoConst.DIR_TARGET, ".xml");

        for (Iterator<File> ite = list.iterator(); ite.hasNext();) {
            String path = ite.next().getPath();
            NicoVideoUtil.parseXml(path, new TimeShiftCheckerHandler(path));
        }

        System.out.println("-------------------- タイムシフト精査完了 --------------------");
    }

}
