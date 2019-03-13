package org.timeml.tarsqi.utils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


/**
 * Interface to the Apache Commons CLI command line parser.
 */

public class CLI {

	/**
	 * Use Apache Commons CLI to parse the command line
	 *
	 * @param options Defined options
	 * @param args Command line arguments
	 * @return Parsed command line
	 */
    public static CommandLine parse(Options options, String[] args) {

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        org.apache.commons.cli.CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("runPipeline(args)", options);
            System.exit(1);
        }
		return cmd;
    }

	/**
	 * Pretty print the command line options.
	 * @param cmd the org.apache.commons.cli.CommandLine instance to print
	 */
	public static void prettyPrint(CommandLine cmd) {
		System.out.println("Command line options:");
		for (Option option : cmd.getOptions()) {
			System.out.print("  --" + option.getLongOpt());
			if (option.hasArg())
				System.out.print(" " + option.getValue());
			//System.out.print(" (type = " + option.getType() + ")");
			System.out.println();
		}
	}
}
