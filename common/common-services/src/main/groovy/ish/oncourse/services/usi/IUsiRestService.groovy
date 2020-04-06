package ish.oncourse.services.usi

import ish.common.types.LocateUSIRequest
import ish.common.types.LocateUSIResult
import ish.common.types.USIVerificationRequest
import ish.common.types.USIVerificationResult

interface IUsiRestService {

    public USIVerificationResult verify(USIVerificationRequest request);

    public LocateUSIResult locate(LocateUSIRequest request);

}