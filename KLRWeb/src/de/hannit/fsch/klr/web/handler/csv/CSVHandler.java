/**
 * 
 */
package de.hannit.fsch.klr.web.handler.csv;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * @author fsch
 *
 */
public class CSVHandler
{
protected Calendar cal = Calendar.getInstance();	
protected DecimalFormat summenFormat = new DecimalFormat("0.00");

	/**
	 * Oberklasse für alle Handler, die CSV-Dateien exportieren.
	 * 
	 * Stellt häufig benutzte Funktionen zur Verfügung
	 */
	public CSVHandler()
	{
	}
	
	/*
	 * In Zelle 1 wird der letzte Tag des Auswertungmonats eingefügt.
	 * Un den Monatzletzten (auch in Schaltjahren) zuverlässig zu ermitteln, 
	 * sind einige Kalenderberechnungen notwendig.
	 */
	protected String getLetzterTagdesMonats(Date auswertungsMonat)
	{
	DateFormat df  = DateFormat.getDateInstance(DateFormat.MEDIUM);
	cal.setTime(auswertungsMonat);
	cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

	return df.format(cal.getTime());	
	}	
	
	/*
	 * Liefert die Quartalsnummer für den Pfad
	 */
	protected String getQuartalsnummer(Date auswertungsMonat)
	{
	String qNummer = null;
	cal.setTime(auswertungsMonat);	
	DateFormat df  = new SimpleDateFormat("MM");

		switch (df.format(cal.getTime()))
		{
		case "01":
		qNummer = "1";	
		break;
		
		case "02":
		qNummer = "1";	
		break;		
		
		case "03":
		qNummer = "1";	
		break;

		case "04":
		qNummer = "2";	
		break;
		
		case "05":
		qNummer = "2";	
		break;		
		
		case "06":
		qNummer = "2";	
		break;
		
		case "07":
		qNummer = "3";	
		break;
		
		case "08":
		qNummer = "3";	
		break;		
		
		case "09":
		qNummer = "3";	
		break;
		
		case "10":
		qNummer = "4";	
		break;
		
		case "11":
		qNummer = "4";	
		break;		
		
		case "12":
		qNummer = "4";	
		break;
		
		default:
		qNummer = null;
		break;
		}
	return qNummer;	
	}
	
	
	/*
	 * In Zelle 5 wird die Konstante 'UML-' + die Nummer des Monats im Quartal benötigt
	 */
	protected String getMonatsnummer(Date auswertungsMonat)
	{
	String mNummer = null;
	cal.setTime(auswertungsMonat);	
	DateFormat df  = new SimpleDateFormat("MM");

		switch (df.format(cal.getTime()))
		{
		case "01":
		mNummer = "01";	
		break;
		
		case "02":
		mNummer = "02";	
		break;		
		
		case "03":
		mNummer = "03";	
		break;

		case "04":
		mNummer = "01";	
		break;
		
		case "05":
		mNummer = "02";	
		break;		
		
		case "06":
		mNummer = "03";	
		break;
		
		case "07":
		mNummer = "01";	
		break;
		
		case "08":
		mNummer = "02";	
		break;		
		
		case "09":
		mNummer = "03";	
		break;
		
		case "10":
		mNummer = "01";	
		break;
		
		case "11":
		mNummer = "02";	
		break;		
		
		case "12":
		mNummer = "03";	
		break;
		
		default:
		mNummer = null;
		break;
		}
	return mNummer;	
	}	
	
	/*
	 * Liefert den Auswertungsmonat kurz für den Pfad
	 */
	protected String getAuswertungsmonat(Date auswertungsMonat)
	{
	cal.setTime(auswertungsMonat);	
	DateFormat df  = new SimpleDateFormat("MM");
	
	return df.format(cal.getTime());	
	}

	protected String getJahr(Date auswertungsMonat)
	{
	cal.setTime(auswertungsMonat);	
	DateFormat df  = new SimpleDateFormat("YYYY");

	return df.format(cal.getTime());	
	}

	protected String getMonatLang(Date auswertungsMonat)
	{
	cal.setTime(auswertungsMonat);	
	DateFormat df  = new SimpleDateFormat("MMMM");
	
	return df.format(cal.getTime());	
	}		

}
