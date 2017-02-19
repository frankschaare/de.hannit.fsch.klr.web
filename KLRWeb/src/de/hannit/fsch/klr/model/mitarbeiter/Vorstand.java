/**
 * 
 */
package de.hannit.fsch.klr.model.mitarbeiter;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Properties;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * @author fsch
 *
 */
@ManagedBean
@ApplicationScoped
public class Vorstand extends Mitarbeiter implements Serializable
{
private static final long serialVersionUID = -1111516258611396699L;
private	Properties props;	
private ArrayList<Mitarbeiter> stellvertreter = new ArrayList<>();
private FacesContext fc = null;
private ExternalContext ec = null;

	/**
	 * 
	 */
	public Vorstand()
	{
	props = new Properties();
	fc = FacesContext.getCurrentInstance();
	ec = fc.getExternalContext();
	loadProperties();
	}
	
	private void loadProperties() 
	{
		try 
		{
		InputStream in = ec.getResourceAsStream("META-INF/HannITProperties.xml");			
		props.loadFromXML(in);
			
		setPersonalNR(Integer.parseInt(props.getProperty("vorstandPNR")));
		setTarifGruppe(props.getProperty("vorstandGehaltsgruppe"));
		setNachname(props.getProperty("vorstandName"));
		setVorname(props.getProperty("vorstandVorname"));
		
			for (int i = 1; i <= Integer.parseInt(props.getProperty("anzahlStellvertreter")); i++)
			{
			Mitarbeiter vertreter = new Mitarbeiter();
			vertreter.setPersonalNR(Integer.parseInt(props.getProperty("stellvertretenderVorstand" + i + "PNR")));
			vertreter.setTarifGruppe(props.getProperty("stellvertretenderVorstand" + i + "Gehaltsgruppe"));
			vertreter.setNachname(props.getProperty("stellvertretenderVorstand" + i + "Name"));
			vertreter.setVorname(props.getProperty("stellvertretenderVorstand" + i + "Vorname"));
					
			stellvertreter.add(vertreter);
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
