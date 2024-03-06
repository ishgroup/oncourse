/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.monitoring.util

class ListItem {

    protected String id
    protected String title
    protected String value

    ListItem(String id, String title, String value) {
        this.id = id
        this.title = title
        this.value = value
    }
}

class MonitoringPrinter {

    static String printText(String value) {
        return "<p>${value}</p>"
    }

    static String printListItem(String id, String title, String value) {
        return "<li id=\"${id}\"> ${title}: ${value} </li>"
    }

    static String printList(List<ListItem> listItems) {
        return  "<ul>" + String.join('', listItems.collect {printListItem(it.id, it.title, it.value)}) + "</ul>"
    }
}
