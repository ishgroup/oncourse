/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package au.com.ish.docs.handlebars;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;

import java.util.Map;

/**
 * A context wrapper for integrating data and helpers in the {@link Handlebars} template engine.
 *
 * <p>
 *     The `HandlebarsContext` provides way to manage and supply context-specific data, helpers, and configurations
 *     to templates processed by the {@link Handlebars} engine.
 * </p>
 *
 * <h5>Usage:</h5>
 * <pre>
 * {@code
 * HandlebarsContext context = new HandlebarsContext();
 * context.put("key", "value"); // Add data to the context
 * context.registerHelper("helperName", new CustomHelper()); // Register a custom helper
 * Template template = handlebars.compile("templateName");
 * String result = template.apply(context);
 * }
 * </pre>
 */

public class HandlebarsContext extends Context {

    public HandlebarsContext(Map<String, Object> data) {
        super(null);
        this.data = data;
    }

}
