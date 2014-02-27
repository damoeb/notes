package org.notes.plugin.tf;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.maven.plugin.logging.Log;
import org.notes.common.model.TermFrequency;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Iterator;

public class SqlOutputter implements Outputter {

    private final Log log;
    private final String outputFileName;
    private final Integer minTermOccurrences;

    public SqlOutputter(String outputFileName, Integer minTermOccurrences, Log log) {

        this.outputFileName = outputFileName;
        this.minTermOccurrences = minTermOccurrences;
        this.log = log;

        ConsoleAppender console = new ConsoleAppender(); //create appender
        console.setLayout(new PatternLayout("%d [%p|%c|%C{1}] %m%n"));
        console.setThreshold(Level.OFF);
        console.activateOptions();

        Logger.getRootLogger().addAppender(console);
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

//        Configuration configuration = new Configuration();

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("primary");
        EntityManager em = factory.createEntityManager();

        PrintStream out = System.out;
        try (PrintStream grabber = new PrintStream(new FileOutputStream(outputFileName))) {
            System.setOut(grabber);

            writeTerms(result, em);
            writeMetadata(result, em);


            em.close();

        } catch (FileNotFoundException e) {
            getLog().error(e);
        } finally {
            System.setOut(out);
        }

    }

    private void writeMetadata(ParserResult result, EntityManager em) {
        EntityTransaction ta = em.getTransaction();
        ta.begin();


        ta.commit();
    }

    private void writeTerms(ParserResult result, EntityManager em) {
        Iterator<TermFrequency> iterator = result.getTerms().iterator();

        EntityTransaction ta = em.getTransaction();
        ta.begin();

        int BULK_SIZE = 100;
        int elementsInBulk = 0;

        while (iterator.hasNext()) {

            TermFrequency tf = iterator.next();

            em.persist(tf);

            if (elementsInBulk++ >= BULK_SIZE) {
                ta.commit();
                ta.begin();
            }
        }
        ta.commit();
    }
}
