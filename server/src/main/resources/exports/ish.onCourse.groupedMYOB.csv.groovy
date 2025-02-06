csv.delimiter = '\t'

records.sort { at -> at.account.accountCode }.sort { at -> at.transactionDate }.groupBy { at -> at.transactionDate?.format("d/M/y") }.each { atGroupByDate ->

    atGroupByDate.each { group ->

        group.value.groupBy { at -> at.account.accountCode }.each { finalGroup ->

            def amount = finalGroup.value*.amount.inject { v1, v2 -> v1.add(v2) }

            if (amount.compareTo(Money.ZERO()) != 0) {
                csv << [
                        "Date"          : atGroupByDate.key,
                        "Account Number": finalGroup.key,
                        "Debit Amount"  : finalGroup.value[0]?.account?.debit ? amount.toPlainString() : Money.ZERO().toPlainString(),
                        "Credit Amount" : finalGroup.value[0]?.account?.credit ? amount.toPlainString() : Money.ZERO().toPlainString()
                ]
            }
        }
    }
}
