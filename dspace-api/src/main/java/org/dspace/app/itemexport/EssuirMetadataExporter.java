package org.dspace.app.itemexport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
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

    private static ItemExportMetadata constructItemMetadata(Item item) {
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
                .withHandle("Https://essuir.sumdu.edu.ua/" + item.getHandle())
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
        return builder.build();
    }

    private static void export(String fileName) throws SQLException, IOException {
        Context context = new Context(Context.Mode.READ_ONLY);
        CsvSchema csvSchema = CsvSchema.builder()
                .setColumnSeparator(';')
                .addColumn("title")
                .addColumn("handle")
                .addColumn("collection")
                .addColumn("submitterEmail")
                .addColumn("submitterFirstName")
                .addColumn("submitterLastName")
                .addColumn("chairName")
                .addColumn("facultyName")
                .addColumn("dateAvailable")
                .addColumn("type")
                .build();
        CsvMapper csvMapper = new CsvMapper();

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "Cp1251"));
        context.turnOffAuthorisationSystem();
        ContentServiceFactory contentServiceFactory = ContentServiceFactory.getInstance();
        ItemService itemService = contentServiceFactory.getItemService();
        Iterator<Item> items = itemService.findAll(context);
        writer.write("Paper Title;Handle;Collection;Submitter Email;Submitter First Name;Submitter Last Name;Chair Name;Faculty Name;Date Available;Type");
        writer.newLine();
        writer.flush();
        while (items.hasNext()) {
            Item item = items.next();
            String line = csvMapper.writerFor(ItemExportMetadata.class)
                    .with(csvSchema).writeValueAsString(constructItemMetadata(item));
            writer.write(line);
            writer.flush();
            context.uncacheEntity(item);
        }
        writer.close();
        context.restoreAuthSystemState();
        context.complete();
    }

    private static void printHelp(Options options, int exitCode) {
        HelpFormatter myhelp = new HelpFormatter();
        myhelp.printHelp("MetadataExport\n", options);
        System.out.println("\nfull export: metadataexport -f filename");
        System.out.println("partial export: metadataexport -i handle -f filename");
        System.exit(exitCode);
    }
}
