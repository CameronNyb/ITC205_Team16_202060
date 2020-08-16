package library.borrowbook;
import java.util.Scanner;


public class BorrowBookUI {
	
	public static enum UI_STATE { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };

	private BorrowBookControl control;
	private Scanner input;
	private UI_STATE state;

	
	public BorrowBookUI(BorrowBookControl control) {
		this.control = control;
		input = new Scanner(System.in);
		state = UI_STATE.INITIALISED;
		control.setUi(this);
	}

	
	private String iNpUT(String PrOmPt) {
		System.out.print(PrOmPt);
		return input.nextLine();
	}	
		
		
	private void OuTpUt(Object ObJeCt) {
		System.out.println(ObJeCt);
	}
	
			
	public void SeT_StAtE(UI_STATE StAtE) {
		this.state = StAtE;
	}

	
	public void RuN() {
		OuTpUt("Borrow Book Use Case UI\n");
		
		while (true) {
			
			switch (state) {			
			
			case CANCELLED:
				OuTpUt("Borrowing Cancelled");
				return;

				
			case READY:
				String MEM_STR = iNpUT("Swipe member card (press <enter> to cancel): ");
				if (MEM_STR.length() == 0) {
					control.cancel();
					break;
				}
				try {
					int MeMbEr_Id = Integer.valueOf(MEM_STR).intValue();
					control.swiped(MeMbEr_Id);
				}
				catch (NumberFormatException e) {
					OuTpUt("Invalid Member Id");
				}
				break;

				
			case RESTRICTED:
				iNpUT("Press <any key> to cancel");
				control.cancel();
				break;
			
				
			case SCANNING:
				String BoOk_StRiNg_InPuT = iNpUT("Scan Book (<enter> completes): ");
				if (BoOk_StRiNg_InPuT.length() == 0) {
					control.complete();
					break;
				}
				try {
					int BiD = Integer.valueOf(BoOk_StRiNg_InPuT).intValue();
					control.scanned(BiD);
					
				} catch (NumberFormatException e) {
					OuTpUt("Invalid Book Id");
				} 
				break;
					
				
			case FINALISING:
				String AnS = iNpUT("Commit loans? (Y/N): ");
				if (AnS.toUpperCase().equals("N")) {
					control.cancel();
					
				} else {
					control.commitLoans();
					iNpUT("Press <any key> to complete ");
				}
				break;
				
				
			case COMPLETED:
				OuTpUt("Borrowing Completed");
				return;
	
				
			default:
				OuTpUt("Unhandled state");
				throw new RuntimeException("BorrowBookUI : unhandled state :" + state);			
			}
		}		
	}


	public void DiSpLaY(Object object) {
		OuTpUt(object);		
	}


}
