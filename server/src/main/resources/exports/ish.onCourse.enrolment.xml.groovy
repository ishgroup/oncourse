xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

def classEnrolments = records.groupBy { Enrolment e -> e.courseClass }
def courseClasses = classEnrolments.keySet().groupBy { CourseClass cc -> cc.course }
def contacts = records.collect { Enrolment e -> e.student.contact }


xml.data() {
	courseClasses.each { Course co, List<CourseClass> classes ->
		course(id: co.id) {
			modifiedOn(co.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
			createdOn(co.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
			allowWaitingLists(co.allowWaitingLists)
			code(co.code)
			currentlyOffered(co.currentlyOffered)
			fieldOfEducation(co.fieldOfEducation)
			isWebVisible(co.isShownOnWeb)
			isSufficientForQualification(co.isSufficientForQualification)
			isVET(co.isVET)
			name(co.name)
			printedBrochureDescription(co.printedBrochureDescription)
			reportableHours(co.reportableHours?.format("0.00"))
			webDescription(co.webDescription)
			classes.each { cc ->
				courseClass(id: cc.id) {
					modifiedOn(cc.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
					createdOn(cc.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
					code(cc.code)
					deliveryMode(cc.deliveryMode?.displayName)
					endDateTime(cc.endDateTime?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
					price() {
						amount(cc.feeIncGst?.toPlainString())
						taxAmount(cc.feeGST?.toPlainString())
						taxName(cc.tax?.taxCode ?: "unknown")
					}
					cc.discountCourseClasses.each { DiscountCourseClass dcc ->
						price() {
							def taxRate = cc.tax ? cc.tax.rate : new BigDecimal(0)
							amount(DiscountUtils.getDiscountedFee(dcc, cc.feeIncGst, taxRate).toPlainString())
							taxAmount(DiscountUtils.getDiscountedFee(dcc, cc.feeIncGst, taxRate).multiply(taxRate).toPlainString())
							taxName(cc.tax?.taxCode ?: "unknown")
							discountAmount(DiscountUtils.discountValue(dcc, cc.feeIncGst, taxRate).toPlainString())
							discountName(dcc.discount.name)
						}
					}
					cost() {
						incomeMaximum(ish.budget.ClassBudgetUtil.getClassIncomeExTax(cc, ish.budget.ClassBudgetUtil.MAXIMUM)?.toPlainString())
						incomeBudget(ish.budget.ClassBudgetUtil.getClassIncomeExTax(cc, ish.budget.ClassBudgetUtil.BUDGETED)?.toPlainString())
						incomeActual(ish.budget.ClassBudgetUtil.getClassIncomeExTax(cc, ish.budget.ClassBudgetUtil.ACTUAL)?.toPlainString())
						expensesMaximum(ish.budget.ClassBudgetUtil.getClassCostsExTax(cc, ish.budget.ClassBudgetUtil.MAXIMUM)?.toPlainString())
						expensesBudget(ish.budget.ClassBudgetUtil.getClassCostsExTax(cc, ish.budget.ClassBudgetUtil.BUDGETED)?.toPlainString())
						expensesActual(ish.budget.ClassBudgetUtil.getClassCostsExTax(cc, ish.budget.ClassBudgetUtil.ACTUAL)?.toPlainString())
						profitMaximum(ish.budget.ClassBudgetUtil.getClassProfitExTax(cc, ish.budget.ClassBudgetUtil.MAXIMUM)?.toPlainString())
						profitBudget(ish.budget.ClassBudgetUtil.getClassProfitExTax(cc, ish.budget.ClassBudgetUtil.BUDGETED)?.toPlainString())
						profitActual(ish.budget.ClassBudgetUtil.getClassProfitExTax(cc, ish.budget.ClassBudgetUtil.ACTUAL)?.toPlainString())
					}
					fundingSource(cc.fundingSource?.displayName)
					isCancelled(cc.isCancelled)
					type(cc.type)
					isWebVisible(cc.isShownOnWeb)
					maximumPlaces(cc.maximumPlaces)
					message(cc.message)
					minimumPlaces(cc.minimumPlaces)
					budgetedPlaces(cc.budgetedPlaces)
					notes(cc.notes)
					startDateTime(cc.startDateTime?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
					webDescription(cc.webDescription)
					accountCode(cc.incomeAccount?.accountCode)

					classEnrolments.get(cc).each { Enrolment e ->
						enrolment(id: e.id) {
							modifiedOn(e.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
							createdOn(e.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
							source(e.source?.displayName)
							status(e.status?.displayName)
							vetClientID(e.vetClientID)
							contact(id: e.student.contact.id)
							e.tags.each { Tag t ->
								tag(t.pathName)
							}
							e.outcomes.each { Outcome o ->
								if (o.module) {
									outcome(id: o.id) {
										modifiedOn(o.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
										createdOn(o.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
										startDate(o.startDate?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
										endDate(o.endDate?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
										status(o.status?.displayName)
										module(nationalCode: o.module?.nationalCode)
									}
								}
							}
						}
					}
				}
			}
		}
	}

	contacts.unique().each { Contact c ->
		contact(id: c.id) {
			modifiedOn(c.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
			createdOn(c.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
			allowEmail(c.allowEmail)
			allowPost(c.allowPost)
			allowSms(c.allowSms)
			deliveryStatusEmail(c.deliveryStatusEmail)
			deliveryStatusPost(c.deliveryStatusPost)
			deliveryStatusSms(c.deliveryStatusSms)
			email(c.email)
			fax(c.fax)
			firstName(c.firstName)
			homePhone(c.homePhone)
			company(c.isCompany)
			male(c.isMale)
			lastName(c.lastName)
			message(c.message)
			mobilePhone(c.mobilePhone)
			notes(c.notes)
			postcode(c.postcode)
			state(c.state)
			street(c.street)
			suburb(c.suburb)
			tfn(c.tfn)
			uniqueCode(c.uniqueCode)
			workPhone(c.workPhone)
			title(c.title)
			if (c.student) {
				student() {
					disabilityType(c.student.disabilityType?.displayName)
					englishProficiency(c.student.englishProficiency?.displayName)
					highestSchoolLevel(c.student.highestSchoolLevel?.displayName)
					indigenousStatus(c.student.indigenousStatus?.displayName)
					isOverseasClient(c.student.isOverseasClient)
					isStillAtSchool(c.student.isStillAtSchool)
					labourForceStatus(c.student.labourForceStatus?.displayName)
					priorEducationCode(c.student.priorEducationCode?.displayName)
					studentNumber(c.student.studentNumber)
					yearSchoolCompleted(c.student.yearSchoolCompleted)
				}
			}
		}
	}
}
