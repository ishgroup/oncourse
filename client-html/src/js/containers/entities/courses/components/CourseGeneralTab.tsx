/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useCallback, useMemo
} from "react";
import { connect } from "react-redux";
import { change } from "redux-form";
import FormControlLabel from "@mui/material/FormControlLabel";
import Grid from "@mui/material/Grid";
import { CourseEnrolmentType, CourseStatus, Tag } from "@api/model";
import FormField from "../../../../common/components/form/formFields/FormField";
import { State } from "../../../../reducers/state";
import { validateTagsList } from "../../../../common/components/form/simpleTagListComponent/validateTagsList";
import { openInternalLink } from "../../../../common/utils/links";
import { EditViewProps } from "../../../../model/common/ListView";
import { PreferencesState } from "../../../preferences/reducers/state";
import NestedEntity from "../../../../common/components/form/nestedEntity/NestedEntity";
import TimetableButton from "../../../../common/components/buttons/TimetableButton";
import { CourseExtended } from "../../../../model/entities/Course";
import CustomFields from "../../customFieldTypes/components/CustomFieldsTypes";
import { mapSelectItems } from "../../../../common/utils/common";
import CourseAvailableClassChart from "./CourseAvailableClassChart";
import { makeAppStyles } from "../../../../common/styles/makeStyles";
import FullScreenStickyHeader
  from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";

const CourseEnrolmentTypes = Object.keys(CourseEnrolmentType).map(mapSelectItems);
const CourseStatusTypes = Object.keys(CourseStatus).map(mapSelectItems);

interface CourseGeneralTabProps extends EditViewProps<CourseExtended> {
  tags: Tag[];
  dataCollectionRules: PreferencesState["dataCollectionRules"];
  dispatch: any;
  form: string;
}

const useStyles = makeAppStyles()(() => ({
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
    isNew,
    dirty,
     syncErrors,
    dispatch,
    form
  }) => {
    const { classes } = useStyles();

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
    
    return (
      <Grid container columnSpacing={3} rowSpacing={2} className="pt-3 pl-3 pr-3">
        <Grid item xs={12}>
          <FullScreenStickyHeader
            opened={isNew || Object.keys(syncErrors).some(k => ['code', 'name'].includes(k))}
            twoColumn={twoColumn}
            title={twoColumn ? (
              <div className="d-inline-flex-center">
                <span>
                  {values && values.code}
                </span>
                <span className="ml-2">
                  {values && values.name}
                </span>
              </div>
              ) : (
                <div>
                  <div>
                    {values && values.code}
                  </div>
                  <div className="mt-2">
                    {values && values.name}
                  </div>
                </div>
              )}
            fields={(
              <Grid container columnSpacing={3} rowSpacing={2}>
                <Grid item xs={twoColumn ? 2 : 12}>
                  <FormField
                    label="Code"
                    name="code"
                    placeholder={twoColumn ? "Code" : undefined}
                    required
                    fullWidth
                  />
                </Grid>
                <Grid item xs={twoColumn ? 4 : 12}>
                  <FormField
                    label="Name"
                    name="name"
                    placeholder={twoColumn ? "Name" : undefined}
                    required
                    fullWidth
                  />
                </Grid>
              </Grid>
              )}
          />
        </Grid>

        <Grid item xs={12}>
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

        <Grid item xs={12} className="centeredFlex">
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

        <Grid item xs={12} className="centeredFlex">
          <FormControlLabel
            className="checkbox"
            control={<FormField type="checkbox" name="allowWaitingLists" />}
            label="Allows Waiting lists"
          />
        </Grid>

        {values.isTraineeship && (
        <Grid item xs={12} className="centeredFlex">
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
          />
        </Grid>

        <CustomFields
          entityName="Course"
          fieldName="customFields"
          entityValues={values}
          dispatch={dispatch}
          form={form}
          gridItemProps={{
              xs: twoColumn ? 6 : 12,
              lg: twoColumn ? 4 : 12
            }}
        />
      </Grid>
    );
  }
);

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags.Course,
  dataCollectionRules: state.preferences.dataCollectionRules
});

export default connect<any, any, any>(mapStateToProps, null)(CourseGeneralTab);
