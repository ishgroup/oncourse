/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import { SiteDTO } from '@api/model';
import { Request, Create } from '../EpicUtils';
import { CONFIGURE_GOOGLE_FOR_SITE, getGtmDataByAccount } from '../../actions/Google';
import GoogleService from '../../../api/services/GoogleService';
import { getTokenString } from '../../../utils/Google';
import {
  ALL_EVENTS_TRIGGER_DEFAULT,
  ALL_PAGES_TRIGGER_DEFAULT,
  MAPS_PAGE_TRIGGER_DEFAULT,
  MAPS_API_KEY_NAME,
  GTM_CONTAINER_NAME_DEFAULT,
  getMapsApiKeyVariable,
  getGASVariable, GAS_VARIABLE_NAME,
} from '../../../constant/Google';
import { SiteValues } from '../../../models/Sites';
import { updateCollegeSites } from '../../actions/Sites';

const request: Request<SiteDTO, SiteValues> = {
  type: CONFIGURE_GOOGLE_FOR_SITE,
  getData: async (site, state) => {
    const parsedSite = {
      ...site,
      gtmContainerId: site.gtmContainerId === 'new' ? null : site.gtmContainerId,
      gaWebPropertyId: site.gaWebPropertyId === 'new' ? null : site.gaWebPropertyId,
    }

    const token = getTokenString(state.google);

    if (parsedSite.gtmAccountId) {
      let { gaWebPropertyId } = parsedSite;
      let gtmContainerId = state.google.gtmContainers[parsedSite.gtmAccountId].find((c) => c.publicId === parsedSite.gtmContainerId)?.containerId;
      const existingContainer = state.google.gtmContainers[parsedSite.gtmAccountId].find((c) => c.name === GTM_CONTAINER_NAME_DEFAULT);

      if (existingContainer) {
        gtmContainerId = existingContainer.containerId;
      }

      if (!gtmContainerId) {
        await GoogleService.createGTMContainer(
          token,
          parsedSite.gtmAccountId,
          {
            name: GTM_CONTAINER_NAME_DEFAULT,
            usageContext: [
              'web'
            ]
          }
        ).then((res) => {
          gtmContainerId = res.containerId;
        });
      }

      if (!gaWebPropertyId) {
        await GoogleService.createGAProperty(
          token,
          parsedSite.googleAnalyticsId,
          {
            name: `${parsedSite.name} web property`
          }
        ).then((res) => {
          gaWebPropertyId = res.id;
        });

        await GoogleService.createGAProfile(
          token,
          parsedSite.googleAnalyticsId,
          gaWebPropertyId,
          {
            name: `${parsedSite.name} profile`
          }
        );
      }

      const workspaces = await GoogleService.getGTMWorkspaces(
        token,
        parsedSite.gtmAccountId,
        gtmContainerId
      );

      const workspace = workspaces.workspace[0].workspaceId;

      const preview = await GoogleService.getGTMPreview(
        token,
        parsedSite.gtmAccountId,
        gtmContainerId,
        workspace
      );

      const gasVariable = preview.containerVersion.variable?.find((v) => v.type === 'gas' && v.name === GAS_VARIABLE_NAME);

      if (!gasVariable) {
        await GoogleService.createGTMVariable(
          token,
          parsedSite.gtmAccountId,
          gtmContainerId,
          workspace,
          getGASVariable(gaWebPropertyId)
        );
      } else {
        await GoogleService.updateGTMVariable(
          token,
          parsedSite.gtmAccountId,
          gtmContainerId,
          workspace,
          {
            ...gasVariable,
            ...getGASVariable(gaWebPropertyId)
          }
        );
      }

      const trigger_all_pages = preview.containerVersion.trigger?.find((t) => t.name === ALL_PAGES_TRIGGER_DEFAULT.name)
        || await GoogleService.createGTMTrigger(
          token,
          parsedSite.gtmAccountId,
          gtmContainerId,
          workspace,
          ALL_PAGES_TRIGGER_DEFAULT
        );

      const trigger_all_events = preview.containerVersion.trigger?.find((t) => t.name === ALL_EVENTS_TRIGGER_DEFAULT.name)
        || await GoogleService.createGTMTrigger(
          token,
          parsedSite.gtmAccountId,
          gtmContainerId,
          workspace,
          ALL_EVENTS_TRIGGER_DEFAULT
        );

      const trigger_maps_page = preview.containerVersion.trigger?.find((t) => t.name === MAPS_PAGE_TRIGGER_DEFAULT.name)
        || await GoogleService.createGTMTrigger(
          token,
          parsedSite.gtmAccountId,
          gtmContainerId,
          workspace,
          MAPS_PAGE_TRIGGER_DEFAULT
        );

      if (!preview.containerVersion.tag?.some((t) => t.type === 'ua')) {
        await GoogleService.createGTMTag(
          token,
          parsedSite.gtmAccountId,
          gtmContainerId,
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
          parsedSite.gtmAccountId,
          gtmContainerId,
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
      }

      if (parsedSite.googleMapsApiKey) {
        await GoogleService.createGTMVariable(
          token,
          parsedSite.gtmAccountId,
          gtmContainerId,
          workspace,
          getMapsApiKeyVariable(parsedSite.googleMapsApiKey)
        );

        await GoogleService.createGTMTag(
          token,
          parsedSite.gtmAccountId,
          gtmContainerId,
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
                value: `<script src="https://maps.googleapis.com/maps/api/js?key={{${MAPS_API_KEY_NAME}}}&v=3.exp&callback=initMaps"></script>`
              },
              {
                type: 'BOOLEAN',
                key: 'supportDocumentWrite',
                value: 'false'
              }
            ],
          }
        );
      }

      await GoogleService.publishGTM(
        token,
        parsedSite.gtmAccountId,
        gtmContainerId,
        workspace,
      );
    }

    return parsedSite;
  },
  processData: (site) => [
    updateCollegeSites({ changed: [site] }),
    getGtmDataByAccount(site.gtmAccountId)
  ]
};

export const EpicConfigGoogleServicesForSite: Epic<any, any> = Create(request);
