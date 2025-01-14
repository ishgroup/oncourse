/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.service.impl;

import java.io.IOException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.google.inject.Inject;
import ish.oncourse.server.api.v1.model.ProcessStatusDTO;
import ish.oncourse.server.cluster.ClusteredExecutorManager;

@ServerEndpoint("/v1/job-status/{job-id}")
public class JobStatusCallback {

    @Inject
    ClusteredExecutorManager executorManager;

    @OnOpen
    public void onConnect(Session session, @PathParam("job-id") String id) {
        executorManager.addListener(id, () -> {
            try {
                session.getBasicRemote().sendText("\"" + ProcessStatusDTO.FINISHED + "\"");
                session.close();
            } catch (IOException e) {
            }
        });
    }

    @OnError
    public void onError(Throwable t) throws IOException {
    }

    @OnClose
    public void onClose() {
    }
}
