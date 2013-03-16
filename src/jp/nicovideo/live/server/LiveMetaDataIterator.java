package jp.nicovideo.live.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.nicovideo.live.common.NicoVideoConst;
import jp.nicovideo.live.data.LiveMetaData;
import jp.nicovideo.live.exception.NicoVideoException;

/**
 * ニコ生メタデータのイテレーター.
 *
 * @author mozz401
 *
 */
class LiveMetaDataIterator implements Iterator<LiveMetaData> {
    /**
     * ニコ生メタデータリスト.
     */
    private List<LiveMetaData> list = new ArrayList<LiveMetaData>();

    /**
     * ニコ生メタデータリストのイテレーター.
     */
    private Iterator<LiveMetaData> iterator = list.iterator();

    /**
     * カレントページ.
     */
    private int currentPage = 1;

    /**
     * 番兵.
     */
    private static final int SENTINEL = 100;

    /**
     * ニコ生メタデータリストを生成する.
     *
     * @throws IOException
     *             入出力例外
     */
    private void createLiveMetaDataList() throws IOException {
        BufferedReader reader = null;
        list = new ArrayList<LiveMetaData>();

        try {
            StringBuffer queryBuf = new StringBuffer("?page=" + currentPage++ + "&bias=0");
            URL url = new URL(NicoVideoConst.URL_LIVE_ARCHIVES + queryBuf);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            Pattern titleRegex = Pattern.compile("(?:<[^>]*>)*<a href=\".*(lv\\d+).*?\">([^<]*)(?:<[^>]*>)*");
            Pattern dateRegex = Pattern.compile("(?:<[^>]*>)*([^<]*)(?:<[^>]*>)*");
            Pattern startedTimeRegex = Pattern.compile("(?:.*?<br>)*\\s*([^<]*)<br>$");
            Pattern erRegex = Pattern.compile("&#(\\d+);");
            LiveMetaData data = null;
            String line;
            boolean isDate = false;
            boolean isTitle = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.equals("生放送履歴はありません") || currentPage > SENTINEL) {
                    break;
                }

                if (isDate) {
                    data = new LiveMetaData();
                    StringBuffer buf = new StringBuffer(dateRegex.matcher(line).replaceFirst("$1"));

                    if ((line = reader.readLine()) != null) {
                        buf.append(" " + startedTimeRegex.matcher(line.trim()).replaceFirst("$1"));
                    }

                    data.setDate(buf.toString());
                } else if (isTitle) {
                    Matcher m = titleRegex.matcher(line);

                    if (m.matches()) {
                        data.setId(m.group(1));
                        String title = m.group(2);
                        Matcher erMatcher = erRegex.matcher(title);

                        while (erMatcher.find()) {
                            char c = (char) Integer.parseInt(erMatcher.group(1));
                            title = erMatcher.replaceFirst(new String(new char[] { c }));
                        }

                        data.setTitle(title);
                    }

                    list.add(data);
                }

                try {
                    isDate = line.endsWith("<td class=\"date\">");
                    isTitle = line.endsWith("<td class=\"title\">");
                } catch (NullPointerException e) {
                    throw new NicoVideoException(e);
                }
            }

        } finally {
            if (reader != null) {
                reader.close();
            }
        }

    }

    /**
     * ニコ生メタデータリストのイテレーターを更新する.
     */
    private void updateIterator() {
        iterator = list.iterator();
    }

    /*
     * (非 Javadoc)
     *
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
        if (!iterator.hasNext()) {
            try {
                createLiveMetaDataList();
                updateIterator();
                return iterator.hasNext();
            } catch (Exception e) {
                throw new NicoVideoException(e);
            }
        } else {
            return true;
        }
    }

    /*
     * (非 Javadoc)
     *
     * @see java.util.Iterator#next()
     */
    @Override
    public LiveMetaData next() {
        return iterator.next();
    }

    /*
     * (非 Javadoc)
     *
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
        iterator.remove();
    }

}
