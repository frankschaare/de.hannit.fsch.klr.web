/**
 * 
 */
package de.hannit.fsch.common;

import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * @author fsch
 * 
 * Hilfsklasse, um diverse Wirren bei der Sortierung nach Kalenderwoche zu umschiffen
 *
 */
public class KalenderWoche 
{
private int jahr = 0;
private int kw = 0;
private int monat = 0;
private String index = null;
private String bezeichnungLang = "Kalenderwoche dd yyyy";
private String label = null;
	/**
	 * @param startZeit 
	 * 
	 */
	public KalenderWoche(LocalDateTime startZeit) 
	{
	TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
	this.jahr = startZeit.getYear();
	this.monat = startZeit.getMonthValue();
	setKw(startZeit.get(woy));
	}
	
	public int getJahr() {
		return jahr;
	}
	public void setJahr(int jahr) {
		this.jahr = jahr;
	}
	public int getKw() 
	{
	return kw;
	}
	public void setKw(int kw) 
	{
		switch (kw) 
		{
		case 1:
			switch (monat) 
			{
			case 12:
			this.jahr = (this.jahr + 1);	
			break;

			default:
				break;
			}	
		break;

		default:
		break;
		}
	this.kw = kw;
	this.label = String.valueOf(jahr) + (kw < 10 ? "0" + String.valueOf(kw) : String.valueOf(kw));
	}
	public String getIndex() 
	{
	this.index = String.valueOf(jahr) + (kw < 10 ? "0" + String.valueOf(kw) : String.valueOf(kw)); 	
	return index;
	}

	public String getBezeichnungLang() 
	{
	bezeichnungLang = "Kalenderwoche " + (kw < 10 ? "0" + String.valueOf(kw) : String.valueOf(kw)) + " " + String.valueOf(jahr); 	
	return bezeichnungLang;
	}

	public String getLabel() {return label ;}
	
	

}
