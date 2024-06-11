import { Document, Tag } from "@api/model";
import { DocumentSearchItemType } from "../../../../../model/entities/Document";

export interface DocumentsState {
  editingFormName: string;
  editingDocument: Document;
  tags: Tag[];
  documentFile: File;
  searchDocuments: DocumentSearchItemType[];
  viewDocument: boolean;
}
