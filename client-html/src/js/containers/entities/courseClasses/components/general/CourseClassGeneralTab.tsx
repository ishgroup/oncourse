/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Tag } from "@api/model";
import Launch from "@mui/icons-material/Launch";
import { FormControlLabel, Grid, IconButton } from "@mui/material";
import Collapse from "@mui/material/Collapse";
import Typography from "@mui/material/Typography";
import clsx from "clsx";
import { decimalDivide, decimalMinus, decimalPlus, LinkAdornment } from "ish-ui";
import React, { useCallback, useMemo } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { change } from "redux-form";
import { showMessage } from "../../../../../common/actions";
import FormField from "../../../../../common/components/form/formFields/FormField";
import FullScreenStickyHeader
  from "../../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";
import history from "../../../../../constants/History";
import { EditViewProps } from "../../../../../model/common/ListView";
import { AppMessage } from "../../../../../model/common/Message";
import { CourseClassExtended } from "../../../../../model/entities/CourseClass";
import { State } from "../../../../../reducers/state";
import { EntityChecklists } from "../../../../tags/components/EntityChecklists";
import CourseItemRenderer from "../../../courses/components/CourseItemRenderer";
import { courseFilterCondition, openCourseLink } from "../../../courses/utils";
import CustomFields from "../../../customFieldTypes/components/CustomFieldsTypes";
import { getClassCostTypes } from "../../utils";
import CourseClassEnrolmentsChart from "./CourseClassEnrolmentsChart";

interface Props extends Partial<EditViewProps<CourseClassExtended>> {
  tags?: Tag[];
  specialTags?: Tag[];
  showMessage?: (message: AppMessage) => void;
  classes?: any;
  clearActionsQueue?: any;
  enrolments?: any;
  tutorRoles?: any;
  netValues?: any;
  classCostTypes?: any;
}

const normalizeClassCode = (value: any, previousValue?: any, allValues?: any) => value.replace(new RegExp(`${allValues.courseCode}-?`), "");

const CourseClassGeneralTab = React.memo<Props>(
  ({
    tags,
    specialTags,
    twoColumn,
    values,
    isNew,
    syncErrors,
    dispatch,
    form,
    toogleFullScreenEditView,
    tutorRoles,
    netValues,
     classCostTypes
  }) => {

    const openBudget = useCallback(() => {
      if (!twoColumn) {
        toogleFullScreenEditView();
      }

      const search = new URLSearchParams(window.location.search);
      search.append("expandTab", "4");

      history.replace({
        pathname: history.location.pathname,
        search: decodeURIComponent(search.toString())
      });
    }, [twoColumn]);

    const onCourseIdChange = useCallback(
      course => {
        dispatch(change(form, "courseCode", course ? course.code : null));
        dispatch(change(form, "courseName", course ? course.name : null));
        dispatch(change(form, "reportableHours", course.reportableHours ? parseFloat(course.reportableHours) : null));

        if (course.name) {
          const changedSessions = values.sessions.map(s => ({ ...s, name: course.name }));
          dispatch(change(form, "sessions", changedSessions));
        }

        if (!values.code && course.nextAvailableCode) {
          dispatch(change(form, "code", course.nextAvailableCode));
        }
      },
      [form, values.code, values.sessions]
    );

    // Enrolments to profit projected
    const enrolmentsToProfitAllCount = useMemo(() => {
      if (values.feeExcludeGST <= 0) {
        return 0;
      }

      let count = 0;

      let classCostTypes = getClassCostTypes(
        values.budget,
        values.maximumPlaces,
        count,
        values.successAndQueuedEnrolmentsCount,
        values.sessions,
        values.tutors,
        tutorRoles,
      );

      let projected = decimalMinus(
        decimalMinus(
          decimalPlus(classCostTypes.customInvoices.projected, classCostTypes.income.projected),
          classCostTypes.discount.projected
        ),
        classCostTypes.cost.projected
      );

      let prevProjected = -Number.MAX_SAFE_INTEGER;

      if (typeof projected === "number" && projected < 0) {
        while (projected < 0 && prevProjected < projected) {
          prevProjected = projected;

          count++;
          classCostTypes = getClassCostTypes(
            values.budget,
            values.maximumPlaces,
            count,
            values.successAndQueuedEnrolmentsCount,
            values.sessions,
            values.tutors,
            tutorRoles,
          );

          projected = decimalMinus(
            decimalMinus(
              decimalPlus(classCostTypes.customInvoices.projected, classCostTypes.income.projected),
              classCostTypes.discount.projected
            ),
            classCostTypes.cost.projected
          );
        }
      }

      return count;
    }, [
      values.budget,
      values.maximumPlaces,
      values.budgetedPlaces,
      values.successAndQueuedEnrolmentsCount,
      values.sessions,
      values.tutors,
      tutorRoles,
      values.feeExcludeGST
    ]);

    const actualEnrolmentsToProfit = useMemo(() => {
      if (values.successAndQueuedEnrolmentsCount < 1) {
        return 0;
      }

      const actualEnrolment = decimalDivide(netValues.income.actual, values.successAndQueuedEnrolmentsCount);

      if (actualEnrolment <= 0) {
        return 0;
      }

      let covered = 0;

      while (covered < classCostTypes.cost.actual) {
        covered += actualEnrolment;
      }

      return decimalDivide(covered, actualEnrolment);
    }, [
      netValues,
      values.successAndQueuedEnrolmentsCount
    ]);

    const formatClassCode = value => `${values.courseCode ? values.courseCode + "-" : ""}${value || ""}`;

    return (
      <>
        <Grid container columnSpacing={3} rowSpacing={2} className="pl-3 pt-3 pr-3 relative">
          <Grid item xs={12}>
            <FullScreenStickyHeader
              opened={isNew || Object.keys(syncErrors).some(k => ['courseId', 'courseCode', 'code'].includes(k))}
              twoColumn={twoColumn}
              title={twoColumn ? (
                <div className="centeredFlex">
                  {values.courseName}
                  <IconButton disabled={!values.courseId} size="small" color="primary" onClick={() => openCourseLink(values.courseId)}>
                    <Launch fontSize="inherit" />
                  </IconButton>
                  <span className="ml-2">
                    {values.courseCode ? `${values.courseCode}-${values.code || ""}` : null}
                  </span>
                </div>
            ) : (
              <Grid container columnSpacing={3} rowSpacing={2}>
                <Grid item xs={12}>
                  <div className="centeredFlex">
                    {values.courseName}
                    <IconButton disabled={!values.courseId} size="small" color="primary" onClick={() => openCourseLink(values.courseId)}>
                      <Launch fontSize="inherit" />
                    </IconButton>
                  </div>
                </Grid>
                <Grid item xs={12}>
                  {values.courseCode ? `${values.courseCode}-${values.code || ""}` : null}
                </Grid>
              </Grid>
              )}
              fields={(
                <Grid container columnSpacing={3} rowSpacing={2}>
                  <Grid item xs={twoColumn ? 6 : 12}>
                    <FormField
                      type="remoteDataSelect"
                      label="Course"
                      entity="Course"
                      name="courseId"
                      selectValueMark="id"
                      selectLabelMark="name"
                      aqlColumns="code,name,currentlyOffered,isShownOnWeb,reportableHours,nextAvailableCode"
                      selectFilterCondition={courseFilterCondition}
                      defaultValue={values && values.courseName}
                      itemRenderer={CourseItemRenderer}
                      disabled={!isNew}
                      onInnerValueChange={onCourseIdChange}
                      rowHeight={55}
                      labelAdornment={<LinkAdornment link={values.courseId} linkHandler={openCourseLink} />}
                      required
                    />
                  </Grid>
                  <Grid item xs={twoColumn ? 4 : 12}>
                    <FormField
                      type="text"
                      label="Class code"
                      name="code"
                      placeholder="Select course first"
                      normalize={normalizeClassCode}
                      format={formatClassCode}
                      disabled={!values.courseCode}
                      debounced={false}
                      required
                    />
                  </Grid>
                </Grid>
              )}
            />
          </Grid>
          {Boolean(values.isCancelled) && (
            <div className={clsx("backgroundText errorColorFade-0-2", twoColumn ? "fs10" : "fs8")}>Cancelled</div>
          )}
        </Grid>

        <Grid
          container
          className="pt-2 pl-3 pr-3"
          columnSpacing={3}
          rowSpacing={2}
        >
          <Grid item xs={twoColumn ? 8 : 12}>
            <FormField type="tags" name="tags" className="mb-2" tags={tags} />

            <FormField
              type="select"
              items={specialTags}
              name="specialTagId"
              label="Type"
              selectValueMark="id"
              selectLabelMark="name"
              allowEmpty
            />
            
            <div className="heading pb-2 pt-3">Restrictions</div>
            <Typography variant="body2" color="inherit" component="div" className="pb-1">
              Students must be at least
              {" "}
              <FormField
                type="number"
                name="minStudentAge"
                min="1"
                max="99"
                step="1"
                inline
              />
              {" "}
              years old to enrol
            </Typography>

            <Typography variant="body2" color="inherit" component="div" className="pb-2">
              Students must be no older than
              {" "}
              <FormField
                type="number"
                name="maxStudentAge"
                min="1"
                max="99"
                step="1"
                inline
              />
              {" "}
              years old to enrol
            </Typography>

            <Collapse in={!values.isCancelled}>
              <div>
                <FormControlLabel
                  className="switchWrapper"
                  control={<FormField type="switch" name="isActive" />}
                  label="Enrolments allowed"
                  labelPlacement="start"
                />
              </div>
              <div>
                <FormControlLabel
                  className="switchWrapper"
                  control={<FormField type="switch" name="isShownOnWeb" />}
                  label="Visible online"
                  labelPlacement="start"
                />
              </div>
            </Collapse>
            <FormField
              type="multilineText"
              name="message"
              label="Message for operator"
              className="pt-2"
            />
          </Grid>

          <Grid item xs={twoColumn ? 4 : 12}>
            <EntityChecklists
              className={twoColumn ? "mr-4" : null}
              entity="CourseClass"
              form={form}
              entityId={values.id}
              checked={values.tags}
            />
          </Grid>

          <Grid item xs={12}>
            <CourseClassEnrolmentsChart
              classId={values.id}
              classStart={values.startDateTime}
              minEnrolments={values.minimumPlaces}
              maxEnrolments={values.maximumPlaces}
              targetEnrolments={enrolmentsToProfitAllCount}
              actualEnrolmentsToProfit={actualEnrolmentsToProfit}
              openBudget={openBudget}
              hasBudged={values.budget?.some(b => b.invoiceToStudent && b.perUnitAmountIncTax > 0)}
            />
          </Grid>

          <CustomFields
            entityName="CourseClass"
            fieldName="customFields"
            entityValues={values}
            form={form}
            gridItemProps={{
              xs: twoColumn ? 6 : 12
            }}
          />
        </Grid>
      </>
    );
  }
);

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  showMessage: message => dispatch(showMessage(message))
});

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags["CourseClass"],
  specialTags: state.tags.entitySpecialTags["CourseClass"],
  tutorRoles: state.preferences.tutorRoles
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(CourseClassGeneralTab);