package org.dspace.app.itemexport;

import org.apache.commons.cli.*;
import org.dspace.content.Item;
import org.dspace.content.MetadataSchema;
import org.dspace.content.MetadataValue;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;

import java.io.*;
import java.sql.SQLException;
import java.util.Iterator;


public class EssuirMetadataExporter {
    protected ItemService itemService;

    protected EssuirMetadataExporter() {
        itemService = ContentServiceFactory.getInstance().getItemService();
    }

    public static void main(String[] args) throws SQLException, IOException {
        CommandLineParser parser = new PosixParser();

        Options options = new Options();
        options.addOption("f", "file", true, "destination where you want file written");

        CommandLine line = null;

        try {
            line = parser.parse(options, args);
        } catch (ParseException pe) {
            System.err.println("Error with commands.");

            System.exit(0);
        }

        if (!line.hasOption('f')) {
            System.err.println("Required parameter -f missing!");
            printHelp(options, 1);
        }

        String filename = line.getOptionValue('f');
        export(filename);
    }

    private static void export(String fileName) throws SQLException, IOException {
        Context context = new Context(Context.Mode.READ_ONLY);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "Cp1251"));
        context.turnOffAuthorisationSystem();
        ContentServiceFactory contentServiceFactory = ContentServiceFactory.getInstance();
        ItemService itemService = contentServiceFactory.getItemService();
        Iterator<Item> items = itemService.findAll(context);
        int cnt = 0;
        while (items.hasNext()) {
            Item item = items.next();
            EPerson submitter = item.getSubmitter();
            String dateAvailable = item.getItemService().getMetadata(item, MetadataSchema.DC_SCHEMA, "date", "available", Item.ANY)
                    .stream()
                    .findFirst()
                    .map(MetadataValue::getValue)
                    .orElse("Unknown date");

            String type = item.getItemService().getMetadata(item, MetadataSchema.DC_SCHEMA, "type", "*", Item.ANY)
                    .stream()
                    .findFirst()
                    .map(MetadataValue::getValue)
                    .orElse("Unknown type");

            ItemExportMetadata.Builder builder = new ItemExportMetadata.Builder()
                    .withTitle(item.getName())
                    .withHandle(item.getHandle())
                    .withDateAvailable(dateAvailable)
                    .withType(type)
                    .withCollection(item.getOwningCollection().getName())
                    .withSubmitterEmail(submitter.getEmail())
                    .withSubmitterFirstName(submitter.getFirstName())
                    .withSubmitterLastName(submitter.getLastName());

            if (submitter.getChair() != null) {
                builder.withChairName(submitter.getChair().getName());
                if (submitter.getChair().getFacultyEntity() != null) {
                    builder.withFacultyName(submitter.getChair().getFacultyEntityName());
                }
            }

            writer.write(builder.build().toString());
            writer.newLine();
            writer.flush();
            context.uncacheEntity(item);
            cnt++;
        }
        writer.close();
        System.out.println(cnt);
        context.restoreAuthSystemState();
        context.complete();
    }

    private static void printHelp(Options options, int exitCode) {
        // print the help message
        HelpFormatter myhelp = new HelpFormatter();
        myhelp.printHelp("MetadataExport\n", options);
        System.out.println("\nfull export: metadataexport -f filename");
        System.out.println("partial export: metadataexport -i handle -f filename");
        System.exit(exitCode);
    }
}
