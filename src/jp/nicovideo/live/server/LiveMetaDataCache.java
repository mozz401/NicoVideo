package jp.nicovideo.live.server;

import java.util.HashMap;
import java.util.Iterator;

import jp.nicovideo.live.data.LiveMetaData;

/**
 * ニコ生メタデータのキャッシュ.
 *
 * @author mozz401
 *
 */
class LiveMetaDataCache implements LiveMetaDataList {
    /**
     * キャッシュ.
     */
    private static HashMap<String, LiveMetaData> cache    = new HashMap<String, LiveMetaData>();

    /**
     * ニコ生メタデータリストのイテレーター.
     */
    private static Iterator<LiveMetaData>        iterator = new LiveMetaDataIterator();

    /**
     * ニコ生メタデータを取得する.
     *
     * @param id
     *            放送ID
     * @return ニコ生メタデータ
     */
    @Override
    public LiveMetaData getData(String id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }

        LiveMetaData data = null;

        while (iterator.hasNext()) {
            data = iterator.next();
            cache.put(data.getId(), data);

            if (id.equals(data.getId())) {
                break;
            }
        }

        return data;
    }

    /**
     * ニコ生メタデータを取得する.
     *
     * @return ニコ生メタデータ
     */
    @Override
    public LiveMetaData next() {
        LiveMetaData data = null;

        if (iterator.hasNext()) {
            data = iterator.next();
            cache.put(data.getId(), data);
        }

        return data;
    }

}
