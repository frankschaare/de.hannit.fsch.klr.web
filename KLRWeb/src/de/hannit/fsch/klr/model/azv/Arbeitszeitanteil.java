/**
 * 
 */
package de.hannit.fsch.klr.model.azv;

import java.sql.Date;

/**
 * @author fsch
 *
 */
public class Arbeitszeitanteil implements IArbeitszeitanteil
{	
private String id = null;
private int personalNR = 0;
private	int iTeam = 9;	
private	Date berichtsMonat = null;
private	String kostenStelle = null;
private	String kostenStelleBezeichnung = null;
private	String kostenTraeger = null;
private	String kostenTraegerBezeichnung = null;
private	int prozentAnteil = 0;
private double prozentanteilGemeinkosten = 0;
private double anteilGemeinkosten = 0;
/*
 * Der Anteil des Vollzeitäquivalents je Kostenstelle / Kostenträger
 * Wird festegelegt, wenn in der Klasse Mitarbeiter das Vollzeitäquivalent feststeht.
 */
private double bruttoAufwand = 0;

	/**
	 * 
	 */
	public Arbeitszeitanteil()
	{
	}
	
	public double getBruttoAufwand() {return bruttoAufwand;}
	public void setBruttoAufwand(double incoming)
	{
	this.bruttoAufwand = incoming;
	}
	
	public double getAnteilGemeinkosten()
	{
		return anteilGemeinkosten;
	}

	public void setAnteilGemeinkosten(double anteilGemeinkosten)
	{
		this.anteilGemeinkosten = anteilGemeinkosten;
	}

	public double getProzentanteilGemeinkosten()
	{
	return prozentanteilGemeinkosten;
	}

	/*
	 * Ermittelt den Anteil an den gesamten Gemeinkosten
	 * Dabei ist anteilGemeinkosten:
	 * 
	 */
	public void setProzentanteilGemeinkosten(double incoming)
	{
	this.prozentanteilGemeinkosten = incoming;
	}

	/* (non-Javadoc)
	 * @see de.hannit.fsch.common.csv.azv.IArbeitszeitanteil#getBerichtsMonat()
	 */
	@Override
	public Date getBerichtsMonat() {return this.berichtsMonat;}

	/* (non-Javadoc)
	 * @see de.hannit.fsch.common.csv.azv.IArbeitszeitanteil#setBerichtsMonat(java.sql.Date)
	 */
	@Override
	public void setBerichtsMonat(Date berichtsMonat){this.berichtsMonat = berichtsMonat;}

	/* (non-Javadoc)
	 * @see de.hannit.fsch.common.csv.azv.IArbeitszeitanteil#getKostenstelle()
	 */
	@Override
	public String getKostenstelle(){return this.kostenStelle;}

	/* (non-Javadoc)
	 * @see de.hannit.fsch.common.csv.azv.IArbeitszeitanteil#setKostenstelle(java.lang.String)
	 */
	@Override
	public void setKostenstelle(String kostenStelle){this.kostenStelle = kostenStelle;}

	/* (non-Javadoc)
	 * @see de.hannit.fsch.common.csv.azv.IArbeitszeitanteil#getKostentraeger()
	 */
	@Override
	public String getKostentraeger(){return this.kostenTraeger;}

	/* (non-Javadoc)
	 * @see de.hannit.fsch.common.csv.azv.IArbeitszeitanteil#setKostentraeger(java.lang.String)
	 */
	@Override
	public void setKostentraeger(String kostenTraeger){this.kostenTraeger = kostenTraeger;}

	/* (non-Javadoc)
	 * @see de.hannit.fsch.common.csv.azv.IArbeitszeitanteil#getProzentanteil()
	 */
	@Override
	public int getProzentanteil(){return this.prozentAnteil;}

	/* (non-Javadoc)
	 * @see de.hannit.fsch.common.csv.azv.IArbeitszeitanteil#setProzentanteil(int)
	 */
	@Override
	public void setProzentanteil(int prozentAnteil){this.prozentAnteil = prozentAnteil;}

	@Override
	public int getITeam(){return (this.iTeam != 9) ? this.iTeam : 0;}

	@Override
	public void setITeam(int teamNR)
	{ 
		/*
		 * Der OS/ECM Webservice liefert statt bisher TeamNR 0 für den Vorstand jetzt 9
		 * Die 9 wird daher auf 0 umgesetzt 
		 */
		if (teamNR == 9)
		{
		this.iTeam = 0;	
		}
		else
		{
		this.iTeam = teamNR;
		}
	}

	@Override
	public String getKostenstelleOderKostentraegerLang()
	{
	return this.kostenStelle != null ? this.kostenStelle + ": " + getKostenStelleBezeichnung() : this.kostenTraeger + ": " + getKostenTraegerBezeichnung();
	}

	@Override
	public String getKostenStelleBezeichnung() {return this.kostenStelleBezeichnung;}

	@Override
	public void setKostenStelleBezeichnung(String kostenStelleBezeichnung) {this.kostenStelleBezeichnung = kostenStelleBezeichnung;	
	}

	@Override
	public String getKostenTraegerBezeichnung() {return this.kostenTraegerBezeichnung;}

	@Override
	public void setKostenTraegerBezeichnung(String kostenTraegerBezeichnung) {this.kostenTraegerBezeichnung = kostenTraegerBezeichnung;}

	@Override
	public boolean isKostenstelle()
	{
	return this.kostenStelle != null ? true : false;
	}

	@Override
	public boolean isKostentraeger()
	{
	return this.kostenTraeger != null ? true : false;
	}

	@Override
	public String getID() {return this.id;
	}

	@Override
	public void setID(String id) {this.id = id;}

	@Override
	public int getPersonalNummer()
	{
	return this.personalNR;
	}

	@Override
	public void setPersonalNummer(int personalNR)
	{
	this.personalNR = personalNR;	
	}

}
