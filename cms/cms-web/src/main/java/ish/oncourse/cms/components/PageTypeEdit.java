package ish.oncourse.cms.components;

import ish.oncourse.model.RegionKey;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.selectutils.StringSelectModel;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.PrivateResource;

import java.io.File;
import java.io.FilenameFilter;
import java.util.SortedSet;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

public class PageTypeEdit {

	@Parameter(required = true)
	@Property
	private WebNodeType pageType;

	@Property
	@Persist
	private WebNodeType editPageType;

	@Parameter
	@Property
	private Zone updateZone;

	@Inject
	private ICayenneService cayenneService;

	@Property
	@Persist
	private SelectModel layoutSelectModel;

	@Inject
	private IResourceService resourceService;

	@Property
	private RegionKey regionKey;
		
	public SortedSet<WebContent> getBlocksForRegionKey() {
		return null;
	}
	
	public RegionKey[] getRegionKeys() {
		return RegionKey.values();
	}

	public String getBlockIds() {
		StringBuilder str = new StringBuilder();
		RegionKey[] regionKeys = getRegionKeys();

		for (int i = 0; i < regionKeys.length; i++) {
			RegionKey block = regionKeys[i];
			str.append("#b_").append(block.name());
			if (i != regionKeys.length - 1) {
				str.append(",");
			}
		}

		return str.toString();
	}

	@SetupRender
	public void beforeRender() {
		this.editPageType = pageType;
		this.layoutSelectModel = new StringSelectModel(readAvailableLayouts());
	}

	Object onSuccessFromPageTypeEditForm() {
		cayenneService.sharedContext().commitChanges();
		return updateZone.getBody();
	}

	Object onActionFromCancel() {
		return updateZone.getBody();
	}

	Object onActionFromDelete() {

		cayenneService.sharedContext().deleteObject(editPageType);
		cayenneService.sharedContext().commitChanges();

		return updateZone.getBody();
	}

	private String[] readAvailableLayouts() {
		PrivateResource res = resourceService.getTemplateResource("", "");

		File dir = res.getFile();

		return dir.list(new FilenameFilter() {
			public boolean accept(File arg, String arg1) {
				File f = new File(arg.getAbsolutePath() + "/" + arg1);
				return f.isDirectory() && !(arg1.charAt(0) == '.');
			}
		});
	}
}
