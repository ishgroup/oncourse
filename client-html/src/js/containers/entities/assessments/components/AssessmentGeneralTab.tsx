/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback } from "react";
import { connect } from "react-redux";
import Grid from "@material-ui/core/Grid";
import { Assessment, GradingType, Tag } from "@api/model";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Button from "@material-ui/core/Button";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { State } from "../../../../reducers/state";
import { validateTagsList } from "../../../../common/components/form/simpleTagListComponent/validateTagsList";
import { EditViewProps } from "../../../../model/common/ListView";
import CustomAppBar from "../../../../common/components/layout/CustomAppBar";
import AppBarHelpMenu from "../../../../common/components/form/AppBarHelpMenu";
import FormSubmitButton from "../../../../common/components/form/FormSubmitButton";

interface Props extends Partial<EditViewProps<Assessment>> {
  tags?: Tag[];
  classes?: any;
  gradingTypes?: GradingType[];
}

const AssessmentGeneralTab = React.memo<Props>(
  (
    {
    tags,
    twoColumn,
    values,
    isNew,
    manualLink,
    rootEntity,
    onCloseClick,
    invalid,
    dirty,
    gradingTypes = []
  }
) => {
    const validateTagListCallback = useCallback(
      (value, allValues, props) => (tags && tags.length ? validateTagsList(tags, value, allValues, props) : undefined),
      [tags]
    );
    const assessmentCodeField = (
      <FormField
        label="Code"
        name="code"
        type={twoColumn ? "headerText" : "text"}
        placeholder={twoColumn ? "Code" : undefined}
        required
        fullWidth
      />
    );
    const assessmentNameField = (
      <FormField
        label="Name"
        name="name"
        type={twoColumn ? "headerText" : "text"}
        placeholder={twoColumn ? "Name" : undefined}
        required
        fullWidth
      />
    );

    return (
      <>
        {twoColumn && (
          <CustomAppBar>
            <Grid container className="flex-fill">
              <Grid item xs={3}>
                {assessmentCodeField}
              </Grid>
              <Grid item xs={8}>
                {assessmentNameField}
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
                disabled={(!isNew && !dirty)}
                invalid={invalid}
              />
            </div>
          </CustomAppBar>
        )}
        <Grid container className="p-3">
          {!twoColumn && (
            <>
              <Grid item xs={12}>
                {assessmentCodeField}
              </Grid>

              <Grid item xs={12}>
                {assessmentNameField}
              </Grid>
            </>
          )}
          <Grid item xs={12}>
            <FormField
              type="tags"
              name="tags"
              tags={tags}
              validate={validateTagListCallback}
            />
          </Grid>
          <Grid item xs={12}>
            <FormField
              type="select"
              name="gradingTypeId"
              label="Grading type"
              selectValueMark="id"
              selectLabelMark="name"
              items={gradingTypes}
            />
          </Grid>
          <Grid item xs={12}>
            <FormControlLabel
              className="checkbox mb-2"
              control={<FormField type="checkbox" name="active" color="secondary" fullWidth />}
              label="Active"
            />
          </Grid>
          <Grid item xs={12}>
            <FormField
              type="multilineText"
              name="description"
              label="Description"
              required
              fullWidth
            />
          </Grid>
        </Grid>
      </>
    );
  }
);

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags["Assessment"],
  gradingTypes: state.preferences.gradingTypes
});

export default connect<any, any, any>(
  mapStateToProps,
  null
)(AssessmentGeneralTab);
