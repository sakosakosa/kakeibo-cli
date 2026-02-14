package dao;

import model.Expense;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ExpenseDaoTest {

    @Test
    void insertAndFindAll() {
        ExpenseDao dao = new ExpenseDao();

        Expense expense = new Expense(
                0,
                LocalDate.of(2026, 2, 14),
                "test",
                999,
                "junit"
        );

        dao.insert(expense);

        List<Expense> list = dao.findAll();

        assertFalse(list.isEmpty());
    }
}
