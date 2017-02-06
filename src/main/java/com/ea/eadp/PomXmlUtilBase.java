package com.ea.eadp;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;


public abstract class PomXmlUtilBase {
    private static Logger logger = Logger.getLogger(PomXmlUtilBase.class);
    private static final String NameSpace = "pom";
    private static final String PomNsPrefix = String.format("%1$s:", NameSpace);
    private static final String EMPTY_STRING = "";

    static boolean changeInAllFiles(List<String> files, BiFunction<Document, String, Boolean> xmlHandler)
            throws FileNotFoundException, DocumentException {
        if (files == null || files.isEmpty()) throw new NullPointerException("files");
        if (xmlHandler == null) throw new NullPointerException("xmlHandler");
        for (String file : files) {
            logger.info(String.format("checking %s ...", file));
            Document doc = PomXmlUtilBase.loadXmlFile(file);
            if (!xmlHandler.apply(doc, file)) return false;
        }
        return true;
    }

    static Document loadXmlFile(String file) throws FileNotFoundException, DocumentException {
        FileInputStream f = new FileInputStream(file);
        SAXReader reader = new SAXReader();
        return reader.read(f);
    }

    static void writeXml(Document doc, OutputStreamWriter output) throws IOException {
        if (doc == null) throw new NullPointerException("doc");
        if (output == null) throw new NullPointerException("writer");
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
            writer.write(doc);
        } finally {
            if (writer != null) writer.close();
        }
    }

    @SuppressWarnings("unchecked")
    static List<Node> selectNodes(Document doc, Function<String, String> xpathGetter) {
        if (doc == null) throw new NullPointerException("doc");
        if (xpathGetter == null) throw new NullPointerException("xpathGetter");
        Element root = doc.getRootElement();
        if (root == null) throw new IllegalArgumentException("doc, no root element");
        Namespace ns = root.getNamespace();
        Map<String, String> uris = new HashMap<>();
        boolean hasNS = StringUtils.isNotBlank(ns.getStringValue());
        if (hasNS) {
            uris.put(NameSpace, ns.getStringValue());
        }
        String xpathString = xpathGetter.apply(hasNS ? PomNsPrefix : EMPTY_STRING);
        XPath xpath = doc.createXPath(xpathString);
        xpath.setNamespaceURIs(uris);
        return (List<Node>) xpath.selectNodes(doc);
    }
}
