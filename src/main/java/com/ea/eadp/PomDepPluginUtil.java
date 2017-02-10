package com.ea.eadp;

import com.ea.eadp.xml.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.util.List;

/**
 * Created by ChrisKang on 2/4/2017.
 */
public class PomDepPluginUtil extends PomXmlUtilBase {
    private static final String DEP_XPATH = "/project/dependencies/dependency";
    private static final String PLUGIN_XPATH = "/project/build/plugins/plugin";

    protected PomDepPluginUtil() throws ParserConfigurationException {
    }


    public static List<Element> getAllDependencies(Document doc) throws XPathExpressionException {
        return XmlHelper.selectElements(doc, DEP_XPATH);
    }

    public static List<Element> getAllPlugins(Document doc) throws XPathExpressionException {
        return XmlHelper.selectElements(doc, PLUGIN_XPATH);
    }
}
