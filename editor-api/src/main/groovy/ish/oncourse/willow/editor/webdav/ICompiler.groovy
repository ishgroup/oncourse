package ish.oncourse.willow.editor.webdav

interface ICompiler {
    
    void compile()
    File getResult()
    File getGzResult()
    List<String> getErrors()
    ErrorEmailTemplate getErrorEmailTemplate()
}