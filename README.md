# ニコ生コメント抽出ツール

## 概要
ニコニコ生放送のコメントファイルから運営コメントやアンケート結果等を抽出します。

## 動作環境
本ツールはJava6にてコンパイルされています。そのため動作にはJava6以降の実行環境が必要です。

## 抽出対象
* 運営コメント
* アンケート結果
* 放送画面で流した動画のタイトル&URL
* 放送画面で表示したニコニコ静画のURL

## 抽出形式
以下のようなXML形式で抽出されます。

    <broadcast>
      <!-- ↓開演時間(1970年1月1日0時0分0秒GMTからの経過ミリ秒)です。 -->
      <date>1282921200000</date>
      <!-- ↓放送タイトルです。 -->
      <title>キラたんがただいまでちゅ～お買い物紹介でしゅ～</title>
      <!-- ↓運営コメントです。 -->
      <comment>みつばちマーチ</comment>
    </broadcast>

    <broadcast>
      <date>1357572300000</date>
      <title>眠くなりたぃ？ほな朗読したげﾘｭＮＡ♪</title>
      <!-- ↓アンケート結果は選択肢毎のパーセンテージが出力されます。 -->
      <comment>【アンケ】良い夢・・・みれそう。かな？ もちろんさベイビー(18.4%) ノンノンハニー寝かさない(81.6%)</comment>
    </broadcast>

    <broadcast>
      <date>1296226800000</date>
      <title>Hamar＠キラーの雑談枠</title>
      <!-- ↓放送画面で流した動画はタイトルとURLが出力されます。 -->
      <comment>ファイナルファンタジー１０ エンディング [高画質] http://nico.ms/sm5500974</comment>
    </broadcast>

    <broadcast>
      <date>1311951600000</date>
      <title>ケミー＆キラー～ＧＯ　ＮＥＸＴ！～</title>
      <!-- ↓放送画面で表示したニコニコ静画はURLが出力されます。 -->
      <comment>http://seiga.nicovideo.jp/seiga/im714513</comment>
    </broadcast>

## 使い方
1. プロジェクトをEclipseに取り込む。（[http://d.hatena.ne.jp/ishibashits/20110627/1309193856](http://d.hatena.ne.jp/ishibashits/20110627/1309193856)が参考になります。）
2. 「resource/config.properties」をテキストエディタで開き、次の2つのキーの値を変更する。
	1. targetキーの値をニコ生のコメントファイルを置いているディレクトリに変更する。(例：target = C:/Users/a/Documents/NicoVideo)
	2. archivesキーの値を放送元のコミュニティIDに変更する。(例：archives = co1144186)
3. jp.nicovideo.live.task.CommentCreator.javaを実行する。

これでoutディレクトリ内にcomment.xmlというファイル名で抽出結果が出力されます。

## 注意事項
データを抽出するファイルのファイル名には放送IDが含まれている必要があります。（例：lv122300926_1.xml）
