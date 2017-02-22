package de.hannit.fsch.util;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

public class DateUtility 
{
public static DateTimeFormatter DF_MONAT = DateTimeFormatter.ofPattern("MMMM");
public static DateTimeFormatter DF_JAHR = DateTimeFormatter.ofPattern("yyyy");	
public static DateTimeFormatter DF_MONATJAHR = DateTimeFormatter.ofPattern("MMMM yyyy");	
	
	private DateUtility() {}
	
	/*
	 * Monatzletzten ermitteln
	 */
	public static LocalDate getEndOfCurrentMonth() 
	{
	return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
	}
	
	 /**
	  * Method calculates last working day for last day of month as input
	  * @param lastDayOfMonth
	  * @return LocalDate instance containing last working day
	  */
	 public static LocalDate getLastWorkingDayOfMonth(LocalDate incoming) 
	 {
	 LocalDate lastWorkingDayofMonth;
	 	switch (DayOfWeek.of(incoming.get(ChronoField.DAY_OF_WEEK))) 
	 	{
	    case SATURDAY:
	    lastWorkingDayofMonth = incoming.minusDays(1);
	    break;
	    case SUNDAY:
	    lastWorkingDayofMonth = incoming.minusDays(2);
	    break;
	    default:
	    lastWorkingDayofMonth = incoming;
	   }
	 return lastWorkingDayofMonth;
	 }
	 
	 public static Date asDate(LocalDate localDate) 
	 {
	 return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	 }

	 public static Date asDate(LocalDateTime localDateTime) 
	 {
	 return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	 }

	 public static LocalDate asLocalDate(Date date) 
	 {
	 return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	 }

	 public static LocalDateTime asLocalDateTime(Date date) 
	 {
	 return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
	 }	 
}
