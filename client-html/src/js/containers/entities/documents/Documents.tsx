/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Document } from '@api/model';
import { OpenWith } from '@mui/icons-material';
import Delete from '@mui/icons-material/Delete';
import Button from '@mui/material/Button';
import $t from '@t';
import { BooleanArgFunction, NoArgFunction } from 'ish-ui';
import React, { useCallback, useEffect } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { initialize } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import { clearEditingDocument, searchDocumentByHash } from '../../../common/components/form/documents/actions';
import DocumentAddDialog from '../../../common/components/form/documents/components/dialogs/DocumentAddDialog';
import {
  getFilters,
  setFilterGroups,
  setListEditRecord,
  setListSelection
} from '../../../common/components/list-view/actions';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../common/components/list-view/constants';
import ListView from '../../../common/components/list-view/ListView';
import { getManualLink } from '../../../common/utils/getManualLink';
import { FilterGroup, FindRelatedItem } from '../../../model/common/ListView';
import { State } from '../../../reducers/state';
import { getEntityTags, getListTags } from '../../tags/actions';
import BinCogwheel from './components/BinCogwheel';
import DocumentEditView from './components/DocumentEditView';

const styles = () => ({
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
  onInit?: (document: Document) => void;
  getRecords?: () => void;
  getFilters?: () => void;
  getTags?: () => void;
  classes?: any;
  updateSelection?: (selection: string[]) => void;
  history?: any;
  match?: any;
  searchExistingDocument?: (inputDocument: File) => void;
  editingDocument?: Document;
  editRecord?: any;
  clearEditingDocument?: NoArgFunction;
  threeColumn?: boolean;
}

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
            {$t('bin')}
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

const manualLink = getManualLink("document-management-documents-in-oncourse");

const openDocumentURL = (e: React.MouseEvent<any>, url: string) => {
  e.stopPropagation();
  window.open(url);
};

const setRowClasses = ({ active }) => (active === "No" ? "text-op05" : undefined);

const Documents: React.FC<DocumentProps> = props => {
  const {
    onInit,
    getFilters,
    editRecord,
    getTags,
    classes,
    updateSelection,
    history,
    searchExistingDocument,
    clearEditingDocument,
    match: { params, url },
    editingDocument,
    threeColumn
  } = props;

  const [openFileModal, setOpenFileModal] = React.useState<boolean>(false);

  const updateHistory = (pathname, search) => {
    const newUrl = window.location.origin + pathname + search;

    if (newUrl !== window.location.href) {
      history.push({
        pathname,
        search
      });
    }
  };

  const closeAddDialog = useCallback(() => {
    clearEditingDocument();
    setOpenFileModal(false);
  }, []);

  const onDragEnter = useCallback(() => {
    setOpenFileModal(true);
  }, []);

  useEffect(() => {
    window.addEventListener("dragenter", onDragEnter);
    return () => {
      window.removeEventListener("dragenter", onDragEnter);
    };
  }, []);

  const setCreateNew = (initial: Document) => {
    updateHistory(params.id ? url.replace(`/${params.id}`, "/new") : url + "/new", window.location.search);

    const processCreate = () => {
      updateSelection(["new"]);
      onInit(initial);
      closeAddDialog();
    };

    setTimeout(processCreate);
  };

  useEffect(() => {
    if (editingDocument) {
      setCreateNew(editingDocument);
    }
  }, [editingDocument]);

  useEffect(() => {
    getFilters();
    getTags();
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
          {$t('view2')}
        </Button>
      </div>
    ) : v)
  }), []);

  const customOnCreate = () => {
    if (editRecord && params.id === "new") return;
    setOpenFileModal(true);
  };

  const closeHandler = () => {
    updateHistory(url.replace("/new", ""), window.location.search);
    closeAddDialog();
  };

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
        findRelated={findRelatedGroup}
        filterGroupsInitial={filterGroups}
        customOnCreate={customOnCreate}
        defaultDeleteDisabled
        noListTags
      />

      <DocumentAddDialog
        opened={openFileModal}
        onClose={closeHandler}
        searchDocument={searchExistingDocument}
        closeAddDialog={closeAddDialog}
        hideSearch
      />
    </>
  );
};

const mapStateToProps = (state: State) => ({
  editingDocument: state.documents.editingDocument,
  editRecord: state.list.editRecord,
  threeColumn: state.list.records.layout === "Three column"
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: initial => {
    dispatch(setListEditRecord(initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, initial));
  },
  getFilters: () => dispatch(getFilters("Document")),
  getTags: () => {
    dispatch(getEntityTags("Document"));
    dispatch(getListTags("Document"));
  },
  setFilterGroups: (filterGroups: FilterGroup[]) => dispatch(setFilterGroups(filterGroups)),
  updateSelection: (selection: string[]) => dispatch(setListSelection(selection)),
  searchExistingDocument: (inputDocument: File, editingFormName: string) =>
    dispatch(searchDocumentByHash(inputDocument, editingFormName)),
  clearEditingDocument: () => dispatch(clearEditingDocument())
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(Documents, styles));