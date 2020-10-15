/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import groovy.time.TimeCategory

def run(args) {
	use(TimeCategory) {

		def sendDebug = false
		def debugLine = ''


		def context = args.context
		def DESCRIPTION = "attendant cost (do not edit) "
		def SUNDAY = 0
		def MONDAY = 1
		def FRIDAY = 5
		def SATURDAY = 6

		excludedPrecincts = [ "Sydney University Village", "No attendant cost", "Not Applicable" ]

		// 1. Find all sessions scheduled in the future
		def today = new Date()
		today.set(hourOfDay: 0, minute: 0, second: 0)

		def sessions = ObjectSelect.query(Session).
				where(Session.START_DATETIME.gte(today)).
				select(context)

		// we want to group the costs by site and date
		Map costGrouped = new HashMap<String, List<ClassCost>>()

		// create the cost if necessary
		sessions.findAll{ session -> session.startDatetime.day == SUNDAY || session.startDatetime.day == SATURDAY }.each { session ->
			// get the first tag in the precinct tag group
            Tag precinct = session.room?.site?.tags.find { it.parentTag.name.equalsIgnoreCase("precinct") }

 			if (precinct && !(precinct.name in excludedPrecincts)) {
                logger.debug("precinct class {}", precinct.class)
				def costName = DESCRIPTION + session.startDatetime.format('d MMM ') + session.id
				// do we have an existing cost?
				def cost = session.courseClass.costs.find { it.description == costName }
				if (!cost) {
					cost = args.context.newObject(ClassCost.class)
					cost.description = costName
					cost.isSunk = false
					cost.flowType = ClassCostFlowType.EXPENSE
					cost.courseClass = session.courseClass
					cost.repetitionType = ClassCostRepetitionType.FIXED
				}

				String groupKey = precinct.name + "-" + session.startDatetime.format('yyMMdd')
				if (!costGrouped[groupKey]) {
					costGrouped[groupKey] = []
				}
				costGrouped[groupKey] << cost
			}
		}

		// remove any session costs for sessions which are deleted
		def classes = ObjectSelect.query(CourseClass).where(CourseClass.START_DATE_TIME.gt(today)).select(context)
		if (classes) {
			classes.each { c ->
				c.costs.findAll { it.description.startsWith(DESCRIPTION) }.each { cost ->
					sessionId = cost.description.split(' ').last()
					def session = ObjectSelect.query(Session).
							where(Session.ID.eq(sessionId)).
							selectOne(context)
					if (!session) {
						context.deleteObject(cost)
					} else {
						Tag precinct = session.room?.site?.tags.find { it.parentTag.name.equalsIgnoreCase("precinct") }
						if (!precinct || (precinct.name in excludedPrecincts)) {
							context.deleteObject(cost)
						}
					}
				}
			}
		}

		// Now calculate all the costs
		costGrouped.each { group, costs ->
			dayOfWeek = null
			hoursInDay = 0..24
			Map<Integer, List<ClassCost>> hourMap = hoursInDay.collectEntries { [ (it) : [] ]}

			costs.each { cost ->
				cost.perUnitAmountExTax = Money.ZERO
				sessionId = cost.description.split().last()
				def session = ObjectSelect.query(Session).
						where(Session.ID.eq(sessionId)).
						selectOne(args.context)

				if (dayOfWeek == null) {
					dayOfWeek = session.startDatetime.day
				}

				// take a minute off the end time so we don't require a whole new hour of attendant time
				(session.startDatetime.hours..(session.endDatetime - 1.minute).hours).each { hour ->

					if ( hour in 7..18 && session.startDatetime.day in MONDAY..FRIDAY ) {
						// don't add session in the free part Mon-Fri 7am-7pm
					} else {
						hourMap[hour] << cost
					}
				}
			}

			switch(dayOfWeek) {
				case SUNDAY: //Sunday
					rate = 110.0
					break
				case SATURDAY: //Saturday
					rate = 85.0
					break
				default: // weekdays
					rate = 0.0
			}

			hourMap.each { hour, hourCosts ->
				sessionsPerHour = hourCosts.size()
				hourCosts.each { cost ->
					costPerHour = rate/sessionsPerHour
					cost.perUnitAmountExTax =  cost.perUnitAmountExTax.add(costPerHour)
					debugLine += "class: ${cost.courseClass.uniqueCode}, hour: ${hour}, cost.description: ${cost.description}, "
					debugLine += "group: ${group} "
					debugLine += ", sessionsPerHour: ${sessionsPerHour}, cost.perUnitAmountExTax: ${cost.perUnitAmountExTax} \n"
				}

			}
			debugLine += "--------------\n"

		}
		if (sendDebug) {
			email {
				to "support@ish.com.au"
				from "operations@cce.sydney.edu.au"
				subject "CCE Site attendant debug"
				content debugLine
			}
		}

		args.context.commitChanges()
	}
}
