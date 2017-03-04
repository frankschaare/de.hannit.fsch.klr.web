package de.hannit.fsch.klr.model.csv;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TreeMap;

import de.hannit.fsch.klr.model.Constants;

/**
 * 
 * @author hit 02.03.2017
 * 
 * Entspricht einer Zeile in einer CSV-Datei
 *
 */
public class CSVZeile implements Serializable 
{
private static final long serialVersionUID = 8856294214991169308L;
private int index = 0;
protected TreeMap<Integer, CSVFeld> felder = new TreeMap<>();
private boolean isnew = true;
protected String rowStyle = null;
private LocalDate berichtmonat = null;
private String delimiter = null;
private String formattedLine = null;


	public CSVZeile() 
	{
	
	}

	public int getIndex() {return index;}
	public void setIndex(int index) {this.index = index;}
	public TreeMap<Integer, CSVFeld> getFelder() {return felder;}
	public void setFelder(TreeMap<Integer, CSVFeld> felder) {this.felder = felder;}
	public void setAll(ArrayList<CSVFeld> toSet) 
	{
		for (CSVFeld csvFeld : toSet) 
		{
		getFelder().put(csvFeld.getIndex(), csvFeld);	
		}
	}

	public CSVFeld getFeld (int feldIndex)
	{	
	return felder.get(feldIndex);
	}
	
	public String getWert (int feldIndex)
	{	
	return felder.get(feldIndex).getWert();
	}

	public String getRowStyle() {return rowStyle;}
	public void setRowStyle(String toSet) 
	{
	this.rowStyle = toSet;
	}
	public boolean getIsnew() {return isnew;}
	public void setIsnew(boolean toSet) 
	{
	this.isnew = toSet;
	
		if (!isnew) 
		{
		setRowStyle(Constants.CSS.ROWSTYLE_NOTNEW);	
		}
	}

	public void addFeld(CSVFeld toAdd) 
	{
	getFelder().put(toAdd.getIndex(), toAdd);	
	}

	public LocalDate getBerichtmonat() {return berichtmonat;}
	public void setBerichtmonat(LocalDate berichtmonat) {this.berichtmonat = berichtmonat;}

	public String getDelimiter() 
	{
	return delimiter == null ? CSVDatei.DEFAULT_DELIMITER : delimiter;
	}

	public String getFormattedLine() 
	{
	formattedLine = "";	
	
		for (CSVFeld feld : getFelder().values()) 
		{
			if (feld.isHeader()) 
			{
			formattedLine = formattedLine + feld.getHeaderText() + getDelimiter();	
			} 
			else 
			{
			formattedLine = formattedLine + feld.getWert() + getDelimiter();
			}
			
		}
	return formattedLine;
	}

	public void setDelimiter(String delimiter) {this.delimiter = delimiter;}
	
}
