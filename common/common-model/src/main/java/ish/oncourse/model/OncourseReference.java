package ish.oncourse.model;

import ish.oncourse.model.auto._OncourseReference;

public class OncourseReference extends _OncourseReference {

    private static OncourseReference instance;

    private OncourseReference() {}

    public static OncourseReference getInstance() {
        if(instance == null) {
            instance = new OncourseReference();
        }

        return instance;
    }
}
