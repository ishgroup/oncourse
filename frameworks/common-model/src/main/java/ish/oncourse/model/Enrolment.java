package ish.oncourse.model;

import ish.common.types.EnrolmentStatus;
import ish.math.Money;
import ish.oncourse.model.auto._Enrolment;

import java.math.BigDecimal;
import java.util.List;


public class Enrolment extends _Enrolment {

	public boolean isDuplicated(Student student) {
		if (getCourseClass() != null && isSuccessfullOrQueued()) {
			List<Enrolment> enrolments = getStudent().getEnrolments();
			boolean duplicateEnroment = false;
			if (enrolments != null)
				for (Enrolment enrolment : enrolments)
					if (!equals(enrolment))//TODO equalsIgnoreContext
						if (getCourseClass().equals(enrolment.getCourseClass()))//TODO equalsIgnoreContext
							if (enrolment.isSuccessfullOrQueued())
								duplicateEnroment = true;

			if (duplicateEnroment) {
				return true;
			}
		}
		return false;
	}

	
	/**
	 * An enrolments is successfull when its status is 'success' or 'queued', or null (the latter means, not processed yet, not even committed)
	 * 
	 * @return true if the enrolment is considered as successful
	 */
	public boolean isSuccessfullOrQueued() {
		return getStatus() == null || getStatus().equals(EnrolmentStatus.SUCCESS) || getStatus().equals(EnrolmentStatus.QUEUED);
	}



	/*public boolean equalsIgnoreContext(Object obj) {
		if (obj instanceof PersistentObject) {
			return super.equals(obj) || getObjectId().equals(((PersistentObject) obj).getObjectId());
		}
		return false;
	}*/

    public BigDecimal getDiscountedExTaxAmount(){
        if (getDiscount() == null) {
            return getCourseClass().getFeeExGst().toBigDecimal();
        }
        BigDecimal discountRate = getDiscount().getDiscountRate();
        if (discountRate != null) {
            return getCourseClass().getFeeExGst().toBigDecimal().multiply(discountRate);
        } else {
            return getDiscount().getDiscountAmount();
        }
    }

    public BigDecimal getDiscountedIncTaxAmount() {
        BigDecimal exTaxAmount = getDiscountedExTaxAmount();
        BigDecimal taxAmount = getCourseClass().getFeeGst();
        return exTaxAmount.add(taxAmount);
    }


    public BigDecimal getTotalDiscountIncTax() {
        return getCourseClass().getFeeIncGst().toBigDecimal().subtract(getDiscountedIncTaxAmount());
    }

}
