package com.ea.eadp;

import com.ea.eadp.cli.PUOperation;
import com.ea.eadp.cli.PomUtilOption;
import com.ea.eadp.cli.PomVersionOption;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class App {
    private static Logger logger = Logger.getLogger(App.class);

    public static void main(String[] args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        App app = new App();
        app.operate(args);
    }

    private static class MethodInfo {
        final Class<? extends PomUtilOption> optionClass;
        final Method method;
        final String description;

        MethodInfo(Class<? extends PomUtilOption> option, Method method, String description) {
            this.optionClass = option;
            this.method = method;
            this.description = description;
        }
    }

    @PUOperation(option = PomVersionOption.class, operationName = "pom-ver", description = "change pom.xml version")
    public void replacePomVersion(PomVersionOption option) throws IOException, DocumentException, ParserConfigurationException, SAXException {
        logger.info(String.format("Change pom version from %1$s to %2$s", option.getOldVersion(), option.getNewVersion()));
        PomVersionUtil.replaceVersionInAllFiles(option.getFiles(), option.getOldVersion(), option.getNewVersion());
    }

    private void operate(String[] args) throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        Map<String, MethodInfo> methodMap = new HashMap<>();
        for (Method m : App.class.getDeclaredMethods()) {
            PUOperation operation = m.getAnnotation(PUOperation.class);
            if (operation == null) continue;
            String operationName = StringUtils.isBlank(operation.operationName()) ? m.getName() : operation.operationName();
            methodMap.put(operationName, new MethodInfo(operation.option(), m, operation.description()));
        }

        if (args.length < 1 || !methodMap.containsKey(args[0])) {
            logError(methodMap);
            return;
        }

        try {
            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
            PomUtilOption option = methodMap.get(args[0]).optionClass.getDeclaredConstructor(String[].class).newInstance(new Object[]{newArgs});
            if (!option.parse()) return;
            methodMap.get(args[0]).method.invoke(this, option);
        } catch (Exception e) {
            logException(e);
        }
    }

    private static void logError(Map<String, MethodInfo> methodInfoMap) {
        logger.error("Please provide operation and proper parameters");
        StringBuilder sb = new StringBuilder();
        methodInfoMap.entrySet().forEach(cur -> sb.append(String.format("%1$s:\t%2$s\n", cur.getKey(), cur.getValue().description)));
        logger.error(String.format("Valid operations are: \n%s\nPlease type <operation> --help for details", sb.toString()));
    }

    private static boolean logException(Exception e) {
        Throwable exp = e;
        if (e instanceof InvocationTargetException) {
            exp = e.getCause() != null ? e.getCause() : e;
            if (exp instanceof PomUtilException) {
                logger.error("Error occurred: " + exp.getMessage());
                logger.debug("Error details: ", e);
            } else {
                logger.error("Error occurred: ", exp);
            }
        }
        return true;
    }}
