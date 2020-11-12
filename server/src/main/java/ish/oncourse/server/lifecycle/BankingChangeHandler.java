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
package ish.oncourse.server.lifecycle;


import ish.oncourse.server.cayenne.Banking;
import ish.oncourse.server.cayenne.PaymentIn;
import ish.oncourse.server.cayenne.PaymentOut;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.graph.GraphChangeHandler;

import java.util.HashMap;
import java.util.Map;

public class BankingChangeHandler implements GraphChangeHandler {

	private Map<ObjectId, BankingChange> changes = new HashMap<>();
	private ObjectContext context;

	public BankingChangeHandler(ObjectContext context) {
		this.context = context;
	}

	@Override
	public void nodeIdChanged(Object nodeId, Object newId) {

	}

	@Override
	public void nodeCreated(Object nodeId) {

	}

	@Override
	public void nodeRemoved(Object nodeId) {

	}

	@Override
	public void nodePropertyChanged(Object nodeId, String property, Object oldValue, Object newValue) {
	}

	@Override
	public void arcCreated(Object nodeId, Object targetNodeId, Object arcId) {
		if (isBankin(targetNodeId) && isPayment(nodeId)) {
			var change = changes.get(nodeId);
			if (change == null) {
				changes.put((ObjectId) nodeId, new BankingChange(null,(Banking)context.getGraphManager().getNode(targetNodeId)));
			} else {
				change.setNewValue((Banking) context.getGraphManager().getNode(targetNodeId));
			}
		}
	}

	public Banking getOldValueFor(ObjectId payment) {
		return changes.get(payment) != null ? changes.get(payment).getOldValue() : null;
	}

	public Banking getNewValueFor(ObjectId payment) {
		return changes.get(payment) != null ? changes.get(payment).getNewValue() : null;
	}

	@Override
	public void arcDeleted(Object nodeId, Object targetNodeId, Object arcId) {

		if (isBankin(targetNodeId) && isPayment(nodeId)) {
			var change = changes.get(nodeId);
			if (change == null) {
				changes.put((ObjectId) nodeId, new BankingChange((Banking)context.getGraphManager().getNode(targetNodeId), null));
			} else if (change.getOldValue() == null) {
				change.setOldValue((Banking)context.getGraphManager().getNode(targetNodeId));
			}
		}


	}


	private boolean isPayment(Object nodeId) {
		return nodeId instanceof ObjectId && (PaymentIn.class.getSimpleName().equals(((ObjectId) nodeId).getEntityName()) || PaymentOut.class.getSimpleName().equals(((ObjectId) nodeId).getEntityName()));
	}

	private boolean isBankin(Object nodeId) {
		return nodeId instanceof ObjectId && Banking.class.getSimpleName().equals(((ObjectId) nodeId).getEntityName());
	}


	public static class BankingChange {

		private Banking oldValue = null;
		private Banking newValue = null;

		public BankingChange(Banking oldValue, Banking newValue) {
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		public Banking getOldValue() {
			return oldValue;
		}

		public void setOldValue(Banking oldValue) {
			this.oldValue = oldValue;
		}

		public Banking getNewValue() {
			return newValue;
		}

		public void setNewValue(Banking newValue) {
			this.newValue = newValue;
		}
	}

}
