package org.notes.plugin.tf;

import org.apache.maven.plugin.logging.Log;

/**
 * Created with IntelliJ IDEA.
 * User: markus
 * Date: 2/27/14
 * Time: 1:58 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Outputter {
    Log getLog();

    void output(ParserResult result);
}
