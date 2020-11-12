xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

def voucherProducts = []

xml.data() {
	records.each { Voucher v ->
		voucher(id: v.id) {
			modifiedOn(v.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
			createdOn(v.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
			code(v.code)
			expiryDate(v.expiryDate?.format("yyyy-MM-dd"))
			redemptionValue(v.redemptionValue?.toPlainString())
			redeemedCourseCount(v.redeemedCourseCount ?: 0)
			voucherProducts << v.voucherProduct
			voucherProduct(id: v.voucherProduct.id)
		}
	}

	voucherProducts.unique().each { VoucherProduct vp ->
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
