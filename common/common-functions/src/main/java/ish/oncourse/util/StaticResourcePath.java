package ish.oncourse.util;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public enum StaticResourcePath {
    stylesheets("stylesheets"),
    stylesheetsCss("stylesheets/css"),
    stylesheetsSrc("stylesheets/src"),
    js("js");


    private String webDavPath;
    private String fileSystemPath;

    StaticResourcePath(String fileSystemPath) {
        this.fileSystemPath = fileSystemPath;
        this.webDavPath = "/s/" + fileSystemPath + "/";
    }

    public String getWebDavPath() {
        return webDavPath;
    }

    public String getFileSystemPath() {
        return fileSystemPath;
    }

    public static StaticResourcePath getStaticResourcePathBy(String webDavPath) {
        for (int i = 0; i < values().length; i++) {
            StaticResourcePath staticResourcePath = values()[i];
            if (staticResourcePath.webDavPath.equals(webDavPath)) {
                return staticResourcePath;
            }
        }
        return null;
    }
}
