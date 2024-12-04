/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package au.com.ish.docs.handlebars;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.TagType;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.TypeSafeTemplate;

import java.io.Writer;
import java.util.List;

/**
 * A mock implementation of the {@link Template} interface, used in scenarios where no actual template processing is required.
 *
 * <p>
 *     The `EmptyTemplate` returns default values for all methods and performs no operations
 *     when applying templates or collecting data.
 * </p>
 *
 * <h5>Features:</h5>
 * <ul>
 *   <li>Returns an empty string or default values for all methods.</li>
 *   <li>Implements all required methods of the {@link Template} interface.</li>
 *   <li>Provides a simple constructor for setting a filename if needed.</li>
 * </ul>
 *
 * <h5>Usage:</h5>
 * <pre>
 * {@code
 * Template template = new EmptyTemplate("templateName");
 * String filename = template.filename(); // Retrieves the filename
 * }
 * </pre>
 */

public class EmptyTemplate implements Template {

    private String fileName;

    public EmptyTemplate(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String filename() { return fileName; }

    @Override
    public void apply(Object o, Writer writer) {}

    @Override
    public String apply(Object o) { return ""; }

    @Override
    public void apply(Context context, Writer writer) {}

    @Override
    public String apply(Context context) { return ""; }

    @Override
    public String text() { return ""; }

    @Override
    public String toJavaScript() { return ""; }

    @Override
    public <T, S extends TypeSafeTemplate<T>> S as(Class<S> aClass) { return null; }

    @Override
    public <T> TypeSafeTemplate<T> as() { return null; }

    @Override
    public List<String> collect(TagType... tagTypes) { return List.of(); }

    @Override
    public List<String> collectReferenceParameters() { return List.of(); }

    @Override
    public int[] position() {return new int[0]; }
}
