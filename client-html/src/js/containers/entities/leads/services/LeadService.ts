/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Diff, Lead, LeadApi } from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class LeadService {
  readonly leadApi = new LeadApi(new DefaultHttpService());

  public bulkChange(diff: Diff): Promise<any> {
    return this.leadApi.bulkChange(diff);
  }

  public createLead(lead: Lead): Promise<any> {
    return this.leadApi.create(lead);
  }

  public getLead(id: number): Promise<any> {
    return this.leadApi.get(id);
  }

  public updateLead(id: number, lead: Lead): Promise<any> {
    return this.leadApi.update(id, lead);
  }

  public removeLead(id: number): Promise<any> {
    return this.leadApi.remove(id);
  }
}

export default new LeadService();
