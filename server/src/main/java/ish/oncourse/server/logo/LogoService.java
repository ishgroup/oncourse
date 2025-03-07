/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.logo;

import io.bootique.annotation.BQConfigProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static ish.persistence.Preferences.*;

public class LogoService {

    private static final Logger logger = LogManager.getLogger();

    private String custom_logo_black = null;
    private String custom_logo_black_small = null;
    private String custom_logo_white = null;
    private String custom_logo_white_small = null;
    private String custom_logo_colour = null;
    private String custom_logo_colour_small = null;

    @BQConfigProperty
    public void setCustom_logo_black(String custom_logo_black) {
        this.custom_logo_black = custom_logo_black;
    }

    @BQConfigProperty
    public void setCustom_logo_black_small(String custom_logo_black_small) {
        this.custom_logo_black_small = custom_logo_black_small;
    }

    @BQConfigProperty
    public void setCustom_logo_white(String custom_logo_white) {
        this.custom_logo_white = custom_logo_white;
    }

    @BQConfigProperty
    public void setCustom_logo_white_small(String custom_logo_white_small) {
        this.custom_logo_white_small = custom_logo_white_small;
    }

    @BQConfigProperty
    public void setCustom_logo_colour(String custom_logo_colour) {
        this.custom_logo_colour = custom_logo_colour;
    }

    @BQConfigProperty
    public void setCustom_logo_colour_small(String custom_logo_colour_small) {
        this.custom_logo_colour_small = custom_logo_colour_small;
    }

    public Map<String, String> getLogoProperties() {
        Map<String, String> logoMap = new HashMap<>();
        logoMap.put(CUSTOM_LOGO_BLACK, custom_logo_black);
        logoMap.put(CUSTOM_LOGO_BLACK_SMALL, custom_logo_black_small);
        logoMap.put(CUSTOM_LOGO_WHITE, custom_logo_white);
        logoMap.put(CUSTOM_LOGO_WHITE_SMALL, custom_logo_white_small);
        logoMap.put(CUSTOM_LOGO_COLOUR, custom_logo_colour);
        logoMap.put(CUSTOM_LOGO_COLOUR_SMALL, custom_logo_colour_small);
        return logoMap;
    }

}
