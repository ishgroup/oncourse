import { DefaultHttpService } from "../../../../../common/services/HttpService";
import { AutomationConfigs, PdfApi, PdfTemplateApi, PrintRequest, Report } from "@api/model";

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
    return this.pdfTemplateApi.getPreview(id, false);
  }

  public getLowQualityPreview(id: number): Promise<any> {
    return this.pdfTemplateApi.getPreview(id, true);
  }

  public getConfigs(id: number): Promise<string> {
    return this.pdfTemplateApi.getConfigs(id);
  }

  public updateConfigs(id: number, config: AutomationConfigs): Promise<any> {
    return this.pdfTemplateApi.updateConfigs(id, config);
  }
  
  public deletePreview(id: number): Promise<any>  {
    return this.pdfTemplateApi.deletePreview(id);
  }
}

export default new PdfService();