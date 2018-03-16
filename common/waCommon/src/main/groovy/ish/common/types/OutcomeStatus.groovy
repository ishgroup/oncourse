/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types

import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.API

/**
 * A set of values for AVETMISS reporting from the version 7 standard.
 * Consult the AVETMISS documentation for more detail about these options.
 * 
 */
@API
public enum OutcomeStatus implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 0
	 *
	 * Not set
	 */
	@API
	STATUS_NOT_SET(0, "Not set", "Not set"),

	/**
	 * Database value: 20
	 *
	 * Competency achieved/pass
	 */
	@API
	STATUS_ASSESSABLE_PASS(20, "Competency achieved/pass (20)", null, true),
	
	/**
	 * Database value: 30
	 *
	 * Competency not achieved/fail
	 */
	@API
	STATUS_ASSESSABLE_FAIL(30, "Competency not achieved/fail (30)", null),

	/**
	 * Database value: 40
	 *
	 * Withdrawn
	 */
	@API
	STATUS_ASSESSABLE_WITHDRAWN(40, "Withdrawn (40)", null),

    /**
     * Database value: 41
     *
     * Incomplete due to RTO closure
     */
    @API
    STATUS_ASSESSABLE_WITHDRAWN_INCOMPLETE_DUE_TO_RTO(41, "Incomplete due to RTO closure (41)", null),

    /**
	 * Database value: 51
	 *
	 * RPL granted
	 */
	@API
	STATUS_ASSESSABLE_RPL_GRANTED(51, "RPL granted (51)", null, true), // recognition of prior learning

	/**
	 * Database value: 52
	 *
	 * RPL not granted
	 */
	@API
	STATUS_ASSESSABLE_RPL_NOT_GRANTED(52, "RPL not granted (52)", null),

	/**
	 * Database value: 53
	 *
	 * RCC granted
	 */
	@API
	STATUS_ASSESSABLE_RCC_GRANTED(53, "RCC granted (53)", null, true),

	/**
	 * Database value: 54
	 *
	 * RCC not granted
	 */
	@API
	STATUS_ASSESSABLE_RCC_NOT_GRANTED(54, "RCC not granted (54)", null), // recognition of current competency

	/**
	 * Database value: 60
	 *
	 * Credit Transfer
	 */
	@API
	STATUS_ASSESSABLE_CREDIT_TRANSFER(60, "Credit Transfer (60)", null, true),

	/**
	 * Database value: 65
	 *
	 * Superseded Qualification
	 */
	@API
	STATUS_SUPERSEDED_QUALIFICATION_QLD(65, "Superseded Qualification (QLD: 65)", null),

	/**
	 * Database value: 66
	 *
	 * Did not start
	 */
	@API
	STATUS_ASSESSABLE_DET_DID_NOT_START(66, "Did not start (NSW: 66, SA: @@)", null),

	/**
	 * Database value: 70
	 *
	 * Continuing enrolment
	 */
	@API
	STATUS_ASSESSABLE_CONTINUING_ENROLMENT(70, "Continuing enrolment (70)", null),

	/**
	 * Database value: 81
	 *
	 * Satisfactorily completed
	 */
	@API
	STATUS_NON_ASSESSABLE_COMPLETED(81, "Satisfactorily completed (81)", "Satisfactorily completed (81)"),

	/**
	 * Database value: 82
	 *
	 * Withdrawn or not satisfactorily completed
	 */
	@API
	STATUS_NON_ASSESSABLE_NOT_COMPLETED(82, "Withdrawn or not satisfactorily completed (82)", "Withdrawn or not satisfactorily completed (82)"),

	/**
	 * Database value: 90
	 *
	 * Result not available
	 */
	@API
	STATUS_NO_RESULT_QLD(90, "Result not available (90)", "Result not available (90)"),

	// WA only statuses
	/**
	 * Database value: 5
	 *
	 * Participating, but studies not finished (with evidence of Training)
	 */
	@API
	STATUS_WA_PARTICIPATING_WITH_EVIDENCE(5, "Participating, but studies not finished (with evidence of Training) (WA:5)", null),

	/**
	 * Database value: 55
	 *
	 * Participating, but studies not finished (With NO evidence of Training)
	 */
	@API
	STATUS_WA_PARTICIPATING_WITHOUT_EVIDENCE(55, "Participating, but studies not finished (With NO evidence of Training) (WA: 55)", null),

	/**
	 * Database value: 15
	 *
	 * Recognition of Current Competencies (RCC) granted
	 */
	@API
	STATUS_WA_RCC_GRANTED(15, "Recognition of Current Competencies (RCC) granted (WA:15)", null),

	/**
	 * Database value: 16
	 *
	 * Recognition of Current Competencies (RCC) not granted
	 */
	@API
	STATUS_WA_RCC_NOT_GRANTED(16, "Recognition of Current Competencies (RCC) not granted (WA:16)", null),

	/**
	 * Database value: 8
	 *
	 * Provisionally competent off the job (apprentices only)
	 */
	@API
	STATUS_WA_PROVISIONALLY_COMPETENT(8, "Provisionally competent off the job (apprentices only) (WA:8)", null),

	/**
	 * Database value: 11
	 *
	 * Discontinued - no formal withdrawal (after some participation)
	 */
	@API
	STATUS_WA_DISCONTINUED(11, "Discontinued - no formal withdrawal (after some participation) (WA:11)", null),

	/**
	 * Database value: 105
	 *
	 * Not yet started
	 */
	@API
	STATUS_WA_NOT_YET_STARTED(105, "Not yet started (WA:105)", null),

	/**
	 * Database value: 61
	 *
	 * Superseded subject
	 */
	@API
	SUPERSEDED_SUBJECT(61, "Superseded subject (61)", null);

	/**
	 * OutcomeStatuses which are valid to be added to a certificate
	 * 
	 */
	@API
	public static final List<OutcomeStatus> STATUSES_VALID_FOR_CERTIFICATE = Arrays.asList(STATUS_ASSESSABLE_PASS,
            STATUS_ASSESSABLE_RPL_GRANTED,
            STATUS_ASSESSABLE_RCC_GRANTED,
            STATUS_ASSESSABLE_CREDIT_TRANSFER);

	private String assessableName;
	private String nonAssessableName;
	private int value;

	/**
	 * statuses which are valid to display
	 */
	public static final Map<String, OutcomeStatus> STATUS_CHOICES_ALL = TypesUtil.getValuesAsMap(OutcomeStatus.class);
	/**
	 * statuses which are valid to add to a non-VET course
	 */
	public static final Map<String, OutcomeStatus> STATUS_CHOICES_NON_VET = new LinkedHashMap<>();
	/**
	 * statuses which are valid to display
	 */
	public static final List<String> STATUS_CHOICES_DEPRECATED = new ArrayList<>();
	/**
	 * statuses which are valid to add to a VET course
	 */
	public static final Map<String, OutcomeStatus> STATUS_CHOICES_VET = TypesUtil.getValuesAsMap(OutcomeStatus.class);

	static {
		STATUS_CHOICES_DEPRECATED.add(STATUS_ASSESSABLE_RCC_GRANTED.getDisplayName());
		STATUS_CHOICES_DEPRECATED.add(STATUS_ASSESSABLE_RCC_NOT_GRANTED.getDisplayName());

		for (String key : STATUS_CHOICES_DEPRECATED) {
			STATUS_CHOICES_VET.remove(key);
		}

		STATUS_CHOICES_NON_VET.put(STATUS_NOT_SET.getAlternateDisplayName(), STATUS_NOT_SET);
		STATUS_CHOICES_NON_VET.put(STATUS_NON_ASSESSABLE_COMPLETED.getAlternateDisplayName(), STATUS_NON_ASSESSABLE_COMPLETED);
		STATUS_CHOICES_NON_VET.put(STATUS_NON_ASSESSABLE_NOT_COMPLETED.getAlternateDisplayName(),
				STATUS_NON_ASSESSABLE_NOT_COMPLETED);
	}

    private boolean assessable = false;

    private OutcomeStatus(int value, String assessableName, String nonAssessableName) {
        this(value, assessableName, nonAssessableName, false);
    }

	private OutcomeStatus(int value, String assessableName, String nonAssessableName, boolean assessable) {
		this.value = value;
		this.assessableName = assessableName;
		this.nonAssessableName = nonAssessableName;
        this.assessable = assessable;
	}

	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDatabaseValue()
	 */
	@Override
	public Integer getDatabaseValue() {
		return this.value;
	}

	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return this.assessableName;
	}

	/**
	 * @return alternate name for a outcome status, associated with the non-assessable outcome
	 */
	public String getAlternateDisplayName() {
		return this.nonAssessableName;
	}

	/**
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return getDisplayName();
	}

	public boolean isAssessable() {
        return assessable
	}
}