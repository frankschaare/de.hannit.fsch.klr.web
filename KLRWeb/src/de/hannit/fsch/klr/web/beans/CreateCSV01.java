package de.hannit.fsch.klr.web.beans;

import java.io.Serializable;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import de.hannit.fsch.common.Dezimalformate;
import de.hannit.fsch.common.MonatsSummen;
import de.hannit.fsch.klr.model.Constants;
import de.hannit.fsch.klr.model.Datumsformate;
import de.hannit.fsch.klr.model.csv.CSV01Datei;
import de.hannit.fsch.klr.model.csv.CSV01Zeile;
import de.hannit.fsch.klr.model.csv.CSVFeld;
import de.hannit.fsch.klr.model.kostenrechnung.KostenStelle;
import de.hannit.fsch.klr.model.kostenrechnung.KostenTraeger;
import de.hannit.fsch.klr.model.kostenrechnung.Kostenrechnungsobjekt;
import de.hannit.fsch.klr.web.handler.csv.CSVHandler;

@ManagedBean
@ViewScoped
public class CreateCSV01 extends CSVHandler implements Serializable 
{
private static final long serialVersionUID = 6755773873844929460L;
private final static Logger log = Logger.getLogger(CreateCSV01.class.getSimpleName());	
private String logPrefix = null;	
private FacesContext fc = null;
private FacesMessage msg = null;
private String detail = null;
@ManagedProperty (value = "#{menuBar}")
private MenuBar menuBar;
private MonatsSummen mSummen = null;
private TreeMap<String, KostenStelle> tmKostenstellen = null;
private TreeMap<String, KostenTraeger> tmKostenträger = null;
private double sumKST = 0;
private double sumKSTGerundet = 0;
private CSV01Datei csvDatei = null;
private ArrayList<String[]> lines = null;
private boolean btnDownloadDisabled = true;
private boolean btnAktualisierenDisabled = false;
private boolean btnSpeichernDisabled = false;
private int zeilenVorhanden = 0;
private int zeilenHinzugefuegt = 0;




	public CreateCSV01() 
	{
   	logPrefix = this.getClass().getName() + ".CreateCSV01(): ";
	
	fc = FacesContext.getCurrentInstance();	
	menuBar = menuBar != null ? menuBar : fc.getApplication().evaluateExpressionGet(fc, "#{menuBar}", MenuBar.class);
		
	mSummen = menuBar.getMonatsSummen();
	
		if (mSummen.isChecked() && mSummen.getSummeOK()) 
		{
		splitKostenobjekte();
		createCSV();
		} 
		else 
		{
		detail = "Die Monatssummen für den ausgewählten Berichtsmonat " + mSummen.getBerichtsMonat() + " sind fehlerhaft und können so nicht verarbeitet werden."; 
		log.log(Level.SEVERE, logPrefix + detail);		
		msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Monatssummen sind fehlerhaft !", detail);
		fc.addMessage(null, msg);			
		setBtnAktualisierenDisabled(true);
		setBtnSpeichernDisabled(true);
		}
	}
	
	/**
	 * Schreibt die vorhandenen Daten in die Datei.
	 * Dowmload Folder kann über den Contextparameter:
	 * 
	 * 	de.hannit.fsch.klr.web.DOWNLOAD_FOLDER
	 * 
	 * konfiguriert werden.
	 */
	public void write()
	{
		if (csvDatei.write()) 
		{
		setBtnDownloadDisabled(true);
		setBtnAktualisierenDisabled(true);
		setBtnSpeichernDisabled(true);		
		menuBar.setCsv01();
		createCSV();
		} 
		else 
		{
		//TODO	
		}
		
	}

	public void append()
	{
		if (csvDatei.append()) 
		{
		setBtnDownloadDisabled(true);
		setBtnAktualisierenDisabled(true);
		setBtnSpeichernDisabled(true);		
		menuBar.setCsv01();
		createCSV();
		} 
		else 
		{
		//TODO
		}
		
	}
	
	/*
	 * Erstellt alle Zeilen der Datei und schreibt diese
	 * 
	 * Nachdem hasHeader(boolean) aufgerufen wurde, prüft die CSV-Datei,
	 * ob bereits Daten vorhanden sind.
	 * Wenn ja, wird ein neues CSV Model erstellt und ist unter getZeilen abrufbar.
	 * 
	 * Die so eingelesenen Zeilen haben das Flag isNew = flase, was besagt, das sie aus einer 
	 * vorhandenen Datei eingelesen wurden.
	 */
	private void createCSV()
	{
	fc = FacesContext.getCurrentInstance();	
	int zeilenIndex = 1;	
	zeilenHinzugefuegt = 0;
	zeilenVorhanden = 0;
	
   	logPrefix = this.getClass().getName() + ".createCSV(): ";
   	
   	csvDatei = menuBar.getCsv01();
   	
   	CSV01Zeile zeile = new CSV01Zeile();
   	zeile.setIndex(zeilenIndex);
   	zeile.setIsnew(true);
   	ArrayList<CSVFeld> felder = new ArrayList<>();

	CSVFeld csvFeld1 = new CSVFeld(1, getLetzterTagdesMonats(mSummen.getBerichtsMonatAsDate()));
	csvFeld1.setBerichtsMonat(mSummen.getBerichtsMonatAsLocalDate());
	CSVFeld csvFeld2 = csvDatei.getConstFeld2Neme();
	CSVFeld csvFeld3 = new CSVFeld(3, CSV01Datei.ENTLASTUNGSKONTO);
	CSVFeld csvFeld4 = csvDatei.getConstFeld4Neme();
	CSVFeld csvFeld5 = new CSVFeld(5, CSV01Datei.ZELLE5_PRÄFIX + getMonatsnummer(mSummen.getBerichtsMonatAsDate()));
	CSVFeld csvFeld6 = new CSVFeld(6, CSV01Datei.ZELLE6_PRÄFIX + getMonatLang(mSummen.getBerichtsMonatAsDate()));
	CSVFeld csvFeld7 = new CSVFeld(7, "-" + Dezimalformate.DEFAULT.format(sumKSTGerundet));
	felder.add(csvFeld1);
	felder.add(csvFeld2);
	felder.add(csvFeld3);
	felder.add(csvFeld4);
	felder.add(csvFeld5);
	felder.add(csvFeld6);
	felder.add(csvFeld7);
	
	zeile.setAll(felder);
	zeile.setRowStyle(Constants.CSS.ROWSTYLE_ENTNAME);
	
		if (!csvDatei.getMonatsZeilen().containsKey(zeile.getBerichtmonat())) 
		{
		detail = "Die gespeicherte CSV01-Datei enthält Monatszeilen für die Auswertungsmonate: " + csvDatei.getBerichtsmonatInfo();
		detail = detail + ", es sollen aber Zeilen für den Berichtsmonat " + Datumsformate.DF_MONATJAHR.format(zeile.getBerichtmonat()) + " hinzugefügt werden."; 
		log.log(Level.SEVERE, logPrefix + detail);		
		msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehlerhafte CSV01-Datei !", detail);
		fc.addMessage(null, msg);			} 
		else 
		{
			if (csvDatei.getMonatsZeilen().get(zeile.getBerichtmonat()).contains(zeile)) 
			{
				if (zeile.getIsnew()) 
				{
				detail = "Die gespeicherte CSV01-Datei enthält bereits eine Zeile mit dem Index " + zeile.getIndex() + " für den Auswertungsmonat " + Datumsformate.DF_MONATJAHR.format(zeile.getBerichtmonat()) + ". Die Zeile wurde nicht hinzugefügt !";
				log.log(Level.SEVERE, logPrefix + detail);
				zeilenVorhanden++;
				}
			} 
			else 
			{
			csvDatei.getMonatsZeilen().get(zeile.getBerichtmonat()).add(zeile);	
			detail = "Zeile " + zeile.getIndex() + " erfolgreich für den Auswertungsmonat " + Datumsformate.DF_MONATJAHR.format(zeile.getBerichtmonat()) + " hinzugefügt.";
			log.log(Level.INFO, logPrefix + detail);
			zeilenHinzugefuegt++;
			}
			
		}
	zeilenIndex++;
	/*
	 * Abschliessend werden die Zeilen für alle Kostenstellen geschrieben
	 * Die Zellen 1,5 und 6 bleiben dabei gleich
	 */
	csvFeld2 = csvDatei.getConstFeld2Gebe();
	csvFeld4 = csvDatei.getConstFeld4Gebe();
	
	double summeBelastung = 0;

		for (KostenStelle kst : tmKostenstellen.values())
		{
		felder = new ArrayList<>();
		zeile = new CSV01Zeile();
		zeile.setIndex(zeilenIndex);
		zeile.setIsnew(true);

		summeBelastung += kst.getSummeGerundet();	
		csvFeld3 = new CSVFeld(3, kst.getBezeichnung());
		csvFeld7 = new CSVFeld(7, Dezimalformate.DEFAULT.format(kst.getSummeGerundet()));
		
		felder.add(csvFeld1);
		felder.add(csvFeld2);
		felder.add(csvFeld3);
		felder.add(csvFeld4);
		felder.add(csvFeld5);
		felder.add(csvFeld6);
		felder.add(csvFeld7);
		
		zeile.setAll(felder);
		zeile.setRowStyle(Constants.CSS.ROWSTYLE_GUTSCHRIFT);
			if (!csvDatei.getMonatsZeilen().containsKey(zeile.getBerichtmonat())) 
			{
			detail = "Die gespeicherte CSV01-Datei enthält Monatszeilen für die Auswertungsmonate: ";
				for (LocalDate date : csvDatei.getMonatsZeilen().keySet()) 
				{
				detail = detail + Datumsformate.DF_MONATJAHR.format(date) + " ";	
				}
			detail = detail + ", es sollen aber Zeilen für den Berichtsmonat " + Datumsformate.DF_MONATJAHR.format(zeile.getBerichtmonat()) + " hinzugefügt werden."; 
			log.log(Level.SEVERE, logPrefix + detail);		
			msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehlerhafte CSV01-Datei !", detail);
			fc.addMessage(null, msg);			} 
			else 
			{
				if (csvDatei.getMonatsZeilen().get(zeile.getBerichtmonat()).contains(zeile)) 
				{
				detail = "Die gespeicherte CSV01-Datei enthält bereits eine Zeile mit dem Index " + zeile.getIndex() + " für den Auswertungsmonat " + Datumsformate.DF_MONATJAHR.format(zeile.getBerichtmonat()) + ". Die Zeile wurde nicht hinzugefügt !";
				log.log(Level.SEVERE, logPrefix + detail);
				zeilenVorhanden++;
				} 
				else 
				{
				csvDatei.getMonatsZeilen().get(zeile.getBerichtmonat()).add(zeile);	
				detail = "Zeile " + zeile.getIndex() + " erfolgreich für den Auswertungsmonat " + Datumsformate.DF_MONATJAHR.format(zeile.getBerichtmonat()) + " hinzugefügt.";
				log.log(Level.INFO, logPrefix + detail);
				zeilenHinzugefuegt++;
				}
				
			}
		zeilenIndex++;	
		}
		
		switch (zeilenVorhanden) 
		{
		case 0: 
		setBtnAktualisierenDisabled(true);
		setBtnSpeichernDisabled(false);
		break;
		
		default:
		detail = "Die gespeicherte CSV01-Datei enthält bereits " + zeilenVorhanden + " Zeilen für den Auswertungsmonat " + Datumsformate.DF_MONATJAHR.format(zeile.getBerichtmonat()) + ". Die Zeilen wurde nicht hinzugefügt !";
		log.log(Level.WARNING, logPrefix + detail);		
		msg = new FacesMessage(FacesMessage.SEVERITY_WARN, zeilenVorhanden + " Zeilen übersprungen !", detail);
		fc.addMessage(null, msg);
		boolean test = csvDatei.getMonatsZeilen().get(zeile.getBerichtmonat()).isOK();
		break;
		}
		
		switch (zeilenHinzugefuegt) 
		{
		case 0:
		detail = "Es wurden keine Zeilen hinzugefügt, da bereits Daten vorhanden waren !";
		log.log(Level.WARNING, logPrefix + detail);		
		msg = new FacesMessage(FacesMessage.SEVERITY_WARN, zeilenVorhanden + " Zeilen übersprungen !", detail);
		fc.addMessage(null, msg);		

		setBtnDownloadDisabled(false);
		setBtnAktualisierenDisabled(true);
		setBtnSpeichernDisabled(true);
		break;

		default:
		detail = "Es wurden " + zeilenHinzugefuegt + " Zeilen für den Auswertungsmonat " + Datumsformate.DF_MONATJAHR.format(zeile.getBerichtmonat()) + " hinzugefügt. Diese können jetzt über den Button speichern in die Datei " + csvDatei.getName() + " gespeichert werden.";
		log.log(Level.INFO, logPrefix + detail);		
		msg = new FacesMessage(FacesMessage.SEVERITY_INFO, zeilenHinzugefuegt + " Zeilen erfolgreich hinzugefügt.", detail);
		fc.addMessage(null, msg);		

			switch (csvDatei.getEmptyMonatsMaps()) 
			{
			case 1:
			setBtnAktualisierenDisabled(false);
			setBtnSpeichernDisabled(true);
			setBtnDownloadDisabled(true);
			break;
			case 2:
			setBtnAktualisierenDisabled(true);
			setBtnSpeichernDisabled(false);
			setBtnDownloadDisabled(true);
			break;			
			default:
			setBtnAktualisierenDisabled(true);
			setBtnSpeichernDisabled(true);
			setBtnDownloadDisabled(false);
			break;
			}
		
		break;
		}
		
		
		if (sumKSTGerundet != summeBelastung)
		{
		detail = "Buchungssätze wurden erstellt. Entlastung 0400 = " + Dezimalformate.DEFAULT.format(sumKSTGerundet) + " entspricht NICHT der Summe Belastung Kostenstellen = " +  Dezimalformate.DEFAULT.format(summeBelastung);
		log.log(Level.SEVERE, logPrefix + detail);	
		msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Monatssummen fehlerhaft !", detail);
		FacesContext.getCurrentInstance().addMessage(null, msg);		
		}
		else
		{
		log.log(Level.INFO, logPrefix + "Buchungssätze wurden erstellt. Entlastung 0400 = " + Dezimalformate.DEFAULT.format(sumKSTGerundet) + " entspricht der Summe Belastung Kostenstellen = " +  Dezimalformate.DEFAULT.format(summeBelastung));	
		}	

	}	
	
	/*
	 * Splitte die Monatssumen nach Kostenstellen und Kostenträgern
	 * Für diese Datei werden nur die Kostenstellen benötigt !
	 */
	private void splitKostenobjekte()
	{
   	logPrefix = this.getClass().getName() + ".splitKostenobjekte(): ";
		
	KostenStelle kst = null;
	KostenTraeger ktr = null;
	tmKostenstellen = new TreeMap<String, KostenStelle>();
	tmKostenträger = new TreeMap<String, KostenTraeger>();
			
		for (Kostenrechnungsobjekt kto : mSummen.getGesamtKosten().values())
		{
			switch (kto.getBezeichnung().length())
			{
			case 4:
			kst = new KostenStelle();
			kst.setBezeichnung(kto.getBezeichnung());
			kst.setSumme(kto.getSumme());
			tmKostenstellen.put(kst.getBezeichnung(), kst);		
			break;
			case 8:
			ktr = new KostenTraeger();
			ktr.setBezeichnung(kto.getBezeichnung());
			ktr.setSumme(kto.getSumme());
			tmKostenträger.put(ktr.getBezeichnung(), ktr);	
			break;

			default:
			detail = "Monatssummen enthält ungültige Bezeichnung (" + kto.getBezeichnung() + " für Kostenrechnungsobjekt !";
			log.log(Level.SEVERE, logPrefix + detail);	
			msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Monatssummen fehlerhaft !", detail);
			FacesContext.getCurrentInstance().addMessage(null, msg);				
			break;
			}
		}
	/*
	 * Sicherheitshalber werden Kostenstellen und Kostenträger erneut aufsummiert
	 * und mit den Gesamtkosten verglichlichen. Diese MÜSSEN gleich sein !	
	 */
	this.sumKST = 0;
	sumKSTGerundet = 0;
		for (KostenStelle ks : tmKostenstellen.values())
		{
		sumKST += ks.getSumme();
		sumKSTGerundet += ks.getSummeGerundet();
		}
			
	double sumKTR = 0;
		for (KostenTraeger kt : tmKostenträger.values())
		{
		sumKTR += kt.getSumme();	
		}	
	log.log(Level.INFO, logPrefix + "Gesamtsumme (" + NumberFormat.getCurrencyInstance().format(mSummen.getKstktrMonatssumme()) + ") wurde erfolgreich in Kostenstellen (" + NumberFormat.getCurrencyInstance().format(sumKST) + ") und Kostenträgern (" + NumberFormat.getCurrencyInstance().format(sumKTR) + ") gesplittet.");	
	}	
	public MenuBar getMenuBar() {return menuBar;}
	public void setMenuBar(MenuBar menuBar) {this.menuBar = menuBar;}
	public ArrayList<String[]> getLines() {return lines;}
	public CSV01Datei getCsvDatei() {return csvDatei;}
	public boolean getBtnAktualisierenDisabled() {return btnAktualisierenDisabled;}
	public void setBtnAktualisierenDisabled(boolean btnAktualisierenDisabled) {this.btnAktualisierenDisabled = btnAktualisierenDisabled;}
	public boolean getBtnSpeichernDisabled() {return btnSpeichernDisabled;}
	public void setBtnSpeichernDisabled(boolean btnSpeichernDisabled) {this.btnSpeichernDisabled = btnSpeichernDisabled;}
	public boolean getBtnDownloadDisabled() {return btnDownloadDisabled;}
	public void setBtnDownloadDisabled(boolean toSet) {this.btnDownloadDisabled = toSet;
	}
	
	
}
