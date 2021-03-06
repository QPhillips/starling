/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.core.legalentity.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.MapMaker;
import com.opengamma.core.change.ChangeManager;
import com.opengamma.core.legalentity.LegalEntity;
import com.opengamma.core.legalentity.LegalEntitySource;
import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.id.ObjectId;
import com.opengamma.id.UniqueId;
import com.opengamma.id.VersionCorrection;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.ehcache.EHCacheUtils;
import com.opengamma.util.tuple.Pair;
import com.opengamma.util.tuple.Pairs;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * A cache decorating a {@code LegalEntitySource}.
 * <p>
 * The cache is implemented using {@code EHCache}.
 * <p>
 * Any requests with a "latest" version/correction or unversioned unique identifier are not cached
 * and will always hit the underlying. This should not be an issue in practice as the engine components
 * which use the legal entity source will always specify an exact version/correction and versioned unique identifiers.
 */
public class EHCachingLegalEntitySource implements LegalEntitySource {

  /**
   * Cache key for legal entities.
   */
  private static final String LEGALENTITY_CACHE = "legalentity";

  /**
   * The underlying legal entity source.
   */
  private final LegalEntitySource _underlying;
  /**
   * The cache manager.
   */
  private final CacheManager _cacheManager;
  /**
   * The legal entity cache.
   */
  private final Cache _legalEntityCache;
  /**
   * The front cache.
   */
  private final ConcurrentMap<Object, LegalEntity> _frontCache = new MapMaker().weakValues().makeMap();

  /**
   * Creates the cache around an underlying legal entity source.
   *
   * @param underlying   the underlying data, not null
   * @param cacheManager the cache manager, not null
   */
  public EHCachingLegalEntitySource(final LegalEntitySource underlying, final CacheManager cacheManager) {
    ArgumentChecker.notNull(underlying, "underlying");
    ArgumentChecker.notNull(cacheManager, "cacheManager");
    _underlying = underlying;
    _cacheManager = cacheManager;
    EHCacheUtils.addCache(cacheManager, LEGALENTITY_CACHE);
    _legalEntityCache = EHCacheUtils.getCacheFromManager(cacheManager, LEGALENTITY_CACHE);
  }

  //-------------------------------------------------------------------------

  /**
   * Gets the underlying source of legal entities.
   *
   * @return the underlying source of legal entities, not null
   */
  protected LegalEntitySource getUnderlying() {
    return _underlying;
  }

  /**
   * Gets the cache manager.
   *
   * @return the cache manager, not null
   */
  protected CacheManager getCacheManager() {
    return _cacheManager;
  }

  /**
   * For use by test methods only to control the front cache.
   */
  void emptyFrontCache() {
    _frontCache.clear();
  }

  /**
   * For use by test methods only to control the EH cache.
   */
  void emptyEHCache() {
    EHCacheUtils.clear(getCacheManager(), LEGALENTITY_CACHE);
  }

  //-------------------------------------------------------------------------
  /**
   * Adds an object to the front cache unless it is the latest version.
   *
   * @param legalEntity  the legal entity
   * @param versionCorrection  the version / correction
   * @return  the legal entity
   */
  protected LegalEntity addToFrontCache(final LegalEntity legalEntity, final VersionCorrection versionCorrection) {
    if (legalEntity.getUniqueId().isLatest()) {
      return legalEntity;
    }
    final LegalEntity existing = _frontCache.putIfAbsent(legalEntity.getUniqueId(), legalEntity);
    if (existing != null) {
      return existing;
    }
    if (versionCorrection != null) {
      _frontCache.put(Pairs.of(legalEntity.getExternalIdBundle(), versionCorrection), legalEntity);
      _frontCache.put(Pairs.of(legalEntity.getUniqueId().getObjectId(), versionCorrection), legalEntity);
    }
    return legalEntity;
  }

  /**
   * Adds an object to the EH cache unless it is the latest version.
   *
   * @param legalEntity  the legal entity
   * @param versionCorrection  the version / correction
   * @return  the legal entity
   */
  protected LegalEntity addToCache(final LegalEntity legalEntity, final VersionCorrection versionCorrection) {
    if (legalEntity != null) {
      final LegalEntity front = addToFrontCache(legalEntity, null);
      if (front == legalEntity) {
        if (legalEntity.getUniqueId().isVersioned()) {
          _legalEntityCache.put(new Element(legalEntity.getUniqueId(), legalEntity));
        }
        if (versionCorrection != null) {
          _legalEntityCache.put(new Element(Pairs.of(legalEntity.getExternalIdBundle(), versionCorrection), legalEntity));
          _legalEntityCache.put(new Element(Pairs.of(legalEntity.getUniqueId().getObjectId(), versionCorrection), legalEntity));
        }
      }
      return front;
    }
    return null;
  }

  //-------------------------------------------------------------------------
  @Override
  public LegalEntity get(final UniqueId uniqueId) {
    ArgumentChecker.notNull(uniqueId, "uniqueId");
    // check cache, but not if latest
    if (uniqueId.isLatest()) {
      return addToCache(getUnderlying().get(uniqueId), null);
    }
    LegalEntity result = _frontCache.get(uniqueId);
    if (result == null) {
      final Element e = _legalEntityCache.get(uniqueId);
      if (e != null) {
        result = (LegalEntity) e.getObjectValue();
        final LegalEntity existing = _frontCache.putIfAbsent(uniqueId, result);
        if (existing != null) {
          result = existing;
        }
      } else {
        result = addToCache(getUnderlying().get(uniqueId), null);
      }
    }
    return result;
  }

  @Override
  public LegalEntity get(final ObjectId objectId, final VersionCorrection versionCorrection) {
    ArgumentChecker.notNull(objectId, "objectId");
    ArgumentChecker.notNull(versionCorrection, "versionCorrection");
    // latest not in cache, can cache only by uniqueId
    if (versionCorrection.containsLatest()) {
      return addToCache(getUnderlying().get(objectId, versionCorrection), null);
    }
    // check cache
    final Pair<ObjectId, VersionCorrection> key = Pairs.of(objectId, versionCorrection);
    LegalEntity cached = _frontCache.get(key);
    if (cached != null) {
      return cached;
    }
    final Element e = _legalEntityCache.get(key);
    if (e != null) {
      cached = (LegalEntity) e.getObjectValue();
      return addToFrontCache(cached, versionCorrection);
    }
    // query underlying
    final LegalEntity legalEntity = getUnderlying().get(objectId, versionCorrection);
    return addToCache(legalEntity, versionCorrection);
  }

  //-------------------------------------------------------------------------
  @Override
  public LegalEntity getSingle(final ExternalId externalId) {
    ArgumentChecker.notNull(externalId, "externalId");
    return getSingle(externalId.toBundle(), VersionCorrection.LATEST);
  }

  @Override
  public LegalEntity getSingle(final ExternalIdBundle bundle) {
    ArgumentChecker.notNull(bundle, "bundle");
    return getSingle(bundle, VersionCorrection.LATEST);
  }

  @Override
  public LegalEntity getSingle(final ExternalIdBundle bundle, final VersionCorrection versionCorrection) {
    ArgumentChecker.notNull(bundle, "bundle");
    ArgumentChecker.notNull(versionCorrection, "versionCorrection");
    // latest not in cache, can cache only by uniqueId
    if (versionCorrection.containsLatest()) {
      return addToCache(getUnderlying().getSingle(bundle, versionCorrection), null);
    }
    // check cache
    final Pair<ExternalIdBundle, VersionCorrection> key = Pairs.of(bundle, versionCorrection);
    LegalEntity cached = _frontCache.get(key);
    if (cached != null) {
      return cached;
    }
    final Element e = _legalEntityCache.get(key);
    if (e != null) {
      cached = (LegalEntity) e.getObjectValue();
      return addToFrontCache(cached, versionCorrection);
    }
    // query underlying
    final LegalEntity legalEntity = getUnderlying().getSingle(bundle, versionCorrection);
    return addToCache(legalEntity, versionCorrection);
  }

  //-------------------------------------------------------------------------
  @Override
  @SuppressWarnings("deprecation")
  public Collection<LegalEntity> get(final ExternalIdBundle bundle) {
    return getUnderlying().get(bundle);
  }

  @Override
  public Collection<LegalEntity> get(final ExternalIdBundle bundle, final VersionCorrection versionCorrection) {
    return getUnderlying().get(bundle, versionCorrection);
  }

  //-------------------------------------------------------------------------
  @Override
  public Map<UniqueId, LegalEntity> get(final Collection<UniqueId> uniqueIds) {
    final Map<UniqueId, LegalEntity> map = getUnderlying().get(uniqueIds);
    for (final Entry<UniqueId, LegalEntity> entry : map.entrySet()) {
      entry.setValue(addToCache(entry.getValue(), null));
    }
    return map;
  }

  @Override
  public Map<ObjectId, LegalEntity> get(final Collection<ObjectId> objectIds, final VersionCorrection versionCorrection) {
    final Map<ObjectId, LegalEntity> map = getUnderlying().get(objectIds, versionCorrection);
    for (final Entry<ObjectId, LegalEntity> entry : map.entrySet()) {
      entry.setValue(addToCache(entry.getValue(), versionCorrection));
    }
    return map;
  }

  @Override
  public Map<ExternalIdBundle, Collection<LegalEntity>> getAll(final Collection<ExternalIdBundle> bundles, final VersionCorrection versionCorrection) {
    return getUnderlying().getAll(bundles, versionCorrection);
  }

  @Override
  public Map<ExternalIdBundle, LegalEntity> getSingle(final Collection<ExternalIdBundle> bundles, final VersionCorrection versionCorrection) {
    final Map<ExternalIdBundle, LegalEntity> map = getUnderlying().getSingle(bundles, versionCorrection);
    for (final Entry<ExternalIdBundle, LegalEntity> entry : map.entrySet()) {
      entry.setValue(addToCache(entry.getValue(), versionCorrection));
    }
    return map;
  }

  //-------------------------------------------------------------------------
  @Override
  public ChangeManager changeManager() {
    return getUnderlying().changeManager();
  }

  /**
   * Call this at the end of a unit test run to clear the state of EHCache.
   * It should not be part of a generic lifecycle method.
   */
  protected void shutdown() {
    _cacheManager.removeCache(LEGALENTITY_CACHE);
    _frontCache.clear();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + getUnderlying() + "]";
  }

}
