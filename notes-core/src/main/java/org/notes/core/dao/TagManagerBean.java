package org.notes.core.dao;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.interfaces.TagManager;
import org.notes.core.model.Tag;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class TagManagerBean implements TagManager {

    private static final Logger LOGGER = Logger.getLogger(TagManagerBean.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Tag findOrCreate(String name) throws NotesException {
        try {
            if (StringUtils.isBlank(name)) {
                throw new NotesException("name is null");
            }

            Query query = em.createNamedQuery(Tag.QUERY_BY_NAME);
            query.setParameter("NAME", name);
            List results = query.getResultList();
            if (!results.isEmpty()) {
                return (Tag) results.get(0);
            }


            Tag tag = new Tag(name);
            em.persist(tag);
            em.flush();
            em.refresh(tag);

            return tag;

        } catch (NotesException e) {
            throw e;
        } catch (Throwable t) {
            throw new NotesException("create folder", t);
        }
    }

}
