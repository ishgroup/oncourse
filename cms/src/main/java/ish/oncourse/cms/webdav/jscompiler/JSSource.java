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
    private JSSource parent;

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

    public boolean isSameFile(JSSource source)
    {
        return this.file.equals(source.file);
    }

    public static JSSource valueOf(String fileName, File file, JSSource parent)
    {
        JSSource source = new JSSource();
        source.fileName = fileName;
        source.file = file;
        source.sourceFile = SourceFile.fromFile(file);
        source.parent = parent;
        return source;
    }

    public SourceFile getSourceFile() {
        return sourceFile;
    }

    public JSSource getParent() {
        return parent;
    }

    public String getPath()
    {
        StringBuilder builder = new StringBuilder();
        if (parent == null)
            return builder.append(fileName).toString();
        return builder.append(parent.getPath()).append("->").append(fileName).toString();
    }
}
