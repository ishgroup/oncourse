package ish.oncourse.ui.components;

import ish.oncourse.model.*;
import ish.oncourse.services.IRichtextConverter;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.html.IPlainTextExtractor;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.utils.StringUtilities;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.*;
import java.util.stream.Collectors;

public class TutorsDetails extends ISHCommon {
    
    @Inject
    private IPlainTextExtractor extractor;
    
    @Inject
    private IBinaryDataService binaryDataService;

    @Inject
    private ICayenneService cayenneService;

    @Inject
    private IWebSiteService webSiteService;

    @Inject
    private IRichtextConverter textileConverter;
    
    @Parameter
    private Course course;

    @Parameter
    private CourseClass courseClass;

    @Parameter
    private Long tutorId;

    @Parameter
    private String tagName;

    @Parameter
    private Integer count;

    @Property
    private Set<Tutor> tutors;

    @Property
    private Tutor tutor;

    @SetupRender
    void beginRender() {
        //Count can be no higher than 12 and defaults to 3 if no count is provided.
        if (count == null || count < 1) {
            count = 3;
        }
        List<Tutor> tutorList;
        if (tutorId != null) {
            tutorList = ObjectSelect.query(Tutor.class)
                    .where(Tutor.ANGEL_ID.eq(tutorId))
                    .and(Tutor.COLLEGE.eq(webSiteService.getCurrentCollege()))
                    .select(cayenneService.newContext());
        } else if (course != null) {
            tutorList = course.getTutors();
        } else if (courseClass != null) {
            tutorList = courseClass.getTutors();
        } else if (tagName != null) {
            tutorList = getTutorsByTag(tagName, count);
        } else {
            tutorList = ObjectSelect.query(Tutor.class)
                    .where(Tutor.COLLEGE.eq(webSiteService.getCurrentCollege()))
                    .and(Tutor.FINISH_DATE.isNotNull().orExp(Tutor.FINISH_DATE.gt(new Date())))
                    .limit(count)
                    .select(cayenneService.newContext());
        }
        
        if (!tutorList.isEmpty()) {
            tutors = tutorList.stream().sorted(Comparator.comparing(a -> a.getContact().getFamilyName())).collect(Collectors.toCollection(LinkedHashSet::new));
        } else {
            tutors = new HashSet<>();
        }

    }
    
    private List<Tutor> getTutorsByTag(String tagName, Integer count) {
        List<Long> ids = ObjectSelect.query(Taggable.class)
                .where(Taggable.ENTITY_IDENTIFIER.eq("Contact"))
                .and(Taggable.COLLEGE.eq(webSiteService.getCurrentCollege()))
                .and(Taggable.TAGGABLE_TAGS.dot(TaggableTag.TAG).dot(Tag.NAME).eq(tagName))
                .column(Taggable.ENTITY_WILLOW_ID).select(cayenneService.newContext());

        if (ids.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> randomIds = new ArrayList<>();
        Integer size = ids.size();

        if (count >= size) {
            randomIds = ids;
        } else {
            Random ran = new Random();
            while (count > 0) {
                int index = ran.nextInt(size);
                randomIds.add(ids.get(index));
                count--;
            }

        }
        return ObjectSelect.query(Contact.class)
                .where(ExpressionFactory.inDbExp(Contact.ID_PK_COLUMN, randomIds))
                .and(Contact.TUTOR.isNotNull())
                .select(cayenneService.newContext())
                .stream().map(Contact::getTutor)
                .collect(Collectors.toList());
    }
    
    public String getProfilePictureUrl() {
       return binaryDataService.getProfilePictureUrl(tutor.getContact());
    }

    public String getResume() {
        String result = null;
        String resume = StringUtils.trimToNull(tutor.getResume());
        if (resume != null) {
            result = textileConverter.convertCustomText(resume, new ValidationErrors());
        } else {
            result = String.format("%s. Tutor of %s", tutor.getFullName(), webSiteService.getCurrentCollege().getName());
        }
        if (result.length() > 200) {
            return StringUtilities.abbreviate(extractor.extractFromHtml(result), 200);
        } else {
            return result;
        }
    }
}
