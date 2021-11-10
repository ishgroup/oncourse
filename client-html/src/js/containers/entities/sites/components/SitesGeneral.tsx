/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Room, Site } from "@api/model";
import * as React from "react";
import Grid, { GridSize } from "@mui/material/Grid";
import {
  arrayInsert, arrayRemove
} from "redux-form";
import ScreenShare from "@mui/icons-material/ScreenShare";
import IconButton from "@mui/material/IconButton";
import Typography from "@mui/material/Typography";
import { FormControlLabel } from "@mui/material";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import Collapse from "@mui/material/Collapse";
import Tooltip from "@mui/material/Tooltip";
import InfoOutlinedIcon from "@mui/icons-material/InfoOutlined";
import FormField from "../../../../common/components/form/formFields/FormField";
import { normalizeNumber } from "../../../../common/utils/numbers/numbersNormalizing";
import { validateSingleMandatoryField, greaterThanNullValidation } from "../../../../common/utils/validation";
import MinifiedEntitiesList from "../../../../common/components/form/minifiedEntitiesList/MinifiedEntitiesList";
import { State } from "../../../../reducers/state";
import { validateTagsList } from "../../../../common/components/form/simpleTagListComponent/validateTagsList";
import StaticGoogleMap from "../../../../common/components/google-maps/StaticGoogleMap";
import CoordinatesValueUpdater from "../../../../common/components/google-maps/CoordinatesValueUpdater";
import { validateDeleteRoom } from "../../rooms/actions";
import { openInternalLink } from "../../../../common/utils/links";
import TimetableButton from "../../../../common/components/buttons/TimetableButton";
import { openRoomLink } from "../../rooms/utils";
import { EditViewProps } from "../../../../model/common/ListView";
import FullScreenStickyHeader
  from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";

const validateRooms = (value: Room[]) => {
  let error;

  if (Array.isArray(value) && value.length && value.some(r => !r.name || typeof r.seatedCapacity !== "number")) {
    error = "Some rooms are missing required fields";
  }

  return error;
};

const openRoom = (entity, id) => openRoomLink(id);

const SitesRoomFields = props => {
  const { item } = props;

  return (
    <div className="centeredFlex container">
      <FormField
        type="text"
        name={`${item}.name`}
        label="Name"
        className="mr-2"
        required
      />

      <FormField
        type="number"
        name={`${item}.seatedCapacity`}
        label="Seated Capacity"
        normalize={normalizeNumber}
        required
      />
    </div>
  );
};

const getLayoutArray = (twoColumn: boolean): { [key: string]: GridSize }[] =>
  (twoColumn
    ? [{ xs: 12 }, { xs: 12 }, { xs: 4 }, { xs: 6 }, { xs: 6 }, { xs: 6 }, { xs: 6 }, { xs: 8 }, { xs: 12 }]
    : [{ xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }]);

interface Props {
  tags: any;
  countries: any;
  timezones: any;
  validateDeleteRoom: any;
  isScrolling?: boolean;
}

class SitesGeneral extends React.PureComponent<EditViewProps<Site> & Props, any> {
  state = {
    addressString: null
  };

  onCalendarClick = () => {
    const { values } = this.props;
    openInternalLink(`/timetable/search?query=room.site.id=${values.id}&title=Timetable for ${values.name}`);
  };

  addRoom = () => {
    const { dispatch, values, form } = this.props;

    const newRoom = {
      siteId: values.id,
      name: null,
      seatedCapacity: null
    };

    dispatch(arrayInsert(form, "rooms", 0, newRoom));
  };

  deleteRoom = (index: number, id: number) => {
    const {
     dispatch, showConfirm, form, validateDeleteRoom
    } = this.props;

    const callback = () => dispatch(arrayRemove(form, "rooms", index));

    showConfirm({
      onConfirm: id ? () => validateDeleteRoom(id, callback) : callback,
      confirmMessage: "Room entity will be deleted. This action can not be undone",
      confirmButtonText: "Delete"
    });
  };

  validateTagList = (value, allValues, props) => {
    const { tags } = this.props;

    return validateTagsList(tags, value, allValues, props);
  };

  updateLatLong = () => {
    const { values } = this.props;

    if (values.street && values.suburb && values.country) {
      this.setState({
        addressString: [values.street, values.suburb, values.country.name].filter(v => v).toString()
      });
    }
  };

  render() {
    const {
      values,
      dispatch,
      form,
      twoColumn,
      tags,
      countries,
      timezones,
      syncErrors
    } = this.props;

    const { addressString } = this.state;

    const layoutArray = getLayoutArray(twoColumn);

    return (
      <>
        <Grid container columnSpacing={3} className="p-3">
          <CoordinatesValueUpdater
            dispatch={dispatch}
            latPath="latitude"
            longPath="longitude"
            addressString={addressString}
            form={form}
          />

          <Grid item xs={layoutArray[2].xs}>
            <FullScreenStickyHeader
              twoColumn={twoColumn}
              title={values && values.name}
              fields={(
                <FormField
                  type="text"
                  name="name"
                  label="Name"
                  listSpacing={false}
                  required
                />
              )}
            />
          </Grid>

          <Grid item xs={layoutArray[0].xs}>
            <Grid container columnSpacing={3} className="flex-nowrap align-items-center mb-1">
              <Grid item xs={12}>
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
            <div className="container centeredFlex mb-2">
              <FormControlLabel
                className="checkbox pr-3"
                control={<FormField type="checkbox" name="isAdministrationCentre" color="secondary" />}
                label="Administration center"
              />

              <FormControlLabel
                className="checkbox pr-3"
                control={<FormField type="checkbox" name="isVirtual" color="secondary" />}
                label="Virtual site"
              />

              <FormControlLabel
                className="checkbox"
                control={<FormField type="checkbox" name="isShownOnWeb" color="secondary" />}
                label="Show this site on the website"
              />
            </div>
          </Grid>

          <Collapse in={!values.isVirtual}>
            <Grid container columnSpacing={3}>
              <Grid item xs={layoutArray[2].xs}>
                <FormField
                  type="text"
                  name="street"
                  label="Street"
                  validate={greaterThanNullValidation}
                  onBlur={this.updateLatLong}
                />

                <FormField type="text" name="suburb" label="Suburb" onBlur={this.updateLatLong} />

                <Grid container>
                  <Grid item xs={layoutArray[3].xs}>
                    <FormField type="text" name="state" label="State" />
                  </Grid>
                  <Grid item xs={layoutArray[4].xs}>
                    <FormField type="text" name="postcode" label="Postcode" />
                  </Grid>
                  <Grid item xs={layoutArray[5].xs}>
                    {countries && (
                      <FormField
                        type="searchSelect"
                        selectValueMark="id"
                        selectLabelMark="name"
                        name="country"
                        label="Country"
                        returnType="object"
                        onBlur={this.updateLatLong}
                        validate={values.isVirtual ? undefined : validateSingleMandatoryField}
                        items={countries}
                      />
                    )}
                  </Grid>
                </Grid>
              </Grid>
              <Grid item xs={layoutArray[7].xs}>
                <StaticGoogleMap
                  markerLetter={values.name && values.name[0].toUpperCase()}
                  latitude={values.latitude}
                  longitude={values.longitude}
                  size={twoColumn ? [600, 207] : [600, 300]}
                />
              </Grid>
            </Grid>
          </Collapse>

          <Grid container item xs={12}>
            <Grid item xs={layoutArray[2].xs}>
              {timezones && (
                <FormField
                  type="searchSelect"
                  name="timezone"
                  label="Default timezone"
                  items={timezones}
                  labelAdornment={(
                    <Tooltip title="Timetables will be adjusted to users' timezone where possible, but in cases where it is unknown such as emails, this default will be used.">
                      <IconButton classes={{ root: "inputAdornmentButton" }}>
                        <InfoOutlinedIcon className="inputAdornmentIcon" color="inherit" />
                      </IconButton>
                    </Tooltip>
                  )}
                  validate={validateSingleMandatoryField}
                />
              )}
            </Grid>
          </Grid>

          <Grid item xs={layoutArray[8].xs}>
            <MinifiedEntitiesList
              name="rooms"
              header="Rooms"
              oneItemHeader="Room"
              entity="Room"
              FieldsContent={SitesRoomFields}
              onAdd={this.addRoom}
              onDelete={this.deleteRoom}
              onViewMore={openRoom}
              count={values.rooms && values.rooms.length}
              validate={validateRooms}
              syncErrors={syncErrors}
            />
          </Grid>
        </Grid>
      </>
    );
  }
}

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags["Site"],
  countries: state.countries,
  timezones: state.timezones
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  validateDeleteRoom: (id: string, callback: any) => dispatch(validateDeleteRoom(id, callback))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(SitesGeneral);
