# kakeibo-cli



Javaで作ったCLIの家計簿アプリです。


## 概要

CLI上で支出を管理できるシンプルな家計簿アプリです。  
CSVファイルにデータを保存します。

現在は学習目的で作成しており、今後機能拡張予定です。

---

## 主な機能

- 支出の追加
- 支出の一覧表示
- 月次集計（カテゴリ別）
- 支出の削除
- 支出の編集

---

## 技術スタック

- Java
- LocalDate（java.time API）
- ファイル入出力（BufferedReader / BufferedWriter）
- CLI引数処理

---

## 実行方法

### コンパイル

```bash
javac Main.java Expense.java
```


### 実行例
```bash
java Main add 2026-02-01 食費 800 昼食
java Main list
java Main summary 2026-02
java Main delete 1
java Main edit 1 2026-02-02 食費 1200 夕食
```

### コマンド一覧
- add yyyy-mm-dd category amount memo
- list
- summary yyyy-mm
- delete id
- edit id yyyy-mm-dd category amount memo