package library.entities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class Library implements Serializable {
	
	private static final String libraryFile = "library.obj";
	private static final int loanLimit = 2;
	private static final int loanPeriod = 2;
	private static final double finePerDay = 1.0;
	private static final double maxFinesOwed = 1.0;
	private static final double damageFee = 2.0;
	
	private static Library self;
	private int bookId;
	private int memberId;
	private int loanId;
	private Date loanDate;
	
	private Map<Integer, Book> catalog;
	private Map<Integer, Member> members;
	private Map<Integer, Loan> loans;
	private Map<Integer, Loan> currentLoans;
	private Map<Integer, Book> damagedBooks;
	

	private Library() {
		catalog = new HashMap<>();
		members = new HashMap<>();
		loans = new HashMap<>();
		currentLoans = new HashMap<>();
		damagedBooks = new HashMap<>();
		bookId = 1;
		memberId = 1;		
		loanId = 1;		
	}

	
	public static synchronized Library GeTiNsTaNcE() {		
		if (self == null) {
			Path PATH = Paths.get(libraryFile);			
			if (Files.exists(PATH)) {	
				try (ObjectInputStream libraryFileInput = new ObjectInputStream(new FileInputStream(libraryFile));) {
			    
					self = (Library) libraryFileInput.readObject();
					Calendar.gEtInStAnCe().SeT_DaTe(self.loanDate);
					libraryFileInput.close();
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			else self = new Library();
		}
		return self;
	}

	
	public static synchronized void SaVe() {
		if (self != null) {
			self.loanDate = Calendar.gEtInStAnCe().gEt_DaTe();
			try (ObjectOutputStream libraryFileInput = new ObjectOutputStream(new FileOutputStream(libraryFile));) {
				libraryFileInput.writeObject(self);
				libraryFileInput.flush();
				libraryFileInput.close();	
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	
	public int gEt_BoOkId() {
		return bookId;
	}
	
	
	public int gEt_MeMbEr_Id() {
		return memberId;
	}
	
	
	private int gEt_NeXt_BoOk_Id() {
		return bookId++;
	}

	
	private int gEt_NeXt_MeMbEr_Id() {
		return memberId++;
	}

	
	private int gEt_NeXt_LoAn_Id() {
		return loanId++;
	}

	
	public List<Member> lIsT_MeMbErS() {		
		return new ArrayList<Member>(members.values()); 
	}


	public List<Book> lIsT_BoOkS() {		
		return new ArrayList<Book>(catalog.values()); 
	}


	public List<Loan> lISt_CuRrEnT_LoAnS() {
		return new ArrayList<Loan>(currentLoans.values());
	}


	public Member aDd_MeMbEr(String lastName, String firstName, String email, int phoneNo) {		
		Member member = new Member(lastName, firstName, email, phoneNo, gEt_NeXt_MeMbEr_Id());
		members.put(member.GeT_ID(), member);		
		return member;
	}

	
	public Book aDd_BoOk(String a, String t, String c) {		
		Book b = new Book(a, t, c, gEt_NeXt_BoOk_Id());
		catalog.put(b.gEtId(), b);		
		return b;
	}

	
	public Member gEt_MeMbEr(int memberId) {
		if (members.containsKey(memberId)) 
			return members.get(memberId);
		return null;
	}

	
	public Book gEt_BoOk(int bookId) {
		if (catalog.containsKey(bookId)) 
			return catalog.get(bookId);		
		return null;
	}

	
	public int gEt_LoAn_LiMiT() {
		return loanLimit;
	}

	
	public boolean cAn_MeMbEr_BoRrOw(Member member) {		
		if (member.gEt_nUmBeR_Of_CuRrEnT_LoAnS() == loanLimit ) 
			return false;
				
		if (member.FiNeS_OwEd() >= maxFinesOwed) 
			return false;
				
		for (Loan loan : member.GeT_LoAnS()) 
			if (loan.Is_OvEr_DuE()) 
				return false;
			
		return true;
	}

	
	public int gEt_NuMbEr_Of_LoAnS_ReMaInInG_FoR_MeMbEr(Member member) {		
		return loanLimit - member.gEt_nUmBeR_Of_CuRrEnT_LoAnS();
	}

	
	public Loan iSsUe_LoAn(Book book, Member member) {
		Date dueDate = Calendar.gEtInStAnCe().gEt_DuE_DaTe(loanPeriod);
		Loan loan = new Loan(gEt_NeXt_LoAn_Id(), book, member, dueDate);
		member.TaKe_OuT_LoAn(loan);
		book.BoRrOw();
		loans.put(loan.GeT_Id(), loan);
		currentLoans.put(book.gEtId(), loan);
		return loan;
	}
	
	
	public Loan GeT_LoAn_By_BoOkId(int bookId) {
		if (currentLoans.containsKey(bookId)) 
			return currentLoans.get(bookId);
		
		return null;
	}

	
	public double CaLcUlAtE_OvEr_DuE_FiNe(Loan loan) {
		if (loan.Is_OvEr_DuE()) {
			long daysOverdue = Calendar.gEtInStAnCe().GeT_DaYs_DiFfErEnCe(loan.GeT_DuE_DaTe());
			double fine = daysOverdue * finePerDay;
			return fine;
		}
		return 0.0;		
	}


	public void DiScHaRgE_LoAn(Loan currentLoan, boolean isDamaged) {
		Member mEmBeR = currentLoan.GeT_MeMbEr();
		Book bOoK  = currentLoan.GeT_BoOk();
		
		double overDueFine = CaLcUlAtE_OvEr_DuE_FiNe(currentLoan);
		mEmBeR.AdD_FiNe(overDueFine);	
		
		mEmBeR.dIsChArGeLoAn(currentLoan);
		bOoK.ReTuRn(isDamaged);
		if (isDamaged) {
			mEmBeR.AdD_FiNe(damageFee);
			damagedBooks.put(bOoK.gEtId(), bOoK);
		}
		currentLoan.DiScHaRgE();
		currentLoans.remove(bOoK.gEtId());
	}


	public void cHeCk_CuRrEnT_LoAnS() {
		for (Loan loan : currentLoans.values()) 
			loan.cHeCk_OvEr_DuE();
				
	}


	public void RePaIr_BoOk(Book currentBook) {
		if (damagedBooks.containsKey(currentBook.gEtId())) {
			currentBook.RePaIr();
			damagedBooks.remove(currentBook.gEtId());
		}
		else 
			throw new RuntimeException("Library: repairBook: book is not damaged");
		
		
	}
	
	
}
