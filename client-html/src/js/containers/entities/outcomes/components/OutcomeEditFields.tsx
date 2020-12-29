/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Typography } from "@material-ui/core";
import React, {
 useCallback, useEffect, useMemo, useState
} from "react";
import Grid from "@material-ui/core/Grid";
import { change } from "redux-form";
import { connect } from "react-redux";
import IconButton from "@material-ui/core/IconButton/IconButton";
import LockOpen from "@material-ui/icons/LockOpen";
import Lock from "@material-ui/icons/Lock";
import {
  ClassFundingSource,
  DeliveryMode, FundingUpload,
  Module,
  Outcome,
  OutcomeStatus
} from "@api/model";
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

interface OutcomeEditFieldsProps extends EditViewProps<Outcome> {
  modules?: any[];
  modulesLoading?: boolean;
  modulesRowsCount?: number;
  setModuleSearch?: any;
  clearModuleSearch?: any;
  getModules?: any;
  className?: string;
  isPriorLearningBinded?: boolean;
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
    access
  } = props;

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
        dispatch(change(form, "startDate", isNew ? null : values.trainingPlanStartDate));
      }
    }, 300);
  };

  const onLockEndtDate = () => {
    setTimeout(() => {
      const newValue = !values.endDateOverridden;
      dispatch(change(form, "endDateOverridden", newValue));
      if (!newValue) {
        dispatch(change(form, "endDate", isNew ? null : values.trainingPlanEndDate));
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
      <Grid item xs={12}>
        <Grid container>
          <Grid item xs={twoColumn ? 4 : 12} className="textField">
            <div>
              <FormField
                type="date"
                name={getFieldName("startDate")}
                label={
                  isPriorLearningBinded || !values.startDateOverridden ? "Start date" : "Override training start date"
                }
                validate={validateStartDate}
                listSpacing={false}
                disabled={!isPriorLearningBinded && !values.startDateOverridden}
                placeHolder="Leave empty to calculate date from class"
                labelAdornment={
                  !isPriorLearningBinded ? (
                    <span>
                      <IconButton className="inputAdornmentButton" onClick={onLockStartDate}>
                        {values.startDateOverridden && <LockOpen className="inputAdornmentIcon" />}
                        {!values.startDateOverridden && <Lock className="inputAdornmentIcon" />}
                      </IconButton>
                    </span>
                  ) : null
                }
              />
            </div>
          </Grid>
          <Grid item xs={twoColumn ? 4 : 12} className="textField">
            <div>
              <FormField
                type="date"
                name={getFieldName("endDate")}
                label={
                  isPriorLearningBinded || !values.endDateOverridden ? "End date" : "Override training end date"
                }
                validate={validateEndtDate}
                listSpacing={false}
                disabled={!isPriorLearningBinded && !values.endDateOverridden}
                placeHolder="Leave empty to calculate date from class"
                labelAdornment={
                  !isPriorLearningBinded ? (
                    <span>
                      <IconButton className="inputAdornmentButton" onClick={onLockEndtDate}>
                        {values.endDateOverridden && <LockOpen className="inputAdornmentIcon" />}
                        {!values.endDateOverridden && <Lock className="inputAdornmentIcon" />}
                      </IconButton>
                    </span>
                  ) : null
                }
              />
            </div>
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
          name={getFieldName("deliveryMode")}
          label="Delivery mode"
          items={deliveryModeValues}
          fullWidth
        />
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
