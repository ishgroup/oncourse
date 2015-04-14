//
// WillowMessagingTemplate.java
// WillowMessaging
//
// Created by Lachlan Deck on 10/03/06.
// Copyright (c) 2006 ISH Group Pty Ltd. All rights reserved.
//
package ish.oncourse.services.template;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This engine uses a call-back methodology to discover the values to replace
 * template keys with. The template engine has the following recursive flow for
 * any <code>responder</code>:
 * <ul>
 * <li>request <code>responder</code> for the template string.</li>
 * <li>discover keypaths in the template string.</li>
 * <li>request <code>responder</code> for relevant values to replace keys with.
 * (Note: Each value may correspond to either an Object or an instance of
 * WillowMessagingTemplateResponder.)</li>
 * <li>replace template string keypaths with provided value.
 * </ul>
 * 
 * @author Lachlan Deck
 */
public class WillowMessagingTemplateEngine {

	public static final String CLASS_ITEM_TEMPLATE = "ClassListItem.messagingtemplate";
	public static final String ENROLMENT_CONFIRMATION_MESSAGE = "Enrolment confirmation";

	private static final Logger logger = LogManager.getLogger();
	public static final String PAYMENT_CLASS_ITEM_TEMPLATE = "ItemisedClassListItem.messagingtemplate";
	public static final String PAYMENT_CONFIRMATION_MESSAGE = "Payment succeeded";
	public static final String PAYMENT_FAILED_MESSAGE = "Payment failed";

	public static final String PAYMENT_LINE_ITEM_TEMPLATE = "ItemisedLineItem.messagingtemplate";
	public static final String SMS_TUTOR_REMINDER = "SMS tutor reminder";
	public static final String STUDENT_ITEM_TEMPLATE = "StudentItem.messagingtemplate";
	private static final Pattern TAGS_PATTERN = Pattern.compile("<[ ]*([\\w]+(?:\\.[\\w]+)*)[ ]*>", Pattern.MULTILINE);

	/**
	 * The engine starting point. Pass the parent template's responder to this
	 * method to generate the entire translated response of the parent templates
	 * and any sub-templates.
	 * 
	 * @param responder
	 *            - the parent responder for a template.
	 * @throws NullPointerException
	 *             if responder is null or values for the interface return null.
	 * @throws IllegalArgumentException
	 *             if the keyValues keys returned from templateValuesForKeys do
	 *             not match keys in template
	 * @return the parsed template with keys ( "message"[, "subject"] ).
	 */
	public static Map<String, String> generateResponseForTemplateResponder(WillowMessagingTemplateResponder responder)
			throws NullPointerException, IllegalArgumentException {
		Map<String, String> input = new HashMap<>(2);
		input.put("message", responder.templateMessage());
		input.put("subject", responder.templateSubject());

		Map<String, String> results = new HashMap<>(2);
		for (Map.Entry<String, String> entry : input.entrySet()) {
			Set<String> keyPaths = keyPathsForTemplate(entry.getValue());
			if (logger.isDebugEnabled())
				logger.debug("keyPaths for template:" + keyPaths);
			if (keyPaths.size() > 0) {
				Map<String, Object> keyValues = responder.templateValuesForKeys(keyPaths);
				if (logger.isDebugEnabled())
					logger.debug("keyValues:" + keyValues);
				if (!keyPaths.equals(keyValues.keySet()))
					throw new IllegalArgumentException("keyValues keys do not match keys of the given template: "
							+ ((Object) responder).getClass().getName());

				results.put(entry.getKey(), translatedTemplateFromValues(entry.getValue(), keyValues));
			} else
				results.put(entry.getKey(), entry.getValue());
		}
		if (results.get("message") == null)
			results.put("message", "");
		if (results.get("subject") == null)
			results.put("subject", "");
		return results;
	}

	/**
	 * This method discovers and returns the embedded keypaths in the template
	 * string. The keypaths returned are of the form <code>key[.key[etc]]</code>
	 * . Within the template document, they are expected to be surrounded with
	 * angle brackets. i.e., <key.path>. Note: spaces between the brackets and
	 * the keypath are optionally allowed.
	 * 
	 * @throws NullPointerException
	 *             if <code>template</code> is null.
	 * @return the keypaths found in the template.
	 */
	public static Set<String> keyPathsForTemplate(String template) throws NullPointerException {
		Set<String> keyPaths = Collections.synchronizedSet(new HashSet<String>());
		if (template != null && template.length() > 0) {
			Matcher matcher = TAGS_PATTERN.matcher(template);
			while (matcher.find() && matcher.groupCount() == 1)
				keyPaths.add(matcher.group(1));
		}
		return keyPaths;
	}

	public static String quoteReplacement(String s) {
		return Matcher.quoteReplacement(s);
	}

	/**
	 * Replaces keypaths in the template with the string representation of the
	 * corresponding values. If any values are an instance of
	 * WillowMessagingTemplateResponder then the string response is found by
	 * calling <code>generateResponseForTemplateResponder(value)</code>.
	 * 
	 * @throws NullPointerException
	 *             if <code>template</code> is null or if any embedded templates
	 *             do not respond with non-null values.
	 * @throws IllegalArgumentException
	 *             if any embedded <code>responder</code>s do not respond
	 *             appropriately.
	 * @return the translated template.
	 */
	public static String translatedTemplateFromValues(String template, Map<String, Object> keyValues) throws NullPointerException,
			IllegalArgumentException {
		String result = template;
		for (String keyPath : keyValues.keySet()) {
			Object value = keyValues.get(keyPath);
			String pattern = "<[ ]*" + keyPath + "[ ]*>";

			if (logger.isDebugEnabled())
				logger.debug("translated keyPath:" + keyPath + " value:" + value);

			if (value != null) {
				if (value instanceof WillowMessagingTemplateResponder) {
					logger.debug("translated templateResponder...start");
					Map<String, String> parsedValue = generateResponseForTemplateResponder((WillowMessagingTemplateResponder) value);
					value = parsedValue.get("message");
					logger.debug("translated templateResponder...end");
				}
				value = quoteReplacement(value.toString());
				if (logger.isDebugEnabled())
					logger.debug("transated quoted:" + value);
				result = result.replaceAll(pattern, value.toString());
			} else
				result = result.replaceAll(pattern, "");
		}
		return result;
	}

	protected WillowMessagingTemplateEngine() {
	}
}
