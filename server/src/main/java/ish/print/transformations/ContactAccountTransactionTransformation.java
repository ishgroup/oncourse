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

package ish.print.transformations;

import ish.common.types.AccountTransactionType;
import ish.oncourse.cayenne.PersistentObjectI;
import ish.print.AdditionalParameters;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 */
public class ContactAccountTransactionTransformation extends PrintTransformation {

	private static Logger logger = LogManager.getLogger();

	public ContactAccountTransactionTransformation() {
		setInputEntityName("Contact");
		setOutputEntityName("AccountTransaction");

		LocalDate now = LocalDate.now();

		//see ish.util.DateTimeUtil.getFinancialYearStart()
		LocalDate defaultStartDate = LocalDate.of(now.getMonth().getValue() < 7 ? now.getYear() - 1 : now.getYear(), Month.JULY, 1);
		LocalDate defaultEndDate = now;

		PrintTransformationField<LocalDate> from = new PrintTransformationField<>(AdditionalParameters.LOCALDATERANGE_FROM.toString(), "from", LocalDate.class, defaultStartDate);
		PrintTransformationField<LocalDate> to = new PrintTransformationField<>(AdditionalParameters.LOCALDATERANGE_TO.toString(), "to", LocalDate.class, defaultEndDate);

		addFieldDefinition(from);
		addFieldDefinition(to);
	}

	@Override
	public List<PersistentObjectI> applyTransformation(ObjectContext context, List<Long> sourceIds, Map<String, Object> additionalValues) {

		logger.info("applying transform {} on {} with {}", this, sourceIds, additionalValues);
		if (sourceIds == null || sourceIds.isEmpty()) {
			throw new IllegalArgumentException("id list is null or empty.");
		}

		Map<String, List<PersistentObjectI>> financialItems = new HashMap<>();
		financialItems.put(AccountTransactionType.INVOICE_LINE.getDatabaseValue(), new ArrayList<>());
		financialItems.put(AccountTransactionType.PAYMENT_IN_LINE.getDatabaseValue(), new ArrayList<>());
		financialItems.put(AccountTransactionType.PAYMENT_OUT_LINE.getDatabaseValue(), new ArrayList<>());



		for (int offset = 0; offset < sourceIds.size(); offset += getBatchSize()) {
		//	Expression base = Expression.fromString("createdOn > $"+AdditionalParameters.DATERANGE_FROM.toString() +" and createdOn < $"+AdditionalParameters.DATERANGE_TO.toString());
			Map<String, Object> params = new HashMap<>(additionalValues);

			params.put("sourceIds", sourceIds.subList(offset, Math.min(offset + getBatchSize(), sourceIds.size())));

			List<PersistentObjectI> invoiceLines = ObjectSelect.query(PersistentObjectI.class, "InvoiceLine")
					.where(ExpressionFactory.exp("invoice.contact.id in $sourceIds").params(params, true))
					.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE_REFRESH)
					.select(context);

			List<PersistentObjectI> paymentInLines = ObjectSelect.query(PersistentObjectI.class, "PaymentInLine")
					.where(ExpressionFactory.exp("paymentIn.payer.id in $sourceIds").params(params, true))
					.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE_REFRESH)
					.select(context);

			List<PersistentObjectI> paymentOutLines = ObjectSelect.query(PersistentObjectI.class, "PaymentOutLine")
					.where(ExpressionFactory.exp("paymentOut.payee.id in $sourceIds").params(params, true))
					.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE_REFRESH)
					.select(context);


			financialItems.get(AccountTransactionType.INVOICE_LINE.getDatabaseValue()).addAll(invoiceLines);
			financialItems.get(AccountTransactionType.PAYMENT_IN_LINE.getDatabaseValue()).addAll(paymentInLines);
			financialItems.get(AccountTransactionType.PAYMENT_OUT_LINE.getDatabaseValue()).addAll(paymentOutLines);
		}

		List<PersistentObjectI> records = new ArrayList<>();

		for (String itemType : financialItems.keySet()) {

			List<Long> items = financialItems.get(itemType).stream()
					.map(item -> (Long) item.getValueForKey("id"))
					.collect(Collectors.toList());

			for (int offset = 0; offset < items.size(); offset += getBatchSize()) {

				Map<String, Object> params = new HashMap<>(additionalValues);

				params.put("sourceIds", items.subList(offset, Math.min(offset + getBatchSize(), items.size())));
				params.put("table", itemType);

				Expression exp = ExpressionFactory.exp("createdOn > $" +
						AdditionalParameters.LOCALDATERANGE_FROM.toString() +
						" and createdOn < $" +
						AdditionalParameters.LOCALDATERANGE_TO.toString())
						.andExp(ExpressionFactory
								.exp("tableName = $table and foreignRecordId in $sourceIds"))
						.params(params, true);

				ObjectSelect<PersistentObjectI> objectSelect = ObjectSelect
						.query(PersistentObjectI.class, "AccountTransaction")
						.where(exp)
						.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE_REFRESH);

				records.addAll(objectSelect.select(context));
			}
		}

		return records;
	}
}
