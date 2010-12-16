package ish.oncourse.model;

import ish.oncourse.model.auto._StudentConcession;

import java.util.Date;

public class StudentConcession extends _StudentConcession {
	/**
	 * If this concession's type has a concession number, will only validate if
	 * the concession number is not empty
	 * 
	 * @return error message
	 */
	public String validateConcessionNumber() {

		if (getConcessionType() != null && getConcessionType().getHasConcessionNumber()) {
			if (getConcessionNumber() == null || getConcessionNumber().length() == 0) {
				return String.format("A %s concession requires a card number.", getConcessionType()
						.getName());
			}
		}
		return null;
	}

	/**
	 * If this concession's type has an expiry date, will only validate if the
	 * expiry date is not empty
	 * 
	 * @return error message
	 */
	public String validateExpiresDate() {
		if (getConcessionType() != null && getConcessionType().getHasExpiryDate()) {
			if (getExpiresOn() == null) {
				return String.format("A %s concession requires an expiry date.",
						getConcessionType().getName());
			}
			if (new Date().compareTo(getExpiresOn()) > 0) {
				return "Expiry date shouldn't be at the past.";
			}
		}
		return null;
	}

	@Override
	public void setConcessionNumber(String concessionNumber) {
		if (concessionNumber != null) {
			concessionNumber = concessionNumber.trim();
		}
		super.setConcessionNumber(concessionNumber);
	}
}
