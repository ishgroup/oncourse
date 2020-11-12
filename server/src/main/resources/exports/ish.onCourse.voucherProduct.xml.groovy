xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

xml.data() {
	records.each { VoucherProduct vp ->
		voucherProduct(id: vp.id) {
			modifiedOn(vp.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
			createdOn(vp.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
			sku(vp.sku)
			name(vp.name)
			priceExTax(vp.priceExTax?.toPlainString())
			value(vp.value?.toPlainString())
			maxCoursesRedemption(vp.maxCoursesRedemption)
			expiryDays(vp.expiryDays)
		}
	}
}
