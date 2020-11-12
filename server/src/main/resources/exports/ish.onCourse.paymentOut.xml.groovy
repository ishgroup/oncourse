xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

def map = records.groupBy { PaymentOut po -> po.payee }

xml.data() {
	map.each { Contact c, List<PaymentOut> payments ->
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

			payments.each { PaymentOut po ->
				paymentOut(id: po.id) {
					modifiedOn(po.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
					createdOn(po.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
					amount(po.amount?.toPlainString())
					creditCardExpiry(po.creditCardExpiry)
					creditCardType(po.creditCardType?.displayName)
					dateBanked(po.dateBanked?.format("yyyy-MM-dd"))
					privateNotes(po.privateNotes)
					reconciled(po.reconciled)
					status(po.status.displayName)
					type(po.paymentMethod?.name)
				}
			}
		}
	}
}
