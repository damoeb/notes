package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;

import javax.ejb.Local;
import java.io.File;
import java.io.IOException;
import java.net.URL;

@Local
public interface StreamManager {

    File tryDownloadStream(URL url) throws NotesException, IOException;
}
