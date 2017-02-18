package de.hannit.fsch.klr.model.mitarbeiter;

import java.util.Date;
import java.util.TreeMap;

public class Tarifgruppen 
{
private TreeMap<String, Tarifgruppe> tarifGruppen = new TreeMap<String, Tarifgruppe>();
private double summeTarifgruppen = 0;
private double summeStellen = 0;
private double summeVollzeitAequivalent = 0;
private int anzahlMitarbeiter = 0;
private Date berichtsMonat = null;

	public Tarifgruppen()
	{
	}
	public TreeMap<String, Tarifgruppe> getTarifGruppen(){return tarifGruppen;}
	public void setTarifGruppen(TreeMap<String, Tarifgruppe> tarifGruppen){this.tarifGruppen = tarifGruppen;}
	
	
	public double getSummeTarifgruppen()
	{
		for (Tarifgruppe t : getTarifGruppen().values())
		{
		summeTarifgruppen = summeTarifgruppen + t.getSummeTarifgruppe();	
		}
	return summeTarifgruppen;
	}
	public double getSummeStellen()
	{
		for (Tarifgruppe t : getTarifGruppen().values())
		{
		summeStellen = summeStellen + t.getSummeStellen();	
		}
	return summeStellen;
	}
	public double getSummeVollzeitAequivalent()
	{
		for (Tarifgruppe t : getTarifGruppen().values())
		{
		summeVollzeitAequivalent = summeVollzeitAequivalent + t.getVollzeitAequivalent();	
		}
	return (summeVollzeitAequivalent * summeStellen);
	}
	
	public int getAnzahlMitarbeiter(){return anzahlMitarbeiter;}
	public void setAnzahlMitarbeiter(int anzahlMitarbeiter)	{this.anzahlMitarbeiter = anzahlMitarbeiter;}
	public Date getBerichtsMonat(){	return berichtsMonat;}
	public void setBerichtsMonat(Date berichtsMonat){this.berichtsMonat = berichtsMonat;}
	
}
