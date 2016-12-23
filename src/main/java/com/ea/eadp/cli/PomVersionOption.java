package com.ea.eadp.cli;

import org.apache.commons.cli.Option;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by chriskang on 12/22/2016.
 */
public class PomVersionOption extends PomUtilOption {
    private static String NEW_VERSION_ARG = "new-version";
    private static String OLD_VERSION_ARG = "old-version";
    private static String FILE_LIST_ARG = "file-list";

    public PomVersionOption(String[] args) {
        super("pom-ver", args);

        super.options.addOption(Option.builder("o")
                .longOpt(OLD_VERSION_ARG)
                .argName(OLD_VERSION_ARG)
                .hasArg()
                .required()
                .desc("old version string, such as 450.0.0-RELEASE")
                .build());

        super.options.addOption(Option.builder("n")
                .longOpt(NEW_VERSION_ARG)
                .argName(NEW_VERSION_ARG)
                .hasArg()
                .required()
                .desc("new version string, such as 1000.0.0-SNAPSHOT")
                .build());

        super.options.addOption(Option.builder("f")
                .longOpt(FILE_LIST_ARG)
                .argName(FILE_LIST_ARG)
                .hasArg()
                .desc("a file listed pom files in which version should be changed, one pom file per line, " +
                        "if this arg is not provided, all pom files in current dir and sub dirs are selected")
                .build());
    }

    public String getOldVersion() {
        return line.hasOption(OLD_VERSION_ARG) ? line.getOptionValue(OLD_VERSION_ARG) : null;
    }

    public String getNewVersion() {
        return line.hasOption(NEW_VERSION_ARG) ? line.getOptionValue(NEW_VERSION_ARG) : null;
    }

    public List<String> getFiles() throws IOException {
        if (line.hasOption(FILE_LIST_ARG)) {
            return Files.readAllLines(Paths.get(line.getOptionValue(FILE_LIST_ARG))).stream()
                    .filter(f -> StringUtils.isNotBlank(f.trim()))
                    .map(String::trim).collect(Collectors.toList());
        } else {
            Path path = Paths.get("").toAbsolutePath();
            final List<String> files = new LinkedList<>();
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if ("pom.xml".equalsIgnoreCase(file.getFileName().toString())) {
                        files.add(file.toAbsolutePath().toString());
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            return files;
        }
    }
}
