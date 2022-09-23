import { DefaultHttpService } from "../../../../../common/services/HttpService";
import { PdfApi, PdfTemplateApi, PrintRequest, Report } from "@api/model";

class PdfService {
  readonly service = new DefaultHttpService();
  readonly pdfApi = new PdfApi(this.service);
  readonly pdfTemplateApi = new PdfTemplateApi(this.service);

  public doPrint(rootEntity: string, printRequest: PrintRequest): Promise<string> {
    return this.pdfApi.execute(rootEntity, printRequest);
  }

  public getAllPrints(entityName: string): Promise<Report[]> {
    return this.pdfTemplateApi.get(entityName);
  }

  public getReport(id: number): Promise<Report> {
    return this.pdfTemplateApi.getById(id);
  }

  public createReport(report: Report): Promise<any> {
    return this.pdfTemplateApi.create(report);
  }

  public updateReport(id: number, report: Report): Promise<any> {
    return this.pdfTemplateApi.update(id, report);
  }

  public updateInternalReport(report: Report): Promise<any> {
    return this.pdfTemplateApi.updateInternal(report);
  }

  public removeReport(id: number): Promise<any> {
    return this.pdfTemplateApi.remove(id);
  }

  public getHighQualityPreview(id: number): Promise<any> {
    return this.pdfTemplateApi.getHighQualityPreview(id);
  }
}

export default new PdfService();