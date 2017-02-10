package com.ea.eadp;


import org.w3c.dom.Element;

public class DependencyInfo extends DepPluginInfo {
    private static final String SCOPE = "scope";
    private final String scope;

    public DependencyInfo(String groupId, String artifactId, String version, String scope) {
        super(groupId, artifactId, version);
        this.scope = scope;
    }

    public static DependencyInfo fromXml(Element element) {
        if (element == null) throw new NullPointerException("element");
        return new DependencyInfo(getSubElementValue(element, GROUP_ID),
                getSubElementValue(element, ARTIFACT_ID),
                getSubElementValue(element, VERSION),
                getSubElementValue(element, SCOPE));
    }

    public String getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return super.toString() + SEPARATOR + scope;
    }
}
