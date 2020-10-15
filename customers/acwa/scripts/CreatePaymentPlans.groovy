/**
 * This script creates Payment Plan lines for any CourseClass which has
 * - not zero fee and
 * - start date is not null
 */

import ish.math.Money
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.CourseClassPaymentPlanLine
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SQLSelect
import org.apache.logging.log4j.LogManager




def run(args) {
    Processor processor = new Processor()
    processor.context = args.context
    processor.run()
}

class Processor {
    private static Logger = LogManager.getLogger(Processor)
    private static String SELECT_CLASSES = 'select DISTINCT cc.* from CourseClass cc \n' +
            'left join CourseClassPaymentPlanLine ccpl on cc.id = ccpl.courseClassId\n' +
            'where cc.feeExGst > 0 and cc.startDateTime is not null and ccpl.id is null'


    ObjectContext context

    void run() {
        List<CourseClass> courseClasses = SQLSelect.query(CourseClass, SELECT_CLASSES).select(context)
        courseClasses.each {
            try {
                CourseClassPaymentPlanLine line = context.newObject(CourseClassPaymentPlanLine)
                line.amount = Money.ZERO
                line.courseClass = it

                line = context.newObject(CourseClassPaymentPlanLine)
                line.amount = it.feeIncGst
                line.dayOffset = 0
                line.courseClass = it
                context.commitChanges()
            } catch (e) {
                Logger.error(e)
                throw e
            }
        }
    }
}
