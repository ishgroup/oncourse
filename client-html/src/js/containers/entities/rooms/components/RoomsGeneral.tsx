/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Grid, { GridSize } from "@mui/material/Grid";
import { change, FieldArray } from "redux-form";
import ScreenShare from "@mui/icons-material/ScreenShare";
import IconButton from "@mui/material/IconButton";
import Typography from "@mui/material/Typography";
import { connect } from "react-redux";
import FormField from "../../../../common/components/form/formFields/FormField";
import { FormEditorField } from "../../../../common/components/form/formFields/FormEditor";
import { State } from "../../../../reducers/state";
import DocumentsRenderer from "../../../../common/components/form/documents/DocumentsRenderer";
import { LinkAdornment, openInternalLink, TimetableButton } from  "ish-ui";
import { openSiteLink } from "../../sites/utils";
import FullScreenStickyHeader
  from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";
import { EntityChecklists } from "../../../tags/components/EntityChecklists";

const normalizeSeatedCapacity = value => ((value && value >= 0) || value === 0 ? Number(value) : null);

const getLayoutArray = (twoColumn: boolean): { [key: string]: GridSize }[] =>
  (twoColumn
    ? [{ xs: 12 }, { xs: 12 }, { xs: 4 }, { xs: 4 }, { xs: 4 }, { xs: 12 }, { xs: 12 }, { xs: 12 }]
    : [{ xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }]);

class RoomsGeneral extends React.PureComponent<any, any> {
  onCalendarClick = () => {
    const { values, sites } = this.props;
    const site = sites.find(el => el.value === values.siteId);
    openInternalLink(
      `/timetable?search=room.id=${values.id}&title=Timetable for ${values.name}, ${
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
      twoColumn,
      syncErrors
    } = this.props;

    const layoutArray = getLayoutArray(twoColumn);

    return (
      <Grid container columnSpacing={3} rowSpacing={2} className="p-3">
        <Grid item xs={layoutArray[2].xs}>
          <FullScreenStickyHeader
            opened={!values.id || Object.keys(syncErrors).includes("name")}
            twoColumn={twoColumn}
            title={values && values.name}
            fields={(
              <FormField
                type="text"
                name="name"
                label="Name"
                required
              />
            )}
          />
        </Grid>
        <Grid item container xs={layoutArray[0].xs} columnSpacing={3} rowSpacing={2}>
          <Grid item xs={twoColumn ? 8 : 12}>
            <FormField
              type="tags"
              name="tags"
              tags={tags}
            />
          </Grid>

          <Grid item xs={twoColumn ? 4 : 12}>
            <div className="centeredFlex">
              <EntityChecklists
                className="flex-fill"
                entity="Room"
                form={form}
                entityId={values.id}
                checked={values.tags}
              />

              <div className="centeredFlex ml-2">
                <IconButton href={values.kioskUrl} disabled={!values.kioskUrl} target="_blank">
                  <ScreenShare />
                </IconButton>
                <Typography variant="caption">Kiosk</Typography>
              </div>
            </div>
          </Grid>
        </Grid>

        <Grid item xs={12} className="mb-2">
          <TimetableButton onClick={this.onCalendarClick} />
        </Grid>

        <Grid item xs={layoutArray[1].xs}>
          <Grid container columnSpacing={3} rowSpacing={2}>
            <Grid item xs={layoutArray[3].xs}>
              <FormField
                type="text"
                name="seatedCapacity"
                label="Seated Capacity"
                required
                normalize={normalizeSeatedCapacity}
                debounced={false}
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
                      link={values && values.siteId}
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

        <Grid item xs={layoutArray[7].xs} className="mb-1">
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
    );
  }
}

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags["Room"],
  sites: state.plainSearchRecords["Site"].items
});

export default connect<any, any, any>(mapStateToProps, null)(RoomsGeneral);
