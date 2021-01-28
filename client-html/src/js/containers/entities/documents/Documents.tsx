/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect } from "react";
import withStyles from "@material-ui/core/styles/withStyles";
import createStyles from "@material-ui/core/styles/createStyles";
import Delete from "@material-ui/icons/Delete";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { initialize } from "redux-form";
import { Document } from "@api/model";
import { OpenWith } from "@material-ui/icons";
import Button from "@material-ui/core/Button";
import FileUploaderDialog from "../../../common/components/file-uploader/FileUploaderDialog";
import { getInitialDocument } from "../../../common/components/form/documents/components/utils";
import DocumentsService from "../../../common/components/form/documents/services/DocumentsService";
import { BooleanArgFunction } from "../../../model/common/CommonFunctions";
import { FilterGroup } from "../../../model/common/ListView";
import ListView from "../../../common/components/list-view/ListView";
import {
  setListEditRecord,
  getFilters,
  clearListState,
  setFilterGroups,
  setListSelection,
  setListCreatingNew,
  setListFullScreenEditView
} from "../../../common/components/list-view/actions";
import { State } from "../../../reducers/state";
import { getEntityTags } from "../../tags/actions";
import { getManualLink } from "../../../common/utils/getManualLink";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import {
  createDocument, getDocument, removeDocument, updateDocument
} from "./actions";
import DocumentEditView from "./components/DocumentEditView";

const styles = () => createStyles({
  linkBtnWrapper: {
    height: "19px"
  },
  linkBtn: {
    height: "100%",
    padding: "0 15px",
    marginBottom: "2px",
    lineHeight: "1",
    fontSize: "inherit",
    borderRadius: "15px"
  },
  openDocIcon: {
    height: "90%",
    fontSize: "1.4rem",
    paddingRight: "5px",
    maxHeight: "100%"
  }
});

interface DocumentProps {
  getDocumentRecord?: () => void;
  onInit?: () => void;
  onSave?: (id: string, document: Document) => void;
  getRecords?: () => void;
  getFilters?: () => void;
  clearListState?: () => void;
  onDelete?: (id: string) => void;
  getTags?: () => void;
  onCreate: (document: Document) => void;
  classes?: any;
  setListCreatingNew?: BooleanArgFunction;
  updateSelection?: (selection: string[]) => void;
  history?: any;
  location?: any;
  match?: any;
  fullScreenEditView?: boolean;
  setListFullScreenEditView?: BooleanArgFunction;
}

let Initial: Document = {
  id: null,
  name: null,
  createdOn: null,
  modifiedOn: null,
  thumbnail: null,
  versionId: null,
  access: null,
  shared: true,
  description: null,
  versions: [],
  tags: []
};

const isRemoved = (value: string) => "isRemoved is " + value;

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Website",
        expression: "webVisibility is PUBLIC and " + isRemoved("false"),
        active: true
      },
      {
        name: "Private",
        expression: "webVisibility is PRIVATE and " + isRemoved("false"),
        active: true
      },
      {
        name: "Tutors and enrolled students",
        expression: "webVisibility is STUDENTS and " + isRemoved("false"),
        active: true
      },
      {
        name: "Tutors",
        expression: "webVisibility is TUTORS and " + isRemoved("false"),
        active: true
      },
      {
        name: "Linkable",
        expression: "(webVisibility is PUBLIC or webVisibility is LINK) and " + isRemoved("false"),
        active: true
      },
      {
        name: "Bin",
        customLabel: () => (
          <span className="centeredFlex">
            Bin
            {" "}
            <Delete style={{ fontSize: "18px" }} color="disabled" />
          </span>
        ),
        expression: isRemoved("true"),
        active: false
      }
    ]
  }
];

const findRelatedGroup: any = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == Document and entityId" },
  { title: "Courses", list: "course", expression: "attachmentRelations.entityIdentifier == 'Course' and attachmentRelations.document.id" },
  {
    title: "Classes",
    list: "class",
    expression: "attachmentRelations.entityIdentifier == 'CourseClass' and attachmentRelations.document.id"
  },
  {
    title: "Contacts",
    list: "contact",
    expression: "attachmentRelations.entityIdentifier == 'Contact' and attachmentRelations.document.id"
  },
  {
    title: "Enrolments",
    list: "enrolment",
    expression: "attachmentRelations.entityIdentifier == 'Enrolment' and attachmentRelations.document.id"
  }
];

const manualLink = getManualLink("documentManagement");

const openDocumentURL = (e: React.MouseEvent<any>, url: string) => {
  e.stopPropagation();
  window.open(url);
};

const setRowClasses = ({ isRemoved }) => (isRemoved === "Yes" ? "op05" : undefined);

const handleFileSelect = (files, setCreateNew) => {
  const file = files[0];

  if (file) {
    DocumentsService.searchDocument(file).then(res => {
      if (res) {
        // open edit view with that document
        Initial = res;
        setCreateNew();
      } else {
        getInitialDocument(file).then(document => {
          // open edit view with newly created document
          Initial = document;
          setCreateNew();
        });
      }
    });
  }
};

const deleteDisabledCondition = listProps => {
  const { selection, records } = listProps;
  const { rows, columns } = records;

  if (!selection.length || !rows.length || !columns.length) return false;

  const id = selection[0];
  const row = rows.find(r => r.id === id);

  if (!row || !row.values) return false;

  const isRemovedIndex = columns.filter(c => c.visible === true || c.system === true).findIndex(c => c.attribute === "isRemoved");
  let isRemoved = false;

  if (isRemovedIndex !== -1) {
    isRemoved = row.values[isRemovedIndex] === "true";
  }

  return isRemoved;
};

const Documents: React.FC<DocumentProps> = props => {
  const {
    getDocumentRecord,
    onInit,
    onDelete,
    onSave,
    getFilters,
    clearListState,
    getTags,
    onCreate,
    classes,
    setListCreatingNew,
    updateSelection,
    history,
    location,
    match: { params, url },
    setListFullScreenEditView
  } = props;

  const [openFileModal, setOpenFileModal] = React.useState<boolean>(false);
  const [isDragging, setIsDragging] = React.useState<boolean>(false);
  const [draggingEventAdded, setSraggingEventAdded] = React.useState<boolean>(false);
  const [manuallyOpenModal, setManuallyOpenModal] = React.useState<boolean>(false);

  const dialogRef: any = React.useRef<any>(null);

  const updateHistory = (pathname, search) => {
    const newUrl = window.location.origin + pathname + search;

    if (newUrl !== window.location.href) {
      history.push({
        pathname,
        search
      });
    }
  };

  const setCreateNew = React.useCallback(() => {
    updateHistory(params.id ? url.replace(`/${params.id}`, "/new") : url + "/new", location.search);

    setListCreatingNew(true);
    updateSelection(["new"]);
    onInit();
  }, [params, location, url]);

  const fileDragEvent = React.useCallback((e, openAddDialog) => {
    e.stopPropagation();
    e.preventDefault();
    setIsDragging(openAddDialog);
    setOpenFileModal(openAddDialog);
  }, []);

  const addDialogRefEvents = () => {
    if (dialogRef.current !== null && !draggingEventAdded) {
      setSraggingEventAdded(true);
      dialogRef.current.addEventListener("dragover", e => fileDragEvent(e, true));
      dialogRef.current.addEventListener("dragenter", e => fileDragEvent(e, true));
      dialogRef.current.addEventListener("dragleave", e => fileDragEvent(e, false));
    }
  };

  useEffect(() => {
    getFilters();
    getTags();
    window.addEventListener("dragenter", e => fileDragEvent(e, true));
    return () => {
      clearListState();
      window.removeEventListener("dragenter", e => fileDragEvent(e, false));
    };
  }, []);

  const getCustomColumnFormats = useCallback(() => ({
    link: v => (v ? (
      <div className={classes.linkBtnWrapper}>
        <Button
          variant="outlined"
          size="small"
          color="secondary"
          onClick={(e: any) => openDocumentURL(e, v)}
          className={classes.linkBtn}
        >
          <OpenWith className={classes.openDocIcon} />
          {` `}
          View
        </Button>
      </div>
    ) : v)
  }), [classes]);

  const onDocumentCreate = useCallback(doc => {
    const docModel = { ...doc };
    onCreate(docModel);
    setManuallyOpenModal(false);
  }, []);

  const customOnCreate = () => {
    setOpenFileModal(true);
    setIsDragging(true);
    setManuallyOpenModal(true);
  };

  const handleDocumentUpload = React.useCallback(files => {
    if (files.length) {
      handleFileSelect(files, () => {
        setCreateNew();
        setIsDragging(false);
        setOpenFileModal(false);
        setListFullScreenEditView(false);
      });
    }
  }, [params, location, url]);

  const onDocumentModalClose = React.useCallback(() => {
    setOpenFileModal(false);
    setIsDragging(false);
    setManuallyOpenModal(false);
    updateHistory(url.replace("/new", ""), location.search);
  }, [url, location]);

  React.useEffect(() => {
    if (!manuallyOpenModal) {
      if (openFileModal) {
        if (!dialogRef.current) {
          setTimeout(() => {
            addDialogRefEvents();
          }, 100);
        } else {
          addDialogRefEvents();
        }
      } else if (dialogRef.current) {
        setSraggingEventAdded(false);
        dialogRef.current.removeEventListener("dragover", e => fileDragEvent(e, false));
        dialogRef.current.removeEventListener("dragenter", e => fileDragEvent(e, false));
        dialogRef.current.removeEventListener("dragleave", e => fileDragEvent(e, false));
      }
    }
  }, [openFileModal, manuallyOpenModal]);

  return (
    <>
      <ListView
        listProps={{
          primaryColumn: "name",
          secondaryColumn: "currentVersion.fileName",
          customColumnFormats: getCustomColumnFormats(),
          setRowClasses
        }}
        editViewProps={{
          manualLink,
          hideFullScreenAppBar: true
        }}
        EditViewContent={DocumentEditView}
        getEditRecord={getDocumentRecord}
        rootEntity="Document"
        onInit={onInit}
        customOnCreate={customOnCreate}
        onCreate={onDocumentCreate}
        onDelete={onDelete}
        deleteActionName="Move to bin"
        deleteWithoutConfirmation
        onSave={onSave}
        findRelated={findRelatedGroup}
        filterGroupsInitial={filterGroups}
        deleteDisabledCondition={deleteDisabledCondition}
        noListTags
      />
      <FileUploaderDialog
        dialog
        fileRef={dialogRef}
        opened={openFileModal}
        onChange={handleDocumentUpload}
        isBackdropDragging={isDragging}
        backdropEnabled={isDragging}
        onClose={onDocumentModalClose}
      />
    </>
  );
};

const mapStateToProps = (state: State) => ({
  fullScreenEditView: state.list.fullScreenEditView
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(setListEditRecord(Initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, Initial));
  },
  getFilters: () => dispatch(getFilters("Document")),
  getTags: () => dispatch(getEntityTags("Document")),
  clearListState: () => dispatch(clearListState()),
  getDocumentRecord: (id: number) => dispatch(getDocument(id)),
  onSave: (id: string, document) => dispatch(updateDocument(id, document)),
  onCreate: document => dispatch(createDocument(document)),
  onDelete: (id: string) => dispatch(removeDocument(id)),
  setFilterGroups: (filterGroups: FilterGroup[]) => dispatch(setFilterGroups(filterGroups)),
  updateSelection: (selection: string[]) => dispatch(setListSelection(selection)),
  setListCreatingNew: (creatingNew: boolean) => dispatch(setListCreatingNew(creatingNew)),
  setListFullScreenEditView: (fullScreenEditView: boolean) => dispatch(setListFullScreenEditView(fullScreenEditView))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(Documents));
