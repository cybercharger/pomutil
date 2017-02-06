package com.ea.eadp;

import org.dom4j.Document;
import org.dom4j.Node;

import java.util.List;

/**
 * Created by ChrisKang on 2/4/2017.
 */
public class PomDepPluginUtil extends PomXmlUtilBase {
    private static final String DEP_XPATH = "/%1$sproject/%1$sdependencies/%1$sdependency";
    private static final String PLUGIN_XPATH = "/%1$sproject/%1$sbuild/%1$splugins/%1$splugin";

    private PomDepPluginUtil() {
    }

    public static List<Node> getAllDependencies(Document doc) {
        return selectNodes(doc, ns -> String.format(DEP_XPATH, ns));
    }

    public static List<Node> getAllPlugins(Document doc) {
        return selectNodes(doc, ns -> String.format(PLUGIN_XPATH, ns));
    }
}
