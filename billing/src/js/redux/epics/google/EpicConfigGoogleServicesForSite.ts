/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import { Request, Create } from '../EpicUtils';
import { CONFIGURE_GOOGLE_FOR_SITE } from '../../actions/Google';
import GoogleService from '../../../api/services/GoogleService';
import { getTokenString } from '../../../utils/Google';
import {
  ALL_EVENTS_TRIGGER_DEFAULT,
  ALL_PAGES_TRIGGER_DEFAULT,
  API_KEY_VARIABLE_DATA_DEFAULT,
  GAS_VARIABLE_DATA_DEFAULT, MAPS_PAGE_TRIGGER_DEFAULT
} from '../../../constant/Google';
import { SiteValues } from '../../../models/Sites';

const request: Request<any, SiteValues> = {
  type: CONFIGURE_GOOGLE_FOR_SITE,
  getData: async (site, state) => {
    const token = getTokenString(state);

    if (site.gtmContainerId) {
      const workspaces = await GoogleService.getGTMWorkspaces(
        token,
        site.gtmAccountId,
        site.gtmContainerId
      );
      const workspace = workspaces.workspace[0].workspaceId;

      await GoogleService.createGTMVariable(
        token,
        site.gtmAccountId,
        site.gtmContainerId,
        workspace,
        GAS_VARIABLE_DATA_DEFAULT
      );
      await GoogleService.createGTMVariable(
        token,
        site.gtmAccountId,
        site.gtmContainerId,
        workspace,
        API_KEY_VARIABLE_DATA_DEFAULT
      );

      const trigger_all_pages = await GoogleService.createGTMTrigger(
        token,
        site.gtmAccountId,
        site.gtmContainerId,
        workspace,
        ALL_PAGES_TRIGGER_DEFAULT
      );

      const trigger_all_events = await GoogleService.createGTMTrigger(
        token,
        site.gtmAccountId,
        site.gtmContainerId,
        workspace,
        ALL_EVENTS_TRIGGER_DEFAULT
      );

      const trigger_maps_page = await GoogleService.createGTMTrigger(
        token,
        site.gtmAccountId,
        site.gtmContainerId,
        workspace,
        MAPS_PAGE_TRIGGER_DEFAULT
      );

      await GoogleService.createGTMTag(
        token,
        site.gtmAccountId,
        site.gtmContainerId,
        workspace,
        {
          type: 'ua',
          name: 'ish onCourse Google Analytics',
          firingTriggerId: [trigger_all_pages.triggerId],
          tagFiringOption: 'oncePerEvent',
          parameter: [
            {
              key: 'overrideGaSettings',
              type: 'boolean',
              value: 'false'
            },
            {
              key: 'trackType',
              type: 'template',
              value: 'TRACK_PAGEVIEW'
            },
            {
              key: 'gaSettings',
              type: 'template',
              value: '{{ish onCourse Google Analytics settings}}'
            }
          ],
        }
      );

      await GoogleService.createGTMTag(
        token,
        site.gtmAccountId,
        site.gtmContainerId,
        workspace,
        {
          type: 'ua',
          name: 'ish onCourse Google Analytics events',
          tagFiringOption: 'oncePerEvent',
          firingTriggerId: [trigger_all_events.triggerId],
          parameter: [
            {
              key: 'overrideGaSettings',
              type: 'boolean',
              value: 'false'
            },
            {
              key: 'trackType',
              type: 'template',
              value: 'TRACK_EVENT'
            },
            {
              key: 'gaSettings',
              type: 'template',
              value: '{{ish onCourse Google Analytics settings}}'
            }
          ]
        }
      );

      await GoogleService.createGTMTag(
        token,
        site.gtmAccountId,
        site.gtmContainerId,
        workspace,
        {
          name: 'Google maps',
          firingTriggerId: [trigger_maps_page.triggerId],
          type: 'html',
          tagFiringOption: 'ONCE_PER_LOAD',
          parameter: [
            {
              type: 'TEMPLATE',
              key: 'html',
              value: '<script src="https://maps.googleapis.com/maps/api/js?key={{Google API Key}}&v=3.exp&callback=initMaps"></script>'
            },
            {
              type: 'BOOLEAN',
              key: 'supportDocumentWrite',
              value: 'false'
            }
          ],
        }
      );
    } else {

    }

    return [

    ];
  },
  processData: ([gtmAccounts, gaAccounts, gtmContainers]) => [

  ]
};

export const EpicConfigGoogleServicesForSite: Epic<any, any> = Create(request);
