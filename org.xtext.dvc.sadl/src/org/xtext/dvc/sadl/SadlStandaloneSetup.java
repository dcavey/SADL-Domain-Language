
package org.xtext.dvc.sadl;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class SadlStandaloneSetup extends SadlStandaloneSetupGenerated{

	public static void doSetup() {
		new SadlStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

