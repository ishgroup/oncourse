package ish.oncourse.willow.service;

import ish.oncourse.willow.model.CourseClass;
import ish.oncourse.willow.model.CourseClassesParams;
import ish.oncourse.willow.model.Error;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for CourseClassesApi
 */
public class CourseClassesApiTest {

    private CourseClassesApi api;
    
    @Before
    public void setup() {
    }

    
    /**
     * Get list of CourseClasses
     * Get list of course classes based on current shopping cart state
     */
    @Test
    public void getCourseClassesTest() {
        CourseClassesParams courseClassesParams = null;
	//List<CourseClass> response = api.getCourseClasses(courseClassesParams);
        //assertNotNull(response);
        // TODO: test validations
        
        
    }
    
}
