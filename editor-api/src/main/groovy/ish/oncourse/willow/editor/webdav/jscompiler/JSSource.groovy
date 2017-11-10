package ish.oncourse.willow.editor.webdav.jscompiler

import com.google.javascript.jscomp.SourceFile

class JSSource {
    private String fileName
    private File file
    private SourceFile sourceFile
    private JSSource parent

    String getFileName() {
        return fileName
    }

    File getFile() {
        return file
    }

    boolean exists()
    {
        return file.isFile() && file.exists()
    }

    boolean isSameFile(JSSource source)
    {
        return this.file == source.file
    }

    static JSSource valueOf(String fileName, File file, JSSource parent)
    {
        JSSource source = new JSSource()
        source.fileName = fileName
        source.file = file
        source.sourceFile = SourceFile.fromFile(file)
        source.parent = parent
        return source
    }

    SourceFile getSourceFile() {
        return sourceFile
    }

    JSSource getParent() {
        return parent
    }

    String getPath()
    {
        StringBuilder builder = new StringBuilder()
        if (parent == null)
            return builder.append(fileName).toString()
        return builder.append(parent.path).append("->").append(fileName).toString()
    }
}
