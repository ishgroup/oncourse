/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback } from "react";
import { connect } from "react-redux";
import Grid from "@material-ui/core/Grid";
import { Tag } from "@api/model";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Button from "@material-ui/core/Button";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { State } from "../../../../reducers/state";
import { validateTagsList } from "../../../../common/components/form/simpleTagListComponent/validateTagsList";
import { EditViewProps } from "../../../../model/common/ListView";
import { CourseClassExtended } from "../../../../model/entities/CourseClass";
import CustomAppBar from "../../../../common/components/layout/CustomAppBar";
import AppBarHelpMenu from "../../../../common/components/form/AppBarHelpMenu";

interface Props extends Partial<EditViewProps> {
  tags?: Tag[];
  values?: CourseClassExtended;
  classes?: any;
}

const AssessmentGeneralTab = React.memo<Props>(
  ({
    tags, twoColumn, values, isNew, manualLink, rootEntity, onCloseClick, invalid, dirty
  }) => {
    const validateTagListCallback = useCallback(
      (value, allValues, props) => (tags && tags.length ? validateTagsList(tags, value, allValues, props) : undefined),
      [tags]
    );

    const classCodeField = (
      <FormField
        label="Code"
        name="code"
        type={twoColumn ? "headerText" : "text"}
        placeholder={twoColumn ? "Code" : undefined}
        required
        fullWidth
      />
    );

    const classNameField = (
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
                {classCodeField}
              </Grid>
              <Grid item xs={8}>
                {classNameField}
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
              <Button
                type="submit"
                classes={{
                  root: "whiteAppBarButton",
                  disabled: "whiteAppBarButtonDisabled"
                }}
                disabled={invalid || (!isNew && !dirty)}
              >
                Save
              </Button>
            </div>
          </CustomAppBar>
        )}
        <Grid container className="pl-3 pt-3 pr-3 relative">
          {!twoColumn && (
            <>
              <Grid item xs={12}>
                {classCodeField}
              </Grid>

              <Grid item xs={12}>
                {classNameField}
              </Grid>
            </>
          )}
        </Grid>

        <Grid container className="pl-3 pr-3">
          <Grid item xs={12} className="pb-2">
            <FormField
              type="tags"
              name="tags"
              tags={tags}
              validate={validateTagListCallback}
            />
          </Grid>
          <Grid item xs={12}>
            <FormControlLabel
              className="checkbox pr-3"
              control={<FormField type="checkbox" name="active" color="secondary" fullWidth />}
              label="Active"
            />
          </Grid>
          <Grid item xs={12}>
            <FormField
              type="multilineText"
              name="description"
              label="Description"
              className="pt-2"
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
  tags: state.tags.entityTags["Assessment"]
});

export default connect<any, any, any>(
  mapStateToProps,
  null
)(AssessmentGeneralTab);
