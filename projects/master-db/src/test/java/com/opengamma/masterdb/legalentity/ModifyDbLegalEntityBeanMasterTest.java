/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.masterdb.legalentity;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.BadSqlGrammarException;
import org.testng.Assert;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.threeten.bp.Instant;

import com.opengamma.DataNotFoundException;
import com.opengamma.elsql.ElSqlBundle;
import com.opengamma.elsql.ElSqlConfig;
import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.id.UniqueId;
import com.opengamma.master.legalentity.LegalEntityDocument;
import com.opengamma.master.legalentity.LegalEntityHistoryRequest;
import com.opengamma.master.legalentity.LegalEntityHistoryResult;
import com.opengamma.master.legalentity.LegalEntitySearchRequest;
import com.opengamma.master.legalentity.LegalEntitySearchResult;
import com.opengamma.master.legalentity.LegalEntitySearchSortOrder;
import com.opengamma.master.legalentity.ManageableLegalEntity;
import com.opengamma.masterdb.bean.DbBeanMaster;
import com.opengamma.util.money.Currency;
import com.opengamma.util.test.DbTest;
import com.opengamma.util.test.TestGroup;

/**
 * Tests modification.
 */
@Test(groups = TestGroup.UNIT_DB)
public class ModifyDbLegalEntityBeanMasterTest extends AbstractDbLegalEntityBeanMasterTest {
  // superclass sets up dummy database

  private static final Logger LOGGER = LoggerFactory.getLogger(ModifyDbLegalEntityBeanMasterTest.class);

  @Factory(dataProvider = "databases", dataProviderClass = DbTest.class)
  public ModifyDbLegalEntityBeanMasterTest(final String databaseType, final String databaseVersion) {
    super(databaseType, databaseVersion, false);
    LOGGER.info("running testcases for {}", databaseType);
  }

  //-------------------------------------------------------------------------
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_addLegalEntity_nullDocument() {
    _lenMaster.add(null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_add_noLegalEntity() {
    final LegalEntityDocument doc = new LegalEntityDocument();
    _lenMaster.add(doc);
  }

  @Test
  public void test_add_add() {
    final Instant now = Instant.now(_lenMaster.getClock());

    final ManageableLegalEntity legalEntity = new MockLegalEntity("TestLegalEntity", ExternalIdBundle.of("A", "B"), Currency.GBP);
    final LegalEntityDocument doc = new LegalEntityDocument();
    doc.setLegalEntity(legalEntity);
    final LegalEntityDocument test = _lenMaster.add(doc);

    final UniqueId uniqueId = test.getUniqueId();
    assertNotNull(uniqueId);
    assertEquals("DbLen", uniqueId.getScheme());
    assertTrue(uniqueId.isVersioned());
    assertTrue(Long.parseLong(uniqueId.getValue()) >= 1000);
    assertEquals("0", uniqueId.getVersion());
    assertEquals(now, test.getVersionFromInstant());
    assertEquals(null, test.getVersionToInstant());
    assertEquals(now, test.getCorrectionFromInstant());
    assertEquals(null, test.getCorrectionToInstant());
    final ManageableLegalEntity testLegalEntity = test.getLegalEntity();
    assertNotNull(testLegalEntity);
    assertEquals(uniqueId, testLegalEntity.getUniqueId());
    assertEquals("TestLegalEntity", legalEntity.getName());
    final ExternalIdBundle idKey = legalEntity.getExternalIdBundle();
    assertNotNull(idKey);
    assertEquals(1, idKey.size());
    assertEquals(ExternalId.of("A", "B"), idKey.getExternalIds().iterator().next());
  }

  @Test
  public void test_add_addThenGet() {
    final ManageableLegalEntity legalEntity = new MockLegalEntity("TestLegalEntity", ExternalIdBundle.of("A", "B"), Currency.GBP);
    final LegalEntityDocument doc = new LegalEntityDocument();
    doc.setLegalEntity(legalEntity);
    final LegalEntityDocument added = _lenMaster.add(doc);

    final LegalEntityDocument test = _lenMaster.get(added.getUniqueId());
    assertEquals(added, test);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_add_addWithMissingNameProperty() throws Exception {
    final ManageableLegalEntity legalEntity = new MockLegalEntity("TestLegalEntity", ExternalIdBundle.of("A", "B"), Currency.GBP);
    final Field field = ManageableLegalEntity.class.getDeclaredField("_name");
    field.setAccessible(true);
    field.set(legalEntity, null);
    final LegalEntityDocument doc = new LegalEntityDocument(legalEntity);
    _lenMaster.add(doc);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_add_addWithMissingExternalIdBundleProperty() throws Exception {
    final ManageableLegalEntity legalEntity = new MockLegalEntity("TestLegalEntity", ExternalIdBundle.of("A", "B"), Currency.GBP);
    final Field field = ManageableLegalEntity.class.getDeclaredField("_externalIdBundle");
    field.setAccessible(true);
    field.set(legalEntity, null);
    final LegalEntityDocument doc = new LegalEntityDocument(legalEntity);
    _lenMaster.add(doc);
  }

  @Test
  public void test_add_searchByAttribute() {
    final ManageableLegalEntity legalEntity = new MockLegalEntity("TestLegalEntity", ExternalIdBundle.of("A", "B"), Currency.GBP);
    legalEntity.addAttribute("city", "London");
    legalEntity.addAttribute("office", "Southern");
    final LegalEntityDocument added = _lenMaster.add(new LegalEntityDocument(legalEntity));

    final ManageableLegalEntity legalEntity2 = new MockLegalEntity("TestLegalEntity2", ExternalIdBundle.of("A", "B"), Currency.GBP);
    legalEntity2.addAttribute("office", "Southern");
    final LegalEntityDocument added2 = _lenMaster.add(new LegalEntityDocument(legalEntity2));

    LegalEntitySearchRequest searchRequest = new LegalEntitySearchRequest();
    searchRequest.addAttribute("city", "London");
    LegalEntitySearchResult searchResult = _lenMaster.search(searchRequest);
    assertEquals(1, searchResult.getDocuments().size());
    assertEquals(added, searchResult.getDocuments().get(0));

    searchRequest = new LegalEntitySearchRequest();
    searchRequest.setSortOrder(LegalEntitySearchSortOrder.NAME_ASC);
    searchRequest.addAttribute("office", "Southern");
    searchResult = _lenMaster.search(searchRequest);
    assertEquals(2, searchResult.getDocuments().size());
    assertEquals(added, searchResult.getDocuments().get(0));
    assertEquals(added2, searchResult.getDocuments().get(1));

    searchRequest = new LegalEntitySearchRequest();
    searchRequest.addAttribute("city", "London");
    searchRequest.addAttribute("office", "*thern");
    searchResult = _lenMaster.search(searchRequest);
    assertEquals(1, searchResult.getDocuments().size());
    assertEquals(added, searchResult.getDocuments().get(0));
  }

  //-------------------------------------------------------------------------
  //-------------------------------------------------------------------------
  //-------------------------------------------------------------------------
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_updateLegalEntity_nullDocument() {
    _lenMaster.update(null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_update_noLegalEntityId() {
    final UniqueId uniqueId = UniqueId.of("DbLen", "101");
    final ManageableLegalEntity legalEntity = new MockLegalEntity("TestLegalEntity", ExternalIdBundle.of("A", "B"), Currency.GBP);
    legalEntity.setUniqueId(uniqueId);
    final LegalEntityDocument doc = new LegalEntityDocument();
    doc.setLegalEntity(legalEntity);
    _lenMaster.update(doc);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_update_noLegalEntity() {
    final LegalEntityDocument doc = new LegalEntityDocument();
    doc.setUniqueId(UniqueId.of("DbLen", "101", "0"));
    _lenMaster.update(doc);
  }

  @Test(expectedExceptions = DataNotFoundException.class)
  public void test_update_notFound() {
    final UniqueId uniqueId = UniqueId.of("DbLen", "0", "0");
    final ManageableLegalEntity legalEntity = new MockLegalEntity("TestLegalEntity", ExternalIdBundle.of("A", "B"), Currency.GBP);
    legalEntity.setUniqueId(uniqueId);
    final LegalEntityDocument doc = new LegalEntityDocument(legalEntity);
    _lenMaster.update(doc);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_update_notLatestVersion() {
    final UniqueId uniqueId = UniqueId.of("DbLen", "201", "0");
    final ManageableLegalEntity legalEntity = new MockLegalEntity("TestLegalEntity", ExternalIdBundle.of("A", "B"), Currency.GBP);
    legalEntity.setUniqueId(uniqueId);
    final LegalEntityDocument doc = new LegalEntityDocument(legalEntity);
    _lenMaster.update(doc);
  }

  @Test
  public void test_update_getUpdateGet() {
    final Instant now = Instant.now(_lenMaster.getClock());

    final UniqueId uniqueId = UniqueId.of("DbLen", "101", "0");
    final LegalEntityDocument base = _lenMaster.get(uniqueId);
    final ManageableLegalEntity legalEntity = new MockLegalEntity("TestLegalEntity", ExternalIdBundle.of("A", "B"), Currency.GBP);
    legalEntity.setUniqueId(uniqueId);
    final LegalEntityDocument input = new LegalEntityDocument(legalEntity);

    final LegalEntityDocument updated = _lenMaster.update(input);
    assertEquals(false, base.getUniqueId().equals(updated.getUniqueId()));
    assertEquals(now, updated.getVersionFromInstant());
    assertEquals(null, updated.getVersionToInstant());
    assertEquals(now, updated.getCorrectionFromInstant());
    assertEquals(null, updated.getCorrectionToInstant());
    assertEquals(input.getLegalEntity(), updated.getLegalEntity());

    final LegalEntityDocument old = _lenMaster.get(uniqueId);
    assertEquals(base.getUniqueId(), old.getUniqueId());
    assertEquals(base.getVersionFromInstant(), old.getVersionFromInstant());
    assertEquals(now, old.getVersionToInstant());  // old version ended
    assertEquals(base.getCorrectionFromInstant(), old.getCorrectionFromInstant());
    assertEquals(base.getCorrectionToInstant(), old.getCorrectionToInstant());
    assertEquals(base.getLegalEntity(), old.getLegalEntity());

    final LegalEntityHistoryRequest search = new LegalEntityHistoryRequest(base.getUniqueId(), null, now);
    final LegalEntityHistoryResult searchResult = _lenMaster.history(search);
    assertEquals(2, searchResult.getDocuments().size());
  }

  @Test
  public void test_update_rollback() {
    final DbLegalEntityBeanMaster w = new DbLegalEntityBeanMaster(_lenMaster.getDbConnector());
    w.setElSqlBundle(ElSqlBundle.of(new ElSqlConfig("TestRollback"), DbBeanMaster.class));
    final LegalEntityDocument base = _lenMaster.get(UniqueId.of("DbLen", "101", "0"));
    final UniqueId uniqueId = UniqueId.of("DbLen", "101", "0");
    final ManageableLegalEntity legalEntity = new MockLegalEntity("TestLegalEntity", ExternalIdBundle.of("A", "B"), Currency.GBP);
    legalEntity.setUniqueId(uniqueId);
    final LegalEntityDocument input = new LegalEntityDocument(legalEntity);
    try {
      w.update(input);
      Assert.fail();
    } catch (final BadSqlGrammarException ex) {
      // expected
    }
    final LegalEntityDocument test = _lenMaster.get(UniqueId.of("DbLen", "101", "0"));

    assertEquals(base, test);
  }

  //-------------------------------------------------------------------------
  //-------------------------------------------------------------------------
  //-------------------------------------------------------------------------
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_correctLegalEntity_nullDocument() {
    _lenMaster.correct(null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_correct_noLegalEntityId() {
    final UniqueId uniqueId = UniqueId.of("DbLen", "101");
    final ManageableLegalEntity legalEntity = new MockLegalEntity("TestLegalEntity", ExternalIdBundle.of("A", "B"), Currency.GBP);
    legalEntity.setUniqueId(uniqueId);
    final LegalEntityDocument doc = new LegalEntityDocument();
    doc.setLegalEntity(legalEntity);
    _lenMaster.correct(doc);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_correct_noLegalEntity() {
    final LegalEntityDocument doc = new LegalEntityDocument();
    doc.setUniqueId(UniqueId.of("DbLen", "101", "0"));
    _lenMaster.correct(doc);
  }

  @Test(expectedExceptions = DataNotFoundException.class)
  public void test_correct_notFound() {
    final UniqueId uniqueId = UniqueId.of("DbLen", "0", "0");
    final ManageableLegalEntity legalEntity = new MockLegalEntity("TestLegalEntity", ExternalIdBundle.of("A", "B"), Currency.GBP);
    legalEntity.setUniqueId(uniqueId);
    final LegalEntityDocument doc = new LegalEntityDocument(legalEntity);
    _lenMaster.correct(doc);
  }

  @Test
  public void test_correct_getUpdateGet() {
    final Instant now = Instant.now(_lenMaster.getClock());

    final UniqueId uniqueId = UniqueId.of("DbLen", "101", "0");
    final LegalEntityDocument base = _lenMaster.get(uniqueId);
    final ManageableLegalEntity legalEntity = new MockLegalEntity("TestLegalEntity", ExternalIdBundle.of("A", "B"), Currency.GBP);
    legalEntity.setUniqueId(uniqueId);
    final LegalEntityDocument input = new LegalEntityDocument(legalEntity);

    final LegalEntityDocument corrected = _lenMaster.correct(input);
    assertEquals(false, base.getUniqueId().equals(corrected.getUniqueId()));
    assertEquals(base.getVersionFromInstant(), corrected.getVersionFromInstant());
    assertEquals(base.getVersionToInstant(), corrected.getVersionToInstant());
    assertEquals(now, corrected.getCorrectionFromInstant());
    assertEquals(null, corrected.getCorrectionToInstant());
    assertEquals(input.getLegalEntity(), corrected.getLegalEntity());

    final LegalEntityDocument old = _lenMaster.get(UniqueId.of("DbLen", "101", "0"));
    assertEquals(base.getUniqueId(), old.getUniqueId());
    assertEquals(base.getVersionFromInstant(), old.getVersionFromInstant());
    assertEquals(base.getVersionToInstant(), old.getVersionToInstant());
    assertEquals(base.getCorrectionFromInstant(), old.getCorrectionFromInstant());
    assertEquals(now, old.getCorrectionToInstant());  // old version ended
    assertEquals(base.getLegalEntity(), old.getLegalEntity());

    final LegalEntityHistoryRequest search = new LegalEntityHistoryRequest(base.getUniqueId(), now, null);
    final LegalEntityHistoryResult searchResult = _lenMaster.history(search);
    assertEquals(2, searchResult.getDocuments().size());
  }

  //-------------------------------------------------------------------------
  //-------------------------------------------------------------------------
  //-------------------------------------------------------------------------
  @Test(expectedExceptions = DataNotFoundException.class)
  public void test_removeLegalEntity_versioned_notFound() {
    final UniqueId uniqueId = UniqueId.of("DbLen", "0", "0");
    _lenMaster.remove(uniqueId);
  }

  @Test
  public void test_remove_removed() {
    final Instant now = Instant.now(_lenMaster.getClock());

    final UniqueId uniqueId = UniqueId.of("DbLen", "101", "0");
    _lenMaster.remove(uniqueId);
    final LegalEntityDocument test = _lenMaster.get(uniqueId);

    assertEquals(uniqueId, test.getUniqueId());
    assertEquals(_version1Instant, test.getVersionFromInstant());
    assertEquals(now, test.getVersionToInstant());
    assertEquals(_version1Instant, test.getCorrectionFromInstant());
    assertEquals(null, test.getCorrectionToInstant());
    final ManageableLegalEntity legalEntity = test.getLegalEntity();
    assertNotNull(legalEntity);
    assertEquals(uniqueId, legalEntity.getUniqueId());
    assertEquals("TestLegalEntity101", legalEntity.getName());
    assertEquals(ExternalIdBundle.of(ExternalId.of("A", "B"), ExternalId.of("C", "D"), ExternalId.of("E", "F")), legalEntity.getExternalIdBundle());
  }

}
