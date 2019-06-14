package org.dspace.storage.bitstore;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.content.Item;
import org.dspace.content.ItemIterator;
import org.dspace.core.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

public class FileExporter {
    public static void main(String[] args) throws SQLException, IOException {
        Context context = new Context();
        context.turnOffAuthorisationSystem();
        CommandLine line = null;
        String usage = "org.dspace.storage.bitstore.FileExporter -o <output directory>";

        HelpFormatter formatter = new HelpFormatter();

        Options options = new Options();
        options.addOption(Option.builder("o")
                        .required()
                        .argName("output directory")
                        .hasArg(true)
                        .desc("Output directory for files")
                        .build());


        try {
            line = new PosixParser().parse(options, args);
        } catch (Exception e) {
            formatter.printHelp(usage, e.getMessage(), options, "");
            System.exit(1);
        }


        String outputDirectory = line.getOptionValue("o");
        int totalProcessed = 0;
        ItemIterator all = Item.findAll(context);
        while (all.hasNext()) {
            Item item = all.next();
            Arrays.stream(item.getBundles())
                    .flatMap(bundle -> Arrays.stream(bundle.getBitstreams()))
                    .filter(bitstream -> !bitstream.getFormat().isInternal())
                    .forEach(bitstream -> processBitstream(bitstream, outputDirectory));
            totalProcessed++;
            System.gc();
        }

        System.out.printf("Processed %d files.\n", totalProcessed);
        System.out.println("output directory" + line.getOptionValue("o"));

    }

    private static void processBitstream(Bitstream bitstream, String folder) {
        System.out.printf("Processing file %s with id %d.\n", bitstream.getName(), bitstream.getID());
        File outputFile = null;
        try {
            outputFile = new File(folder + "/" + bitstream.getName());
            FileUtils.copyInputStreamToFile(bitstream.retrieve(), outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (AuthorizeException e) {
            e.printStackTrace();
        }

    }

}
