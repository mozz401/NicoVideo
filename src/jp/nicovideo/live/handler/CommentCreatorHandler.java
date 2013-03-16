package jp.nicovideo.live.handler;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.nicovideo.live.common.NicoVideoConst;
import jp.nicovideo.live.data.LiveMetaData;
import jp.nicovideo.live.data.LiveVoteData;
import jp.nicovideo.live.exception.NicoVideoException;
import jp.nicovideo.live.util.NicoVideoUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CommentCreatorHandler extends DefaultHandler {
    /**
     * 処理対象外の運営コメントプレフィックス
     */
    private String[] ignoreCommandPrefix = { "/compact", "/disconnect", "/hb ifseetno", "/info", "/koukoku",
            "/loadplugin", "/netduetto", "/nicoden_pc", "/play rtmp:", "/press show_tw", "rtmp://", "/sound", "/stop",
            "/sub soundonly", "/swap", "/vote stop", "/telop", "/playsound", };

    /**
     * コメントバッファ
     */
    private StringBuffer commentBuf = new StringBuffer();

    /**
     * DOM
     */
    private Document doc;

    /**
     * ニコ生メタデータ
     */
    private LiveMetaData data;

    /**
     * ニコ生アンケートデータ.
     */
    private LiveVoteData vote;

    /**
     * 運営コメントフラグ(true=運営コメントである, false=運営コメントでない)
     */
    private boolean isAdminComment;

    /**
     * 投票フラグ(true=投票開始している, false=投票開始していない)
     */
    private boolean isVoteStarted;

    /**
     * アンケートコメントの番号
     */
    private int voteNo;

    /**
     * 動画再生コメントの正規表現
     */
    private static Pattern playSmileRegex = Pattern.compile(".*?:(sm\\d+).*?\"(.*)\"");

    /**
     * ニコニコ静画コメントの正規表現
     */
    private static Pattern playSeigaRegex = Pattern.compile(".*?:(\\d+).*");

    /**
     * アンケート及び選択肢の正規表現
     */
    private static Pattern voteRegex = Pattern.compile("\"(.*?)\"");

    /**
     * アンケート結果の正規表現
     */
    private static Pattern voteResultRegex = Pattern.compile("\\d+");

    /**
     * リンクの正規表現
     */
    private static Pattern linkRegex = Pattern.compile("(.*?)(?:https?://|<).*?a href=\"(.*?)\".*>(.*)?");

    /**
     * 通し番号
     */
    private static int currentNo;

    /**
     * コンストラクタ.
     *
     * @param data
     *            ニコ生放送メタデータ
     */
    public CommentCreatorHandler(LiveMetaData data) {
        this.data = data;
    }

    /**
     * 処理対象のコメントを判定する.
     *
     * @param comment
     *            判定する文字列
     * @return 処理対象である場合はtrue、処理対象でない場合はfalse
     */
    private boolean isTarget(String comment) {
        for (String prefix : ignoreCommandPrefix) {
            if (comment.startsWith(prefix)) {
                return false;
            }
        }

        return true;
    }

    /**
     * アンケート及び選択肢をセットする.
     *
     * @param comment
     *            コメント
     */
    private void setVoteItem(String comment) {
        vote = new LiveVoteData();
        Matcher voteMacher = voteRegex.matcher(comment);

        if (voteMacher.find()) {
            vote.setQuestionnaire(voteMacher.group(1));
        }

        List<String> voteItemList = new ArrayList<String>();

        while (voteMacher.find()) {
            voteItemList.add(voteMacher.group(1));
        }

        vote.setOption(voteItemList);
    }

    /**
     * アンケートコメントをアップデートする.
     *
     * @param comment
     *            コメント
     */
    private void updateVoteComment(String comment) {
        Matcher voteResultMacher = voteResultRegex.matcher(comment);
        List<String> result = new ArrayList<String>();
        List<String> option = vote.getOption();
        int size = option.size();
        int sum = 0;

        for (int i = 0; i < size && voteResultMacher.find(); i++) {
            String match = voteResultMacher.group();
            result.add(match);
            sum += Integer.parseInt(match);
        }

        StringBuffer buf = new StringBuffer("【アンケ】" + vote.getQuestionnaire());

        for (int i = 0; i < size; i++) {
            buf.append(" " + option.get(i));
            BigDecimal percent;

            if (sum != 0) {
                percent = new BigDecimal(Double.valueOf(result.get(i)) / sum * 100);
            } else {
                percent = new BigDecimal(0);
            }

            buf.append("(" + percent.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue() + "%)");
        }

        Element broadcast = (Element) doc.getDocumentElement().getElementsByTagName("broadcast").item(voteNo);
        broadcast.getElementsByTagName("comment").item(0).getFirstChild().setNodeValue(buf.toString());
    }

    /**
     * 動画再生コメントを処理する.
     *
     * @param comment
     *            コメント
     * @return 動画再生コメントの文字列
     */
    private String playSmile(String comment) {
        Matcher m = playSmileRegex.matcher(comment);
        m.matches();
        return new StringBuffer(m.group(2) + " http://nico.ms/" + m.group(1)).toString();
    }

    /**
     * ニコニコ静画コメントを処理する.
     *
     * @param comment
     *            コメント
     * @return ニコニコ静画コメントの文字列
     */
    private String playSeiga(String comment) {
        Matcher m = playSeigaRegex.matcher(comment);
        m.matches();
        return new StringBuffer("http://seiga.nicovideo.jp/seiga/im" + m.group(1)).toString();
    }

    /**
     * broadcast要素を生成する.
     *
     * @param comment
     *            コメント
     * @return broadcast要素
     */
    private Element createBroadcastElement(String comment) {
        Element eBroadcast = doc.createElement("broadcast");
        eBroadcast.appendChild(createElement("id", data.getId()));
        eBroadcast.appendChild(createDateElement());
        eBroadcast.appendChild(createElement("title", data.getTitle()));
        eBroadcast.appendChild(createCommentElement(comment));
        return eBroadcast;
    }

    /**
     * 要素を生成する.
     *
     * @param name
     *            要素名
     * @param text
     *            要素のテキスト
     * @return 生成した要素
     */
    private Element createElement(String name, String text) {
        Element element = doc.createElement(name);
        element.appendChild(doc.createTextNode(text));
        return element;
    }

    /**
     * date要素を生成する.
     *
     * @return date要素
     */
    private Element createDateElement() {
        Element eDate = doc.createElement("date");
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd 開演：HH:mm");

        try {
            eDate.appendChild(doc.createTextNode(String.valueOf(format.parse(data.getDate()).getTime())));
        } catch (ParseException ex) {
            throw new NicoVideoException(ex);
        }

        return eDate;
    }

    /**
     * comment要素を生成する.
     *
     * @param comment
     *            コメント
     * @return comment要素
     */
    private Element createCommentElement(String comment) {
        Element eComment = doc.createElement("comment");
        Matcher linkMatcher = linkRegex.matcher(comment);

        while (linkMatcher.find()) {
            // TODO: 1つのコメントに複数のリンクが含まれてるケース
            comment = linkMatcher.group(1) + linkMatcher.group(2) + linkMatcher.group(3);
            linkMatcher = linkRegex.matcher(comment);
        }

        eComment.appendChild(doc.createTextNode(urlShorten(comment)));
        return eComment;
    }

    /**
     * URLを短縮する.
     *
     * @param str
     *            URLが含まれている文字列
     * @return URL短縮結果
     */
    private String urlShorten(String str) {
        // YouTubeを短縮する場合は最初の「&」を「?」に置き換えないと404になる
        if (str.startsWith("http://www.youtube.com/")) {
            str = str.replaceFirst("&amp;", "?");
        }

        return str.replaceAll("http://(?:www|live)\\.nicovideo\\.jp/watch", "http://nico.ms").replace(
                "http://www.youtube.com/watch?v=", "http://youtu.be/").replace(
                "http://www.amazon.co.jp", "http://amazon.jp");
    }

    /*
     * (非 Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#startDocument()
     */
    @Override
    public void startDocument() {
        doc = NicoVideoUtil.getDocument(NicoVideoConst.FILE_COMMENT);
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

        if (premium != null && NicoVideoConst.PREMIUM.equals(premium)) {
            isAdminComment = true;
        }

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
            String comment = commentBuf.toString();
            commentBuf = new StringBuffer();
            isAdminComment = false;

            if (!isTarget(comment)) {
                return;
            }

            if (comment.startsWith("/vote start")) {
                // 結果を出さなかったアンケートは無視する
                setVoteItem(comment);

                if (isVoteStarted) {
                    return;
                } else {
                    isVoteStarted = true;
                    voteNo = currentNo;
                }
            } else if (comment.startsWith("/vote showresult per")) {
                if (isVoteStarted) {
                    updateVoteComment(comment);
                    isVoteStarted = false;
                }

                return;
            } else if (comment.startsWith("/play smile:")) {
                comment = playSmile(comment);
            } else if (comment.startsWith("/play seiga:")) {
                comment = playSeiga(comment);
            }

            doc.getDocumentElement().appendChild(createBroadcastElement(comment));
            currentNo++;
        }
    }

    /*
     * (非 Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#endDocument()
     */
    @Override
    public void endDocument() throws SAXException {
        NicoVideoUtil.craeteXmlFromDom(doc, NicoVideoConst.FILE_COMMENT);
    }

}
