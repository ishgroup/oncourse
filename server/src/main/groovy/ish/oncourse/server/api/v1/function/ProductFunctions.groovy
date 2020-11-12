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

package ish.oncourse.server.api.v1.function

import ish.common.types.ExpiryType
import ish.oncourse.server.api.BidiMap
import ish.oncourse.server.api.dao.CayenneLayer
import ish.oncourse.server.api.v1.model.ExpiryTypeDTO
import static ish.oncourse.server.api.v1.model.ExpiryTypeDTO.DAYS
import static ish.oncourse.server.api.v1.model.ExpiryTypeDTO.NEVER_LIFETIME_
import static ish.oncourse.server.api.v1.model.ExpiryTypeDTO._1ST_JANUARY
import static ish.oncourse.server.api.v1.model.ExpiryTypeDTO._1ST_JULY
import ish.oncourse.server.api.v1.model.MembershipCorporatePassDTO
import ish.oncourse.server.cayenne.CorporatePass
import ish.oncourse.server.cayenne.CorporatePassProduct
import ish.oncourse.server.cayenne.Product

class ProductFunctions {

    public static final BidiMap<ExpiryType, ExpiryTypeDTO> expiryTypeMap = new BidiMap<ExpiryType, ExpiryTypeDTO>() {{
        put(ExpiryType.DAYS, DAYS)
        put(ExpiryType.FIRST_JANUARY, _1ST_JANUARY)
        put(ExpiryType.FIRST_JULY, _1ST_JULY)
        put(ExpiryType.LIFETIME, NEVER_LIFETIME_)
    }}

    static void updateCorporatePasses(Product cayenneModel,
                                      List<MembershipCorporatePassDTO> corporatePasses,
                                      CayenneLayer<CorporatePassProduct> corporatePassProductDao,
                                      CayenneLayer<CorporatePass> corporatePassDao) {
        List<Long> relationsToSave = corporatePasses*.id ?: [] as List<Long>
        cayenneModel.context.deleteObjects(cayenneModel.corporatePassProducts.findAll { !relationsToSave.contains(it.corporatePass.id) })
        corporatePasses.findAll { !cayenneModel.corporatePassProducts*.corporatePass*.id.contains(it.id) }.each { it ->
            corporatePassProductDao.newObject(cayenneModel.context).with { cpd ->
                cpd.corporatePass = corporatePassDao.getById(cayenneModel.context, it.id)
                cpd.product = cayenneModel
                cpd
            }
        }
    }
    static void updateCorporatePassesByIds(Product cayenneModel,
                                           List<Long> corporatePasses,
                                           CayenneLayer<CorporatePassProduct> corporatePassProductDao,
                                           CayenneLayer<CorporatePass> corporatePassDao) {
        cayenneModel.context.deleteObjects(cayenneModel.corporatePassProducts.findAll { !corporatePasses.contains(it.corporatePass.id) })
        corporatePasses.findAll { id ->
            !cayenneModel.corporatePassProducts*.corporatePass*.id.contains(id)
        }.each { id ->
                corporatePassProductDao.newObject(cayenneModel.context).with { cpd ->
                    cpd.corporatePass = corporatePassDao.getById(cayenneModel.context, id)
                    cpd.product = cayenneModel
                    cpd
                }
        }
    }
}
