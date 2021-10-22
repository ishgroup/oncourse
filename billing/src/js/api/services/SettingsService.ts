/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { SettingsApi, SettingsDTO } from '@api/model';
import { DefaultHttpService } from './HttpService';

class SettingsService {
  readonly settingsApi = new SettingsApi(new DefaultHttpService());

  public getSettings(): Promise<SettingsDTO> {
    return this.settingsApi.getSettings();
  }

  public updateSettings(settings: SettingsDTO): Promise<any> {
    return this.settingsApi.updateSettings(settings);
  }
}

export default new SettingsService();
