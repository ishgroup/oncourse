xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

def contactCertificates = records.groupBy { Certificate cert -> cert.student.contact }

xml.data() {
	contactCertificates.each { Contact c, List<Certificate> certs ->
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
			certs.each { Certificate cert ->
				certificate(id: cert.id) {
					modifiedOn(cert.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
					createdOn(cert.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
					fullQualification(cert.isQualification)
					printed(cert.printedOn?.format("yyyy-MM-dd"))
					firstName(cert.student?.contact?.firstName)
					lastName(cert.student?.contact?.lastName)
					nationalCode(cert.qualification?.nationalCode)
					cert.certificateOutcomes.each { CertificateOutcome co ->
						if (co.outcome.module) {
							module(nationalCode: co.outcome.module.nationalCode)
						}
					}
				}
			}
		}
	}
}
