/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Drawer from "@material-ui/core/Drawer";
import withStyles from "@material-ui/core/styles/withStyles";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import MenuItem from "@material-ui/core/MenuItem";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import { PlayArrow, Help, Publish } from "@material-ui/icons";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import {
 change, Field, FieldArray, getFormValues, initialize, reduxForm,
} from "redux-form";
import IconButton from "@material-ui/core/IconButton";
import {
  ExportRequest,
  PrintRequest,
  Sorting,
  ExportTemplate,
  Binding,
  OutputType,
  Report, SearchQuery,
} from "@api/model";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Checkbox from "@material-ui/core/Checkbox";
import clsx from "clsx";
import FormField from "../../../form/form-fields/FormField";
import { State } from "../../../../../reducers/state";
import bottomDrawerStyles from "../bottomDrawerStyles";
import {
 addPrintOverlay, doPrintRequest, getShareList, getExportTemplates, runExport,
} from "./actions";
import Button from "../../../buttons/Button";
import SelectionSwitcher from "./SelectionSwitcher";
import { ProcessState } from "../../../../reducers/processReducer";
import { interruptProcess } from "../../../../actions";
import { YYYY_MM_DD_MINUSED } from "../../../../utils/dates/format";
import { getManualLink } from "../../../../utils/getManualLink";
import { getExpression } from "../../utils/listFiltersUtils";
import { getTemplateOutputDisplayName } from "./utils";
import { TemplateOutputDisplayName } from "../../../../../model/common/Share";
import DataTypeRenderer from "../../../form/DataTypeRenderer";
import { validateSingleMandatoryField } from "../../../../utils/validation";
import { CommonListItem } from "../../../../../model/common/sidebar";
import FilePreview from "../../../form/FilePreview";
import ConfirmBase from "../../../dialog/confirm/ConfirmBase";
import { ContactType } from "../../../../../containers/entities/contacts/Contacts";
import { LSGetItem, LSSetItem } from "../../../../utils/storage";

type PdfReportType = ContactType | "GENERAL";

const LAST_SELECTED_EXPORT = "lastSelectedExport";

interface Props {
  rootEntity: string;
  showExportDrawer: boolean;
  toggleExportDrawer: () => void;
  selection: string[];
  count: number;
  process?: ProcessState;
  pdfReports: any[];
  exportTemplates: ExportTemplate[];
  overlays: CommonListItem[];
  submitting?: boolean;
  searchQuery?: SearchQuery;
  sort?: Sorting[];
  processStatus?: string;
  classes?: any;
  dispatch?: any;
  getShareList?: (entityName: string) => void;
  getExportTemplates?: (entityName: string) => void;
  addPrintOverlay?: (fileName: string, overlay: File) => void;
  doPrint?: (rootEntity: string, printRequest: PrintRequest) => void;
  doExport?: (exportRequest: ExportRequest, outputType: OutputType, isClipboard: boolean) => void;
  interruptProcess?: (processId: string) => void;
  getOverlayItems?: () => void;
  handleSubmit?: any;
  onSave?: any;
  sidebarWidth: number;
  values?: any;
  invalid?: boolean;
  AlertComponent?: any;
  validating?: boolean;
  pdfReportsFetching?: boolean;
  exportTemplatesFetching?: boolean;
  rows?: any;
  columns?: any;
}

interface ShareState {
  selectedPrimary: number;
  selectedSecondary: number;
  selectAll: boolean;
  exportTemplateTypes: { [N in TemplateOutputDisplayName]?: ExportTemplate[] };
  createPreview: boolean;
  wrongPdfReportMsg?: string;
}

const manualUrl = getManualLink("importExport");

class ShareForm extends React.PureComponent<Props, ShareState> {
  private fileInputNode: HTMLInputElement;

  private resolvePromise: any;

  private rejectPromise: any;

  private isContactList: boolean;

  private isClipboardExport: boolean;

  constructor(props) {
    super(props);

    this.state = {
      selectedPrimary: null,
      selectedSecondary: null,
      selectAll: false,
      exportTemplateTypes: null,
      createPreview: true,
      wrongPdfReportMsg: null,
    };

    this.isContactList = props.rootEntity === "Contact";
  }

  componentDidMount() {
    const { getShareList, rootEntity, getExportTemplates } = this.props;
    getShareList(rootEntity);
    getExportTemplates(rootEntity);
  }

  componentDidUpdate(prevProps, prevState) {
    const {
     pdfReports, exportTemplates, pdfReportsFetching, exportTemplatesFetching, rootEntity,
    } = this.props;

    if (prevProps.pdfReportsFetching && !pdfReportsFetching && pdfReports.length) {
      if (typeof prevState.selectedPrimary !== "number") {
        const savedState: ShareState = LSGetItem(LAST_SELECTED_EXPORT)
          && JSON.parse(LSGetItem(LAST_SELECTED_EXPORT))[rootEntity];

        if (savedState) {
          if (savedState.selectedPrimary === 0) {
            this.setState(savedState);
            this.props.dispatch(initialize("ListShareForm", this.props.pdfReports[savedState.selectedSecondary]));
          }
        } else {
          this.setState({
            selectedPrimary: 0,
            selectedSecondary: 0,
          });
          this.props.dispatch(initialize("ListShareForm", this.props.pdfReports[0]));
        }
      } else if (prevState.selectedPrimary === 0) {
        this.props.dispatch(initialize("ListShareForm", this.props.pdfReports[prevState.selectedSecondary]));
      }
    }

    if (prevProps.exportTemplatesFetching && !exportTemplatesFetching && exportTemplates.length) {
      const exportTemplateTypes = {};

      exportTemplates.forEach(t => {
        const type = getTemplateOutputDisplayName(t.outputType);

        if (t.variables.length) {
          t.variables.forEach(v => {
            if (v.type === "Checkbox") {
              v.value = "false";
            }
          });
        }

        if (exportTemplateTypes[type]) {
          exportTemplateTypes[type].push(t);
        } else {
          exportTemplateTypes[type] = [t];
        }
      });

      if (typeof prevState.selectedPrimary !== "number") {
        const savedState: ShareState = LSGetItem(LAST_SELECTED_EXPORT)
          && JSON.parse(LSGetItem(LAST_SELECTED_EXPORT))[rootEntity];

        if (savedState && savedState.selectedPrimary > 0 ) {
          this.setState(savedState);
          this.props.dispatch(initialize(
            "ListShareForm",
            exportTemplateTypes[Object.keys(exportTemplateTypes)[savedState.selectedPrimary - 1]][
              savedState.selectedSecondary
              ],
          ));
        }
      } else if (prevState.selectedPrimary > 0) {
        this.props.dispatch(initialize(
          "ListShareForm",
          exportTemplateTypes[Object.keys(exportTemplateTypes)[prevState.selectedPrimary - 1]][
            this.state.selectedSecondary
            ],
        ));
      }

      this.setState({ exportTemplateTypes });
    }
  }

  componentWillUnmount() {
    const { process, interruptProcess, submitting } = this.props;

    if (submitting) {
      interruptProcess(process.processId);
      this.resolvePromise();
    }
  }

  initializeShareForm = (primary: number, secondary: number) => {
    const { dispatch, pdfReports, exportTemplates } = this.props;
    const { exportTemplateTypes } = this.state;

    if (primary === 0 && pdfReports.length) {
      dispatch(initialize("ListShareForm", pdfReports[secondary]));
    }

    if (primary > 0 && exportTemplates.length) {
      dispatch(
        initialize("ListShareForm", exportTemplateTypes[Object.keys(exportTemplateTypes)[primary - 1]][secondary]),
      );
    }
  };

  selectPrimary = selectedPrimary => {
    const { rootEntity } = this.props;

    this.setState({
      selectedPrimary,
      selectedSecondary: 0,
    });

    this.initializeShareForm(selectedPrimary, 0);

    LSSetItem(LAST_SELECTED_EXPORT, JSON.stringify({
      [rootEntity]: {
        selectedPrimary,
        selectedSecondary: 0,
      },
    }));
  };

  selectSecondary = selectedSecondary => {
    const { rootEntity } = this.props;

    const { selectedPrimary } = this.state;

    this.setState({
      selectedSecondary,
    });

    LSSetItem(LAST_SELECTED_EXPORT, JSON.stringify({
      [rootEntity]: {
        selectedPrimary,
        selectedSecondary,
      },
    }));

    this.initializeShareForm(selectedPrimary, selectedSecondary);
  };

  handleBackgroundUpload = () => {
    const { addPrintOverlay } = this.props;

    const file = this.fileInputNode.files[0];

    addPrintOverlay(file && file.name.match(/^[^.]+/)[0], file);
  };

  handleUploadBackgroundClick = () => {
    this.props.dispatch(change("ListShareForm", "background", null));
    setTimeout(() => {
      this.fileInputNode.click();
    }, 350);
  };

  getFileInputNode = node => {
    this.fileInputNode = node;
  };

  getSelectedContactsTypes = () => {
    const { selection, rows, columns } = this.props;
    const contactTypeIndex = columns.findIndex(c => c.attribute === "contactType");
    const selectionTypes = rows.filter(r => selection.includes(r.id)).map(r => r.values[contactTypeIndex]);

    return Array.from(new Set(selectionTypes));
  };

  getSelectedPdfReportName = () => {
    const { pdfReports } = this.props;
    const selectedReportIndex = this.state.selectedSecondary;

    if (!pdfReports || typeof selectedReportIndex !== "number") return "";

    const selectedReport = pdfReports[selectedReportIndex];

    return selectedReport && selectedReport.name ? selectedReport.name : "";
  };

  getPdfReportType = (pdfReportName: string): PdfReportType => {
    const name = pdfReportName.toLowerCase();

    if (name.includes("student") && name.includes("tutor")) return "TUTOR_STUDENT";
    if (name.includes("student")) return "STUDENT";
    if (name.includes("tutor")) return "TUTOR";
    if (name.includes("company")) return "COMPANY";

    return "GENERAL";
  };

  getWrongPdfReportMsg = () => {
    const selectedPdfReportName = this.getSelectedPdfReportName();
    const pdfReportType = this.getPdfReportType(selectedPdfReportName);
    const selectedContactsTypes = this.getSelectedContactsTypes();

    switch (pdfReportType) {
      case "STUDENT": {
        if (selectedContactsTypes.includes("COMPANY") || selectedContactsTypes.includes("TUTOR")) {
          return `You have chosen to print ${selectedPdfReportName} but within the records you selected to print there is a non-student.`;
        }
        break;
      }

      case "TUTOR": {
        if (selectedContactsTypes.includes("COMPANY") || selectedContactsTypes.includes("STUDENT")) {
          return `You have chosen to print ${selectedPdfReportName} but within the records you selected to print there is a non-tutor.`;
        }
        break;
      }

      case "TUTOR_STUDENT": {
        if (selectedContactsTypes.includes("COMPANY")) {
          return `You have chosen to print ${selectedPdfReportName} but within the records you selected to print there is a non-company.`;
        }
        break;
      }
      default: {
        return "";
      }
    }

    return "";
  };

  showWrongPdfReportDialog = (msg: string) => {
    this.setState({ wrongPdfReportMsg: msg });
  };

  hideWrongPdfReportDialog = () => {
    this.setState({ wrongPdfReportMsg: null });
  };

  onSave = values => {
    const {
     searchQuery, rootEntity, doPrint, doExport, selection, sort,
    } = this.props;

    const { selectedPrimary, selectAll, createPreview } = this.state;

    if (this.isContactList && selectedPrimary === 0) {
      const wrongPdfReportMsg = this.getWrongPdfReportMsg();

      if (wrongPdfReportMsg) {
        this.showWrongPdfReportDialog(wrongPdfReportMsg);
        return undefined;
      }
    }

    const requestFilters = selectAll ? {
      search: searchQuery.search,
      filter: searchQuery.filter,
      tagGroups: searchQuery.tagGroups,
    } : {
      search: getExpression(selection),
      filter: "",
      tagGroups: [],
    };

    return new Promise((resolve, reject) => {
      this.rejectPromise = reject;
      this.resolvePromise = resolve;

      if (selectedPrimary === 0) {
        const { id, backgroundId, variables } = values as Report;
        const printRequest: PrintRequest = {
          ...requestFilters,
          sorting: sort,
          report: id,
          overlay: backgroundId,
          variables: variables ? variables.reduce((prev: any, cur) => {
            prev[cur.name] = cur.value;
            return prev;
          }, {}) : {},
          createPreview,
        };

        return doPrint(rootEntity, printRequest);
      }

      const { id, variables, outputType } = values as ExportTemplate;

      const exportRequest: ExportRequest = {
        ...requestFilters,
        entityName: rootEntity,
        template: id,
        variables: variables ? variables.reduce((prev: any, cur) => {
          prev[cur.name] = cur.value;
          return prev;
        }, {}) : {},
        sorting: sort,
        exportToClipboard: this.isClipboardExport,
      };

      return doExport(exportRequest, outputType, this.isClipboardExport);
    });
  };

  onClose = () => {
    const {
      toggleExportDrawer, process, interruptProcess, submitting,
    } = this.props;

    if (submitting) {
      interruptProcess(process.processId);
      this.resolvePromise();
    }

    toggleExportDrawer();
  };

  onSubmitClick = e => {
    this.isClipboardExport = e.currentTarget.getAttribute("datatype") === "clipboard";
  };

  setSelectAll = (selectAll: boolean) => {
    this.setState({
      selectAll,
    });
  };

  renderPdfFields() {
    const {
     classes, overlays, pdfReports, values,
    } = this.props;

    const { selectedSecondary, createPreview } = this.state;

    const pdfActive: Report = pdfReports[selectedSecondary];

    const preview = values && values.preview;

    return (
      <>
        <Grid container>
          <Grid item xs={preview ? 8 : 12}>
            {pdfActive && pdfActive.description && (
              <Grid item xs={12} className="mb-2">
                <Typography variant="body2" color="inherit">
                  {pdfActive.description}
                </Typography>
              </Grid>
            )}
            <Grid item xs={12}>
              <Grid container>
                <Grid item xs={12}>
                  <FormField
                    type="select"
                    name="backgroundId"
                    label="Background"
                    placeholder="Blank"
                    selectAdornment={{
                      position: "end",
                      content: (
                        <MenuItem className="relative" key="upload">
                          <div className="heading centeredFlex" onClick={this.handleUploadBackgroundClick}>
                            <Publish />
                            {' '}
                            <span className="ml-1">Upload from disk</span>
                          </div>
                        </MenuItem>
                      ),
                    }}
                    fieldClasses={{
                      text: classes.text,
                      label: classes.customLabel,
                      placeholder: classes.placeholder,
                    }}
                    items={overlays || []}
                    allowEmpty
                    fullWidth
                  />
                </Grid>
                <FieldArray name="variables" component={this.templatesRenderer as any} />
              </Grid>
            </Grid>
          </Grid>
          {preview && (
            <Grid item xs={4} className={classes.previewWrapper}>
              <FilePreview data={preview} />
            </Grid>
          )}
        </Grid>

        {!preview && (
          <>
            <Grid item xs={12} container>
              <FormControlLabel
                control={(
                  <Checkbox
                    color="primary"
                    checked={createPreview}
                    className={clsx(classes.createPreviewCheckbox, classes.label)}
                    onClick={() => this.setState({ createPreview: !createPreview })}
                  />
                )}
                label="Create preview"
              />
            </Grid>
            <Grid item xs={12} container className={classes.label}>
              <Typography variant="caption" color="inherit">
                There is no preview for this report yet. Choose this option to create preview.
              </Typography>
            </Grid>
          </>
        )}
      </>
    );
  }

  templatesRenderer = ({ fields }) => {
    const { classes } = this.props;

    return fields.map((f, index) => {
      const item: Binding = fields.get(index);

      const isCheckbox = item.type === "Checkbox";

      const fieldProps: any = isCheckbox
        ? {
            classes: { label: classes.label },
            uncheckedClass: classes.label,
            color: "primary",
            stringValue: true,
          }
        : {
            fieldClasses: {
              text: classes.text,
              label: classes.customLabel,
              placeholder: classes.placeholder,
            },
            ...(item.type === "Date" ? { formatValue: YYYY_MM_DD_MINUSED } : {}),
            ...(item.type === "Money" ? { stringValue: true } : {}),
          };

      return (
        <Grid item xs={6} key={index}>
          <Field
            label={item.label}
            name={`${f}.value`}
            type={item.type}
            component={DataTypeRenderer}
            validate={validateSingleMandatoryField}
            fullWidth
            {...fieldProps}
          />
        </Grid>
      );
    });
  };

  renderTemplateFields() {
    const { values } = this.props;

    return (
      <Grid item xs={12}>
        <Grid container>
          <Grid item xs={12}>
            <Typography variant="body2" color="inherit">
              {values && values.description}
            </Typography>
          </Grid>
          <Grid item xs={4}>
            <FieldArray name="variables" component={this.templatesRenderer as any} />
          </Grid>
        </Grid>
      </Grid>
    );
  }

  render() {
    const {
      classes,
      selection,
      handleSubmit,
      submitting,
      invalid,
      AlertComponent,
      validating,
      sort,
      searchQuery,
      pdfReports,
      showExportDrawer,
      toggleExportDrawer,
      sidebarWidth,
      count,
      exportTemplatesFetching,
      pdfReportsFetching,
    } = this.props;

    const {
     selectedPrimary, selectedSecondary, selectAll, exportTemplateTypes,
    } = this.state;

    const pdfSelected = selectedPrimary === 0;

    const exportTemplateTypesArr = exportTemplateTypes ? Object.keys(exportTemplateTypes) : [];

    const templateSelected = selectedPrimary > 0;

    return (
      <Drawer
        anchor="bottom"
        open={showExportDrawer}
        onClose={toggleExportDrawer}
        classes={{ paper: classes.exportContainer }}
        PaperProps={{
          style: {
            left: window.innerWidth >= 1024 ? sidebarWidth : 0,
          },
        }}
      >
        <input
          type="file"
          ref={this.getFileInputNode}
          className={classes.fileInput}
          onChange={this.handleBackgroundUpload}
        />
        <Grid container className={classes.content}>
          <Grid container className={classes.header} wrap="nowrap" alignItems="center">
            <Grid item xs={2}>
              <Typography variant="body2" className={classes.headerText}>
                Share
              </Typography>
            </Grid>
            <Grid item xs className="centeredFlex">
              <SelectionSwitcher
                selectedRecords={selection.length}
                allRecords={count}
                selectAll={selectAll}
                setSelectAll={this.setSelectAll}
                disabled={submitting || validating}
              />

              <div className="flex-fill" />

              <IconButton className={classes.headerText} href={manualUrl} target="_blank">
                <Help />
              </IconButton>
            </Grid>
          </Grid>

          <Grid container className={classes.body} wrap="nowrap" spacing={3}>
            <Grid item zeroMinWidth className={classes.menuColumn}>
              <List disablePadding className={classes.list}>
                {Boolean(pdfReports.length) && (
                  <ListItem
                    button
                    classes={{
                      root: classes.listItems,
                      selected: classes.listItemsSelected,
                    }}
                    key={0}
                    selected={selectedPrimary === 0}
                    onClick={() => this.selectPrimary(0)}
                    disableRipple
                  >
                    <Typography variant="body2" color="inherit" classes={{ root: classes.listItemsText }}>
                      PDF
                    </Typography>

                    {selectedPrimary === 0 && <div className={classes.menuCorner} />}
                  </ListItem>
                )}
                {exportTemplateTypesArr.map((t, i) => (
                  <ListItem
                    button
                    classes={{
                      root: classes.listItems,
                      selected: classes.listItemsSelected,
                    }}
                    key={i + 1}
                    selected={selectedPrimary === i + 1}
                    onClick={() => this.selectPrimary(i + 1)}
                    disableRipple
                  >
                    <Typography variant="body2" color="inherit" classes={{ root: classes.listItemsText }}>
                      {t}
                    </Typography>

                    {selectedPrimary === i + 1 && <div className={classes.menuCorner} />}
                  </ListItem>
                ))}
              </List>
            </Grid>
            <Grid item zeroMinWidth className={classes.menuColumn}>
              <List disablePadding className={classes.list}>
                {pdfSelected
                  && pdfReports.map((i, index) => (
                    <ListItem
                      button
                      classes={{
                        root: classes.listItems,
                        selected: classes.listItemsSelected,
                      }}
                      key={index + i.name}
                      selected={selectedSecondary === index}
                      onClick={() => this.selectSecondary(index)}
                      disableGutters
                      disableRipple
                      style={{ position: "relative" }}
                    >
                      <Typography variant="body2" color="inherit" classes={{ root: classes.listItemsText }}>
                        {i.name}
                      </Typography>

                      {selectedSecondary === index && <div className={classes.menuCorner} />}
                    </ListItem>
                  ))}

                {templateSelected
                  && Boolean(exportTemplateTypesArr.length)
                  && exportTemplateTypes[exportTemplateTypesArr[selectedPrimary - 1]].map((t, index) => (
                    <ListItem
                      button
                      classes={{
                        root: classes.listItems,
                        selected: classes.listItemsSelected,
                      }}
                      key={index + t.name}
                      selected={selectedSecondary === index}
                      onClick={() => this.selectSecondary(index)}
                      disableGutters
                      disableRipple
                      style={{ position: "relative" }}
                    >
                      <Typography variant="body2" color="inherit" classes={{ root: classes.listItemsText }}>
                        {t.name}
                      </Typography>

                      {selectedSecondary === index && <div className={classes.menuCorner} />}
                    </ListItem>
                  ))}
              </List>
            </Grid>
            <Grid item xs className={classes.menuColumn}>
              <form autoComplete="off" onSubmit={handleSubmit(this.onSave)} className={classes.form}>
                <Grid container className={classes.formContent}>
                  {AlertComponent && (
                    <Grid item xs={12}>
                      <AlertComponent
                        selection={selection}
                        validating={validating}
                        selectAll={selectAll}
                        sort={sort}
                        searchQuery={searchQuery}
                      />
                    </Grid>
                  )}

                  {pdfSelected && this.renderPdfFields()}
                  {templateSelected && this.renderTemplateFields()}
                </Grid>

                <Grid item xs={12} className={classes.closeShareButtons}>
                  <Button className={classes.closeButton} onClick={this.onClose} variant="text">
                    Close
                  </Button>
                  {templateSelected
                  && ["Excel", "Text", "XML"].includes(exportTemplateTypesArr[selectedPrimary - 1])
                  && (
                    <Button
                      disabled={invalid || validating}
                      className={classes.closeButton}
                      type="submit"
                      datatype="clipboard"
                      variant="text"
                      onClick={this.onSubmitClick}
                      loading={submitting || validating || exportTemplatesFetching || pdfReportsFetching}
                    >
                      Copy to clipboard
                    </Button>
                  )}
                  <Button
                    disabled={invalid || validating}
                    rootClasses={classes.shareButton}
                    type="submit"
                    datatype="share"
                    onClick={this.onSubmitClick}
                    loading={submitting || validating || exportTemplatesFetching || pdfReportsFetching}
                  >
                    Share
                  </Button>
                </Grid>
              </form>
            </Grid>
          </Grid>
        </Grid>
        <PlayArrow className={window.innerWidth < 1024 ? classes.hide1024andDown : classes.corner} />
        {this.isContactList && (
          <ConfirmBase
            title=""
            confirmMessage={this.state.wrongPdfReportMsg}
            open={!!this.state.wrongPdfReportMsg}
            onCancel={this.hideWrongPdfReportDialog}
            cancelButtonText="OK"
          />
        )}
      </Drawer>
    );
  }
}

const mapStateToProps = (state: State) => ({
  values: getFormValues("ListShareForm")(state),
  pdfReports: state.share.pdfReports,
  pdfReportsFetching: state.share.pdfReportsFetching,
  exportTemplates: state.share.exportTemplates,
  exportTemplatesFetching: state.share.exportTemplatesFetching,
  validating: state.share.validating,
  overlays: state.share.overlays.map(i => ({ value: i.id, label: i.name })),
  processStatus: state.process.status,
  searchQuery: state.list.searchQuery,
  sort: state.list.records.sort,
  rows: state.list.records.rows,
  columns: state.list.records.columns,
  process: state.process,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getShareList: (entityName: string) => dispatch(getShareList(entityName)),
  getExportTemplates: (entityName: string) => dispatch(getExportTemplates(entityName)),
  addPrintOverlay: (fileName: string, overlay: File) => dispatch(addPrintOverlay(fileName, overlay)),
  doPrint: (rootEntity: string, printRequest: PrintRequest) => dispatch(doPrintRequest(rootEntity, printRequest)),
  doExport: (exportRequest: ExportRequest, outputType: OutputType, isClipboard) => dispatch(
    runExport(exportRequest, outputType, isClipboard),
  ),
  interruptProcess: (processId: string) => dispatch(interruptProcess(processId)),
});

export default reduxForm({
  form: "ListShareForm",
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(bottomDrawerStyles)(ShareForm))) as any;
