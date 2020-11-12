xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

def map = records.groupBy { Payslip p -> p.contact }

xml.data() {
	map.each { Contact c, List<Payslip> payslips ->
		contact(id: c.id) {
			modifiedOn(c.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
			createdOn(c.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
			allowEmail(c.allowEmail)
			allowPost(c.allowPost)
			allowSms(c.allowSms)
			birthDate(c.birthDate?.format("EEE MMM dd hh:mm:ss zzz yyyy"))
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

			payslips.each { Payslip ps ->
				tutorPay(id: ps.id) {
					modifiedOn(ps.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
					createdOn(ps.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
					status(ps.status)

					ps.paylines.each { PayLine pl ->
						payline() {
							modifiedOn(pl.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
							createdOn(pl.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
							budgetedQuantity(pl.budgetedQuantity?.setScale(2, BigDecimal.ROUND_HALF_UP)?.toPlainString())
							budgetedValue(pl.budgetedValue?.toPlainString())
							dateFor(pl.dateFor?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
							description(pl.description)
							quantity(pl.quantity?.setScale(2, BigDecimal.ROUND_HALF_UP)?.toPlainString())
							taxValue(pl.taxValue?.toPlainString())
							value(pl.value?.toPlainString())

							if (pl.classCost) {
								classCost(id: pl.classCost.id) {
									flowType(pl.classCost.flowType.displayName)
									isSunk(pl.classCost.isSunk)
									perUnitAmountExTax(pl.classCost.perUnitAmountExTax?.toPlainString())
									repetitionType(pl.classCost.repetitionType.displayName)
								}
							}

							if (pl.session) {
								session() {
									modifiedOn(pl.session.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
									createdOn(pl.session.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
									start(pl.session.startDatetime?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
									end(pl.session.endDatetime?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
									classCode(pl.session.courseClass.uniqueCode)
								}
							}
						}
					}
					if (c.tutor) {
						tutor() {
							dateStarted(c.tutor.dateStarted)
							dateFinished(c.tutor.dateFinished)
							resume(c.tutor.resume)
							payrollRef(c.tutor.payrollRef)
						}
					}
				}
			}

			c.tags.each { Tag t ->
				tag(t.pathName)
			}
		}
	}
}
