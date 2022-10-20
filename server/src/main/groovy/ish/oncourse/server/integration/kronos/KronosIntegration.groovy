/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.integration.kronos

import groovy.transform.CompileDynamic
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import ish.oncourse.server.api.v1.function.CustomFieldFunctions
import ish.oncourse.server.cayenne.TutorAttendance
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import ish.util.LocalDateUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.text.SimpleDateFormat
import java.time.LocalDate

@CompileDynamic
@Plugin(type = 19)
class KronosIntegration implements PluginTrait {
    public static final String KRONOS_USERNAME_KEY = "username"
    public static final String KRONOS_PASSWORD_KEY = "password"
    public static final String KRONOS_API_KEY = "apiKey"
    public static final String KRONOS_COMPANY_SHORT_NAME_KEY = "companyShortName"
    public static final String KRONOS_CID_KEY = "CID"

    public static final String KRONOS_REST_URL = "https://secure.workforceready.com.au"
    public static final int HTTP_NOT_FOUND = 404
    /**
     * Error codes from https://secure.workforceready.com.au/ta/docs/rest/pages/errorCodes.html
     */
    public static final int ACCOUNT_NOT_FOUND_CODE = 10002

    private static final String KRONOS_SHIFT_ID_CUSTOM_FIELD_KEY = "kronosShiftId"
    private static final String KRONOS_SCHEDULE_ID_CUSTOM_FIELD_KEY = "kronosScheduleId"

    private static Logger logger = LogManager.logger

    String username
    String password
    String apiKey
    String companyShortName
    String CID
    String authToken

    static Map kronosSkills = new HashMap()
    static Map kronosCostCentersIndex0 = new HashMap()
    static Map kronosCostCentersIndex2 = new HashMap()
    static Map kronosEmployees = new HashMap()
    static Map kronosTimeZones = new HashMap()
    static List<KronosSchedule> kronosSchedules = new ArrayList<>()

    static final SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd")

    KronosIntegration(Map args) {
        loadConfig(args)

        this.username = configuration.getIntegrationProperty(KRONOS_USERNAME_KEY).value
        this.password= configuration.getIntegrationProperty(KRONOS_PASSWORD_KEY).value
        this.apiKey = configuration.getIntegrationProperty(KRONOS_API_KEY).value
        this.companyShortName = configuration.getIntegrationProperty(KRONOS_COMPANY_SHORT_NAME_KEY).value
        this.CID = configuration.getIntegrationProperty(KRONOS_CID_KEY).value
    }

    /**
     * Auth header will need to be generated every hour or so. So only call this function if you have a very long running script
     * using this KronosIntegration object
     *
     * @return the access token for used as Bearer authentication
     */
    def initAuthHeader() {
        def client = new RESTClient(KRONOS_REST_URL)
        client.headers["Api-Key"] = apiKey
        def result = client.request(Method.POST, ContentType.JSON) {
            uri.path = "/ta/rest/v1/login"
            body = [
                    credentials: [
                        username: username,
                        password: password,
                        company: companyShortName,
                    ]
            ]
            response.success = { resp, result ->
                authToken = result["token"]
            }
            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to get access token (username, password, company) - (${username}, ${password}, ${companyShortName}): ${resp.getStatusLine()}, ${result}")
            }
        }

        return result
    }

    /**
     * Find Skill from stored kronosSkills, if not find -> reinit Map kronosSkills from Kronos and try to find one more time.
     */
    protected findSkillId(String skillName) {
        def skillId = kronosSkills.get(skillName)
        if (!skillId) {
//            Integration store data, but users can update or add data to Kronos -> that is why reinit data to get actual from Kronos and try to find one more time.
            initSkills()
            skillId = kronosSkills.get(skillName)
            return skillId
        }
        return skillId
    }

    /**
     * Find Cost Center with tree_index:0 from stored kronosCostCenters0Index, if not find -> reinit Map kronosCostCentersIndex0 from Kronos and try to find one more time.
     */
    protected findCostCenterIndex0Id(String costCenterExternalIdOrName) {
        def costCenter0Id = kronosCostCentersIndex0.get(costCenterExternalIdOrName)
        if (!costCenter0Id) {
//            Integration store data, but users can update or add data to Kronos -> that is why reinit data to get actual from Kronos and try to find one more time.
            initCostCenters0Index()
            costCenter0Id = kronosCostCentersIndex0.get(costCenterExternalIdOrName)
            return costCenter0Id
        }
        return costCenter0Id
    }

    /**
     * Find Cost Center with tree_index:2 from stored kronosCostCenters0Index, if not find -> reinit Map kronosCostCentersIndex2 from Kronos and try to find one more time.
     */
    protected findCostCenterIndex2Id(String costCenterName) {
        def costCenter2Id = kronosCostCentersIndex2.get(costCenterName)
        if (!costCenter2Id) {
//            Integration store data, but users can update or add data to Kronos -> that is why reinit data to get actual from Kronos and try to find one more time.
            initCostCenters2Index()
            costCenter2Id = kronosCostCentersIndex2.get(costCenterName)
            return costCenter2Id
        }
        return costCenter2Id
    }

    /**
     * Find Account Id from stored kronosEmployees, if not find -> reinit Map kronosEmployees from Kronos and try to find one more time.
     */
    protected findAccountId(String employeeId) {
        def accountId = kronosEmployees.get(employeeId)
        if (!accountId) {
//            Integration store data, but users can update or add data to Kronos -> that is why reinit data to get actual from Kronos and try to find one more time.
            initEmployees()
            accountId = kronosEmployees.get(employeeId)
            return accountId
        }
        return accountId
    }

    /**
     * Find Schedule Id from stored kronosSchedules, if not find -> reinit List kronosSchedules from Kronos and try to find one more time.
     */
    protected findScheduleId(String scheduleName, scheduleSettingId, String sessionDate) {
        Date date = dateFormat.parse(sessionDate)
        def schedules = kronosSchedules.findAll { it.name == scheduleName && it.scheduleStart <= date && it.scheduleEnd > date}
        if (schedules.size() < 1) {
//            Integration store data, but users can update or add data to Kronos -> that is why reinit data to get actual from Kronos and try to find one more time.
            initKronosSchedules(scheduleName, scheduleSettingId, date)
            schedules = kronosSchedules.findAll {it.name == scheduleName && it.scheduleStart <= date && it.scheduleEnd > date}
            if (schedules.size() < 1) {
                throw new IllegalStateException("Kronos Company '${companyShortName}' have no Schedule with name '${scheduleName}' and settings_id '${scheduleSettingId}', which contains the date '${sessionDate}'.")
            }
        }
        if (schedules.size() > 1) {
            throw new IllegalStateException("Kronos Company '${companyShortName}' have more then one Schedules with name '${scheduleName}' and settings_id '${scheduleSettingId}' and contains the date '${sessionDate}'. Here are the schedules with their ids: '${schedules.id}'.")
        }
        return schedules[0].id
    }

    /**
     * Find Time Zone from stored kronosTimeZones, if not find -> reinit Map kronosTimeZones from Kronos and try to find one more time.
     */
    protected findTimeZoneName(timeZoneId) {
        def timeZoneName = kronosTimeZones.get(timeZoneId)
        if (!timeZoneName) {
//            Integration store data, but users can update or add data to Kronos -> that is why reinit data to get actual from Kronos and try to find one more time.
            initTimeZones()
            timeZoneName = kronosTimeZones.get(timeZoneId)
            return timeZoneName
        }
        return timeZoneName
    }

    /**
     * Get list of Skills from Kronos and store skill names and skill ids in kronosSkills Map<name,id>.
     */
    protected void initSkills() {
        Map skillsFromKronos = getCompanySkills() as Map
        List<Map> items = skillsFromKronos["items"] as List<Map>
        if (!items){
            throw new IllegalStateException("Kronos Company '${companyShortName}' doesn't have any Skills")
        }
        kronosSkills = new HashMap()
        for (Map skill : items) {
            kronosSkills.put(skill["name"], skill["id"])
        }
    }

    /**
     * Get list of Cost Centers from Kronos with tree_index:0 and store Cost Center external_id or names (if external_id doesn't exist) and Cost Center ids in kronosCostCenters0Index Map<(external_id/name),id>.
     */
    protected void initCostCenters0Index() {
        Map costCentersIndex0FromKronos = getCompanyCostCenters(0) as Map
        List<Map> centers = costCentersIndex0FromKronos["cost_centers"] as List<Map>
        if (!centers){
            throw new IllegalStateException("Kronos Company '${companyShortName}' doesn't have any Cost Centers with tree_index:0")
        }
        kronosCostCentersIndex0 = new HashMap()
        for (Map center : centers) {
            if (center["external_id"]) {
                kronosCostCentersIndex0.put(center["external_id"], center["id"])
            } else {
                kronosCostCentersIndex0.put(center["name"], center["id"])
            }
        }
    }

    /**
     * Get list of Cost Centers from Kronos with tree_index:2 and store Cost Center external_id or names (if external_id doesn't exist) and Cost Center ids in kronosCostCentersIndex2 Map<(external_id/name),id>.
     */
    protected void initCostCenters2Index() {
        Map costCentersIndex2FromKronos = getCompanyCostCenters(2) as Map
        List<Map> centers = costCentersIndex2FromKronos["cost_centers"] as List<Map>
        if (!centers){
            throw new IllegalStateException("Kronos Company '${companyShortName}' doesn't have any Cost Centers with tree_index:2")
        }
        kronosCostCentersIndex2 = new HashMap()
        for (Map center : centers) {
            kronosCostCentersIndex2.put(center["name"], center["id"])
        }
    }

    /**
     * Get list of Employees from Kronos and store Employee employee_id and Employee ids in kronosEmployees Map<employee_id,id>.
     */
    protected void initEmployees() {
        Map employeesFromKronos = getAllEmployees() as Map
        List<Map> items = employeesFromKronos["employees"] as List<Map>
        if (!items){
            throw new IllegalStateException("Kronos Company '${companyShortName}' doesn't have any Employees")
        }
        kronosEmployees = new HashMap()
        for (Map employee : items) {
            kronosEmployees.put(employee["employee_id"], employee["id"])
        }
    }

    /**
     * Get list of Schedules by Schedule Name from Kronos and store Shedule name, id, schedule_start and schedule_end in kronosSchedules List<KronosSchedule>.
     */
    protected void initKronosSchedules(String scheduleName, scheduleSettingId, Date date) {
//        A Schedule GET request without 'from' and 'to' return Schedules only for current month. If today is 2022-09-29 the request won't return Schedules for with date 2022-10-03 - 2022-10-09. Date range cannot be more than '31' days. That is why I added and substracted 15 days to get Schedules from this date range.
        String dateFrom = addDaysToDate(date, -15)
        String dateTo = addDaysToDate(date, 15)
        Map resultFromKronos = getCompanySchedules(dateFrom, dateTo) as Map
        List<Map> schedulesFromKronos = resultFromKronos["schedules"] as List<Map>
        if (!schedulesFromKronos) {
            throw new IllegalStateException("Kronos Company '${companyShortName}' doesn't have any Schedules between ${dateFrom} and ${dateTo}.")
        }
        List<Map> schedulesByNameAndSettindId = schedulesFromKronos.findAll { it["name"] == scheduleName && it["settings_id"] == scheduleSettingId }
        if (schedulesByNameAndSettindId.size() == 0) {
            throw new IllegalStateException("Kronos Company '${companyShortName}' doesn't have Schedules with name '${scheduleName}' and settings_id '${scheduleSettingId}' between ${dateFrom} and ${dateTo}.")
        }
        kronosSchedules = new ArrayList()
        for (Map schedule : schedulesByNameAndSettindId) {
            kronosSchedules.add(new KronosSchedule(schedule["name"] as String, schedule["id"] as String, schedule["schedule_start"] as String, schedule["schedule_end"] as String))
        }
    }

    /**
     * Get list of TimeZones from Kronos and store TimeZones timezone id and time zone java name in kronosTimeZones Map<id,javaName>.
     */
    protected void initTimeZones() {
        Map timeZonesFromKronos = getCompanyTimeZones() as Map
        List<Map> timeZones = timeZonesFromKronos["items"] as List<Map>
        if (!timeZones){
            throw new IllegalStateException("Kronos Company '${companyShortName}' doesn't have time zones")
        }
        kronosTimeZones = new HashMap()
        for (Map timeZone : timeZones) {
            kronosTimeZones.put(timeZone["id"], timeZone["java_name"])
        }
    }

    /**
     * Find Time Zone from scheduleSetting a convert it id for java time zone
     */
    protected String findTimeZone(scheduleSetting) {
        def timeZoneId = scheduleSetting["time_zone"]["id"]
        String timeZoneName = findTimeZoneName(timeZoneId)
        if (!timeZoneName) {
            throw new IllegalStateException("Kronos Company '${companyShortName}' with Schedule Settings name '${scheduleSetting["name"]}' doesn't have correct Time Zone, Time Zone id is '${timeZoneId}'")
        }
        return timeZoneName
    }

    /**
     * Get Schedule Setting from Kronos by Schedule Setting Name
     */
    protected Map getScheduleSettingByName(String scheduleSettingName){
        Map resultFromKronos = getCompanyScheduleSettings() as Map
        List<Map> scheduleSettingsFromKronos = resultFromKronos["settings"] as List<Map>
        if (!scheduleSettingsFromKronos) {
            throw new IllegalStateException("Kronos Company '${companyShortName}' doesn't have Schedule Settings.")
        }
        def scheduleSettingsByName = scheduleSettingsFromKronos.findAll { it["name"] == scheduleSettingName}
        if (scheduleSettingsByName.size() < 1) {
            throw new IllegalStateException("Kronos Company '${companyShortName}' have no Schedule Settings with name '${scheduleSettingName}'.")
        }
        if (scheduleSettingsByName.size() > 1) {
            throw new IllegalStateException("Kronos Company '${companyShortName}' have more then one Schedule Settings with name '${scheduleSettingName}'. Here are the schedules with their ids: '${scheduleSettingName["id"]}'.")
        }
        return scheduleSettingsByName[0]
    }

    /**
     * Retrieves the list of Schedule Settings. Required to create a Schedule in the System.
     * Advanced Scheduler (SCHEDULE) Subsystem must be enabled in company setup.
     *
     * @return schedule settings
     */
    protected getCompanyScheduleSettings() {
        def client = new RESTClient(KRONOS_REST_URL)
        client.headers["Authentication"] = "Bearer ${authToken}"
        client.request(Method.GET) {
            uri.path = "/ta/rest/v2/companies/${CID}/schedules/settings"

            response.success = { resp, result ->
                return result
            }
            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to get Schedule Settings (company cid) - (${CID}): ${resp.getStatusLine()}, ${result}")
            }
        }
    }

    /**
     * Return list of defined skills in the company.
     * Skills from Kronos via API v2
     *
     * @return skills
     */
    protected getCompanySkills() {
        def client = new RESTClient(KRONOS_REST_URL)
        client.headers["Authentication"] = "Bearer ${authToken}"
        client.request(Method.GET) {
            uri.path = "/ta/rest/v2/companies/${CID}/lookup/skills"

            response.success = { resp, result ->
                return result
            }
            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to get Skills (company cid) - (${CID}): ${resp.getStatusLine()}, ${result}")
            }
        }
    }

    /**
     * Allows existing cost centers to be returned. Response is a flat list.
     * Relationships can be traced using parent_id field. Root does not have parent_id field.
     * All visible cost centers will be return even if their parent has visible set to false.
     * To view cost centers, user must have a Cost Center Definitions view permission.
     * Cost Centers from Kronos via API v2
     *
     * @return cost centers
     */
    protected getCompanyCostCenters(int treeIndex) {
        def client = new RESTClient(KRONOS_REST_URL)
        client.headers["Authentication"] = "Bearer ${authToken}"
        client.request(Method.GET) {
            uri.path = "/ta/rest/v2/companies/${CID}/config/cost-centers"
            uri.query = [
                    tree_index: treeIndex,
            ]

            response.success = { resp, result ->
                return result
            }
            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to get Cost Centers (company cid) - (${CID}), (tree_index) - (${treeIndex}) : ${resp.getStatusLine()}, ${result}")
            }
        }
    }

    /**
     * Returns the the list of Schedules
     * Advanced Scheduler (SCHEDULE) Subsystem must be enabled in company setup.
     *
     * @return schedules
     */
    protected getCompanySchedules(String from, String to) {
        def client = new RESTClient(KRONOS_REST_URL)
        client.headers["Authentication"] = "Bearer ${authToken}"
        client.request(Method.GET) {
            uri.path = "/ta/rest/v2/companies/${CID}/schedules"
            uri.query = [
                    from: from,
                    to: to,
            ]

            response.success = { resp, result ->
                return result
            }
            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to get Schedules (company cid, from, to) - (${CID}, ${from}, ${to}): ${resp.getStatusLine()}, ${result}")
            }
        }
    }

    /**
     * Returns basic information about the employees a user has permissions to view,
     * together with links to APIs that provide detailed information about each employee.
     * Employees from Kronos via API v2
     *
     * @return employees
     */
    protected getAllEmployees() {
        def client = new RESTClient(KRONOS_REST_URL)
        client.headers["Authentication"] = "Bearer ${authToken}"
        client.request(Method.GET) {
            uri.path = "/ta/rest/v2/companies/${CID}/employees"

            response.success = { resp, result ->
                return result
            }
            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to get Employees (company cid) - (${CID}): ${resp.getStatusLine()}, ${result}")
            }
        }
    }

    /**
     * Endpoint to fetch list of defined timezones
     *
     * @return timezones
     */
    protected getCompanyTimeZones() {
        def client = new RESTClient(KRONOS_REST_URL)
        client.headers["Authentication"] = "Bearer ${authToken}"
        client.request(Method.GET) {
            uri.path = "/ta/rest/v2/companies/${CID}/lookup/timezones"

            response.success = { resp, result ->
                return result
            }
            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to get Time Zones (company cid) - (${CID}): ${resp.getStatusLine()}, ${result}")
            }
        }
    }

    protected getKronosShiftIdBySessionFields(scheduleId, accountId, shiftStart, shiftEnd, skillId, costCenter0Id, costCenter2Id, tutorAttendanceId) {
        Map shiftsFromKronos = getShiftsByScheduleId(scheduleId) as Map
        List<Map> shifts = shiftsFromKronos["shifts"] as List<Map>
        if (!shifts){
            throw new IllegalStateException("Kronos Company '${companyShortName}' ScheduleId ${scheduleId} doesn't have shifts")
        }
        List shiftIds = new ArrayList()
        for (Map shift : shifts) {
            if (shift["account"]["account_id"] == accountId && shift["shift_start"] == shiftStart && shift["shift_end"] == shiftEnd && shift["skill"]["id"] == skillId) {
                List<Map> costCenters = shift["cost_centers"] as List<Map>
                def costCenter0 = costCenters.findAll { it["index"] == 0 && it["value"]["id"] == costCenter0Id }
                def costCenter2 = costCenters.findAll { it["index"] == 2 && it["value"]["id"] == costCenter2Id}
                if (costCenter0.size() == 1 && costCenter2.size() == 1) {
                    shiftIds.add(shift["id"])
                }
            }
        }
        if (shiftIds.size() < 1) {
            throw new IllegalStateException("Kronos Company '${companyShortName}' Schedule with id '${scheduleId}' have no one shift with given fields. Custom Field won't add to TutorAttendance with id '${tutorAttendanceId}'")
        }
        if (shiftIds.size() > 1) {
            throw new IllegalStateException("Kronos Company '${companyShortName}' Schedule with id '${scheduleId}' have more then 1 shift with given fields: (shift ids) - (${shiftIds}). Custom Field won't add to TutorAttendance with id '${tutorAttendanceId}'")
        }
        return shiftIds.get(0)
    }

    /**
     * Gets the list of Shifts being housed in a Schedule
     * Advanced Scheduler (SCHEDULE) Subsystem must be enabled in company setup.
     * Shifts from Kronos via API v2
     *
     * @return Shifts
     */
    protected getShiftsByScheduleId(scheduleId) {
        def client = new RESTClient(KRONOS_REST_URL)
        client.headers["Authentication"] = "Bearer ${authToken}"
        client.request(Method.GET) {
            uri.path = "/ta/rest/v2/companies/${CID}/schedules/${scheduleId}/shifts"

            response.success = { resp, result ->
                return result
            }
            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to get shifts (company cid, scheduleId) - (${CID}, ${scheduleId}): ${resp.getStatusLine()}, ${result}")
            }
        }
    }

    /**
     * Create a new shift in Kronos
     *
     */
    def createNewShift(accountId, date, shiftStart, shiftEnd, costCenter0Id, costCenter2Id, skillId, scheduleId) {
        def client = new RESTClient(KRONOS_REST_URL)
        client.headers["Authentication"] = "Bearer ${authToken}"
        client.request(Method.POST, ContentType.JSON) {
            uri.path = "/ta/rest/v2/companies/${CID}/schedules/${scheduleId}/shifts/collection"
            body = [
                    shifts: [
                            [
                                    account: [
                                            account_id: accountId
                                    ],
                                    date: date,
                                    type: "FIXED",
                                    shift_start: shiftStart,
                                    shift_end: shiftEnd,
                                    cost_centers: [
                                            [
                                                    index: 0,
                                                    value: [
                                                            id: costCenter0Id,
                                                    ]
                                            ],
                                            [
                                                    index: 2,
                                                    value: [
                                                            id: costCenter2Id,
                                                    ]
                                            ]
                                    ],
                                    skill: [
                                            schedulable: true,
                                            id: skillId,
                                    ],
                            ],
                    ]
                    ]
            response.success = { resp, result ->
                return [created: true, response: resp, result: result]
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to create shift (company cid, scheduleId, accountId, date, shiftStart, shiftEnd, costCenter0Id, costCenter2Id, skillId) - " +
                        "(${CID}, ${scheduleId}, ${accountId}, ${date}, ${shiftStart}, ${shiftEnd}, ${costCenter0Id}, ${costCenter2Id}, ${skillId}): ${resp.getStatusLine()}, ${result}")
            }
        }
    }

    /**
     * Update shift in Kronos
     *
     */
    def updateShift(shiftId, accountId, date, shiftStart, shiftEnd, costCenter0Id, costCenter2Id, skillId, scheduleId) {
        def client = new RESTClient(KRONOS_REST_URL)
        client.headers["Authentication"] = "Bearer ${authToken}"
        client.request(Method.PUT, ContentType.JSON) {
            uri.path = "/ta/rest/v2/companies/${CID}/schedules/${scheduleId}/shifts/collection"
            body = [
                    shifts: [
                            [
                                    id: shiftId,
                                    account: [
                                            account_id: accountId
                                    ],
                                    date: date,
                                    type: "FIXED",
                                    shift_start: shiftStart,
                                    shift_end: shiftEnd,
                                    cost_centers: [
                                            [
                                                    index: 0,
                                                    value: [
                                                            id: costCenter0Id,
                                                    ]
                                            ],
                                            [
                                                    index: 2,
                                                    value: [
                                                            id: costCenter2Id,
                                                    ]
                                            ]
                                    ],
                                    skill: [
                                            schedulable: true,
                                            id: skillId,
                                    ],
                            ],
                    ]
            ]
            response.success = { resp, result ->
                return [updated: true, response: resp, result: result]
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to update shift (company cid, scheduleId, shiftId, accountId, date, shiftStart, shiftEnd, costCenter0Id, costCenter2Id, skillId) - " +
                        "(${CID}, ${scheduleId}, ${shiftId}, ${accountId}, ${date}, ${shiftStart}, ${shiftEnd}, ${costCenter0Id}, ${costCenter2Id}, ${skillId}): ${resp.getStatusLine()}, ${result}")
            }
        }
    }

    /**
     * Delete Shift by shift id and schedule id
     *
     * @return timezones
     */
    protected deleteShift(shiftId, scheduleId) {
        def client = new RESTClient(KRONOS_REST_URL)
        client.headers["Authentication"] = "Bearer ${authToken}"
        client.request(Method.DELETE) {
            uri.path = "/ta/rest/v2/companies/${CID}/schedules/${scheduleId}/shifts/${shiftId}"

            response.success = { resp, result ->
                return [response: resp, result: result]
            }
            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to delete Shift from Kronos (company cid, scheduleId, shiftId) - (${CID}, ${scheduleId}, ${shiftId}): ${resp.getStatusLine()}, ${result}")
            }
        }
    }

    private String addDaysToDate(Date date, long daysToAdd) {
        LocalDate localDate = LocalDateUtils.dateToValue(date)
        LocalDate dateWithAddedDays = localDate.plusDays(daysToAdd)
        return LocalDateUtils.valueToString(dateWithAddedDays)
    }

    protected static getCustomFieldShiftId(TutorAttendance tutorAttendance) {
        return getCustomFieldValue(tutorAttendance, KRONOS_SHIFT_ID_CUSTOM_FIELD_KEY)
    }

    protected static getCustomFieldScheduleId(TutorAttendance tutorAttendance) {
        return getCustomFieldValue(tutorAttendance, KRONOS_SCHEDULE_ID_CUSTOM_FIELD_KEY)
    }

    private static getCustomFieldValue(TutorAttendance tutorAttendance, String customFieldKey) {
        try {
            tutorAttendance.getCustomFieldValue(customFieldKey)
        } catch (MissingPropertyException ex) {
            logger.warn("TutorAttendanceCustomField '${customFieldKey}' doesn't exist yet. Will be added the first time when will add a shift to Kronos and save TutorAttendanceCustomField with key '${customFieldKey}'. Exception message: ${ex.getMessage()}")
            return null
        }
    }

    protected static void saveCustomFields(TutorAttendance tutorAttendance, String kronosShiftId, String kronosScheduleId) {
        CustomFieldFunctions.addCustomFieldWithoutCommit(KRONOS_SHIFT_ID_CUSTOM_FIELD_KEY, kronosShiftId, tutorAttendance, tutorAttendance.context)
        CustomFieldFunctions.addCustomFieldWithoutCommit(KRONOS_SCHEDULE_ID_CUSTOM_FIELD_KEY, kronosScheduleId, tutorAttendance, tutorAttendance.context)
        tutorAttendance.context.commitChanges()
    }

    private static class KronosSchedule {

        private String name
        private String id
        private Date scheduleStart
        private Date scheduleEnd

        KronosSchedule(String name, String id, String scheduleStart, String scheduleEnd) {
            this.name = name
            this.id = id
            this.scheduleStart = dateFormat.parse(scheduleStart)
            this.scheduleEnd = dateFormat.parse(scheduleEnd)
        }

        String getName() {
            return name
        }

        String getId() {
            return id
        }

        Date getScheduleStart() {
            return scheduleStart
        }

        Date getScheduleEnd() {
            return scheduleEnd
        }
    }
}
