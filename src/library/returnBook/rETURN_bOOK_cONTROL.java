package library.returnBook;
import library.entities.Book;
import library.entities.Library;
import library.entities.Loan;

public class rETURN_bOOK_cONTROL {

	private ReturnBookUI ui;
	private enum cOnTrOl_sTaTe { INITIALISED, READY, INSPECTING };
	private cOnTrOl_sTaTe state;
	
	private Library library;
	private Loan currentLoan;
	

	public rETURN_bOOK_cONTROL() {
		this.library = Library.getInstance();
		state = cOnTrOl_sTaTe.INITIALISED;
	}
	
	
	public void setUi(ReturnBookUI ui) {
		if (!state.equals(cOnTrOl_sTaTe.INITIALISED)) 
			throw new RuntimeException("ReturnBookControl: cannot call setUI except in INITIALISED state");
		
		this.ui = ui;
		ui.setState(ReturnBookUI.uI_sTaTe.READY);
		state = cOnTrOl_sTaTe.READY;		
	}


	public void bookScanned(int bookId) {
		if (!state.equals(cOnTrOl_sTaTe.READY)) 
			throw new RuntimeException("ReturnBookControl: cannot call bookScanned except in READY state");
		
		Book currentBook = library.getBook(bookId);
		
		if (currentBook == null) {
			ui.display("Invalid Book Id");
			return;
		}
		if (!currentBook.isOnLoan()) {
			ui.display("Book has not been borrowed");
			return;
		}		
		currentLoan = library.getLoanByBookId(bookId);	
		double overdueFine = 0.0;
		if (currentLoan.isOverDue()) 
			overdueFine = library.calculateOverDueFine(currentLoan);
		
		ui.display("Inspecting");
		ui.display(currentBook.toString());
		ui.display(currentLoan.toString());
		
		if (currentLoan.isOverDue()) 
			ui.display(String.format("\nOverdue fine : $%.2f", overdueFine));
		
		ui.setState(ReturnBookUI.uI_sTaTe.INSPECTING);
		state = cOnTrOl_sTaTe.INSPECTING;		
	}


	public void scanningComplete() {
		if (!state.equals(cOnTrOl_sTaTe.READY)) 
			throw new RuntimeException("ReturnBookControl: cannot call scanningComplete except in READY state");
			
		ui.setState(ReturnBookUI.uI_sTaTe.COMPLETED);		
	}


	public void dischargeLoan(boolean damaged) {
		if (!state.equals(cOnTrOl_sTaTe.INSPECTING)) 
			throw new RuntimeException("ReturnBookControl: cannot call dischargeLoan except in INSPECTING state");
		
		library.dischargeLoan(currentLoan, damaged);
		currentLoan = null;
		ui.setState(ReturnBookUI.uI_sTaTe.READY);
		state = cOnTrOl_sTaTe.READY;				
	}


}
