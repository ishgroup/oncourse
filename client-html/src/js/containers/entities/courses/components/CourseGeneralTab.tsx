/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useCallback, useMemo
} from "react";
import { connect } from "react-redux";
import { change } from "redux-form";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Grid from "@material-ui/core/Grid";
import Button from "@material-ui/core/Button";
import makeStyles from "@material-ui/core/styles/makeStyles";
import { CourseEnrolmentType, CourseStatus, Tag } from "@api/model";
import FormField from "../../../../common/components/form/form-fields/FormField";
import FormSubmitButton from "../../../../common/components/form/FormSubmitButton";
import { State } from "../../../../reducers/state";
import { validateTagsList } from "../../../../common/components/form/simpleTagListComponent/validateTagsList";
import { openInternalLink } from "../../../../common/utils/links";
import CustomAppBar from "../../../../common/components/layout/CustomAppBar";
import AppBarHelpMenu from "../../../../common/components/form/AppBarHelpMenu";
import { EditViewProps } from "../../../../model/common/ListView";
import { PreferencesState } from "../../../preferences/reducers/state";
import NestedEntity from "../../../../common/components/form/nestedEntity/NestedEntity";
import TimetableButton from "../../../../common/components/buttons/TimetableButton";
import { CourseExtended } from "../../../../model/entities/Course";
import CustomFields from "../../customFieldTypes/components/CustomFieldsTypes";
import { mapSelectItems } from "../../../../common/utils/common";
import CourseAvailableClassChart from "./CourseAvailableClassChart";

const CourseEnrolmentTypes = Object.keys(CourseEnrolmentType).map(mapSelectItems);
const CourseStatusTypes = Object.keys(CourseStatus).map(mapSelectItems);

interface CourseGeneralTabProps extends EditViewProps<CourseExtended> {
  tags: Tag[];
  dataCollectionRules: PreferencesState["dataCollectionRules"];
  dispatch: any;
  form: string;
}

const useStyles = makeStyles(() => ({
  chartWrapper: {
    height: "250px",
  },
}));

const CourseGeneralTab = React.memo<CourseGeneralTabProps>(
  ({
    showConfirm,
    tags,
    dataCollectionRules,
    twoColumn,
    values,
    invalid,
    isNew,
    dirty,
    manualLink,
    rootEntity,
    onCloseClick,
    dispatch,
    form
  }) => {
    const classes = useStyles();

    const validateTagList = useCallback((value, allValues, props) => validateTagsList(tags, value, allValues, props), [tags]);

    const onCalendarClick = useCallback(() => {
      openInternalLink(`/timetable/search?query=courseClass.course.id=${values.id}`);
    }, [values.id]);

    const onIsTraineeshipChange = useCallback(
      (e, value) => {
        if (value) {
          dispatch(change(form, "isSufficientForQualification", value));
        }
      },
      [form]
    );

    const waitingListTypes = useMemo(
      () => [
        {
          name: `${values.studentWaitingListCount ? `S` : "No s"}tudents on waiting list`,
          count: values.studentWaitingListCount,
          disabled: !values.studentWaitingListCount,
          link: `/waitingList?search=course.id is ${values.id}`
        }
      ],
      [values.studentWaitingListCount, values.id]
    );

    const headComponentsType = twoColumn ? "headerText" : "text";

    const nameField = (
      <FormField
        type={headComponentsType}
        name="name"
        label="Name"
        placeholder={twoColumn ? "Name" : undefined}
        required
        fullWidth
      />
    );
    const codeField = (
      <FormField
        type={headComponentsType}
        name="code"
        label="Code"
        placeholder={twoColumn ? "Code" : undefined}
        required
        fullWidth
      />
    );

    return (
      <>
        {twoColumn && (
          <CustomAppBar>
            <Grid container className="flex-fill">
              <Grid item xs={6}>
                {nameField}
              </Grid>
              <Grid item xs={4} className="pl-2">
                {codeField}
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

              <Button onClick={onCloseClick} className="closeAppBarButton">
                Close
              </Button>

              <FormSubmitButton
                invalid={invalid}
                disabled={(!isNew && !dirty)}
              />
            </div>
          </CustomAppBar>
        )}

        <Grid container className="pt-3 pl-3 pr-3">
          {!twoColumn && (
            <>
              <Grid item xs={12}>
                {nameField}
              </Grid>

              <Grid item xs={12}>
                {codeField}
              </Grid>
            </>
          )}

          <Grid item xs={12} className="mb-2">
            <FormField
              type="tags"
              name="tags"
              tags={tags}
              validate={tags && tags.length ? validateTagList : undefined}
            />
          </Grid>

          <Grid item xs={12} className={classes.chartWrapper}>
            <CourseAvailableClassChart courseId={values.id} isNew={isNew} />
          </Grid>

          <Grid item xs={12} className="mb-2">
            <TimetableButton onClick={onCalendarClick} />
          </Grid>

          {!values.isTraineeship && (
            <>
              <Grid item xs={twoColumn ? 4 : 12}>
                <FormField
                  type="select"
                  name="enrolmentType"
                  label="Enrolment type"
                  items={CourseEnrolmentTypes}
                  disabled={values.isTraineeship}
                />
              </Grid>
              <Grid item xs={twoColumn ? 4 : 12}>
                <FormField
                  type="select"
                  name="status"
                  label="Status"
                  items={CourseStatusTypes}
                  disabled={values.isTraineeship}
                />
              </Grid>
            </>
          )}
          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="select"
              name="dataCollectionRuleId"
              defaultValue={values.dataCollectionRuleName}
              label="Data collection rule"
              selectValueMark="id"
              selectLabelMark="name"
              items={dataCollectionRules || []}
              sort
              required
            />
          </Grid>

          <Grid item xs={12} className="centeredFlex mb-2">
            <FormControlLabel
              className="checkbox"
              control={(
                <FormField
                  type="checkbox"
                  name="isTraineeship"
                  disabled={!isNew}
                  onChange={onIsTraineeshipChange}
                />
              )}
              label="Traineeship"
            />
          </Grid>

          <Grid item xs={12} className="centeredFlex mb-2">
            <FormControlLabel
              className="checkbox"
              control={<FormField type="checkbox" name="allowWaitingLists" />}
              label="Allows Waiting lists"
            />
          </Grid>

          {values.isTraineeship && (
            <Grid item xs={12} className="centeredFlex mb-2">
              <FormControlLabel
                className="checkbox"
                control={<FormField type="checkbox" name="currentlyOffered" />}
                label="Currently offered"
              />
            </Grid>
          )}

          <Grid item xs={12}>
            <NestedEntity
              entityTypes={waitingListTypes}
              dirty={dirty}
              showConfirm={showConfirm}
              twoColumn={twoColumn}
            />
          </Grid>

          <Grid item xs={12}>
            <FormField
              type="multilineText"
              name="brochureDescription"
              label="Print brochure description"
              fullWidth
            />

            <CustomFields
              entityName="Course"
              fieldName="customFields"
              entityValues={values}
              dispatch={dispatch}
              form={form}
            />
          </Grid>
        </Grid>
      </>
    );
  }
);

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags.Course,
  dataCollectionRules: state.preferences.dataCollectionRules
});

export default connect<any, any, any>(mapStateToProps, null)(CourseGeneralTab);
