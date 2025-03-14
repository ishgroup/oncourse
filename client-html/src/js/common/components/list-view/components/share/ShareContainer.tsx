/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
  Binding,
  ExportRequest,
  ExportTemplate,
  OutputType,
  PrintRequest,
  Report,
  SearchQuery,
  Sorting,
} from '@api/model';
import { Help, Publish } from '@mui/icons-material';
import Delete from '@mui/icons-material/Delete';
import FullscreenIcon from '@mui/icons-material/Fullscreen';
import PlayArrow from '@mui/icons-material/PlayArrow';
import LoadingButton from '@mui/lab/LoadingButton';
import { Checkbox, CircularProgress, Grid, ListItemButton, MenuItem, Typography } from '@mui/material';
import Button from '@mui/material/Button';
import Drawer from '@mui/material/Drawer';
import FormControlLabel from '@mui/material/FormControlLabel';
import IconButton from '@mui/material/IconButton';
import List from '@mui/material/List';
import $t from '@t';
import clsx from 'clsx';
import { ConfirmBase, EditInPlaceField, FilePreview, getDocumentContent, YYYY_MM_DD_MINUSED } from 'ish-ui';
import * as React from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { change, Field, FieldArray, getFormValues, initialize, reduxForm, } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import { COMMON_PLACEHOLDER } from '../../../../../constants/Forms';
import {
  exportTemplateFullScreenPreview,
} from '../../../../../containers/automation/containers/export-templates/actions';
import ExportTemplatesService
  from '../../../../../containers/automation/containers/export-templates/services/ExportTemplatesService';
import { reportFullScreenPreview } from '../../../../../containers/automation/containers/pdf-reports/actions';
import PdfService from '../../../../../containers/automation/containers/pdf-reports/services/PdfService';
import { ContactType } from '../../../../../containers/entities/contacts/Contacts';
import { TemplateOutputDisplayName } from '../../../../../model/common/Share';
import { CommonListItem } from '../../../../../model/common/sidebar';
import { State } from '../../../../../reducers/state';
import { interruptProcess } from '../../../../actions';
import InstantFetchErrorHandler from '../../../../api/fetch-errors-handlers/InstantFetchErrorHandler';
import { ProcessState } from '../../../../reducers/processReducer';
import { getManualLink } from '../../../../utils/getManualLink';
import { LSGetItem, LSSetItem } from '../../../../utils/storage';
import { validateEmail, validateSingleMandatoryField } from '../../../../utils/validation';
import DataTypeRenderer from '../../../form/DataTypeRenderer';
import FormField from '../../../form/formFields/FormField';
import { getExpression } from '../../utils/listFiltersUtils';
import bottomDrawerStyles from '../bottomDrawerStyles';
import {
  addPrintOverlay,
  deleteExportTemplatePreview,
  deletePdfReportPreview,
  doPrintRequest,
  getExportTemplates,
  getShareList,
  runExport,
} from './actions';
import { LIST_SHARE_FORM_NAME } from './constants';
import SelectionSwitcher from './SelectionSwitcher';
import { getTemplateOutputDisplayName } from './utils';

type PdfReportType = ContactType | "GENERAL";

const LAST_SELECTED_EXPORT = "lastSelectedExport";

interface Props {
  rootEntity: string;
  showExportDrawer: boolean;
  toggleExportDrawer: () => void;
  selection: string[];
  count: number;
  process?: ProcessState;
  pdfReports: Report[];
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
  preview: string;
  loadingPreview: boolean;
  exportTemplateTypes: { [N in TemplateOutputDisplayName]?: ExportTemplate[] };
  createPreview: boolean;
  emailToSent: string;
  wrongPdfReportMsg?: string;
}

const manualUrl = getManualLink("importing-and-exporting");

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
      preview: null,
      loadingPreview: true,
      exportTemplateTypes: null,
      createPreview: false,
      wrongPdfReportMsg: null,
      emailToSent: null
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
      pdfReports, submitting, process, exportTemplates, pdfReportsFetching, exportTemplatesFetching, rootEntity,
    } = this.props;

    if (submitting && prevProps.process.processId && !process.processId) {
      this.resolvePromise();
    }

    if (prevProps.pdfReportsFetching && !pdfReportsFetching && pdfReports.length) {
      if (typeof prevState.selectedPrimary !== "number") {
        const savedState: ShareState = LSGetItem(LAST_SELECTED_EXPORT)
          && JSON.parse(LSGetItem(LAST_SELECTED_EXPORT))[rootEntity];

        if (savedState) {
          if (savedState.selectedPrimary === 0) {
            this.setState(savedState);
            const report = this.props.pdfReports[savedState.selectedSecondary];
            this.props.dispatch(initialize(LIST_SHARE_FORM_NAME, report));
            this.getCompressedPreview(report.id);
          }
        } else {
          this.setState({
            selectedPrimary: 0,
            selectedSecondary: 0,
          });
          const report = this.props.pdfReports[0];
          this.props.dispatch(initialize(LIST_SHARE_FORM_NAME, report));
          this.getCompressedPreview(report.id);
        }
      } else if (prevState.selectedPrimary === 0) {
        const report = this.props.pdfReports[prevState.selectedSecondary];
        this.props.dispatch(initialize(LIST_SHARE_FORM_NAME, report));
        this.getCompressedPreview(report.id);
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

        if (savedState && savedState.selectedPrimary > 0) {
          this.setState(savedState);
          const template  = exportTemplateTypes[Object.keys(exportTemplateTypes)[savedState.selectedPrimary - 1]][
            savedState.selectedSecondary
            ];
          this.props.dispatch(initialize(
            LIST_SHARE_FORM_NAME,
            template
          ));
          this.getCompressedPreview(null, template.id);
        }
      } else if (prevState.selectedPrimary > 0) {
        const template = exportTemplateTypes[Object.keys(exportTemplateTypes)[prevState.selectedPrimary - 1]][
          this.state.selectedSecondary
        ];
        this.props.dispatch(initialize(
          LIST_SHARE_FORM_NAME,
          template
        ));
        this.getCompressedPreview(null, template.id);
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
      const report = pdfReports[secondary];
      dispatch(initialize(LIST_SHARE_FORM_NAME, report));
      this.getCompressedPreview(report.id);
    }

    if (primary > 0 && exportTemplates.length) {
      const template = exportTemplateTypes[Object.keys(exportTemplateTypes)[primary - 1]][secondary];
      dispatch(
        initialize(LIST_SHARE_FORM_NAME, template),
      );
      this.getCompressedPreview(null, template.id);
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

  getCompressedPreview = async (pdfId, templateID?) => {
    const { dispatch } = this.props;

    try {
      this.setState({ loadingPreview: true, preview: null });
      let preview;

      if (typeof pdfId === "number") {
        preview = await PdfService.getLowQualityPreview(pdfId);
      }

      if (typeof templateID === "number") {
        preview = await ExportTemplatesService.getLowQualityPreview(templateID);
      }

      preview = await getDocumentContent(preview);

      this.setState({
        preview,
        loadingPreview: false
      });
    } catch (e) {
      this.setState({ loadingPreview: false });
      InstantFetchErrorHandler(dispatch, e, "Failed to get preview");
    }
  };

  handleBackgroundUpload = () => {
    const { addPrintOverlay } = this.props;

    const file = this.fileInputNode.files[0];

    addPrintOverlay(file && file.name.match(/^[^.]+/)[0], file);
  };

  handleUploadBackgroundClick = () => {
    this.props.dispatch(change(LIST_SHARE_FORM_NAME, "background", null));
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

  isExportTemplateSelected(selectedPrimary: number) {
    return selectedPrimary > 0;
  }

  isPdfReportSelected(selectedPrimary: number) {
    return selectedPrimary === 0;
  }

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

    const { selectedPrimary, selectAll, createPreview, emailToSent } = this.state;

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

    return new Promise<void>((resolve, reject) => {
      this.rejectPromise = () => {
        this.setState({
          createPreview: false
        });
        reject();
      };
      this.resolvePromise = () => {
        this.setState({
          createPreview: false
        });
        resolve();
      };

      if (selectedPrimary === 0) {
        const { id, backgroundId, variables } = values as Report;
        const printRequest: PrintRequest = {
          ...requestFilters,
          emailToSent,
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
        createPreview,
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

  handlePreviewAction = (exportAction, reportAction) => {
    const { pdfReports, dispatch } = this.props;
    const { selectedSecondary, selectedPrimary, exportTemplateTypes } = this.state;

    if (this.isExportTemplateSelected(selectedPrimary)) {
      const activeExportTemplate = exportTemplateTypes[Object.keys(exportTemplateTypes)[selectedPrimary - 1]][selectedSecondary];
      dispatch(exportAction(activeExportTemplate.id));
    } else {
      const activeReport = pdfReports[selectedSecondary];
      dispatch(reportAction(activeReport.id));
    }
  };

  handleFullScreenPreview = () => {
    this.handlePreviewAction(exportTemplateFullScreenPreview, reportFullScreenPreview);
  };

  deletePreview = () => {
    this.handlePreviewAction(deleteExportTemplatePreview, deletePdfReportPreview);
  };

  renderPdfFields(emailError) {
    const {
      classes, overlays, pdfReports,
    } = this.props;

    const { emailToSent, selectedSecondary, createPreview, preview, loadingPreview  } = this.state;

    const pdfActive: Report = pdfReports[selectedSecondary];

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
              <Grid container columnSpacing={3} rowSpacing={2}>
                <Grid item xs={12}>
                  <FormField
                    type="select"
                    name="backgroundId"
                    label={$t('background')}
                    placeholder={$t('blank')}
                    selectAdornment={{
                      position: "end",
                      content: (
                        <MenuItem className="relative w-100" key="upload" onClick={this.handleUploadBackgroundClick}>
                          <div className="heading centeredFlex">
                            <Publish/>
                            {' '}
                            <span className="ml-1">{$t('Upload from disk')}</span>
                          </div>
                        </MenuItem>
                      ),
                    }}
                    fieldClasses={{
                      text: classes.text,
                      label: classes.customLabel,
                    }}
                    items={overlays || []}
                    allowEmpty
                  />
                </Grid>
                <Grid item xs={12}>
                  
                  <EditInPlaceField
                    label={$t('send_result_on_email')}
                    placeholder={COMMON_PLACEHOLDER}
                    fieldClasses={{
                      text: classes.text,
                      label: classes.customLabel,
                    }}
                    input={{
                      onChange: e => this.setState({ emailToSent: e.target.value }),
                      value: emailToSent
                    }}
                    meta={{
                      error: emailError,
                      invalid: Boolean(emailError)
                    }}
                  />
                </Grid>
                <FieldArray name="variables" component={this.templatesRenderer as any}/>
              </Grid>
            </Grid>
          </Grid>

          {loadingPreview && <CircularProgress />}

          {!loadingPreview && preview && (
            <Grid item xs={4} className={classes.previewWrapper}>
              <FilePreview
                data={preview}
                actions={[
                  {
                    actionLabel: "Full size preview",
                    onAction: this.handleFullScreenPreview,
                    icon: <FullscreenIcon/>
                  },
                  {
                    actionLabel: "Delete preview",
                    onAction: this.deletePreview,
                    icon: <Delete/>
                  }
                ]}
              />
            </Grid>
          )}
        </Grid>

        {!loadingPreview && !preview && (
          <>
            <Grid item xs={12} container className="mt-2">
              <FormControlLabel
                control={(
                  <Checkbox
                    color="primary"
                    checked={createPreview}
                    className={clsx(classes.createPreviewCheckbox, classes.label)}
                    onClick={() => this.setState({ createPreview: !createPreview })}
                  />
                )}
                label={$t('create_preview')}
              />
            </Grid>
            <Grid item xs={12} container className={classes.label}>
              <Typography variant="caption" color="inherit">
                {$t('there_is_no_preview_for_this_report_yet_choose_thi')}
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
            label: classes.customLabel
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
            {...fieldProps}
          />
        </Grid>
      );
    });
  };

  renderTemplateFields() {
    const { classes, values } = this.props;

    const { createPreview, preview, loadingPreview } = this.state;

    return (
      <>
        <Grid item container xs={12}>
          <Grid xs={preview ? 8 : 12}>
            <Typography variant="body2" color="inherit">
              {values && values.description}
            </Typography>
            <Grid item container rowSpacing={2} columnSpacing={3} xs={12}>
              <FieldArray name="variables" component={this.templatesRenderer as any}/>
            </Grid>
          </Grid>
          {loadingPreview && <CircularProgress />}
          {!loadingPreview && preview && (
            <Grid item xs={4} className={classes.previewWrapper}>
              <FilePreview
                data={preview}
                actions={[
                  {
                    actionLabel: "Full size preview",
                    onAction: this.handleFullScreenPreview,
                    icon: <FullscreenIcon/>
                  },
                  {
                    actionLabel: "Delete preview",
                    onAction: this.deletePreview,
                    icon: <Delete/>
                  }
                ]}
              />
            </Grid>
          )}
        </Grid>
        {!loadingPreview && !preview && (
          <>
            <Grid item xs={12} container className="mt-2">
              <FormControlLabel
                control={(
                  <Checkbox
                    color="primary"
                    checked={createPreview}
                    className={clsx(classes.createPreviewCheckbox, classes.label)}
                    onClick={() => this.setState({ createPreview: !createPreview })}
                  />
                )}
                label={$t('create_preview')}
              />
            </Grid>
            <Grid item xs={12} container className={classes.label}>
              <Typography variant="caption" color="inherit">
                {$t('there_is_no_preview_for_this_report_yet_choose_thi')}
              </Typography>
            </Grid>
          </>
        )}
      </>
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
      emailToSent, selectedPrimary, selectedSecondary, selectAll, exportTemplateTypes,
    } = this.state;

    const pdfSelected = this.isPdfReportSelected(selectedPrimary);

    const exportTemplateTypesArr = exportTemplateTypes ? Object.keys(exportTemplateTypes) : [];

    const templateSelected = this.isExportTemplateSelected(selectedPrimary);

    const emailError = validateEmail(emailToSent);

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
                {$t('share')}
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

              <div className="flex-fill"/>

              <IconButton className={classes.headerText} href={manualUrl} target="_blank">
                <Help/>
              </IconButton>
            </Grid>
          </Grid>

          <Grid container className={classes.body} wrap="nowrap" spacing={3}>
            <Grid item zeroMinWidth className={classes.menuColumn}>
              <List disablePadding className={classes.list}>
                {Boolean(pdfReports.length) && (
                  <ListItemButton
                    classes={{
                      root: classes.listItems,
                      selected: classes.listItemsSelected,
                    }}
                    key={0}
                    selected={selectedPrimary === 0}
                    onClick={() => this.selectPrimary(0)}
                  >
                    <Typography variant="body2" color="inherit" classes={{ root: classes.listItemsText }}>
                      {$t('pdf')}
                    </Typography>

                    {selectedPrimary === 0 && <div className={classes.menuCorner}/>}
                  </ListItemButton>
                )}
                {exportTemplateTypesArr.map((t, i) => (
                  <ListItemButton
                    classes={{
                      root: classes.listItems,
                      selected: classes.listItemsSelected,
                    }}
                    key={i + 1}
                    selected={selectedPrimary === i + 1}
                    onClick={() => this.selectPrimary(i + 1)}
                  >
                    <Typography variant="body2" color="inherit" classes={{ root: classes.listItemsText }}>
                      {t}
                    </Typography>

                    {selectedPrimary === i + 1 && <div className={classes.menuCorner}/>}
                  </ListItemButton>
                ))}
              </List>
            </Grid>
            <Grid item zeroMinWidth className={classes.menuColumn}>
              <List disablePadding className={classes.list}>
                {pdfSelected
                  && pdfReports.map((i, index) => (
                    <ListItemButton
                      classes={{
                        root: classes.listItems,
                        selected: classes.listItemsSelected,
                      }}
                      key={index + i.name}
                      selected={selectedSecondary === index}
                      onClick={() => this.selectSecondary(index)}
                      disableGutters
                      style={{ position: "relative" }}
                    >
                      <Typography variant="body2" color="inherit" classes={{ root: classes.listItemsText }}>
                        {i.name}
                      </Typography>

                      {selectedSecondary === index && <div className={classes.menuCorner}/>}
                    </ListItemButton>
                  ))}

                {templateSelected
                  && Boolean(exportTemplateTypesArr.length)
                  && exportTemplateTypes[exportTemplateTypesArr[selectedPrimary - 1]].map((t, index) => (
                    <ListItemButton
                      classes={{
                        root: classes.listItems,
                        selected: classes.listItemsSelected,
                      }}
                      key={index + t.name}
                      selected={selectedSecondary === index}
                      onClick={() => this.selectSecondary(index)}
                      disableGutters
                      style={{ position: "relative" }}
                    >
                      <Typography variant="body2" color="inherit" classes={{ root: classes.listItemsText }}>
                        {t.name}
                      </Typography>

                      {selectedSecondary === index && <div className={classes.menuCorner}/>}
                    </ListItemButton>
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

                  {pdfSelected && this.renderPdfFields(emailError)}
                  {templateSelected && this.renderTemplateFields()}
                </Grid>

                <Grid item xs={12} className={classes.closeShareButtons}>
                  <Button className={classes.closeButton} onClick={this.onClose} variant="text">
                    {$t('close')}
                  </Button>
                  {templateSelected
                    && ["Excel", "Text", "XML"].includes(exportTemplateTypesArr[selectedPrimary - 1])
                    && (
                      <LoadingButton
                        disabled={invalid || validating}
                        className={classes.closeButton}
                        classes={{
                          loadingIndicator: "primaryColor"
                        }}
                        type="submit"
                        datatype="clipboard"
                        variant="text"
                        onClick={this.onSubmitClick}
                        loading={submitting || validating || exportTemplatesFetching || pdfReportsFetching}
                      >
                        {$t('copy_to_clipboard')}
                      </LoadingButton>
                    )}
                  <LoadingButton
                    disabled={invalid || validating || Boolean(emailError)}
                    className={classes.shareButton}
                    classes={{
                      loadingIndicator: "primaryColor"
                    }}
                    type="submit"
                    datatype="share"
                    variant="contained"
                    onClick={this.onSubmitClick}
                    loading={submitting || validating || exportTemplatesFetching || pdfReportsFetching}
                  >
                    {$t('share')}
                  </LoadingButton>
                </Grid>
              </form>
            </Grid>
          </Grid>
        </Grid>
        <PlayArrow className={window.innerWidth < 1024 ? classes.hide1024andDown : classes.corner}/>
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
  values: getFormValues(LIST_SHARE_FORM_NAME)(state),
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
  form: LIST_SHARE_FORM_NAME,
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(ShareForm, bottomDrawerStyles))) as any;