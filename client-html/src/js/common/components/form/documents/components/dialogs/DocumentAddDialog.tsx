/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import IconButton from "@mui/material/IconButton";
import Close from "@mui/icons-material/Close";
import React, { RefObject } from "react";
import createStyles from "@mui/styles/createStyles";
import debounce from "lodash.debounce";
import { format } from "date-fns";
import Collapse from "@mui/material/Collapse";
import Dialog from "@mui/material/Dialog";
import withStyles from "@mui/styles/withStyles";
import clsx from "clsx";
import LinearProgress from "@mui/material/LinearProgress";
import withTheme from "@mui/styles/withTheme";
import { connect } from "react-redux";
import Typography from "@mui/material/Typography";
import Grid from "@mui/material/Grid";
import Tooltip from "@mui/material/Tooltip";
import { Dispatch } from "redux";
import { KK_MM_AAAA_EEE_DD_MMM_YYYY_SPECIAL } from "../../../../../utils/dates/format";
import { getDocumentItem, searchDocumentByName } from "../../actions";
import { State } from "../../../../../../reducers/state";
import { dialogStyles } from "./dialogStyles";
import EditInPlaceSearchSelect from "../../../../../../../ish-ui/formFields/EditInPlaceSearchSelect";
import { DocumentSearchItem as DocumentSearchItemType } from "../../epics/EpicSearchExistingDocumentByName";
import { stubFunction } from "../../../../../utils/common";
import FileUploaderDialog from "../../../../../../../ish-ui/fileUploader/FileUploaderDialog";

const addDialogStyles = theme => createStyles({
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

const DocumentSearchItem = React.memo<{ data: DocumentSearchItemType; content: string; classes: any, parentProps: any }>(props => {
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

class DocumentAddDialog extends React.PureComponent<any, any> {
  private searchContainerNode: RefObject<HTMLDivElement>;

  constructor(props) {
    super(props);

    this.searchContainerNode = React.createRef();

    this.state = {
      searchValue: "",
      isDragging: this.props.isParentDragging,
      draggingEventAdded: false
    };
  }

  dialogRef: any = React.createRef();

  draggingEvent = isDragging => {
    const { closeAddDialog } = this.props;
    this.setState({
      isDragging
    });

    if (closeAddDialog && !isDragging) {
      closeAddDialog();
    }
  };

  addDraggingEvent = draggingEventAdded => {
    this.setState({ draggingEventAdded });
  };

  addDialogRefEvents = () => {
    const { draggingEventAdded } = this.state;
    if (this.dialogRef.current && !draggingEventAdded) {
      this.addDraggingEvent(true);
      this.dialogRef.current.addEventListener("dragover", () => this.draggingEvent(true));
      this.dialogRef.current.addEventListener("dragenter", () => this.draggingEvent(true));
      this.dialogRef.current.addEventListener("dragleave", () => this.draggingEvent(false));
    }
  };

  setParentDraggingState = isDragging => {
    this.setState({ isDragging });
  };

  UNSAFE_componentWillUpdate(nextProps) {
    this.setParentDraggingState(nextProps.isParentDragging);
    if (nextProps.opened) {
      if (!this.dialogRef.current) {
        setTimeout(() => {
          this.addDialogRefEvents();
        }, 10);
      } else {
        this.addDialogRefEvents();
      }
    } else if (this.dialogRef.current) {
        this.addDraggingEvent(false);
        this.dialogRef.current.removeEventListener("dragover", () => this.draggingEvent(false));
        this.dialogRef.current.removeEventListener("dragenter", () => this.draggingEvent(false));
        this.dialogRef.current.removeEventListener("dragleave", () => this.draggingEvent(false));
      }
  }

  debounceSearch = debounce(() => {
    if (this.state.searchValue) {
      this.props.searchDocumentByName(this.state.searchValue, this.props.form);
    }
  }, 600);

  checkExistingOnUpload = (document: Blob) => {
    const { searchDocument, setDocumentFile } = this.props;

    setDocumentFile(document);
    searchDocument(document);
  };

  handleDocumentUpload = files => {
    if (files.length) this.checkExistingOnUpload(files[0]);
  };

  onSelectInputChange = searchValue => {
    this.setState({ searchValue }, () => {
      if (searchValue) {
        this.debounceSearch();
      } else {
        this.props.clearSearchDocuments();
      }
    });
  };

  onSelectChange = value => {
    const { getDocumentItem, form } = this.props;

    this.setState({
      searchValue: ""
    });

    getDocumentItem(value, form);
  };

  onClose = () => {
    const { onClose, clearSearchDocuments } = this.props;

    this.setState({
      searchValue: ""
    });

    onClose();
    clearSearchDocuments();
  };

  searchItemsRenderer = (content, data, search, props) => (
    <DocumentSearchItem classes={this.props.classes} data={data} content={content} parentProps={props} />
  );

  componentWillUnmount() {
    this.props.clearSearchDocuments();
  }

  render() {
    const {
      opened, classes, fetch, searchItems, searchExistingDocsDisabled
    } = this.props;

    const { searchValue, isDragging } = this.state;

    return (
      <>
        <Dialog
          ref={this.dialogRef}
          open={opened}
          onClose={this.onClose}
          classes={{
            paper: clsx("overflow-visible", {
              [classes.addDialogMargin]: Boolean(searchItems?.length),
              [classes.paperWithSearch]: Boolean(searchItems?.length) && !fetch.pending
            })
          }}
        >
          <div className="centeredFlex p-1" ref={this.searchContainerNode}>
            <div className={clsx(classes.selectContainer, { "zIndex2": isDragging })}>
              <EditInPlaceSearchSelect
                selectValueMark="id"
                selectLabelMark="name"
                input={{ value: searchValue, onChange: this.onSelectChange as any, onBlur: stubFunction } as any}
                meta={{} as any}
                onInputChange={this.onSelectInputChange}
                placeholder="Find existing documents"
                items={searchItems || []}
                itemRenderer={this.searchItemsRenderer}
                popperAnchor={this.searchContainerNode.current}
                disabled={searchExistingDocsDisabled}
                hideMenuOnNoResults
                disableUnderline
                hideEditIcon
              />
            </div>
            <IconButton
              className={clsx("closeAndClearButton", classes.closeAndClearButton, { "zIndex2": isDragging })}
              onClick={this.onClose}
            >
              <Close className="inputAdornmentIcon" />
            </IconButton>
            {fetch.pending && <LinearProgress className={classes.documentLoading} />}
          </div>
          <Collapse in={!searchValue}>
            <FileUploaderDialog onChange={this.handleDocumentUpload} disabled={fetch.pending} isBackdropDragging={isDragging} />
          </Collapse>
          <FileUploaderDialog onChange={this.handleDocumentUpload} disabled={fetch.pending} backdropEnabled />
        </Dialog>
      </>
    );
  }
}

const mapStateToProps = (state: State) => ({
  fetch: state.fetch,
  searchItems: state.documents.searchDocuments
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  searchDocumentByName: (documentName: string, editingFormName: string) => dispatch(searchDocumentByName(documentName, editingFormName)),
  getDocumentItem: (id: number, editingFormName: string) => dispatch(getDocumentItem(id, editingFormName))
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(withTheme(withStyles(theme => ({ ...dialogStyles(theme), ...addDialogStyles(theme) }))(DocumentAddDialog)));