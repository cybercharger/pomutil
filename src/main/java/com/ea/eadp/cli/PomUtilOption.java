package com.ea.eadp.cli;

import com.ea.eadp.PomUtilException;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

/**
 * Created by chriskang on 12/22/2016.
 */
public class PomUtilOption {
    protected static final String HELP_ARG = "help";

    private final Options helpOptions = new Options();
    protected final Options options = new Options();
    protected CommandLine line;
    private final String[] args;
    private final String cmd;

    public PomUtilOption(String cmd, String[] args) {
        this.cmd = cmd;
        this.args = args;
        Option helpOption = Option.builder("h").argName(HELP_ARG).longOpt(HELP_ARG).desc("Show this help").required().build();
        helpOptions.addOption(helpOption);
    }


    final public boolean parse() {
        CommandLineParser parser = new DefaultParser();
        try {
            line = parser.parse(helpOptions, args);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(cmd, options);
            return false;
        } catch (ParseException e) {
            try {
                line = parser.parse(options, args, false);
                baseOnParse();
            } catch (ParseException exp) {
                Logger.getLogger(this.getClass()).error("Parsing failed: " + exp.getMessage());
                throw new PomUtilException(exp.getMessage());
            }
        }

        return true;
    }

    private void baseOnParse() throws ParseException {
        onParse();
    }

    protected void onParse() throws ParseException {
    }
}
