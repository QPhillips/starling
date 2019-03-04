/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

package com.opengamma.integration.tool.enginedebugger;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang.ObjectUtils;
import org.jdesktop.swingx.JXDatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

import com.opengamma.engine.marketdata.spec.FixedHistoricalMarketDataSpecification;
import com.opengamma.engine.marketdata.spec.LatestHistoricalMarketDataSpecification;
import com.opengamma.engine.marketdata.spec.MarketDataSpecification;
import com.opengamma.engine.marketdata.spec.UserMarketDataSpecification;
import com.opengamma.id.ObjectId;
import com.opengamma.id.UniqueId;
import com.opengamma.integration.swing.HistoricalMarketDataSpecificationListModel;
import com.opengamma.integration.swing.LiveMarketDataSpecificationListModel;
import com.opengamma.integration.swing.SnapshotMarketDataSpecificationListModel;
import com.opengamma.integration.swing.SnapshotMarketDataSpecificationVersionListModel;
import com.opengamma.master.config.ConfigMaster;
import com.opengamma.master.marketdatasnapshot.MarketDataSnapshotMaster;
import com.opengamma.provider.livedata.LiveDataMetaDataProvider;

import net.miginfocom.swing.MigLayout;

/**
 * Component representing a market data specification
 */
public class MarketDataSpecificationComponent extends JPanel {
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(MarketDataSpecificationComponent.class);
  private static final String SNAPSHOT = "Snapshot";
  private static final String HISTORICAL = "Historical";
  private static final String LIVE = "Live";
  private static final String DATA_SOURCE_TYPE_PROMPT = "select type...";
  private static final String[] VALID_ITEMS = new String[] {DATA_SOURCE_TYPE_PROMPT, LIVE, HISTORICAL, SNAPSHOT};
  private static final String DATA_SOURCE_PROMPT = "select data source...";
  private static final String SNAPSHOT_VERSION_PROMPT = "select snapshot version...";
  private static final String LATEST = "Latest";
  private static final String FIXED = "Fixed";

  private static final int HEIGHT = 24;

  private static final Dimension DATA_SOURCE_TYPE_PREFERRED_SIZE = new Dimension(150, HEIGHT);

  private static final Dimension SNAPSHOT_VERSION_PREFERRED_SIZE = new Dimension(250, HEIGHT);

  private static final Dimension DATA_SOURCE_PREFERRED_SIZE = new Dimension(350, HEIGHT);

  private static final Dimension COMPONENT_PREFERRED_SIZE = new Dimension(960, HEIGHT);

  // main controls
  private final JComboBox<String> _dataSourceTypeCombo;
  private final JComboBox<String> _dataSourceCombo;
  private final JComboBox<String> _snapshotVersionCombo;
  private final JXDatePicker _datePicker;

  // incidental controls
  private JRadioButton _latestRadio;
  private JRadioButton _fixedRadio;

  // models
  private LiveMarketDataSpecificationListModel _liveModel;
  private HistoricalMarketDataSpecificationListModel _historicalModel;
  private SnapshotMarketDataSpecificationListModel _snapshotModel;

  // externally injected sources/data
  private final List<LiveDataMetaDataProvider> _metaDataProviders;
  private final ConfigMaster _configMaster;
  private final MarketDataSnapshotMaster _snapshotMaster;

  // state
  private MarketDataSpecification _currentState;
  private final Set<ChangeListener> _listeners = new LinkedHashSet<>();

  public MarketDataSpecificationComponent(final List<LiveDataMetaDataProvider> metaDataProviders, final ConfigMaster configMaster, final MarketDataSnapshotMaster snapshotMaster) {
    super();
    _configMaster = configMaster;
    _snapshotMaster = snapshotMaster;
    _metaDataProviders = metaDataProviders;
    _dataSourceTypeCombo = createDataSourceTypeCombo();
    _dataSourceCombo = createDataSourceCombo();
    _snapshotVersionCombo = createSnapshotVersionCombo();
    _datePicker = new JXDatePicker();
    _datePicker.setPreferredSize(SNAPSHOT_VERSION_PREFERRED_SIZE);
    _datePicker.setMinimumSize(SNAPSHOT_VERSION_PREFERRED_SIZE);
    _datePicker.setMaximumSize(SNAPSHOT_VERSION_PREFERRED_SIZE);
    createModels();
    addComponents();
    setPreferredSize(COMPONENT_PREFERRED_SIZE);
  }

  private void createModels() {
    _liveModel = new LiveMarketDataSpecificationListModel(_metaDataProviders);
    _historicalModel = new HistoricalMarketDataSpecificationListModel(_configMaster);
    _snapshotModel = new SnapshotMarketDataSpecificationListModel(_snapshotMaster);
  }

  public void addComponents() {
    setLayout(new MigLayout("insets 0"));
    _dataSourceTypeCombo.setVisible(true);
    add(_dataSourceTypeCombo, "align left");
    _dataSourceCombo.setVisible(true);
    add(_dataSourceCombo, "align center, growx");
    _latestRadio = new JRadioButton(LATEST);
    _latestRadio.setVisible(false);
    _latestRadio.setActionCommand(LATEST);
    _latestRadio.addActionListener(_radioActionListener);
    _latestRadio.setSelected(true);
    _fixedRadio = new JRadioButton(FIXED);
    _fixedRadio.setVisible(false);
    _fixedRadio.setActionCommand(FIXED);
    _fixedRadio.addActionListener(_radioActionListener);
    final ButtonGroup group = new ButtonGroup();
    group.add(_latestRadio);
    group.add(_fixedRadio);
    add(_latestRadio, "align right");
    add(_fixedRadio, "align right");
    _datePicker.setEnabled(false);
    _datePicker.addActionListener(_historicalActionListener);
  }

  @SuppressWarnings("unused")
  private void showSnapshotVersionPicker(final ObjectId id) {
    _latestRadio.setVisible(true);
    _fixedRadio.setVisible(true);
    _snapshotVersionCombo.setModel(new SnapshotMarketDataSpecificationVersionListModel(_snapshotMaster, id));
    _snapshotVersionCombo.setVisible(true);
    remove(_datePicker);
    add(_snapshotVersionCombo, "align right");
    validate();
  }

  @SuppressWarnings("unused")
  private void showSnapshotNamePicker() {
    _latestRadio.setVisible(false);
    _fixedRadio.setVisible(false);
    _dataSourceCombo.setModel(_snapshotModel);
    _dataSourceCombo.addActionListener(_snapshotActionListener);
    _dataSourceCombo.removeActionListener(_historicalActionListener);
    _dataSourceCombo.removeActionListener(_liveActionListener);
    _dataSourceCombo.setVisible(true);
    remove(_snapshotVersionCombo);
    remove(_datePicker);
    validate();
  }

  @SuppressWarnings("unused")
  private void showHistoricalDatePicker() {
    _snapshotVersionCombo.setVisible(false);
    _snapshotVersionCombo.setModel(new DefaultComboBoxModel<>(new String[] {SNAPSHOT_VERSION_PROMPT }));
    _dataSourceCombo.setModel(_historicalModel);
    _dataSourceCombo.removeActionListener(_liveActionListener);
    _dataSourceCombo.removeActionListener(_snapshotActionListener);
    _dataSourceCombo.addActionListener(_historicalActionListener);
    _dataSourceCombo.setVisible(true);
    _latestRadio.setVisible(true);
    _fixedRadio.setVisible(true);
    remove(_snapshotVersionCombo);
    add(_datePicker, "align right");
    validate();
  }

  @SuppressWarnings("unused")
  private void showLive() {
    _latestRadio.setVisible(false);
    _fixedRadio.setVisible(false);
    _dataSourceCombo.setModel(_liveModel);
    _dataSourceCombo.addActionListener(_liveActionListener);
    _dataSourceCombo.setVisible(true);
    _snapshotVersionCombo.setModel(new DefaultComboBoxModel<>(new String[] {SNAPSHOT_VERSION_PROMPT }));
    remove(_datePicker);
    remove(_snapshotVersionCombo);
    validate();
  }

  private void validSpecification(final MarketDataSpecification marketDataSpec) {
    LOGGER.warn("valid specification {}", marketDataSpec);
    if (!ObjectUtils.equals(_currentState, marketDataSpec)) {
      _currentState = marketDataSpec;
      fireStateChanged();
    }
  }

  private void invalidSpecification() {
    LOGGER.warn("invalid specification");
    _currentState = null;
    fireStateChanged();
  }

  private final ActionListener _radioActionListener = new ActionListener() {
    @Override
    public void actionPerformed(final ActionEvent e) {
      LOGGER.warn("radio action triggered");
      switch (e.getActionCommand()) {
        case LATEST:
          _datePicker.setEnabled(false);
          _snapshotVersionCombo.setEnabled(false);
          break;
        case FIXED:
          _datePicker.setEnabled(true);
          _snapshotVersionCombo.setEnabled(true);;
          break;
      }
      switch ((String) _dataSourceTypeCombo.getSelectedItem()) {
        case HISTORICAL:
          historySelected();
          break;
        case SNAPSHOT:
          switch (e.getActionCommand()) {
            case LATEST:
              snapshotSelected();
              break;
            case FIXED:
              snapshotVersionSelected();
              break;
          }
          break;
        case LIVE:
          liveSelected();
          break;
      }
    }
  };

  private final ActionListener _liveActionListener = new ActionListener() {
    @Override
    public void actionPerformed(final ActionEvent e) {
      liveSelected();
    }
  };

  private void liveSelected() {
    LOGGER.warn("live selected");
    final JComboBox<String> source = _dataSourceCombo;
    final String item = (String) source.getSelectedItem();
    final MarketDataSpecification marketDataSpec = MarketDataSpecificationComponent.this._liveModel.getMarketDataSpec(item);
    validSpecification(marketDataSpec);
  }

  private final ActionListener _historicalActionListener = new ActionListener() {
    @Override
    public void actionPerformed(final ActionEvent e) {
      historySelected();
    }
  };

  private void historySelected() {
    LOGGER.warn("history selected");
    final MarketDataSpecificationComponent outer = MarketDataSpecificationComponent.this;
    @SuppressWarnings("unchecked")
    final
    JComboBox<String> source = _dataSourceCombo;
    final String item = (String) source.getSelectedItem();
    if (item != null) {
      MarketDataSpecification marketDataSpec;
      if (_latestRadio.isSelected()) {
        marketDataSpec = new LatestHistoricalMarketDataSpecification(item);
        validSpecification(marketDataSpec);
        return;
      }
      final Date datePickerDate = outer._datePicker.getDate();
      if (item.length() > 0 && datePickerDate != null) {
        final LocalDate localDate = Instant.ofEpochMilli(datePickerDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        marketDataSpec = new FixedHistoricalMarketDataSpecification(item, localDate);
        validSpecification(marketDataSpec);
        return;
      }
    }
    invalidSpecification();
  }

  private final ActionListener _snapshotActionListener = new ActionListener() {
    @Override
    public void actionPerformed(final ActionEvent e) {
      snapshotSelected();
    }
  };

  private void snapshotSelected() {
    LOGGER.warn("snapshot selected");
    final JComboBox<String> source = _dataSourceCombo;
    final int index = source.getSelectedIndex();
    if (index >= 0) {
      final ObjectId oid = MarketDataSpecificationComponent.this._snapshotModel.getObjectIdAt(index);
      if (oid != null) {
        final MarketDataSpecification marketDataSpec = UserMarketDataSpecification.of(oid.atLatestVersion());
        showSnapshotVersionPicker(oid);
        if (_latestRadio.isSelected()) {
          validSpecification(marketDataSpec);
          return;
        }
      }
    }
    invalidSpecification();
  }


  private final ActionListener _snapshotVersionActionListener = new ActionListener() {
    @Override
    public void actionPerformed(final ActionEvent e) {
      snapshotVersionSelected();
    }
  };

  private void snapshotVersionSelected() {
    LOGGER.warn("snapshot version selected");
    final int index = _snapshotVersionCombo.getSelectedIndex();
    if (index >= 0) {
      final ObjectId oid = MarketDataSpecificationComponent.this._snapshotModel.getObjectIdAt(index);
      if (_latestRadio.isSelected()) {
        final MarketDataSpecification marketDataSpec = UserMarketDataSpecification.of(oid.atLatestVersion());
        MarketDataSpecificationComponent.this._currentState = marketDataSpec;
        validSpecification(marketDataSpec);
        return;
      }
      final int selectedIndex = _snapshotVersionCombo.getSelectedIndex();
      if (selectedIndex >= 0 && _snapshotVersionCombo.getModel() instanceof SnapshotMarketDataSpecificationVersionListModel) {
        final SnapshotMarketDataSpecificationVersionListModel model = (SnapshotMarketDataSpecificationVersionListModel) _snapshotVersionCombo.getModel();
        final UniqueId uid = model.getUniqueIdAt(selectedIndex);
        final MarketDataSpecification marketDataSpec = UserMarketDataSpecification.of(uid);
        validSpecification(marketDataSpec);
        return;
      }
    }
    invalidSpecification();
  }

  public JComboBox<String> createDataSourceCombo() {
    final JComboBox<String> comboBox = new JComboBox<>();
    comboBox.setModel(new DefaultComboBoxModel<>(new String[] {DATA_SOURCE_PROMPT }));
    comboBox.setPreferredSize(DATA_SOURCE_PREFERRED_SIZE);
    comboBox.setMaximumSize(DATA_SOURCE_PREFERRED_SIZE);
    comboBox.setMinimumSize(DATA_SOURCE_PREFERRED_SIZE);
    return comboBox;
  }

  public JComboBox<String> createSnapshotVersionCombo() {
    final JComboBox<String> comboBox = new JComboBox<>();
    comboBox.setModel(new DefaultComboBoxModel<>(new String[] {SNAPSHOT_VERSION_PROMPT }));
    comboBox.addActionListener(_snapshotVersionActionListener);
    comboBox.setPreferredSize(SNAPSHOT_VERSION_PREFERRED_SIZE);
    comboBox.setMaximumSize(SNAPSHOT_VERSION_PREFERRED_SIZE);
    comboBox.setMinimumSize(SNAPSHOT_VERSION_PREFERRED_SIZE);
    return comboBox;
  }

  public JComboBox<String> createDataSourceTypeCombo() {
    final JComboBox<String> comboBox = new JComboBox<>();
    comboBox.setModel(new DefaultComboBoxModel<>(VALID_ITEMS));
    comboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        @SuppressWarnings("unchecked")
        final
        JComboBox<String> source = (JComboBox<String>) e.getSource();
        final String item = (String) source.getSelectedItem();
        switch (item) {
          case LIVE:
            showLive();
            liveSelected();
            break;
          case HISTORICAL:
            showHistoricalDatePicker();
            historySelected();
            break;
          case SNAPSHOT:
            showSnapshotNamePicker();
            snapshotSelected();
            break;
        }
      }
    });
    comboBox.setPreferredSize(DATA_SOURCE_TYPE_PREFERRED_SIZE);
    comboBox.setMinimumSize(DATA_SOURCE_TYPE_PREFERRED_SIZE);
    comboBox.setMaximumSize(DATA_SOURCE_TYPE_PREFERRED_SIZE);
    return comboBox;
  }

  public void addChangeListener(final ChangeListener listener) {
    _listeners.add(listener);
  }

  public void removeChangeListener(final ChangeListener listener) {
    _listeners.remove(listener);
  }

  protected void fireStateChanged() {
    LOGGER.warn("state changed");
    for (final ChangeListener listener : _listeners) {
      listener.stateChanged(new ChangeEvent(this));
    }
  }

  public MarketDataSpecification getCurrentState() {
    return _currentState;
  }

}

