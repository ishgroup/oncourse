import {
  AvetmissExportOutcome,
  AvetmissExportOutcomeCategory,
  AvetmissExportOutcomeStatus,
  AvetmissExportSettings,
  AvetmissExportType
} from "@api/model";
import { format, subDays } from "date-fns";
import { getRandomInt, generateArraysOfRecords } from "../mockUtils";
import { YYYY_MM_DD_MINUSED } from "../../../js/common/utils/dates/format";

const types = Object.keys(AvetmissExportType) as AvetmissExportType[];
const statuses = Object.keys(AvetmissExportOutcomeStatus) as AvetmissExportOutcomeStatus[];
const categories = Object.keys(AvetmissExportOutcomeCategory) as AvetmissExportOutcomeCategory[];

export function mockAvetmissExportOutcomes(): AvetmissExportOutcome[] {
  this.outcomesID = "0e8eb6ef-5ea9-452c-bc07-396a301bef8a";
  this.getExportOutcomes = () => [...Array(120)].map((i, n) => ({
    ids: [n],
    type: types[getRandomInt(0, types.length - 1)],
    status: statuses[getRandomInt(0, statuses.length - 1)],
    category: categories[getRandomInt(0, categories.length - 1)]
  }));

  return [];
}

export function mockAvetmissExportSettings(): AvetmissExportSettings {
  this.getExportSettings = () => this.exportSettings;

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
    fundingContracts: [],
    outcomesStart: format(subDays(new Date(), 1), YYYY_MM_DD_MINUSED),
    outcomesEnd: format(new Date(), YYYY_MM_DD_MINUSED)
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

    const response = { rows, columns } as any;

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

  this.getAvetmissExportUploads = () => generateArraysOfRecords(50, [
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
}
