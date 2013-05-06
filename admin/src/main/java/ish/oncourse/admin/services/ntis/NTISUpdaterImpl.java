package ish.oncourse.admin.services.ntis;

import au.gov.training.services.organisation.IOrganisationService;
import ish.oncourse.admin.services.ntis.organisation.OrganisationNTISUpdater;
import ish.oncourse.admin.services.ntis.trainingcomponent.ModuleNTISUpdater;
import ish.oncourse.admin.services.ntis.trainingcomponent.QualificationNTISUpdater;
import ish.oncourse.admin.services.ntis.trainingcomponent.TrainingPackageNTISUpdater;
import ish.oncourse.model.Module;
import ish.oncourse.model.Organisation;
import ish.oncourse.model.Qualification;
import ish.oncourse.model.TrainingPackage;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.reference.ReferenceService;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.datacontract.schemas._2004._07.system.DateTimeOffset;

import au.gov.training.services.trainingcomponent.ITrainingComponentService;

public class NTISUpdaterImpl implements INTISUpdater {

	@Inject
	private ITrainingComponentService trainingService;

	@Inject
	private IOrganisationService organisationService;

	@Inject
	private ReferenceService referenceService;

	@Inject
	private ICayenneService cayenneService;

	/**
	 * 
	 * @see ish.oncourse.admin.services.ntis.INTISUpdater#doUpdate(java.util.Date, java.util.Date, Class)
	 */
	@Override
	public synchronized NTISResult doUpdate(Date from, Date to, Class<?> type) throws NTISException {

		try {

			GregorianCalendar cal = new GregorianCalendar();

			DateTimeOffset fromDate = new DateTimeOffset();
			DateTimeOffset toDate = new DateTimeOffset();

			cal.setTime(from);
			fromDate.setDateTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal));
			cal.setTime(to);
			cal.add(Calendar.HOUR_OF_DAY, 23);
			cal.add(Calendar.MINUTE, 59);
			toDate.setDateTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal));

			if (type == Qualification.class) {
				return new QualificationNTISUpdater(
						trainingService,
						cayenneService,
						referenceService,
						fromDate,
						toDate).doUpdate();
			} else if (type == TrainingPackage.class) {
				return new TrainingPackageNTISUpdater(
						trainingService,
						cayenneService,
						referenceService,
						fromDate,
						toDate).doUpdate();
			} else if (type == Module.class) {
				return new ModuleNTISUpdater(
						trainingService,
						cayenneService,
						referenceService,
						fromDate,
						toDate).doUpdate();
			} else if (type == Organisation.class) {
				return new OrganisationNTISUpdater(
						organisationService,
						cayenneService,
						referenceService,
						fromDate,
						toDate).doUpdate();
			}

		} catch (DatatypeConfigurationException e) {
			throw new NTISException();
		}

		return null;

	}

}
