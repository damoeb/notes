package org.notes.core.interfaces;

import org.notes.core.model.Account;
import org.notes.core.model.User;

import javax.ejb.Local;

@Local
public interface UserManager {

    User getUser(Long userId);

    @Deprecated
    long getUserId();

    User createUser(String name, Account account);

}
