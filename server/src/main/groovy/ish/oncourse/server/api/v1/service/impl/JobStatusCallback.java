/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.service.impl;

import com.google.inject.Inject;
import ish.oncourse.server.api.v1.model.ProcessResultDTO;
import ish.oncourse.server.cluster.ClusteredExecutorManager;
import ish.oncourse.server.cluster.ClusteredExecutorUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/v1/job-status/{job-id}")
public class JobStatusCallback {
    private static final Logger logger = LogManager.getLogger();

    @Inject
    ClusteredExecutorManager executorManager;

    @OnMessage
    public void onMessageText(String message) {
        System.out.println("message via websocket:" + message );
    }

    @OnOpen
    public void onConnect(Session session, @PathParam("job-id") String id) {
        executorManager.addListener(id, (taskResult) -> {
            try {
                var processResult = new ProcessResultDTO();
                processResult.setStatus(ClusteredExecutorUtils.statusFrom(taskResult.getType()));
                String message = taskResult.getStatusMessage() != null ? taskResult.getStatusMessage() : taskResult.getError();
                processResult.setMessage(message);
                session.getBasicRemote().sendObject(processResult);
            } catch (IOException | EncodeException e) {
                logger.catching(e);
            } finally {
                try {
                    session.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnError
    public void onError(Throwable t) throws IOException {
        logger.catching(t);
    }

    @OnClose
    public void onClose() {
    }
}
