/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Module, Qualification } from "@api/model";
import { Collapse, FormControlLabel, Grid, Tooltip } from "@mui/material";
import { AnyArgFunction, BooleanArgFunction, LinkAdornment, normalizeNumberToZero, StringArgFunction } from "ish-ui";
import React, { useCallback, useMemo } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { change } from "redux-form";
import {
  clearCommonPlainRecords,
  getCommonPlainRecords,
  setCommonPlainSearch
} from "../../../../common/actions/CommonPlainRecordsActions";
import FormField from "../../../../common/components/form/formFields/FormField";
import Uneditable from "../../../../common/components/form/formFields/Uneditable";
import NestedList, { NestedListItem } from "../../../../common/components/form/nestedList/NestedList";
import { validateSingleMandatoryField } from "../../../../common/utils/validation";
import { PLAIN_LIST_MAX_PAGE_SIZE } from "../../../../constants/Config";
import { EditViewProps } from "../../../../model/common/ListView";
import { CourseExtended } from "../../../../model/entities/Course";
import { State } from "../../../../reducers/state";
import QualificationListItemRenderer from "../../qualifications/components/QualificationListItemRenderer";
import { openQualificationLink } from "../../qualifications/utils";

const getQualificationLabel = (qal: Qualification) => `${qal.title}, ${qal.nationalCode}`;

interface CourseVetTab extends EditViewProps<CourseExtended> {
  classes?: any;
  setModuleSearch?: StringArgFunction;
  getModules?: AnyArgFunction;
  modulesPending?: boolean;
  moduleError?: boolean;
  moduleItems?: Module[];
  clearModuleSearch?: BooleanArgFunction;
}

const transformModule = (module: Module): NestedListItem => ({
  id: module.id.toString(),
  entityId: module.id,
  primaryText: module.title,
  secondaryText: module.nationalCode,
  link: `/module/${module.id}`,
  active: true
});

const tooltipText = "Changing this default value will not update enrolments or outcomes already created.";

const CourseVetTab = React.memo<CourseVetTab>(props => {
  const {
    twoColumn,
    values,
    dispatch,
    form,
    getModules,
    setModuleSearch,
    classes,
    submitSucceeded,
    moduleItems,
    modulesPending,
    moduleError,
    clearModuleSearch
  } = props;

  const onQualificationCodeChange = useCallback(
    (q: Qualification) => {
      if (!q && !values.isTraineeship) {
        dispatch(change(form, "isSufficientForQualification", false));
      }

      dispatch(change(form, "qualNationalCode", q ? q.nationalCode : null));
      dispatch(change(form, "qualTitle", q ? q.title : null));
      dispatch(change(form, "qualLevel", q ? q.qualLevel : null));
      dispatch(change(form, "fieldOfEducation", q ? q.fieldOfEducation : null));
      dispatch(change(form, "isVET", Boolean(q)));
    },
    [form, values.isTraineeship]
  );

  const updateReportableHours = useCallback(
    modules => {
      dispatch(
        change(
          form,
          "reportableHours",
          modules.reduce((p, c) => p + (c.nominalHours ? Number(c.nominalHours) : 0), 0)
        )
      );
    },
    [form]
  );

  const onAddModules = useCallback(
    (modulesToAdd: NestedListItem[]) => {
      const newModules = values.modules.concat(modulesToAdd.map(v1 => moduleItems.find(v2 => v2.id === v1.entityId)));
      dispatch(change(form, "modules", newModules));
      updateReportableHours(newModules);
    },
    [form, values.modules, moduleItems]
  );

  const onDeleteModules = useCallback(
    module => {
      const updated = values.modules.filter(m => m.id !== module.entityId);
      dispatch(change(form, "modules", updated));
      updateReportableHours(updated);
    },
    [values.modules]
  );

  const searchModule = useCallback(search => {
    setModuleSearch(search);
    getModules();
  }, []);

  const moduleItemsTransformed = useMemo(() => {
    if (!values.modules) {
      return [];
    }

    const result = values.modules.map(transformModule);

    result.sort((a, b) => (a.secondaryText > b.secondaryText ? 1 : -1));

    return result;
  }, [values.modules]);
  const moduleSearchItemsTransformed = useMemo(() => {
    const result = moduleItems.map(transformModule);

    result.sort((a, b) => (a.secondaryText > b.secondaryText ? 1 : -1));

    return result;
  }, [moduleItems]);

  return (
    <Grid container columnSpacing={3} rowSpacing={2} className="pl-3 pr-3 mt-1">
      <Grid item xs={12}>
        <div className="heading">Vet</div>
      </Grid>

      <Grid item xs={twoColumn ? 6 : 12}>
        <FormField
          type="remoteDataSelect"
          entity="Qualification"
          name="qualificationId"
          label="Qualification"
          selectValueMark="id"
          defaultValue={values.qualTitle}
          labelAdornment={<LinkAdornment link={values.qualificationId} linkHandler={openQualificationLink} />}
          onInnerValueChange={onQualificationCodeChange}
          itemRenderer={QualificationListItemRenderer}
          selectLabelCondition={getQualificationLabel}
          validate={values.isTraineeship && validateSingleMandatoryField}
          rowHeight={55}
          allowEmpty
        />
      </Grid>

      <Grid item xs={twoColumn ? 6 : 12}>
        <Uneditable value={values.qualNationalCode} label="National code" />
      </Grid>

      <Grid item xs={twoColumn ? 6 : 12}>
        <Uneditable value={values.qualLevel} label="Level" />
      </Grid>

      <Grid item xs={twoColumn ? 6 : 12}>
        <FormControlLabel
          className="checkbox"
          control={<FormField type="checkbox" name="isSufficientForQualification" />}
          label="Satisfies complete qualification or skill set"
          disabled={!values.qualificationId || values.isTraineeship}
        />
      </Grid>

      <Grid item xs={twoColumn ? 6 : 12}>
        <FormControlLabel
          className="checkbox"
          control={<FormField type="checkbox" name="isVET" />}
          label="VET course"
          disabled={Boolean(values.qualificationId) || values.isTraineeship}
        />
      </Grid>

      <Grid item xs={twoColumn ? 6 : 12}>
        <FormField
          type="text"
          name="fieldOfEducation"
          label="Field of education"
          disabled={Boolean(values.qualificationId || values.isTraineeship)}
        />
      </Grid>

      <Grid item xs={twoColumn ? 6 : 12}>
        <FormField
          type="number"
          normalize={normalizeNumberToZero}
          name="reportableHours"
          label={(
            <Tooltip title={tooltipText} placement="top">
              <div>Default reportable hours</div>
            </Tooltip>
          )}
          debounced={false}
        />
      </Grid>

      <Grid item xs={12}>
        <div className="heading">Vet student loans</div>
      </Grid>

      <Grid item xs={twoColumn ? 6 : 12}>
        <FormControlLabel
          className="checkbox"
          control={<FormField type="checkbox" name="feeHelpClass" />}
          label="This is a VET Student Loan eligible course"
        />
      </Grid>
      <Grid item xs={twoColumn ? 6 : 12}>
        <Collapse in={values.feeHelpClass}>
          <FormField type="text" name="fullTimeLoad" label="Equivalent full-time student load" />
        </Collapse>
      </Grid>

      <Grid item xs={twoColumn ? 8 : 12}>
        <NestedList
          formId={values.id}
          title="Modules / Units of competency"
          titleCaption={
            values.hasEnrolments
              ? "There are enrolments in this course. Modifying the modules is not allowed."
              : undefined
          }
          values={moduleItemsTransformed}
          searchValues={moduleSearchItemsTransformed}
          pending={modulesPending}
          onAdd={onAddModules}
          onDelete={onDeleteModules}
          onSearch={searchModule}
          clearSearchResult={clearModuleSearch}
          resetSearch={submitSucceeded}
          dataRowClass={classes.moduleRowClass}
          aqlEntities={["Module"]}
          aqlQueryError={moduleError}
          disabled={values.hasEnrolments}
        />
      </Grid>
    </Grid>
  );
});

const mapStateToProps = (state: State) => ({
  modulesPending: state.plainSearchRecords["Module"].loading,
  moduleItems: state.plainSearchRecords["Module"].items,
  moduleError: state.plainSearchRecords["Module"].error
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getModules: (offset?: number) => {
    dispatch(getCommonPlainRecords("Module", offset, "nationalCode,title,nominalHours", true, null, PLAIN_LIST_MAX_PAGE_SIZE));
  },
  setModuleSearch: (search: string) => dispatch(setCommonPlainSearch("Module", search)),
  clearModuleSearch: (loading: boolean) => dispatch(clearCommonPlainRecords("Module", loading))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(CourseVetTab);
