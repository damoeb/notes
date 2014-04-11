package org.notes.core.util;

import org.apache.commons.lang.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by damoeb on 4/11/14.
 */
public final class CryptUtils {

    public static String hash(String password, String salt) throws NoSuchAlgorithmException {

        if (StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("Password is null");
        }

        if (StringUtils.isEmpty(salt)) {
            throw new IllegalArgumentException("Salt is null");
        }

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(salt.getBytes());
        byte[] bytes = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

}
