package model;

import java.time.LocalDate;

public class Expense {
	
	private int id;
	private LocalDate date;
	private String category;
	private int amount;
	private String memo;
	
	public Expense(int id, LocalDate date, String category, int amount, String memo) {
		this.id = id;
		this.date = date;
		this.category = category;
		this.amount = amount;
		this.memo = memo;
	}
	
	public int getId() {
		return id;
	}
	
	public LocalDate getDate() {
		return date;
	}
	
	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	
	public String getMemo() {
		return memo;
	}
	
	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	@Override
	public String toString() {
		return id + "," + date + "," + category + "," + amount + "," + memo;
	}
	
}
