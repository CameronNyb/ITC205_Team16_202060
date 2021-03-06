package library.payfine;
import java.util.Scanner;


public class PayFineUI {

    public static enum UIState { INITIALISED, READY, PAYING, COMPLETED, CANCELLED };

    private PayFineControl control;
    private Scanner input;
    private UIState state;
    
    public PayFineUI(PayFineControl control) {
        this.control = control;
        input = new Scanner(System.in);
        state = UIState.INITIALISED;
        control.setUI(this);
    }    
    
    public void setState(UIState state) {
        this.state = state;
    }

    public void run() {
        output("Pay Fine Use Case UI\n");
        
        while (true) {
            
            switch (state) {
            
            case READY:
                String memberIDString = input("Swipe member card (press <enter> to cancel): ");
                if (memberIDString.length() == 0) {
                    control.cancel();
                    break;
                }
                try {
                    int memberID = Integer.valueOf(memberIDString).intValue();
                    control.cardSwiped(memberID);
                }
                catch (NumberFormatException e) {
                    output("Invalid memberId");
                }
                break;
                
            case PAYING:
                double amount = 0;
                String amountString = input("Enter amount (<Enter> cancels) : ");
                if (amountString.length() == 0) {
                    control.cancel();
                    break;
                }
                try {
                    amount = Double.valueOf(amountString).doubleValue();
                }
                catch (NumberFormatException e) {}
                if (amount <= 0) {
                    output("Amount must be positive");
                    break;
                }
                control.payFine(amount);
                break;
                                
            case CANCELLED:
                output("Pay Fine process cancelled");
                return;
            
            case COMPLETED:
                output("Pay Fine process complete");
                return;
            
            default:
                output("Unhandled state");
                throw new RuntimeException("FixBookUI : unhandled state :" + state);            
            
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

}
