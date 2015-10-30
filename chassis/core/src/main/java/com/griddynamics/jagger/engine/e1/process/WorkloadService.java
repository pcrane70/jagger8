package com.griddynamics.jagger.engine.e1.process;

import com.griddynamics.jagger.util.concurrent.Service;

public interface WorkloadService extends Service {

    Integer getStartedSamples();
    Integer getFinishedSamples();
    void changeDelay(int delay);
}
