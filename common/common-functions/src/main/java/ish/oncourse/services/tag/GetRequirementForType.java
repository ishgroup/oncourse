package ish.oncourse.services.tag;

import ish.oncourse.model.Tag;
import ish.oncourse.model.TagGroupRequirement;

public class GetRequirementForType {

    private Tag tag;
    private String entityIdentifier;

    private GetRequirementForType() {}

    public static GetRequirementForType valueOf(Tag tag, String entityIdentifier) {
        GetRequirementForType obj = new GetRequirementForType();
        obj.tag = tag;
        obj.entityIdentifier = entityIdentifier;
        return obj;
    }

    public TagGroupRequirement get() {
        TagGroupRequirement res = null;
        if (tag != null && entityIdentifier != null && tag.getTagGroupRequirements() != null) {
            res = tag.getTagGroupRequirements().stream()
                    .filter(rq -> entityIdentifier.equals(rq.getEntityIdentifier()))
                    .findFirst()
                    .orElse(null);
        }
        return res;
    }
}
