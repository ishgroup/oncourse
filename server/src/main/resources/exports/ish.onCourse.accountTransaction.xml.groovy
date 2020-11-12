xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

xml.data() {
	records.each { AccountTransaction t ->
		accountTransaction(id: t.id) {
			amount(t.amount?.toPlainString())
			transactionDate(t.transactionDate?.format("yyyy-MM-dd"))
			sourse(t.source)
			invoiceNumber(t.invoiceNumber)
			paymentType(t.paymentType)
			contactName(t.contactName)
			transactionDescription(t.transactionDescription)
			accountCode(t.account?.accountCode)
			accountCode(t.account?.type?.displayName)
		}
	}
}
