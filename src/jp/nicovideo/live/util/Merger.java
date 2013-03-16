package jp.nicovideo.live.util;

import java.util.HashMap;

import jp.nicovideo.live.common.NicoVideoConst;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * マージ.
 *
 * @author mozz401
 *
 */
public class Merger {

    /**
     * コンストラクタ.
     */
    private Merger() {
    }

    /**
     * 同一枠の同一コメントをマージする.
     */
    public static void merge() {
        Document doc = NicoVideoUtil.getDocument(NicoVideoConst.FILE_COMMENT);
        NodeList list = doc.getDocumentElement().getElementsByTagName("broadcast");
        String currentId = "";
        HashMap<String, Boolean> mergeMap = null;
        int len = list.getLength();

        for (int i = len - 1; i >= 0; i--) {
            Element broadcast = (Element) list.item(i);
            String id = getNodeValue(broadcast, "id");
            String comment = getNodeValue(broadcast, "comment");

            if (id.equals(currentId)) {

                if (mergeMap.containsKey(comment)) {
                    doc.getDocumentElement().removeChild(broadcast);
                } else {
                    mergeMap.put(comment, true);
                }

            } else {
                currentId = id;
                mergeMap = new HashMap<String, Boolean>();
                mergeMap.put(comment, true);
            }

            broadcast.removeChild(broadcast.getElementsByTagName("id").item(0));
            // id要素を除去した後にできる不要なテキストノードを除去
            broadcast.removeChild(broadcast.getFirstChild());
        }

        removeTextNode(doc);
        NicoVideoUtil.craeteXmlFromDom(doc, NicoVideoConst.FILE_COMMENT);
    }

    /**
     * テキストノードの値を取得する.
     *
     * @param parent
     *            親要素
     * @param tagName
     *            タグ名
     * @return テキストノードの値
     */
    private static String getNodeValue(Element parent, String tagName) {
        return parent.getElementsByTagName(tagName).item(0).getFirstChild().getNodeValue();
    }

    /**
     * マージした後にできる不要なテキストノードを除去する.
     *
     * @param doc
     *            対象のDOM
     */
    private static void removeTextNode(Document doc) {
        doc.normalize();
        Element root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        int len = nodeList.getLength();

        for (int i = len - 1; i >= 0; i--) {
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.TEXT_NODE) {
                root.removeChild(node);
            }
        }

    }
}
