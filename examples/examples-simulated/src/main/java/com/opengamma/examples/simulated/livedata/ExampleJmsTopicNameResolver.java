/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.examples.simulated.livedata;

import com.opengamma.core.id.ExternalSchemes;
import com.opengamma.financial.credit.CdsRecoveryRateIdentifier;
import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalScheme;
import com.opengamma.livedata.resolver.AbstractResolver;
import com.opengamma.livedata.resolver.JmsTopicNameResolveRequest;
import com.opengamma.livedata.resolver.JmsTopicNameResolver;

/**
 * Produces JMS topic names for the data.
 */
public class ExampleJmsTopicNameResolver extends AbstractResolver<JmsTopicNameResolveRequest, String> implements JmsTopicNameResolver {

  @Override
  public String resolve(final JmsTopicNameResolveRequest request) {
    final ExternalId identifier = request.getMarketDataUniqueId();
    final ExternalScheme scheme = identifier.getScheme();
    if (ExternalSchemes.OG_SYNTHETIC_TICKER.equals(scheme) || CdsRecoveryRateIdentifier.SAMEDAY_CDS_SCHEME.equals(scheme)
        || CdsRecoveryRateIdentifier.COMPOSITE_CDS_SCHEME.equals(scheme)) {
      return "LiveData" + SEPARATOR + "EXAMPLE" + SEPARATOR + identifier.getValue() + request.getNormalizationRule().getJmsTopicSuffix();
    }
    return null;
  }

}
