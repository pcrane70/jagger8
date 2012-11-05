package com.griddynamics.jagger.webclient.client.viewresults;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.griddynamics.jagger.webclient.client.DefaultActivity;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;

public class ViewResults extends DefaultActivity {
    interface ViewResultsUIBinder extends UiBinder<Widget, ViewResults> {
    }

    private static ViewResultsUIBinder uiBinder = GWT.create(ViewResultsUIBinder.class);

    @UiField
    Widget widget;

    public ViewResults(JaggerResources resources) {
        super(resources);
        createWidget(); // TODO Maybe bind in initializeWidget ?
    }

    private void createWidget() {
        uiBinder.createAndBindUi(this);
    }


    @Override
    protected Widget initializeWidget() {
        return widget;
    }
}
