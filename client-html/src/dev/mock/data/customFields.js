import { generateArraysOfRecords } from "../mockUtils";
export function mockCustomFields() {
    this.saveCustomFields = items => {
        this.customFields = items;
    };
    this.removeCustomField = id => {
        this.customFields = this.customFields.filter(it => it.id !== id);
    };
    this.getCustomFields = (search) => {
        const defaultValue = generateArraysOfRecords(20, [
            { name: "id", type: "number" },
            { name: "name", type: "string" }
        ]).map(l => `${l.id} = ${l.name}`).join("; ");
        const rows = generateArraysOfRecords(20, [
            { name: "id", type: "number" },
            { name: "fieldKey", type: "string" },
            { name: "name", type: "string" },
            { name: "defaultValue", type: "string" },
            { name: "mandatory", type: "boolean" }
        ]).map(l => ({
            id: l.id,
            values: [l.fieldKey.replace(' ', '_'), l.name, defaultValue, false]
        }));
        const columns = [];
        const response = { rows, columns };
        response.entity = "CustomFieldType";
        response.offset = 0;
        response.filterColumnWidth = null;
        response.layout = null;
        response.pageSize = 0;
        response.search = null;
        response.count = rows.length;
        response.sort = [];
        return response;
    };
    return [
        {
            id: "886543",
            name: "Seniors card",
            defaultValue: "Default value",
            fieldKey: "886543",
            mandatory: true,
            entityType: "Application"
        },
        {
            id: "5684452",
            name: "Student ",
            defaultValue: "",
            fieldKey: "5684452",
            mandatory: true,
            entityType: "WaitingList"
        },
        {
            id: "32435",
            name: "Pensioner",
            defaultValue: "Enrolment",
            fieldKey: "32435",
            mandatory: true,
            entityType: "Enrolment"
        }
    ];
}
//# sourceMappingURL=customFields.js.map