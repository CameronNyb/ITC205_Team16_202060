package library.payfine;
import library.entities.Library;
import library.entities.Member;

public class PayFineControl {
    
    private PayFineUI ui;
    private enum ControlState { INITIALISED, READY, PAYING, COMPLETED, CANCELLED };
    private ControlState state;
    
    private final Library library;
    private Member member;

    public PayFineControl() {
        this.library = Library.getInstance();
        state = ControlState.INITIALISED;
    }
        
    public void setUI(PayFineUI ui) {
        if (!state.equals(ControlState.INITIALISED)) {
            throw new RuntimeException("PayFineControl: cannot call setUI except in INITIALISED state");
        }    
        this.ui = ui;
        ui.setState(PayFineUI.UIState.READY);
        state = ControlState.READY;        
    }

    public void cardSwiped(int memberID) {
        if (!state.equals(ControlState.READY)) {
            throw new RuntimeException("PayFineControl: cannot call cardSwiped except in READY state");
        }
            
        member = library.getMember(memberID);
        
        if (member == null) {
            ui.display("Invalid Member Id");
            return;
        }
        ui.display(member.toString());
        ui.setState(PayFineUI.UIState.PAYING);
        state = ControlState.PAYING;
    }
    
    public void cancel() {
        ui.setState(PayFineUI.UIState.CANCELLED);
        state = ControlState.CANCELLED;
    }

    public double payFine(double amount) {
        if (!state.equals(ControlState.PAYING)) {
            throw new RuntimeException("PayFineControl: cannot call payFine except in PAYING state");
        }
            
        double change = member.payFine(amount);
        if (change > 0) {
            ui.display(String.format("Change: $%.2f", change));
        }
        
        ui.display(member.toString());
        ui.setState(PayFineUI.UIState.COMPLETED);
        state = ControlState.COMPLETED;
        return change;
    }
    
}
