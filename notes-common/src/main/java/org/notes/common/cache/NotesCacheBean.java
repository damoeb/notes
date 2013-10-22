package org.notes.common.cache;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.notes.common.exceptions.NotesException;
import org.notes.common.exceptions.NotesStatus;
import org.infinispan.manager.EmbeddedCacheManager;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class NotesCacheBean<T> {

    private static final Logger _log = Logger.getLogger(NotesCacheBean.class);

    @Inject
    private EmbeddedCacheManager container;

    @SuppressWarnings("unchecked")
    public T get(String key, CacheName name) throws NotesException {
        if (name == null) {
            String message = "Cache name is empty";
            _log.error(message);
            throw new NotesException(NotesStatus.CACHE_ERROR, message);
        }
        if (StringUtils.isEmpty(key)) {
            String message = "Cache key is empty";
            _log.error(message);
            throw new NotesException(NotesStatus.CACHE_ERROR, message);
        }
        return (T) container.getCache(name.toString()).get(key);
    }

    @SuppressWarnings("unchecked")
    public List<T> getAll(CacheName name) throws NotesException {

        if (StringUtils.isEmpty(name.toString())) {
            String message = "Cache name is empty";
            _log.error(message);
            throw new NotesException(NotesStatus.CACHE_ERROR, message);
        }
        List<T> userSet = new ArrayList<T>();
        for (Object obj : container.getCache(name.toString()).values()) {
            userSet.add((T) obj);
        }
        return userSet;
    }


    public void put(String key, T obj, CacheName name) throws NotesException {
        if (StringUtils.isEmpty(name.toString())) {
            String message = "Cache name is empty";
            _log.error(message);
            throw new NotesException(NotesStatus.CACHE_ERROR, message);
        }
        if (StringUtils.isEmpty(key)) {
            String message = "Cache key is empty";
            _log.error(message);
            throw new NotesException(NotesStatus.CACHE_ERROR, message);
        }
        container.getCache(name.toString()).put(key, obj);
    }

    public boolean contains(String key, CacheName name) throws NotesException {
        if (StringUtils.isEmpty(name.toString())) {
            String message = "Cache name is empty";
            _log.error(message);
            throw new NotesException(NotesStatus.CACHE_ERROR, message);
        }
        if (StringUtils.isEmpty(key)) {
            String message = "Cache key is empty";
            _log.error(message);
            throw new NotesException(NotesStatus.CACHE_ERROR, message);
        }
        return container.getCache(name.toString()).containsKey(key);
    }
}
