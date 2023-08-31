/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
  Certificate,
  CertificateApi,
  CertificateCreateForEnrolmentsRequest,
  CertificateRevokeRequest,
  CertificateValidationRequest
} from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class CertificateService {
  readonly certificateApi = new CertificateApi(new DefaultHttpService());

  public getCertificate(id: number): Promise<any> {
    return this.certificateApi.get(id);
  }

  public updateCertificate(id: number, certificate: Certificate): Promise<any> {
    return this.certificateApi.update(id, certificate);
  }

  public createCertificate(certificate: Certificate): Promise<any> {
    return this.certificateApi.create(certificate);
  }

  public createForEnrolments(createRequest: CertificateCreateForEnrolmentsRequest): Promise<number[]> {
    return this.certificateApi.createForEnrolments(createRequest);
  }

  public removeCertificate(id: number): Promise<any> {
    return this.certificateApi.remove(id);
  }

  public validateForPrint(validationRequest: CertificateValidationRequest): Promise<any> {
    return this.certificateApi.validateForPrint(validationRequest);
  }

  public revokeCertificate(revokeRequest: CertificateRevokeRequest): Promise<any> {
    return this.certificateApi.revoke(revokeRequest);
  }
}

export default new CertificateService();
