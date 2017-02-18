/**
 * 
 */
package de.hannit.fsch.klr.model.kostenrechnung;

/**
 * @author fsch
 *
 */
public class Kostenrechnungsobjekt
{
public static final String KST = "Kostenstelle";
public static final String KTR = "Kostenträger";
	
private String bezeichnung = null;
private String beschreibung = null;
private String kostenart = null;
private double summe = 0;
private String art = null;

/**
 * Auf einigen Services wird nicht gebucht.
 * Diese werden auf nicht aktiv gesetzt 
 * und tauchen dann nicht in den Übersichtstabellen auf
 */
private boolean aktiv = true; 

/**
 * Einzelkosten
 */
private double ertrag = 0;
private double materialAufwand = 0;
private double afa = 0;
private double sonstigeBetrieblicheAufwendungen = 0;
private double personalKosten  = 0;
private double summeEinzelkosten  = 0;

/**
 * Deckungsbeitrag 1 (Ertrag - Einzelkosten)
 */
private double deckungsbeitrag1  = 0;

/**
 * Verteilung aus Kostenstellen
 */
private double verteilungKST1110  = 0;
private double verteilungKST2010  = 0;
private double verteilungKST2020  = 0;
private double verteilungKST3010  = 0;
private double verteilungKST4010  = 0;
private double verteilungKSTGesamt  = 0;

/**
 * Ergebnis = (deckungsbeitrag1 - verteilungKSTGesamt)
 */
private double ergebnis  = 0;




	/**
	 * Oberklasse für alle Kostenstellen / Kostenträger
	 */
	public Kostenrechnungsobjekt() {}

	public String getBezeichnung(){return bezeichnung;}
	public String getBeschreibung(){return beschreibung;}
	public void setBeschreibung(String incoming){this.beschreibung = incoming;}

	public void setBezeichnung(String bezeichnung)
	{
		if (bezeichnung.contains(":"))
		{
		String[] parts = bezeichnung.split(":");	
		bezeichnung = parts[0].trim();
		setBeschreibung(parts[1].trim());
		}
	this.bezeichnung = bezeichnung;
	setArt(bezeichnung);
	}
	
	public boolean isAktiv()
	{
	return this.aktiv;	
	}
	
	public String getKostenart()
	{
		return kostenart;
	}

	public void setKostenart(String kostenart)
	{
		this.kostenart = kostenart;
	}

	/*
	 * Summe auf zwei Nachkommastellen gerundet
	 */
	public double getSummeGerundet()
	{
	return Math.rint( summe * 100 ) / 100.;
	}
	
	public double getSumme()
	{
	return summe;
	}

	public void setSumme(double summe)
	{
		this.summe = summe;
	}

	public String getArt()
	{
		return art;
	}

	public void setArt(String art)
	{
		if (art.length() == 4)
		{
		this.art = KST;	
		}
		else
		{
		this.art = KTR;
		}
	}

	public double getErtrag()
	{
		return ertrag;
	}

	public void setErtrag(double ertrag)
	{
		this.ertrag = ertrag;
	}

	public double getMaterialAufwand()
	{
		return materialAufwand;
	}

	public void setMaterialAufwand(double materialAufwand)
	{
		this.materialAufwand = materialAufwand;
	}

	public double getAfa()
	{
		return afa;
	}

	public void setAfa(double afa)
	{
		this.afa = afa;
	}

	public double getSonstigeBetrieblicheAufwendungen()
	{
		return sonstigeBetrieblicheAufwendungen;
	}

	public void setSonstigeBetrieblicheAufwendungen(
			double sonstigeBetrieblicheAufwendungen)
	{
		this.sonstigeBetrieblicheAufwendungen = sonstigeBetrieblicheAufwendungen;
	}

	public double getPersonalKosten()
	{
		return personalKosten;
	}

	public void setPersonalKosten(double personalKosten)
	{
		this.personalKosten = personalKosten;
	}

	public double getSummeEinzelkosten()
	{
		return summeEinzelkosten;
	}

	public void setSummeEinzelkosten(double summeEinzelkosten)
	{
		this.summeEinzelkosten = summeEinzelkosten;
	}

	public double getDeckungsbeitrag1()
	{
		return deckungsbeitrag1;
	}

	public void setDeckungsbeitrag1(double deckungsbeitrag1)
	{
		this.deckungsbeitrag1 = deckungsbeitrag1;
	}

	public double getVerteilungKST1110()
	{
		return verteilungKST1110;
	}

	public void setVerteilungKST1110(double verteilungKST1110)
	{
		this.verteilungKST1110 = verteilungKST1110;
	}

	public double getVerteilungKST2010()
	{
		return verteilungKST2010;
	}

	public void setVerteilungKST2010(double verteilungKST2010)
	{
		this.verteilungKST2010 = verteilungKST2010;
	}

	public double getVerteilungKST2020()
	{
		return verteilungKST2020;
	}

	public void setVerteilungKST2020(double verteilungKST2020)
	{
		this.verteilungKST2020 = verteilungKST2020;
	}

	public double getVerteilungKST3010()
	{
		return verteilungKST3010;
	}

	public void setVerteilungKST3010(double verteilungKST3010)
	{
		this.verteilungKST3010 = verteilungKST3010;
	}

	public double getVerteilungKST4010()
	{
		return verteilungKST4010;
	}

	public void setVerteilungKST4010(double verteilungKST4010)
	{
		this.verteilungKST4010 = verteilungKST4010;
	}

	public double getVerteilungKSTGesamt()
	{
	return verteilungKSTGesamt;
	}

	public void setVerteilungKSTGesamt(double verteilungKSTGesamt)
	{
		this.verteilungKSTGesamt = verteilungKSTGesamt;
	}

	public double getErgebnis()
	{
		return ergebnis;
	}

	public void setErgebnis(double ergebnis)
	{
	this.ergebnis = ergebnis;
		if (this.ergebnis == 0 && this.summeEinzelkosten == 0 && this.deckungsbeitrag1 == 0)
		{
		this.aktiv = false;	
		}
	}
}
