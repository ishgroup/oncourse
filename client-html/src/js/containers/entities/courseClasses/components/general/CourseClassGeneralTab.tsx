/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useCallback, useEffect, useMemo, useState
} from "react";
import clsx from "clsx";
import { connect } from "react-redux";
import { change } from "redux-form";
import Grid from "@mui/material/Grid";
import { Dispatch } from "redux";
import { Tag } from "@api/model";
import Typography from "@mui/material/Typography";
import FormControlLabel from "@mui/material/FormControlLabel";
import Collapse from "@mui/material/Collapse";
import { IconButton } from "@mui/material";
import Launch from "@mui/icons-material/Launch";
import FormField from "../../../../../common/components/form/formFields/FormField";
import { State } from "../../../../../reducers/state";
import { validateTagsList } from "../../../../../common/components/form/simpleTagListComponent/validateTagsList";
import EditInPlaceField from "../../../../../common/components/form/formFields/EditInPlaceField";
import { courseFilterCondition, openCourseLink } from "../../../courses/utils";
import CourseItemRenderer from "../../../courses/components/CourseItemRenderer";
import { LinkAdornment } from "../../../../../common/components/form/FieldAdornments";
import { EditViewProps } from "../../../../../model/common/ListView";
import { CourseClassExtended } from "../../../../../model/entities/CourseClass";
import CourseClassEnrolmentsChart from "./CourseClassEnrolmentsChart";
import { stubFunction } from "../../../../../common/utils/common";
import { showMessage } from "../../../../../common/actions";
import { AppMessage } from "../../../../../model/common/Message";
import history from "../../../../../constants/History";
import { decimalMinus, decimalPlus } from "../../../../../common/utils/numbers/decimalCalculation";
import { getClassCostTypes } from "../../utils";
import CustomFields from "../../../customFieldTypes/components/CustomFieldsTypes";
import FullScreenStickyHeader
  from "../../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";

interface Props extends Partial<EditViewProps<CourseClassExtended>> {
  tags?: Tag[];
  showMessage?: (message: AppMessage) => void;
  classes?: any;
  clearActionsQueue?: any;
  enrolments?: any;
  tutorRoles?: any;
}

const CourseClassGeneralTab = React.memo<Props>(
  ({
    tags,
    twoColumn,
    values,
    isNew,
    syncErrors,
    dispatch,
    form,
    showMessage,
    toogleFullScreenEditView,
    tutorRoles
  }) => {
    const [classCodeError, setClassCodeError] = useState(null);
    const [showAllWeeks, setShowAllWeeks] = useState(false);

    useEffect(() => {
      if (isNew && !values.code) {
        setClassCodeError("Class code is mandatory");
      }
    }, [isNew]);

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

    const validateTagListCallback = useCallback(
      (value, allValues, props) => (tags && tags.length ? validateTagsList(tags, value, allValues, props) : undefined),
      [tags]
    );

    const onClassCodeChange = useCallback(
      e => {
        const val = e.target.value;
        const codePrefix = `${values.courseCode}-`;

        if (val.includes(codePrefix)) {
          const codeValue = val.replace(codePrefix, "");
          dispatch(change(form, "code", codeValue));
          if (!codeValue) {
            setClassCodeError("Class code is mandatory");
          } else if (classCodeError) {
            setClassCodeError(null);
          }
        } else {
          showMessage({ message: "Course code part can not be changed" });
        }
      },
      [values.code, values.courseCode, form, classCodeError]
    );

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
          setClassCodeError(null);
        }
      },
      [form, values.code, values.sessions]
    );

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
                      type="remoteDataSearchSelect"
                      label="Course"
                      entity="Course"
                      name="courseId"
                      selectValueMark="id"
                      selectLabelMark="name"
                      aqlColumns="code,name,currentlyOffered,isShownOnWeb,reportableHours,nextAvailableCode"
                      selectFilterCondition={courseFilterCondition}
                      defaultDisplayValue={values && values.courseName}
                      itemRenderer={CourseItemRenderer}
                      disabled={!isNew}
                      onInnerValueChange={onCourseIdChange}
                      rowHeight={55}
                      labelAdornment={<LinkAdornment link={values.courseId} linkHandler={openCourseLink} />}
                      required
                    />
                  </Grid>
                  <Grid item xs={twoColumn ? 4 : 12}>
                    <EditInPlaceField
                      label="Class code"
                      input={{
                      onChange: onClassCodeChange,
                      onFocus: stubFunction,
                      onBlur: stubFunction,
                      value: values.courseCode ? `${values.courseCode}-${values.code || ""}` : null
                    }}
                      meta={{
                      error: classCodeError,
                      invalid: Boolean(classCodeError)
                    }}
                      disabled={!values.courseCode}
                    />
                  </Grid>
                </Grid>
            )}
            />
          </Grid>
          {Boolean(values.isCancelled) && (
            <div className={clsx("backgroundText errorColorFade-0-2", twoColumn ? "fs10" : "fs8")}>Cancelled</div>
          )}

          <Grid item xs={12}>
            <FormField type="stub" name="code" required />
            <FormField type="tags" name="tags" tags={tags} validate={validateTagListCallback} />
          </Grid>
        </Grid>

        <Grid
          container
          className="pt-2 pl-3 pr-3"
          columnSpacing={3}
          rowSpacing={2}
          direction={twoColumn && !showAllWeeks ? undefined : "column-reverse"}
        >
          <Grid item xs={twoColumn && !showAllWeeks ? 6 : 12}>
            <div className="heading pb-2 pt-3">Restrictions</div>
            <Typography variant="body2" color="inherit" component="div" className="pb-1">
              Students must be over
              <FormField
                type="number"
                name="minStudentAge"
                min="1"
                max="99"
                step="1"
                props={{
                  formatting: "inline"
                }}
              />
              years old to enrol
            </Typography>

            <Typography variant="body2" color="inherit" component="div" className="pb-2">
              Students must be under
              <FormField
                type="number"
                name="maxStudentAge"
                min="1"
                max="99"
                step="1"
                props={{
                  formatting: "inline"
                }}
              />
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
          <Grid item xs={twoColumn && !showAllWeeks ? 6 : 12}>
            <CourseClassEnrolmentsChart
              classId={values.id}
              classStart={values.startDateTime}
              minEnrolments={values.minimumPlaces}
              maxEnrolments={values.maximumPlaces}
              targetEnrolments={enrolmentsToProfitAllCount}
              openBudget={openBudget}
              showAllWeeks={showAllWeeks}
              setShowAllWeeks={setShowAllWeeks}
              hasBudget={values.budget.some(b => b.invoiceToStudent && b.perUnitAmountIncTax > 0)}
            />
          </Grid>
          <CustomFields
            entityName="CourseClass"
            fieldName="customFields"
            entityValues={values}
            dispatch={dispatch}
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
  tutorRoles: state.preferences.tutorRoles
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(CourseClassGeneralTab);
