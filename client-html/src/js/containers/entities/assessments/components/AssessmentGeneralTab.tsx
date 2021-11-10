/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback } from "react";
import { connect } from "react-redux";
import Grid from "@mui/material/Grid";
import { Assessment, GradingType, Tag } from "@api/model";
import FormControlLabel from "@mui/material/FormControlLabel";
import FormField from "../../../../common/components/form/formFields/FormField";
import { State } from "../../../../reducers/state";
import { validateTagsList } from "../../../../common/components/form/simpleTagListComponent/validateTagsList";
import { EditViewProps } from "../../../../model/common/ListView";
import FullScreenStickyHeader
  from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";

interface Props extends Partial<EditViewProps<Assessment>> {
  tags?: Tag[];
  classes?: any;
  gradingTypes?: GradingType[];
  isScrolling?: boolean;
}

const AssessmentGeneralTab = React.memo<Props>(
  (
    {
    tags,
    twoColumn,
    values,
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
        placeholder={twoColumn ? "Code" : undefined}
        required
        fullWidth
      />
    );
    const assessmentNameField = (
      <FormField
        label="Name"
        name="name"
        placeholder={twoColumn ? "Name" : undefined}
        required
        fullWidth
      />
    );

    return (
      <>
        {twoColumn && (
          <FullScreenStickyHeader
            twoColumn={twoColumn}
            title={(
              <Grid container columnSpacing={3}>
                <Grid item xs={4}>
                  <span className="d-block text-truncate text-nowrap">
                    {values && values.code}
                  </span>
                </Grid>
                <Grid item xs={8}>
                  <span className="d-block text-truncate text-nowrap">
                    {values && values.name}
                  </span>
                </Grid>
              </Grid>
            )}
            fields={(
              <Grid container columnSpacing={3} className="flex-fill">
                <Grid item xs={4}>
                  {assessmentCodeField}
                </Grid>
                <Grid item xs={7}>
                  {assessmentNameField}
                </Grid>
              </Grid>
            )}
          />
        )}
        <Grid container columnSpacing={3} className="p-3">
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
              allowEmpty
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
