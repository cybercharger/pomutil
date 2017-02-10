package com.ea.eadp;

import org.w3c.dom.Element;

/**
 * Created by ChrisKang on 2/4/2017.
 */
public class PluginInfo extends DepPluginInfo {
    public PluginInfo(String artifactId, String groupId, String version) {
        super(artifactId, groupId, version);
    }

    public static PluginInfo fromXml(Element element) {
        if (element == null) throw new NullPointerException("element");
        return new PluginInfo(getSubElementValue(element, GROUP_ID),
                getSubElementValue(element, ARTIFACT_ID),
                getSubElementValue(element, VERSION));
    }
}
