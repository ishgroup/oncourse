package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * A code which indicates the student status for a unit of study
 * http://heimshelp.deewr.gov.au/sites/heimshelp/2014_data_requirements/2014dataelements/pages/490
 * @author vdavidovich
 */
public enum StudentStatusForUnitOfStudy implements DisplayableExtendedEnumeration<Integer> {

	/*Contribution-liable students*/
	DEFERRED_ALL_OR_PART_OF_STUDENT_CONTRIBUTION_THROUGH_HECS_HELP(201,
		"Deferred all or part of the student contribution through HECS-HELP",
			"Deferred all or part of the student contribution through HECS-HELP"),
	PAID_FULL_STUDENT_CONTRIBUTION_UP_FRONT_WITH_HECS_HELP(202,
		"Paid the full student contribution up-front with the HECS-HELP discount",
			"Paid the full student contribution up-front with the HECS-HELP discount"),
	PAID_FULL_STUDENT_CONTRIBUTION_UP_FRONT_WITHOUT_HECS_HELP(203,
		"Paid the full student contribution up-front without the HECS-HELP discount",
			"Paid the full student contribution up-front without the HECS-HELP discount"),
	/*FEE-HELP eligible students*/
	DEFERRED_ALL_OR_PART_OF_AWARD_OR_ENABLING_COURSE_TUITION_FEE_THROUGH_FEE_HELP(230,
		"Deferred all or part of Award or Enabling course tuition fee through FEE-HELP",
			"Deferred all or part of Award or Enabling course tuition fee through FEE-HELP"),
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_FEE_HELP(231,
		"Deferred all or part of tuition fee through FEE-HELP - Employer reserved place",
			"Deferred all or part of tuition fee through FEE-HELP - Employer reserved place"),
	DEFERRED_ALL_OR_PART_OF_OUA_TUITION_FEE_THROUGH_FEE_HELP(232,
		"Deferred all or part of OUA tuition fee through FEE-HELP",
			"Deferred all or part of OUA tuition fee through FEE-HELP"),
	DEFERRED_ALL_OR_PART_OF_BOTP_TUITION_FEE_THROUGH_FEE_HELP(233,
		"Deferred all or part of BOTP tuition fee through FEE-HELP",
			"Deferred all or part of BOTP tuition fee through FEE-HELP"),
	/*Students receiving OS-HELP assistance*/
	OS_HELP_FOR_STUDY_IN_NON_ASIAN_COUNTRIES(240, "OS-HELP for study in non-Asian countries",
		"OS-HELP for study in non-Asian countries"),
	OS_HELP_FOR_STUDY_IN_ASIA(241, "OS-HELP for study in Asia", "OS-HELP for study in Asia"),
	OS_HELP_LANGUAGE_STUDY(242, "OS-HELP Language study", "OS-HELP Language study"),
	/*Contribution-exempt students*/
	STUDENT_IN_A_COMMONWEALTH_SUPPORTED_PLACE_WITH_AN_EXEMPTION_SCHOLARSHIP(260,
		"Student in a Commonwealth supported place with an Exemption scholarship (no student contribution to be charged)",
			"Student in a Commonwealth supported place with an Exemption scholarship (no student contribution to be charged)"),
	DOMESTIC_STUDENT_ENROLLED_IN_AN_ENABLING_COURSE(261,
		"A domestic student enrolled in an enabling course (i.e. bridging or supplementary programme)",
			"A domestic student enrolled in an enabling course (i.e. bridging or supplementary programme)"),
	STUDENT_UNDERTAKING_WORK_EXPERIENCE_IN_INDUSTRY_PROVIDER_AND_FOR_WHICH_A_STUDENT_CONTRIBUTION_CANNOT_BE_CHARGE(262,
		"Student undertaking Work Experience in Industry (WEI) where learning and performance is not directed by, and support is not received from, the provider and for which a student contribution cannot be charged",
			"Student undertaking Work Experience in Industry (WEI) where learning and performance is not directed by, and support is not received from, the provider and for which a student contribution cannot be charged"),
	/*Non-overseas tuition fee-exempt students*/
	STUDENT_IN_A_NON_COMMONWEALTH_SUPPORTED_PLACE_WITH_AN_EXEMPTION_SCHOLARSHIP(270,
		"Student in a non-Commonwealth supported place with an Exemption scholarship (no tuition fee to be charged)",
			"Student in a non-Commonwealth supported place with an Exemption scholarship (no tuition fee to be charged)"),
	STUDENT_UNDERTAKING_WORK_EXPERIENCE_IN_INDUSTRY_PROVIDER_AND_FOR_WHICH_A_TUITION_FEE_CANNOT_BE_CHARGED(271,
		"Student undertaking Work Experience in Industry (WEI) where learning and performance is not directed by, and support is not received from, the provider and for which a tuition fee cannot be charged",
			"Student undertaking Work Experience in Industry (WEI) where learning and performance is not directed by, and support is not received from, the provider and for which a tuition fee cannot be charged"),
	/*Students receiving SA-HELP assistance*/
	DEFERRED_ALL_OR_PART_OF_SA_FEE_FOR_A_COURSE_OF_STUDY_THROUGH_SA_HELP(280,
		"Deferred all or part of SA-fee for a Course of Study through SA-HELP",
			"Deferred all or part of SA-fee for a Course of Study through SA-HELP"),
	DEFERRED_ALL_OR_PART_OF_SA_FEE_FOR_A_BRIDGING_COURSE_FOR_OVERSEAS_TRAINED_PROFESSIONAL_THROUGH_SA_HELP(281,
		"Deferred all or part of SA-fee for a Bridging Course for Overseas Trained Professional through SA-HELP",
			"Deferred all or part of SA-fee for a Bridging Course for Overseas Trained Professional through SA-HELP"),
	/*Fee-paying non-overseas students*/
	DOMESTIC_STUDENT_ENROLLED_IN_A_NON_AWARD_COURSE(301,
		"A domestic student enrolled in a non-award course (other than an Enabling course)",
			"A domestic student enrolled in a non-award course (other than an Enabling course)"),
	PAID_FULL_AWARD_OR_ENABLING_COURSE_TUITION_FEE(302, "Paid full Award or Enabling course tuition fee",
		"Paid full Award or Enabling course tuition fee"),
	PAID_FULL_TUITION_FEE_FOR_EMPLOYER_RESERVED_PLACE(303, "Paid full tuition fee - for Employer reserved place",
		"Paid full tuition fee - for Employer reserved place"),
	PAID_FULL_OUA_TUITION_FEE(304, "Paid full OUA tuition fee", "Paid full OUA tuition fee"),
	PAID_FULL_BOTP_TUITION_FEE(305, "Paid full BOTP tuition fee", "Paid full BOTP tuition fee"),
	/*Fee-paying overseas students*/
	FEE_PAYING_OVERSEAS_STUDENT_NOT_SPONSORED_UNDER_A_FOREIGN_AID_PROGRAM_AND_INCLUDING_STUDENTS_WITH_AWARDS_IPRS_OR_SOPF_OR_AUSTRALIAN_EUROPEAN_AWARD_PROGRAMM_OR_COMMONWEALTH_SCHOLARSHIP_AND_FELLOWSHIP_PLAN(310,
		"A fee-paying overseas student who is not sponsored under a foreign aid program, and including students with these awards: IPRS (International Postgraduate Research Scheme); SOPF (Special Overseas Postgraduate Fund); Australian-European Awards Program; and the Commonwealth Scholarship and Fellowship Plan",
			"A fee-paying overseas student who is not sponsored under a foreign aid program, and including students with these awards: IPRS (International Postgraduate Research Scheme); SOPF (Special Overseas Postgraduate Fund); Australian-European Awards Program; and the Commonwealth Scholarship and Fellowship Plan"),
	FEE_PAYING_OVERSEAS_STUDENT_SPONSORED_UNDER_A_FOREIGN_AID_PROGRAM_AND_INCLUDES_ADCOS_AND_ANY_OTHER_AUSTRALIAN_FOREIGN_AID_PROGRAM(311,
		"A fee-paying overseas student who is sponsored under a foreign aid program. Includes those with Australian Development Cooperation Scholarships (ADCOS) and any other Australian foreign aid program for which students are enrolled in higher education providers by the Australian Agency for International Development (AusAID)",
			"A fee-paying overseas student who is sponsored under a foreign aid program. Includes those with Australian Development Cooperation Scholarships (ADCOS) and any other Australian foreign aid program for which students are enrolled in higher education providers by the Australian Agency for International Development (AusAID)"),
	/*Pre-2001 RTS students*/
	DOMESTIC_STUDENT_WHO_RECEIVED_A_RESEARCH_HECS_EXAMPTION_PRIOR_TO_FIRST_SEPTEMBER_2000_AND_CONTINUES_STUDYING_ON_A_STUDENT_CONTRIBUTION_EXEMPT_BASIS_UNDER_RTS_AS_PRE_2001_STUDENT(320,
		"A domestic student who received a research HECS exemption prior to 1 September 2000 and continues studying on a student contribution exempt basis under the Research Training Scheme (RTS) as a pre-2001 student. Include internal transfers/upgrades/downgrades (as defined under Element 465) (see coding notes)",
			"A domestic student who received a research HECS exemption prior to 1 September 2000 and continues studying on a student contribution exempt basis under the Research Training Scheme (RTS) as a pre-2001 student. Include internal transfers/upgrades/downgrades (as defined under Element 465) (see coding notes)"),
	/*RTS students from 2001*/
	DOMESTIC_STUDENT_ENROLLED_UNDER_RTS_FROM_FIRST_SEPTEMBER_2000(330,
		"A domestic student enrolled under the Research Training Scheme (RTS) from 1 September 2000 (see coding notes)",
			"A domestic student enrolled under the Research Training Scheme (RTS) from 1 September 2000 (see coding notes)"),
	/*VET Students only:*/
	/*VET FEE-HELP assisted students*/
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_NON_STATE_GOVERNMENT_SUBSIDISED(401,
		"Deferred all/part of tuition fee through VET FEE-HELP - non State Government subsidised",
			"Deferred all/part of tuition fee through VET FEE-HELP - non State Government subsidised"),
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_RESTRICTED_ACCESS_ARRANGEMENT(402,
		"Deferred all/part of tuition fee through VET FEE-HELP - Restricted Access Arrangement",
			"Deferred all/part of tuition fee through VET FEE-HELP - Restricted Access Arrangement"),
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_VICTORIAN_STATE_GOVERNMENT_SUBSIDISED(403,
		"Deferred all/part of tuition fee through VET FEE-HELP - Victorian State Government subsidised",
			"Deferred all/part of tuition fee through VET FEE-HELP - Victorian State Government subsidised"),
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_QUEENSLAND_STATE_GOVERNMENT_SUBSIDISED(405,
		"Deferred all/part of tuition fee through VET FEE-HELP - Queensland State Government subsidised",
			"Deferred all/part of tuition fee through VET FEE-HELP - Queensland State Government subsidised"),
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_SOUTH_AUSTRALIAN_STATE_GOVERNMENT_SUBSIDISED(406,
		"Deferred all/part of tuition fee through VET FEE-HELP - South Australian State Government subsidised",
			"Deferred all/part of tuition fee through VET FEE-HELP - South Australian State Government subsidised"),
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_WESTERN_AUSTRALIAN_STATE_GOVERNMENT_SUBSIDISED(407,
		"Deferred all/part of tuition fee through VET FEE-HELP - Western Australian State Government subsidised",
			"Deferred all/part of tuition fee through VET FEE-HELP - Western Australian State Government subsidised"),
	/*Students paying upfront*/
	PAID_FULL_TUITION_FEE_NON_STATE_GOVERNMENT_SUBSIDISED(501,
		"Paid full tuition fee - non-State Government subsidised",
			"Paid full tuition fee - non-State Government subsidised"),
	PAID_FULL_TUITION_FEE_RESTRICTED_ACCESS_ARRANGEMENT(502,
		"Paid full tuition fee - Restricted Access Arrangement",
			"Paid full tuition fee - Restricted Access Arrangement"),
	PAID_FULL_TUITION_FEE_VICTORIAN_STATE_GOVERNMENT_SUBSIDISED(503,
		"Paid full tuition fee - Victorian State Government subsidised",
			"Paid full tuition fee - Victorian State Government subsidised"),
	PAID_FULL_TUITION_FEE_QUEENSLAND_STATE_GOVERNMENT_SUBSIDISED(505,
		"Paid full tuition fee - Queensland State Government subsidised",
			"Paid full tuition fee - Queensland State Government subsidised"),
	PAID_FULL_TUITION_FEE_AUSTRALIAN_STATE_GOVERNMENT_SUBSIDISED(506,
		"Paid full tuition fee - South Australian State Government subsidised",
			"Paid full tuition fee - South Australian State Government subsidised"),
	PAID_FULL_TUITION_FEE_WESTERN_AUSTRALIAN_STATE_GOVERNMENT_SUBSIDISED(507,
		"Paid full tuition fee - Western Australian State Government subsidised",
			"Paid full tuition fee - Western Australian State Government subsidised")
	;

	private String displayValue;
	private String description;
	private int value;

	private StudentStatusForUnitOfStudy(int value, String displayValue, String description) {
		this.displayValue = displayValue;
		this.description = description;
		this.value = value;
	}

	@Override
	public String getDisplayName() {
		return displayValue;
	}

	@Override
	public Integer getDatabaseValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
