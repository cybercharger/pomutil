package com.ea.eadp;


import com.ea.eadp.xml.XmlHelper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class AppTest {
    private static Logger logger = Logger.getLogger(AppTest.class);


    @Test
    public void test() throws IOException {
        Map<String, String> fileMap = new HashMap<String, String>() {{
            put("xmlNamespace/pom.xml", "pomWithNS.xml");
            put("xmlNoNamespace/pom.xml", "pomWithoutNS.xml");
        }};

        Map<String, String> expectedMap = new HashMap<String, String>() {{
            put("xmlNamespace/pom.xml", "xmlNamespace/expected.xml");
            put("xmlNoNamespace/pom.xml", "xmlNoNamespace/expected.xml");
        }};

        try {
            for (Map.Entry<String, String> e : fileMap.entrySet()) {
                logger.info(String.format("handling %1$s...", e.getKey()));
                InputStream xmlFile = AppTest.class.getClassLoader().getResourceAsStream(e.getKey());
                byte[] content = IOUtils.toByteArray(xmlFile);
                Files.write(Paths.get(".", e.getValue()), content, StandardOpenOption.CREATE);
                Document doc = XmlHelper.loadXml(e.getValue());
                boolean replaced = PomVersionUtil.replaceVersion(doc, "1000.0.0-Java8-SNAPSHOT", "450.0.0-SNAPSHOT", d -> {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    OutputStreamWriter writer = new OutputStreamWriter(baos);
                    try {
                        PomVersionUtil.writeXml(doc, writer);
                        String newXml = new String(baos.toByteArray(), "UTF-8");
                        InputStream exp = AppTest.class.getClassLoader().getResourceAsStream(expectedMap.get(e.getKey()));
                        byte[] expBytes = IOUtils.toByteArray(exp);
                        String expected = new String(expBytes, "UTF-8");
                        Assert.assertEquals(expected, newXml);
                    } catch (IOException | TransformerException | DocumentException e1) {
                        logger.error(e1);
                    }
                });
                if (!replaced) throw new IllegalStateException("Not Replaced");
            }
        } catch (ParserConfigurationException | XPathExpressionException | SAXException e) {
            e.printStackTrace();
        } finally {
            for (Map.Entry<String, String> e : fileMap.entrySet()) {
                Files.deleteIfExists(Paths.get(".", e.getValue()));
                Files.deleteIfExists(Paths.get(".", e.getValue() + ".new"));
            }
        }
    }

    @Test
    public void testDepPlugin() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        final String[] expectedDeps = new String[]{
                "junit:junit:4.12:test",
                "dom4j:dom4j:1.6.1:",
                "jaxen:jaxen:1.1.1:",
                "log4j:log4j:1.2.17:",
                "commons-lang3:org.apache.commons:3.4:",
                "commons-io:commons-io:2.4:test",
                "commons-cli:commons-cli:1.3.1:"
        };
        final String[] expectedPlugins = new String[]{
                "maven-compiler-plugin:org.apache.maven.plugins:3.1",
                "maven-assembly-plugin:org.apache.maven.plugins:2.3"
        };
        InputStream is = new ByteArrayInputStream(pomString.getBytes(StandardCharsets.UTF_8));
        Document doc = XmlHelper.loadXml(is);
        List<Element> deps = PomDepPluginUtil.getAllDependencies(doc);
        String[] actualDeps = deps.stream()
                .map(e -> DependencyInfo.fromXml(e).toString())
                .collect(Collectors.toList())
                .toArray(new String[deps.size()]);

        Assert.assertArrayEquals(expectedDeps, actualDeps);

        List<Element> plugins = PomDepPluginUtil.getAllPlugins(doc);
        String[] actualPlugins = plugins.stream()
                .map(e -> PluginInfo.fromXml(e).toString())
                .collect(Collectors.toList())
                .toArray(new String[plugins.size()]);

        Assert.assertArrayEquals(expectedPlugins, actualPlugins);
    }

    public static final String pomString = "" +
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
            "    <modelVersion>4.0.0</modelVersion>\n" +
            "\n" +
            "    <groupId>com.ea.eadp</groupId>\n" +
            "    <artifactId>pom.util</artifactId>\n" +
            "    <version>1.0-SNAPSHOT</version>\n" +
            "    <packaging>jar</packaging>\n" +
            "\n" +
            "    <name>pom.util</name>\n" +
            "    <url>http://maven.apache.org</url>\n" +
            "\n" +
            "    <properties>\n" +
            "        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" +
            "        <java.version>1.8</java.version>\n" +
            "    </properties>\n" +
            "\n" +
            "    <dependencies>\n" +
            "        <dependency>\n" +
            "            <groupId>junit</groupId>\n" +
            "            <artifactId>junit</artifactId>\n" +
            "            <version>4.12</version>\n" +
            "            <scope>test</scope>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>dom4j</groupId>\n" +
            "            <artifactId>dom4j</artifactId>\n" +
            "            <version>1.6.1</version>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>jaxen</groupId>\n" +
            "            <artifactId>jaxen</artifactId>\n" +
            "            <version>1.1.1</version>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>log4j</groupId>\n" +
            "            <artifactId>log4j</artifactId>\n" +
            "            <version>1.2.17</version>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>org.apache.commons</groupId>\n" +
            "            <artifactId>commons-lang3</artifactId>\n" +
            "            <version>3.4</version>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>commons-io</groupId>\n" +
            "            <artifactId>commons-io</artifactId>\n" +
            "            <version>2.4</version>\n" +
            "            <scope>test</scope>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>commons-cli</groupId>\n" +
            "            <artifactId>commons-cli</artifactId>\n" +
            "            <version>1.3.1</version>\n" +
            "        </dependency>\n" +
            "    </dependencies>\n" +
            "    <build>\n" +
            "        <plugins>\n" +
            "            <plugin>\n" +
            "                <groupId>org.apache.maven.plugins</groupId>\n" +
            "                <artifactId>maven-compiler-plugin</artifactId>\n" +
            "                <version>3.1</version>\n" +
            "                <configuration>\n" +
            "                    <source>${java.version}</source>\n" +
            "                    <target>${java.version}</target>\n" +
            "                </configuration>\n" +
            "            </plugin>\n" +
            "            <plugin>\n" +
            "                <groupId>org.apache.maven.plugins</groupId>\n" +
            "                <artifactId>maven-assembly-plugin</artifactId>\n" +
            "                <version>2.3</version>\n" +
            "                <configuration>\n" +
            "                    <!-- get all project dependencies -->\n" +
            "                    <descriptorRefs>\n" +
            "                        <descriptorRef>jar-with-dependencies</descriptorRef>\n" +
            "                    </descriptorRefs>\n" +
            "                    <!-- MainClass in manifest make a executable jar -->\n" +
            "                    <archive>\n" +
            "                        <manifest>\n" +
            "                            <mainClass>com.ea.eadp.App</mainClass>\n" +
            "                        </manifest>\n" +
            "                    </archive>\n" +
            "                </configuration>\n" +
            "                <executions>\n" +
            "                    <execution>\n" +
            "                        <id>make-assembly</id>\n" +
            "                        <!-- bind to the packaging phase -->\n" +
            "                        <phase>package</phase>\n" +
            "                        <goals>\n" +
            "                            <goal>single</goal>\n" +
            "                        </goals>\n" +
            "                    </execution>\n" +
            "                </executions>\n" +
            "            </plugin>\n" +
            "\n" +
            "        </plugins>\n" +
            "    </build>\n" +
            "\n" +
            "</project>\n";
}
