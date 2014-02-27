package org.notes.plugin.tf;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.maven.plugin.logging.Log;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class SqlOutputter implements Outputter {

    public SqlOutputter() {

        ConsoleAppender console = new ConsoleAppender(); //create appender
        console.setLayout(new PatternLayout("%d [%p|%c|%C{1}] %m%n"));
        console.setThreshold(Level.INFO);
        console.activateOptions();

        Logger.getRootLogger().addAppender(console);

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

//        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(
//                configuration.getProperties()).buildServiceRegistry();
//        SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
//
//        TermFrequency tf = new TermFrequency("wefwef");
//        sessionFactory.getCurrentSession().persist(tf);

    }

    @Override
    public Log getLog() {
        return null;
    }

    @Override
    public void output(ParserResult result) {

    }

    public static void main(String[] args) {
        new SqlOutputter();
    }

}
