/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hannit.fsch.soa.osecm.azv;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.optimalsystems.schemas.osecm.ws.EcmWsMtomSoapService;
import de.optimalsystems.schemas.osecm.ws.OsecmWsPortType;
import de.optimalsystems.schemas.osecm.ws.types.Authentication;
import de.optimalsystems.schemas.osecm.ws.types.Base64AsStringParameter;
import de.optimalsystems.schemas.osecm.ws.types.ExecuteParameter;
import de.optimalsystems.schemas.osecm.ws.types.IntegerParameter;
import de.optimalsystems.schemas.osecm.ws.types.Job;
import de.optimalsystems.schemas.osecm.ws.types.Parameter;
import de.optimalsystems.schemas.osecm.ws.types.StringParameter;


/**
 *
 * @author hkuehl
 */
public class ecmApi {
     public enum ecmFieldOperator{Unknown,Equal,NotEqual,Smaller,Larger,SmallerOrEqual,LargerOrEqual,In,Ex}
    
      static public class ecmCore
    {
        String OsGuid;
        String Name;
        String InternalName;
        String DbName;
        String System_Id;
        
       
        
        public ecmCore(){
            OsGuid="";
            Name="";
            InternalName="";
            DbName="";
            System_Id="";
        }
    }
    
      static public class ecmObject extends ecmCore{
        String Id;
        String Type;
        String CoType;
        String MainType;
        List<ecmField> Fields;
        String Creator;
        String Created;
        String Modifier;
        String Modified;
        String Owner;
        String Links;
        String ForeignId;  
        
        public ecmObject(){
            Id ="";
            Type="";
            CoType="";
            MainType="";
            Fields=new ArrayList<ecmField>();
            Creator ="";
            Created = "";
            Modifier ="";
            Modified ="";
            Owner="";
            Links="";
            ForeignId=""; 
        }
    }
    
     static public class ecmField extends ecmCore{
        boolean IsSearchField;
        String Size;
        String Value;
        String OsType;
        String DataType;
        ecmFieldOperator Operator;
        
        public ecmField(){
            IsSearchField=true;
            Size ="";
            Value ="";
            OsType ="";
            DataType="";
            Operator = ecmFieldOperator.Equal ;
        }
        
        public ecmField(String FieldName,String FieldValue){
            IsSearchField=true;
            Size ="";
            Value =FieldValue;
            OsType ="";
            DataType="";
            Operator = ecmFieldOperator.Equal;
            Name = FieldName;
        }
        
        public ecmField Clone()
        {
            ecmField nField = new ecmField();
            nField.DataType=DataType.toString();
            nField.DbName=DbName.toString();
            nField.InternalName=InternalName.toString();
            nField.IsSearchField=IsSearchField;
            nField.Name=Name.toString();
            nField.Operator=Operator;
            nField.OsGuid=OsGuid.toString();
            nField.OsType=OsType.toString();
            nField.Size=Size.toString();
            nField.System_Id=System_Id.toString();
            nField.Value=Value.toString();
            return nField;
        }
    }
    
    static public class ecmFolder extends ecmObject{
                       
        public ecmFolder(){
          
          }
        
        public ecmFolder Clone()
        {
            ecmFolder nFolder = new ecmFolder();
            
            nFolder.CoType = CoType.toString();
            nFolder.Created = Created.toString();
            nFolder.Creator = Creator.toString();
            nFolder.DbName = DbName.toString();
            nFolder.ForeignId = ForeignId.toString();
            nFolder.Id = Id.toString();
            nFolder.InternalName = InternalName.toString();
            nFolder.Links = Links.toString();
            nFolder.MainType = MainType.toString();
            nFolder.Modified= Modified.toString();
            nFolder.Modifier = Modifier.toString();
            nFolder.Name = Name.toString();
            nFolder.OsGuid = OsGuid.toString();
            nFolder.Owner = Owner.toString();
            nFolder.System_Id = System_Id.toString();
            nFolder.Type = Type.toString();
            
            for (int j = 0; j < Fields.size(); j++)
            {
                ecmField nField = Fields.get(j);
                nFolder.Fields.add(nField.Clone());                    
            }
            return nFolder;
        }
    }
    
    static public class ecmRegister extends ecmObject{
        ecmFolder ParentFolder ;
        ecmRegister ParentRegister ;
        List<ecmRegister> Registers;
        
        public ecmRegister(){
             ParentFolder = new ecmFolder();
             //ParentRegister = new ecmRegister();
            //Registers = new ArrayList<ecmRegister>();
        }
        
        public ecmRegister Clone()
        {
            ecmRegister nReg = new ecmRegister();
            
            nReg.CoType = CoType.toString();
            nReg.Created = Created.toString();
            nReg.Creator = Creator.toString();
            nReg.DbName = DbName.toString();
            nReg.ForeignId = ForeignId.toString();
            nReg.Id = Id.toString();
            nReg.InternalName = InternalName.toString();
            nReg.Links = Links.toString();
            nReg.MainType = MainType.toString();
            nReg.Modified= Modified.toString();
            nReg.Modifier = Modifier.toString();
            nReg.Name = Name.toString();
            nReg.OsGuid = OsGuid.toString();
            nReg.Owner = Owner.toString();
            nReg.System_Id = System_Id.toString();
            nReg.Type = Type.toString();
            
            for (int j = 0; j < Fields.size(); j++)
            {
                ecmField nField = Fields.get(j);
                nReg.Fields.add(nField.Clone());
                                
            }
            
            if (ParentRegister != null)
            {
                nReg.ParentRegister = ParentRegister.Clone();
            }
            
            if ( Registers != null)
            {
                for (int j = 0; j < Registers.size(); j++)
                {
                    ecmRegister nSubReg = Registers.get(j);
                    nReg.Registers.add(nSubReg.Clone());
                }
            }
            
            if (ParentFolder != null)
            {
                nReg.ParentFolder = ParentFolder.Clone();
            }
            
            return nReg;
        }
    }
    
    static public class ecmDocument extends ecmObject{
        ecmFolder ParentFolder;
        ecmRegister ParentRegister;
        String SubType;
        List<String> Files;
        String Modul;
        List<ecmRegister> Registers;
        String ArchiveState;
        String ArchiveStateText;
        String Archivist;
        String ArchiveDate;
        String Locked;
        String LockedText;
        String Version;
        String retentionDate;
        String RetentionPlannedDate;
        
        public ecmDocument(){
            //ParentFolder = new ecmFolder();
            //ParentRegister = new ecmRegister();
            SubType ="";
            Files = new ArrayList<String>();
            Modul = "";
            //Registers = new ArrayList<ecmRegister>();
            ArchiveState = "";
            ArchiveStateText ="";
            Archivist ="";
            ArchiveDate ="";
            Locked ="";
            LockedText ="";
            Version ="";
            retentionDate ="";
            RetentionPlannedDate="";
        }
        
        public ecmDocument Clone()
        {
            ecmDocument nDoc = new ecmDocument();
            
            nDoc.ArchiveDate = ArchiveDate.toString();
            nDoc.ArchiveState = ArchiveState.toString();
            nDoc.ArchiveStateText = ArchiveStateText.toString();
            nDoc.Archivist = Archivist.toString();
            nDoc.CoType = CoType.toString();
            nDoc.Created = Created.toString();
            nDoc.Creator = Creator.toString();
            nDoc.DbName = DbName.toString();
            nDoc.ForeignId = ForeignId.toString();
            nDoc.Id = Id.toString();
            nDoc.InternalName = InternalName.toString();
            nDoc.Links = Links.toString();
            nDoc.Locked = Locked.toString();
            nDoc.LockedText = LockedText.toString();
            nDoc.MainType = MainType.toString();
            nDoc.Modified = Modified.toString();
            nDoc.Modul = Modul.toString();
            nDoc.Name = Name.toString();
            nDoc.OsGuid = OsGuid.toString();
            nDoc.Owner = Owner.toString();
            nDoc.RetentionPlannedDate = RetentionPlannedDate.toString();
            nDoc.Modifier = Modifier.toString();
            nDoc.SubType = SubType.toString();
            nDoc.System_Id = System_Id.toString();
            nDoc.Type = Type.toString();
            nDoc.Version = Version.toString();
            nDoc.retentionDate = retentionDate.toString();
            
            for (int j = 0; j < Fields.size(); j++)
            {
                ecmField nField = Fields.get(j);
                nDoc.Fields.add(nField.Clone());
                                
            }
            
            if (ParentFolder != null)
            {
                nDoc.ParentFolder = ParentFolder.Clone();
            }
            
            if (ParentRegister != null)
            {
                nDoc.ParentRegister = ParentRegister.Clone();
            }
            
            return nDoc;
        }
    }
    
    public List<ecmDocument> Search(String DmsUserName,String DmsPassword,ecmDocument Document)
    {
        List<ecmDocument> DocumentList = new ArrayList<ecmDocument>();
        
        if (Document == null)
        {
         return null;   
        }
        
        if (Document.ParentFolder == null)
        {
         return null;   
        }
        
        String Xml;
        
        //Xml = "<?xml version=\"\"1.0\"\" encoding=\"\"ISO-8859-1\"\" ?>";
        //Xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n";
        Xml = "<DMSQuery status=\"1\" rights=\"1\" baseparams=\"1\" fileinfo=\"1\">\r\n";
        Xml = Xml + "   <Archive name=\"" + Document.ParentFolder.Name + "\">\r\n";
        Xml = Xml + "      <ObjectType name=\"" + Document.Name + "\" type=\"DOCUMENT\">\r\n";
        Xml = Xml + "         <Fields field_schema=\"ALL\"/>\r\n";
        Xml = Xml + "         <Conditions>\r\n";
        
        if ( Document.ParentFolder.Id == null || Document.ParentFolder.Id.equals(""))
        {
           if (Document.ParentFolder.Fields.size() > 0 )
            {
                 Xml = Xml + "            <ConditionObject name=\"" + Document.ParentFolder.Name + "\" type=\"FOLDER\">\r\n";

                for(ecmField folderField: Document.ParentFolder.Fields)
                {
                    Xml = Xml + "               <FieldCondition name=\"" + folderField.Name + "\" operator=\"=\">\r\n";
                    Xml = Xml + "                   <Value>" + folderField.Value + "</Value>\r\n";
                    Xml = Xml + "               </FieldCondition>\r\n";
                }
            }
        }
        else
            {
                Xml = Xml + "            <ConditionObject bname=\"" + Document.ParentFolder.Name + "\" type=\"FOLDER\">\r\n";
                Xml = Xml + "               <FieldCondition dbname=\"id\" operator=\"=\">\r\n";
                Xml = Xml + "                   <Value>" + Document.ParentFolder.Id + "</Value>\r\n";
                Xml = Xml + "               </FieldCondition>\r\n";
            }
        
        if (Document.ParentRegister != null)
        {
            if (Document.ParentRegister.Id.equals("") || Document.ParentRegister.Id == null)
            {
                if (Document.ParentRegister.Id.equalsIgnoreCase(""))
                {
                    if (Document.ParentRegister.Fields.size() > 0)
                    {
                         Xml = Xml + "            <ConditionObject name=\"" + Document.ParentRegister.Name + "\" type=\"REGISTER\">\r\n";

                        for(ecmField regField: Document.ParentRegister.Fields)
                        {
                            Xml = Xml + "               <FieldCondition name=\"" + regField.Name + "\" operator=\"=\">\r\n";
                            Xml = Xml + "                   <Value>" + regField.Value + "</Value>\r\n";
                            Xml = Xml + "               </FieldCondition>\r\n";
                        }
                    } 
                }
                else
                    {
                        Xml = Xml + "            <ConditionObject bname=\"" + Document.ParentRegister.Name + "\" type=\"REGISTER\">\r\n";
                        Xml = Xml + "               <FieldCondition dbname=\"id\" operator=\"=\">\r\n";
                        Xml = Xml + "                   <Value>" + Document.ParentRegister.Id + "</Value>\r\n";
                        Xml = Xml + "               </FieldCondition>\r\n";

                    }
            }
        }  
        
            
        if (Document.Id.equals("") || Document.Id == null)
        {
            if (Document.Fields.size() > 0)
            {
                 Xml = Xml + "            <ConditionObject name=\"" + Document.Name + "\" type=\"DOCUMENT\">\r\n";

                for(ecmField docField: Document.Fields)
                {
                    Xml = Xml + "               <FieldCondition name=\"" + docField.Name + "\" operator=\"=\">\r\n";
                    Xml = Xml + "                   <Value>" + docField.Value + "</Value>\r\n";
                    Xml = Xml + "               </FieldCondition>\r\n";
                }
            }
        }
        else
        {
            Xml = Xml + "            <ConditionObject bname=\"" + Document.Name + "\" type=\"DOCUMENT\">\r\n";
            Xml = Xml + "               <FieldCondition dbname=\"id\" operator=\"=\">\r\n";
            Xml = Xml + "                   <Value>" + Document.Id + "</Value>\r\n";
            Xml = Xml + "               </FieldCondition>\r\n"; 
        }
        
        Xml = Xml + "            </ConditionObject>\r\n";
        
        
       
        Xml = Xml + "         </Conditions>\r\n";
        Xml = Xml + "      </ObjectType>\r\n";
        Xml = Xml + "   </Archive>\r\n";
        Xml = Xml + "</DMSQuery>";
        
        Authentication authentication = new Authentication();
	authentication.setUser(DmsUserName);
	authentication.setPassword(DmsPassword);
        
        IntegerParameter flags = new IntegerParameter();
	flags.setName("Flags");
	flags.setValue(0);
        
        StringParameter OutputFormat = new StringParameter();
        OutputFormat.setName("OutputFormat");
        OutputFormat.setValue("HOL");
        
        StringParameter encoding = new StringParameter();
        encoding.setName("Encoding");
        encoding.setValue("UTF-8");
        
        Base64AsStringParameter XmlData = new Base64AsStringParameter();
        XmlData.setName("XML");
        XmlData.setValue(Xml);
        
        Job job = new Job();
        job.setName("dms.GetResultList");
        
        List<Parameter> parameter = job.getParameter();
        parameter.add(flags);
        parameter.add(encoding);
        parameter.add(OutputFormat);
        parameter.add(XmlData);
        
        ExecuteParameter execPara = new ExecuteParameter();
        execPara.setAuthentication(authentication);
        execPara.setJob(job);
        
        EcmWsMtomSoapService service = new EcmWsMtomSoapService();
        OsecmWsPortType port = service.getPort(OsecmWsPortType.class);
        
        try {
            ExecuteParameter response = port.execute(execPara); 
            Base64AsStringParameter list = (Base64AsStringParameter) response.getJob().getParameter().get(1);

            Document doc = LoadXml(list.getValue());
            
            if (doc != null)
            {
                String xPath = "//DMSContent/Archive";
                NodeList ArchiveNodes = GetNodes(doc,xPath);
                
                ecmFolder RootFolder = Document.ParentFolder;
                
                if (ArchiveNodes != null)
                {
                    
                    if (ArchiveNodes.item(0).getAttributes().getNamedItem("osguid") != null)
                    {
                       RootFolder.OsGuid = ArchiveNodes.item(0).getAttributes().getNamedItem("osguid").getNodeValue();
                    }
                    
                    if (ArchiveNodes.item(0).getAttributes().getNamedItem("id") != null)
                    {
                       RootFolder.Type = ArchiveNodes.item(0).getAttributes().getNamedItem("id").getNodeValue();
                    }
                    if (ArchiveNodes.item(0).getAttributes().getNamedItem("name") != null)
                    {
                       RootFolder.Name = ArchiveNodes.item(0).getAttributes().getNamedItem("name").getNodeValue();
                    }
                }
                
                xPath = "//DMSContent/Archive/ObjectType";
                NodeList BaseNodes = GetNodes(doc,xPath);
                ecmDocument BaseDoc = new ecmDocument();
                
                if (BaseNodes != null)
                {
                    if (BaseNodes.item(0).getAttributes().getNamedItem("osguid") != null)
                    {
                       BaseDoc.OsGuid = BaseNodes.item(0).getAttributes().getNamedItem("osguid").getNodeValue();
                    }
                    if (BaseNodes.item(0).getAttributes().getNamedItem("id") != null)
                    {
                       BaseDoc.Type = BaseNodes.item(0).getAttributes().getNamedItem("id").getNodeValue();
                    }
                    if (BaseNodes.item(0).getAttributes().getNamedItem("name") != null)
                    {
                       BaseDoc.Name = BaseNodes.item(0).getAttributes().getNamedItem("name").getNodeValue();
                    }
                    if (BaseNodes.item(0).getAttributes().getNamedItem("table") != null)
                    {
                       BaseDoc.DbName = BaseNodes.item(0).getAttributes().getNamedItem("table").getNodeValue();
                    }
                    if (BaseNodes.item(0).getAttributes().getNamedItem("modul") != null)
                    {
                       BaseDoc.Modul = BaseNodes.item(0).getAttributes().getNamedItem("modul").getNodeValue();
                    }
                    if (BaseNodes.item(0).getAttributes().getNamedItem("cotype") != null)
                    {
                       BaseDoc.CoType = BaseNodes.item(0).getAttributes().getNamedItem("cotype").getNodeValue();
                    }
                    if (BaseNodes.item(0).getAttributes().getNamedItem("maintype") != null)
                    {
                       BaseDoc.MainType = BaseNodes.item(0).getAttributes().getNamedItem("maintype").getNodeValue();
                    }
                }
                
                
                
                xPath = "//DMSContent/Archive/ObjectType/ObjectList/Object";
                NodeList nodes = GetNodes(doc,xPath);
                
                if (nodes != null)
                {
                    for (int i = 0; i < nodes.getLength(); i++)
                    {
                        //System.out.println(nodes.item(i).getAttributes().getNamedItem("id"));
                        ecmDocument nDoc = BaseDoc.Clone();
                        
                        nDoc.ParentFolder=RootFolder;
                                                         
                        nDoc.Id=nodes.item(i).getAttributes().getNamedItem("id").getNodeValue();
                        
                        xPath = "//DMSContent/Archive/ObjectType/ObjectList/Object[@id='" + nDoc.Id + "']/BaseParams";
                            
                            NodeList BaseParamNodes = GetNodes(doc,xPath);
                            
                            nDoc = GetBaseParamsFromNode(BaseParamNodes.item(0), nDoc);
                            
                            /*if (BaseParamNodes != null)
                            {
                                if(BaseParamNodes.item(0) != null)
                                {
                                  Element elem = (Element)BaseParamNodes.item(0);
                                  
                                  if (elem.getElementsByTagName("Creator") != null)
                                  {
                                      nDoc.Creator = elem.getElementsByTagName("Creator").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("Created") != null)
                                  {
                                      nDoc.Created = elem.getElementsByTagName("Created").item(0).getTextContent();
                                  }
                                  
                                  if (elem.getElementsByTagName("Modifier") != null)
                                  {
                                      nDoc.Modifier = elem.getElementsByTagName("Modifier").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("Modified") != null)
                                  {
                                      nDoc.Modified = elem.getElementsByTagName("Modified").item(0).getTextContent();
                                  }
                                  
                                  if (elem.getElementsByTagName("Owner") != null)
                                  {
                                      nDoc.Owner = elem.getElementsByTagName("Owner").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("Links") != null)
                                  {
                                      nDoc.Links = elem.getElementsByTagName("Links").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("SystemID") != null)
                                  {
                                      nDoc.System_Id = elem.getElementsByTagName("SystemID").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("ForeignID") != null)
                                  {
                                      nDoc.ForeignId = elem.getElementsByTagName("ForeignID").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("ArchiveState") != null)
                                  {
                                      if (elem.getElementsByTagName("ArchiveState").item(0).getAttributes().getNamedItem("value") != null)
                                      {
                                          nDoc.ArchiveState=elem.getElementsByTagName("ArchiveState").item(0).getAttributes().getNamedItem("value").getNodeValue();
                                      }
                                      nDoc.ArchiveStateText = elem.getElementsByTagName("ArchiveState").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("Archivist") != null)
                                  {
                                      nDoc.Archivist = elem.getElementsByTagName("Archivist").item(0).getTextContent();
                                  }
                                  
                                  if (elem.getElementsByTagName("ArchiveDate") != null)
                                  {
                                      nDoc.ArchiveDate = elem.getElementsByTagName("ArchiveDate").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("Locked") != null)
                                  {
                                      if (elem.getElementsByTagName("Locked").item(0).getAttributes().getNamedItem("value") != null)
                                      {
                                          nDoc.Locked=elem.getElementsByTagName("Locked").item(0).getAttributes().getNamedItem("value").getNodeValue();
                                      }
                                              
                                      nDoc.LockedText = elem.getElementsByTagName("Locked").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("Version") != null)
                                  {
                                      nDoc.Version = elem.getElementsByTagName("Version").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("RetentionDate") != null)
                                  {
                                      nDoc.retentionDate = elem.getElementsByTagName("RetentionDate").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("RetentionPlannedDate") != null)
                                  {
                                      nDoc.RetentionPlannedDate = elem.getElementsByTagName("RetentionPlannedDate").item(0).getTextContent();
                                  }
                                 }
                            }
                            */
                            
                            
                        xPath = "//DMSContent/Archive/ObjectType/ObjectList/Object[@id='" + nDoc.Id + "']/Fields/Field";
                        
                        NodeList FieldNodes = GetNodes(doc,xPath);
                        
                        nDoc.Fields = GetFieldsFromNode(FieldNodes);
                        
                        /*for (int j = 0; j < FieldNodes.getLength(); j++)
                        {
                            ecmField nField = new ecmField();
                            if (FieldNodes.item(j).getAttributes().getNamedItem("name") != null)
                            {
                                nField.Name = FieldNodes.item(j).getAttributes().getNamedItem("name").getNodeValue();
                            }
                            
                            if (FieldNodes.item(j).getAttributes().getNamedItem("dbname") != null)
                            {
                                nField.DbName = FieldNodes.item(j).getAttributes().getNamedItem("dbname").getNodeValue();
                            }
                            
                            if ( FieldNodes.item(j).getAttributes().getNamedItem("ostype") != null)
                            {
                                nField.OsType = FieldNodes.item(j).getAttributes().getNamedItem("ostype").getNodeValue();
                            }
                            
                            if (FieldNodes.item(j).getAttributes().getNamedItem("size") != null)
                            {
                                nField.Size = FieldNodes.item(j).getAttributes().getNamedItem("size").getNodeValue();
                            }
                            
                            if (FieldNodes.item(j).getAttributes().getNamedItem("datatype") != null)
                            {
                                nField.DataType = FieldNodes.item(j).getAttributes().getNamedItem("datatype").getNodeValue();
                            }
                            
                            if (FieldNodes.item(j).getAttributes().getNamedItem("osguid") != null)
                            {
                                nField.OsGuid = FieldNodes.item(j).getAttributes().getNamedItem("osguid").getNodeValue();
                            }
                            
                            if (FieldNodes.item(j).getAttributes().getNamedItem("internal_name") != null)
                            {
                                nField.InternalName = FieldNodes.item(j).getAttributes().getNamedItem("internal_name").getNodeValue();
                            }
                            
                            nField.Value = FieldNodes.item(j).getTextContent();
                            nDoc.Fields.add(nField);
                        }
                                */
                       DocumentList.add(nDoc);
                    }

                    
                }
            }
            }

            catch (Exception e) 
            {
                        System.out.println(e.getMessage());
            }
        
            return DocumentList;
   } 
            
       
    public Document LoadXml(String Xml)
    {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            byte[] bytes = Xml.getBytes();
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            
            Document doc = builder.parse(bais);
            return doc;
            
        } catch (Exception e) {
            return null;
        }
        
    }
    
       
    public NodeList GetNodes(Document doc, String xPath)
    {
        try {
            
            XPathFactory xpathfactory = XPathFactory.newInstance();
            XPath xpath = xpathfactory.newXPath();

            XPathExpression expr = xpath.compile(xPath);
            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            NodeList nodes = (NodeList) result;
            return nodes;
        } catch (Exception e) {
            return null;
        }
         
    }
    
    public ecmDocument GetBaseParamsFromNode(Node node, ecmDocument obj)
    {
        if (node != null)
                            {
                                if(node != null)
                                {
                                    
                                  Element elem = (Element)node;
                                  
                                  if (elem.getElementsByTagName("Creator") != null)
                                  {
                                      obj.Creator = elem.getElementsByTagName("Creator").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("Created") != null)
                                  {
                                      obj.Created = elem.getElementsByTagName("Created").item(0).getTextContent();
                                  }
                                  
                                  if (elem.getElementsByTagName("Modifier") != null)
                                  {
                                      obj.Modifier = elem.getElementsByTagName("Modifier").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("Modified") != null)
                                  {
                                      obj.Modified = elem.getElementsByTagName("Modified").item(0).getTextContent();
                                  }
                                  
                                  if (elem.getElementsByTagName("Owner") != null)
                                  {
                                      obj.Owner = elem.getElementsByTagName("Owner").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("Links") != null)
                                  {
                                      obj.Links = elem.getElementsByTagName("Links").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("SystemID") != null)
                                  {
                                      obj.System_Id = elem.getElementsByTagName("SystemID").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("ForeignID") != null)
                                  {
                                      obj.ForeignId = elem.getElementsByTagName("ForeignID").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("ArchiveState") != null)
                                  {
                                      if (elem.getElementsByTagName("ArchiveState").item(0).getAttributes().getNamedItem("value") != null)
                                      {
                                          obj.ArchiveState=elem.getElementsByTagName("ArchiveState").item(0).getAttributes().getNamedItem("value").getNodeValue();
                                      }
                                      obj.ArchiveStateText = elem.getElementsByTagName("ArchiveState").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("Archivist") != null)
                                  {
                                      obj.Archivist = elem.getElementsByTagName("Archivist").item(0).getTextContent();
                                  }
                                  
                                  if (elem.getElementsByTagName("ArchiveDate") != null)
                                  {
                                      obj.ArchiveDate = elem.getElementsByTagName("ArchiveDate").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("Locked") != null)
                                  {
                                      if (elem.getElementsByTagName("Locked").item(0).getAttributes().getNamedItem("value") != null)
                                      {
                                          obj.Locked=elem.getElementsByTagName("Locked").item(0).getAttributes().getNamedItem("value").getNodeValue();
                                      }
                                              
                                      obj.LockedText = elem.getElementsByTagName("Locked").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("Version") != null)
                                  {
                                      obj.Version = elem.getElementsByTagName("Version").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("RetentionDate") != null)
                                  {
                                      obj.retentionDate = elem.getElementsByTagName("RetentionDate").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("RetentionPlannedDate") != null)
                                  {
                                      obj.RetentionPlannedDate = elem.getElementsByTagName("RetentionPlannedDate").item(0).getTextContent();
                                  }
                                 }
                            }
        return obj;
    }
    
    public ecmFolder GetBaseParamsFromNode(Node node, ecmFolder obj)
    {
        if (node != null)
                            {
                                if(node != null)
                                {
                                  Element elem = (Element)node;
                                  
                                  if (elem.getElementsByTagName("Creator") != null)
                                  {
                                      obj.Creator = elem.getElementsByTagName("Creator").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("Created") != null)
                                  {
                                      obj.Created = elem.getElementsByTagName("Created").item(0).getTextContent();
                                  }
                                  
                                  if (elem.getElementsByTagName("Modifier") != null)
                                  {
                                      obj.Modifier = elem.getElementsByTagName("Modifier").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("Modified") != null)
                                  {
                                      obj.Modified = elem.getElementsByTagName("Modified").item(0).getTextContent();
                                  }
                                  
                                  if (elem.getElementsByTagName("Owner") != null)
                                  {
                                      obj.Owner = elem.getElementsByTagName("Owner").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("Links") != null)
                                  {
                                      obj.Links = elem.getElementsByTagName("Links").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("SystemID") != null)
                                  {
                                      obj.System_Id = elem.getElementsByTagName("SystemID").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("ForeignID") != null)
                                  {
                                      obj.ForeignId = elem.getElementsByTagName("ForeignID").item(0).getTextContent();
                                  }                                                                 
                                 }
                            }
        return obj;
    }
    
    public ecmRegister GetBaseParamsFromNode(Node node, ecmRegister obj)
    {
        if (node != null)
                            {
                                if(node != null)
                                {
                                  Element elem = (Element)node;
                                  
                                  if (elem.getElementsByTagName("Creator") != null)
                                  {
                                      obj.Creator = elem.getElementsByTagName("Creator").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("Created") != null)
                                  {
                                      obj.Created = elem.getElementsByTagName("Created").item(0).getTextContent();
                                  }
                                  
                                  if (elem.getElementsByTagName("Modifier") != null)
                                  {
                                      obj.Modifier = elem.getElementsByTagName("Modifier").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("Modified") != null)
                                  {
                                      obj.Modified = elem.getElementsByTagName("Modified").item(0).getTextContent();
                                  }
                                  
                                  if (elem.getElementsByTagName("Owner") != null)
                                  {
                                      obj.Owner = elem.getElementsByTagName("Owner").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("Links") != null)
                                  {
                                      obj.Links = elem.getElementsByTagName("Links").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("SystemID") != null)
                                  {
                                      obj.System_Id = elem.getElementsByTagName("SystemID").item(0).getTextContent();
                                  }
                                  if (elem.getElementsByTagName("ForeignID") != null)
                                  {
                                      obj.ForeignId = elem.getElementsByTagName("ForeignID").item(0).getTextContent();
                                  }                                                                 
                                 }
                            }
        return obj;
    }
    
    public List<ecmField> GetFieldsFromNode(NodeList FieldNodes)
    {
        List<ecmField> returnList = new ArrayList<ecmField>();
        
                        for (int j = 0; j < FieldNodes.getLength(); j++)
                        {
                            ecmField nField = new ecmField();
                            if (FieldNodes.item(j).getAttributes().getNamedItem("name") != null)
                            {
                                nField.Name = FieldNodes.item(j).getAttributes().getNamedItem("name").getNodeValue();
                            }
                            
                            if (FieldNodes.item(j).getAttributes().getNamedItem("dbname") != null)
                            {
                                nField.DbName = FieldNodes.item(j).getAttributes().getNamedItem("dbname").getNodeValue();
                            }
                            
                            if ( FieldNodes.item(j).getAttributes().getNamedItem("ostype") != null)
                            {
                                nField.OsType = FieldNodes.item(j).getAttributes().getNamedItem("ostype").getNodeValue();
                            }
                            
                            if (FieldNodes.item(j).getAttributes().getNamedItem("size") != null)
                            {
                                nField.Size = FieldNodes.item(j).getAttributes().getNamedItem("size").getNodeValue();
                            }
                            
                            if (FieldNodes.item(j).getAttributes().getNamedItem("datatype") != null)
                            {
                                nField.DataType = FieldNodes.item(j).getAttributes().getNamedItem("datatype").getNodeValue();
                            }
                            
                            if (FieldNodes.item(j).getAttributes().getNamedItem("osguid") != null)
                            {
                                nField.OsGuid = FieldNodes.item(j).getAttributes().getNamedItem("osguid").getNodeValue();
                            }
                            
                            if (FieldNodes.item(j).getAttributes().getNamedItem("internal_name") != null)
                            {
                                nField.InternalName = FieldNodes.item(j).getAttributes().getNamedItem("internal_name").getNodeValue();
                            }
                            
                            nField.Value = FieldNodes.item(j).getTextContent();
                            returnList.add(nField);
                        }
         return returnList;
                    
    }
  }
   
      
