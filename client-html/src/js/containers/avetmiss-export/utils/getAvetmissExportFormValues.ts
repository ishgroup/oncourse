import { AvetmissExportFee, AvetmissExportSettings } from "@api/model";
import { format as formatDate } from "date-fns";
import { YYYY_MM_DD_MINUSED } from "../../../common/utils/dates/format";

const Initial = {
  flavour: "NCVER (Standard AVETMISS)",
  fee: Object.keys(AvetmissExportFee).filter(i => i !== "Non VET"),
  outcomesStart: formatDate(new Date(`01/01/${new Date().getFullYear()}`), YYYY_MM_DD_MINUSED),
  outcomesEnd: formatDate(new Date(), YYYY_MM_DD_MINUSED),
  includeLinkedOutcomes: false,
  fundingContracts: []
};

const getAvetmissExportFormValues = (settings: AvetmissExportSettings) => {
  const result = JSON.parse(JSON.stringify(settings || Initial));
  result.dateRange = settings && (settings.outcomesStart || settings.outcomesEnd) ? "Custom date range" : "Commenced outcomes";
  return result;
};

export default getAvetmissExportFormValues;
