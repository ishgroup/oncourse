/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Room, Site } from '@api/model';
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';
import ScreenShare from '@mui/icons-material/ScreenShare';
import { Collapse, FormControlLabel, Grid } from '@mui/material';
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';
import $t from '@t';
import { normalizeNumber, openInternalLink, TimetableButton } from 'ish-ui';
import * as React from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { arrayInsert, arrayRemove } from 'redux-form';
import FormField from '../../../../common/components/form/formFields/FormField';
import MinifiedEntitiesList from '../../../../common/components/form/minifiedEntitiesList/MinifiedEntitiesList';
import CoordinatesValueUpdater from '../../../../common/components/google-maps/CoordinatesValueUpdater';
import StaticGoogleMap from '../../../../common/components/google-maps/StaticGoogleMap';
import FullScreenStickyHeader
  from '../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader';
import {
  greaterThanNullValidation,
  validateSingleMandatoryField,
  validateURL
} from '../../../../common/utils/validation';
import { EditViewProps } from '../../../../model/common/ListView';
import { State } from '../../../../reducers/state';
import { EntityChecklists } from '../../../tags/components/EntityChecklists';
import CustomFields from '../../customFieldTypes/components/CustomFieldsTypes';
import { validateDeleteRoom } from '../../rooms/actions';
import { openRoomLink } from '../../rooms/utils';

const validateRooms = (value: Room[]) => {
  let error;

  if (Array.isArray(value) && value.length && value.some(r => !r.name || typeof r.seatedCapacity !== "number")) {
    error = "Some rooms are missing required fields";
  }

  return error;
};

const openRoom = (entity, id) => openRoomLink(id);

export const validateRoomUniqueName = (value, allValues) => {
  const matches = allValues.rooms.filter(item => item.name && item.name.trim() === value.trim());

  return matches.length > 1 ? "Room name must be unique" : undefined;
};

const SitesRoomFields = ({ item, isParenSiteVirtual }) => (
  <Grid container columnSpacing={3} rowSpacing={2}>
    <Grid item xs={12}>
      <FormField
        type="text"
        name={`${item}.name`}
        label={$t('name')}
        className="mr-2"
        debounced={false}
        validate={validateRoomUniqueName}
        required
      />
    </Grid>
    <Grid item xs={12}>
      <FormField
        type="number"
        name={`${item}.seatedCapacity`}
        label={$t('seated_capacity')}
        normalize={normalizeNumber}
        debounced={false}
        required
      />
    </Grid>

    {isParenSiteVirtual && <Grid item xs={12}><FormField
      type="text"
      label={$t('virtual_room_url')}
      name={`${item}.virtualRoomUrl`}
      validate={validateURL}
    /></Grid>}
  </Grid>
);

const getLayoutArray = (twoColumn: boolean): { [key: string]: any }[] =>
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
    openInternalLink(`/timetable?search=room.site.id=${values?.id}&title=Timetable for ${values?.name}`);
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
      syncErrors,
    } = this.props;

    const { addressString } = this.state;

    const layoutArray = getLayoutArray(twoColumn);

    return (
      <Grid container columnSpacing={3} rowSpacing={2} className="pt-3 pl-3 pr-3">
        <CoordinatesValueUpdater
          dispatch={dispatch}
          latPath="latitude"
          longPath="longitude"
          addressString={addressString}
          form={form}
        />

        <Grid item xs={layoutArray[2].xs}>
          <FullScreenStickyHeader
            opened={!values.id || Object.keys(syncErrors).includes("name")}
            twoColumn={twoColumn}
            title={values && values.name}
            fields={(
              <Grid item xs={twoColumn ? 4 : 12}>
                <FormField
                  type="text"
                  name="name"
                  label={$t('name')}
                  required
                />
              </Grid>
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
                entity="Site"
                form={form}
                entityId={values.id}
                checked={values.tags}
              />

              <div className="centeredFlex ml-2">
                <IconButton href={values.kioskUrl} disabled={!values.kioskUrl} target="_blank">
                  <ScreenShare />
                </IconButton>
                <Typography variant="caption">{$t('kiosk')}</Typography>
              </div>
            </div>
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
              label={$t('administration_center')}
            />

            <FormControlLabel
              className="checkbox pr-3"
              control={<FormField type="checkbox" name="isVirtual" color="secondary" />}
              label={$t('virtual_site')}
            />

            <FormControlLabel
              className="checkbox"
              control={<FormField type="checkbox" name="isShownOnWeb" color="secondary" />}
              label={$t('show_this_site_on_the_website')}
            />
          </div>
        </Grid>
   
        {timezones && (
          <Grid item xs={layoutArray[2].xs} className="mb-2">
            <FormField
              type="select"
              name="timezone"
              label={$t('default_timezone')}
              items={timezones}
              labelAdornment={(
                <Tooltip title={$t('timetables_will_be_adjusted_to_users_timezone_wher')}>
                  <IconButton classes={{ root: "inputAdornmentButton" }}>
                    <InfoOutlinedIcon className="inputAdornmentIcon" color="inherit" />
                  </IconButton>
                </Tooltip>
                )}
              validate={validateSingleMandatoryField}
            />
          </Grid>
        )}

        <Collapse in={!values.isVirtual}>
          <Grid container columnSpacing={3} className="pr-3 pl-3">
            <Grid container item xs={layoutArray[2].xs} columnSpacing={3} rowSpacing={2}>
              <Grid item xs={12}>
                <FormField
                  type="text"
                  name="street"
                  label={$t('street')}
                  validate={greaterThanNullValidation}
                  onBlur={this.updateLatLong}
                />
              </Grid>
              <Grid item xs={12}>
                <FormField type="text" name="suburb" label={$t('suburb')} onBlur={this.updateLatLong} />
              </Grid>
              <Grid item xs={12}>
                <FormField type="text" name="state" label={$t('state')} />
              </Grid>
              <Grid item xs={12}>
                <FormField type="text" name="postcode" label={$t('postcode')} />
              </Grid>
              <Grid item xs={12}>
                {Boolean(countries?.length) && (
                  <FormField
                    type="select"
                    selectValueMark="id"
                    selectLabelMark="name"
                    name="country"
                    label={$t('country')}
                    returnType="object"
                    onBlur={this.updateLatLong}
                    required={!values.isVirtual}
                    items={countries}
                  />
                )}
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

        <CustomFields
          entityName="Site"
          fieldName="customFields"
          entityValues={values}
          form={form}
          gridItemProps={{
            xs: twoColumn ? 6 : 12,
          }}
        />

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
            fieldProps={{
              isParenSiteVirtual: values.isVirtual
            }}
          />
        </Grid>
      </Grid>
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