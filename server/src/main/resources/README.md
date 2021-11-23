#reports
##correct usage of dsl in report/subreport
1. define report entity
`   <property name="entity" value="CourseClassTutor"/>
`
2. define entity variable (note that datasource field defined by default)
`   <field name="courseClassTutor" class="ish.oncourse.server.cayenne.CourseClassTutor"/>`
3. define any fields that you are going to use
`   <field name="tutor.contact.lastName" class="java.lang.String"/>
   <field name="tutor.contact.phones" class="java.lang.String"/>`
esentially it is relevant paths from entity variable:
   `tutor == courseClassTutor.getTutor()`
4. usage:
   `$F{tutor.contact.lastName}`
or
   `$F{tutor}.contact.lastName`