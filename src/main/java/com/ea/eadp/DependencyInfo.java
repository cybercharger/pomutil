package com.ea.eadp;

import org.dom4j.Node;

public class DependencyInfo extends DepPluginInfo {
    private static final String SCOPE = "scope";
    private final String scope;

    public DependencyInfo(String groupId, String artifactId, String version, String scope) {
        super(groupId, artifactId, version);
        this.scope = scope;
    }

    public static DependencyInfo fromXml(Node node) {
        if (node == null) throw new NullPointerException("node");
        return new DependencyInfo(getSubElementValue(node, GROUP_ID),
                getSubElementValue(node, ARTIFACT_ID),
                getSubElementValue(node, VERSION),
                getSubElementValue(node, SCOPE));
    }

    public String getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return super.toString() + SEPARATOR + scope;
    }
}
