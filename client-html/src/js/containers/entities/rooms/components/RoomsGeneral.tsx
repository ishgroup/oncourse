/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Room } from '@api/model';
import ScreenShare from '@mui/icons-material/ScreenShare';
import Grid, { GridSize } from '@mui/material/Grid';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import { LinkAdornment, openInternalLink, TimetableButton } from 'ish-ui';
import debounce from 'lodash.debounce';
import * as React from 'react';
import { useCallback, useEffect, useState } from 'react';
import { change, FieldArray } from 'redux-form';
import InstantFetchErrorHandler from '../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler';
import DocumentsRenderer from '../../../../common/components/form/documents/DocumentsRenderer';
import { FormEditorField } from '../../../../common/components/form/formFields/FormEditor';
import FormField from '../../../../common/components/form/formFields/FormField';
import FullScreenStickyHeader
  from '../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader';
import EntityService from '../../../../common/services/EntityService';
import { useAppSelector } from '../../../../common/utils/hooks';
import { validateURL } from '../../../../common/utils/validation';
import { EditViewProps } from '../../../../model/common/ListView';
import { EntityChecklists } from '../../../tags/components/EntityChecklists';
import CustomFields from '../../customFieldTypes/components/CustomFieldsTypes';
import { openSiteLink } from '../../sites/utils';

const normalizeSeatedCapacity = value => ((value && value >= 0) || value === 0 ? Number(value) : null);

const getLayoutArray = (twoColumn: boolean): { [key: string]: GridSize }[] =>
  (twoColumn
    ? [{ xs: 12 }, { xs: 12 }, { xs: 4 }, { xs: 4 }, { xs: 4 }, { xs: 12 }, { xs: 12 }, { xs: 12 }]
    : [{ xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }]);

function RoomsGeneral({ 
  isNew,
  values,
  showConfirm,
  dispatch,
  form,
  twoColumn,
  syncErrors 
}: EditViewProps<Room>) {
  const [isParenSiteVirtual, setIsParenSiteVirtual] = useState(false);
  
  const tags = useAppSelector(state => state.tags.entityTags["Room"]);
  const sites = useAppSelector(state => state.plainSearchRecords["Site"].items);
  
  const onCalendarClick = () => {
    const site = sites.find(el => el.value === values.siteId);
    openInternalLink(
      `/timetable?search=room.id=${values.id}&title=Timetable for ${values.name}, ${
        site ? site.label : ""
      }`
    );
  };

  const onSiteChange = site => {
    dispatch(change(form, "siteTimeZone", site.localTimezone));
  };

  const layoutArray = getLayoutArray(twoColumn);
  
  const resetIsVirtualParent = useCallback(debounce(id => {
    EntityService.getPlainRecords('Site', 'isVirtual', `id is ${id}`)
      .then(res => {
          setIsParenSiteVirtual(JSON.parse(res.rows[0].values[0]));
        }
      )
      .catch(err => InstantFetchErrorHandler(dispatch, err));
  }, 500), []);
  
  useEffect(() => {
    setIsParenSiteVirtual(false);
    resetIsVirtualParent(values.siteId);
  }, [values.siteId]);

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
            className="mb-2"
          />
          {isParenSiteVirtual && <FormField
            type="text"
            label="Virtual room URL"
            name="virtualRoomUrl"
            validate={validateURL}
          />}
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
        <TimetableButton onClick={onCalendarClick} />
      </Grid>

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
            onInnerValueChange={onSiteChange}
            required
          />
        )}
      </Grid>

      <CustomFields
        entityName="Room"
        fieldName="customFields"
        entityValues={values}
        form={form}
        gridItemProps={{
          xs: twoColumn ? 4 : 12,
        }}
      />

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

export default RoomsGeneral;