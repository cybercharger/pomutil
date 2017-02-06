package com.ea.eadp;

import org.dom4j.Node;

/**
 * Created by ChrisKang on 2/4/2017.
 */
public class PluginInfo extends DepPluginInfo {
    public PluginInfo(String artifactId, String groupId, String version) {
        super(artifactId, groupId, version);
    }

    public static PluginInfo fromXml(Node node) {
        if (node == null) throw new NullPointerException("node");
        return new PluginInfo(getSubElementValue(node, GROUP_ID),
                getSubElementValue(node, ARTIFACT_ID),
                getSubElementValue(node, VERSION));
    }
}
