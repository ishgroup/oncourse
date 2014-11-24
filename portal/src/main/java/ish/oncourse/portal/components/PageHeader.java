package ish.oncourse.portal.components;

import ish.oncourse.services.environment.IEnvironmentService;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class PageHeader {

	@Property
	@Parameter
	private String title;

	@Inject
	private Request request;

	@Inject
	private IEnvironmentService environmentService;

	public String getContextPath() {
		return request.getContextPath();
	}

    public String getCiVersion()
    {
        String ciVersion = environmentService.getCiVersion();
        if (StringUtils.isEmpty(StringUtils.trimToEmpty(ciVersion))) {
            return "r" + ciVersion;
        } else {
          return "dvelopment";
        }
    }

	public String getMetaGeneratorContent() {
		StringBuilder buff = new StringBuilder(
				environmentService.getApplicationName());

		String buildServerID = environmentService.getBuildServerID();
		if (!StringUtils.isEmpty(buildServerID)) {
			buff.append(' ').append(buildServerID);
		}

		String scmVersion = environmentService.getScmVersion();
		if (!StringUtils.isEmpty(scmVersion)) {
			buff.append(StringUtils.isEmpty(buildServerID) ? ' ' : '/');
			buff.append('r');
			buff.append(scmVersion);
		}

		String ciVersion = getCiVersion();
		if (ciVersion != null) {
			buff.append(StringUtils.isEmpty(buildServerID) ? ' ' : '/');
			buff.append(ciVersion);
		}
		return buff.toString();
	}
}
