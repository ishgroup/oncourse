package ish.oncourse.model;

import ish.oncourse.model.auto._InvoiceLineDiscount;
import ish.oncourse.utils.QueueableObjectUtils;

public class InvoiceLineDiscount extends _InvoiceLineDiscount implements Queueable {
	private static final long serialVersionUID = -1535339144583077217L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	/**
	 * Check if async replication is allowed for this object.
	 * 
	 * @return
	 */
	public boolean isAsyncReplicationAllowed() {
        /**
         * property InvoiceLine is null when InvoiceLineDiscount was deleted.
         * so we shoud replicate this changes to angel when if the InvoiceLineDiscount has angelid
         */
        if (getInvoiceLine() == null)
            return getAngelId() != null;
		return getInvoiceLine().isAsyncReplicationAllowed();
	}
}
