/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import {
  Card, Chip, Grid, Tooltip, Typography
} from "@mui/material";
import React, {
 useCallback, useEffect, useMemo, useState
} from "react";
import { change } from "redux-form";
import { connect } from "react-redux";
import clsx from "clsx";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from '@mui/icons-material/Delete';
import { makeStyles } from "@mui/styles";
import {
 DeliveryMode, FundingUpload, Module, Outcome, OutcomeStatus
} from "@api/model";
import instantFetchErrorHandler from "../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import FormField from "../../../../common/components/form/formFields/FormField";
import FundingUploadComponent from "../../../../common/components/form/FundingUploadComponent";
import { AccessState } from "../../../../common/reducers/accessReducer";
import { mapSelectItems } from "../../../../common/utils/common";
import {
  validateFundingSourse,
  validateMinMaxDate,
  validatePurchasingContractScheduleIdentifier,
  validateSingleMandatoryField,
  validateSpecificProgramIdentifier,
  validateVetPurchasingContractIdentifier
} from "../../../../common/utils/validation";
import Uneditable from "../../../../common/components/form/formFields/Uneditable";
import { HeaderContactTitle, LinkAdornment } from "../../../../../ish-ui/formFields/FieldAdornments";
import { fundingUploadsPath } from "../../../../constants/Api";
import FundingUploadService from "../../../avetmiss-export/services/FundingUploadService";
import { openModuleLink } from "../../modules/utils";
import { State } from "../../../../reducers/state";
import { EditViewProps } from "../../../../model/common/ListView";
import { normalizeNumberToZero } from "../../../../common/utils/numbers/numbersNormalizing";
import { AppTheme } from "../../../../../ish-ui/model/Theme";
import { AssessmentChart, AttendanceChart } from "./OutcomeProgressionChart";
import FullScreenStickyHeader
  from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";
import { StringKeyObject } from "../../../../model/common/CommomObjects";
import EntityService from "../../../../common/services/EntityService";
import { fundingSourceValues } from "../../courseClasses/constants";

interface OutcomeEditFieldsProps extends EditViewProps<Outcome> {
  modules?: any[];
  modulesLoading?: boolean;
  modulesRowsCount?: number;
  setModuleSearch?: any;
  clearModuleSearch?: any;
  getModules?: any;
  className?: string;
  isPriorLearningBinded?: boolean;
  priorLearningEditView?: boolean;
  getFieldName: (name: keyof Outcome) => string;
  clearModules?: any;
  access?: AccessState;
  noHeader?: boolean;
}

const deliveryModeValues = Object.keys(DeliveryMode).map(mapSelectItems);

const parseIntValue = v => (v ? parseInt(v, 10) : v);

const validateMaxDate = (value, allValues) => validateMinMaxDate(
  value, "", allValues.endDate, "", "Outcome start date can not be after the end date"
);

const validateMinDate = (value, allValues) => validateMinMaxDate(
  value, allValues.startDate, "", "Outcome end date can not be before the start date"
);

let isPriorLearning;

const validateStartDate = (value, allValues) => {
  let result: string = null;
  if (allValues.startDateOverridden || isPriorLearning) {
    result = validateSingleMandatoryField(value);
  }
  return result || ((isPriorLearning || allValues.startDateOverridden) && validateMaxDate(value, allValues));
};

const validateEndtDate = (value, allValues) => {
  let result: string = null;
  if (allValues.endDateOverridden || isPriorLearning) {
    result = validateSingleMandatoryField(value);
  }
  return result || ((isPriorLearning || allValues.endDateOverridden) && validateMinDate(value, allValues));
};

const useStyles = makeStyles((theme: AppTheme) => ({
  card: {
    margin: "14px 0 14px 0",
    width: "100%",
    padding: "20px",
    minWidth: "none",
  },
  dateWrapper: {
    display: "flex",
    alignItems: "center",
  },
  lockIcon: {
    marginRight: "5px",
  },
  header: {
    color: theme.share.color.itemText,
    fontSize: "12px",
    margin: "10px 0",
  },
  width240: {
    width: "240px",
  },
  buttonWrapper: {
    display: "flex",
    alignItems: "center",
    height: "40px",
  },
  chip: {
    minWidth: "8em",
    height: "26px",
  },
  tooltip: {
    marginTop: theme.spacing(-2)
  },
  label: {
    color: theme.palette.text.secondary,
    fontSize: "12px",
    margin: 0,
  }
}));

const MODULES_WARNING = "This module doesn’t match any of the modules used in associated class. Default class start/end dates will be used as Training plan dates";

const OutcomeEditFields = React.memo<OutcomeEditFieldsProps>(props => {
  const {
    twoColumn,
    values,
    form,
    dispatch,
    getFieldName,
    className,
    isPriorLearningBinded,
    isNew,
    access,
    priorLearningEditView,
    noHeader
  } = props;

  const classes = useStyles();

  const [fundingUploads, setFundingUploads] = useState<FundingUpload[]>([]);
  const [warnings, setWarnings] = useState<StringKeyObject>({});

  isPriorLearning = isPriorLearningBinded;

  const fundingUploadAccess = access[fundingUploadsPath] && access[fundingUploadsPath]["GET"];

  useEffect(() => {
    if (values.id && fundingUploadAccess) {
      FundingUploadService
        .getFundingUploads(`fundingUploadOutcomes.outcome.id is ${values.id}`)
        .then(res => {
          setFundingUploads(res);
        })
        .catch(er => {
          setFundingUploads([]);
          instantFetchErrorHandler(dispatch, er);
        });
    }
  }, [values.id, access]);

  const setModuleWarnings = async () => {
    try {
      const courseId = await EntityService.getPlainRecords("Enrolment", "courseClass.course.id", `id is ${values.enrolmentId}`, 1)
        .then(r => r.rows[0]?.values[0]);

      const moduleIdsRes = await EntityService.getPlainRecords("Course", "courseModules.module.id", `id is ${courseId}`, 1);

      const moduleIds = moduleIdsRes.rows.length ? JSON.parse(moduleIdsRes.rows[0].values[0]) : null;
      
      const warningsUpdated = {};

      if (moduleIds && !moduleIds.includes(values.moduleId)) {
        warningsUpdated["moduleName"] = MODULES_WARNING;
      }

      setWarnings(warningsUpdated);
    } catch (e) {
      instantFetchErrorHandler(dispatch, e);
    }
  };

  useEffect(() => {
    if (values.enrolmentId && values.moduleId) {
      setModuleWarnings();
    }
  }, [values.enrolmentId, values.moduleId]);

  const onModuleCodeChange = useCallback(
    (m: Module) => {
      dispatch(change(form, getFieldName("moduleName"), m ? m.title : null));
      dispatch(change(form, getFieldName("moduleId"), m ? m.id : null));
      dispatch(change(form, getFieldName("moduleId"), m ? m.id : null));
      dispatch(change(form, getFieldName("reportableHours"), m ? Number(m.nominalHours) : 0));
    },
    [form]
  );

  const onLockStartDate = () => {
    setTimeout(() => {
      const newValue = !values.startDateOverridden;
      dispatch(change(form, getFieldName("startDateOverridden"), newValue));
      if (!newValue) {
        dispatch(change(form, getFieldName("startDate"), isNew ? null : values.actualStartDate));
      }
    }, 300);
  };

  const onLockEndDate = () => {
    setTimeout(() => {
      const newValue = !values.endDateOverridden;
      dispatch(change(form, getFieldName("endDateOverridden"), newValue));
      if (!newValue) {
        dispatch(change(form, getFieldName("endDate"), isNew ? null : values.actualEndDate));
      }
    }, 300);
  };

  const onModuleNameChange = useCallback(
    (m: Module) => {
      dispatch(change(form, getFieldName("moduleCode"), m ? m.nationalCode : null));
      dispatch(change(form, getFieldName("moduleId"), m ? m.id : null));
      dispatch(change(form, getFieldName("reportableHours"), m ? Number(m.nominalHours) : 0));
    },
    [form]
  );

  const outcomeStatusValues = useMemo(() => (values && values.moduleId
    ? Object.keys(OutcomeStatus).map(mapSelectItems)
    : ["Not set", "Satisfactorily completed (81)", "Withdrawn or not satisfactorily completed (82)"].map(
      mapSelectItems
    )), [values]);

  const today = new Date();

  return (
    <Grid container columnSpacing={3} rowSpacing={2} className={className}>
      {!noHeader && <Grid item xs={12}>
        <FullScreenStickyHeader
          disableInteraction
          twoColumn={twoColumn}
          title={(
            <HeaderContactTitle name={values?.studentName} id={values?.contactId} />
          )}
        />
      </Grid>}

      
      <Grid container rowSpacing={2} item xs={twoColumn ? 4 : 12}>
        <Grid item xs={12}>
          <FormField
            type="remoteDataSelect"
            name={getFieldName("moduleCode")}
            label="Module code"
            entity="Module"
            selectValueMark="nationalCode"
            selectLabelMark="nationalCode"
            defaultValue={values && values.moduleCode}
            labelAdornment={(
              <LinkAdornment
                linkHandler={openModuleLink}
                link={values && values.moduleId}
                disabled={values && !values.moduleId}
              />
            )}
            onInnerValueChange={onModuleCodeChange}
            disabled={values && values.hasCertificate}
            allowEmpty
                      />
        </Grid>
        <Grid item xs={12}>
          <FormField
            type="remoteDataSelect"
            entity="Module"
            name={getFieldName("moduleName")}
            label="Module name"
            selectValueMark="title"
            selectLabelMark="title"
            defaultValue={values && values.moduleName}
            labelAdornment={(
              <LinkAdornment
                linkHandler={openModuleLink}
                link={values && values.moduleId}
                disabled={values && !values.moduleId}
              />
            )}
            onInnerValueChange={onModuleNameChange}
            disabled={values && values.hasCertificate}
            warning={warnings["moduleName"]}
            allowEmpty
                      />
        </Grid>
        <Grid item xs={12}>
          <FormField
            type="select"
            name={getFieldName("deliveryMode")}
            label="Delivery mode"
            items={deliveryModeValues}
                      />
        </Grid>
        <Grid item xs={12}>
          <FormField
            type="number"
            name={getFieldName("reportableHours")}
            label="Reportable hours"
            normalize={normalizeNumberToZero}
            debounced={false}
          />
        </Grid>
        <Grid item xs={12}>
          <FormField
            type="select"
            name={getFieldName("fundingSource")}
            label="Funding source"
            items={fundingSourceValues}
          />
        </Grid>
      </Grid>

      <Grid item xs={twoColumn ? 8 : 12}>
        <Card className={classes.card}>
          <Grid container columnSpacing={3} rowSpacing={2}>
            <Grid item xs={12}>
              <div className="heading">OUTCOME PROGRESSION</div>
            </Grid>
            <Grid item xs={twoColumn ? 6 : 12} className="d-flex justify-content-center">
              <AttendanceChart data={values.progression} />
            </Grid>
            <Grid item xs={twoColumn ? 6 : 12} className="d-flex justify-content-center">
              <AssessmentChart data={values.progression} />
            </Grid>
          </Grid>
        </Card>
      </Grid>

      {priorLearningEditView ? (
        <Grid item xs={12}>
          <Grid container columnSpacing={3} rowSpacing={2} item xs={12}>
            <Grid item xs={twoColumn ? 4 : 12}>
              <FormField
                type="date"
                name={getFieldName("startDate")}
                label="Start date"
                validate={validateStartDate}
                placeholder="Leave empty to calculate date from class"
              />
            </Grid>
            <Grid item xs={twoColumn ? 4 : 12}>
              <FormField
                type="date"
                name={getFieldName("endDate")}
                label="End date"
                validate={validateEndtDate}
                placeholder="Leave empty to calculate date from class"
              />
            </Grid>
          </Grid>
        </Grid>
      ) : (
        <Grid item xs={12}>
          <Card className={classes.card}>
            <Grid container columnSpacing={3} rowSpacing={2} className="p-3 pb-0">
              <Grid item xs={twoColumn ? 3 : 12}>
                <div className={clsx(classes.header, classes.width240, "secondaryHeading")}>Training Plan</div>
                <Tooltip
                  placement="top-start"
                  title="First session related to this outcome"
                >
                  <div className="pb-2">
                    <FormField
                      type="date"
                      name={getFieldName("trainingPlanStartDate")}
                      label="Start date"
                      disabled
                    />
                  </div>
                </Tooltip>
                <Tooltip placement="top-start" title="Last session or assessment due date related to this outcome">
                  <div className="pb-2">
                    <FormField
                      type="date"
                      name={getFieldName("trainingPlanEndDate")}
                      label="End date"
                      disabled
                    />
                  </div>
                </Tooltip>
              </Grid>
              <Grid item xs={twoColumn ? 3 : 12}>
                <div className={clsx(classes.header, classes.width240, "secondaryHeading")}>Actual</div>
                <Tooltip placement="top-start" title="First session related to this outcome where student was not marked as absent">
                  <div className="pb-2">
                    {values.actualStartDate && new Date(values.actualStartDate) > today
                      ? <Uneditable label="Start date" value="Not yet started" />
                      : (
                        <FormField
                          type="date"
                          name={getFieldName("actualStartDate")}
                          label="Start date"
                          disabled
                        />
                      )}
                  </div>
                </Tooltip>
                <Tooltip placement="top-start" title="Last session or assessment due date related to this outcome">
                  <div className="pb-2">
                    {values.actualEndDate && new Date(values.actualEndDate) > today
                      ? <Uneditable label="End date" value="Not yet finished" />
                      : (
                        <FormField
                          type="date"
                          name={getFieldName("actualEndDate")}
                          label="End date"
                          disabled
                        />
                      )}
                  </div>
                </Tooltip>
              </Grid>
              <Grid item xs={twoColumn ? 3 : 12}>
                <div className={clsx(classes.header, classes.width240, "secondaryHeading")}>Override</div>
                <Grid item className={clsx(classes.width240, classes.dateWrapper)}>
                  <div className="pb-2">
                    {values.startDateOverridden ? (
                      <div className="centeredFlex">
                        <FormField
                          type="date"
                          name={getFieldName("startDate")}
                          validate={validateStartDate}
                          disabled={!isPriorLearningBinded && !values.startDateOverridden}
                          placeholder={(!isPriorLearningBinded && !values.startDateOverridden)
                            ? null : "Leave empty to calculate date from class"}
                          label="Start date"
                        />
                        <IconButton size="small" onClick={onLockStartDate}>
                          <DeleteIcon fontSize="inherit" color="disabled" />
                        </IconButton>
                      </div>
                    ) : (
                      <>
                        <p className={classes.label}>
                          Start date
                        </p>
                        <div className={classes.buttonWrapper}>
                          <Chip label="Override start date" onClick={onLockStartDate} className={classes.chip} />
                        </div>
                      </>
                    )}
                  </div>
                </Grid>
                <Grid item className={clsx(classes.width240, classes.dateWrapper)}>
                  <div className="pb-2">
                    {values.endDateOverridden ? (
                      <div className="centeredFlex">
                        <FormField
                          type="date"
                          name={getFieldName("endDate")}
                          validate={validateEndtDate}
                          disabled={!isPriorLearningBinded && !values.endDateOverridden}
                          placeholder={(!isPriorLearningBinded && !values.endDateOverridden)
                            ? null : "Leave empty to calculate date from class"}
                          label="End date"
                        />
                        <IconButton size="small" onClick={onLockEndDate}>
                          <DeleteIcon fontSize="inherit" color="disabled" />
                        </IconButton>
                      </div>
                    ) : (
                      <>
                        <p className={classes.label}>
                          End date
                        </p>
                        <div className={classes.buttonWrapper}>
                          <Chip label="Override end date" onClick={onLockEndDate} className={classes.chip} />
                        </div>
                      </>
                    )}
                  </div>
                </Grid>
              </Grid>
            </Grid>
          </Card>
        </Grid>
      )}
      <Grid item xs={12}>
        <Grid container columnSpacing={3} rowSpacing={2}>
          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="select"
              name={getFieldName("status")}
              label="Status"
              items={outcomeStatusValues}
              disabled={values && values.printed}
                          />
          </Grid>
          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="number"
              name={getFieldName("hoursAttended")}
              parse={parseIntValue}
              label="Hours attended"
              debounced={false}
                          />
          </Grid>
          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="text"
              name={getFieldName("vetFundingSourceStateID")}
              label="Funding source state"
              validate={validateFundingSourse}
                          />
          </Grid>
        </Grid>
      </Grid>
      <Grid item xs={12}>
        <Grid container columnSpacing={3} rowSpacing={2}>
          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="text"
              name={getFieldName("vetPurchasingContractID")}
              label="Purchasing contract identifier"
              validate={validateVetPurchasingContractIdentifier}
                          />
          </Grid>
          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="text"
              name={getFieldName("vetPurchasingContractScheduleID")}
              label="Purchasing contract schedule identifier"
              validate={validatePurchasingContractScheduleIdentifier}
                          />
          </Grid>

          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="text"
              name={getFieldName("specificProgramIdentifier")}
              label="Specific program identifier"
                            validate={validateSpecificProgramIdentifier}
            />
          </Grid>

          {fundingUploadAccess && values.id
            && (
            <Grid item xs={12} className="saveButtonTableOffset mt-1">
              <div className="heading mb-1">Funding Uploads</div>
              {fundingUploads.length
                ? (
                  <>
                    {fundingUploads.map(u => (
                      <FundingUploadComponent
                        key={u.id}
                        fundingUpload={u}
                        readOnly
                      />
                    ))}
                  </>
                )
                : <Typography variant="caption" color="textSecondary" className="mt-1">No funding uploads were found</Typography>}
            </Grid>
          )}
        </Grid>
      </Grid>
    </Grid>
  );
});

const mapStateToProps = (state: State) => ({
  access: state.access
});

export default connect<any, any, OutcomeEditFieldsProps>(mapStateToProps)(OutcomeEditFields);