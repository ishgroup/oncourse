package ish.oncourse.admin.pages;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ish.oncourse.admin.services.ntis.INTISUpdater;
import ish.oncourse.model.Module;
import ish.oncourse.model.Qualification;
import ish.oncourse.model.TrainingPackage;

import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class NTIS {
	
	private boolean isUpdate;
	
	@Inject
	private INTISUpdater ntisUpdater;
	
	@Property
	private String dateFrom;
	
	@Property
	private String dateTo;

	@SetupRender
	void setupRender() {
		
	}
	
	@OnEvent(component = "update", value = "selected")
	void onSelectedUpdate() {
		this.isUpdate = true;
	}
	
	@OnEvent(component = "updateForm", value = "success")
	void submitted() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date from = dateFormat.parse(dateFrom);
		Date to = dateFormat.parse(dateTo);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(from);
		
		while (cal.getTime().before(to)) {
			
			Date fromDate = cal.getTime();
			cal.add(Calendar.MONTH, 1);
			Date toDate = cal.getTime();
			if (toDate.after(to)) {
				toDate = to;
			}
			
			ntisUpdater.doUpdate(fromDate, toDate, Module.class);
			ntisUpdater.doUpdate(fromDate, toDate, Qualification.class);
			ntisUpdater.doUpdate(fromDate, toDate, TrainingPackage.class);
		}
	}
}
