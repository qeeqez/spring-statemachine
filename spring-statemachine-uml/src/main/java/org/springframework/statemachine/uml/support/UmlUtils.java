/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.statemachine.uml.support;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.BodyOwner;
import org.eclipse.uml2.uml.Event;
import org.eclipse.uml2.uml.FinalState;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Pseudostate;
import org.eclipse.uml2.uml.PseudostateKind;
import org.eclipse.uml2.uml.Signal;
import org.eclipse.uml2.uml.SignalEvent;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Trigger;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;

/**
 * Utilities for uml model processing.
 *
 * @author Janne Valkealahti
 */
public abstract class UmlUtils {

	/**
	 * Gets the model.
	 *
	 * @param modelPath the model path
	 * @return the model
	 */
	public static Model getModel(String modelPath) {
		URI modelUri = URI.createFileURI(modelPath);
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
		resourceSet.createResource(modelUri);
		Resource resource = resourceSet.getResource(modelUri, true);
		Model m = (Model) EcoreUtil.getObjectByType(resource.getContents(), UMLPackage.Literals.MODEL);
		return m;
	}

	/**
	 * Checks if {@link State} is an initial state by checking if
	 * it has incoming transition from UML's initial literal.
	 *
	 * @param state the state
	 * @return true, if is initial state
	 */
	public static boolean isInitialState(State state) {
		for (Transition t : state.getIncomings()) {
			if (t.getSource() instanceof Pseudostate) {
				if (((Pseudostate)t.getSource()).getKind() == PseudostateKind.INITIAL_LITERAL) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if {@link State} is a final state.
	 *
	 * @param state the state
	 * @return true, if is final state
	 */
	public static boolean isFinalState(State state) {
		return state instanceof FinalState;
	}

	/**
	 * Resolve body by language.
	 *
	 * @param language the language
	 * @param owner the owner
	 * @return the body or null if not found
	 */
	public static String resolveBodyByLanguage(String language, BodyOwner owner) {
		try {
			return owner.getBodies().get(owner.getLanguages().indexOf(language)).trim();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Resolve dererred events from a state.
	 *
	 * @param state the state
	 * @return the collection of deferred events
	 */
	public static Collection<String> resolveDererredEvents(State state) {
		ArrayList<String> events = new ArrayList<String>();
		for (Trigger trigger : state.getDeferrableTriggers()) {
			Event event = trigger.getEvent();
			if (event instanceof SignalEvent) {
				Signal signal = ((SignalEvent)event).getSignal();
				events.add(signal.getName());
			}
		}
		return events;
	}
}
