/**
 * 
 */
package de.hannit.fsch.klr.model.organisation;


/**
 * @author fsch
 *
 */
public interface IBericht
{
public Double getSummeBrutto();
public void setSummeBrutto(Double dSumme);

public Double getAnzahlStellen();
public void setAnzahlStellen(Double dSumme);

public int getMitarbeiterGesamt();
public void setMitarbeiterGesamt(int iSumme);
}
