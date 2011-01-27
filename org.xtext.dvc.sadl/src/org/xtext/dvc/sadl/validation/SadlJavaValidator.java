package org.xtext.dvc.sadl.validation;
 
import java.util.HashMap;
import java.util.Map;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;

import org.xtext.dvc.sadl.sadl.ArchitectureConcept;
import org.xtext.dvc.sadl.sadl.ISService;
import org.xtext.dvc.sadl.sadl.Model;
import org.xtext.dvc.sadl.sadl.SadlPackage;



public class SadlJavaValidator extends AbstractSadlJavaValidator {
	
	public final Logger logger = Logger.getLogger(SadlJavaValidator.class.getSimpleName());

	final Map<URI,Model> allModels = new HashMap<URI,Model>();
	
	@Check() 
	public void checkISServiceStartsWithCapital(ISService service) {
		if (!Character.isUpperCase(service.getName().charAt(0))) {
			error("Name should start with a capital", service, SadlPackage.IS_SERVICE__NAME);
		}
	}

	@Check()
	public void TrackModel(Model thisModel) {
		//Map<URI,Model> allModels = new HashMap<URI,Model>();
		
		logger.debug("Setting off logging NOW !!!! ");
		logger.setLevel(Level.OFF);
		
		if (! allModels.containsKey(thisModel.eResource().getURI()) )
		{
			allModels.put(thisModel.eResource().getURI(), thisModel);
		}
		
		logger.debug ("Number of models identified so far: " + allModels.size() );
				
		for (URI uri: allModels.keySet()  )			// loop over all models
		{
			logger.debug ("Model: " + uri.path().toString() );
			logger.debug("Model: " + uri.path().toString() );
		}
		
	}

	@Check(CheckType.EXPENSIVE)
	public void TrackUsageOfElement(ArchitectureConcept concept) 
	{
		logger.setLevel(Level.WARN);
		if (!allModels.isEmpty() )
		{
			logger.info ("******* Checking usage of: " + concept.getName() +  "**************" ) ;
			if ( ! checkUsageOfOneElementInAllModels(concept, allModels) ) 
			{
				logger.warn ( concept.eClass().getName() + ": " + concept.getName() +  " is not used!" ) ;

				// ALTERNATIVE: Add warning on all concepts
				//warning(concept.eClass().getName() + ": " + concept.getName() +  " is not used!", concept,  SadlPackage.ARCHITECTURE_CONCEPT);
			}  
		}
		logger.setLevel(Level.OFF);
	}

	public boolean checkUsageOfOneElementInAllModels(ArchitectureConcept modelElem , Map<URI,Model> allModels ) 
	{
		boolean used = false;
		
		String modelElemName = ((ArchitectureConcept) ( modelElem)).getName();
		
		for (URI uri: allModels.keySet()  )			// loop over all models
		{
		logger.debug ("Looking in URI: " + uri.path().toString() + " for " + modelElemName ) ;
			
		for( ArchitectureConcept loopArEl : allModels.get(uri).getArchiElements()  )  	// loop over all elements of one model
		{
			logger.debug ("Start looking for cross references of " + modelElemName +  " in refs of " + loopArEl.getName() ) ;
			
			if (! loopArEl.eCrossReferences().isEmpty()) 
			{
				try {
					for (EObject xRef : loopArEl.eCrossReferences() )
					{
						String xRefName = ((ArchitectureConcept)(xRef)).getName();
						logger.debug("?Match attempt: " + xRefName + " with " + modelElemName );					
				
						if ( xRefName.equals(modelElemName)) {			
							logger.debug("Found cross reference of " + xRefName + " in "+ loopArEl.getName());
							used = true;
							break; 				}
					} // end of loop over all cross-references of one element
				}  
				catch ( Exception e ) {
			 		logger.debug("Exception : "+ e.getMessage());				
				}
			} 
		} // end of loop over all for elements of one model
		if (used) { break;} 
		} // end for all models
		
		return used;
		
	}
}


