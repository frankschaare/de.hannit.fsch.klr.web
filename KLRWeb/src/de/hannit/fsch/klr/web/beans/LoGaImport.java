package de.hannit.fsch.klr.web.beans;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@ManagedBean
@RequestScoped
public class LoGaImport implements Serializable 
{
private static final long serialVersionUID = -15869998202315851L;
private UploadedFile file = null;

	public LoGaImport() 
	{
		// TODO Auto-generated constructor stub
	}
	
    public void handleFileUpload(FileUploadEvent event) 
    {
    FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
    FacesContext.getCurrentInstance().addMessage(null, message);
    }

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) 
	{
	this.file = file;
	System.out.println("Ole !");
	}	
	
    public void upload() 
    {
	    if(file != null) 
	    {
	    FacesMessage message = new FacesMessage("Succesful", file.getFileName() + " is uploaded.");
	    FacesContext.getCurrentInstance().addMessage(null, message);
	    }
    
        
    }
}
