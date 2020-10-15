// We need to create a new builder here with an IndentPrinter that doesn't indent or add new lines
// This is because inDesign is sensitive to where those new lines are added
import groovy.xml.MarkupBuilder
import ish.common.types.NodeSpecialType

xml = new MarkupBuilder(new IndentPrinter(output, "", false, false))
xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

xml.brochure {
    courseClasses = records.groupBy { CourseClass cc -> cc.course }
    subj = courseClasses.keySet().collectMany { Course co -> co.tags }.find { Tag t -> t.getRoot().name.equals('Category')}?.getRoot()

    if (subj) {
        "Tag"(subj.name) {
           addChild(subj, xml, courseClasses)
        }
    }
}

def addChild(parent, xml, courseClasses) {
    parent.childTags.each { Tag child ->
        xml.Tag(child.name) {
            courseClasses.each { Course co, List<CourseClass> classes ->
                if (co.tags.contains(child)) {
                    mkp.yield("\n")
                    Course() {
                        mkp.yield("\n")
                        CourseName(co.name)
                        mkp.yield("\nwith ")
                        TutorNameFromFirstClass(classes[0]?.tutorRoles[0]?.tutor?.fullName)
                        mkp.yield("\n")
                        Description(co.printedBrochureDescription)
                        classes.each { CourseClass cc ->
                            mkp.yield("\n")
                            Class() {
                                SessionCount(cc.sessionsCount.toString())
                                mkp.yield(" ")
                                DayOfWeekOfFirstSession(cc.startDateTime?.format("EEE"))
                                mkp.yield(", ")
                                StartDate(cc.startDateTime?.format("d MMMMM"))
                                mkp.yield(" to ")
                                EndDate(cc.endDateTime?.format("d MMMMM"))
                                mkp.yield("\n")
                                FirstSessionStartTime(cc.startDateTime?.format("h:mm a").toLowerCase())
                                mkp.yield(" - ")
                                FirstSessionEndTime(cc.endDateTime?.format("h:mm a ").toLowerCase())
                                Site(cc.room?.site?.name.replace("Mullumbimby Campus", "Mullum").replace("Byron Bay Campus","Byron"))

                                def noCents = new java.text.DecimalFormat("###,##0")
                                ConcessionFee() {
                                    cc.discounts.findAll { discount -> !discount.code && !discount.hideOnWeb && (discount.validTo>new Date() || !discount.validTo) }.each { Discount discount ->
                                        if (discount.discountDollar) {
                                            Price("\nConcession: \$" + noCents.format((cc.feeExGst.toBigDecimal() - discount.discountDollar.toBigDecimal()) * 1.1) )
                                        } else {
                                            Price("\nConcession: \$" + noCents.format(cc.feeIncGst.toBigDecimal() * ( 1 - discount.discountPercent.toBigDecimal()) ))
                                        }
                                    }
                                }
                                mkp.yield(" Full Fee: ")
                                FullFee("\$" + noCents.format(cc.feeIncGst?.toBigDecimal()))
                            }
                        }
                        courseHorizontalLine()
                        mkp.yield("\n")
                    }
                }
            }
            addChild(child, xml, courseClasses)
        }
    }
}