/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect, useMemo, useState } from "react";
import clsx from "clsx";
import { connect } from "react-redux";
import { change } from "redux-form";
import Grid from "@material-ui/core/Grid";
import { Dispatch } from "redux";
import { Tag } from "@api/model";
import Typography from "@material-ui/core/Typography";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Collapse from "@material-ui/core/Collapse";
import Button from "@material-ui/core/Button";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import FormSubmitButton from "../../../../../common/components/form/FormSubmitButton";
import { State } from "../../../../../reducers/state";
import { validateTagsList } from "../../../../../common/components/form/simpleTagListComponent/validateTagsList";
import EditInPlaceField from "../../../../../common/components/form/form-fields/EditInPlaceField";
import { courseFilterCondition, openCourseLink } from "../../../courses/utils";
import CourseItemRenderer from "../../../courses/components/CourseItemRenderer";
import { LinkAdornment } from "../../../../../common/components/form/FieldAdornments";
import { EditViewProps } from "../../../../../model/common/ListView";
import { CourseClassExtended } from "../../../../../model/entities/CourseClass";
import CourseClassEnrolmentsChart from "./CourseClassEnrolmentsChart";
import CustomAppBar from "../../../../../common/components/layout/CustomAppBar";
import AppBarHelpMenu from "../../../../../common/components/form/AppBarHelpMenu";
import HeaderTextField from "../../../../../common/components/form/form-fields/HeaderTextField";
import { stubFunction } from "../../../../../common/utils/common";
import { showMessage } from "../../../../../common/actions";
import { AppMessage } from "../../../../../model/common/Message";
import history from "../../../../../constants/History";
import { decimalMinus, decimalPlus } from "../../../../../common/utils/numbers/decimalCalculation";
import { getClassCostTypes } from "../../utils";
import CustomFields from "../../../customFieldTypes/components/CustomFieldsTypes";

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
    manualLink,
    rootEntity,
    onCloseClick,
    invalid,
    dirty,
    dispatch,
    form,
    showMessage,
    toogleFullScreenEditView,
    tutorRoles
  }) => {
    const [classCodeError, setClassCodeError] = useState(null);
    const [showAllWeeks, setShowAllWeeks] = useState(false);

    const onClose = () => {
      onCloseClick();
    };

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

    const courseIdFieldTwoColumnProps = useMemo(
      () => ({
        placeholder: "Course",
        endAdornment: (
          <LinkAdornment
            link={values.courseId}
            linkHandler={openCourseLink}
            linkColor="inherit"
            className="appHeaderFontSize pl-0-5"
          />
        ),
        formatting: "inline",
        fieldClasses: {
          text: "appHeaderFontSize primaryContarstText primaryContarstHover text-nowrap text-truncate",
          input: "primaryContarstText",
          underline: "primaryContarstUnderline",
          selectMenu: "textPrimaryColor",
          loading: "primaryContarstText",
          editIcon: "primaryContarstText"
        }
      }),
      [values.courseId]
    );

    const courseIdFieldThreecolumnProps = useMemo(
      () => ({
        labelAdornment: <LinkAdornment link={values.courseId} linkHandler={openCourseLink} />,
        label: "Course"
      }),
      []
    );

    const courseIdField = (
      <FormField
        type="remoteDataSearchSelect"
        entity="Course"
        name="courseId"
        selectValueMark="id"
        selectLabelMark="name"
        aqlColumns="code,name,currentlyOffered,isShownOnWeb,reportableHours,nextAvailableCode"
        selectFilterCondition={courseFilterCondition}
        defaultDisplayValue={values && values.courseName}
        itemRenderer={CourseItemRenderer}
        disabled={!isNew}
        props={twoColumn ? courseIdFieldTwoColumnProps : courseIdFieldThreecolumnProps}
        onInnerValueChange={onCourseIdChange}
        rowHeight={55}
        required
      />
    );

    const classCodeProps = useMemo(
      () => ({
        label: "Class code",
        input: {
          onChange: onClassCodeChange,
          onFocus: stubFunction,
          onBlur: stubFunction,
          value: values.courseCode ? `${values.courseCode}-${values.code || ""}` : null
        },
        meta: {
          error: classCodeError,
          invalid: Boolean(classCodeError)
        },
        fullWidth: true,
        disabled: !values.courseCode
      }),
      [values.code, values.courseCode, classCodeError]
    );

    const classCodeField = useMemo(
      () => (twoColumn ? (
        <HeaderTextField {...classCodeProps} placeholder="Class code" />
        ) : (
          <EditInPlaceField {...classCodeProps} />
        )),
      [twoColumn, classCodeProps, values.code, values.courseCode]
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
        values.tutorAttendance
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
            values.tutorAttendance
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
        {twoColumn && (
          <CustomAppBar>
            <Grid container className="flex-fill">
              <Grid item xs={6} className="pr-2">
                {courseIdField}
              </Grid>
              <Grid item xs={4}>
                {classCodeField}
              </Grid>
            </Grid>
            <div>
              {manualLink && (
                <AppBarHelpMenu
                  created={values ? new Date(values.createdOn) : null}
                  modified={values ? new Date(values.modifiedOn) : null}
                  auditsUrl={`audit?search=~"${rootEntity}" and entityId in (${values ? values.id : 0})`}
                  manualUrl={manualLink}
                />
              )}

              <Button onClick={onClose} className="closeAppBarButton">
                Close
              </Button>
              <FormSubmitButton
                disabled={(!isNew && !dirty)}
                invalid={invalid}
              />
            </div>
          </CustomAppBar>
        )}
        <Grid container className="pl-3 pt-3 pr-3 relative">
          {Boolean(values.isCancelled) && (
            <div className={clsx("backgroundText errorColorFade-0-2", twoColumn ? "fs10" : "fs8")}>Cancelled</div>
          )}
          {!twoColumn && (
            <>
              <Grid item xs={12}>
                {courseIdField}
              </Grid>

              <Grid item xs={12}>
                {classCodeField}
              </Grid>
            </>
          )}

          <Grid item xs={12}>
            <FormField type="stub" name="code" required />
            <FormField type="tags" name="tags" tags={tags} validate={validateTagListCallback} />
          </Grid>
        </Grid>

        <Grid
          container
          className="pl-3 pr-3"
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
              fullWidth
            />

            <CustomFields
              entityName="CourseClass"
              fieldName="customFields"
              entityValues={values}
              dispatch={dispatch}
              form={form}
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
