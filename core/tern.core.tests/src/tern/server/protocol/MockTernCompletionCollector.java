/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package tern.server.protocol;

import java.util.Collection;
import java.util.HashMap;

import tern.server.ITernServer;
import tern.server.protocol.completions.ITernCompletionCollector;
import tern.server.protocol.completions.TernCompletionItem;

public class MockTernCompletionCollector extends
		HashMap<String, TernCompletionItem> implements ITernCompletionCollector {

	@Override
	public void addProposal(String name, String type, String doc, String url,
			String origin, int pos, Object completion, ITernServer ternServer) {
		super.put(name, new TernCompletionItem(name, type, doc, url, origin));
	}

	public Collection<TernCompletionItem> getCompletions() {
		return super.values();
	}

}
