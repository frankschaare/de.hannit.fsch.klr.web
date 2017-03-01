package de.hannit.fsch.klr.web.beans;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import de.hannit.fsch.common.Dezimalformate;
import de.hannit.fsch.common.MonatsSummen;
import de.hannit.fsch.klr.model.csv.CSV01Datei;
import de.hannit.fsch.klr.model.csv.CSVDatei;
import de.hannit.fsch.klr.model.kostenrechnung.KostenStelle;
import de.hannit.fsch.klr.model.kostenrechnung.KostenTraeger;
import de.hannit.fsch.klr.model.kostenrechnung.Kostenrechnungsobjekt;
import de.hannit.fsch.klr.web.handler.csv.CSVHandler;

@ManagedBean
@SessionScoped
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

	public CreateCSV01() 
	{
	fc = FacesContext.getCurrentInstance();	
	menuBar = menuBar != null ? menuBar : fc.getApplication().evaluateExpressionGet(fc, "#{menuBar}", MenuBar.class);
		
	mSummen = menuBar.getCreateCSV01Command().getMonatsSummen();	
	splitKostenobjekte();
	createCSV();
	}


	
	/*
	 * Erstellt alle Zeilen der Datei und schreibt diese
	 */
	private void createCSV()
	{
   	logPrefix = this.getClass().getName() + ".createCSV(): ";
	
	String feld1 = null;
	String feld2 = null;
	String feld3 = null;
	String feld4 = null;
	String feld5 = null;
	String feld6 = null;
	String feld7 = null;
	String delimiter = CSVDatei.DEFAULT_DELIMITER;
	
	ArrayList<String> lines = new ArrayList<String>();	
	
	feld1 = getLetzterTagdesMonats(mSummen.getBerichtsMonatAsDate()) + delimiter;
	feld2 = CSV01Datei.ZELLE2_NEHME + delimiter;
	feld3 = CSV01Datei.ENTLASTUNGSKONTO + delimiter;
	feld4 = CSV01Datei.ZELLE4_NEHME + delimiter;
	feld5 = CSV01Datei.ZELLE5_PRÄFIX + getMonatsnummer(mSummen.getBerichtsMonatAsDate()) + delimiter;
	feld6 = CSV01Datei.ZELLE6_PRÄFIX + getMonatLang(mSummen.getBerichtsMonatAsDate()) + delimiter;
	
	feld7 = "-" + Dezimalformate.DEFAULT.format(sumKSTGerundet);
	
	lines.add(feld1+feld2+feld3+feld4+feld5+feld6+feld7);
	
		/*
		 * Abschliessend werden die Zeilen für alle Kostenstellen geschrieben
		 * Die Zellen 1,6 und 6 bleiben dabei gleich
		 */
	feld2 = CSV01Datei.ZELLE2_GEBE + delimiter;
	feld4 = CSV01Datei.ZELLE4_GEBE + delimiter;
		
	double summeBelastung = 0;
		for (KostenStelle kst : tmKostenstellen.values())
		{
		summeBelastung += kst.getSummeGerundet();	
		feld3 = kst.getBezeichnung() + delimiter;
		feld7 = Dezimalformate.DEFAULT.format(kst.getSummeGerundet());
		
		lines.add(feld1+feld2+feld3+feld4+feld5+feld6+feld7);
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
	
	/*
	 * Alle Werte sind nun in der ArrayLIst lines gesichert, 
	 * es muss nur noch die CSV Datei geschrieben werden. 
	 * 
	 * Dazu wird ein Pfad nach dem Muster:
	 * PATH_PRÄFIX + YYYY Quartal # + PATH_SUFFIX + DATEINAME_PRÄFIX + YYYYMM + DATEINAME_SUFFIX benötigt: 	
	 */
	String strPath = CSVDatei.PATH_PRÄFIX + getJahr(mSummen.getBerichtsMonatAsDate()) + " Quartal " + getQuartalsnummer(mSummen.getBerichtsMonatAsDate()) + CSV01Datei.PATH_SUFFIX;

	
	csvDatei = new CSV01Datei(strPath);
	csvDatei.hasHeader(false);
	csvDatei.setContent(lines);
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
}
