/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Grid, { GridSize } from "@material-ui/core/Grid";
import { FieldArray, change } from "redux-form";
import ScreenShare from "@material-ui/icons/ScreenShare";
import IconButton from "@material-ui/core/IconButton";
import Typography from "@material-ui/core/Typography";
import { connect } from "react-redux";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { FormEditorField } from "../../../../common/components/markdown-editor/FormEditor";
import { State } from "../../../../reducers/state";
import { validateTagsList } from "../../../../common/components/form/simpleTagListComponent/validateTagsList";
import DocumentsRenderer from "../../../../common/components/form/documents/DocumentsRenderer";
import { openInternalLink } from "../../../../common/utils/links";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import TimetableButton from "../../../../common/components/buttons/TimetableButton";
import { openSiteLink } from "../../sites/utils";

const normalizeSeatedCapacity = value => ((value && value >= 0) || value === 0 ? Number(value) : null);

const getLayoutArray = (twoColumn: boolean): { [key: string]: GridSize }[] =>
  (twoColumn
    ? [{ xs: 12 }, { xs: 12 }, { xs: 4 }, { xs: 4 }, { xs: 4 }, { xs: 12 }, { xs: 12 }, { xs: 12 }]
    : [{ xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }]);

class RoomsGeneral extends React.PureComponent<any, any> {
  validateTagList = (value, allValues, props) => {
    const { tags } = this.props;

    return validateTagsList(tags, value, allValues, props);
  };

  onCalendarClick = () => {
    const { values, sites } = this.props;
    const site = sites.find(el => el.value === values.siteId);
    openInternalLink(
      `/timetable/search?query=room.id=${values.id}&title=Timetable for ${values.name}, ${
        site ? site.label : ""
      }`
    );
  };

  onSiteChange = site => {
    const { dispatch, form } = this.props;

    dispatch(change(form, "siteTimeZone", site.localTimezone));
  };

  render() {
    const {
      classes,
      isNew,
      values,
      showConfirm,
      dispatch,
      form,
      tags,
      sites,
      twoColumn
    } = this.props;

    const layoutArray = getLayoutArray(twoColumn);

    return (
      <>
        <Grid container className="p-3">
          <Grid item xs={layoutArray[2].xs}>
            <FormField
              type="text"
              name="name"
              label="Name"
              required
              listSpacing={false}
            />
          </Grid>
          <Grid item xs={layoutArray[0].xs}>
            <Grid container className="flex-nowrap align-items-center mb-1">
              <Grid item xs={12} className="container">
                <FormField
                  type="tags"
                  name="tags"
                  tags={tags}
                  validate={tags && tags.length ? this.validateTagList : undefined}
                />
              </Grid>

              <Grid item className="centeredFlex">
                <IconButton href={values.kioskUrl} disabled={!values.kioskUrl} target="_blank">
                  <ScreenShare />
                </IconButton>

                <Typography variant="caption">Kiosk</Typography>
              </Grid>
            </Grid>
          </Grid>

          <Grid item xs={12} className="mb-2">
            <TimetableButton onClick={this.onCalendarClick} />
          </Grid>

          <Grid item xs={layoutArray[1].xs}>
            <Grid container>
              <Grid item xs={layoutArray[3].xs}>
                <FormField
                  type="text"
                  name="seatedCapacity"
                  label="Seated Capacity"
                  required
                  normalize={normalizeSeatedCapacity}
                />
              </Grid>

              <Grid item xs={layoutArray[4].xs}>
                {sites && (
                  <FormField
                    type="select"
                    name="siteId"
                    label="Site"
                    selectLabelMark="name"
                    selectValueMark="id"
                    labelAdornment={
                      isNew ? undefined : (
                        <LinkAdornment
                          link="true"
                          disabled={!values.siteId}
                          clickHandler={() => openSiteLink(values.siteId)}
                        />
                      )
                    }
                    items={sites}
                    onInnerValueChange={this.onSiteChange}
                    required
                  />
                )}
              </Grid>
            </Grid>
          </Grid>

          <Grid item xs={layoutArray[5].xs}>
            <FormEditorField name="facilities" label="Facilities" />
          </Grid>

          <Grid item xs={layoutArray[6].xs}>
            <FormEditorField name="directions" label="Directions" />
          </Grid>

          <Grid item xs={layoutArray[7].xs}>
            <FieldArray
              name="documents"
              label="Documents"
              entity="Room"
              classes={classes}
              component={DocumentsRenderer}
              xsGrid={layoutArray[3].xs}
              mdGrid={layoutArray[4].md}
              lgGrid={layoutArray[5].lg}
              dispatch={dispatch}
              form={form}
              showConfirm={showConfirm}
              rerenderOnEveryChange
            />
          </Grid>
        </Grid>
      </>
    );
  }
}

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags["Room"],
  sites: state.plainSearchRecords["Site"].items
});

export default connect<any, any, any>(mapStateToProps, null)(RoomsGeneral);
