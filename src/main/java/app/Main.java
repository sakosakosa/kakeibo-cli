package app;

import model.Expense;
import dao.ExpenseDao;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:data/expenses.db");
             Statement stmt = conn.createStatement()) {

            String sql = """
                    CREATE TABLE IF NOT EXISTS expenses (
                    id INTEGER PRIMARY KEY,
                    date TEXT NOT NULL,
                    category TEXT NOT NULL,
                    amount INTEGER NOT NULL,
                    memo TEXT
                    );
                    """;

            stmt.execute(sql);

            System.out.println("テーブル作成完了");

        } catch (Exception e) {
            e.printStackTrace();
        }

        //引数チェック
        if (args.length == 0) {
            printUsage();
            return;
        }

        String command = args[0];

        //コマンド実行
        if ("add".equals(command)) {
            handleAdd(args);
        } else if ("list".equals(command)) {
            handleList();
        } else if ("summary".equals(command)) {
            handleSummary(args);
        } else if ("delete".equals(command)) {
            handleDelete(args);
        } else if ("edit".equals(command)) {
            handleEdit(args);
        } else {
            System.out.println("[ERROR] Unknown command:" + command);
            printUsage();
        }

    }

    //間違った使い方だった場合の説明表示
    private static void printUsage() {
        System.out.println("使い方:");
        System.out.println(" 支出の追加");
        System.out.println("  add yyyy-mm-dd category amount memo");
        System.out.println(" 支出の一覧表示");
        System.out.println("  list");
        System.out.println(" 支出の月次集計");
        System.out.println("  summary yyyy-mm");
        System.out.println(" 支出の削除");
        System.out.println("  delete id");
        System.out.println(" 支出の編集");
        System.out.println("  edit id yyyy-mm-dd category amount memo");
    }

    //addコマンドの詳細
    static void handleAdd(String[] args) {
        if (args.length < 5) {
            System.out.println("[ERROR] add yyyy-mm-dd 種別 メモ の形式で入力してください。");
            printUsage();
            return;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(args[1]);
        } catch (DateTimeParseException e) {
            System.out.println("[ERROR] 日付は yyyy-mm-ddの形式で入力してください");
            return;
        }

        String category = args[2];

        int amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] 金額は数字で入力してください");
            return;
        }

        String memo = args[4];

        ExpenseDao dao = new ExpenseDao();
        Expense expense = new Expense(0, date, category, amount, memo);

        dao.insert(expense);
        System.out.println("DBに追加しました！");

    }

    //listコマンドの詳細
    static void handleList() {

        ExpenseDao dao = new ExpenseDao();
        List<Expense> expenses = dao.findAll();

        if (expenses.isEmpty()) {
            System.out.println("まだ支出がありません");
            return;
        }

        int total = 0;

        for (Expense e : expenses) {
            System.out.println(e);
            total += e.getAmount();
        }

        System.out.println("--------------------------------");
        System.out.println("合計: " + total + "円");
    }

    //summaryコマンドの詳細
    static void handleSummary(String[] args) {
        if (args.length < 2) {
            System.out.println("[ERROR] summary yyyy-mmの形式で入力してください。");
            return;
        }

        String targetYm = args[1];

        ExpenseDao dao = new ExpenseDao();
        Map<String, Integer> summary = dao.findMonthlySummary(targetYm);

        if (summary.isEmpty()) {
            System.out.println("データがありません");
            return;
        }

        int total = 0;

        for (Map.Entry<String, Integer> entry : summary.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + "円");
            total += entry.getValue();
        }

        System.out.println("--------------------------");
        System.out.println(" total: " + total + "円");

    }

    //deleteコマンドの詳細
    static void handleDelete(String[] args) {
        if (args.length < 2) {
            System.out.println("[ERROR] delete idの形式で入力してください");
            return;
        }

        int targetId;
        try {
            targetId = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] idは数字で指定してください");
            return;
        }

        ExpenseDao dao = new ExpenseDao();
        boolean result = dao.deleteById(targetId);

        if (!result) {
            System.out.println("指定されたIDは見つかりませんでした");
        } else {
            System.out.println("削除しました");
        }

    }

    //editコマンドの詳細
    static void handleEdit(String[] args) {
        if (args.length < 6) {
            System.out.println("[ERROR] edit id yyyy-mm-dd category amount memo の形式で入力してください");
            return;
        }

        int targetId;
        try {
            targetId = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] idは数字で指定してください");
            return;
        }
        LocalDate newDate;
        try {
            newDate = LocalDate.parse(args[2]);
        } catch (DateTimeParseException e) {
            System.out.println("[ERROR] 日付は yyyy-mm-ddの形式で入力してください");
            return;
        }
        String newCategory = args[3];
        int newAmount;
        try {
            newAmount = Integer.parseInt(args[4]);
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] 金額は数字で入力してください");
            return;
        }

        String newMemo = args[5];

        ExpenseDao dao = new ExpenseDao();
        Expense updated = new Expense(targetId, newDate, newCategory, newAmount, newMemo);

        boolean result = dao.update(updated);

        if (!result) {
            System.out.println("指定したIDが見つかりません");
        } else {
            System.out.println("更新しました");
        }


    }

}