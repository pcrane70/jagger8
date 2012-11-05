package com.griddynamics.jagger.webclient.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;
import com.griddynamics.jagger.webclient.client.trends.Trends;
import com.griddynamics.jagger.webclient.client.trends.TrendsPlace;
import com.griddynamics.jagger.webclient.client.viewresults.ViewResults;
import com.griddynamics.jagger.webclient.client.viewresults.ViewResultsPlace;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/20/12
 */
public class JaggerActivityMapper implements ActivityMapper {
    private JaggerResources resources;

    public JaggerActivityMapper(JaggerResources resources) {
        this.resources = resources;
    }

    @Override
    public Activity getActivity(Place place) {
        if (place instanceof TrendsPlace) {
            Trends trendsActivity = new Trends(resources);
            trendsActivity.setSessionIds(((TrendsPlace) place).getSessionIds());
            return trendsActivity;
        }
        if (place instanceof ViewResultsPlace) {
            ViewResults viewResults = new ViewResults(resources);
            return viewResults;
        }

        return null;
    }
}
