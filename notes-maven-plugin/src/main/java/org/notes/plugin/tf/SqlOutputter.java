package org.notes.plugin.tf;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.maven.plugin.logging.Log;
import org.notes.common.domain.TermFrequency;
import org.notes.common.domain.TermFrequencyPropertiesKey;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.Iterator;

public class SqlOutputter implements Outputter {

    private final Log log;
    private final String outputFileName;
    private final Integer minTermOccurrences;

    public SqlOutputter(String outputFileName, Integer minTermOccurrences, Log log) {

        this.outputFileName = outputFileName;
        this.minTermOccurrences = minTermOccurrences;
        this.log = log;

//        ConsoleAppender console = new ConsoleAppender(); //create appender
//        console.setLayout(new PatternLayout("%d [%p|%c|%C{1}] %m%n"));
//        console.setThreshold(Level.OFF);
//        console.activateOptions();
//
//        Logger.getRootLogger().addAppender(console);
        /*
        Configuration configuration = new Configuration();

        configuration
                .setProperty(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");

        configuration.addAnnotatedClass(TermFrequency.class);

//        configuration.configure();

        SchemaExport schemaExport = new SchemaExport(configuration);
//        schemaExport.setDelimiter(";\n");
        schemaExport.setOutputFile("/tmp/ddl-test.sql");
//        schemaExport.setFormat(true);

        schemaExport.create(true, false);
        */


    }

    @Override
    public Log getLog() {
        return log;
    }

    @Override
    public void output(ParserResult result) {

        try (PrintStream fout = new PrintStream(new FileOutputStream("target/" + outputFileName))) {

//            Configuration configuration = new Configuration();
//
//            configuration
//                    .setProperty(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
//
//            configuration.addAnnotatedClass(TermFrequency.class);
//            configuration.addAnnotatedClass(TermFrequencyProperties.class);
//
//            SchemaExport schemaExport = new SchemaExport(configuration);
//            schemaExport.setFormat(true);
//
//            PrintStream out = System.out;
//            System.setOut(fout);
//            schemaExport.create(true, false);
//            System.setOut(out);

            fout.println(String.format("insert into TermFrequencyProperties(property, value) values ('%s', '%s');", TermFrequencyPropertiesKey.DOCUMENT_COUNT, result.getDocumentCount()));
            fout.println(String.format("insert into TermFrequencyProperties(property, value) values ('%s', '%s')", TermFrequencyPropertiesKey.BUILD_DATE, new Date()));


            Iterator<TermFrequency> iterator = result.getTerms().iterator();

            int BULK_SIZE = 100;
            int count = 0;

            while (iterator.hasNext()) {

                TermFrequency t = iterator.next();

                if (NumberUtils.isNumber(t.getTerm())) {
                    continue;
                }

                if (t.getFrequency() < minTermOccurrences) {
                    continue;
                }

                if (count++ % BULK_SIZE == 0) {
                    fout.print("; insert into TermFrequency (frequency, term, original) values ");
                    count = 1;
                } else {
                    fout.print(",\n ");
                }

                fout.print(toSql(t));
            }
            fout.println(";");

        } catch (FileNotFoundException e) {
            getLog().error(e);
        }

    }

    private String toSql(TermFrequency termFrequency) {
        return String.format("(%s, '%s', '%s') ", termFrequency.getFrequency(), termFrequency.getTerm(), termFrequency.getOriginal());
    }
}
