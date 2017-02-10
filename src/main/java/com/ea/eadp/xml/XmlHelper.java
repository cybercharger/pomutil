package com.ea.eadp.xml;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;


public class XmlHelper {
    private static Logger logger = Logger.getLogger(XmlHelper.class);

    private static final Properties default_properties = new Properties() {{
        put("encoding", "UTF-8");
        put("omit-xml-declaration", "yes");
        put("standalone", "yes");
        put("indent", "yes");
    }};
    public static final short ANY_NODE = Short.MIN_VALUE;
    private static final XPathFactory xpathFactory = XPathFactory.newInstance();
    private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();
    private static final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

    static {
        dbFactory.setCoalescing(true);
    }


    public static List<Node> fromNodeList(NodeList nl, short nodeType) {
        if (nl == null) return null;
        List<Node> result = new LinkedList<>();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node n = nl.item(i);
            if (nodeType == ANY_NODE || n.getNodeType() == nodeType) {
                result.add(n);
            }
        }
        return result;
    }

    public static Document loadXml(String file) throws IOException, SAXException, ParserConfigurationException {
        return loadXml(new FileInputStream(file));
    }

    public static Document loadXml(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
        if (inputStream == null) throw new NullPointerException("inputStream");
        DocumentBuilder db = dbFactory.newDocumentBuilder();
        return db.parse(inputStream);
    }

    public static List<Element> selectElements(Document document, String xpathString) throws XPathExpressionException {
        return xpathSelect(document, xpathString, Node.ELEMENT_NODE).stream()
                .filter(n -> n instanceof Element)
                .map(n -> (Element) n)
                .collect(Collectors.toList());
    }

    public static List<Node> xpathSelect(Document document, String xpathString, Short nodeType) throws XPathExpressionException {
        if (document == null) throw new NullPointerException("document");
        if (document.getDocumentElement() == null)
            throw new IllegalArgumentException("document.getDocumentElement() == null");
        if (StringUtils.isBlank(xpathString)) throw new NullPointerException("xpathString");
        XPath xpath = xpathFactory.newXPath();
        return fromNodeList((NodeList) xpath.evaluate(xpathString, document.getDocumentElement(), XPathConstants.NODESET), nodeType);
    }

    public static void writeXml(Document document, OutputStreamWriter output) throws TransformerException, DocumentException, IOException {
        if (document == null) throw new NullPointerException("document");
        if (output == null) throw new NullPointerException("output");
        DOMSource source = new DOMSource(document);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        StreamResult result = new StreamResult(ps);
        Transformer t = transformerFactory.newTransformer();
        t.setOutputProperties(default_properties);
        t.transform(source, result);

        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        SAXReader reader = new SAXReader();
        org.dom4j.Document saxDoc = reader.read(is);
        XMLWriter writer = null;
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setSuppressDeclaration(false);
            format.setNewLineAfterDeclaration(false);
            format.setTrimText(true);
            format.setNewLineAfterNTags(0);
            format.setIndentSize(4);
            format.setPadText(false);
            format.setEncoding("UTF-8");
            writer = new XMLWriter(output, format);
            writer.write(saxDoc);
        } finally {
            if (writer != null) writer.close();
        }
    }
}
