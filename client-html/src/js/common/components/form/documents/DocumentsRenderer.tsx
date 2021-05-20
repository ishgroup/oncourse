/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { Document, Tag } from "@api/model";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import Launch from "@material-ui/icons/Launch";
import Typography from "@material-ui/core/Typography";
import Grid from "@material-ui/core/Grid";
import AddCircle from "@material-ui/icons/AddCircle";
import IconButton from "@material-ui/core/IconButton";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { WrappedFieldArrayProps } from "redux-form";
import { AppTheme } from "../../../../model/common/Theme";
import { openInternalLink } from "../../../utils/links";
import DocumentItem from "./components/items/DocumentItem";
import DocumentAddDialog from "./components/dialogs/DocumentAddDialog";
import { State } from "../../../../reducers/state";
import {
  clearEditingDocument,
  createDocument,
  searchDocumentByHash,
  setDocumentFile,
  setEditingDocument,
  setSearchDocuments
} from "./actions";
import DocumentEditDialog, { DocumentDialogType } from "./components/dialogs/DocumentEditDialog";
import { getEntityTags } from "../../../../containers/tags/actions";
import { EntityName } from "../../../../model/entities/common";
import { ShowConfirmCaller } from "../../../../model/common/Confirm";

const styles = (theme: AppTheme) => createStyles({
  dropInfo: {
    borderBottom: `1px dashed ${theme.palette.text.primary}`,
    visibility: "hidden"
  },
  addButton: {
    "&:hover + $dropInfo": {
      visibility: "visible"
    }
  },
  documentGridItem: {
    height: "auto"
  }
});

interface DocumentsRendererState {
  openAddDialog: boolean;
  editingDocumentIndex: number;
  editingDocumentPath: string;
  editingDocumentType: DocumentDialogType;
  isNewEditingDocument: boolean;
  menuTags: Tag[];
  isDragging: boolean;
}

interface DocumentsRendererProps {
  editingDocument: any;
  editingFormName: string;
  form: string;
  classes: any;
  label: string;
  xsGrid: any;
  mdGrid: any;
  lgGrid: any;
  dispatch: Dispatch;
  entity: EntityName;
  setDocumentFile: any;
  getDocumentTags: any;
  viewDocument: any;
  setEditingDocument: any;
  clearEditingDocument: any;
  searchExistingDocument: any;
  showConfirm: ShowConfirmCaller;
  clearSearchDocuments: any;
  createDocument: any;
  tags: any;
}

class DocumentsRenderer extends React.PureComponent<DocumentsRendererProps & WrappedFieldArrayProps, DocumentsRendererState> {
  state = {
    openAddDialog: false,
    editingDocumentIndex: null,
    editingDocumentPath: "",
    editingDocumentType: null,
    isNewEditingDocument: false,
    menuTags: null,
    isDragging: true
  };

  toggleAdd = (e, addButtonClicked = false) => {
    this.setState(prev => ({
      openAddDialog: !prev.openAddDialog
    }));

    if (addButtonClicked) {
      this.setState(() => ({
        isDragging: false
      }));
    }
  };

  closeEdit = () => {
    const { clearEditingDocument, clearSearchDocuments } = this.props;

    clearSearchDocuments();
    clearEditingDocument();

    this.setState({
      editingDocumentIndex: null,
      editingDocumentPath: "",
      editingDocumentType: null,
      isNewEditingDocument: false
    });
  };

  removeNewDocument = () => {
    const { fields } = this.props;

    const { isNewEditingDocument, editingDocumentIndex } = this.state;

    if (isNewEditingDocument) {
      fields.remove(editingDocumentIndex);
    }

    this.closeEdit();
  };

  unlinkDocument = (index: number) => {
    const { showConfirm, fields } = this.props;

    const { editingDocumentPath } = this.state;

    showConfirm({ onConfirm: () => fields.remove(index), confirmMessage: "Document will be unlinked" });

    if (editingDocumentPath) {
      this.closeEdit();
    }
  };

  onAddDocument = () => {
    const { createDocument, form, fields } = this.props;

    const { editingDocumentIndex } = this.state;

    createDocument(fields.get(editingDocumentIndex), form, fields.name, editingDocumentIndex);
  };

  onCancelEdit = () => {
    const { editingDocument, fields } = this.props;

    const { editingDocumentIndex, isNewEditingDocument } = this.state;

    if (!isNewEditingDocument) {
      fields.remove(editingDocumentIndex);
      fields.insert(editingDocumentIndex, editingDocument);
    }

    this.closeEdit();
  };

  fileDragEvent = (e, openAddDialog) => {
    e.stopPropagation();
    e.preventDefault();
    if (!e.dataTransfer.types.some(t => t === "Files")) {
      return;
    }

    this.setState(prev => ({
      ...prev,
      openAddDialog: prev.editingDocumentIndex !== null ? false : openAddDialog,
      isDragging: openAddDialog
    }));
  };

  componentDidMount() {
    const { tags, getDocumentTags } = this.props;

    if (!tags && getDocumentTags) {
      getDocumentTags();
    }

    window.addEventListener("dragenter", e => this.fileDragEvent(e, true));
  }

  componentDidUpdate(prevProps) {
    const {
     editingDocument, editingFormName, form, fields, tags, viewDocument
    } = this.props;

    if (!prevProps.editingDocument && editingDocument && editingFormName === form) {
      const allDocuments = fields.getAll() || [];

      if (editingDocument.id) {
        const matchDocIndex = allDocuments.findIndex(i => i.id === editingDocument.id);

        const isNew = matchDocIndex === -1;

        if (isNew) {
          fields.push(editingDocument);
        }

        this.setState({
          editingDocumentIndex: isNew ? allDocuments.length : matchDocIndex,
          editingDocumentPath: isNew ? `${fields.name}[${allDocuments.length}]` : `${fields.name}[${matchDocIndex}]`,
          editingDocumentType: viewDocument ? "view" : "edit",
          openAddDialog: false,
          isNewEditingDocument: isNew
        });

        return;
      }

      fields.push(editingDocument);

      this.setState({
        editingDocumentIndex: allDocuments.length,
        editingDocumentPath: `${fields.name}[${allDocuments.length}]`,
        editingDocumentType: "create",
        openAddDialog: false,
        isNewEditingDocument: true
      });
    }

    if (prevProps.editingDocument && !editingDocument) {
      this.closeEdit();
    }

    if (!this.state.menuTags && tags) {
      this.setState({
        menuTags: JSON.parse(JSON.stringify(tags))
      });
    }
  }

  componentWillUnmount() {
    window.removeEventListener("dragenter", e => this.fileDragEvent(e, false));
  }

  setEditingItem = (document: Document) => {
    const { setEditingDocument, form } = this.props;

    setEditingDocument(document, form);
  };

  setViewItem = (document: Document) => {
    const { setEditingDocument, form } = this.props;

    setEditingDocument(document, form, true);
  };

  searchDocumentItem = (inputDocument: File) => {
    const { searchExistingDocument, form } = this.props;
    searchExistingDocument(inputDocument, form);
  };

  closeAddDialog = () => {
    this.setState(prev => ({
      ...prev,
      openAddDialog: false,
    }));
  };

  render() {
    const {
      classes,
      fields,
      label,
      xsGrid,
      mdGrid,
      lgGrid,
      dispatch,
      form,
      entity,
      setDocumentFile,
      showConfirm,
      clearSearchDocuments,
      meta: { dirty }
    } = this.props;

    const {
      openAddDialog,
      editingDocumentIndex,
      editingDocumentPath,
      editingDocumentType,
      menuTags,
      isNewEditingDocument,
      isDragging
    } = this.state;

    const editItem = fields.get(editingDocumentIndex);

    const renderedItems = fields.getAll();

    return (
      <>
        <DocumentAddDialog
          opened={openAddDialog}
          onClose={this.toggleAdd}
          searchDocument={this.searchDocumentItem}
          setDocumentFile={setDocumentFile}
          form={form}
          clearSearchDocuments={clearSearchDocuments}
          closeAddDialog={this.closeAddDialog}
          isParentDragging={isDragging}
        />

        <DocumentEditDialog
          opened={Boolean(editItem)}
          item={editItem}
          dispatch={dispatch}
          form={form}
          tags={menuTags}
          type={editingDocumentType}
          index={editingDocumentIndex}
          itemPath={editingDocumentPath}
          isNew={isNewEditingDocument}
          onClose={this.removeNewDocument}
          onCancelEdit={this.onCancelEdit}
          onSave={this.closeEdit}
          onAdd={this.onAddDocument}
          onUnlink={this.unlinkDocument}
          dirty={dirty}
          entity={entity}
        />

        <Grid item xs={12}>
          <div className="centeredFlex">
            <div className="heading">
              {fields.length > 0 ? fields.length : ""}
              {' '}
              {fields.length === 1 ? label.replace(/s$/, "") : label}
            </div>
            <IconButton
              style={{ marginRight: "-8px" }}
              color="secondary"
              onClick={() => openInternalLink("/document")}
            >
              <Launch />
            </IconButton>
            <IconButton onClick={e => this.toggleAdd(e, true)} className={classes.addButton}>
              <AddCircle className="addButtonColor" />
            </IconButton>
            <Typography variant="caption" className={`relative ${classes.dropInfo}`}>
              Drag and drop file or click to browse.
            </Typography>
          </div>
        </Grid>
        <Grid container spacing={3} wrap="wrap">
          {renderedItems && renderedItems.map((item, index) => (
            <Grid item xs={xsGrid} md={mdGrid} lg={lgGrid} key={item.id} className={classes.documentGridItem}>
              <DocumentItem
                entity={entity}
                index={index}
                item={item}
                editItem={this.setEditingItem}
                viewItem={this.setViewItem}
                unlink={this.unlinkDocument}
              />
            </Grid>
          ))}
        </Grid>
      </>
    );
  }
}

const mapStateToProps = (state: State) => ({
  editingDocument: state.documents.editingDocument,
  editingFormName: state.documents.editingFormName,
  viewDocument: state.documents.viewDocument,
  tags: state.tags.entityTags["Document"]
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    clearEditingDocument: () => dispatch(clearEditingDocument()),
    clearSearchDocuments: () => dispatch(setSearchDocuments(null)),
    setEditingDocument: (document: Document, editingFormName: string, viewDocument: boolean = false) =>
      dispatch(setEditingDocument(document, editingFormName, viewDocument)),
    setDocumentFile: (file: File) => dispatch(setDocumentFile(file)),
    getDocumentTags: () => dispatch(getEntityTags("Document")),
    searchExistingDocument: (inputDocument: File, editingFormName: string) =>
      dispatch(searchDocumentByHash(inputDocument, editingFormName)),
    createDocument: (document: Document, form: string, documentPath: string, index: number) =>
      dispatch(createDocument(document, form, documentPath, index))
  });

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(DocumentsRenderer));
