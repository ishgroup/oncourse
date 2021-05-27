/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import posed from "react-pose";
import {
 format as formatDate, getDaysInMonth, setDate, setMonth, setYear
} from "date-fns";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import {
  Grid,
  Typography,
  withStyles,
  Card,
  CardContent,
  Divider,
  FormControlLabel,
  FormGroup,
  IconButton,
  Tooltip,
  Collapse,
  CircularProgress,
  Hidden
} from "@material-ui/core";
import { ExpandMore, HelpOutline } from "@material-ui/icons";
import {
  arrayPush,
  arrayRemove,
  change,
  getFormValues,
  initialize,
  InjectedFormProps,
  reduxForm
} from "redux-form";
import clsx from "clsx";
import {
  AvetmissExportFee,
  AvetmissExportFlavour,
  AvetmissExportRequest,
  AvetmissExportSettings,
  FundingSource,
  FundingStatus,
  FundingUpload
} from "@api/model";
import ErrorMessage from "../../../common/components/form/fieldMessage/ErrorMessage";
import FormField from "../../../common/components/form/form-fields/FormField";
import CustomAppBar from "../../../common/components/layout/CustomAppBar";
import HamburgerMenu from "../../../common/components/layout/swipeable-sidebar/components/HamburgerMenu";
import { VARIANTS } from "../../../common/components/layout/swipeable-sidebar/utils";
import { State } from "../../../reducers/state";
import { StyledCheckbox } from "../../../common/components/form/form-fields/CheckboxField";
import Button from "../../../common/components/buttons/Button";
import AvetmissExportResults from "../components/AvetmissExportResults";
import {
  clearAvetmiss8ExportID,
  clearExportOutcomes,
  getActiveFundingContracts,
  getAvetmiss8ExportID,
  getAvetmiss8ExportOutcomesProcessID,
  getAvetmiss8ExportStatus,
  getAvetmiss8OutcomesStatus,
  getFundingUploads,
  updateFundingUpload
} from "../actions";
import { interruptProcess } from "../../../common/actions";
import { validateMinMaxDate } from "../../../common/utils/validation";
import { YYYY_MM_DD_MINUSED, III_DD_MMM_YYYY } from "../../../common/utils/dates/format";
import { getManualLink } from "../../../common/utils/getManualLink";
import AvetmissHistory from "../components/AvetmissHistory/AvetmissHistory";
import PreviousExportPanel from "../components/PreviousExportPanel/PreviousExportPanel";
import getAvetmissExportFormValues from "../utils/getAvetmissExportFormValues";
import { AppTheme } from "../../../model/common/Theme";

export const FORM: string = "AvetmissExportForm";

const styles: any = (theme: AppTheme) => ({
  divider: {
    margin: theme.spacing(3, -3)
  },
  stepsArrowContainer: {
    width: "65px",
    position: "relative",
    display: "flex",
    justifyContent: "center",
    minHeight: "90px"
  },
  stepsArrow: {
    height: "690px",
    width: "8px",
    marginBottom: "36px",
    background: theme.palette.primary.main
  },
  stepsArrowHead: {
    borderStyle: "solid",
    borderWidth: "40px 12px 0 12px",
    borderColor: `${theme.palette.primary.main} transparent transparent transparent`
  },
  stepWrapper: {
    padding: "5px",
    position: "absolute",
    width: "100%",
    zIndex: 1,
    top: "-5px"
  },
  step: {
    background: theme.palette.primary.main,
    color: theme.palette.primary.contrastText,
    padding: "2px",
    borderRadius: "15px",
    textAlign: "center"
  },
  resultsHeader: {
    position: "absolute",
    left: theme.spacing(-2),
    top: "-110px",
    visibility: "visible"
  },
  arrowLine: {
    width: "8px",
    position: "absolute",
    height: "100%",
    background: theme.palette.primary.main
  },
  settingsWrapper: {
    "&:last-child": {
      paddingBottom: theme.spacing(1)
    }
  },
  exportHeadersContainer: {
    height: theme.spacing(3)
  },
  recordIcon: {
    fontSize: "0.875rem",
    verticalAlign: "middle",
    color: theme.palette.secondary.main
  },
  recordContainer: {
    textAlign: "left",
    display: "inline-block"
  },
  indicator: {
    position: "absolute",
    left: "50%",
    top: "50%",
    transform: "translate(-50%, -50%)"
  },
  hidden: {
    visibility: "hidden",
    height: 0
  }
});

const Box = posed.div({
  static: {
    width: "auto",
    height: "auto"
  },
  moved: {
    position: "absolute",
    opacity: 0,
    height: ({ target }) => target && target.getBoundingClientRect().height,
    width: ({ target }) => target && target.getBoundingClientRect().width,
    left: ({ target }) => target && target.getBoundingClientRect().left,
    top: ({ target, element }) => {
      if (target) {
        if (window.innerWidth < 1280) {
          return target.offsetTop - element.clientHeight;
        }
        return target.offsetTop;
      }
    },
    transition: {
      height: {
        duration: 0
      },
      default: {
        duration: 300,
        ease: "easeInOut"
      },
      opacity: {
        duration: 600
      }
    },
    flip: true
  }
});

const getModel = val =>
  Object.keys(val)
    .filter(i => isNaN(Number(i)))
    .map(i => ({
      label: i,
      value: i
    }));

const flavourModel = getModel(AvetmissExportFlavour);

const today = new Date();

const todayMonth = today.getMonth();

const formated = setMonth(new Date(), todayMonth);

const manualUrl = getManualLink("AVETMISS");

const openManual = () => {
  window.open(manualUrl);
};

// Australian quarters
const getCurrentQuarter = () => {
  const todayWithZeroTime = new Date();
  todayWithZeroTime.setHours(0, 0, 0, 0);

  let quarter = null;
  const quarters = new Map();
  quarters.set(1, {
    start: new Date(todayWithZeroTime.getFullYear(), 6, 1),
    end: new Date(todayWithZeroTime.getFullYear(), 8, 30),
    prevStart: new Date(todayWithZeroTime.getFullYear(), 3, 1),
    prevEnd: new Date(todayWithZeroTime.getFullYear(), 5, 30)
  });
  quarters.set(2, {
    start: new Date(todayWithZeroTime.getFullYear(), 9, 1),
    end: new Date(todayWithZeroTime.getFullYear(), 11, 31),
    prevStart: new Date(todayWithZeroTime.getFullYear(), 6, 1),
    prevEnd: new Date(todayWithZeroTime.getFullYear(), 8, 30)
  });
  quarters.set(3, {
    start: new Date(todayWithZeroTime.getFullYear(), 0, 1),
    end: new Date(todayWithZeroTime.getFullYear(), 2, 31),
    prevStart: new Date(todayWithZeroTime.getFullYear() - 1, 9, 1),
    prevEnd: new Date(todayWithZeroTime.getFullYear() - 1, 11, 31)
  });
  quarters.set(4, {
    start: new Date(todayWithZeroTime.getFullYear(), 3, 1),
    end: new Date(todayWithZeroTime.getFullYear(), 5, 30),
    prevStart: new Date(todayWithZeroTime.getFullYear(), 0, 1),
    prevEnd: new Date(todayWithZeroTime.getFullYear(), 2, 31)
  });
  for (let i = 1; i <= 4; i++) {
    const value = quarters.get(i);
    if (todayWithZeroTime >= value.start && todayWithZeroTime <= value.end) {
      quarter = value;
    }
  }
  return quarter;
};

const currentQuarter = getCurrentQuarter();

const datesValues = [
  "Commenced outcomes",
  `${formatDate(new Date(today.getFullYear(), 0, 1), III_DD_MMM_YYYY)} - ${formatDate(
    setDate(formated, getDaysInMonth(formated)),
    III_DD_MMM_YYYY
  )}`,
  `${formatDate(currentQuarter.prevStart, III_DD_MMM_YYYY)} - ${formatDate(currentQuarter.prevEnd, III_DD_MMM_YYYY)}`,
  `${setYear(today, today.getFullYear() - 1).getFullYear()}`,
  "Custom date range"
];

const dateRangeModel = datesValues.map(i => ({
  label: i,
  value: i
}));

const feeModel = Object.keys(AvetmissExportFee);

const feeModelCollapsed = feeModel.filter(
  i => isNaN(Number(i)) && i !== "Fee for service VET (non-funded)" && i !== "Non VET"
);

const setDates = (v, dispatch) => {
  switch (v) {
    case datesValues[0]: {
      dispatch(change(FORM, "outcomesStart", null));
      dispatch(change(FORM, "outcomesEnd", null));
      return;
    }
    case datesValues[1]: {
      dispatch(change(FORM, "outcomesStart", formatDate(new Date(`01/01/${today.getFullYear()}`), YYYY_MM_DD_MINUSED)));
      dispatch(
        change(
          FORM,
          "outcomesEnd",
          formatDate(new Date(today.getFullYear(), formated.getMonth(), getDaysInMonth(formated)), YYYY_MM_DD_MINUSED)
        )
      );
      return;
    }
    case datesValues[2]: {
      dispatch(change(FORM, "outcomesStart", formatDate(currentQuarter.prevStart, YYYY_MM_DD_MINUSED)));
      dispatch(change(FORM, "outcomesEnd", formatDate(currentQuarter.prevEnd, YYYY_MM_DD_MINUSED)));
      return;
    }
    case datesValues[3]: {
      dispatch(
        change(
          FORM,
          "outcomesStart",
          formatDate(new Date(`01/01/${setYear(today, today.getFullYear() - 1).getFullYear()}`), YYYY_MM_DD_MINUSED)
        )
      );
      dispatch(change(FORM, "outcomesEnd", formatDate(new Date(`01/01/${today.getFullYear()}`), YYYY_MM_DD_MINUSED)));
      return;
    }
    case datesValues[4]: {
      dispatch(change(FORM, "outcomesStart", formatDate(new Date(`01/01/${today.getFullYear()}`), YYYY_MM_DD_MINUSED)));
      dispatch(change(FORM, "outcomesEnd", formatDate(new Date(), YYYY_MM_DD_MINUSED)));
    }
  }
};

const setDefaultIncluded = (v, prev, dispatch, contracts, all) => {
  const isStandard = v === "NCVER (Standard AVETMISS)";

  if (isStandard && prev !== "NCVER (Standard AVETMISS)" && all.dateRange === "Commenced outcomes") {
    dispatch(change(FORM, "dateRange", null));
  }

  dispatch(change(FORM, "fee", isStandard ? feeModel.filter(i => i !== "Non VET") : []));

  const contractsToSelect = contracts.filter(value => value.flavour === v).map(value => value.id) || [];
  dispatch(change(FORM, "fundingContracts", contractsToSelect));
};

interface Props {
  values?: any;
  enrolmentsCount?: number;
  data?: any;
  dispatch?: Dispatch;
  classes?: any;
  outcomes?: any;
  exportID?: string;
  outcomesID?: string;
  resetOutcomes?: () => void;
  clearAvetmiss8ExportID?: () => void;
  processExport?: (processId: string) => void;
  processOutcomes?: (outcomesId: string) => void;
  interruptProcess?: (processId: string) => void;
  settings?: AvetmissExportSettings;
  getExportOutcomesID?: (settings: AvetmissExportSettings) => void;
  getAvetmiss8ExportID?: (requestParameters: AvetmissExportRequest) => void;
  getFundingUploads?: () => void;
  uploads?: FundingUpload[];
  updateFundingUpload?: (id: number, status: FundingStatus) => void;
  getFundingContracts?: () => void;
  contracts?: FundingSource[];
  updateSettings?: (settings: AvetmissExportSettings) => void;
  onClose?: () => void;
  formatSettings?: (settings: AvetmissExportSettings) => AvetmissExportSettings;
}

const avetmissStateInitial = {
  hasNoResults: false,
  pending: false,
  showUploads: true,
  previousExportStatusSetted: false,
  skipAnimation: false,
  showEnrolmentsCount: false
};

const getEndDateWarningMsg = (date: string) => {
  if (!date) return "";
  const today = new Date();
  const endDate = new Date(date);
  return (endDate > today) ? "Setting the end date in the future will usually result in bad data." : "";
};

class AvetmissExportForm extends React.PureComponent<Props & InjectedFormProps, any> {
  private firstUploadNode;

  private prevExportDetails;

  state = { ...avetmissStateInitial };

  componentDidMount() {
    this.props.getFundingContracts();
    this.props.getFundingUploads();

    if (typeof this.props.enrolmentsCount === "number") {
      this.setState({
        showEnrolmentsCount: true
      });
    }
  }

  componentWillUnmount() {
    this.reset();
  }

  componentDidUpdate(prevProps: Props) {
    const {
      resetOutcomes, processExport, exportID, processOutcomes, outcomesID, uploads
    } = this.props;

    if (!prevProps.uploads && uploads) {
      if (uploads.length === 0 || uploads[0].status !== FundingStatus.unknown) {
        this.setState({
          showUploads: false,
          previousExportStatusSetted: true,
          skipAnimation: true
        });
      } else {
        this.setState({
          showUploads: true,
          previousExportStatusSetted: this.state.showEnrolmentsCount,
          skipAnimation: false
        });
      }
    }

    if (!prevProps.exportID && exportID) {
      processExport(exportID);
    }

    if ((!prevProps.outcomesID && outcomesID) || (outcomesID && prevProps.outcomesID !== outcomesID)) {
      processOutcomes(outcomesID);
    }

    if (this.state.pending && !prevProps.outcomes && this.props.outcomes) {
      this.setState({
        pending: false
      });
    }

    if (this.state.pending && prevProps.exportID && !this.props.exportID) {
      this.setState({
        pending: false,
        previousExportStatusSetted: false
      });
    }

    if (!prevProps.outcomes && this.props.outcomes && !this.props.outcomes.length) {
      this.setState({
        hasNoResults: true
      });

      resetOutcomes();
      return;
    }

    if (this.state.hasNoResults && JSON.stringify(prevProps.values) !== JSON.stringify(this.props.values)) {
      this.setState({
        hasNoResults: false
      });
    }
  }

  setFirstUploadNode = node => {
    this.firstUploadNode = node;
  };

  onFind = settings => {
    const { formatSettings } = this.props;
    const { showEnrolmentsCount } = this.state;

    this.setState({
      pending: true
    });

    const settingsObj = JSON.parse(JSON.stringify(settings));

    const request = formatSettings && showEnrolmentsCount ? formatSettings(settingsObj) : settingsObj;

    delete request.dateRange;
    delete request.defaultStatus;
    delete request.noAssessment;

    this.props.getExportOutcomesID(request);
  };

  onExport = params => {
    const { showEnrolmentsCount } = this.state;
    const { formatSettings } = this.props;

    this.setState({
      pending: true
    });

    const request = JSON.parse(JSON.stringify(params));
    delete request.settings.dateRange;

    if (showEnrolmentsCount) {
      request.settings = formatSettings(request.settings);
    }

    this.props.getAvetmiss8ExportID(request);
  };

  onFlavourChange = (e, v, prev) =>
    setDefaultIncluded(v, prev, this.props.dispatch, this.props.contracts, this.props.values);

  onDateRangeChange = (e, v) => setDates(v, this.props.dispatch);

  onContractChange = (e, v, con) => {
    if (v) {
      this.props.dispatch(arrayPush(FORM, "fundingContracts", con.id));
    } else {
      const newArray = this.props.values.fundingContracts.filter(v => v !== con.id);
      this.props.dispatch(change(FORM, "fundingContracts", newArray));
    }
  };

  onFeeChange = (e, v) => {
    if (v) {
      this.props.dispatch(
        change(FORM, "fee", [
          "Fee for service VET (non-funded)",
          ...feeModelCollapsed,
          ...(this.props.values.fee.findIndex(f => f === "Non VET") === -1 ? [] : ["Non VET"])
        ])
      );

      return;
    }

    this.props.dispatch(
      change(FORM, "fee", [...(this.props.values.fee.findIndex(f => f === "Non VET") === -1 ? [] : ["Non VET"])])
    );
  };

  onFeeItemChange = (e, v, i) => {
    if (v) {
      this.props.dispatch(arrayPush(FORM, "fee", i));
      return;
    }
    this.props.dispatch(
      arrayRemove(
        FORM,
        "fee",
        this.props.values.fee.findIndex(f => f === i)
      )
    );
  };

  onNonVetChange = (e, v) => {
    if (v) {
      this.props.dispatch(arrayPush(FORM, "fee", "Non VET"));
      return;
    }
    this.props.dispatch(
      arrayRemove(
        FORM,
        "fee",
        this.props.values.fee.findIndex(f => f === "Non VET")
      )
    );
  };

  reset = () => {
    const {
 exportID, outcomesID, resetOutcomes, clearAvetmiss8ExportID, interruptProcess, outcomes
} = this.props;

    const { pending } = this.state;

    if (outcomes) {
      resetOutcomes();
    }

    if (pending) {
      this.setState({
        pending: false
      });
    }

    if (exportID) {
      clearAvetmiss8ExportID();
      interruptProcess(exportID);
    }

    if (outcomesID) {
      clearAvetmiss8ExportID();
      interruptProcess(outcomesID);
    }
  };

  validateMaxDate = (value, allValues) => validateMinMaxDate(value, "", allValues.outcomesEnd);

  validateMinDate = (value, allValues) => validateMinMaxDate(value, allValues.outcomesStart, "");

  uploadStatusUpdated = (id: number, status: FundingStatus) => {
    this.prevExportDetails = { id, status };

    this.runAnimation();
  };

  runAnimation = () => {
    this.setState({
      previousExportStatusSetted: true
    });
  };

  onRunAgainClicked = (settings: AvetmissExportSettings) => {
    const { updateSettings } = this.props;

    updateSettings(settings);

    if (this.state.showEnrolmentsCount) {
      this.setState({
        showEnrolmentsCount: false
      });
    }
  };

  showUploads = () => {
    const { updateFundingUpload } = this.props;

    if (this.prevExportDetails) {
      const { id, status } = this.prevExportDetails;

      updateFundingUpload(id, status);
      this.prevExportDetails = null;
    }

    if (this.state.previousExportStatusSetted) {
      this.setState({
        showUploads: false
      });
    }
  };

  render() {
    const {
      classes,
      handleSubmit,
      values,
      dispatch,
      outcomes,
      invalid,
      exportID,
      uploads,
      updateFundingUpload,
      contracts,
      onClose,
      enrolmentsCount
    } = this.props;

    const {
      hasNoResults,
      pending,
      showUploads,
      previousExportStatusSetted,
      skipAnimation,
      showEnrolmentsCount
    } = this.state;

    const hasOutcomesOrExport = Boolean(exportID || (outcomes && outcomes.length));

    let checkboxesValid = false;
    let feeForServiceChecked = false;

    if (values !== undefined) {
      checkboxesValid = values.fee.length > 0 || values.fundingContracts;
      feeForServiceChecked = Boolean(values.fee.find(f => f === "Fee for service VET (non-funded)"));
    }

    const flavourField = (
      <FormField
        type="select"
        name="flavour"
        label="Flavour"
        formatting="primary"
        items={flavourModel}
        onChange={this.onFlavourChange}
        reqired
      />
    );

    const endDateWarning = getEndDateWarningMsg(values && values.outcomesEnd);

    return (
      <form className="container" onSubmit={handleSubmit(this.onFind)}>
        <CustomAppBar fullWidth>
          <HamburgerMenu variant={VARIANTS.temporary} />
          <Grid container>
            <Grid item xs={12} className="centeredFlex">
              <Typography variant="h6" color="inherit" noWrap>
                AVETMISS 8
              </Typography>

              <div className="flex-fill" />

              <Tooltip title="Additional information">
                <IconButton onClick={openManual}>
                  <HelpOutline className="text-white" />
                </IconButton>
              </Tooltip>
              {onClose && (
                <Button onClick={onClose} className="whiteAppBarButton">
                  Close
                </Button>
              )}
            </Grid>
          </Grid>
        </CustomAppBar>

        {values && (
          <Grid container spacing={2}>
            {uploads && uploads.length > 0 && !skipAnimation && (
              <Grid item xs={12} lg={8}>
                <Box
                  pose={previousExportStatusSetted ? "moved" : "static"}
                  target={this.firstUploadNode}
                  onPoseComplete={this.showUploads}
                >
                  <PreviousExportPanel
                    hideHeader={previousExportStatusSetted}
                    classes={classes}
                    onSubmit={this.uploadStatusUpdated}
                    item={uploads ? uploads[0] : {}}
                  />
                </Box>
              </Grid>
            )}

            <Hidden xsUp={showUploads}>
              <Grid item xs={12} lg={8}>
                <Card onClick={hasOutcomesOrExport ? this.reset : undefined}>
                  <CardContent
                    className={clsx("mb-0 pl-2", classes.settingsWrapper, {
                      "cursor-pointer": hasOutcomesOrExport
                    })}
                  >
                    <Typography
                      color="inherit"
                      component="div"
                      className={clsx("heading mt-1 centeredFlex pl-2", {
                        "mb-2": hasOutcomesOrExport
                      })}
                    >
                      Select
                      {hasOutcomesOrExport && (
                        <>
                          <div className="flex-fill" />
                          {' '}
                          <ExpandMore />
                        </>
                      )}
                    </Typography>
                    {!hasOutcomesOrExport && <Divider className={classes.divider} />}

                    <Collapse in={!hasOutcomesOrExport}>
                      <div
                        className={clsx("d-flex pl-2 pb-2", {
                          "invisible": hasOutcomesOrExport
                        })}
                      >
                        {showEnrolmentsCount ? (
                          <div>
                            {flavourField}
                            <Typography gutterBottom variant="caption">
                              Exporting
                              {' '}
                              {enrolmentsCount}
                              {' '}
                              enrolment
                              {enrolmentsCount !== 1 ? "s" : ""}
                              ...
                              {" "}
                            </Typography>
                          </div>
                        ) : (
                          <>
                            <div className="flex-fill">
                              {flavourField}
                              <FormField
                                type="select"
                                name="dateRange"
                                label="Outcomes in progress during"
                                formatting="primary"
                                items={
                                  values.flavour === "NCVER (Standard AVETMISS)"
                                    ? dateRangeModel.slice(1)
                                    : dateRangeModel
                                }
                                onChange={this.onDateRangeChange}
                                required
                              />

                              <Collapse in={values.dateRange === "Custom date range"}>
                                <div>
                                  <FormField
                                    type="date"
                                    name="outcomesStart"
                                    label="Start"
                                    maxDate={values.outcomesEnd}
                                    validate={this.validateMaxDate}
                                    className="pr-2"
                                  />
                                  <div>
                                    <FormField
                                      type="date"
                                      name="outcomesEnd"
                                      label="End"
                                      minDate={values.outcomesStart}
                                      validate={this.validateMinDate}
                                    />
                                    {endDateWarning && <ErrorMessage message={endDateWarning} />}
                                  </div>
                                </div>
                              </Collapse>

                              <FormControlLabel
                                classes={{
                                  root: "checkbox"
                                }}
                                control={
                                  <FormField type="checkbox" name="includeLinkedOutcomes" color="primary" />
                                }
                                label="Include linked outcomes"
                              />

                              <Typography variant="caption" component="div" className="pr-2">
                                Outcomes outside the date range will be included if linked to the same enrolment or if they have the same Purchasing contract identifier.
                              </Typography>
                            </div>
                            <div className="flex-fill ml-2">
                              <FormGroup>
                                {contracts
                                  && contracts.map(con => (
                                    <FormControlLabel
                                      classes={{
                                        root: "checkbox"
                                      }}
                                      key={con.id}
                                      control={(
                                        <StyledCheckbox
                                          color="primary"
                                          checked={values.fundingContracts.some(v => v === con.id)}
                                          onChange={(e, v) => this.onContractChange(e, v, con)}
                                        />
                                      )}
                                      label={con.name}
                                    />
                                  ))}
                              </FormGroup>

                              <FormControlLabel
                                classes={{
                                  root: "checkbox"
                                }}
                                control={(
                                  <StyledCheckbox
                                    color="primary"
                                    checked={feeForServiceChecked}
                                    onChange={this.onFeeChange}
                                  />
                                )}
                                label="No funding contract (Fee for service VET)"
                              />

                              <Collapse in={feeForServiceChecked}>
                                <FormGroup className="ml-2">
                                  {feeModelCollapsed.map(i => (
                                    <FormControlLabel
                                      key={i}
                                      classes={{
                                        root: "checkbox"
                                      }}
                                      control={(
                                        <StyledCheckbox
                                          color="primary"
                                          checked={Boolean(values.fee.find(f => f === i))}
                                          onChange={(e, v) => this.onFeeItemChange(e, v, i)}
                                        />
                                      )}
                                      label={i}
                                    />
                                  ))}
                                </FormGroup>
                              </Collapse>
                              {!checkboxesValid && (
                                <Typography variant="body1" color="error">
                                  Please select one or more options.
                                </Typography>
                              )}
                            </div>
                          </>
                        )}
                      </div>
                    </Collapse>
                  </CardContent>
                </Card>

                <Card className="flex-column justify-content-center mt-3">
                  {!hasOutcomesOrExport && (
                    <div className="centeredFlex justify-content-center pt-3 pb-3">
                      {!hasNoResults && (
                        <Button
                          text="Find"
                          color="primary"
                          type="submit"
                          disabled={invalid || !checkboxesValid}
                          loading={pending}
                          rootClasses="avetmissButton"
                        />
                      )}

                      {hasNoResults && (
                        <Typography variant="body1" color="error">
                          No outcomes match your criteria above
                        </Typography>
                      )}
                    </div>
                  )}

                  <Collapse in={hasOutcomesOrExport}>
                    <CardContent className="pt-3">
                      {hasOutcomesOrExport && (
                        <AvetmissExportResults
                          classes={classes}
                          outcomes={outcomes}
                          values={values}
                          dispatch={dispatch}
                          onExport={this.onExport}
                          pending={pending}
                        />
                      )}
                    </CardContent>
                  </Collapse>
                </Card>
              </Grid>
            </Hidden>
            <Grid item lg={4} xs={12}>
              {uploads && uploads.length > 0 && (
                <AvetmissHistory
                  classes={classes}
                  items={uploads}
                  setFirstUploadNode={this.setFirstUploadNode}
                  skipAnimation={skipAnimation}
                  previousExportStatusSetted={previousExportStatusSetted}
                  onStatusChange={updateFundingUpload}
                  onRunAgainClicked={this.onRunAgainClicked}
                />
              )}
            </Grid>
          </Grid>
        )}
        <div className={clsx(classes.indicator, values && "d-none")}>
          <CircularProgress size={40} thickness={5} />
        </div>
      </form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  values: getFormValues(FORM)(state),
  outcomes: state.export.outcomes,
  exportID: state.export.exportID,
  outcomesID: state.export.outcomesID,
  settings: state.export.settings,
  uploads: state.export.uploads,
  contracts: state.export.contracts
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getExportOutcomesID: (settings: AvetmissExportSettings) => dispatch(getAvetmiss8ExportOutcomesProcessID(settings)),
  resetOutcomes: () => dispatch(clearExportOutcomes()),
  clearAvetmiss8ExportID: () => dispatch(clearAvetmiss8ExportID()),
  getAvetmiss8ExportID: (params: AvetmissExportRequest) => dispatch(getAvetmiss8ExportID(params)),
  processExport: (processId: string) => dispatch(getAvetmiss8ExportStatus(processId)),
  processOutcomes: (outcomesID: string) => dispatch(getAvetmiss8OutcomesStatus(outcomesID)),
  interruptProcess: (processId: string) => dispatch(interruptProcess(processId)),
  getFundingUploads: () => dispatch(getFundingUploads()),
  updateFundingUpload: (id: number, status: FundingStatus) => dispatch(updateFundingUpload(id, status)),
  getFundingContracts: () => dispatch(getActiveFundingContracts()),
  updateSettings: (s: AvetmissExportSettings) => {
    const settings = getAvetmissExportFormValues(s);
    dispatch(initialize(FORM, settings));
  }
});

export default reduxForm<any, Props>({
  form: FORM
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(AvetmissExportForm)));
