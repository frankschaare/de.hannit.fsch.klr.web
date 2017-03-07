package de.hannit.fsch.klr.web.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import de.hannit.fsch.klr.dataservice.mssql.MSSQLDataService;
import de.hannit.fsch.klr.model.Datumsformate;
import de.hannit.fsch.util.DateUtility;

@ManagedBean
@RequestScoped
public class IndexSelectOneController implements Serializable 
{
private static final long serialVersionUID = 1L;
@ManagedProperty (value = "#{dataService}")
private MSSQLDataService dataService;	
private FacesContext fc = null;
private TreeMap<Integer, SelectItem> availableMonth = null;
private TreeMap<Integer, SelectItem> availableYears = null;
private SelectItem maxMonth = null;
private SelectItem maxYear = null;
private String selectedMonth = null;
private String selectedYear = null;
private LocalDate auswertungsMonat = null;
private LocalDate maxDate = null;	
	
private boolean buttonForwardDisabled = true;
private boolean buttonBackDisabled = false;
private String buttonForwardToolTip = "Momentan nicht verfügbar, da bereits die aktuellsten Daten geladen wurden";
private String buttonBackToolTip = null;

	public IndexSelectOneController() 
	{
	fc = FacesContext.getCurrentInstance();
	dataService = dataService != null ? dataService : fc.getApplication().evaluateExpressionGet(fc, "#{dataService}", MSSQLDataService.class);
	setMaxDate(dataService.getMaxLoGaDate());
	
	updateCombos();
	}
	
	private void updateCombos()
	{
	auswertungsMonat = maxDate;
	setSelectedMonth(Datumsformate.DF_MONAT.format(auswertungsMonat));
	setSelectedYear(Datumsformate.DF_JAHR.format(auswertungsMonat));
	buttonBackToolTip = "Lädt Daten für den Auswertungsmonat " + DateUtility.DF_MONATJAHR.format(auswertungsMonat.minusMonths(1)) + " aus der Datenbank";
	}

	public boolean getButtonForwardDisabled() {
		return buttonForwardDisabled;
	}

	public void setButtonForwardDisabled(boolean buttonForwardDisabled) {
		this.buttonForwardDisabled = buttonForwardDisabled;
	}

	public boolean getButtonBackDisabled() {
		return buttonBackDisabled;
	}

	public void setButtonBackDisabled(boolean buttonBackDisabled) {
		this.buttonBackDisabled = buttonBackDisabled;
	}

	public void monthValueChanged(ValueChangeEvent event)
	{
	System.out.println("monthValueChanged invoked");
	}
	
	public void yearValueChanged(ValueChangeEvent event)
	{
	System.out.println("yearValueChanged invoked");
	}
	public void setSelectedYear(String selectedYear) 
	{
	this.selectedYear = selectedYear;	
	System.out.println("setSelectedYear(String selectedYear) invoked");	
	}	

	public ArrayList<SelectItem> getAvailableMonth() {return availableMonth.values().stream().filter(si -> si != null).collect(Collectors.toCollection(ArrayList::new));}
	public ArrayList<SelectItem> getAvailableYears() {return availableYears.descendingMap().values().stream().filter(si -> si != null).collect(Collectors.toCollection(ArrayList::new));}
	public String getSelectedMonth() {return selectedMonth;}
	public void setSelectedMonth(String selectedMonth) 
	{
	this.selectedMonth = selectedMonth;	
	System.out.println("setSelectedMonth(String selectedMonth) invoked");
	}
	
	public String getButtonForwardToolTip() {return buttonForwardToolTip;}
	public String getButtonBackToolTip() {return buttonBackToolTip;}
	public String getSelectedYear() {return selectedYear;}
	public SelectItem getMaxMonth() {return maxMonth;}
	public SelectItem getMaxYear() {return maxYear;}
	public LocalDate getMaxDate() {return maxDate;}
	public MSSQLDataService getDataService() {return dataService;}
	public void setDataService(MSSQLDataService dataService) {this.dataService = dataService;}	
	public LocalDate getAuswertungsMonat() {return auswertungsMonat;}
	public void setAuswertungsMonat(LocalDate auswertungsMonat) 
	{
	this.auswertungsMonat = auswertungsMonat;
	
		if (auswertungsMonat.isBefore(maxDate)) 
		{
		buttonForwardDisabled = false;
		buttonBackToolTip = "Lädt Daten für den Auswertungsmonat " + DateUtility.DF_MONATJAHR.format(auswertungsMonat.minusMonths(1)) + " aus der Datenbank";
		buttonForwardToolTip = "Lädt Daten für den Auswertungsmonat " + DateUtility.DF_MONATJAHR.format(auswertungsMonat.plusMonths(1)) + " aus der Datenbank";
		} 
		else 
		{
		buttonForwardDisabled = true;
		buttonBackToolTip = "Lädt Daten für den Auswertungsmonat " + DateUtility.DF_MONATJAHR.format(auswertungsMonat.minusMonths(1)) + " aus der Datenbank";
		buttonForwardToolTip = "Momentan nicht verfügbar, da bereits die aktuellsten Daten geladen wurden";
		}
	setSelectedMonth(Datumsformate.DF_MONAT.format(auswertungsMonat));
	setSelectedYear(DateUtility.DF_JAHR.format(auswertungsMonat));
	}

	public void setMaxDate(LocalDate maxDate) {this.maxDate = maxDate;}
	
}
