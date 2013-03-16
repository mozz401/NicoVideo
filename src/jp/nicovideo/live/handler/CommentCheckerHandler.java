package jp.nicovideo.live.handler;

import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * コメントデータのXMLファイルを精査するハンドラ.
 *
 * @author mozz401
 *
 */
public class CommentCheckerHandler extends DefaultHandler {
    /**
     * コメントデータを保持するバッファ.
     */
    private StringBuffer commentBuf = new StringBuffer();

    /**
     * 前回処理したタイトルデータを保持するバッファ.
     */
    private StringBuffer prevTitleBuf = new StringBuffer();

    /**
     * 現在処理しているタイトルデータを保持するバッファ.
     */
    private StringBuffer currentTitleBuf = new StringBuffer();

    /**
     * コメントタグフラグ(true=コメントタグである, false=コメントタグでない).
     */
    private boolean isCommentTag;

    /**
     * タイトルタグフラグ(true=タイトルタグである, false=タイトルタグでない).
     */
    private boolean isTitleTag;

    /**
     * kakorokuRecorderの文字化けコメントを表す正規表現パターン.
     */
    private static Pattern garbledRegex = Pattern.compile(".*\\uFFFD.*");

    /**
     * Twitterでリプライになるアットマークを表す正規表現パターン.
     */
    private static Pattern atmarkRegex = Pattern.compile(".*(?:@|＠)\\w+.*");

    /**
     * 単純なタグを表す正規表現パターン.
     */
    private static Pattern tagRegex = Pattern.compile(".*<.*>.*");

    /*
     * (非 Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        isCommentTag = qName.equals("comment");
        isTitleTag = qName.equals("title");
    }

    /*
     * (非 Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (isCommentTag) {
            commentBuf.append(new String(ch, start, length));
        } else if (isTitleTag) {
            currentTitleBuf.append(new String(ch, start, length));
        }
    }

    /*
     * (非 Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (isCommentTag) {
            checkComment();
        } else if (isTitleTag) {
            checkTitle();
        }
    }

    /**
     * コメントデータを検査する.
     */
    private void checkComment() {
        String comment = commentBuf.toString();

        // スキップor処理されなかった運営コマンドを検出
        if (comment.startsWith("/")) {
            System.out.println("運営コマンド検出\t\t⇒\t" + comment);
        }

        // kakorokuRecorderの文字化けコメントを検出
        if (garbledRegex.matcher(comment).matches()) {
            System.out.println("文字化けコメント検出\t\t⇒\t" + comment);
        }

        // コメント内の@を検出(Twitterで@関連になるため)
        if (atmarkRegex.matcher(comment).matches()) {
            System.out.println("コメント内の @ 検出\t\t⇒\t" + comment);
        }

        // コメント内のタグを検出
        if (tagRegex.matcher(comment).matches()) {
            System.out.println("コメント内のタグ検出\t\t⇒\t" + comment);
        }
        commentBuf = new StringBuffer();
        isCommentTag = false;
    }

    /**
     * タイトルデータを検査する.
     */
    private void checkTitle() {
        if (!currentTitleBuf.toString().equals(prevTitleBuf.toString())) {

            // タイトル内の@を検出(Twitterで@関連になるため)
            if (atmarkRegex.matcher(currentTitleBuf).matches()) {
                System.out.println("タイトル内の @ 検出\t\t⇒\t" + currentTitleBuf);
            }

            prevTitleBuf = currentTitleBuf;
        }

        currentTitleBuf = new StringBuffer();
        isTitleTag = false;
    }

}
