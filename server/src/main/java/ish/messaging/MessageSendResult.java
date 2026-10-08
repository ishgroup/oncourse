/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.messaging;

import java.io.Serializable;

public class MessageSendResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private int recipientsSent;

	private String errorMessage;

	public int getRecipientsSent() {
		return recipientsSent;
	}

	public void setRecipientsSent(int recipientsSent) {
		this.recipientsSent = recipientsSent;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		return "MessageSendResult{" +
				"recipientsSent=" + recipientsSent +
				", errorMessage='" + errorMessage + '\'' +
				'}';
	}
}
