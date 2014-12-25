package com.rivet.app.observer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.rivet.app.core.AdsBuilder;
import com.rivet.app.core.ContentManager;

public class AdsBuilderObserver implements PropertyChangeListener {

	public AdsBuilderObserver(AdsBuilder adsBuilder,
			ContentManager contentManager) {
		adsBuilder.addChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// TODO : work here for ads 
		System.out.println("Property has been changed");
	
	}

}
