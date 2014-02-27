package org.notes.plugin.tf;

import org.apache.maven.plugin.logging.Log;

public interface Outputter {
    Log getLog();

    void output(ParserResult result);
}
