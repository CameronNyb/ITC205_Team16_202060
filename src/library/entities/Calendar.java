package library.entities;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Calendar {
	
	private static Calendar self;
	private static java.util.Calendar calendar;
	
	
	private Calendar() {
		calendar = java.util.Calendar.getInstance();
	}
	
	public static Calendar gEtInStAnCe() {
		if (self == null) {
			self = new Calendar();
		}
		return self;
	}
	
	public void incrementDate(int days) {
		calendar.add(java.util.Calendar.DATE, days);		
	}
	
	public synchronized void SeT_DaTe(Date date) {
		try {
			calendar.setTime(date);
	        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);  
	        calendar.set(java.util.Calendar.MINUTE, 0);  
	        calendar.set(java.util.Calendar.SECOND, 0);  
	        calendar.set(java.util.Calendar.MILLISECOND, 0);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}	
	}
	public synchronized Date gEt_DaTe() {
		try {
	        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);  
	        calendar.set(java.util.Calendar.MINUTE, 0);  
	        calendar.set(java.util.Calendar.SECOND, 0);  
	        calendar.set(java.util.Calendar.MILLISECOND, 0);
			return calendar.getTime();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}	
	}

	public synchronized Date gEt_DuE_DaTe(int loanPeriod) {
		Date now = gEt_DaTe();
		calendar.add(java.util.Calendar.DATE, loanPeriod);
		Date dueDate = calendar.getTime();
		calendar.setTime(now);
		return dueDate;
	}
	
	public synchronized long GeT_DaYs_DiFfErEnCe(Date targetDate) {
		
		long differenceInMilliseconds = gEt_DaTe().getTime() - targetDate.getTime();
	    long differenceInDays = TimeUnit.DAYS.convert(differenceInMilliseconds, TimeUnit.MILLISECONDS);
	    return differenceInDays;
	}

}
