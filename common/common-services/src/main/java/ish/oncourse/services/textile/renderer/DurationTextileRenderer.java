package ish.oncourse.services.textile.renderer;

import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.DurationTextileAttribute;
import ish.oncourse.services.textile.validator.DurationTextileValidator;
import ish.oncourse.util.IPageRenderer;

import java.util.HashMap;
import java.util.Map;

public class DurationTextileRenderer extends AbstractRenderer {


    private IPageRenderer pageRenderer;

    public DurationTextileRenderer(IPageRenderer pageRenderer) {
        this.pageRenderer = pageRenderer;
        validator = new DurationTextileValidator();
    }

    @Override
    protected String internalRender(String tag) {
        Map<String, String> tagParams = TextileUtil.getTagParams(tag,
                DurationTextileAttribute.getAttrValues());
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(TextileUtil.TEXTILE_DURATION_PAGE_PARAM, tagParams);
        tag = pageRenderer.renderPage(TextileUtil.TEXTILE_DURATION_PAGE, parameters);
        return tag;
    }
}
