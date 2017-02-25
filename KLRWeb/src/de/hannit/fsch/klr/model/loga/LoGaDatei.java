/**
 * 
 */
package de.hannit.fsch.klr.model.loga;

import java.io.File;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

import org.primefaces.model.UploadedFile;

import de.hannit.fsch.common.CSVConstants;
import de.hannit.fsch.common.Dezimalformate;
import de.hannit.fsch.klr.model.csv.CSVDatei;

/**
 * @author fsch
 *
 */
public class LoGaDatei extends CSVDatei
{
private static final long serialVersionUID = -4808470669223797111L;

private String label = null;	

private TreeMap<Integer, LoGaDatensatz> daten = new TreeMap<Integer, LoGaDatensatz>();
private LoGaDatensatz datenSatz = null;
private SimpleDateFormat format = new SimpleDateFormat(CSVConstants.Loga.ABRECHNUNGSMONAT_DATUMSFORMAT_CSV);

private String logPrefix;


	/**
	 * @param arg0
	 */
	public LoGaDatei(String arg0)
	{
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public LoGaDatei(URI arg0)
	{
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public LoGaDatei(String arg0, String arg1)
	{
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public LoGaDatei(File arg0, String arg1)
	{
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.hannit.fsch.common.CSVDatei#read()
	 */
	@Override
	public void read()
	{
	super.read();
		
		lineCount = 1;
		for (String line : getLines())
		{
			switch (lineCount) 
			{
			// Erste Zeile wird nur verarbeitet wenn keine Kopfzeile vorhanden ist
			case 1:
				if (!hasHeader) 
				{
				datenSatz = split(line);
				daten.put(datenSatz.getPersonalNummer(), datenSatz);	
				}
			break;

			default:
			datenSatz = split(line);
			datenSatz.setLineNumber(lineCount);
			daten.put(datenSatz.getPersonalNummer(), datenSatz);	
			break;
			}	
			lineCount++;
		}
	}
	
	@Override
	public void read(UploadedFile file) 
	{
	super.read(file);
	
	lineCount = 1;
		for (String line : getLines())
		{
			switch (lineCount) 
			{
			// Erste Zeile wird nur verarbeitet wenn keine Kopfzeile vorhanden ist
			case 1:
				if (!hasHeader) 
				{
				datenSatz = split(line);
				datenSatz.setLineNumber(lineCount);
				daten.put(lineCount, datenSatz);	
				}
			break;
	
			default:
			datenSatz = split(line);
			datenSatz.setLineNumber(lineCount);
			daten.put(lineCount, datenSatz);	
			break;
			}	
			lineCount++;
		}	
	}

	/*
	 * Die CSV-Daten werden so genau wie möglich geprüft
	 */
	private LoGaDatensatz split(String line)
	{
	logPrefix = this.getClass().getName() + ".split()";	
	datenSatz = new LoGaDatensatz();	
	datenSatz.setSource(line);
	
	String[] parts = line.split(delimiter);
		// PNr.
		try
		{
		int pnr = Integer.parseInt(parts[CSVConstants.Loga.PERSONALNUMMER_INDEX_CSV]);	
		datenSatz.setPersonalNummer(pnr);
		}
		catch (NumberFormatException e)
		{
		e.printStackTrace();
		//getLog().error("NumberFormatException beim parsen der Zeile: " + datenSatz.getSource(), plugin, e);
		}

		// Summe( Betrag )
		try
		{
		double brutto = (double) Dezimalformate.DFBRUTTO.parse(parts[CSVConstants.Loga.BRUTTO_INDEX_CSV].trim()).doubleValue();		
		datenSatz.setBrutto(brutto);
		}
		catch (NumberFormatException | ParseException e)
		{
		e.printStackTrace();
		//getLog().error("NumberFormatException beim parsen der Zeile: " + datenSatz.getSource(), plugin, e);
		}
		
		// Abrechnungsmonat
		try
		{
		Date abrechnungsMonat = format.parse(parts[CSVConstants.Loga.ABRECHNUNGSMONAT_INDEX_CSV]);	
		datenSatz.setAbrechnungsMonat(abrechnungsMonat);
		}
		catch (ParseException e)
		{
		e.printStackTrace();
		//getLog().error("ParseException beim parsen des Abrechnungsmonats in Zeile: " + datenSatz.getSource(), plugin, e);
		}		
		
		// Tarifgruppe
		try
		{
		String tarifGruppe = parts[CSVConstants.Loga.TARIFGRUPPE_INDEX_CSV];	
		datenSatz.setTarifGruppe(tarifGruppe);
		}
		catch (Exception e)
		{
		e.printStackTrace();
		//getLog().error("Exception beim parsen der Zeile: " + datenSatz.getSource(), plugin, e);
		
		// TODO HIER MUSS WAS MIT DEN AUSHILFEN PASSIEREN !!!
		
		}
		
		// Tarifstufe
		try
		{
		int tarifStufe = Integer.parseInt(parts[CSVConstants.Loga.TARIFSTUFE_INDEX_CSV]);	
		datenSatz.setTarifstufe(tarifStufe);
		}
		catch (NumberFormatException e)
		{
		//getLog().error("NumberFormatException beim parsen der Zeile: " + datenSatz.getSource(), plugin, e);
		datenSatz.setTarifstufe(0);
		}		
		
		// Stellenanteil
		try
		{
		double stellenAnteil = Double.parseDouble(parts[CSVConstants.Loga.STELLENNTEIL_INDEX_CSV].replace(",", "."));	
		datenSatz.setStellenAnteil(stellenAnteil);;
		}
		catch (NumberFormatException e)
		{
		e.printStackTrace();
		//getLog().error("NumberFormatException beim parsen der Zeile: " + datenSatz.getSource(), plugin, e);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
		// e.printStackTrace();
		//getLog().error("ArrayIndexOutOfBoundsException beim parsen der Zeile: " + datenSatz.getSource(), plugin, e);
		datenSatz.setStellenAnteil(999999);
		}
		
	return datenSatz;
	}
	
	public TreeMap<Integer, LoGaDatensatz> getDaten()
	{
	return daten;
	}

	public String getColumnText(Object element, int columnIndex) 
	{
	datenSatz =  (LoGaDatensatz) element;

		switch (columnIndex) 
		{
		case 0:
		label = String.valueOf(lineCount);
		lineCount++;
		break;
		
		case CSVConstants.Loga.PERSONALNUMMER_INDEX_TABLE:
		label = String.valueOf(datenSatz.getPersonalNummer());
		break;
		
		case CSVConstants.Loga.BRUTTO_INDEX_TABLE:
		label = String.valueOf(datenSatz.getBrutto()).replace(".", ",") + " €";
		break;
		
		case CSVConstants.Loga.ABRECHNUNGSMONAT_INDEX_TABLE:
		label = format.format((datenSatz.getAbrechnungsMonat()));
		break;
		
		case CSVConstants.Loga.TARIFGRUPPE_INDEX_TABLE:
		label = datenSatz.getTarifGruppe();
		break;
		
		case CSVConstants.Loga.TARIFSTUFE_INDEX_TABLE:
		label = String.valueOf(datenSatz.getTarifstufe());
		break;		
		
		case CSVConstants.Loga.STELLENNTEIL_INDEX_TABLE:
			if (datenSatz.getStellenAnteil() > 1)
			{
			//log.warn("Stellenanteil bei Personalnummer: " + datenSatz.getPersonalNummer() + " enthält ungültigen Wert !", this.getClass().getName() + ".ITableLabelProvider.getColumnText()");	
			label = "ERROR";
			}
			else
			{
			label = String.valueOf(datenSatz.getStellenAnteil());
			}
		break;		
		
		default:
		label = "ERROR";
		break;
			
		}
	return label;
	}

}
