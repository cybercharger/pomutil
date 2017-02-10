package com.ea.eadp;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public abstract class DepPluginInfo {
    public static final String SEPARATOR = ":";
    protected static final String GROUP_ID = "groupId";
    protected static final String ARTIFACT_ID = "artifactId";
    protected static final String VERSION = "version";
    private final String artifactId;
    private final String groupId;
    private final String version;

    public DepPluginInfo(String artifactId, String groupId, String version) {
        if (StringUtils.isBlank(artifactId)) throw new NullPointerException("artifactId");
        if (StringUtils.isBlank(groupId)) throw new NullPointerException("groupId");
        this.artifactId = artifactId;
        this.groupId = groupId;
        this.version = version;
    }

    protected static String getSubElementValue(Element element, String name) {
        if (element == null) throw new NullPointerException("element");
        if (StringUtils.isBlank(name)) throw new NullPointerException("name");
        NodeList nl = element.getElementsByTagName(name);
        if (nl == null || nl.getLength() == 0) return "";
        if (nl.getLength() != 1) {
            throw new IllegalStateException("more than one node found");
        }
        return nl.item(0).getTextContent();
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return groupId + SEPARATOR + artifactId + SEPARATOR + version;
    }
}
