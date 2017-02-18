/**
 * 
 */
package de.hannit.fsch.klr.model.azv;

import java.util.Date;

/**
 * @author fsch
 *
 */
public interface IAZVDatensatz
{
public int getRowCount();
public void setRowCount(int incoming);

public int getPersonalNummer();
public void setPersonalNummer(int personalNummer);

public String getTeam();
public void setTeam(String team);

public String getNachname();
public void setNachname(String nachname);

public String getEMail();
public void setEMail(String eMail);

public Date getBerichtsMonat();
public void setBerichtsMonat(Date berichtsMonat);
public void setBerichtsMonatAsString(String berichtsMonat);
public String getBerichtsMonatAsString();
public void setBerichtsJahrAsString(String berichtsJahr);
public String getBerichtsJahrAsString();

public String getUserName();
public void setUserName(String username);

public void setKostenArt(String kostenArt);

public String getKostenstelle();
public void setKostenstelle(String kostenStelle);

public String getKostentraeger();
public void setKostentraeger(String kostenTraeger);

public int getProzentanteil();
public void setProzentanteil(int prozentAnteil);

public String getSource();
public void setSource(String source);
}
