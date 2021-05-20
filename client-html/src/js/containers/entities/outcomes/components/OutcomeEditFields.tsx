/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import {
  Card, Chip, Grid, Tooltip, Typography
} from "@material-ui/core";
import React, {
 useCallback, useEffect, useMemo, useState
} from "react";
import { change } from "redux-form";
import { connect } from "react-redux";
import clsx from "clsx";
import IconButton from "@material-ui/core/IconButton/IconButton";
import DeleteIcon from '@material-ui/icons/Delete';
import {
 ClassFundingSource, DeliveryMode, FundingUpload, Module, Outcome, OutcomeStatus
} from "@api/model";
import makeStyles from "@material-ui/core/styles/makeStyles";
import instantFetchErrorHandler from "../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import FormField from "../../../../common/components/form/form-fields/FormField";
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
import Uneditable from "../../../../common/components/form/Uneditable";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import { fundingUploadsPath } from "../../../../constants/Api";
import FundingUploadService from "../../../avetmiss-export/services/FundingUploadService";
import { defaultContactName } from "../../contacts/utils";
import { openModuleLink } from "../../modules/utils";
import { State } from "../../../../reducers/state";
import { EditViewProps } from "../../../../model/common/ListView";
import { normalizeNumberToZero } from "../../../../common/utils/numbers/numbersNormalizing";
import { AppTheme } from "../../../../model/common/Theme";

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
}

const deliveryModeValues = Object.keys(DeliveryMode).map(mapSelectItems);
const fundingSourceValues = Object.keys(ClassFundingSource).map(mapSelectItems);

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
    height: "60px",
  },
  deleteIcon: {
    marginTop: "10px",
    fontSize: "18px"
  },
  chip: {
    minWidth: "8em",
    height: "26px",
  },
  tooltip: {
    marginTop: theme.spacing(-2)
  }
}));

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
    priorLearningEditView
  } = props;

  const classes = useStyles();

  const [fundingUploads, setFundingUploads] = useState<FundingUpload[]>([]);

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
  }, [values && values.id, access]);

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
      dispatch(change(form, "startDateOverridden", newValue));
      if (!newValue) {
        dispatch(change(form, "startDate", isNew ? null : values.actualStartDate));
      }
    }, 300);
  };

  const onLockEndDate = () => {
    setTimeout(() => {
      const newValue = !values.endDateOverridden;
      dispatch(change(form, "endDateOverridden", newValue));
      if (!newValue) {
        dispatch(change(form, "endDate", isNew ? null : values.actualEndDate));
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
    <Grid container className={className}>
      {!twoColumn && (
        <Grid item xs={12}>
          <Uneditable
            label="Student name"
            value={values && defaultContactName(values.studentName)}
            url={`/contact/${values.contactId}`}
          />
        </Grid>
      )}
      <Grid item xs={12}>
        <Grid container>
          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="remoteDataSearchSelect"
              name={getFieldName("moduleCode")}
              label="Module code"
              entity="Module"
              selectValueMark="nationalCode"
              selectLabelMark="nationalCode"
              defaultDisplayValue={values && values.moduleCode}
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
              fullWidth
            />
          </Grid>
          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="remoteDataSearchSelect"
              entity="Module"
              name={getFieldName("moduleName")}
              label="Module name"
              selectValueMark="title"
              selectLabelMark="title"
              defaultDisplayValue={values && values.moduleName}
              labelAdornment={(
                <LinkAdornment
                  linkHandler={openModuleLink}
                  link={values && values.moduleId}
                  disabled={values && !values.moduleId}
                />
              )}
              onInnerValueChange={onModuleNameChange}
              allowEmpty
              disabled={values && values.hasCertificate}
              fullWidth
            />
          </Grid>
        </Grid>
      </Grid>

      {priorLearningEditView ? (
        <Grid container item={true} xs={12}>
          <Grid item xs={twoColumn ? 4 : 12} className="textField">
            <div>
              <FormField
                type="date"
                name={getFieldName("startDate")}
                label="Start date"
                validate={validateStartDate}
                listSpacing={false}
                placeHolder="Leave empty to calculate date from class"
              />
            </div>
          </Grid>
          <Grid item xs={twoColumn ? 4 : 12} className="textField">
            <div>
              <FormField
                type="date"
                name={getFieldName("endDate")}
                label="End date"
                validate={validateEndtDate}
                listSpacing={false}
                placeholder="Leave empty to calculate date from class"
              />
            </div>
          </Grid>
        </Grid>
      ) : (
        <Card className={classes.card}>
          <Grid container>
            <Grid xs={twoColumn ? 3 : 12}>
              <Grid className={clsx(classes.header, classes.width240, "secondaryHeading")}>Training Plan</Grid>
              <Tooltip
                placement="top-start"
                title="First session related to this outcome"
              >
                <div>
                  <FormField
                    type="date"
                    name={getFieldName("trainingPlanStartDate")}
                    label="Start date"
                    disabled
                  />
                </div>
              </Tooltip>
              <Tooltip placement="top-start" title="Last session or assessment due date related to this outcome">
                <div>
                  <FormField
                    type="date"
                    name={getFieldName("trainingPlanEndDate")}
                    label="End date"
                    disabled
                  />
                </div>
              </Tooltip>
            </Grid>
            <Grid xs={twoColumn ? 3 : 12}>
              <Grid className={clsx(classes.header, classes.width240, "secondaryHeading")}>Actual</Grid>
              <Tooltip placement="top-start" title="First session related to this outcome where student was not marked as absent">
                <div>
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
                <div>
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
            <Grid xs={twoColumn ? 3 : 12}>
              <Grid className={clsx(classes.header, classes.width240, "secondaryHeading")}>Override</Grid>
              <Grid item className={clsx(classes.width240, classes.dateWrapper)}>
                {values.startDateOverridden ? (
                  <>
                    <FormField
                      type="date"
                      name={getFieldName("startDate")}
                      validate={validateStartDate}
                      disabled={!isPriorLearningBinded && !values.startDateOverridden}
                      placeholder={(!isPriorLearningBinded && !values.startDateOverridden) ? null : "Leave empty to calculate date from class"}
                      label="Start date"
                    />
                    <IconButton className="inputAdornmentButton" onClick={onLockStartDate}>
                      <DeleteIcon className={classes.deleteIcon} />
                    </IconButton>
                  </>
                ) : (
                  <Grid item className={classes.buttonWrapper}>
                    <Chip label="Override" onClick={onLockStartDate} className={classes.chip} />
                  </Grid>
                )}
              </Grid>
              <Grid item className={clsx(classes.width240, classes.dateWrapper)}>
                {values.endDateOverridden ? (
                  <>
                    <FormField
                      type="date"
                      name={getFieldName("endDate")}
                      validate={validateEndtDate}
                      disabled={!isPriorLearningBinded && !values.endDateOverridden}
                      placeholder={(!isPriorLearningBinded && !values.endDateOverridden) ? null : "Leave empty to calculate date from class"}
                      label="End date"
                    />
                    <IconButton className="inputAdornmentButton" onClick={onLockEndDate}>
                      <DeleteIcon className={classes.deleteIcon} />
                    </IconButton>
                  </>
                ) : (
                  <Grid item className={classes.buttonWrapper}>
                    <Chip label="Override" onClick={onLockEndDate} className={classes.chip} />
                  </Grid>
                )}
              </Grid>
            </Grid>
          </Grid>
        </Card>
      )}

      <Grid item xs={12}>
        <Grid container>
          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="select"
              name={getFieldName("deliveryMode")}
              label="Delivery mode"
              items={deliveryModeValues}
              fullWidth
            />
          </Grid>
          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="number"
              name={getFieldName("reportableHours")}
              label="Reportable hours"
              normalize={normalizeNumberToZero}
            />
          </Grid>
        </Grid>
      </Grid>
      <Grid item xs={12}>
        <FormField
          type="select"
          name={getFieldName("fundingSource")}
          label="Funding source"
          items={fundingSourceValues}
          fullWidth
        />
      </Grid>
      <Grid item xs={12}>
        <Grid container>
          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="select"
              name={getFieldName("status")}
              label="Status"
              items={outcomeStatusValues}
              disabled={values && values.printed}
              fullWidth
            />
          </Grid>
          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="number"
              name={getFieldName("hoursAttended")}
              parse={parseIntValue}
              label="Hours attended"
              fullWidth
            />
          </Grid>
          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="text"
              name={getFieldName("vetFundingSourceStateID")}
              label="Funding source state"
              validate={validateFundingSourse}
              fullWidth
            />
          </Grid>
        </Grid>
      </Grid>
      <Grid item xs={12}>
        <Grid container>
          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="text"
              name={getFieldName("vetPurchasingContractID")}
              label="Purchasing contract identifier"
              validate={validateVetPurchasingContractIdentifier}
              fullWidth
            />
          </Grid>
          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="text"
              name={getFieldName("vetPurchasingContractScheduleID")}
              label="Purchasing contract schedule identifier"
              validate={validatePurchasingContractScheduleIdentifier}
              fullWidth
            />
          </Grid>

          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="text"
              name={getFieldName("specificProgramIdentifier")}
              label="Specific program identifier"
              fullWidth
              validate={validateSpecificProgramIdentifier}
            />
          </Grid>

          {fundingUploadAccess && values.id
            && (
            <Grid item xs={twoColumn ? 4 : 12} className="saveButtonTableOffset mt-1">
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
                  ) : <Typography variant="caption" color="textSecondary" className="mt-1">No funding uploads were found</Typography>}
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
