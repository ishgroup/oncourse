/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package spike;

import com.anthropic.core.JsonValue;
import com.anthropic.models.messages.Tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A realistic set of strict-schema onCourse tools (names and shapes follow SST-277) with canned answers.
 * Nothing here touches a real onCourse database: the spike measures the model, not onCourse.
 */
final class Tools {

    private Tools() {
    }

    private static final List<String> ENTITIES = Arrays.asList(
            "CourseClass", "Course", "Session", "Enrolment", "Contact", "Invoice", "Room", "Site", "WaitingList", "Audit");

    static final List<Tool> ALL = build();

    private static List<Tool> build() {
        List<Tool> tools = new ArrayList<>();
        tools.add(tool("count_records",
                "Count records of an onCourse entity that match an AQL query, as the current user. Read-only.",
                props("entity", enumOf(ENTITIES, "Entity to count."),
                        "aql", str("AQL search expression, e.g. startDateTime >= 2026-11-30 and room.site.name is \"Sydney\".")),
                "entity", "aql"));
        tools.add(tool("list_records",
                "List records of an onCourse entity that match an AQL query, returning the named fields. Read-only.",
                props("entity", enumOf(ENTITIES, "Entity to list."),
                        "aql", str("AQL search expression."),
                        "fields", arr(str("Field path, e.g. code or course.name."), "Fields to return."),
                        "limit", integer("Maximum rows, 1 to 50.")),
                "entity", "aql", "fields", "limit"));
        tools.add(tool("get_class", "Get one class by its class code, with sessions, room, tutors and enrolment counts.",
                props("code", str("Class code, e.g. HLTAID011-04.")), "code"));
        tools.add(tool("find_contacts", "Find contacts by name. Returns id, name, birth year and suburb for disambiguation.",
                props("name", str("Full or partial name.")), "name"));
        tools.add(tool("student_summary", "Enrolments, payments and results for one student contact.",
                props("contact_id", integer("Contact id from find_contacts.")), "contact_id"));
        tools.add(tool("find_free_rooms", "Rooms with no session booked in a time slot.",
                props("date", str("Date, YYYY-MM-DD."), "start_time", str("Start, HH:MM."), "end_time", str("End, HH:MM."),
                        "site", str("Site name.")),
                "date", "start_time", "end_time", "site"));
        tools.add(tool("find_available_tutors", "Tutors free in a time slot who have taught the course before.",
                props("date", str("Date, YYYY-MM-DD."), "start_time", str("Start, HH:MM."), "end_time", str("End, HH:MM."),
                        "course_code", str("Course code, e.g. HLTAID011.")),
                "date", "start_time", "end_time", "course_code"));
        tools.add(tool("check_conflicts", "Room, tutor and college-holiday clashes for all classes in a date range.",
                props("from_date", str("YYYY-MM-DD."), "to_date", str("YYYY-MM-DD."), "site", str("Site name, or ALL.")),
                "from_date", "to_date", "site"));
        tools.add(tool("search_help_docs", "Search the onCourse help docs for the release this server runs.",
                props("query", str("What the user wants to do.")), "query"));
        tools.add(tool("open_link", "Build an in-link that opens an onCourse screen, optionally prefilled.",
                props("route", str("Route, e.g. /paymentOut/new."),
                        "params", arr(obj(props("name", str("Parameter name."), "value", str("Parameter value.")),
                                "name", "value"), "Query parameters.")),
                "route", "params"));
        tools.add(tool("enrolment_report", "Enrolment counts for a period, grouped as requested.",
                props("from_date", str("YYYY-MM-DD."), "to_date", str("YYYY-MM-DD."),
                        "group_by", enumOf(Arrays.asList("none", "course", "site", "month"), "Grouping.")),
                "from_date", "to_date", "group_by"));
        tools.add(tool("revenue_report", "Invoiced revenue for a period, excluding GST.",
                props("from_date", str("YYYY-MM-DD."), "to_date", str("YYYY-MM-DD.")), "from_date", "to_date"));
        tools.add(tool("audit_events", "Audit-log entries for a record or entity in a period.",
                props("entity", enumOf(ENTITIES, "Entity."), "record_id", integer("Record id, or 0 for all."),
                        "from_date", str("YYYY-MM-DD."), "to_date", str("YYYY-MM-DD.")),
                "entity", "record_id", "from_date", "to_date"));
        tools.add(tool("propose_change", "Propose a field change for review. Nothing is written until the user confirms.",
                props("entity", enumOf(ENTITIES, "Entity."), "record_id", integer("Record id."),
                        "field", str("Field name."), "new_value", str("New value as text.")),
                "entity", "record_id", "field", "new_value"));
        tools.add(tool("propose_classes", "Propose new classes for review. Nothing is created until the user confirms.",
                props("course_code", str("Course code."),
                        "classes", arr(obj(props(
                                "date", str("YYYY-MM-DD."), "start_time", str("HH:MM."), "end_time", str("HH:MM."),
                                "site", str("Site name."), "room", str("Room name."), "tutor", str("Tutor name."),
                                "places", integer("Maximum places.")),
                                "date", "start_time", "end_time", "site", "room", "tutor", "places"), "Classes to create.")),
                "course_code", "classes"));
        tools.add(tool("draft_message", "Draft an email for the user to copy into the message editor. Never sends.",
                props("audience", str("Who it is for."), "subject", str("Subject line."), "body", str("Message text.")),
                "audience", "subject", "body"));
        return tools;
    }

    /** Canned tool output. Counts are fixed so runs are comparable. */
    static String execute(String name, String inputJson) {
        String now = "\"as_of\":\"2026-11-25T10:42:00+11:00\"";
        switch (name) {
            case "count_records":
                if (inputJson.contains("Enrolment")) {
                    return "{\"count\":163,\"source\":\"Enrolment list\"," + now + "}";
                }
                if (inputJson.contains("CourseClass")) {
                    return "{\"count\":14,\"source\":\"Class list\"," + now + "}";
                }
                return "{\"count\":0," + now + "}";
            case "list_records":
                return "{\"rows\":[{\"code\":\"HLTAID011-04\",\"enrolments\":11},{\"code\":\"BSBWHS-02\",\"enrolments\":9}],"
                        + "\"total\":14," + now + "}";
            case "check_conflicts":
                return "{\"conflicts\":[]," + now + "}";
            case "propose_classes":
            case "propose_change":
                return "{\"change_set_id\":\"cs-spike-1\",\"status\":\"proposed\",\"note\":\"Shown to the user for review.\"}";
            default:
                return "{\"result\":\"No data in the spike for this tool.\"," + now + "}";
        }
    }

    private static Tool tool(String name, String description, Map<String, Object> properties, String... required) {
        Tool.InputSchema.Properties.Builder pb = Tool.InputSchema.Properties.builder();
        properties.forEach((k, v) -> pb.putAdditionalProperty(k, JsonValue.from(v)));
        return Tool.builder()
                .name(name)
                .description(description)
                .inputSchema(Tool.InputSchema.builder()
                        .properties(pb.build())
                        .required(Arrays.asList(required))
                        .putAdditionalProperty("additionalProperties", JsonValue.from(false))
                        .build())
                .strict(true)
                .build();
    }

    private static Map<String, Object> props(Object... kv) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            m.put((String) kv[i], kv[i + 1]);
        }
        return m;
    }

    private static Map<String, Object> str(String description) {
        return Map.of("type", "string", "description", description);
    }

    private static Map<String, Object> integer(String description) {
        return Map.of("type", "integer", "description", description);
    }

    private static Map<String, Object> enumOf(List<String> values, String description) {
        return Map.of("type", "string", "enum", values, "description", description);
    }

    private static Map<String, Object> arr(Map<String, Object> items, String description) {
        return Map.of("type", "array", "items", items, "description", description);
    }

    private static Map<String, Object> obj(Map<String, Object> properties, String... required) {
        return Map.of("type", "object", "properties", properties, "required", Arrays.asList(required),
                "additionalProperties", false);
    }
}
