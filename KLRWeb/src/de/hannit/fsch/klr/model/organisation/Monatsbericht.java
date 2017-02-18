package de.hannit.fsch.klr.model.organisation;

import java.util.Date;

public class Monatsbericht implements IBericht
{
private Date berichtsMonat = null;
private double summeArbeitgeberBrutto = 0;
private double summeStellen = 0;
private int anzahlMitarbeiter = 0;

	public Monatsbericht()
	{
	}

	public Date getBerichtsMonat() {return berichtsMonat;}
	public void setBerichtsMonat(java.sql.Date berichtsMonat) 
	{
	this.berichtsMonat = new java.util.Date(berichtsMonat.getTime());
	}

	@Override
	public Double getSummeBrutto() {return summeArbeitgeberBrutto;}

	@Override
	public void setSummeBrutto(Double dSumme) {this.summeArbeitgeberBrutto = dSumme;}

	@Override
	public Double getAnzahlStellen() {return summeStellen;}

	@Override
	public void setAnzahlStellen(Double dSumme) {this.summeStellen = dSumme; }

	@Override
	public int getMitarbeiterGesamt() {return anzahlMitarbeiter;}

	@Override
	public void setMitarbeiterGesamt(int iSumme) {this.anzahlMitarbeiter = iSumme;}

}
