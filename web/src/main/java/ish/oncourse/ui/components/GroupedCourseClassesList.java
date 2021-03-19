package ish.oncourse.ui.components;

import ish.math.Money;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Site;
import ish.oncourse.model.TutorRole;
import ish.oncourse.model.auto._Site;
import ish.oncourse.ui.base.ISHCommon;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import static java.util.stream.Collectors.*;
import java.util.*;
import java.util.function.Function;
import java.text.Format;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GroupedCourseClassesList  extends ISHCommon {

    @Parameter
    @Property
    private boolean linkToLocationsMap;

    @Parameter
    @Property
    private Format startDateFormat;

    @Parameter
    @Property
    private boolean allowByAplication;

    @Parameter
    @Property
    private Money feeOverride;

    @SuppressWarnings("all")
    @Parameter
    @Property
    private boolean isList;
    
    @Parameter
    private List<CourseClass> courseClasses;

    @Property
    private Map<Site, List<CourseClass>> sitesMap;

    @Property
    private Map.Entry<Site, List<CourseClass>> siteEntry;

    @Property
    private List<CourseClass> unassignedClasses;
    
    @Property
    private CourseClass courseClass;


    @SetupRender
    public void beforeRender() {
        unassignedClasses =  courseClasses.stream().filter(cc -> cc.getRoom() == null)
                .sorted(Comparator.comparing(CourseClass::getStartDate, Comparator.nullsFirst(Date::compareTo)))
                .collect(Collectors.toList());

        sitesMap = courseClasses.stream().filter(cc -> cc.getRoom() != null)
                .collect(Collectors.groupingBy(cc -> cc.getRoom().getSite(), () -> new TreeMap<>(Comparator.comparing(_Site::getName)), Collectors.toList()));
        
    }
    


}
