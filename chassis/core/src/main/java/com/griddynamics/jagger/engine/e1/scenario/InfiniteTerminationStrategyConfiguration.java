package com.griddynamics.jagger.engine.e1.scenario;

public class InfiniteTerminationStrategyConfiguration implements  TerminateStrategyConfiguration {
    @Override
    public TerminationStrategy getTerminateStrategy() {
        return new TerminationStrategy() {
            @Override
            public boolean isTerminationRequired(WorkloadExecutionStatus status) {
                return false;
            }
        };
    }
}
