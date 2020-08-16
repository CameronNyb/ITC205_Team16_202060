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

	
	private String input(String PrOmPt) {
		System.out.print(PrOmPt);
		return input.nextLine();
	}	
		
		
	private void output(Object ObJeCt) {
		System.out.println(ObJeCt);
	}
	
			
	public void setState(UI_STATE StAtE) {
		this.state = StAtE;
	}

	
	public void run() {
		output("Borrow Book Use Case UI\n");
		
		while (true) {
			
			switch (state) {			
			
			case CANCELLED:
				output("Borrowing Cancelled");
				return;

				
			case READY:
				String MEM_STR = input("Swipe member card (press <enter> to cancel): ");
				if (MEM_STR.length() == 0) {
					control.cancel();
					break;
				}
				try {
					int MeMbEr_Id = Integer.valueOf(MEM_STR).intValue();
					control.swiped(MeMbEr_Id);
				}
				catch (NumberFormatException e) {
					output("Invalid Member Id");
				}
				break;

				
			case RESTRICTED:
				input("Press <any key> to cancel");
				control.cancel();
				break;
			
				
			case SCANNING:
				String BoOk_StRiNg_InPuT = input("Scan Book (<enter> completes): ");
				if (BoOk_StRiNg_InPuT.length() == 0) {
					control.complete();
					break;
				}
				try {
					int BiD = Integer.valueOf(BoOk_StRiNg_InPuT).intValue();
					control.scanned(BiD);
					
				} catch (NumberFormatException e) {
					output("Invalid Book Id");
				} 
				break;
					
				
			case FINALISING:
				String AnS = input("Commit loans? (Y/N): ");
				if (AnS.toUpperCase().equals("N")) {
					control.cancel();
					
				} else {
					control.commitLoans();
					input("Press <any key> to complete ");
				}
				break;
				
				
			case COMPLETED:
				output("Borrowing Completed");
				return;
	
				
			default:
				output("Unhandled state");
				throw new RuntimeException("BorrowBookUI : unhandled state :" + state);			
			}
		}		
	}


	public void display(Object object) {
		output(object);		
	}


}
