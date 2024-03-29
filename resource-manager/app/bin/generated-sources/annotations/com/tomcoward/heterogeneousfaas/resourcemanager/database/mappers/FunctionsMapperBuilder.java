package com.tomcoward.heterogeneousfaas.resourcemanager.database.mappers;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.mapper.MapperBuilder;
import com.datastax.oss.driver.internal.mapper.DefaultMapperContext;
import java.lang.Override;
import java.lang.SuppressWarnings;

/**
 * Builds an instance of {@link FunctionsMapper} wrapping a driver {@link CqlSession}.
 *
 * <p>Generated by the DataStax driver mapper, do not edit directly.
 */
@SuppressWarnings("all")
public class FunctionsMapperBuilder extends MapperBuilder<FunctionsMapper> {
  public FunctionsMapperBuilder(CqlSession session) {
    super(session);
  }

  @Override
  public FunctionsMapper build() {
    DefaultMapperContext context = new DefaultMapperContext(session, defaultKeyspaceId, defaultExecutionProfileName, defaultExecutionProfile, customState);
    return new FunctionsMapperImpl__MapperGenerated(context);
  }
}
