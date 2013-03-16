package jp.nicovideo.live.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import jp.nicovideo.live.common.NicoVideoConst;
import jp.nicovideo.live.exception.NicoVideoException;

/**
 * プロパティ保持.
 *
 * @author mozz401
 *
 */
public final class NicoVideoProp {

    /**
     * プロパティ.
     */
    private static Properties prop = new Properties();

    static {
        try {
            prop.load(new FileReader(NicoVideoConst.FILE_CONFIG));
        } catch (IOException e) {
            throw new NicoVideoException(e);
        }
    }

    /**
     * プロパティの値を取得する.
     *
     * @param key
     *            プロパティのキー
     * @return キーに対応する値
     */
    public static String getProperty(String key) {
        return prop.getProperty(key);
    }

}
