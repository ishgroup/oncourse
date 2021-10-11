/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { DefaultHttpService } from './HttpService';
import { GTMAccount } from '../../models/Google';

class GoogleService {
  readonly defaultHttpService = new DefaultHttpService();

  public getGTMAccounts(Authorization: string): Promise<{ account: GTMAccount[] }> {
    return this.defaultHttpService.GET('https://www.googleapis.com/tagmanager/v2/accounts', {
      headers: {
        Authorization
      }
    });
  }

  public getGTMContainers(Authorization: string, accountId: string): Promise<gapi.client.tagmanager.ListContainersResponse> {
    return this.defaultHttpService.GET(`https://www.googleapis.com/tagmanager/v2/accounts/${accountId}/containers`, {
      headers: {
        Authorization
      }
    });
  }

  public getGAAccounts(Authorization: string): Promise<gapi.client.analytics.Accounts> {
    return this.defaultHttpService.GET('https://www.googleapis.com/analytics/v3/management/accounts', {
      headers: {
        Authorization
      }
    });
  }
}

export default new GoogleService();
