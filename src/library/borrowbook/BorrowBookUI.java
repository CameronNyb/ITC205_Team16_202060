package library.borrowbook;
import java.util.Scanner;


public class BorrowBookUI {

    public static enum UIState { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };

    private BorrowBookControl control;
    private Scanner input;
    private UIState state;

    public BorrowBookUI(BorrowBookControl control) {
        this.control = control;
        input = new Scanner(System.in);
        state = UIState.INITIALISED;
        control.setUi(this);
    }

    private String input(String prompt) {
        System.out.print(prompt);
        return input.nextLine();
    }
        
    private void output(Object object) {
        System.out.println(object);
    }

    public void setState(UIState state) {
        this.state = state;
    }

    public void run() {
        output("Borrow Book Use Case UI\n");

        while (true) {
            switch (state) {
                case CANCELLED:
                    output("Borrowing Cancelled");
                    return;

                case READY:
                    String memberIdStr = input("Swipe member card (press <enter> to cancel): ");
                    if (memberIdStr.length() == 0) {
                        control.cancel();
                        break;
                    }
                    try {
                        int memberId = Integer.valueOf(memberIdStr).intValue();
                        control.swiped(memberId);
                    } catch (NumberFormatException e) {
                        output("Invalid Member Id");
                    }
                    break;

                case RESTRICTED:
                    input("Press <any key> to cancel");
                    control.cancel();
                    break;

                case SCANNING:
                    String bookStringInput = input("Scan Book (<enter> completes): ");
                    if (bookStringInput.length() == 0) {
                        control.complete();
                        break;
                    }
                    try {
                        int bookId = Integer.valueOf(bookStringInput).intValue();
                        control.scanned(bookId);
                        
                    } catch (NumberFormatException e) {
                        output("Invalid Book Id");
                    } 
                    break;

                case FINALISING:
                    String answer = input("Commit loans? (Y/N): ");
                    if (answer.toUpperCase().equals("N")) {
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