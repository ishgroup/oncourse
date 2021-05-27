/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.plugin.xero

import groovy.json.JsonSlurper
import groovy.transform.CompileDynamic
import groovy.xml.MarkupBuilder

import java.util.concurrent.ExecutionException

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.POST
import groovyx.net.http.RESTClient
import ish.common.types.DataType
import ish.common.types.Gender
import ish.common.types.PayslipStatus
import ish.math.Money

import static ish.oncourse.commercial.plugin.xero.AuthentificationContext.XERO_ACCESS_TOKEN
import static ish.oncourse.commercial.plugin.xero.AuthentificationContext.XERO_CLIENT_ID
import static ish.oncourse.commercial.plugin.xero.AuthentificationContext.XERO_CLIENT_SECRET
import static ish.oncourse.commercial.plugin.xero.AuthentificationContext.XERO_REFRESH_TOKEN
import static ish.oncourse.server.api.v1.service.impl.IntegrationApiImpl.VERIFICATION_CODE
import ish.oncourse.server.cayenne.AccountTransaction
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.ContactCustomField
import ish.oncourse.server.cayenne.CustomFieldType
import ish.oncourse.server.cayenne.IntegrationConfiguration
import ish.oncourse.server.cayenne.IntegrationProperty
import ish.oncourse.server.cayenne.Payslip
import ish.oncourse.server.integration.GetProps
import ish.oncourse.server.integration.OnSave
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.time.LocalDate
import java.time.YearMonth

@CompileDynamic
@Plugin(type = 5)
class XeroIntegration implements PluginTrait {

	static final String EMAIL_SUBJECT = "onCourse->Xero payroll export failed for %s"
	static final String EMAIL_CONTENT_TEMPLATE = """The payslip for tutor %s created on %s failed to upload to Xero with the following error:

%s


The payslip status has been set to Complete. To process this pay again, fix the problem and set the payslip to Approved."""

	static final String MESSAGE_DOB_REQUIRED = "Date of birth is required. Set this in onCourse for the tutor."
	static final String MESSAGE_ADDRESS_REQUIRED = "Postal address (suburb, state, postcode) is required. Set this in onCourse for the tutor."
	static final String MESSAGE_CONFIGURE_XERO_EMPLOYEE = """A new employee was just created in Xero. Log into to Xero and complete the configuration for this employee.
1. Employment -> Payroll calendar
2. Employment -> Start Date
3. Pay template -> Add an ordinary Earnings Rate
4. Pay template -> Add superannuation details
5. Taxes -> Review all the settings and submit a declaration to the ATO

Review all the other employee settings and ensure they are correct.
"""

	static final String MESSAGE_CONFIGURE_EARNING = "Add an 'Ordinary Earnings Rate' of type 'fixed' to this employee in Xero."
	static final String MESSAGE_CONFIGURE_CALENDAR = "Add a 'payroll calendar' to this employee in Xero."
	static final String MESSAGE_PAYSLIP_MISSING =  "The draft pay run in Xero has this employee disabled. Log into Xero, go to 'Pay Employee' menu, choose the payrun and ensure there is a green tick next to the employee."
	
	private static final String XERO_ACTIVE = "active"
	private static final String XERO_ORGANISATION = "companyName"

	private static final String EMPLOYEE_FIELD_KEY = "xeroEmployeeId"
	private static final String EMPLOYEE_FIELD_NAME = "Xero employee identifier"

	static final XERO_API_BASE = "https://api.xero.com"

	private static final String DATE_FORMAT = "yyyy-MM-dd"
	private static final String today = LocalDate.now().format(DATE_FORMAT)

	private CustomFieldType employeeField
	private String accessToken
	private String tenantId

	public ObjectContext objectContext
	public Contact contact
	public Payslip payslip
	protected String bounceAddress

	private static Logger logger = LogManager.logger

	@OnSave
	static void onSave(IntegrationConfiguration configuration, Map<String, String> props) {

		if (props[XERO_ACTIVE] != null && props[XERO_ACTIVE] == 'false' && configuration.getIntegrationProperty(XERO_ACTIVE).value == 'true') {
			AuthentificationContext authContext = new AuthentificationContext(configuration)
			authContext.init()
			authContext.disconnect()
			configuration.setProperty(XERO_ACTIVE, 'false')
		} else {
			
			new RESTClient('https://identity.xero.com').request(POST, URLENC) {
				uri.path = '/connect/token'
				headers.'Authorization' = "Basic ${"$XERO_CLIENT_ID:$XERO_CLIENT_SECRET".bytes.encodeBase64().toString()}"
				body = "grant_type=authorization_code&code=${props[(VERIFICATION_CODE)]}&redirect_uri=https://secure-payment.oncourse.net.au/services/s/integrations/xero/auth.html"

				response.success = { resp, body ->
					Map<String, String> jsonResponce = new JsonSlurper().parseText(body.keySet()[0])
					configuration.setProperty(XERO_ACCESS_TOKEN, jsonResponce['access_token'])
					configuration.setProperty(XERO_REFRESH_TOKEN,jsonResponce['refresh_token'])
				}

				response.failure = { resp, body ->
					logger.error(body.toString())
					throw new RuntimeException("Xero app authentication error: $body.toString()")
				}
			}

			AuthentificationContext authContext = new AuthentificationContext(configuration)
			authContext.init()

			configuration.setProperty(XERO_ACTIVE, 'true')
			configuration.setProperty(XERO_ORGANISATION, getOrganisationName(authContext))
			
		}

	}

	@GetProps
	static List<IntegrationProperty> getProps(IntegrationConfiguration configuration) {
		List<IntegrationProperty> result = []
		
		IntegrationProperty active = configuration.getIntegrationProperty(XERO_ACTIVE)  
		if (!active) {
			active = configuration.context.newObject(IntegrationProperty)
			active.integrationConfiguration = configuration
			active.keyCode = XERO_ACTIVE
			active.value = 'true'
			configuration.context.commitChanges()
		}
		
		IntegrationProperty organisation = configuration.getIntegrationProperty(XERO_ORGANISATION)
		if (organisation) {
			result << organisation
		} else if (active.value == 'true') {
			try {
				AuthentificationContext authContext = new AuthentificationContext(configuration)
				authContext.init()
				organisation = configuration.context.newObject(IntegrationProperty)
				organisation.integrationConfiguration = configuration
				organisation.keyCode = XERO_ORGANISATION
				organisation.value = getOrganisationName(authContext)
				
				configuration.context.commitChanges()

				result << organisation
			} catch (Exception e) {
				active.value = 'false'
				logger.catching(e)
			}
		}
		result << active

		return result	
	}
	
	static String getOrganisationName(AuthentificationContext authContext) {
		

		String name = null
		new RESTClient(XERO_API_BASE).request(GET, JSON) {
			uri.path = '/api.xro/2.0/Organisations'
			headers.'Authorization' = "Bearer ${authContext.newAccessToken}"
			headers.'Xero-tenant-id' = authContext.tenantId
			response.success = { resp, body ->
				name =  body['Organisations'][0]['Name']
			}
			response.failure = { resp, body ->
				logger.error(body.toString())
				throw new RuntimeException("Xero app authentication error: $body.toString()")
			}
		}
		
		return name
		
	}

	XeroIntegration(Map args) {
		loadConfig(args)
		objectContext = cayenneService.newContext
		loadCustomField()
		refreshXeroAccessToken()
	}

	private loadCustomField() {
		employeeField = ObjectSelect.query(CustomFieldType).where(CustomFieldType.KEY.eq(EMPLOYEE_FIELD_KEY)).selectOne(objectContext)
		if (!employeeField) {
			employeeField = objectContext.newObject(CustomFieldType)
			employeeField.dataType = DataType.TEXT
			employeeField.entityIdentifier = Contact.simpleName
			employeeField.key = EMPLOYEE_FIELD_KEY
			employeeField.name = EMPLOYEE_FIELD_NAME
			employeeField.isMandatory = false
			employeeField.sortOrder = 1000l
			objectContext.commitChanges()
		}
	}

	void refreshXeroAccessToken()  {
		String oldRefreshToken = this.configuration.getIntegrationProperty(XERO_REFRESH_TOKEN).value

		AuthentificationContext authContext = new AuthentificationContext(oldRefreshToken)
		authContext.init()
		
		//save actual refresh token
		this.configuration = this.objectContext.localObject(this.configuration)
		this.configuration.setProperty(XERO_ACCESS_TOKEN, authContext.newAccessToken)
		this.configuration.setProperty(XERO_REFRESH_TOKEN, authContext.newRefreshToken)
		objectContext.commitChanges()
		
		this.accessToken = authContext.newAccessToken
		this.tenantId = authContext.tenantId
		
	}

	private addManualJournal(String narration, Map<String, Money> entries, LocalDate date, Boolean posted) {
		new RESTClient(XERO_API_BASE).request(POST, JSON) {
			uri.path = '/api.xro/2.0/ManualJournals'
			headers.'Authorization' = "Bearer $accessToken"
			headers.'Xero-tenant-id' = tenantId

			body = [
				Date: date.format(DATE_FORMAT),
				Narration: narration,
				Status: posted ? "POSTED"	: "DRAFT",
				JournalLines: entries.collect {k, v -> [ AccountCode:k, LineAmount:v.toPlainString()] }
			]

			response.failure = { resp, result ->
				
				logger.error(result.toString())
				emailService.email {
					subject('onCourse->Xero manual journal export failed')
					content("$narration \n Reason: $result")
					from (preferenceController.emailFromAddress)
					to (preferenceController.emailAdminAddress)
				}
				
				throw new RuntimeException(result.toString())
			}
			response.success = { resp, result ->
				return result
			}
		}

	}

	protected addManualJournalForTransactions(LocalDate startOn, Boolean posted) {
		LocalDate yestarday = LocalDate.now().minusDays(1)
		LocalDate from
		LocalDate to

		LocalDate lastTransactionUpload = getLastTransactionUpload()

		if (lastTransactionUpload) {
			from = lastTransactionUpload.isAfter(startOn)?lastTransactionUpload.plusDays(1):startOn
		} else {
			from = startOn
		}

		while (from.isBefore(yestarday) || from.equals(yestarday)) {
			YearMonth month = YearMonth.from(from)
			to = month.atEndOfMonth()
			if (to.isAfter(yestarday)) {
				to = yestarday
			}

			List<AccountTransaction> transactions = ObjectSelect.query(AccountTransaction)
					.where(AccountTransaction.TRANSACTION_DATE.gte(from))
					.and(AccountTransaction.TRANSACTION_DATE.lte(to))
					.select(objectContext)

			if (!transactions.empty) {
				Map<String, Money> groups = transactions.inject([:] as Map<String, Money> ) { result, transaction ->
					result[(transaction.account.accountCode)] = (result[transaction.account.accountCode] ?: Money.ZERO).add(transaction.account.credit ? transaction.amount.negate() : transaction.amount)
					return result
				}
				groups = groups.findAll {k,v -> v != Money.ZERO }
				String narration = "onCourse transaction summary for period ${from.format("d MMM yyyy")} to ${to.format("d MMM yyyy")} (inclusive)"
				addManualJournal(narration, groups, to, posted)
			}


			from = to.plusDays(1)
		}
	}

	private LocalDate getLastTransactionUpload() {
		LocalDate lastUpload = null
		new RESTClient(XERO_API_BASE).request(GET, JSON) {
			uri.path = '/api.xro/2.0/ManualJournals'
			headers.'Authorization' = "Bearer $accessToken"
			headers.'Xero-tenant-id' = tenantId

			uri.query = [
					page: "1",
					where: "Narration.StartsWith(\"onCourse transaction summary\")",
					order: "Date DESC"
			]


			response.success = { resp, result ->
				if (result['ManualJournals'] && !result['ManualJournals'].empty ) {
					String jsonDate = result['ManualJournals'][0]['Date']
					Long millis = Long.parseLong(jsonDate.substring(6, 19))
					lastUpload =  LocalDate.ofEpochDay((millis/86400000L).longValue())
				}
			}
		}

		return lastUpload
	}

	protected Employee getEmployee() {

		String id = StringUtils.trimToNull(contact.getCustomFieldValue(EMPLOYEE_FIELD_KEY)?.toString()) ?: searchEmployee()
		if (!id) {
			return null
		}

		new RESTClient(XERO_API_BASE).request(GET, JSON) {
			uri.path = "/payroll.xro/1.0/Employees/$id"
			headers.'Authorization' = "Bearer $accessToken"
			headers.'Xero-tenant-id' = tenantId
			headers.'Accept' = JSON

			response.success = { resp, result ->
				return  new Employee().with { e ->
					e.id = id
					e.calendarId = result['Employees'][0]['PayrollCalendarID']
					e.earningId = result['Employees'][0]['OrdinaryEarningsRateID']
					e
				}
			}
			response.failure = { resp, body ->
				logger.error(body?.toString())
				interruptExport("Error appears while getting employee by extrernal ID: ${body['Message']}")
			}
		} as Employee
	}

	private String searchEmployee() {
		String id = null
		String firstName = contact.tutor?.givenNameLegal?:contact.firstName
		String lastName = contact.tutor?.familyNameLegal?:contact.lastName
		new RESTClient(XERO_API_BASE).request(GET, JSON) {
			uri.path = '/payroll.xro/1.0/Employees'
			headers.'Authorization' = "Bearer $accessToken"
			headers.'Xero-tenant-id' = tenantId
			headers.'Accept' = JSON

			uri.query = [
					where: "FirstName==\"$firstName\" AND LastName==\"$lastName\" AND DateOfBirth==${contact.dateOfBirth.format('\'DateTime\'(YYYY,MM,dd)')}",
			]
			response.success = { resp, result ->
				if (result['Employees'] && !result['Employees'].empty) {
					id = result['Employees'][0]['EmployeeID']
					saveEmployeeId(id)
				}
			}
			response.failure = { resp, body ->
				logger.error(body?.toString())
				interruptExport("Error appears while searching employee by first name/last name/DOB: ${body['Message']}")
			}
		}
		return id
	}

	protected String createEmployee() {

		new RESTClient(XERO_API_BASE).request(POST, XML) {
			uri.path = '/payroll.xro/1.0/Employees'
			headers.'Authorization' = "Bearer $accessToken"
			headers.'Xero-tenant-id' = tenantId
			headers.'Accept' = XML

			body = employeeXmlContent

			response.success = { resp, result ->
				saveEmployeeId(result.Employees.Employee[0].EmployeeID.toString())
				interruptExport(MESSAGE_CONFIGURE_XERO_EMPLOYEE)
			}
			response.failure = { resp, body ->
				logger.error(body?.toString())
				interruptExport("Error appears while creating employee: ${body['Message']}")
			}
		}

	}

	String getPayRunId(String calendarId) {
		new RESTClient(XERO_API_BASE).request(GET, JSON) {
			uri.path = '/payroll.xro/1.0/PayRuns'
			headers.'Authorization' = "Bearer $accessToken"
			headers.'Xero-tenant-id' = tenantId
			headers.'Accept' = JSON

			uri.query = [
					where: "PayRunStatus==\"DRAFT\" && PayrollCalendarID==GUID(\"$calendarId\")",
			]
			response.success = { resp, result ->
				if (result['PayRuns'] && !result['PayRuns'].empty) {
					return result['PayRuns'][0]['PayRunID']
				}
				return createPayRun(calendarId)
			}
			response.failure = { resp, body ->
				logger.error(body?.toString())
				interruptExport("Error appears while getting draft Pay Run: ${body['Message']}")
			}
		}

	}

	String createPayRun(String calendarId) {

		new RESTClient(XERO_API_BASE).request(POST, XML) {
			uri.path = '/payroll.xro/1.0/PayRuns'
			headers.'Authorization' = "Bearer $accessToken"
			headers.'Xero-tenant-id' = tenantId
			headers.'Accept' = XML

			body = ({
				def writer = new StringWriter()
				def xml = new MarkupBuilder(writer)
				xml.PayRuns() {
					PayRun(){
						PayrollCalendarID(calendarId)
					}
				}
				writer.toString()
			})()

			response.success = { resp, result ->
				return result.PayRuns.PayRun[0].PayRunID.toString()
			}
			response.failure = { resp, body ->
				logger.error(body?.toString())
				interruptExport("Xero app create PayRun error: ${body['Message']}")
			}
		}

	}

	String getPaysplipId(String payrunId, String employeeId) {
		new RESTClient(XERO_API_BASE).request(GET, JSON) {
			uri.path = "/payroll.xro/1.0/PayRuns/$payrunId"
			headers.'Authorization' = "Bearer $accessToken"
			headers.'Xero-tenant-id' = tenantId
			headers.'Accept' = JSON

			response.success = { resp, result ->
				if (result['PayRuns'] && !result['PayRuns'].empty &&  result['PayRuns'][0]['Payslips'] && !result['PayRuns'][0]['Payslips'].empty) {
					def payslip = result['PayRuns'][0]['Payslips'].find { payslip -> payslip['EmployeeID'] == employeeId }
					if (payslip) {
						return payslip['PayslipID']
					}
				}
				return null
			}
			response.failure = { resp, body ->
				logger.error(body?.toString())
				interruptExport("Xero app get Paysplip error: ${body['Message']}")
			}
		}
	}

	String getEarningId(String earningName) {
		new RESTClient(XERO_API_BASE).request(GET, JSON) {
			uri.path = "/payroll.xro/1.0/PayItems/"
			headers.'Authorization' = "Bearer $accessToken"
			headers.'Xero-tenant-id' = tenantId
			headers.'Accept' = JSON

			response.success = { resp, result ->
				return result['PayItems']['EarningsRates'].find { earning -> earning['Name'] == earningName }
			}
			response.failure = { resp, body ->
				logger.error(body.toString())
				throw new RuntimeException("Xero app get Earning error: $body")
			}
		}
	}

	void addEarningLine(String payslipId, String earningId, Money amount) {

		new RESTClient(XERO_API_BASE).request(GET, JSON) {
			uri.path = "/payroll.xro/1.0/Payslip/$payslipId"
			headers.'Authorization' = "Bearer $accessToken"
			headers.'Xero-tenant-id' = tenantId
			headers.'Accept' = JSON

			response.success = { resp, result ->

				def currentLines
				if (result['Payslip']['EarningsLines'] && !result['Payslip']['EarningsLines'].empty) {
					currentLines = result['Payslip']['EarningsLines']
				} else {
					currentLines = []
				}

				currentLines << [EarningsRateID: earningId, RatePerUnit: amount.toPlainString(), NumberOfUnits: '1']
				postEarningLines(currentLines as List<Map<String, String>>, payslipId)

			}
			response.failure = { resp, body ->
				logger.error(body?.toString())
				interruptExport("Xero app add Earning Line error: ${body['Message']}")
			}
		}
	}

	void postEarningLines(List<Map<String, String>> lines, String payslipId){

		new RESTClient(XERO_API_BASE).request(POST, JSON) {
			uri.path = "/payroll.xro/1.0/Payslip/$payslipId"
			headers.'Authorization' = "Bearer $accessToken"
			headers.'Xero-tenant-id' = tenantId
			headers.'Accept' = JSON

			body = [
					[ EarningsLines: lines ]
			]

			response.success = { resp, result ->
				payslip.status = PayslipStatus.FINALISED
				objectContext.commitChanges()
			}
			response.failure = { resp, body ->
				logger.error(body?.toString())
				interruptExport("Xero app post Earning Lines error: ${body['Message']}")
			}
		}
	}

	protected void interruptExport(String message) {
		String errorInfo = "Interrapt xero payroll export, payslipId: $payslip.id, reason: $message"
		logger.error(errorInfo)
		payslip.setStatus(PayslipStatus.COMPLETED)
		objectContext.commitChanges()

		emailService.email {
			subject(String.format(EMAIL_SUBJECT, contact.fullName))
			content(String.format(EMAIL_CONTENT_TEMPLATE, contact.fullName, payslip.createdOn.format(DATE_FORMAT), message))
			from (preferenceController.emailFromAddress)
			to (bounceAddress)
		}
		throw new XeroException()
	}

	private void saveEmployeeId(String employeeId) {
		ContactCustomField customField = contact.customFields.find{it.customFieldType.key == EMPLOYEE_FIELD_KEY} ?: objectContext.newObject(ContactCustomField)
		customField.relatedObject = contact
		customField.customFieldType = employeeField
		customField.value = employeeId
		objectContext.commitChanges()
	}

	private String getEmployeeXmlContent() {
		def writer = new StringWriter()
		def xml = new MarkupBuilder(writer)

		xml.Employees() {
			Employee() {

				FirstName(contact.tutor?.givenNameLegal?:contact.firstName)
				LastName(contact.tutor?.familyNameLegal?:contact.lastName)
				DateOfBirth(contact.birthDate.format(DATE_FORMAT))

				HomeAddress() {
					AddressLine1(contact.street)
					City(contact.suburb)
					Region(contact.state)
					PostalCode(contact.postcode)

				}

				if (contact.email) {
					Email(contact.email)
				}
				Gender(contact.gender == null ? "N" : (Gender.MALE == contact.gender ? "M" : "F"))
				StartDate(contact.tutor?.dateStarted ? contact.tutor?.dateStarted?.format(DATE_FORMAT)  : today)
				if (contact.tutor?.dateFinished) {
					TerminationDate(contact.tutor.dateFinished.format(DATE_FORMAT))
				}
				if (contact.workPhone) {
					Phone(contact.workPhone)
				}
				if (contact.mobilePhone) {
					Mobile(contact.mobilePhone)
				}
				if (contact.tfn) {
					TaxDeclaration {
						TaxFileNumber(contact.tfn)
						EmploymentBasis('CASUAL')
					}
				}

			}
		}

		return writer.toString()
	}

	static class Employee {
		String id
		String earningId
		String calendarId
	}

}
