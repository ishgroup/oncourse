import { Tag, Document } from "@api/model";

export interface DocumentsState {
  editingFormName: string;
  editingDocument: Document;
  tags: Tag[];
  documentFile: File;
  searchDocuments: any;
  viewDocument: boolean;
}
