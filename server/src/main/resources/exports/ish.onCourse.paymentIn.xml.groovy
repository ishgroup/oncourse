xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

def map = records.groupBy { PaymentIn pi -> pi.payer }

xml.data() {
	map.each { Contact c, List<PaymentIn> payments ->
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

			payments.each { PaymentIn pi ->
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
					status(pi.status.displayName)
					type(pi.paymentMethod?.name)
				}
			}
		}
	}
}
