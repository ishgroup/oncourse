package ish.oncourse.services.location;

import ish.oncourse.model.PostcodeDb;

import java.util.List;

public interface IPostCodeDbService {
	List<PostcodeDb> findBySuburb(String state, String... suburbs);
	List<PostcodeDb> getAllPostcodes();
}
