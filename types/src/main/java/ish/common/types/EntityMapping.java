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

package ish.common.types;

import ish.oncourse.common.field.ReplicatedConfigurationFields;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * provides translation between angel and willow entity names.
 */
public final class EntityMapping {

	private static final Map<String, String> ANGEL_TO_WILLOW = new HashMap<>();

	private static final Map<String, String> WILLOW_TO_ANGEL = new HashMap<>();

	private static final String ANGEL_CONFIGURATION_TO_WILLOW = "Settings";

	public static final Map<String, String> BINARY_RELATION_MAPPING = new HashMap<>();

	static {
		ANGEL_TO_WILLOW.put("AttachmentData", "BinaryData");
		ANGEL_TO_WILLOW.put("AttachmentInfo", "BinaryInfo");

		ANGEL_TO_WILLOW.put("AttachmentRelation", "BinaryInfoRelation");

		ANGEL_TO_WILLOW.put("CertificateAttachmentRelation", "BinaryInfoRelation");
		ANGEL_TO_WILLOW.put("ContactAttachmentRelation", "BinaryInfoRelation");
		ANGEL_TO_WILLOW.put("CourseAttachmentRelation", "BinaryInfoRelation");
		ANGEL_TO_WILLOW.put("CourseClassAttachmentRelation", "BinaryInfoRelation");
		ANGEL_TO_WILLOW.put("EnrolmentAttachmentRelation", "BinaryInfoRelation");
		ANGEL_TO_WILLOW.put("InvoiceAttachmentRelation", "BinaryInfoRelation");
		ANGEL_TO_WILLOW.put("PriorLearningAttachmentRelation", "BinaryInfoRelation");
		ANGEL_TO_WILLOW.put("RoomAttachmentRelation", "BinaryInfoRelation");
		ANGEL_TO_WILLOW.put("SessionAttachmentRelation", "BinaryInfoRelation");
		ANGEL_TO_WILLOW.put("SiteAttachmentRelation", "BinaryInfoRelation");
		ANGEL_TO_WILLOW.put("StudentAttachmentRelation", "BinaryInfoRelation");
		ANGEL_TO_WILLOW.put("TagAttachmentRelation", "BinaryInfoRelation");
		ANGEL_TO_WILLOW.put("TutorAttachmentRelation", "BinaryInfoRelation");
		ANGEL_TO_WILLOW.put("ApplicationAttachmentRelation", "BinaryInfoRelation");
		ANGEL_TO_WILLOW.put("AssessmentAttachmentRelation", "BinaryInfoRelation");
		ANGEL_TO_WILLOW.put("AssessmentSubmissionAttachmentRelation", "BinaryInfoRelation");
		ANGEL_TO_WILLOW.put("ArticleAttachmentRelation", "BinaryInfoRelation");
		ANGEL_TO_WILLOW.put("VoucherAttachmentRelation", "BinaryInfoRelation");
		ANGEL_TO_WILLOW.put("MembershipAttachmentRelation", "BinaryInfoRelation");
		ANGEL_TO_WILLOW.put("ArticleProductAttachmentRelation", "BinaryInfoRelation");
		ANGEL_TO_WILLOW.put("VoucherProductAttachmentRelation", "BinaryInfoRelation");
		ANGEL_TO_WILLOW.put("MembershipProductAttachmentRelation", "BinaryInfoRelation");

		ANGEL_TO_WILLOW.put("TagRequirement", "TagGroupRequirement");
		ANGEL_TO_WILLOW.put("CourseClassTutor", "TutorRole");
		ANGEL_TO_WILLOW.put("TutorAttendance", "SessionTutor");

		ANGEL_TO_WILLOW.put("AttachmentInfoTagRelation", "Taggable");
		ANGEL_TO_WILLOW.put("ContactTagRelation", "Taggable");
		ANGEL_TO_WILLOW.put("CourseClassTagRelation", "Taggable");
		ANGEL_TO_WILLOW.put("CourseTagRelation", "Taggable");
		ANGEL_TO_WILLOW.put("ReportTagRelation", "Taggable");
		ANGEL_TO_WILLOW.put("SiteTagRelation", "Taggable");
		ANGEL_TO_WILLOW.put("StudentTagRelation", "Taggable");
		ANGEL_TO_WILLOW.put("WaitingListTagRelation", "Taggable");
		ANGEL_TO_WILLOW.put("TagTagRelation", "Taggable");
		ANGEL_TO_WILLOW.put("TagRelation", "Taggable");
		ANGEL_TO_WILLOW.put("TutorTagRelation", "Taggable");
		ANGEL_TO_WILLOW.put("DocumentTagRelation", "Taggable");
		ANGEL_TO_WILLOW.put("ApplicationTagRelation", "Taggable");
		ANGEL_TO_WILLOW.put("EnrolmentTagRelation", "Taggable");
		ANGEL_TO_WILLOW.put("RoomTagRelation", "Taggable");
		ANGEL_TO_WILLOW.put("AssessmentTagRelation", "Taggable");
		ANGEL_TO_WILLOW.put("ProductItemTagRelation", "Taggable");
		ANGEL_TO_WILLOW.put("ArticleProductTagRelation", "Taggable");
		ANGEL_TO_WILLOW.put("VoucherProductTagRelation", "Taggable");
		ANGEL_TO_WILLOW.put("MembershipProductTagRelation", "Taggable");

		ANGEL_TO_WILLOW.put("CustomField", "ContactCustomField");

		ANGEL_TO_WILLOW.put("FieldConfiguration", "EnrolmentFieldConfiguration");
		ANGEL_TO_WILLOW.put("Invoice", "AbstractInvoice");

		WILLOW_TO_ANGEL.put("BinaryData", "AttachmentData");
		WILLOW_TO_ANGEL.put("BinaryInfo", "AttachmentInfo");
		WILLOW_TO_ANGEL.put("BinaryInfoRelation", "AttachmentRelation");
		WILLOW_TO_ANGEL.put("TutorRole", "CourseClassTutor");
		WILLOW_TO_ANGEL.put("SessionTutor", "TutorAttendance");
		WILLOW_TO_ANGEL.put("TagGroupRequirement", "TagRequirement");
		WILLOW_TO_ANGEL.put("Taggable", "TagRelation");
		WILLOW_TO_ANGEL.put("TaggableTag", "TagRelation");

		BINARY_RELATION_MAPPING.put("Certificate", "CertificateAttachmentRelation");
		BINARY_RELATION_MAPPING.put("Contact", "ContactAttachmentRelation");
		BINARY_RELATION_MAPPING.put("Course", "CourseAttachmentRelation");
		BINARY_RELATION_MAPPING.put("CourseClass","CourseClassAttachmentRelation");
		BINARY_RELATION_MAPPING.put("Enrolment", "EnrolmentAttachmentRelation");
		BINARY_RELATION_MAPPING.put("Invoice", "InvoiceAttachmentRelation");
		BINARY_RELATION_MAPPING.put("PriorLearning", "PriorLearningAttachmentRelation");
		BINARY_RELATION_MAPPING.put("Room", "RoomAttachmentRelation");
		BINARY_RELATION_MAPPING.put("Session", "SessionAttachmentRelation");
		BINARY_RELATION_MAPPING.put("Site", "SiteAttachmentRelation");
		BINARY_RELATION_MAPPING.put("Student", "StudentAttachmentRelation");
		BINARY_RELATION_MAPPING.put("Tag", "TagAttachmentRelation");
		BINARY_RELATION_MAPPING.put("Tutor", "TutorAttachmentRelation");
		BINARY_RELATION_MAPPING.put("Application", "ApplicationAttachmentRelation");
		BINARY_RELATION_MAPPING.put("Assessment", "AssessmentAttachmentRelation");
		BINARY_RELATION_MAPPING.put("AssessmentSubmission", "AssessmentSubmissionAttachmentRelation");
		BINARY_RELATION_MAPPING.put("Article", "ArticleAttachmentRelation");
		BINARY_RELATION_MAPPING.put("Voucher", "VoucherAttachmentRelation");
		BINARY_RELATION_MAPPING.put("Membership", "MembershipAttachmentRelation");
		BINARY_RELATION_MAPPING.put("ArticleProduct", "ArticleProductAttachmentRelation");
		BINARY_RELATION_MAPPING.put("VoucherProduct", "VoucherProductAttachmentRelation");
		BINARY_RELATION_MAPPING.put("MembershipProduct", "MembershipProductAttachmentRelation");
	}

	/**
	 * default private constructor for utility class
	 */
	private EntityMapping() {}

    /**
	 * translates angel entity name to willow entity name
	 *
	 * @param entityIdentifier to be translated
	 * @return willow entity name
	 */
	public static String getWillowEntityIdentifer(String entityIdentifier) {
		if (Arrays.asList(ReplicatedConfigurationFields.getValues()).contains(entityIdentifier)) {
			return ANGEL_CONFIGURATION_TO_WILLOW;
		}
		return EntityMapping.ANGEL_TO_WILLOW.getOrDefault(entityIdentifier, entityIdentifier);
	}

	/**
	 * translates willow entity name to angel entity name
	 *
	 * @param entityIdentifier to be translated
	 * @return angel entity name
	 */
	public static String getAngelEntityIdentifer(String entityIdentifier) {
		if (Arrays.asList(ReplicatedConfigurationFields.getValues()).contains(entityIdentifier)) {
			return ANGEL_CONFIGURATION_TO_WILLOW;
		}
		return EntityMapping.WILLOW_TO_ANGEL.getOrDefault(entityIdentifier, entityIdentifier);
	}

	public static String getAttachmentRelationIdentifer(String attachable) {
		return EntityMapping.BINARY_RELATION_MAPPING.get(attachable);
	}
}
