import java.time.LocalDate;

public class Expense {
	
	private LocalDate date;
	private String category;
	private int amount;
	private String memo;
	
	public Expense(LocalDate date, String category, int amount, String memo) {
		this.date = date;
		this.category = category;
		this.amount = amount;
		this.memo = memo;
	}
	
	public LocalDate getDate() {
		return date;
	}
	
	public String getCategory() {
		return category;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public String getMemo() {
		return memo;
	}
	
	@Override
	public String toString() {
		return date + "," + category + "," + amount + "," + memo;
	}
	
}
