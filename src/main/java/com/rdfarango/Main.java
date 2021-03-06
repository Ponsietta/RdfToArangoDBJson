package com.rdfarango;

import com.rdfarango.utils.ArangoDbModelDataBuilder;
import com.rdfarango.utils.RdfToDocumentModelBuilder;
import com.rdfarango.utils.RdfToGraphModelBuilder;
import org.apache.commons.cli.*;
import org.apache.jena.query.Dataset;
import org.apache.jena.riot.RDFDataMgr;
import java.util.Iterator;

public class Main {

    public enum ARANGODATAMODEL
    {
        D, G
    }

    public static void main(String[] args) {
        // create the parser
        CommandLineParser parser = new DefaultParser();

        // create the Options
        Options options = new Options();
        options.addOption(Option.builder("f").longOpt("file").hasArg().desc("Path to rdf file").argName("file").required().build());
        options.addOption(Option.builder("m").longOpt("data_model").hasArg().desc("ArangoDB data model into which the RDF data will be transformed; Value must be either 'D' if you want to use the document model, or 'G' if you want to use the graph model").argName("data model").required().build());

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            ARANGODATAMODEL data_model = ARANGODATAMODEL.valueOf(line.getOptionValue("m"));

            System.out.println("Reading RDF file...");
            String fileName = line.getOptionValue("f");

            //to handle triples in different named graphs, we need to use Dataset, not one Model
            //then iterate over and process all the triples in the default model and named graph models in the dataset
            Dataset dataset = RDFDataMgr.loadDataset(fileName);
            Iterator<String> namedGraphs = dataset.listNames();

            System.out.println("Parsing RDF into JSON...");
            ArangoDbModelDataBuilder builder;

            switch (data_model){
                case D:
                    builder = new RdfToDocumentModelBuilder();
                    break;
                case G:
                    builder = new RdfToGraphModelBuilder();
                    break;
                default: throw new RuntimeException("Unsupported ArangoDB data model");
            }

            builder.RDFModelToJson(dataset.getDefaultModel(), null);
            while (namedGraphs.hasNext()) {
                String namedGraph = namedGraphs.next();
                builder.RDFModelToJson(dataset.getNamedModel(namedGraph), namedGraph);
            }

            builder.SaveJsonCollectionsToFiles();
        }
        catch(ParseException exp) {
            System.out.println("Illegal parameter usage: " + exp.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "ant", options );
        }
    }
}
