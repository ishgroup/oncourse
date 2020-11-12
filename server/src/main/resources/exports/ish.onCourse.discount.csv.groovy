records.each { Discount d ->
	csv << [
			"name"                     : d.name,
			"code"                     : d.code,
			"modifiedOn"               : d.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
			"createdOn"                : d.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
			"discountPercent"          : d.discountPercent,
			"discountMax"              : d.discountMax?.toPlainString(),
			"discountMin"              : d.discountMin?.toPlainString(),
			"discountDollar"           : d.discountDollar?.toPlainString(),
			"rounding"                 : d.rounding?.displayName,
			"publicDescription"        : d.publicDescription,
			"validFrom"                : d.validFrom?.format("yyyy-MM-dd"),
			"validTo"                  : d.validTo?.format("yyyy-MM-dd"),
			"studentAge"               : d.studentAge,
			"studentConcessionObsolete": d.studentConcessionObsolete,
			"studentEnrolledWithinDays": d.studentEnrolledWithinDays,
			"studentPostcode"          : d.studentPostcode
	]
}
