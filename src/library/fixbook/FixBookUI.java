package library.fixbook;
import java.util.Scanner;


public class FixBookUI {

	public static enum uI_sTaTe { INITIALISED, READY, FIXING, COMPLETED };

	private fIX_bOOK_cONTROL control;
	private Scanner input;
	private uI_sTaTe state;

	
	public FixBookUI(fIX_bOOK_cONTROL control) {
		this.control = control;
		input = new Scanner(System.in);
		state = uI_sTaTe.INITIALISED;
		control.SeT_Ui(this);
	}


	public void SeT_StAtE(uI_sTaTe state) {
		this.state = state;
	}

	
	public void RuN() {
		OuTpUt("Fix Book Use Case UI\n");
		
		while (true) {
			
			switch (state) {
			
			case READY:
				String bookEntryString = iNpUt("Scan Book (<enter> completes): ");
				if (bookEntryString.length() == 0) 
					control.SCannING_COMplete();
				
				else {
					try {
						int bookId = Integer.valueOf(bookEntryString).intValue();
						control.BoOk_ScAnNeD(bookId);
					}
					catch (NumberFormatException e) {
						OuTpUt("Invalid bookId");
					}
				}
				break;	
				
			case FIXING:
				String answer = iNpUt("Fix Book? (Y/N) : ");
				boolean fix = false;
				if (answer.toUpperCase().equals("Y")) 
					fix = true;
				
				control.FiX_BoOk(fix);
				break;
								
			case COMPLETED:
				OuTpUt("Fixing process complete");
				return;
			
			default:
				OuTpUt("Unhandled state");
				throw new RuntimeException("FixBookUI : unhandled state :" + state);			
			
			}		
		}
		
	}

	
	private String iNpUt(String prompt) {
		System.out.print(prompt);
		return input.nextLine();
	}	
		
		
	private void OuTpUt(Object object) {
		System.out.println(object);
	}
	

	public void dIsPlAy(Object object) {
		OuTpUt(object);
	}
	
	
}
