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

package ish.oncourse.server.entity.mixins

import ish.math.Money
import ish.oncourse.API
import static ish.oncourse.cayenne.MappedSelectParams.AMOUNT_SUM_COLUMN
import static ish.oncourse.cayenne.MappedSelectParams.ENTITY_NAME_PARAMETER
import static ish.oncourse.cayenne.MappedSelectParams.FIELD_NAME
import static ish.oncourse.cayenne.MappedSelectParams.SUM_QUERY
import static ish.oncourse.cayenne.MappedSelectParams.WHERE_CLAUSE_PARAMETER
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.AccountTransaction
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.MappedSelect

import java.time.LocalDate

class AccountMixin {

	static final String SQL_DATE_FORMAT = 'yyyy-MM-dd 00:00:00'

	static final String BALANCE_QUERY_TEMPLATE = "where accountId = %d and transactionDate < '%s'"
	static final String MOVEMENTS_QUERY_TEMPLATE =
			"where accountId = %d and transactionDate >= '%s' and transactionDate <= '%s'"

	/**
	 * @param until get data up to a given date
	 * @return balance up until a given
	 */
	@API
	static Money getBalance(Account self, LocalDate until) {
		return Money.of(performTransactionSumQuery(self.objectContext, buildWhereClause(self, BALANCE_QUERY_TEMPLATE, until)))
	}

	//TODO docs
	@API
	static Money getMovements(Account self, LocalDate from, LocalDate to) {
		return Money.of(performTransactionSumQuery(self.objectContext, buildWhereClause(self, MOVEMENTS_QUERY_TEMPLATE, from, to)))
	}

	@Deprecated
	static String buildWhereClause(Account account, String template, LocalDate... dates) {

		Object[] args = new Object[dates.length + 1]
		args[0] = account.id

		for (int i = 0; i < dates.length; i++) {
			args[i + 1] = dates[i].format(SQL_DATE_FORMAT)
		}

		return String.format(template, args)
	}

	@Deprecated
	static BigDecimal performTransactionSumQuery(ObjectContext context, final String whereClause) {

		MappedSelect query = MappedSelect.query(SUM_QUERY)
				.param(ENTITY_NAME_PARAMETER, AccountTransaction.class.getSimpleName())
				.param(FIELD_NAME, AccountTransaction.AMOUNT.name)
				.param(WHERE_CLAUSE_PARAMETER, whereClause)
				.forceNoCache()

		Map<String, BigDecimal> row = (Map<String, BigDecimal>) context.performQuery(query).get(0)
		BigDecimal result = row.get(AMOUNT_SUM_COLUMN)

		return result
	}
}
