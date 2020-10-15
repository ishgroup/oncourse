import groovy.sql.Sql
import ish.oncourse.server.export.CsvBuilder


def run(args) {

	LIABILITY_LOOK_SQL = "select \n" +
			"concat(c.code, '-', cc.code) as 'Class code',\n" +
			"cc.startDateTime as 'Start date',\n" +
			"i.invoiceNumber as 'Invoice Number', \n" +
			"il.title as 'Invoice line title',\n" +
			"sum(t.amount) as 'Liability outstanding', \n" +
			"a.accountCode as 'Account code',\n" +
			"a.description as 'Account name'\n" +
			"from InvoiceLine il \n" +
			"join Account a on a.id = il.prepaidFeesAccountId\n" +
			"join AccountTransaction t on t.foreignRecordId=il.id and t.tableName='I' and t.accountId=il.prepaidFeesAccountId\n" +
			"join Invoice i on il.invoiceId = i.id\n" +
			"join CourseClass cc on il.courseClassId = cc.id\n" +
			"join Course c on c.id = cc.courseId\n" +
			"where il.courseClassId is not null and  il.prepaidFeesRemaining = 0\n" +
			"group by il.id having sum(t.amount) != 0 "

	Sql sql = new Sql(Email.cayenneService.runtime.dataSource)

	def writer = new StringWriter()
	def csv = new CsvBuilder(writer)


	sql.eachRow(LIABILITY_LOOK_SQL) { row ->
		csv << row.toRowResult()
	}

	email {
		to 'akravchenko@objectstyle.com'
		from 'admin@example.com'
		subject 'Liability outstanding'
		attachment 'liability_outstanding.csv', 'text/csv', writer.toString()
	}
}