xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

def invoices = records.collectMany { Contact c -> c.invoices }
def paymentIns = records.collectMany { Contact c -> c.paymentsIn }
def paymentOuts = records.collectMany { Contact c -> c.paymentsOut }
def enrolments = records.findAll { Contact c -> c.student }.collectMany { Contact c -> c.student.enrolments }

def classEnrolments = enrolments.groupBy { Enrolment e -> e.courseClass }
def courseClasses = classEnrolments.keySet().groupBy { CourseClass cc -> cc.course }

xml.data() {
	records.each { Contact c ->
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
			student() {
				if (c.student) {
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

					c.student.enrolments.each { Enrolment e ->
						enrolment(id: e.id) {
							modifiedOn(e.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
							createdOn(e.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
							source(e.source?.displayName)
							status(e.status?.displayName)
							vetClientID(e.vetClientID)
							courseClass(id: e.courseClass.id)
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
			tutor() {
				if (c.tutor) {
					dateStarted(c.tutor.dateStarted?.format("yyyy-MM-dd"))
					dateFinished(c.tutor.dateFinished?.format("yyyy-MM-dd"))
					resume(c.tutor.resume)
					payrollRef(c.tutor.payrollRef)
				}
			}
			c.tags.each { Tag t ->
				tag(t.pathName)
			}
		}
	}

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
			reportableHours(co.reportableHours)
			webDescription(co.webDescription)
			classes.each { cc ->
				courseClass(id: cc.id) {
					modifiedOn(cc.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
					createdOn(cc.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
					code(cc.code)
					deliveryMode(cc.deliveryMode?.displayName)
					endDateTime(cc.endDateTime?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
					price {
						amount(cc.feeIncGst?.toPlainString())
						taxAmount(cc.feeGST?.toPlainString())
						taxName(cc.tax?.taxCode ?: "unknown")
					}
					cc.discountCourseClasses.each { DiscountCourseClass dcc ->
						price {
							def taxRate = cc.tax ? cc.tax.rate : new BigDecimal(0)
							amount(DiscountUtils.getDiscountedFee(dcc, cc.feeIncGst, taxRate).toPlainString())
							taxAmount(DiscountUtils.getDiscountedFee(dcc, cc.feeIncGst, taxRate).multiply(taxRate).toPlainString())
							taxName(cc.tax?.taxCode ?: "unknown")
							discountAmount(DiscountUtils.discountValue(dcc, cc.feeIncGst, taxRate).toPlainString())
							discountName(dcc.discount.name)
						}
					}
					cost {
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
				}
			}
		}
	}

	invoices.each { Invoice i ->
		invoice(id: i.id) {
			modifiedOn(i.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
			createdOn(i.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
			amountOwing(i.amountOwing?.toPlainString())
			billToAddress(i.billToAddress)
			dateDue(i.dateDue?.format("yyyy-MM-dd"))
			description(i.description)
			invoiceDate(i.invoiceDate?.format("yyyy-MM-dd"))
			invoiceNumber(i.invoiceNumber)
			privateNotes(i.notes)
			publicNotes(i.publicNotes)
			shippingAddress(i.shippingAddress)
			source(i.source?.displayName)
			total(i.total?.toPlainString())
			totalIncTax(i.totalIncTax?.toPlainString())
			totalTax(i.totalTax?.toPlainString())
			createdByUserName(i.createdByUserName)
			customerReference(i.customerReference)
			contact(id: i.contact.id)
			i.invoiceLines.each { InvoiceLine il ->
				invoiceLine(id: il.id) {
					modifiedOn(il.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
					createdOn(il.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
					price() {
						amount(il.priceTotalIncTax?.toPlainString())
						taxAmount(il.totalTax?.toPlainString())
						taxName(il.tax?.taxCode ?: "unknown")
						discountAmount(il.discountTotalExTax?.toPlainString())
						il.discounts.each { d ->
							discountName(d.name)
						}
					}
					description(il.description)
					discountEachExTax(il.discountEachExTax?.toPlainString())
					quantity(il.quantity)
					title(il.title)
					unit(il.unit)
					prepaidFeesRemaining(il.prepaidFeesRemaining?.toPlainString())
					accountCode(il.account?.accountCode)
					accountType(il.account?.type?.displayName)
				}
			}
		}
	}

	paymentIns.each { PaymentIn pi ->
		paymentIn(id: pi.id) {
			modifiedOn(pi.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
			createdOn(pi.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
			amount(pi.amount?.toPlainString())
			creditCardExpiry(pi.creditCardExpiry)
			creditCardType(pi.creditCardType?.displayName)
			dateBanked(pi.dateBanked?.format("yyyy-MM-dd"))
			gatewayReference(pi.gatewayReference)
			gatewayResponse(pi.gatewayResponse)
			privateNotes(pi.privateNotes)
			reconciled(pi.reconciled)
			source(pi.source?.displayName)
			status(pi.status?.displayName)
			type(pi.paymentMethod?.name)
			contact(id: pi.payer.id)
		}
	}

	paymentOuts.each { PaymentOut po ->
		paymentOut(id: po.id) {
			modifiedOn(po.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
			createdOn(po.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
			amount(po.amount?.toPlainString())
			creditCardExpiry(po.creditCardExpiry)
			creditCardType(po.creditCardType?.displayName)
			dateBanked(po.dateBanked?.format("yyyy-MM-dd"))
			privateNotes(po.privateNotes)
			reconciled(po.reconciled)
			status(po.status?.displayName)
			type(po.paymentMethod?.name)
			contact(id: po.payee.id)
		}
	}
}
