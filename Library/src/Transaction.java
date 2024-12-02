import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {
	// Instance of Transaction
	private static Transaction transaction;
	
	// Private constructor
	private Transaction() {}
	
	// Get the instance of Transaction
	public static Transaction getTransaction() {
		if (transaction == null) {
			transaction = new Transaction();
		}
		return transaction;
	}

    // Perform the borrowing of a book
    public boolean borrowBook(Book book, Member member) {
        if (book.isAvailable()) {
            book.borrowBook();
            member.borrowBook(book); 
            String transactionDetails = getCurrentDateTime() + " - Borrowing: " + member.getName() + " borrowed " + book.getTitle();
            System.out.println(transactionDetails);
            saveTransaction(transactionDetails);
            return true;
        } else {
            System.out.println("The book is not available.");
            return false;
        }
    }

    // Perform the returning of a book
    public boolean returnBook(Book book, Member member) {
        if (member.getBorrowedBooks().contains(book)) {
            member.returnBook(book);
            book.returnBook();
            String transactionDetails = getCurrentDateTime() + " - Returning: " + member.getName() + " returned " + book.getTitle();
            System.out.println(transactionDetails);
            saveTransaction(transactionDetails);
            return true;
        } else {
            System.out.println("This book was not borrowed by the member.");
            return false;
        }
    }

    // Get the current date and time in a readable format
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
    
    // Save the transaction history
	public void saveTransaction(String details) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.txt", true))) {
			writer.write(details);
			writer.newLine();
		} catch (IOException e) {
			System.err.println("An error occurred writing the transaction history file: " + e.getMessage());
		}
	}
	
	// Display the transaction history
	public void displayTransactionHistory() {
		try (BufferedReader reader = new BufferedReader(new FileReader("transactions.txt"))) {
			System.out.println("\nTransaction History:");
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			System.out.println();
		} catch (IOException e) {
			System.err.println("Error reading the transaction history file: " + e.getMessage());
		}
	}
}