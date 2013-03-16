package jp.nicovideo.live.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jp.nicovideo.live.exception.NicoVideoException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * ユーティリティ.
 *
 * @author mozz401
 *
 */
public final class NicoVideoUtil {

    /**
     * コンストラクタ.
     */
    private NicoVideoUtil() {
    }

    /**
     * ディレクトリを作成する.
     *
     * @param dir
     *            作成するディレクトリ
     * @return
     *            ディレクトリ作成したらtrue、作成しなかったらfalse
     */
    public static boolean mkdir(String dir) {
        File file = new File(dir);

        if (!file.exists()) {
            return file.mkdir();
        }

        return false;
    }

    /**
     * ファイルをコピーする.
     *
     * @param from
     *            コピー元ファイルのパス
     * @param to
     *            コピー先ファイルのパス
     * @param src
     *            入力チャネル
     * @param dest
     *            出力チャネル
     * @throws IOException
     *             入出力例外
     */
    private static void copy(String from, String to, FileChannel src, FileChannel dest) throws IOException {
        try {
            src = new FileInputStream(from).getChannel();
            dest = new FileOutputStream(to).getChannel();
            src.transferTo(0, src.size(), dest);
        } finally {
            if (src != null) {
                src.close();
            }

            if (dest != null) {
                dest.close();
            }
        }
    }

    /**
     * ファイルをコピーする.
     *
     * @param from
     *            コピー元ファイルのパス
     * @param to
     *            コピー先ファイルのパス
     */
    public static void copy(String from, String to) {
        FileChannel src = null;
        FileChannel dest = null;

        try {
            copy(from, to, src, dest);
        } catch (IOException ex) {
            src = dest = null;
            throw new NicoVideoException(ex);
        }
    }

    /**
     * XMLファイルからDOMツリーを構築する.
     *
     * @param path
     *            XMLファイルのパス
     * @return XMLファイルから構築されたDOMツリー
     */
    public static Document getDocument(String path) {
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return db.parse(path);
        } catch (Exception e) {
            throw new NicoVideoException(e);
        }
    }

    /**
     * DOMツリーからXMLファイルを生成する.
     *
     * @param doc
     *            DOMツリー
     * @param path
     *            出力ファイルのパス
     */
    public static void craeteXmlFromDom(Document doc, String path) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(new File(path)));
        } catch (Exception e) {
            throw new NicoVideoException(e);
        }
    }

    /**
     * XMLファイルをSAXハンドラでパースする.
     *
     * @param path
     *            XMLファイルのパス
     * @param handler
     *            SAXハンドラ
     */
    public static void parseXml(String path, DefaultHandler handler) {
        try {
            XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            reader.setContentHandler(handler);
            reader.parse(new InputSource(new BufferedReader(new FileReader(path))));
        } catch (Exception e) {
            throw new NicoVideoException(e);
        }
    }

}
