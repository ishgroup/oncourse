/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Assessment, GradingType, Tag } from "@api/model";
import FormControlLabel from "@mui/material/FormControlLabel";
import Grid from "@mui/material/Grid";
import React from "react";
import { connect } from "react-redux";
import FormField from "../../../../common/components/form/formFields/FormField";
import FullScreenStickyHeader
  from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";
import { EditViewProps } from "../../../../model/common/ListView";
import { State } from "../../../../reducers/state";
import { EntityChecklists } from "../../../tags/components/EntityChecklists";

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
    gradingTypes = [],
    syncErrors,
    isNew,
    form
  }
) => (
  <Grid container columnSpacing={3} rowSpacing={2} className="p-3">
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
                type="text"
                label="Code"
                name="code"
                placeholder={twoColumn ? "Code" : undefined}
                required
             />
            </Grid>
            <Grid item xs={twoColumn ? 4 : 12}>
              <FormField
                type="text"
                label="Name"
                name="name"
                placeholder={twoColumn ? "Name" : undefined}
                required
              />
            </Grid>
          </Grid>
        )}
      />
    </Grid>

    <Grid item xs={twoColumn ? 8 : 12}>
      <FormField
        type="tags"
        name="tags"
        tags={tags}
      />
    </Grid>

    <Grid item xs={twoColumn ? 4 : 12}>
      <EntityChecklists
        entity="Assessment"
        form={form}
        entityId={values.id}
        checked={values.tags}
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
        control={<FormField type="checkbox" name="active" color="secondary"  />}
        label="Active"
      />
    </Grid>
    <Grid item xs={12}>
      <FormField
        type="multilineText"
        name="description"
        label="Description"
        required
              />
    </Grid>
  </Grid>
    )
);

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags["Assessment"],
  gradingTypes: state.preferences.gradingTypes
});

export default connect<any, any, any>(
  mapStateToProps,
  null
)(AssessmentGeneralTab);
