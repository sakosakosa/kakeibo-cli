\# kakeibo-cli (SQLite Version)



Javaで作成したCLI型の家計簿アプリです。  

SQLiteを使用してデータを永続化しています。



---



\## 概要



コマンドラインから支出を管理できるアプリです。  

SQLiteデータベースに保存することで、CSV版より堅牢な構成に進化しました。



---



\## 主な機能



\- 支出の追加

\- 支出の一覧表示

\- 月次集計（カテゴリ別）

\- 支出の削除

\- 支出の編集



---



\## 技術スタック



\- Java 17

\- SQLite

\- JDBC

\- Maven

\- DAOパターン

\- JUnit

\- fat JAR（実行可能JAR生成）



---



\## ビルド方法



```bash

mvn clean package

```



\## 実行方法

java -jar target/kakeibo-cli-1.0-SNAPSHOT.jar list



\## コマンド例

```bash

java -jar target/kakeibo-cli-1.0-SNAPSHOT.jar add 2026-02-01 食費 800 昼食

java -jar target/kakeibo-cli-1.0-SNAPSHOT.jar list

java -jar target/kakeibo-cli-1.0-SNAPSHOT.jar summary 2026-02

java -jar target/kakeibo-cli-1.0-SNAPSHOT.jar delete 1

java -jar target/kakeibo-cli-1.0-SNAPSHOT.jar edit 1 2026-02-02 食費 1200 夕食

```







