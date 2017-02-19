package de.hannit.fsch.klr.web.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.faces.application.ProjectStage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import de.hannit.fsch.klr.dataservice.mssql.MSSQLDataService;
import de.hannit.fsch.klr.model.Datumsformate;

@ManagedBean
@ApplicationScoped
public class IndexSelectOneController implements Serializable 
{
private static final long serialVersionUID = 8804628853957061909L;
private final static Logger log = Logger.getLogger(IndexSelectOneController.class.getSimpleName());	
private String logPrefix = null;	

@ManagedProperty (value = "#{dataService}")
private MSSQLDataService dataService;	
private FacesContext fc = null;
private ArrayList<LocalDate> azvBerichtsMonate = null;
private ArrayList<LocalDate> logaBerichtsMonate = null;
private TreeMap<Integer, SelectItem> availableMonth = null;
private TreeMap<Integer, SelectItem> availableYears = null;
private SelectItem maxMonth = null;
private SelectItem maxYear = null;
private String selectedMonth = null;
private String selectedYear = null;
private LocalDate selectedDate = null;	
private LocalDate maxDate = null;	
private LocalDate maxAZVDate = null;	
private LocalDate maxLoGaDate = null;	
private boolean buttonForwardEnabled = false;
private boolean buttonBackEnabled = false;


	public IndexSelectOneController() 
	{
	logPrefix = this.getClass().getName() + ": ";
		
	fc = FacesContext.getCurrentInstance();
	dataService = dataService != null ? dataService : fc.getApplication().evaluateExpressionGet(fc, "#{dataService}", MSSQLDataService.class);
	
	this.azvBerichtsMonate = dataService.getAzvBerichtsMonate();
				
		for (LocalDate localDate : azvBerichtsMonate) 
		{
			if (maxAZVDate == null)
			{
			maxAZVDate = localDate;	
			}
			else
			{
			maxAZVDate = localDate.isAfter(maxAZVDate) ? localDate : maxAZVDate;	
			}	
		}
	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "in der Datenbank wurden AZV Daten bis " + Datumsformate.DF_MONATJAHR.format(maxAZVDate) + " gefunden");}	
	
	this.logaBerichtsMonate = dataService.getLogaBerichtsMonate();
		for (LocalDate localDate : logaBerichtsMonate) 
		{
			if (maxLoGaDate == null)
			{
			maxLoGaDate = localDate;	
			}
			else
			{
			maxLoGaDate = localDate.isAfter(maxLoGaDate) ? localDate : maxLoGaDate;	
			}		
		}
	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "in der Datenbank wurden LoGa Daten bis " + Datumsformate.DF_MONATJAHR.format(maxLoGaDate) + " gefunden");}	

	updateCombos();
	}
	
	private void updateCombos()
	{
	logPrefix = this.getClass().getName() + ".updateCombos(): ";
	
	availableMonth = new TreeMap<>();
	availableYears = new TreeMap<>();
	
		if (maxAZVDate.isBefore(maxLoGaDate) && maxAZVDate.isEqual(maxLoGaDate)) 
		{
		if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "SelectOneMenüs werden anhand der AZV Berichtsmonate generiert. ");}			
			for (LocalDate date : azvBerichtsMonate)
			{
				if (maxDate == null)
				{
					maxDate = date;	
				}
				else
				{
					maxDate = date.isAfter(maxDate) ? date : maxDate;	
				}
				if (! availableMonth.containsKey(date.getMonthValue()))
				{
					availableMonth.put(date.getMonthValue(), new SelectItem(date, Datumsformate.DF_MONAT.format(date)));
				}
				if (! availableYears.containsKey(date.getYear()))
				{
					availableYears.putIfAbsent(date.getYear(), new SelectItem(date, Datumsformate.DF_JAHR.format(date)));
				}			
			}
		} 
		else 
		{
		if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "SelectOneMenüs werden anhand der LoGa Berichtsmonate generiert. ");}			
			for (LocalDate date : logaBerichtsMonate)
			{
				if (maxDate == null)
				{
					maxDate = date;	
				}
				else
				{
					maxDate = date.isAfter(maxDate) ? date : maxDate;	
				}
				if (! availableMonth.containsKey(date.getMonthValue()))
				{
					availableMonth.put(date.getMonthValue(), new SelectItem(date, Datumsformate.DF_MONAT.format(date)));
				}
				if (! availableYears.containsKey(date.getYear()))
				{
					availableYears.putIfAbsent(date.getYear(), new SelectItem(date, Datumsformate.DF_JAHR.format(date)));
				}			
			}

		}
	maxMonth = new SelectItem(maxDate, Datumsformate.DF_MONAT.format(maxDate));	
	maxYear  = new SelectItem(maxDate, Datumsformate.DF_JAHR.format(maxDate));	
	}

	public void yearValueChanged(ValueChangeEvent event)
	{
	Object test = event.getNewValue();
	System.out.println("ole");
	}
	public void setSelectedYear(String selectedYear) 
	{
	this.selectedYear = selectedYear;
	selectedDate = LocalDate.of(Integer.parseInt(selectedYear), 9, 1);
	}	

	public ArrayList<SelectItem> getAvailableMonth() {return availableMonth.values().stream().filter(si -> si != null).collect(Collectors.toCollection(ArrayList::new));}
	public ArrayList<SelectItem> getAvailableYears() {return availableYears.descendingMap().values().stream().filter(si -> si != null).collect(Collectors.toCollection(ArrayList::new));}
	public String getSelectedMonth() {return selectedMonth;}
	public void setSelectedMonth(String selectedMonth) {this.selectedMonth = selectedMonth;	}
	public String getSelectedYear() {return selectedYear;}
	public SelectItem getMaxMonth() {return maxMonth;}
	public SelectItem getMaxYear() {return maxYear;}
	public LocalDate getMaxDate() {return maxDate;}
	public MSSQLDataService getDataService() {return dataService;}
	public void setDataService(MSSQLDataService dataService) {this.dataService = dataService;}	

}
