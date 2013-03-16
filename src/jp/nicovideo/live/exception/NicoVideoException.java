package jp.nicovideo.live.exception;

/**
 * 例外.
 *
 * @author mozz401
 *
 */
@SuppressWarnings("serial")
public class NicoVideoException extends RuntimeException {

    /**
     * コンストラクタ.
     *
     * @param cause
     *            例外
     */
    public NicoVideoException(Throwable cause) {
        super(cause);
    }

}
