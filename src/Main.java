import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
//summar
public class Main {

	static final String FILE_NAME = "../data/expenses.csv";

	public static void main(String[] args) {

		//引数チェック
		if (args.length == 0) {
			printUsage();
			return;
		}

		String command = args[0];

		//addコマンド実行時
		if ("add".equals(command)) {
			handleAdd(args);
		} else if ("list".equals(command)) {
			handleList();
		} else if ("summary".equals(command)) {
			handleSummary(args);
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
		System.out.println(" 支出の追加");
		System.out.println("  list");
		System.out.println(" 支出の月次集計");
		System.out.println("  summary yyyy-mm");
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
		} catch(DateTimeParseException e) {
			System.out.println("[ERROR] 日付は yyyy-mm-ddの形式で入力してください");
			return;
		}
		
		String category = args[2];
		
		int amount;
		try {
			amount = Integer.parseInt(args[3]);
		} catch(NumberFormatException e) {
			System.out.println("[ERROR] 金額は数字で入力してください");
			return;
		}
		
		String memo = args[4];
		
		String line = date + "," + category + "," + amount + "," + memo;

		try (FileWriter fw = new FileWriter(FILE_NAME, true);
				BufferedWriter bw = new BufferedWriter(fw)) {

			bw.write(line);
			bw.newLine();

			System.out.println("追加しました！");
		} catch (IOException e) {
			System.out.println("[ERROR] ファイル書き込みに失敗しました");
		}

	}

	//listコマンドの詳細
	static void handleList() {
		if (!Files.exists(Paths.get(FILE_NAME))) {
			System.out.println("まだ支出がありません");
			return;
		}

		List<Expense> expenses = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {

			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(",");

				LocalDate date = LocalDate.parse(parts[0]);
				String category = parts[1];
				int amount = Integer.parseInt(parts[2]);
				String memo = parts[3];

				Expense expense = new Expense(date, category, amount, memo);
				expenses.add(expense);
			}

			int total = 0;

			for (Expense e : expenses) {
				System.out.println(e);
				total += e.getAmount();
			}

			System.out.println("--------------------------");
			System.out.println("合計: " + total + "円");

		} catch (IOException e) {
			System.out.println("[ERROR] ファイル読み込みに失敗しました");
		}
	}
	
	//summaryコマンドの詳細
	static void handleSummary(String[] args) {
		if (args.length < 2) {
			System.out.println("[ERROR] summary yyyy-mmの形式で入力してください。");
			return;
		}
		
		String targetYm = args[1]; //例:2026-01
		
		if (!Files.exists(Paths.get(FILE_NAME))) {
			System.out.println("まだ支出がありません");
			return;
		}
		
		int total = 0;
		
		try(BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
			
			String line;
			while((line = br.readLine()) != null) {
				String[] parts = line.split(",");
				
				LocalDate date = LocalDate.parse(parts[0]);
				int amount = Integer.parseInt(parts[2]);
				
				String ym = date.getYear() + "-" +
						String.format("%02d", date.getMonthValue());
				
				if (ym.equals(targetYm)) {
					total += amount;
				}
			}
		} catch (IOException e) {
			System.out.println("[ERROR] ファイル読み込みに失敗しました");
			return;
		}
		
		System.out.println(targetYm + "合計: " + total + "円");
	}
}
