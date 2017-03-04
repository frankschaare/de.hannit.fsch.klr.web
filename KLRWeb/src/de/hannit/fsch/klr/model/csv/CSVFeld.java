package de.hannit.fsch.klr.model.csv;

import java.io.Serializable;
import java.time.LocalDate;

import de.hannit.fsch.klr.model.Datumsformate;

/**
 * 
 * @author hit 02.03.2017
 * 
 * Entspricht einem Feld innerhalb einer CSV Zeile
 *
 */
public class CSVFeld implements Serializable 
{
private static final long serialVersionUID = -7098562401271938747L;
private int index = 0;
private String wert = null;
private String headerText = null;
private boolean isHeader = false;
private LocalDate berichtsMonat = null;


	public CSVFeld() 
	{
	
	}

	public CSVFeld(int indexToSet, String valueToSet) 
	{
	setIndex(indexToSet);
	setWert(valueToSet);
	}
	
	public int getIndex() {return index;}
	public void setIndex(int index) {this.index = index;}
	public String getWert() {return wert;}
	public void setWert(String wert) {this.wert = wert;}
	public String getHeaderText() {return headerText;}
	public void setHeaderText(String headerText) {this.headerText = headerText;}
	public boolean isHeader() {return isHeader;}
	public void setHeader(boolean isHeader) {this.isHeader = isHeader;}
	public LocalDate getBerichtsMonat() {return berichtsMonat;}
	public void setBerichtsMonat(LocalDate berichtsMonat) {this.berichtsMonat = berichtsMonat;}
	public void setBerichtsMonat(String toParse) 
	{
	LocalDate tmp = LocalDate.parse(toParse, Datumsformate.df);	
	this.berichtsMonat = LocalDate.of(tmp.getYear(), tmp.getMonthValue(), 1);	
	}

	
}
