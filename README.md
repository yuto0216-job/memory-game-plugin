# 🃏MemoryGame Plugin

### 📌 概要  
Minecraft(Spigot)向けの神経衰弱を模したミニゲームが遊べるプラグインです。  
プレイヤーがランダムに並んだブロックをクリックし、各ブロックの数字のペアを揃えてスコアを競います。

---
 
### ✒️ 制作目的  
Java学習の一環として、実際に動くものの開発を通して技術を身につけることを目的としました。

---
 
### 🎥 プレイ画面  

![start](https://github.com/user-attachments/assets/3de61937-36f0-48eb-b45c-8e5d23fd26cd)  

![play](https://github.com/user-attachments/assets/1fef7716-b60f-458d-af3b-11c94c683b5d)  

![rank](https://github.com/user-attachments/assets/0dde3ec2-7ab0-4418-9298-a884119fce21)

---

### 🪄 機能・特徴

| コマンド | 説明 |
|----------|------|
| `/memorygame play <1-20>` | 指定ペア数でゲーム開始 |
| `/memorygame rank` | スコアランキング表示 |

- 制限時間: 30秒 画面上部に残り時間を表すバーが表示されます

- ブロックの出現: プレイヤーからX方向に指定したペア数の分並んで出現します

- マルチプレイ対応: 同じワールド内の別プレイヤーがゲーム中でも遊べます

- スコアの永続化: スコアをDBに保存します(MyBatis + MySQL使用)

- 演出: 正解時や終了時にサウンド/パーティクル演出が発生します


---

### 💎 技術スタック
| 区分 | 使用技術 | 詳細 |
| :--- | :--- | :--- |
| 言語 | Java | version 21 |
| API | Spigot API | version 1.21 |
| DB | MySQL | version 8.0 |
| ORM | MyBatis | version 3.5.19 |
| ビルド | Gradle | |
| 環境 | Minecraft | version 1.21 |
---
 
### 🛠️ 導入方法  
1. JARファイルを/pluginsフォルダへ配置

2. MySQLでplayer_score テーブルを作成

```sql
CREATE TABLE player_score (
  id INT AUTO_INCREMENT PRIMARY KEY,
  player_name VARCHAR(15) NOT NULL,
  score INT NOT NULL,
  registered_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

3. サーバーを起動し、生成された config.yml にDB情報を記述

---

### 💡 設計・意識した点

- 保守性・拡張性を意識し、役割ごとにクラスを分けました。(command/service/repository等)

- 例外処理　ブロック破壊、ゲーム中のログアウト、二重起動 各ケースに対して明示的な処理を実装しました。

- マルチプレイの対応　プレイヤーごとに`ExecutigPlayer`インスタンスを生成し、メモリ上で管理しました。個々の進行状況を持たせることでデータが競合せずにゲームが進行するようになっています。

- 設定の外部化　DB接続情報、制限時間やコマンド`/memorygame rank`による表示数、ブロックの間隔などはコードに埋め込まず、`config.yml`から読み込む仕様にしました。

