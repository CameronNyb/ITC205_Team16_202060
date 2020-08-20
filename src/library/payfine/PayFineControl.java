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
	
	
	public void SeT_uI(PayFineUI uI) {
		if (!state.equals(ControlState.INITIALISED)) {
			throw new RuntimeException("PayFineControl: cannot call setUI except in INITIALISED state");
		}	
		this.ui = uI;
		uI.SeT_StAtE(PayFineUI.uI_sTaTe.READY);
		state = ControlState.READY;		
	}


	public void CaRd_sWiPeD(int MeMbEr_Id) {
		if (!state.equals(ControlState.READY)) 
			throw new RuntimeException("PayFineControl: cannot call cardSwiped except in READY state");
			
		member = library.getMember(MeMbEr_Id);
		
		if (member == null) {
			ui.DiSplAY("Invalid Member Id");
			return;
		}
		ui.DiSplAY(member.toString());
		ui.SeT_StAtE(PayFineUI.uI_sTaTe.PAYING);
		state = ControlState.PAYING;
	}
	
	
	public void CaNcEl() {
		ui.SeT_StAtE(PayFineUI.uI_sTaTe.CANCELLED);
		state = ControlState.CANCELLED;
	}


	public double PaY_FiNe(double AmOuNt) {
		if (!state.equals(ControlState.PAYING)) 
			throw new RuntimeException("PayFineControl: cannot call payFine except in PAYING state");
			
		double ChAnGe = member.payFine(AmOuNt);
		if (ChAnGe > 0) 
			ui.DiSplAY(String.format("Change: $%.2f", ChAnGe));
		
		ui.DiSplAY(member.toString());
		ui.SeT_StAtE(PayFineUI.uI_sTaTe.COMPLETED);
		state = ControlState.COMPLETED;
		return ChAnGe;
	}
	


}
