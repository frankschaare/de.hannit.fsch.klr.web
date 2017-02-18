/**
 * 
 */
package de.hannit.fsch.klr.model.loga;

import java.util.Calendar;
import java.util.Date;

/**
 * @author fsch
 *
 */
public class LoGaDatensatz implements ILogaDatensatz
{
private	int mandant = 0;
private	int personalNummer = 0;
private	double brutto = 0;
private Date abrechnungsMonat;
private java.sql.Date abrechnungsMonatSQL;
private	String tarifGruppe = null;
private	int Tarifstufe = 0;
private	double stellenAnteil = 0;
private String source = null;
private boolean exists = false;
private boolean mitarbeiterChecked = false;

private Calendar cal = Calendar.getInstance();
	/**
	 * 
	 */
	public LoGaDatensatz()
	{
		// TODO Auto-generated constructor stub
	}
	
	public java.sql.Date getAbrechnungsMonatSQL()
	{
	return abrechnungsMonatSQL;
	}

	public String getSource()
	{
		return source;
	}

	public void setSource(String source)
	{
	this.source = source;
	}



	public int getMandant()
	{
		return mandant;
	}

	public void setMandant(int mandant)
	{
		this.mandant = mandant;
	}

	public int getPersonalNummer()
	{
		return personalNummer;
	}

	public void setPersonalNummer(int personalNummer)
	{
		this.personalNummer = personalNummer;
	}

	public double getBrutto()
	{
		return brutto;
	}

	public void setBrutto(double brutto)
	{
		this.brutto = brutto;
	}

	public Date getAbrechnungsMonat()
	{
		return abrechnungsMonat;
	}

	public void setAbrechnungsMonat(Date abrechnungsMonat)
	{
	this.abrechnungsMonat = abrechnungsMonat;
	
	cal.setTime(abrechnungsMonat);
	this.abrechnungsMonatSQL = new java.sql.Date(cal.getTimeInMillis());
	}

	public String getTarifGruppe()
	{
	return tarifGruppe;
	}

	public void setTarifGruppe(String tarifGruppe)
	{
	this.tarifGruppe = tarifGruppe;
	}

	public int getTarifstufe()
	{
		return Tarifstufe;
	}

	public void setTarifstufe(int tarifstufe)
	{
		Tarifstufe = tarifstufe;
	}

	public double getStellenAnteil()
	{
		return stellenAnteil;
	}

	public void setStellenAnteil(double stellenAnteil)
	{
		this.stellenAnteil = stellenAnteil;
	}

	@Override
	public boolean existsMitarbeiter()
	{
	return exists;
	}

	@Override
	public void setexistsMitarbeiter(boolean existsMitarbeiter)
	{
	this.exists = existsMitarbeiter;	
	}

	@Override
	public boolean mitarbeiterChecked()
	{
	return mitarbeiterChecked;
	}

	@Override
	public void setMitarbeiterChecked(boolean checked)
	{
	this.mitarbeiterChecked = checked;	
	}
}
