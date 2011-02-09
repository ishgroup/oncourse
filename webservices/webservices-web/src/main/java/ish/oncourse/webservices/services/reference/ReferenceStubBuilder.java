package ish.oncourse.webservices.services.reference;

import ish.oncourse.webservices.builders.BuilderNotFoundException;
import ish.oncourse.webservices.builders.IReferenceStubBuilder;
import ish.oncourse.webservices.builders.reference.CountryStubBuilder;
import ish.oncourse.webservices.builders.reference.LanguageStubBuilder;
import ish.oncourse.webservices.builders.reference.ModuleStubBuilder;
import ish.oncourse.webservices.builders.reference.QualificationStubBuilder;
import ish.oncourse.webservices.builders.reference.TrainingPackageStubBuilder;
import ish.oncourse.webservices.v4.stubs.reference.ReferenceStub;

import java.util.HashMap;
import java.util.Map;

import org.apache.cayenne.Persistent;

@SuppressWarnings("rawtypes")
public class ReferenceStubBuilder {

	private Map<String, IReferenceStubBuilder> builders = new HashMap<String, IReferenceStubBuilder>();

	public ReferenceStubBuilder() {
		builders.put("Country", new CountryStubBuilder());
		builders.put("Language", new LanguageStubBuilder());
		builders.put("Module", new ModuleStubBuilder());
		builders.put("Qualification", new QualificationStubBuilder());
		builders.put("TrainingPackage", new TrainingPackageStubBuilder());
	}

	/**
	 * 
	 * 
	 * @param record
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ReferenceStub convert(Persistent record) throws BuilderNotFoundException {
		String key = record.getObjectId().getEntityName();
		IReferenceStubBuilder builder = builders.get(key);

		if (builder == null) {
			throw new BuilderNotFoundException("Builder not found during record conversion", key);
		}

		return builder.convert(record);
	}
}