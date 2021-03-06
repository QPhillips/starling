/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.integration.tool.enginedebugger.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.opengamma.engine.depgraph.ResolutionFailure;
import com.opengamma.engine.depgraph.ResolutionFailureImpl;
import com.opengamma.integration.tool.enginedebugger.ResolutionFailureChildNodeCreatingVisitor;

/**
 * wrapper for a set of resolution failures to make tree table model cleaner.
 */
public class UnsatisfiedResolutionFailuresNode implements TreeTableNode {

  private static final String NAME = "UnsatisfiedResolutionFailures";
  @SuppressWarnings("unused")
  private final Object _parent;
  private final List<ResolutionFailure> _failures;
  private final List<Collection<Object>> _expandedFailures;

  public UnsatisfiedResolutionFailuresNode(final Object parent, final Set<ResolutionFailure> failures) {
    _parent = parent;
    _failures = new ArrayList<>(failures);
    _expandedFailures = new ArrayList<>();
    for (final ResolutionFailure failure : _failures) {
      _expandedFailures.add(failure.accept(new ResolutionFailureChildNodeCreatingVisitor((ResolutionFailureImpl) failure)));
    }
  }

  @Override
  public Object getChildAt(final int index) {
    return _expandedFailures.get(index);
  }

  @Override
  public int getChildCount() {
    return _expandedFailures.size();
  }

  @Override
  public int getIndexOfChild(final Object child) {
    return _expandedFailures.indexOf(child);
  }

  @Override
  public Object getColumn(final int column) {
    if (column == 0) {
      return NAME;
    }
    return null;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (_failures == null ? 0 : _failures.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof UnsatisfiedResolutionFailuresNode)) {
      return false;
    }
    final UnsatisfiedResolutionFailuresNode other = (UnsatisfiedResolutionFailuresNode) obj;
    if (_failures == null) {
      if (other._failures != null) {
        return false;
      }
    } else if (!_failures.equals(other._failures)) {
      return false;
    }
    return true;
  }

}
