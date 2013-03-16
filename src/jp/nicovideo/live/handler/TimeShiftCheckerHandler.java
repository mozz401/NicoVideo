package jp.nicovideo.live.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.nicovideo.live.common.NicoVideoConst;
import jp.nicovideo.live.data.TimeShiftCheckerData;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * TimeshiftのXMLファイルを精査するハンドラ.
 * kakorokuRecorderでDLしたXMLファイルはコメントが欠落していたり妥当なXMLでない可能性があるため精査する.
 *
 * @author mozz401
 *
 */
public class TimeShiftCheckerHandler extends DefaultHandler {
    /**
     * XMLファイルのパス.
     */
    private String path;

    /**
     * コメントデータを保持するバッファ.
     */
    private StringBuffer commentBuf = new StringBuffer();

    /**
     * 運営コメントフラグ(true=運営コメントである, false=運営コメントでない).
     */
    private boolean isAdminComment;

    /**
     * 放送IDを表す正規表現パターン.
     */
    private static Pattern broadcastIdRegex = Pattern.compile(".*(lv\\d+).*");

    /**
     * Timeshift精査用データ.
     */
    private static TimeShiftCheckerData tsData = new TimeShiftCheckerData();

    /**
     * コメント欠落を判断する基準値.
     */
    private static final int CRITERIA_NO = 100;

    /**
     * コンストラクタ.
     *
     * @param path
     *            XMLファイルのパス
     */
    public TimeShiftCheckerHandler(String path) {
        this.path = path;
    }

    /*
     * (非 Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#startDocument()
     */
    @Override
    public void startDocument() {
        Matcher broadcastIdMatcher = broadcastIdRegex.matcher(path);

        if (broadcastIdMatcher.matches()) {
            String currentId = broadcastIdMatcher.group(1);

            if (!currentId.equals(tsData.getPrevId())) {

                if (tsData.getPrevId() != null && !tsData.isDisconnect()) {
                    System.out.println("\t問題\t⇒\t途中～最後までのコメントが連続欠落(" + tsData.getPrevNo() + "より後)");
                }

                tsData.setPrevId(currentId);
                tsData.setPrevNo(0);
                tsData.setDisconnect(false);
            }
        }

        System.out.println(path);
    }

    /*
     * (非 Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        String premium = attributes.getValue("premium");
        String no = attributes.getValue("no");

        if (no != null) {
            int currentNumber = Integer.parseInt(no);

            if (tsData.getPrevNo() == 0 && currentNumber >= CRITERIA_NO) {
                System.out.println("\t問題\t⇒\t最初のコメントが連続欠落(" + currentNumber + "より前)");
            } else if (currentNumber - tsData.getPrevNo() >= CRITERIA_NO) {
                System.out.println("\t問題\t⇒\t途中のコメントが連続欠落(" + tsData.getPrevNo() + "～" + currentNumber + "の間)");
            }
            // CRITERIA_NO未満の連続欠落は開場時の追い出しによるものとして欠落扱いしない。

            tsData.setPrevNo(currentNumber);
        }

        isAdminComment = premium != null && premium.equals(NicoVideoConst.PREMIUM);
    }

    /*
     * (非 Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (isAdminComment) {
            commentBuf.append(new String(ch, start, length));
        }
    }

    /*
     * (非 Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (isAdminComment) {
            if ("/disconnect".equals(commentBuf.toString())) {
                tsData.setDisconnect(true);
            }

            commentBuf = new StringBuffer();
            isAdminComment = false;
        }
    }

}
