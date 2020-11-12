xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

def map = records.groupBy { Invoice i -> i.contact }

xml.data() {
	map.each { Contact c, List<Invoice> invoices ->
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
							if (il.enrolment) {
								enrolment(id: il.enrolment.id) {
									source(il.enrolment.source?.displayName)
									status(il.enrolment.status?.displayName)
									courseClass(code: il.enrolment.courseClass.course.code + "-" + il.enrolment.courseClass.code)
									student(id: il.enrolment.student.id)
								}
							}
							accountCode(il.account?.accountCode)
							accountType(il.account?.type?.displayName)
						}
					}
				}
			}
		}
	}
}
