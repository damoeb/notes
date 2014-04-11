package org.notes.core.services;

import org.notes.common.exceptions.NotesException;

import javax.ejb.Local;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Local
public interface ProxyService {

    void proxyRequest(HttpServletRequest request, HttpServletResponse response, String url) throws NotesException;
}
