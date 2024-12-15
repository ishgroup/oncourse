/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Close from '@mui/icons-material/Close';
import Collapse from '@mui/material/Collapse';
import Dialog from '@mui/material/Dialog';
import Grid from '@mui/material/Grid';
import IconButton from '@mui/material/IconButton';
import LinearProgress from '@mui/material/LinearProgress';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';
import clsx from 'clsx';
import { format } from 'date-fns';
import {
  AnyArgFunction,
  EditInPlaceSearchSelect,
  FileUploaderDialog,
  KK_MM_AAAA_EEE_DD_MMM_YYYY_SPECIAL,
  NoArgFunction,
  stubFunction
} from 'ish-ui';
import debounce from 'lodash.debounce';
import React, { useCallback, useEffect, useRef, useState } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { withStyles } from 'tss-react/mui';
import { Fetch } from '../../../../../../model/common/Fetch';
import { DocumentSearchItemType } from '../../../../../../model/entities/Document';
import { State } from '../../../../../../reducers/state';
import { getDocumentItem, searchDocumentByName, setSearchDocuments } from '../../actions';
import { dialogStyles } from './dialogStyles';

const addDialogStyles = theme => ({
  addDialogMargin: {
    marginBottom: "300px"
  },
  searchItemPartWrapper: {
    marginLeft: theme.spacing(2),
    "&:first-child": {
      marginLeft: 0
    }
  },
  paperWithSearch: {
    height: "46px"
  }
});

const DocumentSearchItem = React.memo<{
  data: DocumentSearchItemType;
  content: string;
  classes: any,
  parentProps: any
}>(props => {
  const {
    classes, data, content, parentProps
  } = props;

  const formattedDate = format(new Date(data.added), KK_MM_AAAA_EEE_DD_MMM_YYYY_SPECIAL).replace(/\./g, "");

  return (
    <div {...parentProps}>
      <Grid item xs={4} className={clsx("text-truncate text-nowrap", classes.searchItemPartWrapper)}>
        <Tooltip title={content}>
          <Typography variant="body2" component="span" className="text-truncate">
            {content}
          </Typography>
        </Tooltip>
      </Grid>
      <Grid item xs={4} className={clsx("text-truncate text-nowrap", classes.searchItemPartWrapper)}>
        <Tooltip title={`${data.name} - ${data.byteSize}`}>
          <Typography variant="body2" component="span" color="textSecondary" className="text-truncate">
            {data.fileName}
            {' '}
            -
            {data.byteSize}
          </Typography>
        </Tooltip>
      </Grid>
      <Grid item xs={4} className={clsx("text-truncate text-nowrap", classes.searchItemPartWrapper)}>
        <Tooltip title={formattedDate}>
          <Typography variant="body2" component="span" color="textSecondary" className="text-truncate">
            {formattedDate}
          </Typography>
        </Tooltip>
      </Grid>
    </div>
  );
});

interface OwnProps {
  opened: boolean;
  onClose: AnyArgFunction;
  closeAddDialog: AnyArgFunction;
  clearSearchDocuments?: AnyArgFunction;
  form?: string;
  searchDocument?: (file: File) => void;
  setDocumentFile?: (file: File) => void;
  hideSearch?: boolean;
  classes?: any;
  searchExistingDocsDisabled?: boolean;
}

interface StateProps {
  fetch: Fetch;
  searchItems: DocumentSearchItemType[];
}

interface DispatchProps {
  searchDocumentByName: (documentName: string, editingFormName: string) => void;
  getDocumentItem: (id: number, editingFormName: string) => void;
  clearSearchDocuments: NoArgFunction;
}

const DocumentAddDialog = (
  {
    opened,
    closeAddDialog,
    searchDocumentByName,
    clearSearchDocuments,
    form,
    searchDocument,
    setDocumentFile,
    getDocumentItem,
    onClose,
    hideSearch,
    fetch,
    classes,
    searchItems,
    searchExistingDocsDisabled
  }: OwnProps & StateProps & DispatchProps) => {
  
  const searchContainerNode = useRef<HTMLDivElement>();
  const dialogRef = useRef<HTMLDivElement>();

  const [searchValue, setSearchValue] = useState("");
  
  const onDragLeave = useCallback(e => {
    if (!e.fromElement && closeAddDialog) {
      closeAddDialog();
    }
  }, []);

  useEffect(() => {
    window.addEventListener("dragleave", onDragLeave);
    return () => {
      clearSearchDocuments();
      window.removeEventListener("dragleave", onDragLeave);
    };
  }, []);

  const debounceSearch = useCallback(debounce(search => {
    searchDocumentByName(search, form);
  }, 600), [form]);

  const checkExistingOnUpload = (document: File) => {
    searchDocument(document);
    if (setDocumentFile) setDocumentFile(document);
  };

  const handleDocumentUpload = files => {
    if (files.length) checkExistingOnUpload(files[0]);
  };

  const onSelectInputChange = searchValue => {
    debounceSearch(searchValue);
    setSearchValue(searchValue);
    if (!searchValue) {
      clearSearchDocuments();
    }
  };

  const onSelectChange = value => {
    setSearchValue("");
    getDocumentItem(value, form);
  };

  const closeHandler = () => {
    setSearchValue("");
    onClose();
    clearSearchDocuments();
  };

  const searchItemsRenderer = useCallback((content, data, search, props) => (
    <DocumentSearchItem classes={classes} data={data} content={content} parentProps={props}/>
  ), [classes]);

  return (
    hideSearch
      ?
      <FileUploaderDialog
        dialog
        fileRef={dialogRef}
        opened={opened}
        onClose={closeHandler}
        onChange={handleDocumentUpload}
        disabled={fetch.pending}
        isBackdropDragging
        backdropEnabled
      />
      : <Dialog
      ref={dialogRef}
      open={opened}
      onClose={closeHandler}
      classes={{
        paper: clsx("overflow-visible", {
          [classes.addDialogMargin]: Boolean(searchItems?.length),
          [classes.paperWithSearch]: Boolean(searchItems?.length) && !fetch.pending
        })
      }}
      maxWidth="sm"
      fullWidth
    >
      <div className={clsx("centeredFlex", classes.selectWrapper)}  ref={searchContainerNode}>
        <div className={clsx(classes.selectContainer)}>
          <EditInPlaceSearchSelect
            selectValueMark="id"
            selectLabelMark="name"
            input={{ value: searchValue, onChange: onSelectChange as any, onBlur: stubFunction } as any}
            meta={{} as any}
            onInputChange={onSelectInputChange}
            placeholder="Find existing documents"
            items={searchItems || []}
            itemRenderer={searchItemsRenderer}
            popperAnchor={searchContainerNode.current}
            disabled={searchExistingDocsDisabled}
            hideMenuOnNoResults
            disableUnderline
            hideEditIcon
          />
        </div>
        <IconButton
          className={clsx("closeAndClearButton", classes.closeAndClearButton)}
          onClick={closeHandler}
        >
          <Close className="inputAdornmentIcon"/>
        </IconButton>
        {fetch.pending && <LinearProgress className={classes.documentLoading}/>}
      </div>
      <Collapse in={!searchValue}>
        <FileUploaderDialog onChange={handleDocumentUpload} disabled={fetch.pending} />
      </Collapse>
        <FileUploaderDialog onChange={handleDocumentUpload} disabled={fetch.pending} isBackdropDragging backdropEnabled />
      </Dialog>
  );
  
};

const mapStateToProps = (state: State) => ({
  fetch: state.fetch,
  searchItems: state.documents.searchDocuments
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  searchDocumentByName: (documentName: string, editingFormName: string) => dispatch(searchDocumentByName(documentName, editingFormName)),
  getDocumentItem: (id: number, editingFormName: string) => dispatch(getDocumentItem(id, editingFormName)),
  clearSearchDocuments: () => dispatch(setSearchDocuments(null)),
});

export default connect<StateProps, DispatchProps, OwnProps>(
  mapStateToProps,
  mapDispatchToProps
)(withStyles(
  DocumentAddDialog,
  theme => ({ ...dialogStyles(theme), ...addDialogStyles(theme) })
));