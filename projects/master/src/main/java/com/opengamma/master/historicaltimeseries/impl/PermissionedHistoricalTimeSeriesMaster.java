/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.master.historicaltimeseries.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.Permission;
import org.threeten.bp.LocalDate;

import com.opengamma.core.change.ChangeManager;
import com.opengamma.id.ObjectIdentifiable;
import com.opengamma.id.UniqueId;
import com.opengamma.id.VersionCorrection;
import com.opengamma.master.historicaltimeseries.HistoricalTimeSeriesGetFilter;
import com.opengamma.master.historicaltimeseries.HistoricalTimeSeriesInfoDocument;
import com.opengamma.master.historicaltimeseries.HistoricalTimeSeriesInfoHistoryRequest;
import com.opengamma.master.historicaltimeseries.HistoricalTimeSeriesInfoHistoryResult;
import com.opengamma.master.historicaltimeseries.HistoricalTimeSeriesInfoMetaDataRequest;
import com.opengamma.master.historicaltimeseries.HistoricalTimeSeriesInfoMetaDataResult;
import com.opengamma.master.historicaltimeseries.HistoricalTimeSeriesInfoSearchRequest;
import com.opengamma.master.historicaltimeseries.HistoricalTimeSeriesInfoSearchResult;
import com.opengamma.master.historicaltimeseries.HistoricalTimeSeriesMaster;
import com.opengamma.master.historicaltimeseries.ManageableHistoricalTimeSeries;
import com.opengamma.master.historicaltimeseries.ManageableHistoricalTimeSeriesInfo;
import com.opengamma.timeseries.date.localdate.LocalDateDoubleTimeSeries;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.auth.AuthUtils;
import com.opengamma.util.auth.Permissionable;

/**
 * A decorator for a time-series master that applies permissions.
 * <p>
 * Two kinds of permissions are applied by this class.
 * <p>
 * The first kind of permission is master-based.
 * These are provided as static constants on this class and cover
 * the basic view, add, update and remove operations.
 * <p>
 * The second kind of permission is entity-based.
 * The {@link ManageableHistoricalTimeSeriesInfo} class implements {@link Permissionable}.
 * This provides each security with a set of permissions that a user needs
 * to be able to view the data. This master enforces those permissions.
 * <p>
 * For the {@code search} and {@code history} methods, each restricted document
 * is removed from the result. Since this happens after paging, it is possible
 * to see pages of data that are smaller than the requested page size.
 * <p>
 * For the bulk {@code get} method, each restricted document is removed from the result.
 * <p>
 * For the {@code get} methods, a restricted document causes an exception to be thrown.
 */
public class PermissionedHistoricalTimeSeriesMaster implements HistoricalTimeSeriesMaster {

  /**
   * The permission object for viewing data.
   */
  public static final Permission PERMISSION_VIEW = AuthUtils.getPermissionResolver().resolvePermission("HistoricalTimeSeriesMaster:view");
  /**
   * The permission object for adding data.
   */
  public static final Permission PERMISSION_ADD = AuthUtils.getPermissionResolver().resolvePermission("HistoricalTimeSeriesMaster:edit:add");
  /**
   * The permission object for updating data.
   */
  public static final Permission PERMISSION_UPDATE = AuthUtils.getPermissionResolver().resolvePermission("HistoricalTimeSeriesMaster:edit:update");
  /**
   * The permission object for removing data.
   */
  public static final Permission PERMISSION_REMOVE = AuthUtils.getPermissionResolver().resolvePermission("HistoricalTimeSeriesMaster:edit:remove");
  /**
   * The permission object for correcting data.
   */
  public static final Permission PERMISSION_CORRECT = AuthUtils.getPermissionResolver().resolvePermission("HistoricalTimeSeriesMaster:edit:correct");

  /**
   * The underlying time-series master.
   */
  private final HistoricalTimeSeriesMaster _underlying;

  //-------------------------------------------------------------------------
  /**
   * Wraps an underlying master if appropriate.
   * <p>
   * No wrapping occurs if permissions are not in use.
   *
   * @param underlying  the underlying master, not null
   * @return the master, not null
   */
  public static HistoricalTimeSeriesMaster wrap(final HistoricalTimeSeriesMaster underlying) {
    if (AuthUtils.isPermissive()) {
      return underlying;
    }
    return new PermissionedHistoricalTimeSeriesMaster(underlying);
  }

  //-------------------------------------------------------------------------
  /**
   * Creates an instance.
   *
   * @param underlying  the underlying time-series master, not null
   */
  public PermissionedHistoricalTimeSeriesMaster(final HistoricalTimeSeriesMaster underlying) {
    _underlying = ArgumentChecker.notNull(underlying, "underlying");
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the underlying time-series master.
   *
   * @return the underlying master, not null
   */
  protected HistoricalTimeSeriesMaster getUnderlying() {
    return _underlying;
  }

  //-------------------------------------------------------------------------
  @Override
  public HistoricalTimeSeriesInfoDocument get(final UniqueId uniqueId) {
    AuthUtils.getSubject().checkPermission(PERMISSION_VIEW);
    final HistoricalTimeSeriesInfoDocument doc = getUnderlying().get(uniqueId);
    AuthUtils.checkPermissions(doc.getValue());
    return doc;
  }

  @Override
  public HistoricalTimeSeriesInfoDocument get(final ObjectIdentifiable objectId, final VersionCorrection versionCorrection) {
    AuthUtils.getSubject().checkPermission(PERMISSION_VIEW);
    final HistoricalTimeSeriesInfoDocument doc = getUnderlying().get(objectId, versionCorrection);
    AuthUtils.checkPermissions(doc.getValue());
    return doc;
  }

  @Override
  public Map<UniqueId, HistoricalTimeSeriesInfoDocument> get(final Collection<UniqueId> uniqueIds) {
    AuthUtils.getSubject().checkPermission(PERMISSION_VIEW);
    final Map<UniqueId, HistoricalTimeSeriesInfoDocument> result = new HashMap<>(getUnderlying().get(uniqueIds));
    for (final Iterator<HistoricalTimeSeriesInfoDocument> it = result.values().iterator(); it.hasNext();) {
      final HistoricalTimeSeriesInfoDocument doc = it.next();
      if (!AuthUtils.isPermitted(doc.getValue())) {
        it.remove();
      }
    }
    return result;
  }

  @Override
  public HistoricalTimeSeriesInfoSearchResult search(final HistoricalTimeSeriesInfoSearchRequest request) {
    AuthUtils.getSubject().checkPermission(PERMISSION_VIEW);
    final HistoricalTimeSeriesInfoSearchResult result = getUnderlying().search(request);
    int removed = 0;
    for (final Iterator<HistoricalTimeSeriesInfoDocument> it = result.getDocuments().iterator(); it.hasNext();) {
      final HistoricalTimeSeriesInfoDocument doc = it.next();
      if (!AuthUtils.isPermitted(doc.getValue())) {
        it.remove();
        removed++;
      }
    }
    result.setUnauthorizedCount(removed);
    return result;
  }

  @Override
  public HistoricalTimeSeriesInfoHistoryResult history(final HistoricalTimeSeriesInfoHistoryRequest request) {
    AuthUtils.getSubject().checkPermission(PERMISSION_VIEW);
    final HistoricalTimeSeriesInfoHistoryResult result = getUnderlying().history(request);
    int removed = 0;
    for (final Iterator<HistoricalTimeSeriesInfoDocument> it = result.getDocuments().iterator(); it.hasNext();) {
      final HistoricalTimeSeriesInfoDocument doc = it.next();
      if (!AuthUtils.isPermitted(doc.getValue())) {
        it.remove();
        removed++;
      }
    }
    result.setUnauthorizedCount(removed);
    return result;
  }

  //-------------------------------------------------------------------------
  @Override
  public ChangeManager changeManager() {
    return getUnderlying().changeManager();
  }

  @Override
  public HistoricalTimeSeriesInfoMetaDataResult metaData(final HistoricalTimeSeriesInfoMetaDataRequest request) {
    AuthUtils.getSubject().checkPermission(PERMISSION_VIEW);
    return getUnderlying().metaData(request);
  }

  @Override
  public HistoricalTimeSeriesInfoDocument add(final HistoricalTimeSeriesInfoDocument document) {
    AuthUtils.getSubject().checkPermission(PERMISSION_ADD);
    return getUnderlying().add(document);
  }

  @Override
  public HistoricalTimeSeriesInfoDocument update(final HistoricalTimeSeriesInfoDocument document) {
    AuthUtils.getSubject().checkPermission(PERMISSION_UPDATE);
    return getUnderlying().update(document);
  }

  @Override
  public void remove(final ObjectIdentifiable oid) {
    AuthUtils.getSubject().checkPermission(PERMISSION_REMOVE);
    getUnderlying().remove(oid);
  }

  @Override
  public HistoricalTimeSeriesInfoDocument correct(final HistoricalTimeSeriesInfoDocument document) {
    AuthUtils.getSubject().checkPermission(PERMISSION_CORRECT);
    return getUnderlying().correct(document);
  }

  @Override
  public List<UniqueId> replaceVersion(final UniqueId uniqueId, final List<HistoricalTimeSeriesInfoDocument> replacementDocuments) {
    AuthUtils.getSubject().checkPermission(PERMISSION_CORRECT);
    return getUnderlying().replaceVersion(uniqueId, replacementDocuments);
  }

  @Override
  public List<UniqueId> replaceAllVersions(final ObjectIdentifiable objectId, final List<HistoricalTimeSeriesInfoDocument> replacementDocuments) {
    AuthUtils.getSubject().checkPermission(PERMISSION_CORRECT);
    return getUnderlying().replaceAllVersions(objectId, replacementDocuments);
  }

  @Override
  public List<UniqueId> replaceVersions(final ObjectIdentifiable objectId, final List<HistoricalTimeSeriesInfoDocument> replacementDocuments) {
    AuthUtils.getSubject().checkPermission(PERMISSION_CORRECT);
    return getUnderlying().replaceVersions(objectId, replacementDocuments);
  }

  @Override
  public UniqueId replaceVersion(final HistoricalTimeSeriesInfoDocument replacementDocument) {
    AuthUtils.getSubject().checkPermission(PERMISSION_CORRECT);
    return getUnderlying().replaceVersion(replacementDocument);
  }

  @Override
  public void removeVersion(final UniqueId uniqueId) {
    AuthUtils.getSubject().checkPermission(PERMISSION_CORRECT);
    getUnderlying().removeVersion(uniqueId);
  }

  @Override
  public UniqueId addVersion(final ObjectIdentifiable objectId, final HistoricalTimeSeriesInfoDocument documentToAdd) {
    AuthUtils.getSubject().checkPermission(PERMISSION_CORRECT);
    return getUnderlying().addVersion(objectId, documentToAdd);
  }

  //-------------------------------------------------------------------------
  @Override
  public ManageableHistoricalTimeSeries getTimeSeries(final UniqueId uniqueId) {
    AuthUtils.getSubject().checkPermission(PERMISSION_VIEW);
    return getUnderlying().getTimeSeries(uniqueId);
  }

  @Override
  public ManageableHistoricalTimeSeries getTimeSeries(final UniqueId uniqueId, final HistoricalTimeSeriesGetFilter filter) {
    AuthUtils.getSubject().checkPermission(PERMISSION_VIEW);
    return getUnderlying().getTimeSeries(uniqueId, filter);
  }

  @Override
  public ManageableHistoricalTimeSeries getTimeSeries(final ObjectIdentifiable objectId, final VersionCorrection versionCorrection) {
    AuthUtils.getSubject().checkPermission(PERMISSION_VIEW);
    return getUnderlying().getTimeSeries(objectId, versionCorrection);
  }

  @Override
  public ManageableHistoricalTimeSeries getTimeSeries(final ObjectIdentifiable objectId, final VersionCorrection versionCorrection,
      final HistoricalTimeSeriesGetFilter filter) {
    AuthUtils.getSubject().checkPermission(PERMISSION_VIEW);
    return getUnderlying().getTimeSeries(objectId, versionCorrection, filter);
  }

  @Override
  public UniqueId updateTimeSeriesDataPoints(final ObjectIdentifiable objectId, final LocalDateDoubleTimeSeries series) {
    AuthUtils.getSubject().checkPermission(PERMISSION_UPDATE);
    return getUnderlying().updateTimeSeriesDataPoints(objectId, series);
  }

  @Override
  public UniqueId correctTimeSeriesDataPoints(final ObjectIdentifiable objectId, final LocalDateDoubleTimeSeries series) {
    AuthUtils.getSubject().checkPermission(PERMISSION_CORRECT);
    return getUnderlying().correctTimeSeriesDataPoints(objectId, series);
  }

  @Override
  public UniqueId removeTimeSeriesDataPoints(final ObjectIdentifiable objectId, final LocalDate fromDateInclusive, final LocalDate toDateInclusive) {
    AuthUtils.getSubject().checkPermission(PERMISSION_REMOVE);
    return getUnderlying().removeTimeSeriesDataPoints(objectId, fromDateInclusive, toDateInclusive);
  }

  //-------------------------------------------------------------------------
  @Override
  public String toString() {
    return String.format("%s[%s]", getClass().getSimpleName(), getUnderlying());
  }

}
