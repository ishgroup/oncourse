package ish.oncourse.portal.components.courseclass;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Site;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;


public class ClassLocation {


    @Parameter
    private CourseClass courseClass;

    private  Site site;

    @Property
    private Boolean hasCoordinates;

    @Property
    private double mapPositionLatitude;

    @Property
    private double mapPositionLongitude;

    @SetupRender
    public void beforeRender() {
		hasCoordinates = true;
        site= courseClass.getRoom().getSite();
        setupMapPosition();
    }



    public void setupMapPosition() {
        /**
         * if at least one site has coordinate  we should show google map.
         */
         if (site.getLatitude() != null && site.getLongitude() != null) {
            mapPositionLatitude = site.getLatitude().doubleValue();
            mapPositionLongitude = site.getLongitude().doubleValue();

            }else
			 hasCoordinates=false;

     }
}


