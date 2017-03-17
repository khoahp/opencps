/**
* OpenCPS is the open source Core Public Services software
* Copyright (C) 2016-present OpenCPS community

* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>
*/
package org.opencps.paymentmgt.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

/**
 * @author trungdk
 */
public class MD5 {
    private Logger log = Logger.getLogger(MD5.class.getName());
    private Logger logError = Logger.getLogger("ErrorLog");

    public String getMD5Hash(String value)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        log.info("String input secure : " + value);
        final StringBuilder sbMd5Hash = new StringBuilder();
        final MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(value.getBytes("UTF-8"));

        final byte data[] = m.digest();

        for (byte element : data) {
            sbMd5Hash.append(Character.forDigit((element >> 4) & 0xf, 16));
            sbMd5Hash.append(Character.forDigit(element & 0xf, 16));
        }        
        return sbMd5Hash.toString();
    }
}
