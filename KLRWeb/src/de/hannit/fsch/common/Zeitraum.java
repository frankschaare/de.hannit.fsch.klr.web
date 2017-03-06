/**
 * 
 */
package de.hannit.fsch.common;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.TreeMap;

/**
 * @author fsch
 * Die Klasse Zeitraum berechnet, ausgehend vom heutigen Datum,
 * die Abfragezeitraum für die Daten
 */
public class Zeitraum implements Berichtszeitraum 
{
private LocalDate startDatum = null;
private LocalDate endDatum = null;
private LocalDateTime startDatumUhrzeit = null;
private LocalDateTime endDatumUhrzeit = null;
private static String datumsFormat = "dd.MM.yyyy";
public static DateTimeFormatter df = DateTimeFormatter.ofPattern(datumsFormat);
public static DateTimeFormatter dfDatumUhrzeit = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss").withLocale(Locale.GERMAN);
public static DateTimeFormatter DF_MONAT = DateTimeFormatter.ofPattern("MMMM");
public static DateTimeFormatter DF_JAHR = DateTimeFormatter.ofPattern("yyyy");
public static DateTimeFormatter DF_JAHR_KURZ = DateTimeFormatter.ofPattern("yy");
public static DateTimeFormatter dfDatumUhrzeitMax = DateTimeFormatter.ofPattern("EEEE', 'dd. MMMM yyyy HH:mm").withLocale(Locale.GERMAN);
public static DateTimeFormatter dfUhrzeit = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss").withLocale(Locale.GERMAN);
public static DateTimeFormatter dfStunde = DateTimeFormatter.ofPattern("HH").withLocale(Locale.GERMAN);
public static DateTimeFormatter dfStundeMinute = DateTimeFormatter.ofPattern("HH:ss").withLocale(Locale.GERMAN);
public static TemporalField TEMPORAL_KW = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear(); 

private String berichtsZeitraum = "unbekannt";

private KalenderWoche kw = null;
private LocalDate auswertungsTag = null;
private TreeMap<Integer, Quartal> quartale = null;
private Quartal auswertungsQuartal = null;
private int typ = 0;


	/**
	 * Der Konstruktor empfängt Start- und Endzeitpunkt als LocalDate
	 * Konstanten für mögliche Berichtszeiträume
	 */
	public Zeitraum(LocalDate startZeit, LocalDate endZeit) 
	{
	this.startDatum = startZeit;	
	this.endDatum = endZeit;
	this.auswertungsQuartal = new Quartal(startDatum.getMonthValue(), startDatum.getYear());
	}
	
	public Zeitraum(LocalDateTime minDatumZeit, LocalDateTime maxDatumZeit) 
	{
	this.startDatum = LocalDate.of(minDatumZeit.getYear(), minDatumZeit.getMonthValue(), minDatumZeit.getDayOfMonth());
	this.endDatum = LocalDate.of(maxDatumZeit.getYear(), maxDatumZeit.getMonthValue(), maxDatumZeit.getDayOfMonth());
	this.auswertungsQuartal = new Quartal(startDatum.getMonthValue(), startDatum.getYear());
	}
	
	/*
	 * Konstruktor für Jahresstatistiken
	 */
	public Zeitraum(String berichtsJahr) 
	{
		startDatum = LocalDate.of(Integer.parseInt(berichtsJahr), 1, 1);
		endDatum = LocalDate.of(Integer.parseInt(berichtsJahr), 12, 31);
	}

	/**
	 * Konstruktor für quartalsweise CSV-Dateien in KLRWeb
	 * Ermittelt das Auswertungsquartal, welches dann die drei Berichtsmonate des Quartals bereitstellt
	 */
	public Zeitraum(LocalDate incoming) 
	{
	setAuswertungsQuartal(new Quartal(incoming.getMonthValue(), incoming.getYear()));
	
	startDatum = getAuswertungsQuartal().getStartDatum();
	endDatum = getAuswertungsQuartal().getEndDatum();
	}
	
	public boolean equals(Zeitraum toCheck)
	{
	boolean result = false;
	
		if (getStartDatum().equals(toCheck.getStartDatum()) && getEndDatum().equals(toCheck.getEndDatum())) 
		{
		result = true;	
		}
	
	return result;
	}
	
	
	/*
	 * Berechnet die enthaltenen Quartale und liegt diese in der TreeMap ab.
	 * 
	 * ACHTUNG ! 
	 * Der Index ist die Reihenfolge der Quartale im Abfragezeitraum,
	 * NICHT die Quartalsnummer ! 
	 * ACHTUNG ! 
	 *  
	 */
	public void setQuartale() 
	{
	int index = 1;	
	LocalDate tmp = startDatum;
	Quartal q = null;
	quartale = new TreeMap<Integer, Quartal>();
	
		while (tmp.isBefore(endDatum)) 
		{
		q = new Quartal(tmp.getMonthValue(), tmp.getYear());
			if (!quartale.containsKey(index)) 
			{
			q.setIndex(index);	
			quartale.put(index, q);	
			}
		index +=1;	
		tmp = tmp.plusMonths(3);	
		}
	}
	
	public TreeMap<Integer, Quartal> getQuartale() 
	{
	return quartale;
	}

	public LocalDate getStartDatum() 
	{
		if (startDatum == null) 
		{
		startDatum = startDatumUhrzeit != null ? LocalDate.of(startDatumUhrzeit.getYear(), startDatumUhrzeit.getMonthValue(), startDatumUhrzeit.getDayOfMonth()) : null;	
		}
	return startDatum;
	}
	
	public void setStartDatum(LocalDate startDatum) 
	{
	this.startDatum = startDatum;
	}

	public void setEndDatum(LocalDate endDatum) 
	{
	this.endDatum = endDatum;
	}

	public Date getSQLStartDatum() {return Date.valueOf(startDatum);}
	public LocalDate getEndDatum() 
	{
		if (endDatum == null) 
		{
		endDatum = endDatumUhrzeit != null ? LocalDate.of(endDatumUhrzeit.getYear(), endDatumUhrzeit.getMonthValue(), endDatumUhrzeit.getDayOfMonth()) : null;	
		}
	return endDatum;
	}
	
	/*
	 * ACHTUNG: Um Fallstricke bei der Datumskonvertierung der Datenbank zu vermeiden,
	 * liefert diese Methode den FOLGETAG des Enddatums. 
	 * 
	 * Das Abfragestatement werden dann die Daten abgefragt, die KLEINER als das hier gelieferte Datum sind !
	 */
	public Date getSQLEndDatum() 
	{
	LocalDate sqlDatum = endDatum.plusDays(1);	
	return Date.valueOf(sqlDatum);
	}

	/*
	 * Leider bin ich hier auf einen Bug im DateTimeFormatter gestossen.
	 * Das Enddatum wird daher etwas umständlich formatiert:
	 */
	@Override
	public String getBerichtszeitraum() 
	{
		switch (typ) 
		{
		case Berichtszeitraum.BERICHTSZEITRAUM_JAEHRLICH:
		berichtsZeitraum = "Berichtszeitraum: Gesamtjahr " + getBerichtsJahr(); 	
		break;
		case Berichtszeitraum.BERICHTSZEITRAUM_QUARTALSWEISE:
		berichtsZeitraum = "Berichtszeitraum: " + auswertungsQuartal.getBezeichnungLang(); 	
		break;
		case Berichtszeitraum.BERICHTSZEITRAUM_MONATLICH:
		berichtsZeitraum = "Berichtszeitraum: " + getBerichtsMonat(); 	
		break;	
		case Berichtszeitraum.BERICHTSZEITRAUM_KW:
		berichtsZeitraum = "Berichtszeitraum: " + kw.getBezeichnungLang(); 	
		break;			
		
		default:
		DateTimeFormatter fDatumUhrzeit = DateTimeFormatter.ofPattern("dd.MM.YY HH:mm");	
		berichtsZeitraum = "Berichtszeitraum: " + fDatumUhrzeit.format(startDatumUhrzeit) + " Uhr - " + fDatumUhrzeit.format(endDatumUhrzeit) + " Uhr";	
		break;
		}
	
	return berichtsZeitraum; 
	}
	
	public String getBerichtszeitraumStart() 
	{
	return df.format(startDatum);
	}

	public String getBerichtszeitraumEnde() 
	{
	return df.format(endDatum);
	}
	
	/* (non-Javadoc)
	 * @see de.hannit.fsch.reportal.model.Berichtszeitraum#setBerichtszeitraum(java.lang.String)
	 */
	@Override
	public void setBerichtszeitraum(String berichtsZeitraum) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see de.hannit.fsch.reportal.model.Berichtszeitraum#getBerichtsJahr()
	 */
	@Override
	public String getBerichtsJahr() 
	{
	DateTimeFormatter df = DateTimeFormatter.ofPattern("YYYY");
	return startDatum != null ? df.format(startDatum) : df.format(startDatumUhrzeit);
	}

	/* (non-Javadoc)
	 * @see de.hannit.fsch.reportal.model.Berichtszeitraum#setBerichtsJahr(java.lang.String)
	 */
	@Override
	public void setBerichtsJahr(String berichtsJahr) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see de.hannit.fsch.reportal.model.Berichtszeitraum#getBerichtsQuartal()
	 */
	@Override
	public String getBerichtsQuartal() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.hannit.fsch.reportal.model.Berichtszeitraum#setBerichtsQuartal(java.lang.String)
	 */
	@Override
	public void setBerichtsQuartal(String berichtsQuartal) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see de.hannit.fsch.reportal.model.Berichtszeitraum#getBerichtsMonat()
	 */
	@Override
	public String getBerichtsMonat() 
	{
	DateTimeFormatter fMonatLang = DateTimeFormatter.ofPattern("MMMM yyyy");
	return startDatum != null ? fMonatLang.format(startDatum) : fMonatLang.format(startDatumUhrzeit);
	}

	/* (non-Javadoc)
	 * @see de.hannit.fsch.reportal.model.Berichtszeitraum#setBerichtsMonat(java.lang.String)
	 */
	@Override
	public void setBerichtsMonat(String berichtsMonat) {
		// TODO Auto-generated method stub

	}

	public LocalDateTime getStartDatumUhrzeit() {
		return startDatumUhrzeit;
	}

	public void setStartDatumUhrzeit(LocalDateTime startDatumUhrzeit) 
	{
	this.startDatumUhrzeit = startDatumUhrzeit;
	
		if (startDatum == null) 
		{
		startDatum = LocalDate.of(startDatumUhrzeit.getYear(), startDatumUhrzeit.getMonthValue(), startDatumUhrzeit.getDayOfMonth());	
		}
	this.kw = new KalenderWoche(startDatumUhrzeit);
	this.auswertungsTag = LocalDate.of(startDatumUhrzeit.getYear(), startDatumUhrzeit.getMonthValue(), startDatumUhrzeit.getDayOfMonth());
	}
	
	public LocalDate getAuswertungsTag() {
		return auswertungsTag;
	}

	public KalenderWoche getKw() {
		return kw;
	}

	public LocalDateTime getEndDatumUhrzeit() {
		return endDatumUhrzeit;
	}

	public void setEndDatumUhrzeit(LocalDateTime endDatumUhrzeit) 
	{
	this.endDatumUhrzeit = endDatumUhrzeit;
		if (endDatum == null) 
		{
		endDatum = LocalDate.of(endDatumUhrzeit.getYear(), endDatumUhrzeit.getMonthValue(), endDatumUhrzeit.getDayOfMonth());	
		}
	
	}

	public int getTyp() {
		return typ;
	}

	public Quartal getAuswertungsQuartal() {
		return auswertungsQuartal;
	}

	public void setAuswertungsQuartal(Quartal auswertungsQuartal) {
		this.auswertungsQuartal = auswertungsQuartal;
	}

}
