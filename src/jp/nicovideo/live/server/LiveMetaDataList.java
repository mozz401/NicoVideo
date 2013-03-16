package jp.nicovideo.live.server;

import jp.nicovideo.live.data.LiveMetaData;

/**
 * ニコ生メタデータのリスト.
 *
 * @author mozz401
 *
 */
public interface LiveMetaDataList {
    public LiveMetaData getData(String id);
    public LiveMetaData next();
}
