package ish.oncourse.willow.editor.webdav.jscompiler

import com.google.javascript.jscomp.SourceFile
import groovy.transform.CompileStatic

@CompileStatic
class JSSource {
    private String fileName
    private File file
    private SourceFile sourceFile
    private JSSource parent

    void setFileName(String fileName) {
        this.fileName = fileName
    }

    void setFile(File file) {
        this.file = file
    }

    void setSourceFile(SourceFile sourceFile) {
        this.sourceFile = sourceFile
    }

    void setParent(JSSource parent) {
        this.parent = parent
    }

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
        source.sourceFile = SourceFile.fromFile(file.getPath())
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
