package com.tomcoward.heterogeneousfaas.resourcemanager.integrations.callbacks;

import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.PushResponseItem;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PushImageCallback extends ResultCallback.Adapter<PushResponseItem> {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Override
    public void onNext(PushResponseItem item) {
        LOGGER.log(Level.INFO, String.format("Docker image pushed: %s", item.getStream()));
    }
}
