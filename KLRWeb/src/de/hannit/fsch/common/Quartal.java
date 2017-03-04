/**
 * 
 */
package de.hannit.fsch.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.TreeMap;

/**
 * @author fsch
 *
 */
public class Quartal 
{
private int quartalsNummer = 0;
private int quartalsJahr = 0;
private String bezeichnung = "unbekannt";
private String bezeichnungLang = "unbekannt";
private String bezeichnungkurz = "unbekannt";

private int index = 0;
private LocalDate startDatum = null;
private LocalDate endDatum = null;
private LocalDateTime startDatumUhrzeit = null;
private LocalDateTime endDatumUhrzeit = null;
private Period quartalsPeriode = null;
/**
 * Jedes Quartal enthält drei Berichtsmonate, die hier abrufbar sind
 */
private TreeMap<Integer, LocalDate> berichtsMonate = null;

	/**
	 * 
	 */
	public Quartal() 
	{

	}

	/*
	 * Dieser Konstruktor ermittelt das aktuelle Quartal automatisch
	 * anhand des Monats
	 */
	public Quartal(int monthValue, int jahr) 
	{
	int tmp = 0;
	
		switch (monthValue) 
		{
		case 1: tmp = 1; break;
		case 2: tmp = 1; break;
		case 3: tmp = 1; break;
		case 4: tmp = 2; break;
		case 5: tmp = 2; break;
		case 6: tmp = 2; break;
		case 7: tmp = 3; break;
		case 8: tmp = 3; break;
		case 9: tmp = 3; break;
		case 10: tmp = 4; break;
		case 11: tmp = 4; break;
		case 12: tmp = 4; break;

		default: break;
		}
	
	setQuartalsNummer(tmp, jahr);	
	setBerichtsMonate(getQuartalsNummer(), getQuartalsJahr());
	}
	
	/**
	 * Neu für KLRWeb
	 * Es wird automatisch eine TreeMap mit den drei enthaltenen Berichtsmonaten generiert
	 */
	public void setBerichtsMonate(int quartalsNummer, int jahr) 
	{
	berichtsMonate = new TreeMap<>();
	
		switch (quartalsNummer) 
		{
		case 1:
		berichtsMonate.put(1, LocalDate.of(jahr, 1, 1));
		berichtsMonate.put(2, LocalDate.of(jahr, 2, 1));
		berichtsMonate.put(3, LocalDate.of(jahr, 3, 1));			
		break;
		
		case 2:
		berichtsMonate.put(1, LocalDate.of(jahr, 4, 1));
		berichtsMonate.put(2, LocalDate.of(jahr, 5, 1));
		berichtsMonate.put(3, LocalDate.of(jahr, 6, 1));			
		break;
		
		case 3:
		berichtsMonate.put(1, LocalDate.of(jahr, 7, 1));
		berichtsMonate.put(2, LocalDate.of(jahr, 8, 1));
		berichtsMonate.put(3, LocalDate.of(jahr, 9, 1));			
		break;		
		
		default:
		berichtsMonate.put(1, LocalDate.of(jahr, 10, 1));
		berichtsMonate.put(2, LocalDate.of(jahr, 11, 1));
		berichtsMonate.put(3, LocalDate.of(jahr, 12, 1));			
		break;
		}
	
	}	


	public void setQuartalsNummer(int quartalsNummer, int jahr) 
	{
	this.quartalsNummer = quartalsNummer;
	this.quartalsJahr = jahr;
		
		switch (quartalsNummer) 
		{
		case 1:
			this.startDatum = LocalDate.of(jahr, 1, 1);
			this.startDatumUhrzeit = LocalDateTime.of(jahr, 1, 1, 0, 0, 0);
			this.endDatum = LocalDate.of(jahr, 3, 31);
			this.endDatumUhrzeit = LocalDateTime.of(jahr, 3, 31, 23, 59, 59);
			this.bezeichnung = "Quartal I";
			this.bezeichnungkurz = "QI " + String.valueOf(jahr);
			this.bezeichnungLang = "1. Quartal " + String.valueOf(jahr);
		break;
		case 2:
			this.startDatum = LocalDate.of(jahr, 4, 1);
			this.startDatumUhrzeit = LocalDateTime.of(jahr, 4, 1, 0, 0, 0);
			this.endDatum = LocalDate.of(jahr, 6, 30);
			this.endDatumUhrzeit = LocalDateTime.of(jahr, 6, 30, 23, 59, 59);
			this.bezeichnung = "Quartal II";
			this.bezeichnungkurz = "QII " + String.valueOf(jahr);			
			this.bezeichnungLang = "2. Quartal " + String.valueOf(jahr);
		break;
		case 3:
			this.startDatum = LocalDate.of(jahr, 7, 1);
			this.startDatumUhrzeit = LocalDateTime.of(jahr, 7, 1, 0, 0, 0);
			this.endDatum = LocalDate.of(jahr, 9, 30);
			this.endDatumUhrzeit = LocalDateTime.of(jahr, 9, 30, 23, 59, 59);
			this.bezeichnung = "Quartal III";
			this.bezeichnungkurz = "QIII " + String.valueOf(jahr);			
			this.bezeichnungLang = "3. Quartal " + String.valueOf(jahr);
		break;		
		default:
			this.startDatum = LocalDate.of(jahr, 10, 1);
			this.startDatumUhrzeit = LocalDateTime.of(jahr, 10, 1, 0, 0, 0);
			this.endDatum = LocalDate.of(jahr, 12, 31);
			this.endDatumUhrzeit = LocalDateTime.of(jahr, 12, 31, 23, 59, 59);
			this.bezeichnung = "Quartal IV";
			this.bezeichnungkurz = "QIV " + String.valueOf(jahr);			
			this.bezeichnungLang = "4. Quartal " + String.valueOf(jahr);
		break;
		}
		
	}
	
	public String getBezeichnungLang() {return bezeichnungLang;}
	public String getBezeichnung() {return bezeichnung;}
	public int getIndex() {return index;}
	public void setIndex(int index) {this.index = index;}
	public Period getQuartalsPeriode() {return quartalsPeriode;}
	public void setQuartalsPeriode() 
	{
	LocalDate ende = endDatum.plusDays(1);	
	this.quartalsPeriode = Period.between(startDatum, ende);
	}

	public int getQuartalsNummer() {return quartalsNummer;}
	public int getQuartalsJahr() {return quartalsJahr;}
	public LocalDate getStartDatum() {return startDatum;}
	public LocalDate getEndDatum() {return endDatum;}
	public LocalDateTime getStartDatumUhrzeit() {return startDatumUhrzeit;}
	public LocalDateTime getEndDatumUhrzeit() {return endDatumUhrzeit;}
	public String getBezeichnungkurz() {return bezeichnungkurz;}
	public TreeMap<Integer, LocalDate> getBerichtsMonate() {return berichtsMonate;}

}
