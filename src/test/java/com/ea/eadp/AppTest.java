package com.ea.eadp;


import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SyslogAppender;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest {
    private static Logger logger = Logger.getLogger(AppTest.class);

    @Test
    public void test() throws DocumentException, IOException {
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
                FileInputStream f = new FileInputStream(e.getValue());
                SAXReader reader = new SAXReader();
                Document doc = reader.read(f);
                PomVersionManager.replaceVersion(doc, "1000.0.0-SNAPSHOT", "450.0.0-SNAPSHOT", d -> {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    OutputStreamWriter writer = new OutputStreamWriter(baos);
                    try {
                        PomVersionManager.writeXml(doc, writer);
                        String newXml = new String(baos.toByteArray(), "UTF-8");
                        InputStream exp = AppTest.class.getClassLoader().getResourceAsStream(expectedMap.get(e.getKey()));
                        byte[] expBytes = IOUtils.toByteArray(exp);
                        String expected = new String(expBytes, "UTF-8");
                        Assert.assertEquals(expected, newXml);
                    } catch (IOException e1) {
                        logger.error(e1);
                    }
                });
            }
        } finally {
            for (Map.Entry<String, String> e : fileMap.entrySet()) {
                Files.deleteIfExists(Paths.get(".", e.getValue()));
                Files.deleteIfExists(Paths.get(".", e.getValue() + ".new"));
            }
        }
    }

//    @Test
//    public void TestPomVer() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//        String[] args = new String[]{"pom-ver", "-n", "450.0.0-SNAPSHOT"};
//        App.main(args);
//    }
}
