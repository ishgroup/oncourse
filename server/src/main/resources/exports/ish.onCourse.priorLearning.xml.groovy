import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.cayenne.PriorLearning

xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

xml.data() {
	records.each { PriorLearning p ->
		priorLearning(id: p.id) {
			createdOn(p.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
			modifiedOn(p.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
			title(p.title)
			notes(p.notes)
			externalRef(p.externalRef)
			outcomeIdTrainingOrg(p.outcomeIdTrainingOrg)
			student(id: p.student?.id) {
				disabilityType(p.student.disabilityType?.displayName)
				englishProficiency(p.student.englishProficiency?.displayName)
				highestSchoolLevel(p.student.highestSchoolLevel?.displayName)
				indigenousStatus(p.student.indigenousStatus?.displayName)
				isOverseasClient(p.student?.isOverseasClient)
				isStillAtSchool(p.student?.isStillAtSchool)
				labourForceStatus(p.student?.labourForceStatus?.displayName)
				priorEducationCode(p.student?.priorEducationCode?.displayName)
				studentNumber(p.student?.studentNumber)
				yearSchoolCompleted(p.student?.yearSchoolCompleted)
			}
			qualification(id: p.qualification?.id) {
				title(p.qualification?.title)
				nationalCode(p.qualification?.nationalCode)
				modifiedOn(p.qualification?.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
				createdOn(p.qualification?.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
				isAccreditedCourse(p.qualification?.type?.displayName)
				level(p.qualification?.level)
				nominalHours(p.qualification?.nominalHours?.format("0.00"))
				reviewDate(p.qualification?.reviewDate?.format("yyyy-MM-dd"))
				anzsco(p.qualification?.anzsco)
				fieldOfEducation(p.qualification?.fieldOfEducation)
				isOffered(p.qualification?.isOffered)
				newApprenticeship(p.qualification?.newApprenticeship)
			}
			p.outcomes.each { Outcome o ->
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
