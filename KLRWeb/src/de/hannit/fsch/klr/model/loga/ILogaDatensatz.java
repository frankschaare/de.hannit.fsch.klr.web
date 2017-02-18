/**
 * 
 */
package de.hannit.fsch.klr.model.loga;

import java.util.Date;

/**
 * @author fsch
 *
 */
public interface ILogaDatensatz
{
public boolean existsMitarbeiter();
public void setexistsMitarbeiter(boolean existsMitarbeiter);

public boolean mitarbeiterChecked();
public void setMitarbeiterChecked(boolean checked);

public int getMandant();
public void setMandant(int mandant);

public int getPersonalNummer();
public void setPersonalNummer(int personalNummer);

public double getBrutto();
public void setBrutto(double brutto);

public Date getAbrechnungsMonat();
public void setAbrechnungsMonat(Date abrechnungsMonat);

public String getTarifGruppe();
public void setTarifGruppe(String tarifGruppe);

public int getTarifstufe();
public void setTarifstufe(int tarifstufe);

public double getStellenAnteil();
public void setStellenAnteil(double stellenAnteil);

public String getSource();
public void setSource(String source);
}
