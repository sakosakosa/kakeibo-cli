import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Main {

	static final String FILE_NAME = "../data/expenses.csv";

	public static void main(String[] args) {

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
		System.out.println(" 支出の削除");
		System.out.println("  delete id");
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
		int id = getNextId();

		String line = id + "," + date + "," + category + "," + amount + "," + memo;
		boolean fileExists = Files.exists(Paths.get(FILE_NAME));

		try (FileWriter fw = new FileWriter(FILE_NAME, true);
				BufferedWriter bw = new BufferedWriter(fw)) {

			if (!fileExists) {
				bw.write("id,date,category,amount,memo");
				bw.newLine();
			}

			bw.write(line);
			bw.newLine();

			System.out.println("追加しました！");
		} catch (IOException e) {
			System.out.println("[ERROR] ファイル書き込みに失敗しました");
		}

	}

	//listコマンドの詳細
	static void handleList() {
		List<Expense> expenses = loadExpenses();

		if (expenses.isEmpty()) {
			System.out.println("まだ支出がありません");
			return;
		}
		int total = 0;

		for (Expense e : expenses) {
			System.out.println(e);
			total += e.getAmount();
		}

		System.out.println("--------------------------");
		System.out.println("合計: " + total + "円");

	}

	//summaryコマンドの詳細
	static void handleSummary(String[] args) {
		if (args.length < 2) {
			System.out.println("[ERROR] summary yyyy-mmの形式で入力してください。");
			return;
		}

		String targetYm = args[1]; //例:2026-01
		List<Expense> expenses = loadExpenses();

		int total = 0;

		for (Expense e : expenses) {
			String ym = e.getDate().getYear() + "-" +
					String.format("%02d", e.getDate().getMonthValue());

			if (ym.equals(targetYm)) {
				total += e.getAmount();
			}
		}

		System.out.println(targetYm + "合計: " + total + "円");
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

		if (!Files.exists(Paths.get(FILE_NAME))) {
			System.out.println("まだ支出がありません");
			return;
		}

		List<Expense> expenses = loadExpenses();
		
		boolean deleted = expenses.removeIf(e -> e.getId() == targetId);
		
		if (!deleted) {
			System.out.println("指定されたIDは見つかりませんでした:" + targetId);
			return;
		}
		
		saveExpenses(expenses);
		System.out.println("ID " + targetId + " を削除しました");

	}

	static void handleEdit(String[] args) {
		if (args.length < 6) {
			System.out.println("[ERROR] edit id date category amount memo の形式で入力してください");
			return;
		}

		int targetId = Integer.parseInt(args[1]);
		LocalDate newDate = LocalDate.parse(args[2]);
		String newCategory = args[3];
		int newAmount = Integer.parseInt(args[4]);
		String newMemo = args[5];

		List<Expense> expenses = loadExpenses();

		boolean found = false;

		for (Expense e : expenses) {
			if (e.getId() == targetId) {
				e.setDate(newDate);
				e.setCategory(newCategory);
				e.setAmount(newAmount);
				e.setMemo(newMemo);
				found = true;
				break;
			}
		}

		if (!found) {
			System.out.println("指定したIDが見つかりません");
			return;
		}

		saveExpenses(expenses);
		System.out.println("ID" + targetId + " を編集しました");

	}

	//新規IDの取得
	static int getNextId() {
		if (!Files.exists(Paths.get(FILE_NAME))) {
			return 1;
		}
		int maxId = 0;

		try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
			String line;
			boolean isFirstLine = true;

			while ((line = br.readLine()) != null) {

				if (isFirstLine) {
					isFirstLine = false;
					continue;
				}
				if (line.isBlank()) {
					continue;
				}

				String[] parts = line.split(",");
				int id = Integer.parseInt(parts[0]);

				if (id > maxId) {
					maxId = id;
				}
			}
		} catch (IOException e) {
			System.out.println("[ERROR] ID取得に失敗しました");
		}
		return maxId + 1;
	}

	//csvの読み込み
	static List<Expense> loadExpenses() {
		List<Expense> expenses = new ArrayList<>();

		if (!Files.exists(Paths.get(FILE_NAME))) {
			return expenses;
		}

		try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
			String line;
			boolean isFirstLine = true;

			while ((line = br.readLine()) != null) {
				if (isFirstLine) {
					isFirstLine = false;
					continue;
				}
				if (line.isBlank()) {
					continue;
				}

				String[] parts = line.split(",");

				int id = Integer.parseInt(parts[0]);
				LocalDate date = LocalDate.parse(parts[1]);
				String category = parts[2];
				int amount = Integer.parseInt(parts[3]);
				String memo = parts[4];

				expenses.add(new Expense(id, date, category, amount, memo));
			}
		} catch (IOException e) {
			System.out.println("[ERROR] ファイルの読み込みに失敗しました");
		}

		return expenses;
	}

	//csvの上書き保存
	static void saveExpenses(List<Expense> expenses) {
		try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {

			pw.println("id,date,category,amount,memo");

			for (Expense e : expenses) {
				pw.println(
						e.getId() + "," +
								e.getDate() + "," +
								e.getCategory() + "," +
								e.getAmount() + "," +
								e.getMemo());
			}
		} catch (IOException e) {
			System.out.println("[ERROR] ファイル書き込みに失敗しました");
		}
	}

}
