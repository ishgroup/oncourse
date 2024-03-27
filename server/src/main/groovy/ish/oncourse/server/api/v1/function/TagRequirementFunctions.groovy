/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.function

import ish.common.types.NodeSpecialType
import ish.oncourse.cayenne.TaggableClasses
import ish.oncourse.server.api.BidiMap
import ish.oncourse.server.api.v1.model.TagRequirementDTO
import ish.oncourse.server.api.v1.model.TagRequirementTypeDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.AbstractInvoice
import ish.oncourse.server.cayenne.Application
import ish.oncourse.server.cayenne.Article
import ish.oncourse.server.cayenne.ArticleProduct
import ish.oncourse.server.cayenne.Assessment
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Document
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.Lead
import ish.oncourse.server.cayenne.Membership
import ish.oncourse.server.cayenne.MembershipProduct
import ish.oncourse.server.cayenne.Payslip
import ish.oncourse.server.cayenne.ProductItem
import ish.oncourse.server.cayenne.Quote
import ish.oncourse.server.cayenne.Room
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.cayenne.Tag
import ish.oncourse.server.cayenne.TagRequirement
import ish.oncourse.server.cayenne.Tutor
import ish.oncourse.server.cayenne.Voucher
import ish.oncourse.server.cayenne.VoucherProduct
import ish.oncourse.server.cayenne.WaitingList
import org.apache.commons.lang3.StringUtils

class TagRequirementFunctions {

    public static final BidiMap<TaggableClasses, TagRequirementTypeDTO> tagRequirementBidiMap = new BidiMap<TaggableClasses, TagRequirementTypeDTO>() {
        {
            put(TaggableClasses.APPLICATION, TagRequirementTypeDTO.APPLICATION)
            put(TaggableClasses.ASSESSMENT, TagRequirementTypeDTO.ASSESSMENT)
            put(TaggableClasses.CONTACT, TagRequirementTypeDTO.CONTACT)
            put(TaggableClasses.COURSE, TagRequirementTypeDTO.COURSE)
            put(TaggableClasses.DOCUMENT, TagRequirementTypeDTO.DOCUMENT)
            put(TaggableClasses.ENROLMENT, TagRequirementTypeDTO.ENROLMENT)
            put(TaggableClasses.INVOICE, TagRequirementTypeDTO.INVOICE)
            put(TaggableClasses.LEAD, TagRequirementTypeDTO.LEAD)
            put(TaggableClasses.PAYSLIP, TagRequirementTypeDTO.PAYSLIP)
            put(TaggableClasses.ROOM, TagRequirementTypeDTO.ROOM)
            put(TaggableClasses.SITE, TagRequirementTypeDTO.SITE)
            put(TaggableClasses.STUDENT, TagRequirementTypeDTO.STUDENT)
            put(TaggableClasses.TUTOR, TagRequirementTypeDTO.TUTOR)
            put(TaggableClasses.WAITING_LIST, TagRequirementTypeDTO.WAITINGLIST)
            put(TaggableClasses.COURSE_CLASS, TagRequirementTypeDTO.COURSECLASS)
            put(TaggableClasses.ARTICLE, TagRequirementTypeDTO.ARTICLE)
            put(TaggableClasses.VOUCHER, TagRequirementTypeDTO.VOUCHER)
            put(TaggableClasses.MEMBERSHIP, TagRequirementTypeDTO.MEMBERSHIP)
            put(TaggableClasses.ARTICLE_PRODUCT, TagRequirementTypeDTO.ARTICLEPRODUCT)
            put(TaggableClasses.VOUCHER_PRODUCT, TagRequirementTypeDTO.VOUCHERPRODUCT)
            put(TaggableClasses.MEMBERSHIP_PRODUCT, TagRequirementTypeDTO.MEMBERSHIPPRODUCT)
        }
    }



    private static final BidiMap<String, TaggableClasses> taggableClassesBidiMap = new BidiMap<String, TaggableClasses>() {
        {
            put(Application.simpleName, TaggableClasses.APPLICATION)
            put(Assessment.simpleName, TaggableClasses.ASSESSMENT)
            put(Contact.simpleName, TaggableClasses.CONTACT)
            put(Course.simpleName, TaggableClasses.COURSE)
            put(Document.simpleName, TaggableClasses.DOCUMENT)
            put(Enrolment.simpleName, TaggableClasses.ENROLMENT)
            put(AbstractInvoice.simpleName, TaggableClasses.INVOICE)
            put(Invoice.simpleName, TaggableClasses.INVOICE)
            put(Quote.simpleName, TaggableClasses.INVOICE)
            put(Lead.simpleName, TaggableClasses.LEAD)
            put(Payslip.simpleName, TaggableClasses.PAYSLIP)
            put(Room.simpleName, TaggableClasses.ROOM)
            put(Site.simpleName, TaggableClasses.SITE)
            put(Student.simpleName, TaggableClasses.STUDENT)
            put(Tutor.simpleName, TaggableClasses.TUTOR)
            put(WaitingList.simpleName, TaggableClasses.WAITING_LIST)
            put(CourseClass.simpleName, TaggableClasses.COURSE_CLASS)
            put(ProductItem.simpleName, TaggableClasses.PRODUCT_ITEM)
            put(Article.simpleName, TaggableClasses.PRODUCT_ITEM)
            put(Voucher.simpleName, TaggableClasses.PRODUCT_ITEM)
            put(Membership.simpleName, TaggableClasses.PRODUCT_ITEM)
            put(ArticleProduct.simpleName, TaggableClasses.ARTICLE_PRODUCT)
            put(VoucherProduct.simpleName, TaggableClasses.VOUCHER_PRODUCT)
            put(MembershipProduct.simpleName, TaggableClasses.MEMBERSHIP_PRODUCT)
        }
    }


    private static final BidiMap<String, TaggableClasses> taggableClassesForRequirements = new BidiMap<String, TaggableClasses>() {
        {
            put(Article.simpleName, TaggableClasses.ARTICLE)
            put(Voucher.simpleName, TaggableClasses.VOUCHER)
            put(Membership.simpleName, TaggableClasses.MEMBERSHIP)
        }
    }


    static TaggableClasses getTaggableClassForName(String entityName) {
        taggableClassesBidiMap.get(entityName)
    }


    static TaggableClasses getRequirementTaggableClassForName(String entityName) {
        return taggableClassesForRequirements.containsKey(entityName)
                ? taggableClassesForRequirements.get(entityName)
                : getTaggableClassForName(entityName);
    }


    static TagRequirementDTO toRestRequirement(TagRequirement req, Tag dbTag){
        new TagRequirementDTO().with { tagRequirement ->
            tagRequirement.id = req.id
            tagRequirement.type = tagRequirementBidiMap.get(req.entityIdentifier)
            tagRequirement.mandatory = req.isRequired
            tagRequirement.limitToOneTag = !req.manyTermsAllowed
            tagRequirement.displayRule = req.displayRule
            tagRequirement.system = dbTag.specialType != null && (
                    (dbTag.specialType == NodeSpecialType.SUBJECTS && tagRequirement.type == TagRequirementTypeDTO.COURSE) ||
                            (dbTag.specialType == NodeSpecialType.ASSESSMENT_METHOD && tagRequirement.type == TagRequirementTypeDTO.ASSESSMENT) ||
                            (dbTag.specialType == NodeSpecialType.PAYROLL_WAGE_INTERVALS && tagRequirement.type == TagRequirementTypeDTO.TUTOR) ||
                            (dbTag.specialType == NodeSpecialType.TERMS && tagRequirement.type == TagRequirementTypeDTO.COURSECLASS) ||
                            (dbTag.specialType == NodeSpecialType.CLASS_EXTENDED_TYPES && tagRequirement.type == TagRequirementTypeDTO.COURSECLASS) ||
                            (dbTag.specialType == NodeSpecialType.COURSE_EXTENDED_TYPES && tagRequirement.type == TagRequirementTypeDTO.COURSE)
            )

            tagRequirement
        }
    }

    static ValidationErrorDTO validateTagRequirementsForSave(Tag dbTag, List<TaggableClasses> deletedEntityList) {

        TaggableClasses requiredEntity = null

        switch (dbTag.specialType) {
            case NodeSpecialType.SUBJECTS:
                requiredEntity = deletedEntityList.find { it == TaggableClasses.COURSE }
                break
            case NodeSpecialType.ASSESSMENT_METHOD:
                requiredEntity = deletedEntityList.find { it == TaggableClasses.ASSESSMENT }
                break
            case NodeSpecialType.PAYROLL_WAGE_INTERVALS:
                requiredEntity = deletedEntityList.find { it == TaggableClasses.TUTOR }
                break
            case NodeSpecialType.TERMS:
                requiredEntity = deletedEntityList.find { it == TaggableClasses.COURSE_CLASS }
                break
            case NodeSpecialType.CLASS_EXTENDED_TYPES:
                requiredEntity = deletedEntityList.find { it == TaggableClasses.COURSE_CLASS }
                break
            case NodeSpecialType.COURSE_EXTENDED_TYPES:
                requiredEntity = deletedEntityList.find { it == TaggableClasses.COURSE }
                break
        }

        return requiredEntity == null ? null :
                new ValidationErrorDTO(null, 'requirements',
                        "The ${StringUtils.capitalize(requiredEntity.name().toLowerCase())} entity is mandatory for the Tag Group.".toString()
                )
    }
}
