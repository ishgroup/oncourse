/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { GTMTrigger, GTMVariable } from '../models/Google';

export const GTM_CONTAINER_NAME_DEFAULT = 'ish onCourse Google Tag manager container';

export const GA_PROPERTY_NAME_DEFAULT = 'ish onCourse Google Analytics web property';

export const GAS_VARIABLE_NAME = 'ish onCourse Google Analytics settings';

export const getGASVariable = (trakingId: string): GTMVariable => ({
  name: GAS_VARIABLE_NAME,
  type: 'gas',
  parameter: [
    {
      key: 'cookieDomain',
      type: 'template',
      value: 'auto'
    },
    {
      key: 'useEcommerceDataLayer',
      type: 'boolean',
      value: 'true'
    },
    {
      key: 'doubleClick',
      type: 'boolean',
      value: 'false'
    },
    {
      key: 'setTrackerName',
      type: 'boolean',
      value: 'false'
    },
    {
      key: 'useDebugVersion',
      type: 'boolean',
      value: 'false'
    },
    {
      key: 'useHashAutoLink',
      type: 'boolean',
      value: 'false'
    },
    {
      key: 'decorateFormsAutoLink',
      type: 'boolean',
      value: 'false'
    },
    {
      key: 'enableLinkId',
      type: 'boolean',
      value: 'false'
    },
    {
      key: 'enableEcommerce',
      type: 'boolean',
      value: 'true'
    },
    {
      key: 'trackingId',
      type: 'template',
      value: `${trakingId}`
    }
  ],
});

export const MAPS_API_KEY_NAME = 'Google Maps API Key';

export const getMapsApiKeyVariable = (apiKey: string): GTMVariable => ({
  name: MAPS_API_KEY_NAME,
  type: 'c',
  parameter: [
    {
      type: 'TEMPLATE',
      key: 'value',
      value: apiKey
    }
  ],
  formatValue: {}
});

export const ALL_PAGES_TRIGGER_DEFAULT: GTMTrigger = {
  name: 'All pages',
  type: 'pageview',
};

export const ALL_EVENTS_TRIGGER_DEFAULT: GTMTrigger = {
  name: 'Any event',
  type: 'customEvent',
  customEventFilter: [{
    parameter: [
      {
        key: 'arg0',
        type: 'template',
        value: '{{_event}}'
      },
      {
        key: 'arg1',
        type: 'template',
        value: '.*'
      }
    ],
    type: 'matchRegex'
  }],
};

export const MAPS_PAGE_TRIGGER_DEFAULT: GTMTrigger = {
  name: 'Google map trigger',
  type: 'ELEMENT_VISIBILITY',
  parameter: [
    {
      type: 'TEMPLATE',
      key: 'elementId',
      value: 'gmapCanvas'
    },
    {
      type: 'BOOLEAN',
      key: 'useOnScreenDuration',
      value: 'false'
    },
    {
      type: 'BOOLEAN',
      key: 'useDomChangeListener',
      value: 'false'
    },
    {
      type: 'TEMPLATE',
      key: 'firingFrequency',
      value: 'ONCE'
    },
    {
      type: 'TEMPLATE',
      key: 'selectorType',
      value: 'ID'
    },
    {
      type: 'TEMPLATE',
      key: 'onScreenRatio',
      value: '50'
    }
  ]
};
