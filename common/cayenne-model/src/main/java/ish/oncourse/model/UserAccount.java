package ish.oncourse.model;

import ish.oncourse.model.auto._UserAccount;

public class UserAccount extends _UserAccount {



    public UserAccountProperty getAccountProperty(String name) {
        return getAccountProperties().stream()
                .filter(property -> property.getName().equals(name))
                .findFirst()
                .orElseGet(()-> {
            UserAccountProperty property = getObjectContext().newObject(UserAccountProperty.class);
            property.setAccount(this);
            property.setName(name);
            return property;
        });
    }


    public void setProperty(String name, String value) {
        UserAccountProperty prop = getAccountProperty(name);
        prop.setValue(value);
    }
}
