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

import groovy.transform.CompileStatic
import ish.common.types.AccountType
import ish.common.types.PaymentType
import static ish.common.types.PaymentType.CONTRA
import static ish.common.types.PaymentType.CREDIT_CARD
import static ish.common.types.PaymentType.INTERNAL
import static ish.common.types.PaymentType.REVERSE
import static ish.common.types.PaymentType.VOUCHER
import ish.oncourse.server.api.v1.model.PayTypeDTO
import ish.oncourse.server.api.v1.model.PaymentMethodDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.PaymentMethod
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

@CompileStatic
class PaymentTypeFunctions {

    public static final List<PaymentMethodDTO> SYSTEM_TYPES = [
            new PaymentMethodDTO(name: 'Zero', bankedAuto: true, active: true, reconcilable: false, systemType: true),
            new PaymentMethodDTO(name: 'Voucher', bankedAuto: true, active: true, reconcilable: false, systemType: true),
            new PaymentMethodDTO(name: 'Contra', bankedAuto: true, active: true, reconcilable: false, systemType: true),
            new PaymentMethodDTO(name: 'Reverse', bankedAuto: true, active: true, reconcilable: false, systemType: true),
    ]

    static ValidationErrorDTO validateForDelete(PaymentMethod method) {

        if (!method) {
            return new ValidationErrorDTO(null, null, "Payment type is not exist")
        }

        if (method.type in [INTERNAL, VOUCHER, REVERSE, CONTRA]) {
            return new ValidationErrorDTO(method?.id?.toString(), 'type', "System payment type can not be deleted")
        }

        if (!method.paymentIns.empty || !method.paymentOuts.empty) {
            return new ValidationErrorDTO(method?.id?.toString(), null, "Payment type can not be deleted, it has assigned payments ")
        }
    }


    static ValidationErrorDTO validateData(ObjectContext context, List<PaymentMethodDTO> paymentTypes) {

        List<PaymentMethodDTO> ccTypes = paymentTypes.findAll { it.type == PayTypeDTO.CREDIT_CARD &&
                it.active }
        PaymentMethod bdCcType = ObjectSelect.query(PaymentMethod).where(PaymentMethod.TYPE.eq(CREDIT_CARD)).and(PaymentMethod.ACTIVE.isTrue()).selectOne(context)

        if (ccTypes.size() > 1 ||
                (ccTypes.size() == 1
                        && bdCcType
                        && bdCcType.id != ccTypes[0].id
                        && paymentTypes.find { bdCcType.id?.toString() == it.id } == null)) {
            return new ValidationErrorDTO(ccTypes[0].id?.toString(), 'type', "You already have a real time credit card method. Only one such payment method allowed in onCourse")
        }


        List<String> names =  paymentTypes*.name.flatten() as List<String>
        List<String> duplicates = names.findAll{names.count(it) > 1}.unique()

        if (!duplicates.empty) {
            return new ValidationErrorDTO(null, 'name', "Payment type name should be unique: ${duplicates.join(', ')}")
        }

        return null
    }


    static ValidationErrorDTO validateForUpdate(ObjectContext context, PaymentMethodDTO type, List<PaymentMethodDTO> updatedTypes) {
        PaymentMethod method = null
        if (type.id) {
            method = SelectById.query(PaymentMethod, type.id).selectOne(context)
            if (!method) {
                return new ValidationErrorDTO(type.id?.toString(), 'id', "Payment type $type.id is not exist")
            }
        }

        if (!type.name || type.name.empty) {
            return new ValidationErrorDTO(type.id?.toString(), 'name', "Payment type name can not be empty")
        }

        PaymentMethod duplicate = ObjectSelect.query(PaymentMethod).where(PaymentMethod.NAME.eq(type.name)).selectOne(context)

        if (duplicate) {
            if (method && duplicate.id == method.id) {
                // changing name of the same record - valid
            } else if ( updatedTypes.find {  it.id && Long.valueOf(it.id) == duplicate.id} ) {
                return new ValidationErrorDTO(type.id?.toString(), 'name', "To replace payment types' names you should remove that paymentTypes")
            } else {
                return new ValidationErrorDTO(type.id?.toString(), 'name', "Payment type with name ${type.name} already exist")
            }
        }

        if (!type.type) {
            return new ValidationErrorDTO(type.id?.toString(), 'type', "Type can not be empty")
        }

        if (!type.accountId) {
            return new ValidationErrorDTO(type.id?.toString(), 'accountId', "Payment account can not be empty")
        } else if (!getAssetAccount(context, type.accountId)) {
            return new ValidationErrorDTO(type.id?.toString(), 'accountId', "Payment account is wrong")
        }

        if (!type.undepositAccountId) {
            return new ValidationErrorDTO(type.id?.toString(), 'undepositAccountId', "Payment undeposited account can not be empty")
        } else if (!getAssetAccount(context, type.undepositAccountId)) {
            return new ValidationErrorDTO(type.id?.toString(), 'undepositAccountId', "Payment undeposited account is wrong")
        }

        return null
    }

    static PaymentMethod updateType(ObjectContext context, PaymentMethodDTO type) {
        PaymentMethod method = type.id ? SelectById.query(PaymentMethod, type.id).selectOne(context) : context.newObject(PaymentMethod)
        method.name = type.name
        method.type = PaymentType.valueOf(type.type.name())
        method.active = type.active
        method.bankedAutomatically = type.bankedAuto
        method.reconcilable = type.reconcilable
        method.account = getAssetAccount(context, type.accountId)
        method.undepositedFundsAccount = getAssetAccount(context, type.undepositAccountId)
        method
    }


    static Account getAssetAccount(ObjectContext context, Long id) {
        return ObjectSelect.query(Account)
                .where(Account.ID.eq(id))
                .and(Account.TYPE.eq(AccountType.ASSET))
                .selectOne(context)
    }

    static Account getLiabilityAccount(ObjectContext context, Long id) {
        return ObjectSelect.query(Account)
                .where(Account.ID.eq(id))
                .and(Account.TYPE.eq(AccountType.LIABILITY))
                .selectOne(context)
    }
}
