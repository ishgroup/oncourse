xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

def map = records.groupBy { Quote q -> q.contact }

xml.data() {
	map.each { Contact c, List<Quote> quotes ->
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

			quotes.each { Quote q ->
				quote(id: q.id) {
					modifiedOn(q.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
					createdOn(q.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
					amountOwing(q.amountOwing?.toPlainString())
					billToAddress(q.billToAddress)
					dateDue(q.dateDue?.format("yyyy-MM-dd"))
					invoiceDate(q.invoiceDate?.format("yyyy-MM-dd"))
					title(q.title)
					description(q.description)
					privateNotes(q.notes)
					publicNotes(q.publicNotes)
					shippingAddress(q.shippingAddress)
					source(q.source?.displayName)
					total(q.total?.toPlainString())
					totalIncTax(q.totalIncTax?.toPlainString())
					totalTax(q.totalTax?.toPlainString())
					createdByUserName(q.createdByUserName)
					customerReference(q.customerReference)

					q.quoteLines.each { QuoteLine ql ->
						quoteLine(id: ql.id) {
							modifiedOn(ql.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
							createdOn(ql.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
							price() {
								amount(ql.priceTotalIncTax?.toPlainString())
								taxAmount(ql.totalTax?.toPlainString())
								taxName(ql.tax?.taxCode ?: "unknown")
								discountAmount(ql.discountTotalExTax?.toPlainString())
								ql.discounts.each { d ->
									discountName(d.name)
								}
							}
							description(ql.description)
							discountEachExTax(ql.discountEachExTax?.toPlainString())
							quantity(ql.quantity)
							title(ql.title)
							unit(ql.unit)
							prepaidFeesRemaining(ql.prepaidFeesRemaining?.toPlainString())
							if (ql.enrolment) {
								enrolment(id: ql.enrolment.id) {
									source(ql.enrolment.source?.displayName)
									status(ql.enrolment.status?.displayName)
									courseClass(code: ql.enrolment.courseClass.course.code + "-" + ql.enrolment.courseClass.code)
									student(id: ql.enrolment.student.id)
								}
							}
							accountCode(ql.account?.accountCode)
							accountType(ql.account?.type?.displayName)
						}
					}
				}
			}
		}
	}
}
