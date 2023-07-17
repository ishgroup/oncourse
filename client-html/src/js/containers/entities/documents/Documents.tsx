/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect } from "react";
import withStyles from "@mui/styles/withStyles";
import createStyles from "@mui/styles/createStyles";
import Delete from "@mui/icons-material/Delete";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { initialize } from "redux-form";
import { Document } from "@api/model";
import { OpenWith } from "@mui/icons-material";
import Button from "@mui/material/Button";
import { getInitialDocument } from "../../../common/ish-ui/documents/utils";
import DocumentsService from "../../../common/components/form/documents/services/DocumentsService";
import { BooleanArgFunction } from "../../../model/common/CommonFunctions";
import { FilterGroup, FindRelatedItem } from "../../../model/common/ListView";
import ListView from "../../../common/components/list-view/ListView";
import {
  clearListState,
  getFilters,
  setFilterGroups,
  setListCreatingNew,
  setListEditRecord,
  setListSelection
} from "../../../common/components/list-view/actions";
import { State } from "../../../reducers/state";
import { getEntityTags, getListTags } from "../../tags/actions";
import { getManualLink } from "../../../common/utils/getManualLink";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import DocumentEditView from "./components/DocumentEditView";
import BinCogwheel from "./components/BinCogwheel";
import FileUploaderDialog from "../../../common/ish-ui/fileUploader/FileUploaderDialog";

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
  onInit?: () => void;
  getRecords?: () => void;
  getFilters?: () => void;
  clearListState?: () => void;
  getTags?: () => void;
  classes?: any;
  setListCreatingNew?: BooleanArgFunction;
  updateSelection?: (selection: string[]) => void;
  history?: any;
  location?: any;
  match?: any;
  threeColumn?: boolean;
  fullScreenEditView?: boolean;
  editRecord?: any;
}

let Initial: Document = {
  id: null,
  name: null,
  createdOn: null,
  modifiedOn: null,
  thumbnail: null,
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

const findRelatedGroup: FindRelatedItem[] = [
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

const setRowClasses = ({ active }) => (active === "No" ? "text-op05" : undefined);

const Documents: React.FC<DocumentProps> = props => {
  const {
    onInit,
    getFilters,
    clearListState,
    getTags,
    classes,
    setListCreatingNew,
    updateSelection,
    history,
    location,
    editRecord,
    threeColumn,
    match: { params, url }
  } = props;

  const [openFileModal, setOpenFileModal] = React.useState<boolean>(false);
  const [isDragging, setIsDragging] = React.useState<boolean>(false);
  const [draggingEventAdded, setSraggingEventAdded] = React.useState<boolean>(false);

  const dialogRef: any = React.useRef<any>(null);

  const handleFileSelect = (files, handleCreate) => {
    const file = files[0];

    if (file) {
      DocumentsService.searchDocument(file).then(res => {
        if (res) {
          // open edit view with that document
          Initial = res;
          handleCreate();
        } else {
          getInitialDocument(file).then(document => {
            // open edit view with newly created document
            Initial = document;
            handleCreate();
          });
        }
      });
    }
  };

  const updateHistory = (pathname, search) => {
    const newUrl = window.location.origin + pathname + search;

    if (newUrl !== window.location.href) {
      history.push({
        pathname,
        search
      });
    }
  };

  const setCreateNew = () => {
    updateHistory(params.id ? url.replace(`/${params.id}`, "/new") : url + "/new", window.location.search);

    const processCreate = () => {
      setListCreatingNew(true);
      updateSelection(["new"]);
      onInit();
    };

    threeColumn ? setTimeout(processCreate) : processCreate();
  };

  const fileDragEvent = (e, openAddDialog) => {
    e.stopPropagation();
    e.preventDefault();
    setIsDragging(openAddDialog);
    setOpenFileModal(openAddDialog);
  };

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

  const getCustomColumnFormats = () => ({
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
  });

  const customOnCreate = () => {
    if (editRecord && params.id === "new") return;
    setOpenFileModal(true);
    setIsDragging(true);
  };

  const handleDocumentUpload = files => {
    if (files.length) {
      handleFileSelect(files, () => {
        setCreateNew();
        setOpenFileModal(false);
        setIsDragging(false);
      });
    }
  };

  const onDocumentModalClose = () => {
    setOpenFileModal(false);
    setIsDragging(false);
    updateHistory(url.replace("/new", ""), location.search);
  };

  React.useEffect(() => {
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
  }, [openFileModal]);

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
          hideTitle: true
        }}
        CogwheelAdornment={BinCogwheel}
        EditViewContent={DocumentEditView}
        rootEntity="Document"
        onInit={onInit}
        customOnCreate={customOnCreate}
        findRelated={findRelatedGroup}
        filterGroupsInitial={filterGroups}
        defaultDeleteDisabled
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
  fullScreenEditView: state.list.fullScreenEditView,
  editRecord: state.list.editRecord,
  threeColumn: state.list.records.layout === "Three column"
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(setListEditRecord(Initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, Initial));
  },
  getFilters: () => dispatch(getFilters("Document")),
  getTags: () => {
    dispatch(getEntityTags("Document"));
    dispatch(getListTags("Document"));
  },
  clearListState: () => dispatch(clearListState()),
  setFilterGroups: (filterGroups: FilterGroup[]) => dispatch(setFilterGroups(filterGroups)),
  updateSelection: (selection: string[]) => dispatch(setListSelection(selection)),
  setListCreatingNew: (creatingNew: boolean) => dispatch(setListCreatingNew(creatingNew)),
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(Documents));