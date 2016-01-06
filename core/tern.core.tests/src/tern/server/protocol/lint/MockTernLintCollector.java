/**
 *  Copyright (c) 2013-2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package tern.server.protocol.lint;

import tern.server.protocol.IJSONObjectHelper;
import tern.server.protocol.lint.ITernLintCollector;

public class MockTernLintCollector implements ITernLintCollector {

	@Override
	public void startLint(String file) {

	}

	@Override
	public void addMessage(String messageId, String message, Long start, Long end, Long lineNumber, String severity,
			String file, Object messageObject, IJSONObjectHelper helper) {

	}

	@Override
	public void endLint(String file) {

	}

}
