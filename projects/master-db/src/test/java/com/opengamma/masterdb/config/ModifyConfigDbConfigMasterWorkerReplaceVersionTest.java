/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.masterdb.config;

import static com.google.common.collect.Lists.newArrayList;
import static org.testng.AssertJUnit.assertEquals;
import static org.threeten.bp.temporal.ChronoUnit.HOURS;
import static org.threeten.bp.temporal.ChronoUnit.MINUTES;
import static org.threeten.bp.temporal.ChronoUnit.SECONDS;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.threeten.bp.Clock;
import org.threeten.bp.Instant;
import org.threeten.bp.ZoneOffset;

import com.opengamma.DataNotFoundException;
import com.opengamma.core.config.impl.ConfigItem;
import com.opengamma.id.ObjectId;
import com.opengamma.id.UniqueId;
import com.opengamma.id.VersionCorrection;
import com.opengamma.master.config.ConfigDocument;
import com.opengamma.master.config.ConfigHistoryRequest;
import com.opengamma.master.config.ConfigHistoryResult;
import com.opengamma.master.config.ConfigSearchRequest;
import com.opengamma.master.config.ConfigSearchResult;
import com.opengamma.util.test.DbTest;
import com.opengamma.util.test.TestGroup;

/**
 * Tests ModifyConfigDbConfigMasterWorker.
 */
@Test(groups = TestGroup.UNIT_DB)
public class ModifyConfigDbConfigMasterWorkerReplaceVersionTest extends AbstractDbConfigMasterWorkerTest {
  // superclass sets up dummy database

  private static final Logger LOGGER = LoggerFactory.getLogger(ModifyConfigDbConfigMasterWorkerReplaceVersionTest.class);

  @Factory(dataProvider = "databases", dataProviderClass = DbTest.class)
  public ModifyConfigDbConfigMasterWorkerReplaceVersionTest(final String databaseType, final String databaseVersion) {
    super(databaseType, databaseVersion, false);
    LOGGER.info("running testcases for {}", databaseType);
  }

  @Test
  public void test_ReplaceVersion_of_some_middle_version() {
    final Clock origClock = _cfgMaster.getClock();
    try {
      final Instant now = Instant.now();

      final ObjectId baseOid = setupTestData(now);
      _cfgMaster.setClock(Clock.fixed(now.plus(2, HOURS), ZoneOffset.UTC));
      final ConfigDocument latestDoc = _cfgMaster.get(baseOid, VersionCorrection.LATEST);
      final Instant latestFrom = latestDoc.getVersionFromInstant();

      final List<ConfigDocument> replacement = newArrayList();
      for (int i = 0; i <= 10; i++) {
        final String val = "test" + i;
        final ConfigDocument doc = new ConfigDocument(ConfigItem.of(val, "some_name_"+i));
        doc.setVersionFromInstant(latestFrom.plus(i, MINUTES));
        replacement.add(doc);
      }

      _cfgMaster.replaceVersion(latestDoc.getUniqueId(), replacement);

      final ConfigSearchRequest<String> searchRequest = new ConfigSearchRequest<>();
      searchRequest.addConfigId(baseOid);
      searchRequest.setVersionCorrection(VersionCorrection.LATEST);
      searchRequest.setType(String.class);
      final ConfigSearchResult<String> result = _cfgMaster.search(searchRequest);
      final List<ConfigItem<String>> values = result.getValues();

      assertEquals(1, values.size());
      final String val = values.get(0).getValue();
      assertEquals("test10", val);

    } finally {
      _cfgMaster.setClock(origClock);
    }
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_ReplaceVersion_of_some_middle_version_timeBoundsNotExact() {
    final Clock origClock = _cfgMaster.getClock();
    try {
      final Instant now = Instant.now();

      final ObjectId baseOid = setupTestData(now);
      _cfgMaster.setClock(Clock.fixed(now.plus(2, HOURS), ZoneOffset.UTC));
      final ConfigDocument latestDoc = _cfgMaster.get(baseOid, VersionCorrection.LATEST);
      final Instant latestFrom = latestDoc.getVersionFromInstant();

      final List<ConfigDocument> replacement = newArrayList();
      for (int i = 1; i <= 10; i++) {
        final String val = "test" + i;
        final ConfigDocument doc = new ConfigDocument(ConfigItem.of(val));
        doc.setVersionFromInstant(latestFrom.plus(i, MINUTES));
        replacement.add(doc);
      }

      _cfgMaster.replaceVersion(latestDoc.getUniqueId(), replacement);

      final ConfigSearchRequest<String> searchRequest = new ConfigSearchRequest<>();
      searchRequest.addConfigId(baseOid);
      searchRequest.setVersionCorrection(VersionCorrection.LATEST);
      final ConfigSearchResult<String> result = _cfgMaster.search(searchRequest);
      final List<ConfigItem<String>> values = result.getValues();

      assertEquals(1, values.size());
      final String val = values.get(0).getValue();
      assertEquals("test10", val);

    } finally {
      _cfgMaster.setClock(origClock);
    }
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_ReplaceVersion_which_is_not_current() {
    final Clock origClock = _cfgMaster.getClock();
    try {
      final Instant now = Instant.now();

      final ObjectId baseOid = setupTestData(now);
      _cfgMaster.setClock(Clock.fixed(now.plus(2, HOURS), ZoneOffset.UTC));

      final ConfigHistoryRequest<String> historyRequest = new ConfigHistoryRequest<>();
      historyRequest.setObjectId(baseOid);
      historyRequest.setCorrectionsToInstant(null);
      final ConfigHistoryResult<String> result = _cfgMaster.history(historyRequest);
      final List<ConfigDocument> confDocs = result.getDocuments();

      final ConfigDocument lastButOneDoc = confDocs.get(confDocs.size() - 1);
      final Instant lastButOneDocVersionFrom = lastButOneDoc.getVersionFromInstant();


      final List<ConfigDocument> replacement = newArrayList();
      for (int i = 1; i <= 10; i++) {
        final String val = "test" + i;
        final ConfigDocument doc = new ConfigDocument(ConfigItem.of(val));
        doc.setVersionFromInstant(lastButOneDocVersionFrom.plus(i, MINUTES));
        replacement.add(doc);
      }

      _cfgMaster.replaceVersion(lastButOneDoc.getUniqueId(), replacement);

    } finally {
      _cfgMaster.setClock(origClock);
    }
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_ReplaceVersion_which_is_not_in_the_time_bounds_of_the_replaced_doc() {
    final Clock origClock = _cfgMaster.getClock();
    try {
      final Instant now = Instant.now();

      final ObjectId baseOid = setupTestData(now);
      _cfgMaster.setClock(Clock.fixed(now.plus(2, HOURS), ZoneOffset.UTC));


      final ConfigHistoryRequest<String> historyRequest = new ConfigHistoryRequest<>();
      historyRequest.setObjectId(baseOid);
      historyRequest.setCorrectionsToInstant(null);
      final ConfigHistoryResult<String> result = _cfgMaster.history(historyRequest);
      final List<ConfigDocument> confDocs = result.getDocuments();

      final ConfigDocument lastButOneDoc = confDocs.get(confDocs.size() - 3);
      final Instant lastButOneDocVersionFrom = lastButOneDoc.getVersionFromInstant();


      final List<ConfigDocument> replacement = newArrayList();
      for (int i = 1; i <= 10; i++) {
        final String val = "test" + i;
        final ConfigDocument doc = new ConfigDocument(ConfigItem.of(val));
        doc.setVersionFromInstant(lastButOneDocVersionFrom.plus(i, MINUTES));
        replacement.add(doc);
      }

      _cfgMaster.replaceVersion(lastButOneDoc.getUniqueId(), replacement);

    } finally {
      _cfgMaster.setClock(origClock);
    }
  }

  @Test
  public void test_ReplaceVersions() {
    final Clock origClock = _cfgMaster.getClock();
    try {
      final Instant now = Instant.now();

      final ObjectId baseOid = setupTestData(now);
      _cfgMaster.setClock(Clock.fixed(now.plus(2, HOURS), ZoneOffset.UTC));
      final ConfigDocument latestDoc = _cfgMaster.get(baseOid, VersionCorrection.LATEST);
      final Instant latestFrom = latestDoc.getVersionFromInstant();


      final List<ConfigDocument> replacement = newArrayList();
      for (int i = 0; i <= 10; i++) {
        final String val = "test" + i;
        final ConfigDocument doc = new ConfigDocument(ConfigItem.of(val, "some_name_"+i));
        doc.setVersionFromInstant(latestFrom.plus(i, SECONDS));
        replacement.add(doc);
      }

      _cfgMaster.replaceVersions(latestDoc, replacement);

      final ConfigHistoryRequest<String> historyRequest = new ConfigHistoryRequest<>();
      historyRequest.setObjectId(baseOid);
      historyRequest.setCorrectionsFromInstant(now.plus(2, HOURS));
      final ConfigHistoryResult<String> result = _cfgMaster.history(historyRequest);
      final List<ConfigDocument> confDocs = result.getDocuments();

      assertEquals(15, confDocs.size());

    } finally {
      _cfgMaster.setClock(origClock);
    }
  }

  @Test
  public void test_ReplaceVersions2() {
    final Clock origClock = _cfgMaster.getClock();
    try {
      final Instant now = Instant.now();

      final ObjectId baseOid = setupTestData(now);
      _cfgMaster.setClock(Clock.fixed(now.plus(2, HOURS), ZoneOffset.UTC));
      final ConfigDocument latestDoc = _cfgMaster.get(baseOid, VersionCorrection.LATEST);
      final Instant latestFrom = latestDoc.getVersionFromInstant();


      final List<ConfigDocument> replacement = newArrayList();
      for (int i = 0; i <= 10; i++) {
        final String val = "test" + i;
        final ConfigDocument doc = new ConfigDocument(ConfigItem.of(val, "some_name_"+i));
        doc.setVersionFromInstant(latestFrom.plus(i - 3, MINUTES));
        replacement.add(doc);
      }

      _cfgMaster.replaceVersions(latestDoc, replacement);

      final ConfigHistoryRequest<String> historyRequest = new ConfigHistoryRequest<>();
      historyRequest.setObjectId(baseOid);
      historyRequest.setCorrectionsFromInstant(now.plus(2, HOURS));
      final ConfigHistoryResult<String> result = _cfgMaster.history(historyRequest);
      final List<ConfigDocument> confDocs = result.getDocuments();

      assertEquals(12, confDocs.size());

    } finally {
      _cfgMaster.setClock(origClock);
    }
  }

  /**
   *
   *       |                        |             |
   *       |                        |             |
   *       |     setup_4            |             |
   *       |                        |             |
   *   +5m |------------------------|             |
   *       |                        |             |
   *       |     setup_3            |             |
   *       |                        |             |
   *   +4m |------------------------|             |
   *       |                        |             |
   *       |     setup_2            |             |
   *       |                        |             |
   *   +3m |------------------------|   |         |
   *       |                        |             |      replace_3
   *       |                        |  <-- +2m30s |----------------------------------->>>
   *       |     setup_1            |             |      replace_2
   *       |                        |  <-- +2m20s |----------------------------------->>>
   *       |                        |             |      replace_1
   *       |                        |  <-- +2m10s |----------------------------------->>>
   *   +2m |------------------------
   *       |
   *       |     setup_0
   *       |
   *   +1m |------------------------ ... --------------------------------------------->>>
   *
   *
   *   NOW =================================================================================
   *
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_ReplaceVersion_notInTimeBounds() {
    final Clock origClock = _cfgMaster.getClock();
    try {
      final Instant now = Instant.now();

      final ObjectId baseOid = setupTestData(now);
      _cfgMaster.setClock(Clock.fixed(now.plus(2, HOURS), ZoneOffset.UTC));
      @SuppressWarnings("unused")
      final
      ConfigDocument latestDoc = _cfgMaster.get(baseOid, VersionCorrection.LATEST);

      final List<ConfigDocument> replacement = newArrayList();
      for (int i = 1; i <= 3; i++) {
        final String val = "replace_" + i;
        final ConfigDocument doc = new ConfigDocument(ConfigItem.of(val));
        doc.setVersionFromInstant(now.plus(1, MINUTES).plus(i * 10, SECONDS));
        replacement.add(doc);
      }

      final ConfigHistoryRequest<String> historyRequest = new ConfigHistoryRequest<>();
      historyRequest.setObjectId(baseOid);
      historyRequest.setCorrectionsFromInstant(now.plus(2, HOURS));
      final ConfigHistoryResult<String> result = _cfgMaster.history(historyRequest);
      final List<ConfigDocument> confDocs = result.getDocuments();

      final ConfigDocument secondVersionDoc = confDocs.get(confDocs.size() - 2);

      _cfgMaster.replaceVersion(secondVersionDoc.getUniqueId(), replacement);

    } finally {
      _cfgMaster.setClock(origClock);
    }
  }

  /**
   *
   *       |                        |
   *       |                        |
   *       |     setup_4            |
   *       |                        |
   *   +4m |------------------------|
   *       |                        |
   *       |     setup_3            |
   *       |                        |
   *   +3m |------------------------|
   *       |                        |
   *       |     setup_2            |
   *       |                        |
   *   +2m |------------------------|   |
   *       |                        |  <-- +1m40s |----------------------------------->>>
   *       |                        |             |      replace_3
   *       |                        |  <-- +1m30s |----------------------------------->>>
   *       |     setup_1            |             |      replace_2
   *       |                        |  <-- +1m20s |----------------------------------->>>
   *       |                        |             |      replace_1
   *       |                        |  <-- +1m10s |----------------------------------->>>
   *   +1m |------------------------
   *       |
   *       |     setup_0
   *       |
   *   NOW ===========================================================================>>>
   *
   *
   *
   *
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_ReplaceVersion_inTimeBounds() {
    final Clock origClock = _cfgMaster.getClock();
    try {
      final Instant now = Instant.now();

      final ObjectId baseOid = setupTestData(now);
      _cfgMaster.setClock(Clock.fixed(now.plus(2, HOURS), ZoneOffset.UTC));
      @SuppressWarnings("unused")
      final
      ConfigDocument latestDoc = _cfgMaster.get(baseOid, VersionCorrection.LATEST);

      final List<ConfigDocument> replacement = newArrayList();
      for (int i = 1; i <= 3; i++) {
        final String val = "replace_" + i;
        final ConfigDocument doc = new ConfigDocument(ConfigItem.of(val));
        doc.setVersionFromInstant(now.plus(1, MINUTES).plus(i * 10, SECONDS));
        replacement.add(doc);
      }
      replacement.get(replacement.size() - 1).setVersionToInstant(now.plus(1, MINUTES).plus(40, SECONDS));

      final ConfigHistoryRequest<String> historyRequest = new ConfigHistoryRequest<>();
      historyRequest.setObjectId(baseOid);
      historyRequest.setCorrectionsFromInstant(now.plus(2, HOURS));
      final ConfigHistoryResult<String> result = _cfgMaster.history(historyRequest);
      final List<ConfigDocument> confDocs = result.getDocuments();

      final ConfigDocument secondVersionDoc = confDocs.get(confDocs.size() - 2);

      _cfgMaster.replaceVersion(secondVersionDoc.getUniqueId(), replacement);
    } finally {
      _cfgMaster.setClock(origClock);
    }
  }

  /**
   *
   *       |                        |
   *       |                        |
   *       |     setup_4            |
   *       |                        |
   *   +4m |------------------------|
   *       |                        |
   *       |     setup_3            |
   *       |                        |
   *   +3m |------------------------|
   *       |                        |
   *       |     setup_2            |
   *       |                        |
   *   +2m |------------------------|  <-- +2m00s |----------------------------------->>>
   *       |                        |             |      replace_3
   *       |     setup_1            |  <-- +1m40s |----------------------------------->>>
   *       |                        |             |      replace_2
   *       |                        |  <-- +1m20s |----------------------------------->>>
   *       |                        |             |      replace_1
   *   +1m |------------------------   <-- +1m00s |----------------------------------->>>
   *       |
   *       |     setup_0
   *       |
   *   NOW ===========================================================================>>>
   *
   *
   *
   *
   */
  @Test
  public void test_ReplaceVersion_atTimeBounds() {
    final Clock origClock = _cfgMaster.getClock();
    try {
      final Instant now = Instant.now();

      final ObjectId baseOid = setupTestData(now);
      _cfgMaster.setClock(Clock.fixed(now.plus(2, HOURS), ZoneOffset.UTC));


      final List<ConfigDocument> replacement = newArrayList();
      for (int i = 0; i <= 2; i++) {
        final String val = "replace_" + i;
        final ConfigDocument doc = new ConfigDocument(ConfigItem.of(val, "some_name_"+i));
        doc.setVersionFromInstant(now.plus(1, MINUTES).plus(i * 20, SECONDS));
        replacement.add(doc);
      }
      replacement.get(replacement.size() - 1).setVersionToInstant(now.plus(2, MINUTES));

      ConfigHistoryRequest<String> historyRequest = new ConfigHistoryRequest<>();
      historyRequest.setObjectId(baseOid);
      historyRequest.setCorrectionsFromInstant(now.plus(2, HOURS));
      ConfigHistoryResult<String> result = _cfgMaster.history(historyRequest);
      List<ConfigDocument> confDocs = result.getDocuments();

      final ConfigDocument secondVersionDoc = confDocs.get(confDocs.size() - 2);

      _cfgMaster.replaceVersion(secondVersionDoc.getUniqueId(), replacement);

      historyRequest = new ConfigHistoryRequest<>();
      historyRequest.setObjectId(baseOid);
      historyRequest.setCorrectionsFromInstant(now.plus(2, HOURS));
      result = _cfgMaster.history(historyRequest);
      confDocs = result.getDocuments();

      assertEquals(7, confDocs.size());

      assertEquals(now, confDocs.get(6).getVersionFromInstant());
      assertEquals(now.plus(1, MINUTES), confDocs.get(6).getVersionToInstant());
      //
      assertEquals(now.plus(1, MINUTES), confDocs.get(5).getVersionFromInstant());
      assertEquals(now.plus(1, MINUTES).plus(20, SECONDS), confDocs.get(5).getVersionToInstant());
      //
      assertEquals(now.plus(1, MINUTES).plus(20, SECONDS), confDocs.get(4).getVersionFromInstant());
      assertEquals(now.plus(1, MINUTES).plus(40, SECONDS), confDocs.get(4).getVersionToInstant());
      //
      assertEquals(now.plus(1, MINUTES).plus(40, SECONDS), confDocs.get(3).getVersionFromInstant());
      assertEquals(now.plus(1, MINUTES).plus(60, SECONDS), confDocs.get(3).getVersionToInstant());
      //
      assertEquals(now.plus(1, MINUTES).plus(60, SECONDS), confDocs.get(2).getVersionFromInstant());
      assertEquals(now.plus(1, MINUTES).plus(120, SECONDS), confDocs.get(2).getVersionToInstant());
      //
      assertEquals(now.plus(1, MINUTES).plus(120, SECONDS), confDocs.get(1).getVersionFromInstant());
      assertEquals(now.plus(1, MINUTES).plus(180, SECONDS), confDocs.get(1).getVersionToInstant());
      //
      assertEquals(now.plus(1, MINUTES).plus(180, SECONDS), confDocs.get(0).getVersionFromInstant());
      assertEquals(null, confDocs.get(0).getVersionToInstant());
    } finally {
      _cfgMaster.setClock(origClock);
    }
  }

  /**
   *
   *       |                                      |                            |
   *       |                                      |                            |
   *       |     setup_4                          |                            |      setup_4
   *       |                                      |                            |
   *   +4m |------------------------              |                            |-------------------
   *       |                                      |                            |
   *       |     setup_3                          |                            |      setup_3
   *       |                                      |                            |
   *   +3m |------------------------              |                            |-------------------
   *       |                                      |                            |
   *       |     setup_2                          |                      =>    |      setup_2
   *       |                                      |                            |
   *   +2m |------------------------   <-- +2m00s |                            |-------------------
   *       |                                      |      replace_2             |      replace_2
   *       |     setup_1               <-- +1m40s |-------------------         |-------------------
   *       |                                      |      replace_1             |      replace_1
   *       |                           <-- +1m20s |-------------------         |-------------------
   *       |                                      |      replace_0             |      replace_0
   *   +1m |------------------------   <-- +1m00s |-------------------         |-------------------
   *       |
   *       |     setup_0                                                              setup_0
   *       |
   *   NOW ======================================================================================>>>
   *
   *
   *
   *
   */
  @Test
  public void test_ReplaceVersion_atTimeBounds2() {
    final Clock origClock = _cfgMaster.getClock();
    try {
      final Instant now = Instant.now();

      final ObjectId baseOid = setupTestData(now);
      _cfgMaster.setClock(Clock.fixed(now.plus(2, HOURS), ZoneOffset.UTC));


      final List<ConfigDocument> replacement = newArrayList();
      for (int i = 0; i <= 2; i++) {
        final String val = "replace_" + i;
        final ConfigDocument doc = new ConfigDocument(ConfigItem.of(val, "some_name_"+i));
        doc.setVersionFromInstant(now.plus(1, MINUTES).plus(i * 20, SECONDS));
        replacement.add(doc);
      }

      ConfigHistoryRequest<String> historyRequest = new ConfigHistoryRequest<>();
      historyRequest.setObjectId(baseOid);
      historyRequest.setCorrectionsFromInstant(now.plus(2, HOURS));
      ConfigHistoryResult<String> result = _cfgMaster.history(historyRequest);
      List<ConfigDocument> confDocs = result.getDocuments();

      final ConfigDocument secondVersionDoc = confDocs.get(confDocs.size() - 2);

      _cfgMaster.replaceVersion(secondVersionDoc.getUniqueId(), replacement);

      historyRequest = new ConfigHistoryRequest<>();
      historyRequest.setObjectId(baseOid);
      historyRequest.setCorrectionsFromInstant(now.plus(2, HOURS));
      result = _cfgMaster.history(historyRequest);
      confDocs = result.getDocuments();

      assertEquals(7, confDocs.size());

      assertEquals(now, confDocs.get(6).getVersionFromInstant());
      assertEquals(now.plus(1, MINUTES), confDocs.get(6).getVersionToInstant());
      //
      assertEquals(now.plus(1, MINUTES), confDocs.get(5).getVersionFromInstant());
      assertEquals(now.plus(1, MINUTES).plus(20, SECONDS), confDocs.get(5).getVersionToInstant());
      //
      assertEquals(now.plus(1, MINUTES).plus(20, SECONDS), confDocs.get(4).getVersionFromInstant());
      assertEquals(now.plus(1, MINUTES).plus(40, SECONDS), confDocs.get(4).getVersionToInstant());
      //
      assertEquals(now.plus(1, MINUTES).plus(40, SECONDS), confDocs.get(3).getVersionFromInstant());
      assertEquals(now.plus(1, MINUTES).plus(60, SECONDS), confDocs.get(3).getVersionToInstant());
      //
      assertEquals(now.plus(1, MINUTES).plus(60, SECONDS), confDocs.get(2).getVersionFromInstant());
      assertEquals(now.plus(1, MINUTES).plus(120, SECONDS), confDocs.get(2).getVersionToInstant());
      //
      assertEquals(now.plus(1, MINUTES).plus(120, SECONDS), confDocs.get(1).getVersionFromInstant());
      assertEquals(now.plus(1, MINUTES).plus(180, SECONDS), confDocs.get(1).getVersionToInstant());
      //
      assertEquals(now.plus(1, MINUTES).plus(180, SECONDS), confDocs.get(0).getVersionFromInstant());
      assertEquals(null, confDocs.get(0).getVersionToInstant());
    } finally {
      _cfgMaster.setClock(origClock);
    }
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_ReplaceVersion_noUid() {
    final Clock origClock = _cfgMaster.getClock();
    try {
      final Instant now = Instant.now();

      final ObjectId baseOid = setupTestData(now);
      _cfgMaster.setClock(Clock.fixed(now.plus(2, HOURS), ZoneOffset.UTC));


      final List<ConfigDocument> replacement = newArrayList();
      for (int i = 0; i <= 2; i++) {
        final String val = "replace_" + i;
        final ConfigDocument doc = new ConfigDocument(ConfigItem.of(val));
        doc.setVersionFromInstant(now.plus(1, MINUTES).plus(i * 20, SECONDS));
        replacement.add(doc);
      }

      _cfgMaster.replaceVersion(baseOid.atVersion("no such uid"), replacement);
    } finally {
      _cfgMaster.setClock(origClock);
    }
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_replaceVersion_nullDocument() {
    _cfgMaster.replaceVersion(null);
  }

  @Test(expectedExceptions = DataNotFoundException.class)
  public void test_replaceVersion_notFound() {
    final UniqueId uniqueId = UniqueId.of("DbCfg", "0", "0");
    final String val = "Test";
    final ConfigDocument doc = new ConfigDocument(ConfigItem.of(val));
    doc.setUniqueId(uniqueId);
    _cfgMaster.replaceVersion(doc);
  }

}
