/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.service.impl;

import com.google.inject.Inject;
import ish.oncourse.server.api.service.PaymentInApiService;
import ish.oncourse.server.api.v1.model.BankingParamDTO;
import ish.oncourse.server.api.v1.model.PaymentInDTO;
import ish.oncourse.server.api.v1.service.PaymentInApi;

public class PaymentInApiImpl implements PaymentInApi {

    @Inject
    private PaymentInApiService service;

    @Override
    public PaymentInDTO get(Long id) {
        return service.get(id);
    }


    @Override
    public void reverse(Long id) {
        service.reverseWithCommit(id);
    }

    @Override
    public void update(Long id, BankingParamDTO param) {
        PaymentInDTO restModel = new PaymentInDTO();
        restModel.setDateBanked(param.getDateBanked());
        restModel.setAdministrationCenterId(param.getAdministrationCenterId());

        service.update(id, restModel);
    }

    @Override
    public PaymentInDTO getPerformedPaymentInfo(String merchantReference) {
        return service.getByMerchantReference(merchantReference);
    }

}
