package com.tomcoward.heterogeneousfaas.resourcemanager.integrations.callbacks;

import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.BuildResponseItem;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BuildImageCallback extends ResultCallback.Adapter<BuildResponseItem> {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Override
    public void onNext(BuildResponseItem item) {
        LOGGER.log(Level.INFO, String.format("Docker image built: %s", item.getStream()));
    }
}
