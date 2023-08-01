/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { FundingStatus, FundingUpload, FundingUploadApi } from "@api/model";
import { DefaultHttpService } from "../../../common/services/HttpService";

class FundingUploadService {
  readonly service = new DefaultHttpService();
  readonly fundingUploadApi = new FundingUploadApi(this.service);

  public getFundingUploads(search?: string): Promise<FundingUpload[]> {
    return this.fundingUploadApi.get(search || "");
  }

  public update(id: number, status: FundingStatus): Promise<any> {
    return this.fundingUploadApi.update({ id, status });
  }
}

export default new FundingUploadService();
