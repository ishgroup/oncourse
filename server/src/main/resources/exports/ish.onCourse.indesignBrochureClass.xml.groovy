// We need to create a new builder here with an IndentPrinter that doesn't indent or add new lines
// This is because inDesign is sensitive to where those new lines are added
import groovy.xml.MarkupBuilder
import ish.common.types.NodeSpecialType

xml = new MarkupBuilder(new IndentPrinter(output, "", false, false))

xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

xml.brochure {
	courseClasses = records.groupBy { CourseClass cc -> cc.course }
	subj = courseClasses.keySet().collectMany { Course co -> co.tags }.findAll { Tag t -> NodeSpecialType.SUBJECTS.equals(t.getRoot().specialType) }.unique()[0]?.getRoot()
	if (subj) {
		"Tag"(subj.name) {
            mkp.yield("\n")
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
					"Course"() {
						CourseName(co.name)
                        mkp.yield("\n")
						Description(co.printedBrochureDescription)
						classes.each { CourseClass cc ->
							"Class"() {
								cc.tutorRoles.each { CourseClassTutor tr ->
									"Tutor"(tr.tutor.fullName)
								}
                                mkp.yield("\n")
								ClassCode(co.code + "-" + cc.code)
                                mkp.yield("\t")
								"Site"(cc.room?.site?.name)
								if (!cc.sessions.isEmpty()) {
									mkp.yield("\n")
									mkp.yield(cc.startDateTime?.format("EEE d MMMMM hh:mma") + "-" + cc.endDateTime?.format("hh:mma"))
									mkp.yield("\n")
									mkp.yield(cc.sessions.size().toString() + " sessions of")
									AverageHours(cc.getFirstSession().durationInHours.setScale(1, java.math.RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + (cc.getFirstSession().durationInHours.compareTo(BigDecimal.ONE) > 1 ? " hrs" : " hr"))

								}
								Price(cc.feeIncGst?.toPlainString())
							}
						}
                        mkp.yield("\n")
						courseHorizontalLine()
					}
				}
			}
			addChild(child, xml, courseClasses)
		}
	}
}
