package aw.rmjtromp.RunicCore.utilities;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ocpsoft.prettytime.PrettyTime;

public final class DateUtil {

	@SuppressWarnings("unused")
	private long currentTimestamp = System.currentTimeMillis();
	private long timestamp;
	
	private DateUtil(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public static DateUtil compile(String timestamp) {
		// year month week day hour min sec
		Pattern pattern = Pattern.compile("(?:(?<=^|\\s|[^\\d])(?:(\\d+)(?:years?|y))?(?=$|\\s|[^a-z]))?(?:(?<=^|\\s|[^\\d])(?:(\\d+)(?:months?|m|mo))?(?=$|\\s|[^a-z]))?(?:(?<=^|\\s|[^\\d])(?:(\\d+)(?:weeks?|w))?(?=$|\\s|[^a-z]))?(?:(?<=^|\\s|[^\\d])(?:(\\d+)(?:days?|d))?(?=$|\\s|[^a-z]))?(?:(?<=^|\\s|[^\\d])(?:(\\d+)(?:hours?|h))?(?=$|\\s|[^a-z]))?(?:(?<=^|\\s|[^\\d])(?:(\\d+)(?:minutes?|mins?))?(?=$|\\s|[^a-z]))?(?:(?<=^|\\s|[^\\d])(?:(\\d+)(?:seconds?|secs?|s))?(?=$|\\s|[^a-z]))?", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher("7y2m9d2h2s");
		long eet = System.currentTimeMillis()/1000;
		if(matcher.find()) {
			int years = matcher.group(1) != null && !matcher.group(1).isEmpty() ? Integer.parseInt(matcher.group(1)) : 0;
			int months = matcher.group(2) != null && !matcher.group(2).isEmpty() ? Integer.parseInt(matcher.group(2)) : 0;
			int weeks = matcher.group(3) != null && !matcher.group(3).isEmpty() ? Integer.parseInt(matcher.group(3)) : 0;
			int days = matcher.group(4) != null && !matcher.group(4).isEmpty() ? Integer.parseInt(matcher.group(4)) : 0;
			int hours = matcher.group(5) != null && !matcher.group(5).isEmpty() ? Integer.parseInt(matcher.group(5)) : 0;
			int minutes = matcher.group(6) != null && !matcher.group(6).isEmpty() ? Integer.parseInt(matcher.group(6)) : 0;
			int seconds = matcher.group(7) != null && !matcher.group(7).isEmpty() ? Integer.parseInt(matcher.group(7)) : 0;

			eet += seconds;
			eet += minutes*60;
			eet += hours*60*60;
			eet += days*24*60*60;
			eet += weeks*7*24*60*60;
			eet += months*30*24*60*60;
			eet += years*365*24*60*60;
		}
		return new DateUtil(eet*1000);
	}
	
	public static DateUtil compile(long timestamp) {
		return new DateUtil(timestamp);
	}
	
	public String getRelativeTime() {
		Calendar date = Calendar.getInstance();
		date.setTime(new Date(timestamp));
		PrettyTime p = new PrettyTime();
		return p.format(date);
	}
	
}
