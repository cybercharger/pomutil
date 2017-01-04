package com.ea.eadp;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by chriskang on 12/22/2016.
 */
public class PomVersionManager {
    private static Logger logger = Logger.getLogger(PomVersionManager.class);
    private static final String NameSpace = "pom";
    private static final String _nameSpacePrefix = String.format("%1$s:", NameSpace);
    private static final String XPathFormat = "//%1$sversion[.='%2$s']";

    public static void replaceVersionInAllFiles(List<String> files, String oldVersion, String newVersion)
            throws FileNotFoundException, DocumentException {
        if (files == null || files.isEmpty()) return;
        for (String file : files) {
            logger.info(String.format("checking %s ...", file));
            FileInputStream f = new FileInputStream(file);
            SAXReader reader = new SAXReader();
            Document doc = reader.read(f);
            PomVersionManager.replaceVersion(doc, oldVersion, newVersion, d -> {
                try {
                    writeXml(doc, new FileWriter(file));
                } catch (IOException e1) {
                    logger.error(String.format("failed to save %1$s", file), e1);
                }
            });
        }
    }

    public static boolean replaceVersion(Document doc, String oldVersion, String newVersion, Consumer<Document> handler)
            throws DocumentException, FileNotFoundException {
        if (doc == null) throw new NullPointerException("doc");
        if (handler == null) throw new NullPointerException("handler");
        if (StringUtils.isBlank(oldVersion)) throw new NullPointerException(oldVersion);
        if (StringUtils.isBlank(newVersion)) throw new NullPointerException(newVersion);
        Element root = doc.getRootElement();
        if (root == null) throw new IllegalArgumentException("doc, no root element");
        Namespace ns = root.getNamespace();
        Map<String, String> uris = new HashMap<>();
        boolean hasNS = StringUtils.isNotBlank(ns.getStringValue());
        if (hasNS) {
            uris.put(NameSpace, ns.getStringValue());
        }
        XPath xpath = doc.createXPath(String.format(XPathFormat, hasNS ? _nameSpacePrefix : "", oldVersion));
        xpath.setNamespaceURIs(uris);
        List<Node> nodes = xpath.selectNodes(doc);
        if (nodes == null || nodes.isEmpty()) {
            logger.warn("Cannot find version node: " + oldVersion);
            return false;
        }

        nodes.forEach(n -> n.setText(newVersion));
        handler.accept(doc);
        return true;
    }

    public static void writeXml(Document doc, OutputStreamWriter output) throws IOException {
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
}
