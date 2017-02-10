package com.ea.eadp;

import com.ea.eadp.xml.XmlHelper;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.List;
import java.util.function.BiFunction;


public abstract class PomXmlUtilBase {
    private static Logger logger = Logger.getLogger(PomXmlUtilBase.class);

    protected PomXmlUtilBase() throws ParserConfigurationException {
    }

    static boolean changeInAllFiles(List<String> files, BiFunction<Document, String, Boolean> xmlHandler)
            throws IOException, ParserConfigurationException, SAXException {
        if (files == null || files.isEmpty()) throw new NullPointerException("files");
        if (xmlHandler == null) throw new NullPointerException("xmlHandler");
        for (String file : files) {
            logger.info(String.format("checking %s ...", file));
            Document doc = XmlHelper.loadXml(file);
            if (!xmlHandler.apply(doc, file)) {
                logger.error("Operation failed");
                return false;
            }
        }
        return true;
    }


    static void writeXml(Document doc, OutputStreamWriter output) throws IOException, TransformerException, DocumentException {
        XmlHelper.writeXml(doc, output);
    }
}
