package de.hannit.fsch.klr.model.csv;

import java.io.Serializable;
import java.time.LocalDate;

public class MonatsZeilen implements Serializable 
{
private static final long serialVersionUID = 1419967990631243551L;
private LocalDate berichtsMonat = null;
private int monatImQuartal = 0;


	public MonatsZeilen() 
	{
	}

	public LocalDate getBerichtsMonat() {return berichtsMonat;}
	public void setBerichtsMonat(LocalDate berichtsMonat) {this.berichtsMonat = berichtsMonat;}
	public int getMonatImQuartal() {return monatImQuartal;}
	public void setMonatImQuartal(int monatImQuartal) {this.monatImQuartal = monatImQuartal;}
	
}
