/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.localization.country;

import io.bootique.annotation.BQConfigProperty;
import ish.math.Country;

import java.util.Arrays;
import java.util.Optional;

public class LocalizationService {

    // todo remove default value
    private Country country = Country.AUSTRALIA;

    @BQConfigProperty
    public void setCountry(String countryCode) {
        var configCountry = Arrays
                .stream(Country.values()).filter(
                        country -> country.locale().getISO3Country().equals(countryCode)
                )
                .findFirst();
        configCountry.ifPresent(value -> this.country = value);
    }

    public Optional<Country> getCountry() {
        return Optional.ofNullable(country);
    }
}
