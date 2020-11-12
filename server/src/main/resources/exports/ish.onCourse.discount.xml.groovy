xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

xml.data() {
	records.each { Discount d ->
		discount(id: d.id) {
			modifiedOn(d.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
			createdOn(d.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
			code(d.code)
			if (DiscountType.PERCENT.equals(d.discountType)) {
				discountPercent(d.discountPercent)
				discountMax(d.discountMax?.toPlainString())
				discountMin(d.discountMin?.toPlainString())
			} else {
				discountDollar(d.discountDollar?.toPlainString())
			}
			rounding(d.rounding?.displayName)
			name(d.name)
			publicDescription(d.publicDescription)
			validFrom(d.validFrom?.format("yyyy-MM-dd"))
			validTo(d.validTo?.format("yyyy-MM-dd"))
		}
	}
}
