package ish.oncourse.solr.query;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class Count {
    private String id;
    private String path;
    private int counter;

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public long getCounter() {
        return counter;
    }

    public static Count valueOf(String id, String path, Integer counter) {
        Count count = new Count();
        count.id = id;
        count.path = path;
        count.counter = counter != null ? counter : 0;
        return count;
    }
}
