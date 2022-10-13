/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { DefaultHttpService } from "../../../../../common/services/HttpService";
import { ReportOverlayApi, ReportOverlay } from "@api/model";

class ReportOverlayService {
  readonly service = new DefaultHttpService();
  readonly reportOverlayApi = new ReportOverlayApi(this.service);

  public addOverlay(fileName: string, overlay: File): Promise<any> {
    return this.service.POST("/v1/list/entity/reportOverlay", overlay, {
      headers: { "Content-Type": "application/pdf" },
      params: { fileName }
    });
  }

  public updateReportOverlay(fileName: string, id: number, overlay: File): Promise<any> {
    return this.service.PUT(`/v1/list/entity/reportOverlay/${id}`, overlay, {
      headers: { "Content-Type": "application/pdf" },
      params: { fileName }
    });
  }

  public getReportOverlay(id: number): Promise<ReportOverlay> {
    return this.reportOverlayApi.get(id);
  }

  public getReportOverlayCopy(id: number): Promise<string[]> {
    return this.reportOverlayApi.getOriginal(id);
  }

  public removeReportOverlay(id: number): Promise<any> {
    return this.reportOverlayApi.remove(id);
  }
}

export default new ReportOverlayService();
