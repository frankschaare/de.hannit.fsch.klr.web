/**
 * 
 */
package de.hannit.fsch.soa.osecm.azv;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.hannit.fsch.soa.osecm.IAZVClient;
import de.optimalsystems.schemas.osecm.ws.EcmWsMtomSoapService;
import de.optimalsystems.schemas.osecm.ws.OsecmWsFaultException;
import de.optimalsystems.schemas.osecm.ws.OsecmWsPortType;
import de.optimalsystems.schemas.osecm.ws.types.Authentication;
import de.optimalsystems.schemas.osecm.ws.types.Base64AsStringParameter;
import de.optimalsystems.schemas.osecm.ws.types.ExecuteParameter;
import de.optimalsystems.schemas.osecm.ws.types.IntegerParameter;
import de.optimalsystems.schemas.osecm.ws.types.Job;
import de.optimalsystems.schemas.osecm.ws.types.Parameter;
import de.optimalsystems.schemas.osecm.ws.types.StringParameter;

/**
 * @author fsch
 * 
 * Wie funktioniert die Verbindung zum AZV-Webservice ?
 * 
 * In der Application.e4xmiwird die Menüleiste des Haupfensters definiert. Dort gibt es einen Menüeintrag 'Services'.
 * Beim Start der Applikation wird die Menüleiste initialisiert
 * 
 * Der Untereintrag AZV Webservice ist mit dem Command: de.hannit.fsch.rcp.klr.services.azv.open verknüpft.
 * Dieser Command ist mit einem Handler verknüpft, welcher die Klasse 'de.hannit.fsch.rcp.klr.handler.azv.OpenAZVServiceHandler' initialisiert 
 *
 */
public class AZVClient implements IAZVClient
{
private URL urlWebservice = null;	
private	Properties props = null;
private Authentication auth = null;
private Base64AsStringParameter xmlRequest = null;
private EcmWsMtomSoapService service = null;
private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
private Document request = null;
private XPathFactory xpathfactory = XPathFactory.newInstance();
private XPath xpath = xpathfactory.newXPath();
private XPathExpression xpBerichtsMonat = null;
private XPathExpression xpBerichtsJahr = null;
private TransformerFactory tFactory = TransformerFactory.newInstance();
private FacesContext fc = null;
private ExternalContext ec = null;

	/**
	 * 
	 */
	public AZVClient()
	{
	fc = FacesContext.getCurrentInstance();
	ec = fc.getExternalContext();	
	props = new Properties();
	
		try
		{
		InputStream in = ec.getResourceAsStream("META-INF/osecmProperties.xml");
		props.loadFromXML(in);
		}
		catch (IOException e)
		{
		e.printStackTrace();
		}
		
	// Lade IP des Enaio-Webservices aus den Properties	
		try
		{
		urlWebservice = new URL(props.getProperty("url"));
		}
		catch (MalformedURLException e1)
		{
		e1.printStackTrace();
		}
	service = new EcmWsMtomSoapService(urlWebservice);
	
	auth = new Authentication();
	auth.setUser(props.getProperty("user"));
	auth.setPassword(props.getProperty("password"));

		try
		{
		xpBerichtsMonat = xpath.compile(props.getProperty("xPathBerichtsMonat"));
		xpBerichtsJahr = xpath.compile(props.getProperty("xPathBerichtsJahr"));
		}
		catch (XPathExpressionException e)
		{
		e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see de.hannit.fsch.soa.osecm.IAZVClient#getServiceInfo()
	 */
	@Override
	public String getServiceInfo()
	{
	return "Client für den Zugriff auf den OS/ECM Web Service";
	}

	@Override
	public String getServerInfo()
	{
	return props.getProperty("ip");
	}

	/**
	 * Liefert eine Liste mit OS/ECM Dokumenten
	 * @return 
	 */
	@Override
	public Document getResultList()
	{
	Document doc = null;	
	Job job = new Job();
    job.setName(IAZVClient.JOB_GETRESULTLIST);
    
    List<Parameter> parameter = job.getParameter();
    IntegerParameter flags = new IntegerParameter();
	flags.setName("Flags");
	flags.setValue(0);
	parameter.add(flags);
	
    StringParameter encoding = new StringParameter();
    encoding.setName("Encoding");
    encoding.setValue("UTF-8");
    parameter.add(encoding);
    
    // Harte Mann:
    StringParameter RequestType = new StringParameter();
    RequestType.setName("RequestType");
    RequestType.setValue("HOL");
    parameter.add(RequestType);
    
    StringParameter outputFormat = new StringParameter();
    outputFormat.setName("OutputFormat");
    outputFormat.setValue("HOL");
    parameter.add(outputFormat);
    
    parameter.add(xmlRequest);
    
	ExecuteParameter execPara = new ExecuteParameter();
	execPara.setAuthentication(auth);
	execPara.setJob(job);
	
	OsecmWsPortType port = service.getPort(OsecmWsPortType.class);
	
		try
		{
		ExecuteParameter response = port.execute(execPara);
		Base64AsStringParameter list = (Base64AsStringParameter) response.getJob().getParameter().get(1);
		String result = list.getValue();
        
		InputStream is = new ByteArrayInputStream(result.getBytes());
	 	BufferedReader br = new BufferedReader(new InputStreamReader(is));
		InputSource inputSource = new InputSource(br);
		inputSource.setEncoding("UTF-8");
	    
		DocumentBuilder builder = factory.newDocumentBuilder();
        doc = builder.parse(inputSource);
        }
		catch (OsecmWsFaultException e)
		{
		e.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{
		e.printStackTrace();
		}
		catch (SAXException e)
		{
		e.printStackTrace();
		}
		catch (IOException e)
		{
		e.printStackTrace();
		}
	return doc;	
    }

	/*
	 * (non-Javadoc)
	 * @see de.hannit.fsch.soa.osecm.IAZVClient#setAZVRequest()
	 * Liest die Datei request.xml aus, welche den Anfragestring enthält.
	 * 
	 * Dann wird die Webservice Anfrage vorbereitet. Die vorbereitete Anfrage wird dabei in das DOM 
	 * eingelesen. Die Request - Parameter Monat und Jahr müssen verändert werden, wenn sich die Auswahl in der Oberfäche ändert. 
	 */
	@Override
	public Exception setAZVRequest(String berichtsMonat, String berichtsJahr)
	{
	Exception result = null;	
    StringWriter writer = new StringWriter();
    
    	try
		{
		DocumentBuilder requestBuilder = factory.newDocumentBuilder();
		request = requestBuilder.parse(ec.getResourceAsStream("META-INF/request.xml"));
			    
		Node nodeMonat = (Node) xpBerichtsMonat.evaluate(request, XPathConstants.NODE);
		Node nodeJahr = (Node) xpBerichtsJahr.evaluate(request, XPathConstants.NODE);
		nodeMonat.setTextContent(berichtsMonat);
		nodeJahr.setTextContent(berichtsJahr);
		
		Transformer transformer = tFactory.newTransformer();
		DOMSource source = new DOMSource(request);
		StreamResult output = new StreamResult(writer);
	    transformer.transform(source, output);
		}
		catch (ParserConfigurationException | IOException | SAXException | XPathExpressionException | TransformerException pcE)
		{
		result = pcE;	
		pcE.printStackTrace();
		}
    
		xmlRequest = new Base64AsStringParameter();
		xmlRequest.setName("XML");
		xmlRequest.setValue(writer.toString());
			
	return result;
	}
}
