package jp.nicovideo.live.data;

import java.util.List;

/**
 * ニコ生アンケートデータ.
 *
 * @author mozz401
 *
 */
public final class LiveVoteData {
    /**
     * アンケート.
     */
    private String questionnaire;

    /**
     * アンケートの選択肢
     */
    private List<String> option;

    /**
     * questionnaireを取得する.
     *
     * @return questionnaire
     */
    public String getQuestionnaire() {
        return questionnaire;
    }

    /**
     * questionnaireを設定する.
     *
     * @param questionnaire セットするquestionnaire
     */
    public void setQuestionnaire(String questionnaire) {
        this.questionnaire = questionnaire;
    }

    /**
     * optionを取得する.
     *
     * @return option
     */
    public List<String> getOption() {
        return option;
    }

    /**
     * optionを設定する.
     *
     * @param option セットするoption
     */
    public void setOption(List<String> option) {
        this.option = option;
    }

}
