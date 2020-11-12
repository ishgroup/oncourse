import { AvetmissExportOutcomeCategory, AvetmissExportOutcomeStatus, AvetmissExportType } from "../../../../build/generated-sources/swagger-js";
import { getRandomInt, generateArraysOfRecords } from "../mockUtils";
const types = Object.keys(AvetmissExportType);
const statuses = Object.keys(AvetmissExportOutcomeStatus);
const categories = Object.keys(AvetmissExportOutcomeCategory);
export function mockAvetmissExportOutcomes() {
    this.getExportOutcomes = () => {
        return [...Array(120)].map((i, n) => ({
            ids: [n],
            type: types[getRandomInt(0, types.length - 1)],
            status: statuses[getRandomInt(0, statuses.length - 1)],
            category: categories[getRandomInt(0, categories.length - 1)]
        }));
    };
    return [];
}
export function mockAvetmissExportSettings() {
    this.getExportSettings = () => {
        return this.exportSettings;
    };
    return {
        flavour: "NCVER (Standard AVETMISS)",
        fee: [
            "Fee for service VET (non-funded)",
            "Queensland",
            "New South Wales",
            "Victoria",
            "Tasmania",
            "Australian Capital Territory",
            "Western Australia",
            "South Australia",
            "Northern Territory",
            "No Australian state defined",
            "Non VET"
        ],
        includeLinkedOutcomes: false,
        fundingContracts: []
    };
}
export function mockAvetmissExport() {
    this.getAvetmissExportPlainList = () => {
        const rows = generateArraysOfRecords(20, [
            { name: "id", type: "number" },
            { name: "name", type: "string" },
        ]).map(l => ({
            id: l.id,
            values: [l.name, l.name]
        }));
        const columns = [];
        const response = { rows, columns };
        response.entity = "FundingSource";
        response.offset = 0;
        response.filterColumnWidth = null;
        response.layout = null;
        response.pageSize = 10;
        response.search = "";
        response.count = rows.length;
        response.sort = [];
        return response;
    };
    this.getAvetmissExportUploads = () => {
        return generateArraysOfRecords(50, [
            { name: "id", type: "number" },
            { name: "created", type: "Datetime" },
            { name: "status", type: "string" },
            { name: "systemUser", type: "string" },
            { name: "outcomesCount", type: "number" },
            { name: "settings", type: "string" },
            { name: "lastSettings", type: "boolean" },
        ]).map(l => ({
            id: l.id,
            created: l.created,
            status: "unknown",
            systemUser: l.systemUser,
            outcomesCount: l.outcomesCount,
            settings: null,
            lastSettings: true,
        }));
    };
}
//# sourceMappingURL=avetmissExport.js.map