/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.analytics;

import com.opengamma.engine.security.SecurityKey;
import com.opengamma.engine.viewer.ValueDefinitionVisitor;
import com.opengamma.engine.viewer.VisitableValueDefinition;
import com.opengamma.financial.model.volatility.surface.VolatilitySurface;
import com.opengamma.util.KeyValuePair;

/**
 * 
 *
 * @author jim
 */
public class VolatilitySurfaceValueDefinition extends
    AnalyticValueDefinitionImpl<VolatilitySurface> implements VisitableValueDefinition {
  
  @SuppressWarnings("unchecked")
  public VolatilitySurfaceValueDefinition() {
     super(new KeyValuePair<String, Object>("TYPE", "VOLATILITY_SURFACE"));
  }
  @SuppressWarnings("unchecked")
  public VolatilitySurfaceValueDefinition(SecurityKey securityKey) {
     super(new KeyValuePair<String, Object>("TYPE", "VOLATILITY_SURFACE"),
           new KeyValuePair<String, Object>("SECURITY", securityKey));
  }
  
  public <T> T accept(ValueDefinitionVisitor<T> visitor) {
    return visitor.visitVolatilitySurfaceValueDefinition(this);
  }
}
