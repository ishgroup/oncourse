import { Invoice, InvoiceApi } from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class InvoiceService {
  readonly invoiceApi = new InvoiceApi(new DefaultHttpService());

  public getInvoice(id: number): Promise<Invoice> {
    return this.invoiceApi.get(id);
  }

  public deleteQuote(id: number): Promise<Invoice> {
    return this.invoiceApi.remove(id);
  }

  public updateInvoice(id: number, invoice: Invoice): Promise<any> {
    return this.invoiceApi.update(id, invoice);
  }

  public createInvoice(invoice: Invoice): Promise<any> {
    return this.invoiceApi.create(invoice);
  }

  public postContraInvoices(id: number, invoicesToPay: number[]): Promise<any> {
    return this.invoiceApi.contraInvoice(id, invoicesToPay);
  }

  public searchInvoices(search: string): Promise<Invoice[]> {
    return this.invoiceApi.search(search);
  }
}

export default new InvoiceService();
