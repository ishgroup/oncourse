package ish.oncourse.model;

import ish.common.types.SSOProviderType;
import ish.oncourse.model.auto._User;

public class User extends _User {
    
    public UserAccount getAccount(SSOProviderType type) {
        return getAccounts().stream()
                .filter(account -> account.getType().equals(type))
                .findFirst().orElseGet(() -> {
                    UserAccount account = getObjectContext().newObject(UserAccount.class);
                    account.setUser(this);
                    account.setType(type);
                    return account;
            });
    }
}
