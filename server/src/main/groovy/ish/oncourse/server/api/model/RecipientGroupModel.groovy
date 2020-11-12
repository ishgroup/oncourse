/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.model

class RecipientGroupModel {
    List<Long> suitableForSend = new ArrayList<>()
    List<Long> suppressToSend = new ArrayList<>()
    List<Long> withoutDestination = new ArrayList<>()

    List<Long> getSuitableForSend() {
        return suitableForSend
    }

    void addSuitableToSend(List<Long> suitableToSend) {
        this.suitableForSend.addAll(suitableToSend)
    }

    List<Long> getSuppressToSend() {
        return suppressToSend
    }

    void addSuppressToSend(List<Long> suppressToSend) {
        this.suppressToSend.addAll(suppressToSend)
    }

    List<Long> getWithoutDestination() {
        return withoutDestination
    }

    void addWithoutDestination(List<Long> withoutDestination) {
        this.withoutDestination.addAll(withoutDestination)
    }
}
