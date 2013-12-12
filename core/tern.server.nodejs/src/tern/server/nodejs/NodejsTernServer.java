/*******************************************************************************
 * Copyright (c) 2013 Angelo ZERR.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:      
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *******************************************************************************/
package tern.server.nodejs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import tern.TernException;
import tern.TernProject;
import tern.server.AbstractTernServer;
import tern.server.IResponseHandler;
import tern.server.ITernDef;
import tern.server.ITernPlugin;
import tern.server.nodejs.process.NodejsProcess;
import tern.server.nodejs.process.NodejsProcessListener;
import tern.server.nodejs.process.NodejsProcessListenerAdapter;
import tern.server.nodejs.process.NodejsProcessManager;
import tern.server.protocol.TernDoc;
import tern.server.protocol.completions.ITernCompletionCollector;

/**
 * Tern server implemented with node.js
 * 
 */
public class NodejsTernServer extends AbstractTernServer {

	private final TernProject project;

	private String baseURL;

	private List<IInterceptor> interceptors;

	private NodejsProcess process;
	private List<NodejsProcessListener> listeners;

	private long timeout = 1000;

	private final NodejsProcessListener listener = new NodejsProcessListenerAdapter() {

		@Override
		public void onStop(NodejsProcess server) {
			NodejsTernServer.this.baseURL = null;
			NodejsTernServer.this.process = null;
		}

	};

	public NodejsTernServer(File projectDir, int port) {
		this(new TernProject(projectDir), port);
	}

	public NodejsTernServer(TernProject project, int port) {
		this.project = project;
		this.baseURL = computeBaseURL(port);
	}

	public NodejsTernServer(TernProject project) {
		this(project, NodejsProcessManager.getInstance().create(
				project.getProjectDir()));
	}

	public NodejsTernServer(TernProject project, NodejsProcess process) {
		this.project = project;
		this.process = process;
		process.addProcessListener(listener);
	}

	public NodejsTernServer(TernProject project, File installPath) {
		this(project, NodejsProcessManager.getInstance().create(
				project.getProjectDir(), installPath));
	}

	private String computeBaseURL(Integer port) {
		return new StringBuilder("http://localhost:").append(port).append("/")
				.toString();
	}

	@Override
	public void addDef(ITernDef def) throws TernException {
		project.addLib(def.getName());
		try {
			project.save();
		} catch (IOException e) {
			throw new TernException(e);
		}
	}

	@Override
	public void addPlugin(ITernPlugin plugin) throws TernException {
		project.addPlugin(plugin);
		try {
			project.save();
		} catch (IOException e) {
			throw new TernException(e);
		}
	}

	@Override
	public void addFile(String name, String text) {
		TernDoc t = new TernDoc();
		t.addFile(name, text, null);
		try {
			JSONObject json = makeRequest(t);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void request(TernDoc doc, IResponseHandler handler) {
		try {
			JSONObject json = makeRequest(doc);
			handler.onSuccess(json,
					handler.isDataAsJsonString() ? json.toJSONString() : null);
		} catch (Exception e) {
			handler.onError(e.getMessage());
		}
	}

	private JSONObject makeRequest(TernDoc doc) throws IOException,
			InterruptedException {
		JSONObject json = TernProtocolHelper.makeRequest(getBaseURL(), doc,
				false, interceptors, this);
		return json;
	}

	public TernProject getProject() {
		return project;
	}

	public void addInterceptor(IInterceptor interceptor) {
		if (interceptors == null) {
			interceptors = new ArrayList<IInterceptor>();
		}
		interceptors.add(interceptor);
	}

	public String getBaseURL() throws InterruptedException, IOException {
		if (baseURL == null) {
			int port = getProcess().start(timeout);
			this.baseURL = computeBaseURL(port);
		}
		return baseURL;
	}

	private NodejsProcess getProcess() {
		if (process != null) {
			return process;
		}
		process = NodejsProcessManager.getInstance().create(
				project.getProjectDir());
		process.addProcessListener(listener);
		return process;
	}

	public void addProcessListener(NodejsProcessListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<NodejsProcessListener>();
		}
		listeners.add(listener);
		if (process != null) {
			process.addProcessListener(listener);
		}
	}

	public void removeProcessListener(NodejsProcessListener listener) {
		if (listeners != null && listener != null) {
			listeners.remove(listener);
		}
		if (process != null) {
			process.removeProcessListener(listener);
		}
	}

	@Override
	public void request(TernDoc doc, ITernCompletionCollector collector)
			throws TernException {
		try {
			JSONObject jsonObject = makeRequest(doc);
			if (jsonObject != null) {
				Long startCh = getCh(jsonObject, "start");
				Long endCh = getCh(jsonObject, "end");
				int pos = 0;
				if (startCh != null && endCh != null) {
					pos = endCh.intValue() - startCh.intValue();
				}
				List completions = (List) jsonObject.get("completions");
				for (Object object : completions) {
					addProposal((JSONObject) object, pos, collector);
				}
			}
		} catch (Throwable e) {
			throw new TernException(e);
		}
	}

	protected void addProposal(JSONObject completion, int pos,
			ITernCompletionCollector collector) {
		String name = completion.get("name").toString();
		String type = completion.get("type").toString();
		Object doc = completion.get("doc");
		collector.addProposal(name, type, doc, pos);
	}

	private Long getCh(JSONObject data, String pos) {
		JSONObject loc = (JSONObject) data.get(pos);
		return loc != null ? (Long) loc.get("ch") : null;
	}

	@Override
	public void dispose() {
		super.dispose();
		if (process != null) {
			process.kill();
		}
	}

}