MemoryGame Plugin 

概要
Minecraft(Spigot)向けの神経衰弱を模したゲームが遊べるプラグインです。
プレイヤーがランダムに並んだブロックをクリックし、各ブロックの数字のペアを揃えてスコアを競います。

プレイ画面
to be continued....

ゲーム内コマンド
'/memorygame play <1-20>' 指定したペア数でゲーム開始
'/memorygame rank' スコアランキング(Top3)※ 表示 

機能・特徴
制限時間: 30秒※ 画面上部に残り時間を表すバーを表示
ブロックの出現: プレイヤーからX方向に指定したペア数の分並んで出現
マルチプレイ対応: 同じワールド内の別プレイヤーがゲーム中でも遊べます
スコアの永続化: スコアをDBに保存します(MyBatis + MySQL使用)
演出: 正解時や終了時に演出が発生します
（※はconfig.ymlから変更できる要素です）

技術構成
言語: 
Java 21　
API: 
Spigot API 1.21　
OR Mapper: 
Mybatis ver 3.5.19　
DB: 
MySQL ver 8.0　
ビルドツール:
Gradle　
 

導入方法
1.JARファイルを/pluginsフォルダへ配置
2.MySQLでplayer_score テーブルを作成

CREATE TABLE player_score (
id INT AUTO_INCREMENT PRIMARY KEY,
player_name VARCHAR(15) NOT NULL,
score INT NOT NULL,
registered_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP);

3.サーバーを起動

------------------------------------------------------------------------------------

制作目的
Java学習の一環として、実際に動くものの開発を通して以下の力を身につけることを目的としました。

-言語の理解の向上:
　基本文法/イベント処理/オブジェクト指向への理解を深める

-設計・責務分離の実践:
　機能要件・非機能要件の設定、役割ごとのクラス分け(command/service/repository等)

-DBの理解:
　OR Mapperの仕組みとSQL言語の理解、Mybatisを用いたコードとの連携
 
-例外に対しての対応:
　途中退出やブロック破壊などの例外ケースでも動作が破綻しないような仕組み作り

-変更、拡張性の考慮:
　config.ymlによる外部設定化や仕様変更に対応しやすいコード構成を意識



