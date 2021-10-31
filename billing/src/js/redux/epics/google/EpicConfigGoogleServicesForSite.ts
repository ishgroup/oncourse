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
  MAPS_PAGE_TRIGGER_DEFAULT,
  MAPS_API_KEY_NAME,
  GTM_CONTAINER_NAME_DEFAULT,
  getMapsApiKeyVariable,
  getGASVariable, GAS_VARIABLE_NAME,
} from '../../../constant/Google';
import { SiteValues } from '../../../models/Sites';
import { updateCollegeSites } from '../../actions/Sites';

const request: Request<any, SiteValues> = {
  type: CONFIGURE_GOOGLE_FOR_SITE,
  getData: async (site, state) => {
    const token = getTokenString(state.google);

    if (site.gtmAccountId) {
      let { gaWebPropertyId } = site;
      let gtmContainerId = state.google.gtmContainers[site.gtmAccountId].find((c) => c.publicId === site.gtmContainerId)?.containerId;
      if (!gtmContainerId) {
        await GoogleService.createGTMContainer(
          token,
          site.gtmAccountId,
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
          site.googleAnalyticsId,
          {
            name: `${site.name} web property`
          }
        ).then((res) => {
          gaWebPropertyId = res.id;
        });

        await GoogleService.createGAProfile(
          token,
          site.googleAnalyticsId,
          gaWebPropertyId,
          {
            name: `${site.name} profile`
          }
        );
      }

      const workspaces = await GoogleService.getGTMWorkspaces(
        token,
        site.gtmAccountId,
        gtmContainerId
      );

      const workspace = workspaces.workspace[0].workspaceId;

      const preview = await GoogleService.getGTMPreview(
        token,
        site.gtmAccountId,
        gtmContainerId,
        workspace
      );

      const gasVariable = preview.containerVersion.variable?.find((v) => v.type === 'gas' && v.name === GAS_VARIABLE_NAME);

      if (!gasVariable) {
        await GoogleService.createGTMVariable(
          token,
          site.gtmAccountId,
          gtmContainerId,
          workspace,
          getGASVariable(gaWebPropertyId)
        );
      } else {
        await GoogleService.updateGTMVariable(
          token,
          site.gtmAccountId,
          gtmContainerId,
          workspace,
          {
            ...gasVariable,
            parameter: gasVariable.parameter.map((p) => ({
              ...p,
              value: p.key === 'trackingId' ? gaWebPropertyId : p.value
            }))
          }
        );
      }

      const trigger_all_pages = preview.containerVersion.trigger?.find((t) => t.name === ALL_PAGES_TRIGGER_DEFAULT.name)
        || await GoogleService.createGTMTrigger(
          token,
          site.gtmAccountId,
          gtmContainerId,
          workspace,
          ALL_PAGES_TRIGGER_DEFAULT
        );

      const trigger_all_events = preview.containerVersion.trigger?.find((t) => t.name === ALL_EVENTS_TRIGGER_DEFAULT.name)
        || await GoogleService.createGTMTrigger(
          token,
          site.gtmAccountId,
          gtmContainerId,
          workspace,
          ALL_EVENTS_TRIGGER_DEFAULT
        );

      const trigger_maps_page = preview.containerVersion.trigger?.find((t) => t.name === MAPS_PAGE_TRIGGER_DEFAULT.name)
        || await GoogleService.createGTMTrigger(
          token,
          site.gtmAccountId,
          gtmContainerId,
          workspace,
          MAPS_PAGE_TRIGGER_DEFAULT
        );

      if (!preview.containerVersion.tag?.some((t) => t.type === 'ua')) {
        await GoogleService.createGTMTag(
          token,
          site.gtmAccountId,
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
          site.gtmAccountId,
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

      if (site.googleMapsApiKey) {
        await GoogleService.createGTMVariable(
          token,
          site.gtmAccountId,
          gtmContainerId,
          workspace,
          getMapsApiKeyVariable(site.googleMapsApiKey)
        );

        await GoogleService.createGTMTag(
          token,
          site.gtmAccountId,
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
        site.gtmAccountId,
        gtmContainerId,
        workspace,
      );
    }

    return site;
  },
  processData: (site) => [
    updateCollegeSites({ changed: [site] })
  ]
};

export const EpicConfigGoogleServicesForSite: Epic<any, any> = Create(request);
