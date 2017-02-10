package com.ea.eadp;

import com.ea.eadp.xml.XmlHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

class PomVersionUtil extends PomXmlUtilBase {
    private static final String XPathFormat = "//version[.='%1$s']";
    private static Logger logger = Logger.getLogger(PomVersionUtil.class);

    protected PomVersionUtil() throws ParserConfigurationException {
    }

    static void replaceVersionInAllFiles(List<String> files, String oldVersion, String newVersion)
            throws IOException, ParserConfigurationException, SAXException {
        changeInAllFiles(files, (doc, file) -> replaceVersionAndSave(doc, file, oldVersion, newVersion));
    }

    private static boolean replaceVersionAndSave(Document doc, String file, String oldVersion, String newVersion) {
        try {
            return replaceVersion(doc, oldVersion, newVersion, d -> {
                try {
                    PomXmlUtilBase.writeXml(doc, new FileWriter(file));
                } catch (IOException | TransformerException | DocumentException e) {
                    logger.error(String.format("failed to save %1$s", file), e);
                }
            });
        } catch (XPathExpressionException e) {
            logger.error(e);
            return false;
        }
    }

    static boolean replaceVersion(Document doc, String oldVersion, String newVersion, Consumer<Document> handler) throws XPathExpressionException {
        if (doc == null) throw new NullPointerException("doc");
        if (handler == null) throw new NullPointerException("handler");
        if (StringUtils.isBlank(oldVersion)) throw new NullPointerException(oldVersion);
        if (StringUtils.isBlank(newVersion)) throw new NullPointerException(newVersion);
        List<Node> nodes = XmlHelper.xpathSelect(doc, String.format(XPathFormat, oldVersion), Node.ELEMENT_NODE);
        if (nodes == null || nodes.isEmpty()) {
            logger.warn("Cannot find version node: " + oldVersion);
            return false;
        }

        nodes.forEach(n -> n.setTextContent(newVersion));
        handler.accept(doc);
        return true;
    }
}
