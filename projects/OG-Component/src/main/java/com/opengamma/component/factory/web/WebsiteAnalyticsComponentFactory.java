/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.component.factory.web;

import java.util.LinkedHashMap;
import java.util.Map;

import org.fudgemsg.FudgeContext;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.component.ComponentRepository;
import com.opengamma.component.factory.AbstractComponentFactory;
import com.opengamma.core.position.PositionSource;
import com.opengamma.core.security.SecuritySource;
import com.opengamma.engine.ComputationTargetResolver;
import com.opengamma.engine.view.ViewProcessor;
import com.opengamma.financial.aggregation.PortfolioAggregationFunctions;
import com.opengamma.livedata.UserPrincipal;
import com.opengamma.master.config.ConfigMaster;
import com.opengamma.master.marketdatasnapshot.MarketDataSnapshotMaster;
import com.opengamma.master.portfolio.PortfolioMaster;
import com.opengamma.master.position.PositionMaster;
import com.opengamma.util.ExecutorServiceFactoryBean;
import com.opengamma.util.ExecutorServiceFactoryBean.Style;
import com.opengamma.util.fudgemsg.OpenGammaFudgeContext;
import com.opengamma.web.server.LiveResultsServiceBean;
import com.opengamma.web.server.WebAnalyticsResource;

/**
 * Component factory for the main website.
 */
@BeanDefinition
public class WebsiteAnalyticsComponentFactory extends AbstractComponentFactory {

  /**
   * The security source.
   */
  @PropertyDefinition(validate = "notNull")
  private SecuritySource _securitySource;
  /**
   * The position source.
   */
  @PropertyDefinition(validate = "notNull")
  private PositionSource _positionSource;
  /**
   * The computation target resolver.
   */
  @PropertyDefinition(validate = "notNull")
  private ComputationTargetResolver _computationTargetResolver;
  /**
   * The user master.
   */
  @PropertyDefinition(validate = "notNull")
  private PositionMaster _userPositionMaster;
  /**
   * The user master.
   */
  @PropertyDefinition(validate = "notNull")
  private PortfolioMaster _userPortfolioMaster;
  /**
   * The user master.
   */
  @PropertyDefinition(validate = "notNull")
  private ConfigMaster _userConfigMaster;
  /**
   * The underlying master.
   */
  @PropertyDefinition(validate = "notNull")
  private MarketDataSnapshotMaster _snapshotMaster;
  /**
   * The view processor.
   */
  @PropertyDefinition(validate = "notNull")
  private ViewProcessor _viewProcessor;
  /**
   * The portfolio aggregation functions.
   */
  @PropertyDefinition(validate = "notNull")
  private PortfolioAggregationFunctions _portfolioAggregationFunctions;
  /**
   * The user.
   */
  @PropertyDefinition(validate = "notNull")
  private UserPrincipal _user;
  /**
   * The fudge context.
   */
  @PropertyDefinition(validate = "notNull")
  private FudgeContext _fudgeContext = OpenGammaFudgeContext.getInstance();

  //-------------------------------------------------------------------------
  @Override
  public void init(ComponentRepository repo, LinkedHashMap<String, String> configuration) {
    LiveResultsServiceBean bean = new LiveResultsServiceBean();
    bean.setViewProcessor(getViewProcessor());
    bean.setPositionSource(getPositionSource());
    bean.setSecuritySource(getSecuritySource());
    bean.setComputationTargetResolver(getComputationTargetResolver());
    bean.setUserPortfolioMaster(getUserPortfolioMaster());
    bean.setUserPositionMaster(getUserPositionMaster());
    bean.setUserConfigMaster(getUserConfigMaster());
    bean.setSnapshotMaster(getSnapshotMaster());
    bean.setPortfolioAggregators(getPortfolioAggregationFunctions());
    bean.setUser(getUser());
    bean.setFudgeContext(getFudgeContext());
    ExecutorServiceFactoryBean execBean = new ExecutorServiceFactoryBean();
    execBean.setStyle(Style.CACHED);
    bean.setExecutorService(execBean.getObjectCreating());
    
    WebAnalyticsResource resource = new WebAnalyticsResource(bean);
    repo.registerServletContextAware(resource);
    repo.getRestComponents().publishResource(resource);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code WebsiteAnalyticsComponentFactory}.
   * @return the meta-bean, not null
   */
  public static WebsiteAnalyticsComponentFactory.Meta meta() {
    return WebsiteAnalyticsComponentFactory.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(WebsiteAnalyticsComponentFactory.Meta.INSTANCE);
  }

  @Override
  public WebsiteAnalyticsComponentFactory.Meta metaBean() {
    return WebsiteAnalyticsComponentFactory.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -702456965:  // securitySource
        return getSecuritySource();
      case -1655657820:  // positionSource
        return getPositionSource();
      case 1562222174:  // computationTargetResolver
        return getComputationTargetResolver();
      case 1808868758:  // userPositionMaster
        return getUserPositionMaster();
      case 686514815:  // userPortfolioMaster
        return getUserPortfolioMaster();
      case -763459665:  // userConfigMaster
        return getUserConfigMaster();
      case -2046916282:  // snapshotMaster
        return getSnapshotMaster();
      case -1697555603:  // viewProcessor
        return getViewProcessor();
      case 940303425:  // portfolioAggregationFunctions
        return getPortfolioAggregationFunctions();
      case 3599307:  // user
        return getUser();
      case -917704420:  // fudgeContext
        return getFudgeContext();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -702456965:  // securitySource
        setSecuritySource((SecuritySource) newValue);
        return;
      case -1655657820:  // positionSource
        setPositionSource((PositionSource) newValue);
        return;
      case 1562222174:  // computationTargetResolver
        setComputationTargetResolver((ComputationTargetResolver) newValue);
        return;
      case 1808868758:  // userPositionMaster
        setUserPositionMaster((PositionMaster) newValue);
        return;
      case 686514815:  // userPortfolioMaster
        setUserPortfolioMaster((PortfolioMaster) newValue);
        return;
      case -763459665:  // userConfigMaster
        setUserConfigMaster((ConfigMaster) newValue);
        return;
      case -2046916282:  // snapshotMaster
        setSnapshotMaster((MarketDataSnapshotMaster) newValue);
        return;
      case -1697555603:  // viewProcessor
        setViewProcessor((ViewProcessor) newValue);
        return;
      case 940303425:  // portfolioAggregationFunctions
        setPortfolioAggregationFunctions((PortfolioAggregationFunctions) newValue);
        return;
      case 3599307:  // user
        setUser((UserPrincipal) newValue);
        return;
      case -917704420:  // fudgeContext
        setFudgeContext((FudgeContext) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  protected void validate() {
    JodaBeanUtils.notNull(_securitySource, "securitySource");
    JodaBeanUtils.notNull(_positionSource, "positionSource");
    JodaBeanUtils.notNull(_computationTargetResolver, "computationTargetResolver");
    JodaBeanUtils.notNull(_userPositionMaster, "userPositionMaster");
    JodaBeanUtils.notNull(_userPortfolioMaster, "userPortfolioMaster");
    JodaBeanUtils.notNull(_userConfigMaster, "userConfigMaster");
    JodaBeanUtils.notNull(_snapshotMaster, "snapshotMaster");
    JodaBeanUtils.notNull(_viewProcessor, "viewProcessor");
    JodaBeanUtils.notNull(_portfolioAggregationFunctions, "portfolioAggregationFunctions");
    JodaBeanUtils.notNull(_user, "user");
    JodaBeanUtils.notNull(_fudgeContext, "fudgeContext");
    super.validate();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      WebsiteAnalyticsComponentFactory other = (WebsiteAnalyticsComponentFactory) obj;
      return JodaBeanUtils.equal(getSecuritySource(), other.getSecuritySource()) &&
          JodaBeanUtils.equal(getPositionSource(), other.getPositionSource()) &&
          JodaBeanUtils.equal(getComputationTargetResolver(), other.getComputationTargetResolver()) &&
          JodaBeanUtils.equal(getUserPositionMaster(), other.getUserPositionMaster()) &&
          JodaBeanUtils.equal(getUserPortfolioMaster(), other.getUserPortfolioMaster()) &&
          JodaBeanUtils.equal(getUserConfigMaster(), other.getUserConfigMaster()) &&
          JodaBeanUtils.equal(getSnapshotMaster(), other.getSnapshotMaster()) &&
          JodaBeanUtils.equal(getViewProcessor(), other.getViewProcessor()) &&
          JodaBeanUtils.equal(getPortfolioAggregationFunctions(), other.getPortfolioAggregationFunctions()) &&
          JodaBeanUtils.equal(getUser(), other.getUser()) &&
          JodaBeanUtils.equal(getFudgeContext(), other.getFudgeContext()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getSecuritySource());
    hash += hash * 31 + JodaBeanUtils.hashCode(getPositionSource());
    hash += hash * 31 + JodaBeanUtils.hashCode(getComputationTargetResolver());
    hash += hash * 31 + JodaBeanUtils.hashCode(getUserPositionMaster());
    hash += hash * 31 + JodaBeanUtils.hashCode(getUserPortfolioMaster());
    hash += hash * 31 + JodaBeanUtils.hashCode(getUserConfigMaster());
    hash += hash * 31 + JodaBeanUtils.hashCode(getSnapshotMaster());
    hash += hash * 31 + JodaBeanUtils.hashCode(getViewProcessor());
    hash += hash * 31 + JodaBeanUtils.hashCode(getPortfolioAggregationFunctions());
    hash += hash * 31 + JodaBeanUtils.hashCode(getUser());
    hash += hash * 31 + JodaBeanUtils.hashCode(getFudgeContext());
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the security source.
   * @return the value of the property, not null
   */
  public SecuritySource getSecuritySource() {
    return _securitySource;
  }

  /**
   * Sets the security source.
   * @param securitySource  the new value of the property, not null
   */
  public void setSecuritySource(SecuritySource securitySource) {
    JodaBeanUtils.notNull(securitySource, "securitySource");
    this._securitySource = securitySource;
  }

  /**
   * Gets the the {@code securitySource} property.
   * @return the property, not null
   */
  public final Property<SecuritySource> securitySource() {
    return metaBean().securitySource().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the position source.
   * @return the value of the property, not null
   */
  public PositionSource getPositionSource() {
    return _positionSource;
  }

  /**
   * Sets the position source.
   * @param positionSource  the new value of the property, not null
   */
  public void setPositionSource(PositionSource positionSource) {
    JodaBeanUtils.notNull(positionSource, "positionSource");
    this._positionSource = positionSource;
  }

  /**
   * Gets the the {@code positionSource} property.
   * @return the property, not null
   */
  public final Property<PositionSource> positionSource() {
    return metaBean().positionSource().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the computation target resolver.
   * @return the value of the property, not null
   */
  public ComputationTargetResolver getComputationTargetResolver() {
    return _computationTargetResolver;
  }

  /**
   * Sets the computation target resolver.
   * @param computationTargetResolver  the new value of the property, not null
   */
  public void setComputationTargetResolver(ComputationTargetResolver computationTargetResolver) {
    JodaBeanUtils.notNull(computationTargetResolver, "computationTargetResolver");
    this._computationTargetResolver = computationTargetResolver;
  }

  /**
   * Gets the the {@code computationTargetResolver} property.
   * @return the property, not null
   */
  public final Property<ComputationTargetResolver> computationTargetResolver() {
    return metaBean().computationTargetResolver().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the user master.
   * @return the value of the property, not null
   */
  public PositionMaster getUserPositionMaster() {
    return _userPositionMaster;
  }

  /**
   * Sets the user master.
   * @param userPositionMaster  the new value of the property, not null
   */
  public void setUserPositionMaster(PositionMaster userPositionMaster) {
    JodaBeanUtils.notNull(userPositionMaster, "userPositionMaster");
    this._userPositionMaster = userPositionMaster;
  }

  /**
   * Gets the the {@code userPositionMaster} property.
   * @return the property, not null
   */
  public final Property<PositionMaster> userPositionMaster() {
    return metaBean().userPositionMaster().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the user master.
   * @return the value of the property, not null
   */
  public PortfolioMaster getUserPortfolioMaster() {
    return _userPortfolioMaster;
  }

  /**
   * Sets the user master.
   * @param userPortfolioMaster  the new value of the property, not null
   */
  public void setUserPortfolioMaster(PortfolioMaster userPortfolioMaster) {
    JodaBeanUtils.notNull(userPortfolioMaster, "userPortfolioMaster");
    this._userPortfolioMaster = userPortfolioMaster;
  }

  /**
   * Gets the the {@code userPortfolioMaster} property.
   * @return the property, not null
   */
  public final Property<PortfolioMaster> userPortfolioMaster() {
    return metaBean().userPortfolioMaster().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the user master.
   * @return the value of the property, not null
   */
  public ConfigMaster getUserConfigMaster() {
    return _userConfigMaster;
  }

  /**
   * Sets the user master.
   * @param userConfigMaster  the new value of the property, not null
   */
  public void setUserConfigMaster(ConfigMaster userConfigMaster) {
    JodaBeanUtils.notNull(userConfigMaster, "userConfigMaster");
    this._userConfigMaster = userConfigMaster;
  }

  /**
   * Gets the the {@code userConfigMaster} property.
   * @return the property, not null
   */
  public final Property<ConfigMaster> userConfigMaster() {
    return metaBean().userConfigMaster().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the underlying master.
   * @return the value of the property, not null
   */
  public MarketDataSnapshotMaster getSnapshotMaster() {
    return _snapshotMaster;
  }

  /**
   * Sets the underlying master.
   * @param snapshotMaster  the new value of the property, not null
   */
  public void setSnapshotMaster(MarketDataSnapshotMaster snapshotMaster) {
    JodaBeanUtils.notNull(snapshotMaster, "snapshotMaster");
    this._snapshotMaster = snapshotMaster;
  }

  /**
   * Gets the the {@code snapshotMaster} property.
   * @return the property, not null
   */
  public final Property<MarketDataSnapshotMaster> snapshotMaster() {
    return metaBean().snapshotMaster().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the view processor.
   * @return the value of the property, not null
   */
  public ViewProcessor getViewProcessor() {
    return _viewProcessor;
  }

  /**
   * Sets the view processor.
   * @param viewProcessor  the new value of the property, not null
   */
  public void setViewProcessor(ViewProcessor viewProcessor) {
    JodaBeanUtils.notNull(viewProcessor, "viewProcessor");
    this._viewProcessor = viewProcessor;
  }

  /**
   * Gets the the {@code viewProcessor} property.
   * @return the property, not null
   */
  public final Property<ViewProcessor> viewProcessor() {
    return metaBean().viewProcessor().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the portfolio aggregation functions.
   * @return the value of the property, not null
   */
  public PortfolioAggregationFunctions getPortfolioAggregationFunctions() {
    return _portfolioAggregationFunctions;
  }

  /**
   * Sets the portfolio aggregation functions.
   * @param portfolioAggregationFunctions  the new value of the property, not null
   */
  public void setPortfolioAggregationFunctions(PortfolioAggregationFunctions portfolioAggregationFunctions) {
    JodaBeanUtils.notNull(portfolioAggregationFunctions, "portfolioAggregationFunctions");
    this._portfolioAggregationFunctions = portfolioAggregationFunctions;
  }

  /**
   * Gets the the {@code portfolioAggregationFunctions} property.
   * @return the property, not null
   */
  public final Property<PortfolioAggregationFunctions> portfolioAggregationFunctions() {
    return metaBean().portfolioAggregationFunctions().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the user.
   * @return the value of the property, not null
   */
  public UserPrincipal getUser() {
    return _user;
  }

  /**
   * Sets the user.
   * @param user  the new value of the property, not null
   */
  public void setUser(UserPrincipal user) {
    JodaBeanUtils.notNull(user, "user");
    this._user = user;
  }

  /**
   * Gets the the {@code user} property.
   * @return the property, not null
   */
  public final Property<UserPrincipal> user() {
    return metaBean().user().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the fudge context.
   * @return the value of the property, not null
   */
  public FudgeContext getFudgeContext() {
    return _fudgeContext;
  }

  /**
   * Sets the fudge context.
   * @param fudgeContext  the new value of the property, not null
   */
  public void setFudgeContext(FudgeContext fudgeContext) {
    JodaBeanUtils.notNull(fudgeContext, "fudgeContext");
    this._fudgeContext = fudgeContext;
  }

  /**
   * Gets the the {@code fudgeContext} property.
   * @return the property, not null
   */
  public final Property<FudgeContext> fudgeContext() {
    return metaBean().fudgeContext().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code WebsiteAnalyticsComponentFactory}.
   */
  public static class Meta extends AbstractComponentFactory.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code securitySource} property.
     */
    private final MetaProperty<SecuritySource> _securitySource = DirectMetaProperty.ofReadWrite(
        this, "securitySource", WebsiteAnalyticsComponentFactory.class, SecuritySource.class);
    /**
     * The meta-property for the {@code positionSource} property.
     */
    private final MetaProperty<PositionSource> _positionSource = DirectMetaProperty.ofReadWrite(
        this, "positionSource", WebsiteAnalyticsComponentFactory.class, PositionSource.class);
    /**
     * The meta-property for the {@code computationTargetResolver} property.
     */
    private final MetaProperty<ComputationTargetResolver> _computationTargetResolver = DirectMetaProperty.ofReadWrite(
        this, "computationTargetResolver", WebsiteAnalyticsComponentFactory.class, ComputationTargetResolver.class);
    /**
     * The meta-property for the {@code userPositionMaster} property.
     */
    private final MetaProperty<PositionMaster> _userPositionMaster = DirectMetaProperty.ofReadWrite(
        this, "userPositionMaster", WebsiteAnalyticsComponentFactory.class, PositionMaster.class);
    /**
     * The meta-property for the {@code userPortfolioMaster} property.
     */
    private final MetaProperty<PortfolioMaster> _userPortfolioMaster = DirectMetaProperty.ofReadWrite(
        this, "userPortfolioMaster", WebsiteAnalyticsComponentFactory.class, PortfolioMaster.class);
    /**
     * The meta-property for the {@code userConfigMaster} property.
     */
    private final MetaProperty<ConfigMaster> _userConfigMaster = DirectMetaProperty.ofReadWrite(
        this, "userConfigMaster", WebsiteAnalyticsComponentFactory.class, ConfigMaster.class);
    /**
     * The meta-property for the {@code snapshotMaster} property.
     */
    private final MetaProperty<MarketDataSnapshotMaster> _snapshotMaster = DirectMetaProperty.ofReadWrite(
        this, "snapshotMaster", WebsiteAnalyticsComponentFactory.class, MarketDataSnapshotMaster.class);
    /**
     * The meta-property for the {@code viewProcessor} property.
     */
    private final MetaProperty<ViewProcessor> _viewProcessor = DirectMetaProperty.ofReadWrite(
        this, "viewProcessor", WebsiteAnalyticsComponentFactory.class, ViewProcessor.class);
    /**
     * The meta-property for the {@code portfolioAggregationFunctions} property.
     */
    private final MetaProperty<PortfolioAggregationFunctions> _portfolioAggregationFunctions = DirectMetaProperty.ofReadWrite(
        this, "portfolioAggregationFunctions", WebsiteAnalyticsComponentFactory.class, PortfolioAggregationFunctions.class);
    /**
     * The meta-property for the {@code user} property.
     */
    private final MetaProperty<UserPrincipal> _user = DirectMetaProperty.ofReadWrite(
        this, "user", WebsiteAnalyticsComponentFactory.class, UserPrincipal.class);
    /**
     * The meta-property for the {@code fudgeContext} property.
     */
    private final MetaProperty<FudgeContext> _fudgeContext = DirectMetaProperty.ofReadWrite(
        this, "fudgeContext", WebsiteAnalyticsComponentFactory.class, FudgeContext.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
      this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "securitySource",
        "positionSource",
        "computationTargetResolver",
        "userPositionMaster",
        "userPortfolioMaster",
        "userConfigMaster",
        "snapshotMaster",
        "viewProcessor",
        "portfolioAggregationFunctions",
        "user",
        "fudgeContext");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -702456965:  // securitySource
          return _securitySource;
        case -1655657820:  // positionSource
          return _positionSource;
        case 1562222174:  // computationTargetResolver
          return _computationTargetResolver;
        case 1808868758:  // userPositionMaster
          return _userPositionMaster;
        case 686514815:  // userPortfolioMaster
          return _userPortfolioMaster;
        case -763459665:  // userConfigMaster
          return _userConfigMaster;
        case -2046916282:  // snapshotMaster
          return _snapshotMaster;
        case -1697555603:  // viewProcessor
          return _viewProcessor;
        case 940303425:  // portfolioAggregationFunctions
          return _portfolioAggregationFunctions;
        case 3599307:  // user
          return _user;
        case -917704420:  // fudgeContext
          return _fudgeContext;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends WebsiteAnalyticsComponentFactory> builder() {
      return new DirectBeanBuilder<WebsiteAnalyticsComponentFactory>(new WebsiteAnalyticsComponentFactory());
    }

    @Override
    public Class<? extends WebsiteAnalyticsComponentFactory> beanType() {
      return WebsiteAnalyticsComponentFactory.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code securitySource} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<SecuritySource> securitySource() {
      return _securitySource;
    }

    /**
     * The meta-property for the {@code positionSource} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<PositionSource> positionSource() {
      return _positionSource;
    }

    /**
     * The meta-property for the {@code computationTargetResolver} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ComputationTargetResolver> computationTargetResolver() {
      return _computationTargetResolver;
    }

    /**
     * The meta-property for the {@code userPositionMaster} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<PositionMaster> userPositionMaster() {
      return _userPositionMaster;
    }

    /**
     * The meta-property for the {@code userPortfolioMaster} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<PortfolioMaster> userPortfolioMaster() {
      return _userPortfolioMaster;
    }

    /**
     * The meta-property for the {@code userConfigMaster} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ConfigMaster> userConfigMaster() {
      return _userConfigMaster;
    }

    /**
     * The meta-property for the {@code snapshotMaster} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<MarketDataSnapshotMaster> snapshotMaster() {
      return _snapshotMaster;
    }

    /**
     * The meta-property for the {@code viewProcessor} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ViewProcessor> viewProcessor() {
      return _viewProcessor;
    }

    /**
     * The meta-property for the {@code portfolioAggregationFunctions} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<PortfolioAggregationFunctions> portfolioAggregationFunctions() {
      return _portfolioAggregationFunctions;
    }

    /**
     * The meta-property for the {@code user} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UserPrincipal> user() {
      return _user;
    }

    /**
     * The meta-property for the {@code fudgeContext} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<FudgeContext> fudgeContext() {
      return _fudgeContext;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
