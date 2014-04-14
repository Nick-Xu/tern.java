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
package tern.eclipse.ide.tools.internal.ui.wizards.webbrowser;

import org.eclipse.jface.viewers.ISelection;

import tern.eclipse.ide.tools.core.generator.IGenerator;
import tern.eclipse.ide.tools.core.webbrowser.orion.HTMLOrionEditor;
import tern.eclipse.ide.tools.core.webbrowser.orion.OrionOptions;
import tern.eclipse.ide.tools.internal.ui.TernToolsUIMessages;
import tern.eclipse.ide.tools.internal.ui.wizards.NewFileWizard;
import tern.eclipse.ide.tools.internal.ui.wizards.NewFileWizardPage;

/**
 * Wizard to create HTML page with Tern and Orion editor.
 */
public class NewOrionWizard extends NewFileWizard<OrionOptions> {

	public NewOrionWizard() {
		super.setWindowTitle(TernToolsUIMessages.NewOrionWizard_windowTitle);
		// super.setDefaultPageImageDescriptor(ImageResource
		// .getImageDescriptor(ImageResource.IMG_NEWTYPEDEF_WIZ));
	}

	@Override
	protected NewFileWizardPage createNewFileWizardPage(OrionOptions options,
			ISelection selection) {
		return new NewOrionWizardPage(options, selection);
	}

	@Override
	protected IGenerator getGenerator(String lineSeparator) {
		return HTMLOrionEditor.create(lineSeparator);
	}

	@Override
	protected OrionOptions createOptions() {
		return new OrionOptions();
	}

}