/**
 * 
 */
package de.hannit.fsch.common;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.TreeMap;

import de.hannit.fsch.klr.model.Datumsformate;
import de.hannit.fsch.klr.model.azv.Arbeitszeitanteil;
import de.hannit.fsch.klr.model.kostenrechnung.Kostenrechnungsobjekt;
import de.hannit.fsch.klr.model.mitarbeiter.Mitarbeiter;
import de.hannit.fsch.util.DateUtility;

/**
 * @author fsch
 *
 */
public class MonatsSummen
{
private TreeMap<String, Kostenrechnungsobjekt> gesamtKosten = null;
private TreeMap<String, Kostenrechnungsobjekt> gesamtKostenstellen = null;
private TreeMap<String, Kostenrechnungsobjekt> gesamtKostentraeger = null;
private Kostenrechnungsobjekt kto = null;
private double kstktrMonatssumme = 0;
/**
 * Die folgenden Flags werden im NavPart gesetzt. Dort werden die Monatsummen gepr�ft
 * und es wird ermittelt, ob die Monatssummen der gemeldeten AZV-Anteile gleich des Bruttoaufwandes ist.
 * 
 * Nur, wenn diese beiden Bedingungen erf�llt sind, k�nnen die Daten gespeichert werden !
 */
private boolean isChecked = false;
private boolean summeOK = false;

private Date berichtsMonat = null;
private String monatsNummer = null;

/**
	 * 
	 */
	public MonatsSummen()
	{
		// TODO Auto-generated constructor stub
	}
	
	public void setGesamtSummen(TreeMap<Integer, Mitarbeiter> incoming)
	{
	gesamtKosten = new TreeMap<String, Kostenrechnungsobjekt>();
	gesamtKostenstellen = new TreeMap<String, Kostenrechnungsobjekt>();
	gesamtKostentraeger = new TreeMap<String, Kostenrechnungsobjekt>();
	
		for (Mitarbeiter m : incoming.values())
		{	
			for (Arbeitszeitanteil azv : m.getAzvMonat().values())
			{
				/*
				 * Ist die Kostenstelle / Kostentr�ger bereits in den Monatssummen gespeichert ?
				 * Wenn Ja, wird der Bruttoaufwand addiert,
				 * Wenn Nein, wird die Kostenstelle / Kostentr�ger neu eingef�gt:
				 */
				String bezeichnung = (azv.getKostenstelle() != null) ? azv.getKostenstelle() : azv.getKostentraeger();
				if (gesamtKosten.containsKey(bezeichnung))
				{
				kto = gesamtKosten.get(bezeichnung);	
				kto.setSumme((kto.getSumme() + azv.getBruttoAufwand()));
				}
				else
				{
				kto = new Kostenrechnungsobjekt();	
				kto.setBezeichnung(bezeichnung);
				kto.setBeschreibung(azv.isKostenstelle() ? azv.getKostenStelleBezeichnung() : azv.getKostenTraegerBezeichnung());
				kto.setSumme(azv.getBruttoAufwand());
				
				gesamtKosten.put(bezeichnung, kto);
				}
			}
		}
		for (Kostenrechnungsobjekt k : gesamtKosten.values())
		{
			if (k.getArt().equalsIgnoreCase(Kostenrechnungsobjekt.KST))
			{
			gesamtKostenstellen.put(k.getBezeichnung(), k);	
			}
			else
			{
			gesamtKostentraeger.put(k.getBezeichnung(), k);	
			}
			
		}
	}	
	
	public String getBerichtsMonat()
	{
	return Datumsformate.STANDARDFORMAT_SQLSERVER.format(berichtsMonat);
	}
	
	public LocalDate getBerichtsMonatAsLocalDate()
	{
	return berichtsMonat != null ? DateUtility.asLocalDate(berichtsMonat) : null;
	}
	
	public Date getBerichtsMonatAsDate()
	{
	return berichtsMonat;
	}

	public void setBerichtsMonat(Date berichtsMonat)
	{
	this.berichtsMonat = berichtsMonat;
	setMonatsNummer(getBerichtsMonatAsLocalDate());
	}

	/*
	 * Wurden die Monatssummen gepr�ft ?
	 */
	public boolean isChecked() {return isChecked;}
	public void setChecked(boolean isChecked) {this.isChecked = isChecked;}

	/*
	 * Stimmen die Monatssummen mit dem Gesamtbruttoaufwand �berein ?
	 */
	public boolean getSummeOK() {return summeOK;}
	public void setSummeOK(boolean summeOK) {this.summeOK = summeOK;}

	public TreeMap<String, Kostenrechnungsobjekt> getGesamtKosten()	{return gesamtKosten;}
	public TreeMap<String, Kostenrechnungsobjekt> getGesamtKostenstellen()	{return gesamtKostenstellen;}
	public TreeMap<String, Kostenrechnungsobjekt> getGesamtKostentraeger()	{return gesamtKostentraeger;}

	/*
	 * Die Gesamtsumme alle gemeldeten Kostenstellen / Kostentr�ger
	 */
	public double getKstktrMonatssumme()
	{
	kstktrMonatssumme = 0;

		for (String s : gesamtKosten.keySet())
		{
		kstktrMonatssumme += gesamtKosten.get(s).getSumme();	
		}

	return kstktrMonatssumme;
	}

	/*
	 * Die Gesamtsumme aller gemeldeten Kostenstellen
	 */
	public double getKSTMonatssumme()
	{
	double result = 0;

		for (String s : gesamtKostenstellen.keySet())
		{
		result += gesamtKostenstellen.get(s).getSumme();	
		}

	return result;
	}
	
	/*
	 * Formatierter String f�r Index.xhtml
	 */
	public String getFormattedKSTMonatssumme()
	{
	return "Kostenstellen (" + NumberFormat.getCurrencyInstance().format(getKSTMonatssumme()) + ")";
	}	
	
	/*
	 * Die Gesamtsumme aller gemeldeten Kostentr�ger
	 */
	public double getKTRMonatssumme()
	{
	double result = 0;

		for (String s : gesamtKostentraeger.keySet())
		{
		result += gesamtKostentraeger.get(s).getSumme();	
		}

	return result;
	}
	
	/*
	 * Formatierter String f�r Index.xhtml
	 */
	public String getFormattedKTRMonatssumme()
	{
	return "Kostentr�ger (" + NumberFormat.getCurrencyInstance().format(getKTRMonatssumme()) + ")";
	}

	public String getMonatsNummer() {return monatsNummer;}

	public void setMonatsNummer(LocalDate toCheck) 
	{
	String mNummer = null;
	
		switch (toCheck.getMonthValue())
		{
		case 1: mNummer = "01"; break;
		case 2: mNummer = "02"; break;		
		case 3: mNummer = "03"; break;
		case 4: mNummer = "01"; break;
		case 5: mNummer = "02";	break;		
		case 6: mNummer = "03";	break;
		case 7: mNummer = "01"; break;
		case 8: mNummer = "02";	break;		
		case 9: mNummer = "03";	break;
		case 10:mNummer = "01";	break;
		case 11:mNummer = "02";	break;		
		case 12:mNummer = "03";	break;
		default:mNummer = null;	break;
		}
	this.monatsNummer = mNummer;
	}		
	
	
}
