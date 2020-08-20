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
    private enum ControlState { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };
    private ControlState state;
    
    private List<Book> pendingList;
    private List<Loan> completedList;
    private Book book;
    
    public BorrowBookControl() {
        this.library = Library.getInstance();
        state = ControlState.INITIALISED;
    }

    public void setUi(BorrowBookUI ui) {
        if (!state.equals(ControlState.INITIALISED)) {
            throw new RuntimeException("BorrowBookControl: cannot call setUI except in INITIALISED state");
        }
            
        this.ui = ui;
        ui.setState(BorrowBookUI.UI_STATE.READY);
        state = ControlState.READY;        
    }
        
    public void swiped(int memberId) {
        if (!state.equals(ControlState.READY)) {
            throw new RuntimeException("BorrowBookControl: cannot call cardSwiped except in READY state");
        }
            
        member = library.getMember(memberId);
        if (member == null) {
            ui.display("Invalid memberId");
            return;
        }
        if (library.canMemberBorrow(member)) {
            pendingList = new ArrayList<>();
            ui.setState(BorrowBookUI.UI_STATE.SCANNING);
            state = ControlState.SCANNING; 
        } else {
            ui.display("Member cannot borrow at this time");
            ui.setState(BorrowBookUI.UI_STATE.RESTRICTED); 
        }
    }
    
    public void scanned(int bookId) {
        book = null;
        if (!state.equals(ControlState.SCANNING)) {
            throw new RuntimeException("BorrowBookControl: cannot call bookScanned except in SCANNING state");
        }
            
        book = library.getBook(bookId);
        if (book == null) {
            ui.display("Invalid bookId");
            return;
        }
        if (!book.isAvailable()) {
            ui.display("Book cannot be borrowed");
            return;
        }
        pendingList.add(book);
        for (Book B : pendingList) { 
            ui.display(B.toString());
        }
        
        if (library.getNumberOfLoansRemainingForMember(member) - pendingList.size() == 0) {
            ui.display("Loan limit reached");
            complete();
        }
    }
    
    public void complete() {
        if (pendingList.size() == 0) {
            cancel();
        } else {
            ui.display("\nFinal Borrowing List");
            for (Book bOoK : pendingList) {
                ui.display(bOoK.toString());
            }
            
            completedList = new ArrayList<Loan>();
            ui.setState(BorrowBookUI.UI_STATE.FINALISING);
            state = ControlState.FINALISING;
        }
    }

    public void commitLoans() {
        if (!state.equals(ControlState.FINALISING)) {
            throw new RuntimeException("BorrowBookControl: cannot call commitLoans except in FINALISING state");
        }
            
        for (Book b : pendingList) {
            Loan loanIssue = library.issueLoan(b, member);
            completedList.add(loanIssue);            
        }
        ui.display("Completed Loan Slip");
        for (Loan loan : completedList) {
            ui.display(loan.toString());
        }
        
        ui.setState(BorrowBookUI.UI_STATE.COMPLETED);
        state = ControlState.COMPLETED;
    }
    
    public void cancel() {
        ui.setState(BorrowBookUI.UI_STATE.CANCELLED);
        state = ControlState.CANCELLED;
    }
    
}
