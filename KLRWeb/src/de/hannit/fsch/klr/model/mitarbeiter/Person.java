/**
 * 
 */
package de.hannit.fsch.klr.model.mitarbeiter;

/**
 * @author fsch
 * @since 04.07.2013
 * 
 */
public class Person 
{
private String nachname = null;
private String vorname = null;

	/**
	 * 
	 */
	public Person() 
	{
	
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

}
