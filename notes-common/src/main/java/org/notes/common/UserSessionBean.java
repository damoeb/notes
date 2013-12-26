package org.notes.common;


import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import java.io.Serializable;

@Stateful
public class UserSessionBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(UserSessionBean.class);

    private String username = "testuser";

//    @Resource
//    private javax.ejb.SessionContext sessionContext;

    public UserSessionBean() {
        //
    }

    @PostConstruct
    public void onCreate() {
        LOGGER.info("create");
        //HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        //LOGGER.info("id "+session.getId());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}