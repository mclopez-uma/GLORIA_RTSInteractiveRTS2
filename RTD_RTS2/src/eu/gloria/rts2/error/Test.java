package eu.gloria.rts2.error;

import java.io.File;
import java.math.BigInteger;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

public class Test {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//generateXml();
		readXml();
		
	}
	
	private static void readXml()  throws Exception{
		
		File schemaFile = new File("c:\\repositorio\\workspace\\eclipsews\\RTD_RTS2\\xml\\rts2_error_management.xsd");
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(schemaFile);

		JAXBContext context = JAXBContext.newInstance(Rts2ErrorManagement.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setSchema(schema);
		File file = new File("c:\\repositorio\\workspace\\eclipsews\\RTD_RTS2\\config\\BOOTES02_rtd_rts2_error_management.xml");
		Rts2ErrorManagement root = (Rts2ErrorManagement) unmarshaller.unmarshal(file);
		
		int x = 0;
		x++;
	}
	
	private static void generateXml() throws Exception{
		
		Rts2Message msg = new Rts2Message();
		msg.setAction(ActionType.IGNORE);
		msg.setErrorCod(new BigInteger("0"));
		msg.setErrorMsg("hola");
		msg.setMatchType(MatchType.SUBSTRING);
		msg.setPattern("PATTERN");
		
		Context context = new Context();
		context.setName("context01");
		context.getMessage().add(msg);
		
		Rts2ErrorManagement root = new Rts2ErrorManagement();
		root.setDefaultBehaviour(Behaviour.IGNORE_ERRORS);
		root.getContext().add(context);
		
		JAXBContext ctx = JAXBContext.newInstance(Rts2ErrorManagement.class);
		
		File schemaFile = new File("c:\\repositorio\\workspace\\eclipsews\\RTD_RTS2\\xml\\rts2_error_management.xsd");
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(schemaFile);

		File outputFile = new File("c:\\dummy\\jaxb\\rts2_error_output.xml");
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setSchema(schema);
		marshaller.marshal(root, outputFile);
	}

}
