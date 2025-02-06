records.each { AccountTransaction t ->
	csv << [
			"Date"          : t.transactionDate?.format("d/M/Y"),
			"Memo"          : t.transactionDescription,
			"Account Number": t.account.accountCode,
			"Debit Amount"  : t.amount.compareTo(Money.ZERO()) > 0 ? t.amount.toPlainString() : Money.ZERO().toPlainString(),
			"Credit Amount" : t.amount.compareTo(Money.ZERO()) > 0 ? Money.ZERO().toPlainString() : t.amount.toPlainString()
	]
}
