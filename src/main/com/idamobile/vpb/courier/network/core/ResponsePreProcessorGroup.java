package com.idamobile.vpb.courier.network.core;

import java.util.HashSet;
import java.util.Set;

public class ResponsePreProcessorGroup implements ResponseDTOPreProcessor {

    private Set<ResponseDTOPreProcessor> processors = new HashSet<ResponseDTOPreProcessor>();

    public void addProcessor(ResponseDTOPreProcessor processor) {
        processors.add(processor);
    }

    public void removeProcessor(ResponseDTOPreProcessor processor) {
        processors.remove(processor);
    }

    @Override
    public void processResponse(ResponseDTO<?> response) {
        for (ResponseDTOPreProcessor processor : processors) {
            processor.processResponse(response);
        }
    }
}
