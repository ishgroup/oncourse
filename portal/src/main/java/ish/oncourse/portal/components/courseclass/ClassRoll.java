package ish.oncourse.portal.components.courseclass;

import ish.oncourse.model.*;



import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

import java.util.ArrayList;
import java.util.List;

/**
 * User: artem
 * Date: 10/16/13
 * Time: 3:22 PM
 */
public class ClassRoll {

    @Property
    @Parameter
    private CourseClass courseClass;

    @Property
    private Student student;

    @Property
    private List<Student> students;


    @SetupRender
    boolean setupRender() {
        courseClass.getMaximumPlaces();
        courseClass.getMinimumPlaces();

        List<Enrolment> enrolments = courseClass.getValidEnrolments();
        students = new ArrayList<>();

        for(Enrolment enrolment : enrolments )
        students.add(enrolment.getStudent());

        return true;
    }


    public int getNumberOfStudents(){
        return courseClass.validEnrolmentsCount();
    }
}
