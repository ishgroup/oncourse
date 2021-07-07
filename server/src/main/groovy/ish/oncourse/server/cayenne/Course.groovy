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

package ish.oncourse.server.cayenne


import ish.common.types.CourseEnrolmentType
import ish.messaging.ICourse
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.function.CalculateCourseClassNominalHours
import ish.oncourse.function.CalculateCourseReportableHours
import ish.oncourse.server.api.dao.CourseDao
import ish.oncourse.server.api.dao.EntityRelationDao
import ish.oncourse.server.cayenne.glue._Course
import ish.util.CourseUtil
import ish.validation.ValidationFailure
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.validation.ValidationResult
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable

/**
 * Courses are the 'product' students purchase when they enrol. They are enrolled into an instance of a Course, called
 * a CourseClass. The Course carries all the marketing information and is typically what you tag to create a navigation
 * structure on the website.
 *
 * Traineeships are just a special type of course.
 */
@API
@QueueableEntity
class Course extends _Course implements ICourse, Queueable, NotableTrait, ExpandableTrait, CourseTrait, AttachableTrait, Sellable {


	private static final Logger logger = LogManager.getLogger()
	public static final String COURSE_STATUS = 'status'
	public static final String COURSE_DATA_COLLECTION_RULE_ID = 'dataCollectionRuleId'

	@Override
	void postAdd() {
		super.postAdd()
		if (getAllowWaitingLists() == null) {
			setAllowWaitingLists(Boolean.FALSE)
		}
		if (getIsShownOnWeb() == null) {
			setIsShownOnWeb(Boolean.FALSE)
		}
		if (getIsSufficientForQualification() == null) {
			setIsSufficientForQualification(Boolean.FALSE)
		}
		if (getIsVET() == null) {
			setIsVET(Boolean.FALSE)
		}
		if (getCurrentlyOffered() == null) {
			setCurrentlyOffered(true)
		}
		if (getEnrolmentType() == null) {
			setEnrolmentType(CourseEnrolmentType.OPEN_FOR_ENROLMENT)
		}
	}

	@Override
	void prePersist() {
		updateOverriddenFields()
	}

	private void updateOverriddenFields() {
		if (getReportableHours() == null && getModules() != null && getModules().size() > 0) {
			setReportableHours(CalculateCourseReportableHours.valueOf(this).calculate())
		}
		if (getCourseClasses() != null && getCourseClasses().size() > 0) {
			getCourseClasses().findAll { cc -> !cc.reportableHours}.each {cc -> cc.setReportableHours(CalculateCourseClassNominalHours.valueOf(cc).calculate())}
		}
	}

	boolean isAssessable() {
		return getModules() != null && getModules().size() > 0 || getQualification() != null
	}

	@Override
	void addToAttachmentRelations(AttachmentRelation relation) {
		addToAttachmentRelations((CourseAttachmentRelation) relation)
	}

	@Override
	void removeFromAttachmentRelations(AttachmentRelation relation) {
		removeFromAttachmentRelations((CourseAttachmentRelation) relation)
	}

	@Override
	Class<? extends AttachmentRelation> getRelationClass() {
		return CourseAttachmentRelation.class
	}

	@Override
	void validateForSave(@Nonnull ValidationResult validationResult) {
		super.validateForSave(validationResult)

		if (detectDuplicate([ CODE_PROPERTY ] as String[] , null, true) != null) {
			validationResult.addFailure(ValidationFailure.validationFailure(this, CODE_PROPERTY, "You must enter a unique course code."))
		}
	}

	/**
	 * Courses can have waiting lists enabled to allow students to add and remove themselves from a waiting list.
	 *
	 * @return true if waiting lists are enabled for this course
	 */
	@Nonnull
	@API
	@Override
	Boolean getAllowWaitingLists() {
		return super.getAllowWaitingLists()
	}

	/**
	 * This code must be unique across all courses. It cannot contain spaces or hyphens. Because it is used to generate
	 * a URL for the course, it should also be selected for readability, websafe characters and SEO.
	 *
	 * @return course code
	 */
	@Nonnull
	@API
	@Override
	String getCode() {
		return super.getCode()
	}

	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * @return true if the course is currently advertised
	 */
	@Nonnull
	@API
	@Override
	Boolean getCurrentlyOffered() {
		return super.getCurrentlyOffered()
	}

	/**
	 * Courses can have classes which are enrolled into directly, or they can be 'course by application' where students
	 * apply to the course and then (if accepted) are able to enrol into a class.
	 *
	 * @return type of enrolments allowed for this course
	 */
	@Nonnull
	@API
	@Override
	CourseEnrolmentType getEnrolmentType() {
		return super.getEnrolmentType()
	}

	/**
	 * @return the NCVER 'Subject field of education' identifer code
	 */
	@API
	@Override
	String getFieldOfEducation() {
		return super.getFieldOfEducation()
	}

	/**
	 * @return whether this course is displayed on the website for browsing and searching
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsShownOnWeb() {
		return super.getIsShownOnWeb()
	}

	/**
	 * @return true, if students completing this course will be granted a qualification
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsSufficientForQualification() {
		return super.getIsSufficientForQualification()
	}

	/**
	 * @return true if this course is really a traineeship rather than standard course
	 */
	@API
	@Override
	Boolean getIsTraineeship() {
		return super.getIsTraineeship()
	}

	/**
	 * @return whether this is a VET course
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsVET() {
		return super.getIsVET()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * Note that course names don't need to be the VET official name. It is preferable that they are names appropriate
	 * for the best marketing of the course.
	 *
	 * @return the human readable name of the course
	 */
	@Nonnull
	@API
	@Override
	String getName() {
		return super.getName()
	}

	/**
	 * @return a shorter plaint text description suitable for a brochure
	 */
	@API
	@Override
	String getPrintedBrochureDescription() {
		return super.getPrintedBrochureDescription()
	}

	/**
	 * If this is a VET course, it might have reportable hours recorded for AVETMISS reporting
	 *
	 * @return number of reportable hours
	 */
	@API
	@Override
	BigDecimal getReportableHours() {
		return super.getReportableHours()
	}

	/**
	 * The course description is displayed on the course detail page. It can contain rich text for embedding images,
	 * blocks, video, dynamic text, and more. It also supported unicode for multi-language support.
	 *
	 * @return a rich text field for display on the web
	 */
	@API
	@Override
	String getWebDescription() {
		return super.getWebDescription()
	}

	/**
	 * @return application records linked to this course
	 */
	@Nonnull
	@API
	@Override
	List<Application> getApplications() {
		return super.getApplications()
	}

	/**
	 * @return all courseClasses, whether current, past or future
	 */
	@Nonnull
	@API
	@Override
	List<CourseClass> getCourseClasses() {
		return super.getCourseClasses()
	}

	/**
	 * @return CourseModule records linked to this course
	 */
	@Nonnull
	@API
	@Override
	List<CourseModule> getCourseModules() {
		return super.getCourseModules()
	}

	/**
	 * @return all enrolments from all classes
	 */
	@Nonnull
	@API
	@Override
	List<Enrolment> getEnrolments() {
		return super.getEnrolments()
	}

	/**
	 * @return a list of all courses related to this one
	 */
	@Nonnull @API
	List<Course> getRelatedCourses() {
		List<Course> courses = new ArrayList<>()

		EntityRelationDao.getRelatedFrom(context, Course.simpleName, id)
				.findAll { it -> Course.simpleName == it.fromEntityIdentifier }
				.each { it -> courses.add(SelectById.query(Course, it.fromRecordId).selectOne(context))}  as List<Course>

		EntityRelationDao.getRelatedTo(context, Course.simpleName, id)
				.findAll { it -> Course.simpleName == it.toEntityIdentifier }
				.each { it -> courses.add(SelectById.query(Course, it.toRecordId).selectOne(context))}  as List<Course>

		return courses
	}

	/**
	 * A list of all courses related to this one by type with specified name
	 * @return
	 */
	@Nonnull @API
	List<Course> getRelatedCourses(String name) {
		List<Course> courses = new ArrayList<>()

		EntityRelationDao.getRelatedFromByName(context, Course.simpleName, id, name)
				.findAll { it -> Course.simpleName == it.fromEntityIdentifier }
				.each { it -> courses.add(SelectById.query(Course, it.fromRecordId).selectOne(context))} as List<Course>

		EntityRelationDao.getRelatedToByName(context, Course.simpleName, id, name)
				.findAll { it -> Course.simpleName == it.toEntityIdentifier }
				.each { it -> courses.add(SelectById.query(Course, it.toRecordId).selectOne(context))} as List<Course>

		return courses
	}

	/**
	 * @return a list of all products related to this one
	 */
	@Nonnull @API
	List<Product> getRelatedProducts() {
		List<Product> products = new ArrayList<>()

		EntityRelationDao.getRelatedFrom(context, Course.simpleName, id)
				.findAll { it -> Product.simpleName == it.fromEntityIdentifier }
				.each { it -> products.add(SelectById.query(Product, it.fromRecordId).selectOne(context))} as List<Product>

		EntityRelationDao.getRelatedTo(context, Course.simpleName, id)
				.findAll { it -> Product.simpleName == it.toEntityIdentifier }
				.each { it -> products.add(SelectById.query(Product, it.toRecordId).selectOne(context))} as List<Product>

		return products
	}

	/**
	 * @return a list of products related to this one
	 */
	@Nonnull
	@API
	List<Product> getRelatedToProducts() {
		List<Product> products = new ArrayList<>()

		EntityRelationDao.getRelatedTo(context, Course.simpleName, id)
				.findAll { it -> Product.simpleName == it.toEntityIdentifier }
				.each { it -> products.add(SelectById.query(Product, it.toRecordId).selectOne(context))} as List<Product>

		return products
	}

	/**
	 * @return a list of courses this one relate to
	 */
	@Nonnull
	@API
	List<Product> getRelatedFromProducts() {
		List<Product> products = new ArrayList<>()

		EntityRelationDao.getRelatedFrom(context, Course.simpleName, id)
				.findAll { it -> Product.simpleName == it.fromEntityIdentifier }
				.each { it -> products.add(SelectById.query(Product, it.fromRecordId).selectOne(context))} as List<Product>

		return products
	}

	/**
	 * @return a list of courses related to this one
	 */
	@Nonnull
	@API
	List<Course> getRelatedToCourses() {
		List<Course> courses = new ArrayList<>()

		EntityRelationDao.getRelatedTo(context, Course.simpleName, id)
				.findAll { it -> Course.simpleName == it.toEntityIdentifier }
				.each { it -> courses.add(SelectById.query(Course, it.toRecordId).selectOne(context))}  as List<Course>

		return courses
	}

	/**
	 * @return a list of courses related to this one by type with specified name
	 */
	@Nonnull
	@API
	List<Course> getRelatedToCourses(String name) {
		List<Course> courses = new ArrayList<>()

		EntityRelationDao.getRelatedToByName(context, Course.simpleName, id, name)
				.findAll { it -> Course.simpleName == it.toEntityIdentifier }
				.each { it -> courses.add(SelectById.query(Course, it.toRecordId).selectOne(context))} as List<Course>

		return courses
	}

	/**
	 * @return a list of courses this one relate to
	 */
	@Nonnull
	@API
	List<Course> getRelatedFromCourses() {
		List<Course> courses = new ArrayList<>()

		EntityRelationDao.getRelatedFrom(context, Course.simpleName, id)
				.findAll { it -> Course.simpleName == it.fromEntityIdentifier }
				.each { it -> courses.add(SelectById.query(Course, it.fromRecordId).selectOne(context))}  as List<Course>

		return courses
	}

	/**
	 * @return a list of courses this one relate to by type with specified name
	 */
	@Nonnull
	@API
	List<Course> getRelatedFromCourses(String name) {
		List<Course> courses = new ArrayList<>()

		EntityRelationDao.getRelatedFromByName(context, Course.simpleName, id, name)
				.findAll { it -> Course.simpleName == it.fromEntityIdentifier }
				.each { it -> courses.add(SelectById.query(Course, it.fromRecordId).selectOne(context))}  as List<Course>

		return courses
	}

	/**
	 * @return modules linked to this course
	 */
	@Nonnull
	@API
	@Override
	List<Module> getModules() {
		return super.getModules()
	}

	/**
	 * @return qualification linked to this course
	 */
	@Nullable
	@API
	@Override
	Qualification getQualification() {
		return super.getQualification()
	}

	/**
	 * @return list of sessions linked to this course
	 */
	@Nonnull
	@API
	@Override
	List<Session> getSessions() {
		return super.getSessions()
	}

	/**
	 * @return list of sutable Vouchers for this course
	 */
	@Nonnull
	@API
	List<VoucherProduct> getVoucherProduct() {
		return voucherProductCourses*.voucherProduct
	}

	/**
	 * @return all the waiting list entries for this course
	 */
	@Nonnull
	@API
	@Override
	List<WaitingList> getWaitingLists() {
		return super.getWaitingLists()
	}

	/**
	 * @return The list of tags assigned to course
	 */
	@Nonnull
	@API
	List<Tag> getTags() {
		List<Tag> tagList = new ArrayList<>(getTaggingRelations().size())
		for (CourseTagRelation relation : getTaggingRelations()) {
			tagList.add(relation.getTag())
		}
		return tagList
	}

	@Override
	void addToModules(Module module) {
		CourseUtil.addModule(this, module, CourseModule.class)
	}

	/**
	 * Data collection rules allow a course to have a defined {@link FieldConfigurationScheme} which
	 * is used to define the data collection form displayed to users at time of enrolment, waiting list
	 * or application.
	 *
	 * @return the scheme which applies to this course
	 */
	@Nonnull @Override @API
	FieldConfigurationScheme getFieldConfigurationSchema() {
		return super.getFieldConfigurationSchema()
	}

	@Override
	Class<? extends CustomField> getCustomFieldClass() {
		return CourseCustomField
	}
}
