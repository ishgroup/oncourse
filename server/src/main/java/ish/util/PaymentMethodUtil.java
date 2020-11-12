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
package ish.util;

import ish.common.types.PaymentType;
import ish.oncourse.cayenne.PaymentMethodInterface;
import ish.persistence.RecordNotFoundException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class shared between client and server allowing easy fetching of PaymentMethods.<br/>
 *
 */
public class PaymentMethodUtil {

	private static Logger logger = LogManager.getLogger();

	private PaymentMethodUtil() {}

	public static final List<PaymentType> SYSTEM_TYPES = Arrays.asList(
			PaymentType.CONTRA, PaymentType.INTERNAL, PaymentType.REVERSE, PaymentType.VOUCHER);


	public static <T extends PaymentMethodInterface> Map<String, T> getPaymentMethods(final ObjectContext context, Class<T> paymentMethodClass) throws RecordNotFoundException {

		Map<String, T> result = new HashMap<>();

		List<T>  list = ObjectSelect.query(paymentMethodClass).select(context);
		for (T paymentMethod : list) {
			result.put(paymentMethod.getName(), paymentMethod);
		}

		return result;
	}

	public static <T extends PaymentMethodInterface> Map<String, T> getChoosablePaymentMethods(final ObjectContext context, Class<T> paymentMethodClass) throws RecordNotFoundException {
		Map<String, T> result = new HashMap<>();

		for (T method : getPaymentMethods(context, paymentMethodClass).values()) {
			if (method.getActive()) {
				result.put(method.getName(), method);
			}
		}
		result.remove(PaymentType.VOUCHER.getDisplayName());
		result.remove(PaymentType.CONTRA.getDisplayName());
		result.remove(PaymentType.REVERSE.getDisplayName());
		result.remove(PaymentType.INTERNAL.getDisplayName());

		return result;
	}


	public static <T extends PaymentMethodInterface> T  getREVERSPaymentMethods(final ObjectContext context, Class<T> paymentMethodClass) {
		return ObjectSelect.query(paymentMethodClass).where(ExpressionFactory.matchExp(PaymentMethodInterface.TYPE_PROPERTY, PaymentType.REVERSE)).selectOne(context);
	}

	public static <T extends PaymentMethodInterface> T  getCONTRAPaymentMethods(final ObjectContext context, Class<T> paymentMethodClass) {
		return ObjectSelect.query(paymentMethodClass).where(ExpressionFactory.matchExp(PaymentMethodInterface.TYPE_PROPERTY, PaymentType.CONTRA)).selectOne(context);
	}

	public static <T extends PaymentMethodInterface> T  getINTERNALPaymentMethods(final ObjectContext context, Class<T> paymentMethodClass) {
		return ObjectSelect.query(paymentMethodClass).where(ExpressionFactory.matchExp(PaymentMethodInterface.TYPE_PROPERTY, PaymentType.INTERNAL)).selectOne(context);
	}

	public static <T extends PaymentMethodInterface> T  getVOUCHERPaymentMethods(final ObjectContext context, Class<T> paymentMethodClass) {
		return ObjectSelect.query(paymentMethodClass).where(ExpressionFactory.matchExp(PaymentMethodInterface.TYPE_PROPERTY, PaymentType.VOUCHER)).selectOne(context);
	}

	public static <T extends PaymentMethodInterface> T  getRealTimeCreditCardPaymentMethod(final ObjectContext context, Class<T> paymentMethodClass) {
		return  ObjectSelect.query(paymentMethodClass).where(ExpressionFactory.matchExp(PaymentMethodInterface.TYPE_PROPERTY, PaymentType.CREDIT_CARD))
				.cacheStrategy(QueryCacheStrategy.NO_CACHE).pageSize(1).select(context).get(0);
	}

	/**
	 * Is uses in unit tests only
	 *
	 * @param context
	 * @param paymentMethodClass
	 * @param type
	 * @param <T>
	 * @return
	 */
	public static <T extends PaymentMethodInterface> T getCustomPaymentMethod(final ObjectContext context, Class<T> paymentMethodClass, PaymentType type) {
		return ObjectSelect.query(paymentMethodClass).where(ExpressionFactory.matchExp(PaymentMethodInterface.NAME_PROPERTY, type.getDisplayName())).selectOne(context);
	}

}
