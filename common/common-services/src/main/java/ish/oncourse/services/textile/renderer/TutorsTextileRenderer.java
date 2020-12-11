package ish.oncourse.services.textile.renderer;

import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.TutorsTextileAttributes;
import ish.oncourse.services.textile.validator.TutorsTextileValidator;
import ish.oncourse.util.IPageRenderer;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class TutorsTextileRenderer extends AbstractRenderer {

    private IPageRenderer pageRenderer;

    public TutorsTextileRenderer(IPageRenderer pageRenderer) {
        this.pageRenderer = pageRenderer;
        validator = new TutorsTextileValidator();
    }

    @Override
    protected String internalRender(String tag) {
        Map<String, String> tagParams = TextileUtil.getTagParams(tag,
                TutorsTextileAttributes.getAttrValues());

        String tagName = tagParams.get(TutorsTextileAttributes.TAG_NAME.getValue());
        String id = tagParams.get(TutorsTextileAttributes.ID.getValue());
        String count = tagParams.get(TutorsTextileAttributes.COUNT.getValue());

        Map<String, Object> parameters = new HashMap<>();
        
        if (StringUtils.trimToNull(tagName) != null) {
            parameters.put(TextileUtil.TEXTILE_TUTORS_TAG_NAME_PARAM, tagName);
        }
        if (StringUtils.trimToNull(id) != null) {
            parameters.put(TextileUtil.TEXTILE_TUTORS_TAG_NAME_PARAM, Long.valueOf(id));
        }
        if (StringUtils.trimToNull(count) != null) {
            parameters.put(TextileUtil.TEXTILE_TUTORS_TAG_NAME_PARAM, Integer.valueOf(count));
        }


        tag = pageRenderer.renderPage(TextileUtil.TEXTILE_TUTORS_PAGE, parameters);
        return tag;
    }
}
