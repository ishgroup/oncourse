/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.util;

import java.text.ParseException;

/**
 * extension of @see javax.swing.text.DefaultFormatter. Disables overwrite mode.
 *
 * @author marcin
 */
public class DefaultFormatter extends javax.swing.text.DefaultFormatter {
    private static final long serialVersionUID = 1L;

    public DefaultFormatter() {
        super();
        setValueClass(String.class);
        setOverwriteMode(false);
        setAllowsInvalid(false);
        setCommitsOnValidEdit(true);
    }

    @Override
    public Object stringToValue(String string) throws ParseException {
        return string;
    }

    @Override
    public String valueToString(Object value) {
        if (value == null || value.toString() == null) {
            return "";
        }
        return value.toString().trim();
    }
}