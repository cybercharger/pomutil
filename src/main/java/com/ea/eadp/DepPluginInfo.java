package com.ea.eadp;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Node;
import org.dom4j.tree.DefaultElement;

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

    protected static String getSubElementValue(Node node, String s) {
        DefaultElement e = (DefaultElement) node;
        if (e == null) return "";
        return e.element(s) == null ? "" : e.element(s).getStringValue();
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
