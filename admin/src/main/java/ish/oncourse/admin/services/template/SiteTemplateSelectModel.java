package ish.oncourse.admin.services.template;

import ish.oncourse.model.WebSite;
import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class SiteTemplateSelectModel extends AbstractSelectModel {

    private List<WebSite> templates;

    @Override
    public List<OptionGroupModel> getOptionGroups() {
        return null;
    }

    @Override
    public List<OptionModel> getOptions() {
        List<OptionModel> optionModels = new ArrayList<>(
                templates.size());

        for (WebSite template : templates) {
            optionModels.add(new OptionModelImpl(template.getSiteKey(), template));
        }

        return optionModels;
    }

    public static SiteTemplateSelectModel valueOf(List<WebSite> templates) {
        SiteTemplateSelectModel result = new SiteTemplateSelectModel();
        result.templates = templates;
        return result;
    }
}
