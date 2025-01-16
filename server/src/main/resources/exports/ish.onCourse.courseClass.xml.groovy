xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

def courseClasses = records.groupBy { CourseClass cc -> cc.course }

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
					cc.discountCourseClasses.each { DiscountCourseClass dcc  ->
						price() {
							def taxRate = cc.tax ? cc.tax.rate : new BigDecimal(0)
							amount(DiscountUtils.getDiscountedFee(dcc, cc.feeIncGst, taxRate).toPlainString())
							taxAmount(DiscountUtils.getDiscountedFee(dcc, cc.feeIncGst, taxRate).multiply(taxRate).toPlainString())
							taxName(cc.tax?.taxCode ?: "unknown")
							discountAmount(DiscountUtils.discountValue(dcc, cc.feeIncGst, taxRate).toPlainString())
							discountName(dcc.discount.name)
						}
					}
					cost {
						incomeMaximum(cc.maximumTotalIncome?.toPlainString())
						incomeBudget(cc.budgetedTotalIncome?.toPlainString())
						incomeActual(cc.actualTotalIncome?.toPlainString())
						expensesMaximum(cc.maximumTotalCost?.toPlainString())
						expensesBudget(cc.budgetedTotalCost?.toPlainString())
						expensesActual(cc.actualTotalCost?.toPlainString())
						profitMaximum(cc.maximumTotalProfit?.toPlainString())
						profitBudget(cc.budgetedTotalProfit?.toPlainString())
						profitActual(cc.actualTotalProfit?.toPlainString())
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

					cc.enrolments.each { Enrolment e ->
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

					cc.sessions.each { Session s ->
						session(id: s.id) {
							modifiedOn(s.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
							createdOn(s.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
							start(s.startDatetime?.format("yyyy-MM-dd'T'HH:mm:ssXXX", s.timeZone))
							end(s.endDatetime?.format("yyyy-MM-dd'T'HH:mm:ssXXX", s.timeZone))
							s.tutors.each { Tutor tr ->
								tutor(id: tr.id) {
									firstName(tr.contact.firstName)
									lastName(tr.contact.lastName)
								}
							}
							if (s.room) {
								room(id: s.room.id) {
									name(s.room.name)
									site(id: s.room.site.id) {
										name(s.room.site.name)
									}
								}
							}

						}

					}

				}
			}
		}
	}
}
