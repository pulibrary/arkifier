package edu.princeton.diglib.arkifier;

import java.io.File;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;

import com.ctc.wstx.stax.WstxInputFactory;

/**
 * Fast class for extracting OBJID values from METS. Keeps us from having to 
 * use the METS API to read only, thus saving us from parsing DOMs unnecessarily. 
 * 
 * @see http://woodstox.codehaus.org/Performance
 * @author <a href="mailto:jstroop@princeton.edu">Jon Stroop</a>
 * @since Friday, May 27 2011
 */
public class ObjidExtractor {
    private static XMLInputFactory2 xmlif;
    private static XMLStreamReader2 xmlr;
    private static boolean init = false;

    public static void main(String[] args) throws XMLStreamException, MissingObjidException {
        ObjidExtractor oe = new ObjidExtractor();
        File file= new File("/home/jstroop/workspace/pudl-testData/mdata/pudl0033/2007/03954.mets");
        System.out.println(oe.getObjid(file));
    }

    public ObjidExtractor() {
        if (init == false) {
            Boolean f = Boolean.FALSE;
            Boolean t = Boolean.TRUE;
            xmlif = (XMLInputFactory2) WstxInputFactory.newInstance();
            xmlif.configureForSpeed();
            xmlif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, f);
            xmlif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, f);
            xmlif.setProperty(XMLInputFactory.IS_VALIDATING, f);
            xmlif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, t);
            xmlif.setProperty(XMLInputFactory.IS_COALESCING, f);
            init = true;
        }
    }

    public String getObjid(File thisFile) throws XMLStreamException, MissingObjidException {
        xmlr = (XMLStreamReader2) xmlif.createXMLStreamReader(thisFile);
        String objid = objidFromMETS(thisFile.getPath());
        xmlr.closeCompletely();
        return objid;
    }

    private static String objidFromMETS(String path) throws XMLStreamException, MissingObjidException {
        xmlr.next();
        String value = null;
        for (int c = 0; c < xmlr.getAttributeCount(); c++) {
            if (xmlr.getAttributeLocalName(c).equals("OBJID")) {
                value = xmlr.getAttributeValue(c);
                break;
            }
        }
        if (value == null) 
            throw new MissingObjidException(path);
        return value;
    }
}
