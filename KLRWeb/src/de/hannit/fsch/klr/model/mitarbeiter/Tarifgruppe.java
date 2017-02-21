/**
 * 
 */
package de.hannit.fsch.klr.model.mitarbeiter;

import java.text.NumberFormat;
import java.util.Date;

import de.hannit.fsch.common.AppConstants;

/**
 * @author fsch
 *
 */
public class Tarifgruppe
{
private String tarifGruppe = null;
private Date berichtsMonat = null;
private double summeTarifgruppe = 0;
private double summeStellen = 0;
private double vollzeitAequivalent = 0;

	/**
	 * 
	 */
	public Tarifgruppe()
	{
	}

	public double getVollzeitAequivalent()
	{
		if (summeTarifgruppe > 0 && summeStellen > 0)
		{
		//sumStellen = (summeStellen < 1) ? 1 : summeStellen;	
		vollzeitAequivalent = summeTarifgruppe / summeStellen;
		}
		
	return vollzeitAequivalent;
	}
	
	public String getFormattedVollzeitAequivalent()
	{
	return NumberFormat.getCurrencyInstance().format((getVollzeitAequivalent()));
	}	

	public String getTarifGruppe(){	return tarifGruppe;}

	public void setTarifGruppe(String tarifGruppe)
	{
		this.tarifGruppe = tarifGruppe;
	}

	public Date getBerichtsMonat()
	{
		return berichtsMonat;
	}

	public void setBerichtsMonat(Date berichtsMonat)
	{
		this.berichtsMonat = berichtsMonat;
	}

	public double getSummeTarifgruppe()
	{
		return summeTarifgruppe;
	}

	public String getFormattedSummeTarifgruppe()
	{
	return NumberFormat.getCurrencyInstance().format((getSummeTarifgruppe()));
	}
	
	public void setSummeTarifgruppe(double summeTarifgruppe)
	{
		this.summeTarifgruppe = summeTarifgruppe;
	}

	public double getSummeStellen()
	{
		return summeStellen;
	}
	
	public String getFormattedSummeStellen()
	{
	return AppConstants.KOMMAZAHL.format((getSummeStellen()));
	}

	public void setSummeStellen(double summeStellen)
	{
		this.summeStellen = summeStellen;
	}

}
