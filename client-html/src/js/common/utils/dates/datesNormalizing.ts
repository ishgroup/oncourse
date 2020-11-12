import { format as formatDate } from "date-fns";
import { YYYY_MM_DD_MINUSED } from "./format";

export const formatToDateOnly = v => (v ? formatDate(new Date(v), YYYY_MM_DD_MINUSED) : v);
