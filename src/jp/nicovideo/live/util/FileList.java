package jp.nicovideo.live.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ファイルリスト.
 *
 * @author mozz401
 *
 */
public class FileList {

    /**
     * コンストラクタ.
     */
    private FileList() {
    }

    /**
     * ディレクトリ内のファイルをリストで取得する.
     *
     * @param files
     *            ディレクトリ内のファイル
     * @param comparator
     *            ファイルの並びを順序付けするコンパレータ
     * @param list
     *            コンパレータでソートされたファイルのリスト
     * @param ext
     *            取得するファイルの拡張子(可変長引数)
     */
    private static void getFileList(File[] files, Comparator<File> comparator, List<File> list, String... ext) {
        Arrays.sort(files, comparator);

        for (int i = 0; i < files.length; i++) {

            if (files[i].isDirectory()) {
                getFileList(files[i].listFiles(), comparator, list, ext);
            } else if (ext.length != 0) {

                for (String s : ext) {
                    if (files[i].getName().endsWith(s)) {
                        list.add(files[i]);
                    }
                }

            } else {
                list.add(files[i]);
            }
        }
    }

    /**
     * ディレクトリ内のファイルをリストで取得する.
     *
     * @param path
     *            ディレクトリのパス
     * @param ext
     *            取得するファイルの拡張子(可変長引数)
     * @return ディレクトリ名、ファイル名で昇順ソートされたファイルのリスト
     */
    public static List<File> getFileList(String path, String... ext) {
        List<File> list = new ArrayList<File>();

        getFileList(new File(path).listFiles(), new Comparator<File>() {
            private Pattern p = Pattern.compile(".*lv(\\d+).*_timeshift(\\d+).*");

            @Override
            public int compare(File f1, File f2) {
                String name1 = f1.getName();
                String name2 = f2.getName();

                // ディレクトリ同士の場合、数値で昇順ソート
                if (f1.isDirectory() && f2.isDirectory()) {
                    try {
                        return Integer.valueOf(name1).compareTo(Integer.valueOf(name2));
                    } catch (NumberFormatException e) {
                        // 数値でソートできない場合、文字列で昇順ソート
                        return name1.compareTo(name2);
                    }
                }

                Matcher m1 = p.matcher(name1);
                Matcher m2 = p.matcher(name2);

                // 放送IDが同じファイル同士の場合、タイムシフト番号を数値で昇順ソート
                if (m1.matches() && m2.matches() && m1.group(1).equals(m2.group(1))) {
                    return Integer.valueOf(m1.group(2)).compareTo(Integer.valueOf(m2.group(2)));
                }

                // それ以外の場合、ファイル名を文字列で昇順ソート(放送ID昇順)
                return name1.compareTo(name2);
            }
        }, list, ext);
        return list;
    }

}
