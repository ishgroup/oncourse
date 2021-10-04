/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { DefaultHttpService } from './HttpService';
import { store } from '../../redux';
import { State } from '../../models/State';

class GoogleService {
  readonly defaultHttpService = new DefaultHttpService();

  private getToken = () => {
    const state: State = store.getState();
    return state.google.token?.access_token;
  };

  public getGTMAccounts(): Promise<any> {
    return this.defaultHttpService.GET('https://www.googleapis.com/tagmanager/v2/accounts', {
      headers: {
        Authorization: `Bearer ${this.getToken()}`
      }
    });
  }
}

export default new GoogleService();
