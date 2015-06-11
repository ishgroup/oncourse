package ish.oncourse.cms.webdav;

import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.util.ContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class RunEditScript {
	private static final Logger logger = LogManager.getLogger();
	private static final int EDIT_FILE_SCRIPT_WAIT_TIMEOUT = 15;


	private File file;
	private IAuthenticationService authenticationService;
	private ExecutorService executorService;

	public void run() {
		String scriptPath = ContextUtil.getCmsEditScriptPath();

		if (StringUtils.trimToNull(scriptPath) == null) {
			logger.error("Edit file script is not defined! Resources haven't been updated!");
			return;
		}

		List<String> scriptCommand = new ArrayList<>();

		String filePath = file.getAbsolutePath();

		scriptCommand.add(scriptPath);
		scriptCommand.add("-p");
		scriptCommand.add(String.format("\"%s\"", filePath));

		String userEmail = authenticationService.getUserEmail();

		if (userEmail != null) {
			scriptCommand.add("-e");
			scriptCommand.add(userEmail);
		}

		ProcessBuilder processBuilder = new ProcessBuilder(scriptCommand);

		try {
			logger.debug("Starting script '{}' for file '{}'", scriptPath, filePath);
			long time = System.currentTimeMillis();
			final Process process = processBuilder.start();

			Future<Integer> scriptCallFuture = executorService.submit(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					return process.waitFor();
				}
			});

			scriptCallFuture.get(EDIT_FILE_SCRIPT_WAIT_TIMEOUT, TimeUnit.SECONDS);
			time = Math.round((System.currentTimeMillis() - time) / 1000.0);
			logger.debug("script '{}' for file '{}' is finished. Time: '{}' sec", scriptPath, filePath, time);
		} catch (Exception e) {
			logger.error("Error executing script '{}' for file '{}'", scriptPath, filePath, e);
		}
	}

	public static RunEditScript valueOf(File file,
	                                    IAuthenticationService authenticationService,
	                                    ExecutorService executorService) {
		RunEditScript runEditScript = new RunEditScript();
		runEditScript.file = file;
		runEditScript.authenticationService = authenticationService;
		runEditScript.executorService = executorService;
		return runEditScript;
	}


}
