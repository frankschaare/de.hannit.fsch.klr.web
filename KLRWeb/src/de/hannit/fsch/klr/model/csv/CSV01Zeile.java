package de.hannit.fsch.klr.model.csv;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

import de.hannit.fsch.klr.model.Constants;
import de.hannit.fsch.klr.model.Datumsformate;

public class CSV01Zeile extends CSVZeile implements Serializable
{
private static final long serialVersionUID = -526270932816496170L;

	public CSV01Zeile() 
	{
	
	}

	@Override
	public void setAll(ArrayList<CSVFeld> toSet) 
	{
		for (CSVFeld csvFeld : toSet) 
		{
			if (csvFeld.getIndex() == CSV01Datei.CSVFELD_DATUMSINDEX) 
			{
			LocalDate tmp = LocalDate.parse(csvFeld.getWert(), Datumsformate.df);	
			setBerichtmonat(LocalDate.of(tmp.getYear(), tmp.getMonthValue(), 1));	
			}
		getFelder().put(csvFeld.getIndex(), csvFeld);	
		}
	}

	@Override
	public void setRowStyle(String toSet) 
	{
	this.rowStyle = (this.rowStyle != null && this.rowStyle.equals(Constants.CSS.ROWSTYLE_NOTNEW)) ? this.rowStyle : toSet;
	}

	@Override
	public void addFeld(CSVFeld toAdd) 
	{
	getFelder().put(toAdd.getIndex(), toAdd);	
		
		if (toAdd.getIndex() == CSV01Datei.CSVFELD_DATUMSINDEX) 
		{
		LocalDate tmp = LocalDate.parse(toAdd.getWert(), Datumsformate.df);	
		setBerichtmonat(LocalDate.of(tmp.getYear(), tmp.getMonthValue(), 1));	
		}
	}
	
	

}
