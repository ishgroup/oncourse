/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.webdav.jscompiler;

import com.google.javascript.jscomp.SourceFile;

import java.io.File;

public class JSSource {
    private String fileName;
    private File file;
    private SourceFile sourceFile;

    public String getFileName() {
        return fileName;
    }

    public File getFile() {
        return file;
    }

    public boolean exists()
    {
        return file.isFile() && file.exists();
    }

    public boolean equals(Object o)
    {
        return o instanceof JSSource && (o == this || (((JSSource) o).fileName.equals(this.fileName)));

    }

    public static JSSource valueOf(String fileName, File file)
    {
        JSSource source = new JSSource();
        source.fileName = fileName;
        source.file = file;
        source.sourceFile = SourceFile.fromFile(file);
        return source;
    }

    public SourceFile getSourceFile() {
        return sourceFile;
    }
}
