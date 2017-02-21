/**
 * 
 */
package de.hannit.fsch.common;

import java.text.NumberFormat;
import java.util.Date;
import java.util.TreeMap;

import de.hannit.fsch.klr.model.Datumsformate;
import de.hannit.fsch.klr.model.azv.Arbeitszeitanteil;
import de.hannit.fsch.klr.model.kostenrechnung.Kostenrechnungsobjekt;
import de.hannit.fsch.klr.model.mitarbeiter.Mitarbeiter;

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
 * Die folgenden Flags werden im NavPart gesetzt. Dort werden die Monatsummen geprüft
 * und es wird ermittelt, ob die Monatssummen der gemeldeten AZV-Anteile gleich des Bruttoaufwandes ist.
 * 
 * Nur, wenn diese beiden Bedingungen erfüllt sind, können die Daten gespeichert werden !
 */
private boolean isChecked = false;
private boolean summeOK = false;

private Date berichtsMonat = null;
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
				 * Ist die Kostenstelle / Kostenträger bereits in den Monatssummen gespeichert ?
				 * Wenn Ja, wird der Bruttoaufwand addiert,
				 * Wenn Nein, wird die Kostenstelle / Kostenträger neu eingefügt:
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
	
	public Date getBerichtsMonatAsDate()
	{
	return berichtsMonat;
	}

	public void setBerichtsMonat(Date berichtsMonat)
	{
	this.berichtsMonat = berichtsMonat;
	}

	/*
	 * Wurden die Monatssummen geprüft ?
	 */
	public boolean isChecked() {return isChecked;}
	public void setChecked(boolean isChecked) {this.isChecked = isChecked;}

	/*
	 * Stimmen die Monatssummen mit dem Gesamtbruttoaufwand überein ?
	 */
	public boolean getSummeOK() {return summeOK;}
	public void setSummeOK(boolean summeOK) {this.summeOK = summeOK;}

	public TreeMap<String, Kostenrechnungsobjekt> getGesamtKosten()	{return gesamtKosten;}
	public TreeMap<String, Kostenrechnungsobjekt> getGesamtKostenstellen()	{return gesamtKostenstellen;}
	public TreeMap<String, Kostenrechnungsobjekt> getGesamtKostentraeger()	{return gesamtKostentraeger;}

	/*
	 * Die Gesamtsumme alle gemeldeten Kostenstellen / Kostenträger
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
	 * Formatierter String für Index.xhtml
	 */
	public String getFormattedKSTMonatssumme()
	{
	return "Kostenstellen (" + NumberFormat.getCurrencyInstance().format(getKSTMonatssumme()) + ")";
	}	
	
	/*
	 * Die Gesamtsumme aller gemeldeten Kostenträger
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
	 * Formatierter String für Index.xhtml
	 */
	public String getFormattedKTRMonatssumme()
	{
	return "Kostenträger (" + NumberFormat.getCurrencyInstance().format(getKTRMonatssumme()) + ")";
	}		
}
