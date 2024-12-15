/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Outcome, OutcomeApi } from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class OutcomeService {
  readonly outcomeApi = new OutcomeApi(new DefaultHttpService());

  public getOutcome(id: number): Promise<any> {
    return this.outcomeApi.get(id);
  }

  public updateOutcome(id: number, outcome: Outcome): Promise<any> {
    return this.outcomeApi.update(id, outcome);
  }

  public removeOutcome(id: number): Promise<any> {
    return this.outcomeApi.remove(id);
  }

  public create(outcome: Outcome): Promise<any> {
    return this.outcomeApi.create(outcome);
  }
}

export default new OutcomeService();
