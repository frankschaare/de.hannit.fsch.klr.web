package de.hannit.fsch.klr.web.beans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import de.hannit.fsch.klr.web.commands.CreateCSV01Command;
import de.hannit.fsch.klr.web.commands.CreateMitarbeiterCommand;

/*
 * Controller für die MenuBar
 */
@ManagedBean (eager = true)
@SessionScoped
public class MenuBar implements Serializable 
{
private static final long serialVersionUID = 6140206232199776516L;
private CreateCSV01Command createCSV01Command = new CreateCSV01Command();
private CreateMitarbeiterCommand createMitarbeiterCommand = new CreateMitarbeiterCommand();

	public MenuBar() 
	{
		// TODO Auto-generated constructor stub
	}

	public CreateCSV01Command getCreateCSV01Command() {return createCSV01Command;}
	public CreateMitarbeiterCommand getCreateMitarbeiterCommand() {return createMitarbeiterCommand;}
	

}
