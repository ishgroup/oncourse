/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { AssistantApi, AssistantMessageRequest, AssistantReply } from "@api/model";
import { DefaultHttpService } from "../../../common/services/HttpService";

class AssistantService {
  readonly assistantApi = new AssistantApi(new DefaultHttpService());

  public sendMessage(request: AssistantMessageRequest): Promise<AssistantReply> {
    return this.assistantApi.sendMessage(request);
  }
}

export default new AssistantService();
