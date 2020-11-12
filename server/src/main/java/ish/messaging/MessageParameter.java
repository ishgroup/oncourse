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
package ish.messaging;

import ish.common.types.MessageType;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Holder class for message sending parameters coming from client.
 *
 */
public class MessageParameter implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String ENROLMENT_CONFIRMATION_TEMPLATE_NAME = "Enrolment confirmation";
	public static final String TAX_INVOICE_TEMPLATE_NAME = "Tax invoice";
	public static final String VOUCHER_EMAIL_TEMPLATE_NAME = "Voucher Email";

	private Map<Long, Long> recordRecipientMap = new HashMap<>();
	private Long userId;
	private MessageType messageType;
	private String messageTemplateName;

	private String entity;

	public void add(Long recordId, Long recipientId) {
		recordRecipientMap.put(recordId, recipientId);
	}

	public Map<Long, Long> getRecordRecipientMap() {
		return Collections.unmodifiableMap(recordRecipientMap);
	}

	public void setUserId(Long id) {
		this.userId = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setMessageType(MessageType type) {
		this.messageType = type;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public void setEntity(String entityName) {
		this.entity = entityName;
	}

	public String getEntity() {
		return entity;
	}

	public String getMessageTemplateName() {
		return messageTemplateName;
	}

	public void setMessageTemplateName(String messageTemplateName) {
		this.messageTemplateName = messageTemplateName;
	}
}
