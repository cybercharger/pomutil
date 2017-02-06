package com.ea.eadp;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

class PomVersionUtil extends PomXmlUtilBase {
    private static final String XPathFormat = "//%1$sversion[.='%2$s']";
    private static Logger logger = Logger.getLogger(PomVersionUtil.class);

    static void replaceVersionInAllFiles(List<String> files, String oldVersion, String newVersion)
            throws FileNotFoundException, DocumentException {
        changeInAllFiles(files, (doc, file) -> replaceVersionAndSave(doc, file, oldVersion, newVersion));
    }

    private static boolean replaceVersionAndSave(Document doc, String file, String oldVersion, String newVersion) {
        return replaceVersion(doc, oldVersion, newVersion, d -> {
            try {
                PomXmlUtilBase.writeXml(doc, new FileWriter(file));
            } catch (IOException e1) {
                logger.error(String.format("failed to save %1$s", file), e1);
            }
        });
    }

    static boolean replaceVersion(Document doc, String oldVersion, String newVersion, Consumer<Document> handler) {
        if (doc == null) throw new NullPointerException("doc");
        if (handler == null) throw new NullPointerException("handler");
        if (StringUtils.isBlank(oldVersion)) throw new NullPointerException(oldVersion);
        if (StringUtils.isBlank(newVersion)) throw new NullPointerException(newVersion);
        List<Node> nodes = PomXmlUtilBase.selectNodes(doc, ns -> String.format(XPathFormat, ns, oldVersion));
        if (nodes == null || nodes.isEmpty()) {
            logger.warn("Cannot find version node: " + oldVersion);
            return false;
        }

        nodes.forEach(n -> n.setText(newVersion));
        handler.accept(doc);
        return true;
    }
}
