package de.hannit.fsch.klr.web.beans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import de.hannit.fsch.common.MonatsSummen;
import de.hannit.fsch.common.Zeitraum;
import de.hannit.fsch.klr.model.ICommand;
import de.hannit.fsch.klr.model.csv.CSV01Datei;
import de.hannit.fsch.klr.model.mitarbeiter.Mitarbeiter;
import de.hannit.fsch.klr.web.commands.CreateCSV01Command;
import de.hannit.fsch.klr.web.commands.CreateMitarbeiterCommand;

/*
 * Controller für die MenuBar
 */
@ManagedBean (eager = true)
@SessionScoped
public class MenuBar implements Serializable, ICommand 
{
private static final long serialVersionUID = 6140206232199776516L;
private CreateCSV01Command createCSV01Command = new CreateCSV01Command();
private CreateMitarbeiterCommand createMitarbeiterCommand = new CreateMitarbeiterCommand();
private String downloadPath = null;
private CSV01Datei csv01 = null;
private MonatsSummen monatsSummen = null;



	public MenuBar() 
	{
	ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();	
	downloadPath = ec.getRealPath(ec.getInitParameter("de.hannit.fsch.klr.web.DOWNLOAD_FOLDER"));
	}

	public CreateCSV01Command getCreateCSV01Command() {return createCSV01Command;}
	public CreateMitarbeiterCommand getCreateMitarbeiterCommand() {return createMitarbeiterCommand;}
	public String getDownloadPath() {return downloadPath;}

	@Override
	public void mitarbeiterChanged(Mitarbeiter incoming) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMitarbeiter(Mitarbeiter toSet) {}
	@Override
	public Mitarbeiter getMitarbeiter() {return null;}

	@Override
	public void monatsSummenChanged(MonatsSummen incoming) 
	{
	setMonatsSummen(incoming);
	setCsv01();	
	}
	
	public CSV01Datei getCsv01() {return csv01;}

	public void setCsv01() 
	{
	Zeitraum berichtsZeitraum = new Zeitraum(monatsSummen.getBerichtsMonatAsLocalDate());
	csv01 = new CSV01Datei(getDownloadPath());
	
		if (csv01.existsZielDatei()) 
		{
		csv01.hasHeader(false);
		csv01.read();
		} 
		else 
		{	
		csv01.setBerichtsZeitraum(berichtsZeitraum);
		csv01.hasHeader(false);
		csv01.read();				
		}
	}

	@Override
	public MonatsSummen getMonatsSummen() {return monatsSummen;}

	@Override
	public void setMonatsSummen(MonatsSummen toSet) {this.monatsSummen = toSet;}

	@Override
	public boolean getDisabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getToolTipText() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
