package de.hannit.fsch.klr.model.csv;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.TreeMap;

public class MonatsCSV01Zeilen extends MonatsZeilen implements Serializable 
{
private static final long serialVersionUID = 5985627468815658735L;
private TreeMap<Integer, CSV01Zeile> csv01Zeilen = new TreeMap<>();

	public MonatsCSV01Zeilen() 
	{
	
	}

	public MonatsCSV01Zeilen(LocalDate berichtsMonat, Integer monatImQuartal) 
	{
	setBerichtsMonat(berichtsMonat);
	setMonatImQuartal(monatImQuartal);
	}
	
	public boolean isEmpty() 
	{
	return (csv01Zeilen.isEmpty() || csv01Zeilen.size() == 0) ? true : false;	
	}

	public TreeMap<Integer, CSV01Zeile> getZeilen() {return csv01Zeilen;}
	public void setZeilen(TreeMap<Integer, CSV01Zeile> monatsZeilen) {this.csv01Zeilen = monatsZeilen;}

	public void add(CSV01Zeile zeile) 
	{
	int index = csv01Zeilen.isEmpty() ? 1 : (csv01Zeilen.lastEntry().getKey() + 1);
	zeile.setIndex(index);
	csv01Zeilen.put(index, zeile);	
	}

	public boolean contains(CSV01Zeile zeile) 
	{
	return csv01Zeilen.containsKey(zeile.getIndex()) ? true : false;
	}

	/**
	 * Prüft, ob die enthaltenen Zeilen OK sind.
	 * Die negative Entlastungszeile plus die Summe aller anderen Zeilen muss 0 sein
	 */
	public boolean isOK() 
	{
	boolean result = false;
	
		if (csv01Zeilen.isEmpty()) 
		{
		result = false;	
		} 
		else 
		{
		CSV01Zeile entlastungsZeile = getZeilen().get(CSV01Datei.CSVZEILE_ENTLASTUNGSZEILE); 	
		float entlastungsSumme = Float.parseFloat((entlastungsZeile.getFeld(CSV01Datei.CSVFELD_ENTLASTUNGSINDEX).getWert().replace(',', '.')));
		
			for (CSV01Zeile	zeile : getZeilen().values()) 
			{
				switch (zeile.getIndex()) 
				{
				case CSV01Datei.CSVZEILE_ENTLASTUNGSZEILE: break;

				default:
				entlastungsSumme = entlastungsSumme + Float.parseFloat(zeile.getFeld(CSV01Datei.CSVFELD_ENTLASTUNGSINDEX).getWert().replace(',', '.'));	
				break;
				}
			}
		result = (Math.round(entlastungsSumme) == 0 || entlastungsSumme < 1) ? true : false;	
		}
	return result;
	}
	
	
}
