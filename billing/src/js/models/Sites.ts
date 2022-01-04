/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { SiteDTO } from '@api/model';

export interface SiteValues extends SiteDTO {
  collegeKey: string;
  googleMapsApiKey: string;
  gaWebPropertyId: string;
}

export interface SitesUpdateRequest {
  changed?: SiteDTO[];
  created?: SiteDTO[];
  removed?: number[];
}

export interface SitePageParams {
  id: string;
  page: 'analytics' | 'urls' | 'tagManager' | 'googleSearch'
}
