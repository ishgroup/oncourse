xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

xml.data() {
	records.each { Banking bnk ->
		banking(id: bnk.id) {
			reconciled(bnk.reconciledStatus)
			date(bnk.settlementDate?.format("d-M-y HH:mm:ss"))
			type(bnk.type)
			site(bnk.adminSite?.name)
			user(bnk.createdBy?.email)
			total(bnk.total?.toPlainString())
		}
	}
}
