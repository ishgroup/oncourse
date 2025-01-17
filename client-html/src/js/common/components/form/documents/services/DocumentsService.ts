import { Document, DocumentApi } from "@api/model";
import { DefaultHttpService } from "../../../../services/HttpService";

class DocumentsService {
  readonly service = new DefaultHttpService();

  readonly documentApi = new DocumentApi(this.service);

  public createDocument(
    name: string,
    description: string,
    shared: boolean,
    access: string,
    content: File,
    tags: string,
    fileName: string,
  ): Promise<Document> {
    return this.documentApi.create(name, description, shared, access, fileName, content, tags);
  }

  public getDocumentItem(id: number): Promise<Document> {
    return this.documentApi.get(id);
  }

  public updateDocumentItem(id: number, document): Promise<Document> {
    return this.documentApi.update(id, document);
  }

  public deleteDocumentItem(id: number): Promise<Document> {
    return this.documentApi.remove(id);
  }

  public searchDocument(content: File): Promise<Document> {
    return this.service.POST("/v1/list/entity/document/search", content);
  }
}

export default new DocumentsService();
