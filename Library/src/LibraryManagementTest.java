import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class LibraryManagementTest {
	private Transaction transaction;
	
	@Before
	public void setUp() {
		transaction = Transaction.getTransaction();
	}

	@Test
	public void testBookId() throws Exception {
		// Add IDs within bounds
		Book book1 = new Book(100, "Valid Book1");
		assertEquals(100, book1.getId());
		
		Book book2 = new Book(999, "Valid Book2");
		assertEquals(999, book2.getId());
		
		// Add books outside of bounds
		Book book3;
		try {
			book3 = new Book(1000, "Invalid Book1");
		} catch (Exception e) {
			assertEquals("Id 1000 is invalid. Must be between 100 and 999", e.getMessage());
		}
		
		Book book4;
		try {
			book4 = new Book(50, "Invalid Book2");
		} catch (Exception e) {
			assertEquals("Id 50 is invalid. Must be between 100 and 999", e.getMessage());
		}
		
		Book book5;
		try {
			book5 = new Book(2000, "Invalid Book3");
		} catch (Exception e) {
			assertEquals("Id 2000 is invalid. Must be between 100 and 999", e.getMessage());
		}
	}
	
	@Test
	public void testBorrowReturn() throws Exception {
		Book book = new Book(100, "Test Book");
		Member member = new Member(1, "Test Member");
		// Check book exists
		assertTrue(book.isAvailable());
		
		boolean firstBorrow = transaction.borrowBook(book, member);
		// Borrow successful
		assertTrue(firstBorrow);
		// Book unavailable
		assertFalse(book.isAvailable());
		
		boolean secondBorrow = transaction.borrowBook(book, member);
		// Borrow attempt unsuccessful
		assertFalse(secondBorrow);
		
		boolean firstReturn = transaction.returnBook(book, member);
		// Return successful
		assertTrue(firstReturn);
		// Book available
		assertTrue(book.isAvailable());
		
		boolean secondReturn = transaction.returnBook(book, member);
		// Return failed
		assertFalse(secondReturn);
	}
}
