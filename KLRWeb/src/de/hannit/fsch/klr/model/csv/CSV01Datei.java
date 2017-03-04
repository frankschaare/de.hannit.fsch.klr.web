/**
 * 
 */
package de.hannit.fsch.klr.model.csv;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import de.hannit.fsch.common.Zeitraum;
import de.hannit.fsch.klr.model.Datumsformate;
import de.hannit.fsch.util.StringUtility;

/**
 * @author fsch
 *
 */
public class CSV01Datei extends CSVDatei
{
private static final long serialVersionUID = -1532024416411533051L;
private final static Logger log = Logger.getLogger(CSV01Datei.class.getSimpleName());
private FacesContext fc = FacesContext.getCurrentInstance();
private String logPrefix = null;	
private FacesMessage msg = null;
private String detail = null;
public static final int CSVFELD_DATUMSINDEX = 1;

public static final String ZELLE2_NEHME = "0";
public static final String ZELLE2_GEBE = "1";
public static final String ZELLE4_NEHME = "1100100";
public static final String ZELLE4_GEBE = "1110100";
public static final String ZELLE5_PR�FIX = "UML-";
public static final String ZELLE6_PR�FIX = "AZV ";
public static final String ENTLASTUNGSKONTO = "0400";
//public static final String PATH_PR�FIX = "\\\\RegionHannover.de\\daten\\hannit\\Rechnungswesen A�R\\KLR\\Arbeitszeitverteilung\\Reports\\";
public static final String PATH_SUFFIX = "\\CSV\\";
public static final String DATEINAME_PR�FIX = "01_CSV_Entlastung 0400 auf andere KST";
public static final String DATEINAME_SUFFIX = ".csv";	
private CSVFeld constFeld2Neme = null;
private CSVFeld constFeld2Gebe = null;
private CSVFeld constFeld4Neme = null;
private CSVFeld constFeld4Gebe = null;
private Charset charset = Charset.forName("ISO-8859-1");
private List<String> lines = null;

/**
 * Die CSV01-Datei wird f�r ein komplettes Quartal erstellt.
 * Der Berichtszeitraum enth�lt alle n�tigen Informationen
 */
private Zeitraum berichtsZeitraum = null;

/**
 * Die Monatszeilen enthalten die Zeilen der Datei nach Monaten getrennt.
 * So kann gepr�ft werden, ob alles vollst�ndig ist, oder ob bestimmte Zeilen 
 * schon vorhanden sind.
 */
private TreeMap<LocalDate, TreeMap<Integer, CSV01Zeile>> monatsZeilen = null;;

	/**
	 * @param arg0
	 */
	public CSV01Datei(String strPath)
	{
	super(strPath, (CSV01Datei.DATEINAME_PR�FIX + CSV01Datei.DATEINAME_SUFFIX));
	initModel();
	}

	/**
	 * Initialisiert das Dateimodel
	 */
	private void initModel() 
	{
	// setZeilen(new TreeMap<Integer, CSVZeile>());
	constFeld2Neme = new CSVFeld(2, CSV01Datei.ZELLE2_NEHME);
	constFeld2Gebe = new CSVFeld(2, CSV01Datei.ZELLE2_GEBE);
	constFeld4Neme = new CSVFeld(4, CSV01Datei.ZELLE4_NEHME);
	constFeld4Gebe = new CSVFeld(4, CSV01Datei.ZELLE4_GEBE);
	}

	/**
	 * @param arg0
	 */
	public CSV01Datei(URI arg0)
	{
	super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public CSV01Datei(String arg0, String arg1)
	{
	super(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public CSV01Datei(File arg0, String arg1)
	{
	super(arg0, arg1);
	}

	public CSVFeld getConstFeld2Neme() {return constFeld2Neme;}
	public CSVFeld getConstFeld2Gebe() {return constFeld2Gebe;}
	public CSVFeld getConstFeld4Neme() {return constFeld4Neme;}
	public CSVFeld getConstFeld4Gebe() {return constFeld4Gebe;}
	public Zeitraum getBerichtsZeitraum() {return berichtsZeitraum;}
	public TreeMap<LocalDate, TreeMap<Integer, CSV01Zeile>> getMonatsZeilen() {return monatsZeilen;}
	
	public void setBerichtsZeitraum(Zeitraum toSet) 
	{
	this.berichtsZeitraum = toSet;
	this.monatsZeilen = new TreeMap<>();
	
		for (LocalDate berichtsMonat : berichtsZeitraum.getAuswertungsQuartal().getBerichtsMonate().values()) 
		{
		monatsZeilen.put(berichtsMonat, new TreeMap<Integer, CSV01Zeile>());	
		}
	}
	
	@Override
	public void hasHeader(boolean toSet) 
	{
	super.hasHeader(toSet);
	}
	
	public ArrayList<CSV01Zeile> getZeilenAsList()
	{
	ArrayList<CSV01Zeile> result = new ArrayList<>();	
		for (TreeMap<Integer, CSV01Zeile> map : getMonatsZeilen().values()) 
		{
			for (CSV01Zeile csv01Zeile : map.values()) 
			{
			result.add(csv01Zeile);	
			}
		}
	return result;
	}
	
	@Override
	public boolean write() 
	{
	boolean success = false;
	
	logPrefix = this.getClass().getName() + ".write(): ";
	fc = FacesContext.getCurrentInstance();
		
		if (Files.exists(this.toPath(), new LinkOption[]{LinkOption.NOFOLLOW_LINKS}))
		{
		detail = "CSV-Datei " + this.getPath() + " existiert bereits ! Bitte pr�fen und ggf. manuell l�schen.";
		log.log(Level.SEVERE, logPrefix + detail);
		msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Datei existiert bereits !", detail);
		fc.addMessage(null, msg);			
		}
		else
		{
			try
			{
			Files.createFile(this.toPath());
			Files.write(this.toPath(), getContent(), Charset.forName("ISO-8859-15"), StandardOpenOption.WRITE);
				
			detail = "Datei " + this.getPath() + " wurde erfolgreich geschrieben.";
			log.log(Level.INFO, logPrefix + detail);
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Datei erstellt.", detail);
			fc.addMessage(null, msg);
			success = true;
			}
			catch (IOException i)
			{
			detail = i.getLocalizedMessage();
			log.log(Level.SEVERE, logPrefix + detail);
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Fehler beim Schreiben der Datei !.", detail);
			fc.addMessage(null, msg);
			i.printStackTrace();
			}
			catch (NullPointerException n) 
			{
			detail = n.getLocalizedMessage();
			log.log(Level.SEVERE, logPrefix + detail);
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Fehler beim Schreiben der Datei !.", detail);
			fc.addMessage(null, msg);
			n.printStackTrace();
			}
			
		}
	return success;	
	}
	
	@Override
	public ArrayList<String> getContent() 
	{
	ArrayList<String> toWrite = new ArrayList<>();	
		
		for (CSV01Zeile zeile : getZeilenAsList()) 
		{
		toWrite.add(zeile.getFormattedLine());	
		}
	return toWrite;
	}

	@Override
	public void read() 
	{
		/*
		 * Existiert die Zieldatei bereits, werden die vorhandenen Zeilen eingelesen.
		 * Diese werden sp�ter im Part ausgegraut dargestellt. 
		 */
		if (existsZielDatei())
		{
		setDateiVorhanden(true);	
		createCSVModel();	
		String toSet = "Datei " + getPath() + " ist bereits f�r das " + getBerichtsZeitraum().getAuswertungsQuartal().getBezeichnungLang() + " vorhanden.";
		setDateiInfo(toSet);
		}
		else 
		{
		setDateiVorhanden(false);
		setDateiInfo(CSVDatei.DATEI_NICHTVORHANDEN);
		}		
	}

	public String getDataTableHeader()
	{
	String result = getBerichtsZeitraum().getAuswertungsQuartal().getBezeichnungLang() + ": ";
		
		TreeMap<Integer, CSV01Zeile> map = null;
		int vorhanden = 0;
		int verfuegbar = 0;
		for (LocalDate date : getMonatsZeilen().keySet()) 
		{
		map = getMonatsZeilen().get(date);
			for (CSV01Zeile zeile : map.values()) 
			{
				if (zeile.getIsnew()) 
				{
				verfuegbar++;	
				} 
				else 
				{
				vorhanden++;	
				}
			}
		result = result + Datumsformate.DF_MONATJAHR.format(date) + " ( " + vorhanden + " vorhanden, " + verfuegbar + " verf�gbar ) | ";
		vorhanden = 0;
		verfuegbar = 0;
		}
	
	return StringUtility.removeLast2Char(result);	
	}
	
	public String getBerichtsmonatInfo()
	{
	String result = "";
		for (LocalDate date : getMonatsZeilen().keySet()) 
		{
		result = result + Datumsformate.DF_MONATJAHR.format(date) + " (" + getMonatsZeilen().get(date).size() + " Datens�tze) | ";	
		}	
	return result;	
	}
	
	public void createCSVModel() 
	{
   	logPrefix = this.getClass().getName() + ".read(): ";
		
	CSV01Zeile zeile = null;
	CSVFeld feld = null;
	String[] fieldArray = null;
	
	lineCount = 1;
		try 
		{
		lines = Files.readAllLines(Paths.get(super.getPath()), charset);
		
			for (String line : lines)
			{
			switch (lineCount) 
			{
			// Erste Zeile wird nur verarbeitet wenn keine Kopfzeile vorhanden ist
			case 0:
			fieldArray = line.split(delimiter); 	
			zeile = new CSV01Zeile();
			zeile.setIsnew(false);
				if (!hasHeader) 
				{
					for (int i = 0; i < fieldArray.length; i++) 
					{
					feld = new CSVFeld();
						if (i == 0) {feld.setBerichtsMonat(fieldArray[i]);}
					feld.setHeader(false);
					feld.setWert(fieldArray[i]);
					feld.setIndex((i+1));
					zeile.addFeld(feld);
					}
				}
				else 
				{
					for (int i = 0; i < fieldArray.length; i++) 
					{
					feld = new CSVFeld();
						if (i == 0) {feld.setBerichtsMonat(fieldArray[i]);}
					feld.setHeader(true);
					feld.setHeaderText(fieldArray[i]);
					feld.setIndex((i+1));
					zeile.addFeld(feld);					
					}
				}	
					try 
					{
					monatsZeilen.get(zeile.getBerichtmonat()).put(lineCount, zeile);	
					} 
					catch (NullPointerException e) 
					{
					detail = "Es wurde versucht, eine Zeile f�r den Berichtsmonat " + Datumsformate.DF_MONATJAHR.format(zeile.getBerichtmonat()) + " einzuf�gen, f�r die es keinen g�ltigen Berichtsmonat gibt.";
					log.log(Level.SEVERE, logPrefix + detail);	
					msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler beim Einlesen der vorhandenen Daten !", detail);
					FacesContext.getCurrentInstance().addMessage(null, msg);					
					}
				break;

				default:
				fieldArray = line.split(delimiter); 	
				zeile = new CSV01Zeile();
				zeile.setIsnew(false);

					for (int i = 0; i < fieldArray.length; i++) 
					{
					feld = new CSVFeld();
					feld.setHeader(false);
					feld.setWert(fieldArray[i]);
					feld.setIndex((i+1));
					zeile.addFeld(feld);
					}
					try 
					{
					monatsZeilen.get(zeile.getBerichtmonat()).put(lineCount, zeile);	
					} 
					catch (NullPointerException e) 
					{
					detail = "Es wurde versucht, eine Zeile f�r den Berichtsmonat " + Datumsformate.DF_MONATJAHR.format(zeile.getBerichtmonat()) + " einzuf�gen, f�r die es keinen g�ltigen Berichtsmonat gibt.";
					log.log(Level.SEVERE, logPrefix + detail);	
					msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler beim Einlesen der vorhandenen Daten !", detail);
					FacesContext.getCurrentInstance().addMessage(null, msg);					
					}
				break;
				}	
				lineCount++;
				}	
			} 
			catch (IOException e) 
			{
			e.printStackTrace();	
			}
	}

}
