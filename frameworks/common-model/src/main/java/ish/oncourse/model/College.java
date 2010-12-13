package ish.oncourse.model;

import ish.oncourse.model.auto._College;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class College extends _College {
	
	public Integer getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? ((Number) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN)).intValue()
				: null;
	}

	public Set<String> getCollegeSiteStates() {
		Set<String> states = new HashSet<String>();
		
		for (Site site : getSites()) {
			if (site.getState() != null) {
				states.add(site.getState());
			}
		}
		
		return states;
	}
	
	public List<ConcessionType> getActiveConcessionTypes(){
		List<ConcessionType> activeConcessionTypes=new ArrayList<ConcessionType>();
		for(ConcessionType concessionType: getConcessionTypes()){
			if(concessionType.getIsConcession()&&concessionType.getIsEnabled()){
				activeConcessionTypes.add(concessionType);
			}
		}
		return activeConcessionTypes;
	}
}
