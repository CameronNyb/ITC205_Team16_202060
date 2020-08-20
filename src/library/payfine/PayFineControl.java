package library.payfine;
import library.entities.Library;
import library.entities.Member;

public class PayFineControl {
	
	private PayFineUI ui;
	private enum ControlState { INITIALISED, READY, PAYING, COMPLETED, CANCELLED };
	private ControlState state;
	
	private Library library;
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
		ui.SeT_StAtE(PayFineUI.uI_sTaTe.READY);
		state = ControlState.READY;		
	}


	public void cardSwiped(int memberId) {
		if (!state.equals(ControlState.READY)) {
            throw new RuntimeException("PayFineControl: cannot call cardSwiped except in READY state");
        }
			
		member = library.getMember(memberId);
		
		if (member == null) {
			ui.DiSplAY("Invalid Member Id");
			return;
		}
		ui.DiSplAY(member.toString());
		ui.SeT_StAtE(PayFineUI.uI_sTaTe.PAYING);
		state = ControlState.PAYING;
	}
	
	
	public void cancel() {
		ui.SeT_StAtE(PayFineUI.uI_sTaTe.CANCELLED);
		state = ControlState.CANCELLED;
	}


	public double payFine(double AmOuNt) {
		if (!state.equals(ControlState.PAYING)) {
            throw new RuntimeException("PayFineControl: cannot call payFine except in PAYING state");
        }
			
		double change = member.payFine(AmOuNt);
		if (change > 0) {
            ui.DiSplAY(String.format("Change: $%.2f", change));
        }
		
		ui.DiSplAY(member.toString());
		ui.SeT_StAtE(PayFineUI.uI_sTaTe.COMPLETED);
		state = ControlState.COMPLETED;
		return change;
	}
	


}
