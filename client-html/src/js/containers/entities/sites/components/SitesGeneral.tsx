import { Room, Site } from '@api/model';
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';
import ScreenShare from '@mui/icons-material/ScreenShare';
import { Collapse, FormControlLabel, Grid, IconButton, Tooltip, Typography } from '@mui/material';
import $t from '@t';
import { debounce } from 'es-toolkit/compat';
import { normalizeNumber, openInternalLink, TimetableButton } from 'ish-ui';
import React, { useCallback, useEffect, useRef, useState } from 'react';
import { arrayInsert, arrayRemove, change } from 'redux-form';
import FormField from '../../../../common/components/form/formFields/FormField';
import MinifiedEntitiesList from '../../../../common/components/form/minifiedEntitiesList/MinifiedEntitiesList';
import { getGeocodeDetails } from '../../../../common/components/google-maps/actions';
import StaticGoogleMap from '../../../../common/components/google-maps/StaticGoogleMap';
import FullScreenStickyHeader
  from '../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader';
import { useAppDispatch, useAppSelector } from '../../../../common/utils/hooks';

import {
  greaterThanNullValidation,
  validateSingleMandatoryField,
  validateURL
} from '../../../../common/utils/validation';

import { EditViewProps } from '../../../../model/common/ListView';
import { EntityChecklists } from '../../../tags/components/EntityChecklists';
import CustomFields from '../../customFieldTypes/components/CustomFieldsTypes';
import { openRoomLink } from '../../rooms/utils';

//
// ---- VALIDATORS --------------------------------------------------
//
const validateRooms = (value: Room[]) => {
  let error;
  if (Array.isArray(value) && value.length && value.some(r => !r.name || typeof r.seatedCapacity !== 'number')) {
    error = 'Some rooms are missing required fields';
  }
  return error;
};

export const validateRoomUniqueName = (value: string, allValues: any) => {
  const matches = allValues.rooms.filter(
    (item: Room) => item.name && item.name.trim() === value.trim()
  );
  return matches.length > 1 ? 'Room name must be unique' : undefined;
};

const openRoom = (_: any, id: number) => openRoomLink(id);

//
// ---- SUBCOMPONENT ------------------------------------------------
//
const SitesRoomFields = ({
                           item,
                           isParenSiteVirtual
                         }: {
  item: string;
  isParenSiteVirtual: boolean;
}) => (
  <Grid container columnSpacing={3} rowSpacing={2}>
    <Grid item xs={12}>
      <FormField
        type="text"
        name={`${item}.name`}
        label={$t('name')}
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

    {isParenSiteVirtual && (
      <Grid item xs={12}>
        <FormField
          type="text"
          label={$t('virtual_room_url')}
          name={`${item}.virtualRoomUrl`}
          validate={validateURL}
        />
      </Grid>
    )}
  </Grid>
);

const getLayoutArray = (twoColumn: boolean): { xs: number }[] =>
  twoColumn
    ? [{ xs: 12 }, { xs: 12 }, { xs: 4 }, { xs: 6 }, { xs: 6 }, { xs: 6 }, { xs: 6 }, { xs: 8 }, { xs: 12 }]
    : Array(9).fill({ xs: 12 });

interface Props {
  tags: any;
  countries: any;
  timezones: any;
  validateDeleteRoom: (id: number, callback: () => void) => any;
  isScrolling?: boolean;
}

const SitesGeneral: React.FC<EditViewProps<Site> & Props> = props => {
  const {
    values,
    form,
    showConfirm,
    twoColumn,
    tags,
    countries,
    timezones,
    syncErrors
  } = props;
  
  const isMounted = useRef(false);
  const geo = useAppSelector(state => state.googleApiResponse.geocode);

  const dispatch = useAppDispatch();

  const [addressString, setAddressString] = useState<string | null>(null);

  useEffect(() => {
    isMounted.current = true;
  }, []);
  
  useEffect(() => {
    if (!isMounted.current) return;
    if (addressString) {
      dispatch(getGeocodeDetails(addressString));
    }
  }, [addressString]);

  useEffect(() => {
    if (!isMounted.current || !geo) return;
    if (geo?.lng !== values.longitude) {
      dispatch(change(form, 'longitude', geo.lng));
    }
    if (geo?.lat !== values.latitude) {
      dispatch(change(form, 'latitude', geo.lat));
    }
  }, [geo]);

  const onCalendarClick = useCallback(() => {
    openInternalLink(
      `/timetable?search=room.site.id=${values?.id}&title=Timetable for ${values?.name}`
    );
  }, [values]);
  
  const addRoom = useCallback(() => {
    const newRoom = {
      siteId: values.id,
      name: null,
      seatedCapacity: null
    };
    dispatch(arrayInsert(form, 'rooms', 0, newRoom));
  }, [dispatch, form, values.id]);
  
  const deleteRoom = useCallback(
    (index: number, id: number) => {
      const callback = () => dispatch(arrayRemove(form, 'rooms', index));

      showConfirm({
        onConfirm: id ? () => props.validateDeleteRoom(id, callback) : callback,
        confirmMessage: 'Room entity will be deleted. This action can not be undone',
        confirmButtonText: 'Delete'
      });
    },
    [dispatch, form, props.validateDeleteRoom, showConfirm]
  );
  
  const updateLatLong = useCallback(debounce(() => {
    if (values.street && values.suburb && values.country) {
      setAddressString(
        [values.street, values.suburb, values.country.name].filter(Boolean).join(', ')
      );
    }
  }, 500), [values]);
  
  const layoutArray = getLayoutArray(twoColumn);

  return (
    <Grid container columnSpacing={3} rowSpacing={2} className="pt-3 pl-3 pr-3">

      {/* --- Sticky Header --- */}
      <Grid item xs={layoutArray[2].xs}>
        <FullScreenStickyHeader
          opened={!values.id || Object.keys(syncErrors).includes('name')}
          twoColumn={twoColumn}
          title={values?.name}
          fields={
            <Grid item xs={twoColumn ? 4 : 12}>
              <FormField type="text" name="name" label={$t('name')} required />
            </Grid>
          }
        />
      </Grid>

      {/* --- TAGS + CHECKLISTS --- */}
      <Grid item container xs={layoutArray[0].xs} columnSpacing={3} rowSpacing={2}>
        <Grid item xs={twoColumn ? 8 : 12}>
          <FormField type="tags" name="tags" tags={tags} />
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

      {/* --- Timetable Button --- */}
      <Grid item xs={12} className="mb-2">
        <TimetableButton onClick={onCalendarClick} />
      </Grid>

      {/* --- CHECKBOXES --- */}
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

      {/* --- TIMEZONES --- */}
      {timezones && (
        <Grid item xs={layoutArray[2].xs} className="mb-2">
          <FormField
            type="select"
            name="timezone"
            label={$t('default_timezone')}
            items={timezones}
            labelAdornment={
              <Tooltip title={$t('timetables_will_be_adjusted_to_users_timezone_wher')}>
                <IconButton classes={{ root: 'inputAdornmentButton' }}>
                  <InfoOutlinedIcon className="inputAdornmentIcon" color="inherit" />
                </IconButton>
              </Tooltip>
            }
            validate={validateSingleMandatoryField}
          />
        </Grid>
      )}

      {/* --- ADDRESS BLOCK --- */}
      <Collapse in={!values.isVirtual}>
        <Grid container columnSpacing={3} className="pr-3 pl-3">
          <Grid container item xs={layoutArray[2].xs} columnSpacing={3} rowSpacing={2}>
            <Grid item xs={12}>
              <FormField
                type="text"
                name="street"
                label={$t('street')}
                validate={greaterThanNullValidation}
                onChange={updateLatLong}
              />
            </Grid>

            <Grid item xs={12}>
              <FormField type="text" name="suburb" label={$t('suburb')} onChange={updateLatLong} />
            </Grid>

            <Grid item xs={12}>
              <FormField type="text" name="state" label={$t('state')} onChange={updateLatLong}/>
            </Grid>

            <Grid item xs={12}>
              <FormField type="text" name="postcode" label={$t('postcode')} onChange={updateLatLong}/>
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
                  onChange={updateLatLong}
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

      {/* --- CUSTOM FIELDS --- */}
      <CustomFields
        entityName="Site"
        fieldName="customFields"
        entityValues={values}
        form={form}
        gridItemProps={{ xs: twoColumn ? 6 : 12 }}
      />

      {/* --- ROOMS --- */}
      <Grid item xs={layoutArray[8].xs}>
        <MinifiedEntitiesList
          name="rooms"
          header="Rooms"
          oneItemHeader="Room"
          entity="Room"
          FieldsContent={SitesRoomFields}
          onAdd={addRoom}
          onDelete={deleteRoom}
          onViewMore={openRoom}
          count={values.rooms?.length}
          validate={validateRooms}
          syncErrors={syncErrors}
          fieldProps={{
            isParenSiteVirtual: values.isVirtual
          }}
        />
      </Grid>
    </Grid>
  );
};

export default SitesGeneral;