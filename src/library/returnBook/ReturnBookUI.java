package library.returnBook;
import java.util.Scanner;


public class ReturnBookUI {

	public static enum uI_sTaTe { INITIALISED, READY, INSPECTING, COMPLETED };

	private rETURN_bOOK_cONTROL control;
	private Scanner input;
	private uI_sTaTe StATe;

	
	public ReturnBookUI(rETURN_bOOK_cONTROL control) {
		this.control = control;
		input = new Scanner(System.in);
		StATe = uI_sTaTe.INITIALISED;
		control.setUi(this);
	}


	public void run() {		
		output("Return Book Use Case UI\n");
		
		while (true) {
			
			switch (StATe) {
			
			case INITIALISED:
				break;
				
			case READY:
				String BoOk_InPuT_StRiNg = input("Scan Book (<enter> completes): ");
				if (BoOk_InPuT_StRiNg.length() == 0) 
					control.scanningComplete();
				
				else {
					try {
						int bookId = Integer.valueOf(BoOk_InPuT_StRiNg).intValue();
						control.bookScanned(bookId);
					}
					catch (NumberFormatException e) {
						output("Invalid bookId");
					}					
				}
				break;				
				
			case INSPECTING:
				String answer = input("Is book damaged? (Y/N): ");
				boolean Is_DAmAgEd = false;
				if (answer.toUpperCase().equals("Y")) 					
					Is_DAmAgEd = true;
				
				control.dischargeLoan(Is_DAmAgEd);
			
			case COMPLETED:
				output("Return processing complete");
				return;
			
			default:
				output("Unhandled state");
				throw new RuntimeException("ReturnBookUI : unhandled state :" + StATe);			
			}
		}
	}

	
	private String input(String prompt) {
		System.out.print(prompt);
		return input.nextLine();
	}	
		
		
	private void output(Object object) {
		System.out.println(object);
	}
	
			
	public void display(Object object) {
		output(object);
	}
	
	public void setState(uI_sTaTe state) {
		this.StATe = state;
	}

	
}
