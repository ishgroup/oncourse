package ish.oncourse.model;

import ish.oncourse.model.auto._College;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class College extends _College {
	
	public static final String REQUESTING_COLLEGE_ATTRIBUTE = "RequestingCollege";

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? ((Number) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN)).longValue() : null;
	}

	public Set<String> getCollegeSiteStates() {
		Set<String> states = new HashSet<String>();

		for (Site site : getSites()) {
			if (site.getState() != null) {
				states.add(site.getState());
			}
		}

		return states;
	}

	public List<ConcessionType> getActiveConcessionTypes() {
		List<ConcessionType> activeConcessionTypes = new ArrayList<ConcessionType>();
		for (ConcessionType concessionType : getConcessionTypes()) {
			if (Boolean.TRUE.equals(concessionType.getIsConcession()) && Boolean.TRUE.equals(concessionType.getIsEnabled())) {
				activeConcessionTypes.add(concessionType);
			}
		}
		return activeConcessionTypes;
	}

	/**
	 * Checks if the payment gateway processing is enabled for this college
	 * 
	 * @return
	 */
	public boolean isPaymentGatewayEnabled() {
		return getPaymentGatewayType() != null
				&& !PaymentGatewayType.DISABLED.equals(getPaymentGatewayType());
	}
}
