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
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait

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

    String username
    String password
    String apiKey
    String companyShortName
    String CID
    String authToken

    static Map kronosSkills = new HashMap()
    static Map kronosCostCenters = new HashMap()
    static Map kronosEmployees = new HashMap()

    static String kronosScheduleName
    static Integer kronosScheduleId
    static String kronosScheduleTimeZone

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
     * Find Cost Center from stored kronosCostCenters, if not find -> reinit Map kronosCostCenters from Kronos and try to find one more time.
     */
    protected findCostCenterId(String costCenterName) {
        def costCenterId = kronosCostCenters.get(costCenterName)
        if (!costCenterId) {
//            Integration store data, but users can update or add data to Kronos -> that is why reinit data to get actual from Kronos and try to find one more time.
            initCostCenters()
            costCenterId = kronosCostCenters.get(costCenterName)
            return costCenterId
        }
        return costCenterId
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
     * Get list of Skills from Kronos and store skill names and skill ids in kronosSkills Map<name,id>.
     */
    protected void initSkills() {
        Map skillsFromKronos = getCompanySkills() as Map
        List<Map> items = skillsFromKronos["items"] as List<Map>
        if (!items){
            throw new IllegalStateException("Kronos Company ${companyShortName} doesn't have skills")
        }
        kronosSkills = new HashMap()
        for (Map skill : items) {
            kronosSkills.put(skill["name"], skill["id"])
        }
    }

    /**
     * Get list of Cost Centers from Kronos and store Cost Center names and Cost Center ids in kronosCostCenters Map<name,id>.
     */
    protected void initCostCenters() {
        Map costCentersFromKronos = getCompanyCostCenters() as Map
        List<Map> centers = costCentersFromKronos["cost_centers"] as List<Map>
        if (!centers){
            throw new IllegalStateException("Kronos Company ${companyShortName} doesn't have Cost Centers")
        }
        kronosCostCenters = new HashMap()
        for (Map center : centers) {
            kronosCostCenters.put(center["name"], center["id"])
        }
    }

    /**
     * Get list of Employees from Kronos and store Employee employee_id and Employee ids in kronosCostCenters Map<employee_id,id>.
     */
    protected void initEmployees() {
        Map employeesFromKronos = getAllEmployees() as Map
        List<Map> items = employeesFromKronos["employees"] as List<Map>
        if (!items){
            throw new IllegalStateException("Kronos Company ${companyShortName} doesn't have employees")
        }
        kronosEmployees = new HashMap()
        for (Map employee : items) {
            kronosEmployees.put(employee["employee_id"], employee["id"])
        }
    }

    /**
     * Get Schedule from Kronos and store Schedule fields.
     */
    protected void initSchedule(String scheduleName) {
        Map resultFromKronos = getCompanySchedules() as Map
        List<Map> schedulesFromKronos = resultFromKronos["schedules"] as List<Map>
        if (!schedulesFromKronos) {
            setStoredScheduleValuesToNull()
            throw new IllegalStateException("Kronos Company ${companyShortName} doesn't have Schedules")
        }
        List<Map> schedulesByName = schedulesFromKronos.findAll { it["name"] == scheduleName }
        if (schedulesByName.size() == 0) {
            setStoredScheduleValuesToNull()
            throw new IllegalStateException("Kronos Company ${companyShortName} doesn't have Schedules with name '${scheduleName}'.")
        }
        if (schedulesByName.size() > 1) {
            setStoredScheduleValuesToNull()
            throw new IllegalStateException("Kronos Company ${companyShortName} have more then 1 Schedules with name '${scheduleName}'.")
        }
        kronosScheduleName = schedulesByName.get(0)["name"]
        kronosScheduleId = schedulesByName.get(0)["id"]
        kronosScheduleTimeZone = schedulesByName.get(0)["time_zone"]["display_name"]
    }

    private static void setStoredScheduleValuesToNull() {
        kronosScheduleName = null
        kronosScheduleId = null
        kronosScheduleTimeZone = null
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
    protected getCompanyCostCenters() {
        def client = new RESTClient(KRONOS_REST_URL)
        client.headers["Authentication"] = "Bearer ${authToken}"
        client.request(Method.GET) {
            uri.path = "/ta/rest/v2/companies/${CID}/config/cost-centers"
            uri.query = [
                    tree_index: 0,
            ]

            response.success = { resp, result ->
                return result
            }
            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to get Cost Centers (company cid) - (${CID}): ${resp.getStatusLine()}, ${result}")
            }
        }
    }

    /**
     * Returns the the list of Schedules
     * Advanced Scheduler (SCHEDULE) Subsystem must be enabled in company setup.
     *
     * @return schedules
     */
    protected getCompanySchedules() {
        def client = new RESTClient(KRONOS_REST_URL)
        client.headers["Authentication"] = "Bearer ${authToken}"
        client.request(Method.GET) {
            uri.path = "/ta/rest/v2/companies/${CID}/schedules"

            response.success = { resp, result ->
                return result
            }
            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to get schedules (company cid) - (${CID}): ${resp.getStatusLine()}, ${result}")
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
     * Returns the core Employee record containing the bulk of the information about employee.
     * Additionally the response contains links to additional APIs to return additional related data such as Pay Information or Demographics.
     * Employee from Kronos via API v2
     *
     * @return employee
     */
    protected getEmployeeById(id) {
        def client = new RESTClient(KRONOS_REST_URL)
        client.headers["Authentication"] = "Bearer ${authToken}"
        client.request(Method.GET) {
            uri.path = "/ta/rest/v2/companies/${CID}/employees/${id}"

            response.success = { resp, result ->
                return result
            }
            response.failure = { resp, result ->
                if (resp.getProperties()["status"] == HTTP_NOT_FOUND && result?.errors?.code?.get(0) == ACCOUNT_NOT_FOUND_CODE) {
                    return null
                } else {
                    throw new IllegalStateException("Failed to get employee with id ${id} (company cid) - (${CID}): ${resp.getStatusLine()}, ${result}")
                }
            }
        }
    }

    /**
     * Create a new shift in Kronos
     *
     */
    def createNewShift(accountId, date, shiftStart, shiftEnd, costCenter1Id, costCenter3Id, skillId, scheduleId) {
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
                                                    index: 1,
                                                    value: [
                                                            id: costCenter1Id,
                                                    ]
                                            ],
                                            [
                                                    index: 3,
                                                    value: [
                                                            id: costCenter3Id,
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
                return result
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to create shift (company cid, accountId, date, shiftStart, shiftEnd, costCenter1Id, costCenter3Id, skillId, scheduleId) - " +
                        "(${CID}, ${accountId}, ${date}, ${shiftStart}, ${shiftEnd}, ${costCenter1Id}, ${costCenter3Id}, ${skillId}, ${scheduleId}): ${resp.getStatusLine()}, ${result}")
            }
        }
    }
}
