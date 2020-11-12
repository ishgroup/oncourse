import { GetListExportPdfTemplate } from "../../common/listExport/GetListExportPdfTemplate.Epic";

describe("Get list export account pdf template epic tests", () => {
  it("GetListExportAccountPdfTemplate should returns correct actions", () => GetListExportPdfTemplate("Account"));
});
