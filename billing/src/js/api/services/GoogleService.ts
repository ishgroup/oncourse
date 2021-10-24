/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { PropertiesApi, PropertiesDTO } from '@api/model';
import { DefaultHttpService } from './HttpService';
import {
  GAWebProperty,
  GTMAccount, GTMContainer, GTMTag, GTMTrigger, GTMVariable
} from '../../models/Google';

class GoogleService {
  readonly http = new DefaultHttpService();

  readonly propertiesApi = new PropertiesApi(this.http);

  public getProperties(): Promise<PropertiesDTO> {
    return this.propertiesApi.getProperties();
  }

  public getGAAccounts(Authorization: string): Promise<gapi.client.analytics.Accounts> {
    return this.http.GET('https://www.googleapis.com/analytics/v3/management/accounts', {
      headers: {
        Authorization
      }
    });
  }

  public createGAProperty(
    Authorization: string,
    account: string,
    property: GAWebProperty
  ): Promise<GAWebProperty> {
    return this.http.POST(`https://www.googleapis.com/analytics/v3/management/accounts/${account}/webproperties`,
      property,
      {
        headers: {
          Authorization
        }
      });
  }

  public getGAWebProperties(Authorization: string, account: string): Promise<gapi.client.analytics.Webproperties> {
    return this.http.GET(`https://www.googleapis.com/analytics/v3/management/accounts/${account}/webproperties`, {
      headers: {
        Authorization
      }
    });
  }

  public getGTMAccounts(Authorization: string): Promise<{ account: GTMAccount[] }> {
    return this.http.GET('https://www.googleapis.com/tagmanager/v2/accounts', {
      headers: {
        Authorization
      }
    });
  }

  public getGTMContainers(Authorization: string, accountId: string): Promise<gapi.client.tagmanager.ListContainersResponse> {
    return this.http.GET(`https://www.googleapis.com/tagmanager/v2/accounts/${accountId}/containers`, {
      headers: {
        Authorization
      }
    });
  }

  public getGTMPreview(Authorization: string, account: string, container: string, workspace: string): Promise<gapi.client.tagmanager.QuickPreviewResponse> {
    return this.http.POST(`https://www.googleapis.com/tagmanager/v2/accounts/${account}/containers/${container}/workspaces/${workspace}:quick_preview`, null, {
      headers: {
        Authorization
      }
    });
  }

  public getGTMWorkspaces(
    Authorization: string,
    account: string,
    container: string
  ): Promise<gapi.client.tagmanager.ListWorkspacesResponse> {
    return this.http.GET(`https://www.googleapis.com/tagmanager/v2/accounts/${account}/containers/${container}/workspaces`,
      {
        headers: {
          Authorization
        }
      });
  }

  public createGTMContainer(
    Authorization: string,
    account: string,
    container: GTMContainer
  ): Promise<GTMContainer> {
    return this.http.POST(`https://www.googleapis.com/tagmanager/v2/accounts/${account}/containers`,
      container,
      {
        headers: {
          Authorization
        }
      });
  }

  public createGTMVariable(
    Authorization: string,
    account: string,
    container: string,
    workspace: string,
    variable: GTMVariable
  ): Promise<GTMVariable> {
    return this.http.POST(`https://www.googleapis.com/tagmanager/v2/accounts/${account}/containers/${container}/workspaces/${workspace}/variables`,
      variable,
      {
        headers: {
          Authorization
        }
      });
  }

  public createGTMTrigger(
    Authorization: string,
    account: string,
    container: string,
    workspace: string,
    trigger: GTMTrigger
  ): Promise<GTMTrigger> {
    return this.http.POST(`https://www.googleapis.com/tagmanager/v2/accounts/${account}/containers/${container}/workspaces/${workspace}/triggers`,
      trigger,
      {
        headers: {
          Authorization
        }
      });
  }

  public createGTMTag(
    Authorization: string,
    account: string,
    container: string,
    workspace: string,
    tag: GTMTag
  ): Promise<GTMTag> {
    return this.http.POST(`https://www.googleapis.com/tagmanager/v2/accounts/${account}/containers/${container}/workspaces/${workspace}/tags`,
      tag,
      {
        headers: {
          Authorization
        }
      });
  }

  public async publishGTM(
    Authorization: string,
    account: string,
    container: string,
    workspace: string
  ): Promise<GTMTrigger> {
    const version: gapi.client.tagmanager.CreateContainerVersionResponse = await this.http.POST(`https://www.googleapis.com/tagmanager/v2/accounts/${account}/containers/${container}/workspaces/${workspace}:create_version`,
      null,
      {
        headers: {
          Authorization
        }
      });

    return this.http.POST(`https://www.googleapis.com/tagmanager/v2/accounts/${account}/containers/${container}/versions/${version.containerVersion.containerVersionId}:publish`,
      null,
      {
        headers: {
          Authorization
        }
      });
  }
}

export default new GoogleService();
