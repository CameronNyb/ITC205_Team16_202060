package library.borrowbook;
import java.util.ArrayList;
import java.util.List;

import library.entities.Book;
import library.entities.Library;
import library.entities.Loan;
import library.entities.Member;

public class BorrowBookControl {
	
	private BorrowBookUI ui;
	
	private Library library;
	private Member member;
	private enum CONTROL_STATE { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };
	private CONTROL_STATE state;
	
	private List<Book> pendingList;
	private List<Loan> completedList;
	private Book book;
	
	
	public BorrowBookControl() {
		this.library = Library.getInstance();
		state = CONTROL_STATE.INITIALISED;
	}
	

	public void setUi(BorrowBookUI Ui) {
		if (!state.equals(CONTROL_STATE.INITIALISED)) 
			throw new RuntimeException("BorrowBookControl: cannot call setUI except in INITIALISED state");
			
		this.ui = Ui;
		Ui.SeT_StAtE(BorrowBookUI.uI_STaTe.READY);
		state = CONTROL_STATE.READY;		
	}

		
	public void swiped(int mEmBeR_Id) {
		if (!state.equals(CONTROL_STATE.READY)) 
			throw new RuntimeException("BorrowBookControl: cannot call cardSwiped except in READY state");
			
		member = library.getMember(mEmBeR_Id);
		if (member == null) {
			ui.DiSpLaY("Invalid memberId");
			return;
		}
		if (library.canMemberBorrow(member)) {
			pendingList = new ArrayList<>();
			ui.SeT_StAtE(BorrowBookUI.uI_STaTe.SCANNING);
			state = CONTROL_STATE.SCANNING; 
		}
		else {
			ui.DiSpLaY("Member cannot borrow at this time");
			ui.SeT_StAtE(BorrowBookUI.uI_STaTe.RESTRICTED); 
		}
	}
	
	
	public void scanned(int bOoKiD) {
		book = null;
		if (!state.equals(CONTROL_STATE.SCANNING)) 
			throw new RuntimeException("BorrowBookControl: cannot call bookScanned except in SCANNING state");
			
		book = library.getBook(bOoKiD);
		if (book == null) {
			ui.DiSpLaY("Invalid bookId");
			return;
		}
		if (!book.isAvailable()) {
			ui.DiSpLaY("Book cannot be borrowed");
			return;
		}
		pendingList.add(book);
		for (Book B : pendingList) 
			ui.DiSpLaY(B.toString());
		
		if (library.getNumberOfLoansRemainingForMember(member) - pendingList.size() == 0) {
			ui.DiSpLaY("Loan limit reached");
			complete();
		}
	}
	
	
	public void complete() {
		if (pendingList.size() == 0) 
			cancel();
		
		else {
			ui.DiSpLaY("\nFinal Borrowing List");
			for (Book bOoK : pendingList) 
				ui.DiSpLaY(bOoK.toString());
			
			completedList = new ArrayList<Loan>();
			ui.SeT_StAtE(BorrowBookUI.uI_STaTe.FINALISING);
			state = CONTROL_STATE.FINALISING;
		}
	}


	public void commitLoans() {
		if (!state.equals(CONTROL_STATE.FINALISING)) 
			throw new RuntimeException("BorrowBookControl: cannot call commitLoans except in FINALISING state");
			
		for (Book B : pendingList) {
			Loan lOaN = library.issueLoan(B, member);
			completedList.add(lOaN);			
		}
		ui.DiSpLaY("Completed Loan Slip");
		for (Loan LOAN : completedList) 
			ui.DiSpLaY(LOAN.toString());
		
		ui.SeT_StAtE(BorrowBookUI.uI_STaTe.COMPLETED);
		state = CONTROL_STATE.COMPLETED;
	}

	
	public void cancel() {
		ui.SeT_StAtE(BorrowBookUI.uI_STaTe.CANCELLED);
		state = CONTROL_STATE.CANCELLED;
	}
	
	
}
