package ish.oncourse.cayenne;

import java.util.Date;
import java.util.List;

public interface AssessmentClassInterface {

	Date getDueDate();

	Date getReleaseDate();

	CourseClassInterface getCourseClass();

	List<? extends AssessmentClassModuleInterface> getAssessmentClassModules();
}
