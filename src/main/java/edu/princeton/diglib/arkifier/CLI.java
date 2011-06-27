/**
 * CLI.java <br/>
 * $LastChangedDate: 2011-04-28 15:04:51 -0400 (Thu, 28 Apr 2011) $ <br/>
 * $Author: jstroop $ <br/>
 * $Rev: 1087 $
 */
package edu.princeton.diglib.arkifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * Command line application for our arkifier.
 * 
 * @author <a href="mailto:jstroop@princeton.edu">Jon Stroop</a>
 * @since Apr 7, 2011
 */
public class CLI {


    private static final String NEWLINE = System.getProperty("line.separator");
    private static final String USAGE = "[options] [dir or file]";
    private static final String HEADER = NEWLINE + "Arkify - Command line " +
    		"utility for minting NOIDS and binding ARKs for PUDL METS.";
    private static final String MINT_DESC = "[optional] mint new NOIDs for " +
    		"any METS that are found without them";
    private static final String BIND_DESC = "[optional] bind ARKs for any " +
    		"newly minted or existng NOIDs to /Objects/{NOID}";
    private static final String SIM_DESC = "[optional] run the utility based " +
    		"on the the options and arguments, logging what what would " +
    		"happen, but do not mint or bind any NOIDs";
    private static final String HELP_DESC = "print help";

    private static String version;

    public static void main(String[] args) {

        Properties props = new Properties();
        try {
            ClassLoader cl = CLI.class.getClassLoader();
            InputStream propsStream = cl.getResourceAsStream("arkifier.properties");
            props.load(propsStream);
            version = props.getProperty("CLI.version");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Options options = initOptions();

        CommandLineParser parser = new PosixParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);

            String[] leftovers = cmd.getArgs();

            if (cmd.hasOption('h') || leftovers.length != 1) {
                if (leftovers.length != 1)
                    System.err.println(NEWLINE + "Please specify one file or directory" + NEWLINE);
                printUsage(options);
                System.exit(0);
            }
            boolean bind = cmd.hasOption("b") ? true : false;
            boolean mint = cmd.hasOption("m") ? true : false;
            boolean simulate = cmd.hasOption("s") ? true : false;

            String dir = leftovers[0];

            Arkifier arkifier = new Arkifier();
            arkifier.setBind(bind);
            arkifier.setMint(mint);
            arkifier.setSimulate(simulate);
            arkifier.run(dir);

        } catch (ParseException e) {
            // TODO: fill out.
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static Options initOptions() {

        Options options = new Options();

        options.addOption("m", "mint", false, MINT_DESC);
        options.addOption("b", "bind", false, BIND_DESC);
        options.addOption("h", "help", false, HELP_DESC);
        options.addOption("s", "simulate", false, SIM_DESC);

        return options;
    }

    private static void printUsage(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setWidth(80);
        helpFormatter.printHelp(USAGE, HEADER, options, "Version " + version + NEWLINE, false);
    }
}
