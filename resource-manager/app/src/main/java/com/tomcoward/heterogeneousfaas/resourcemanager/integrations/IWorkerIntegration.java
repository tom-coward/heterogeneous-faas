package com.tomcoward.heterogeneousfaas.resourcemanager.integrations;

import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import javax.json.JsonObject;

public interface IWorkerIntegration {
    Function createFunction(Function function) throws IntegrationException;
    String invokeFunction(Function function, String functionPayload) throws IntegrationException;
}
