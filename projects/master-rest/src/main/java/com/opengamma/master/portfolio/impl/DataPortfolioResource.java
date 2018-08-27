/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * Copyright (C) 2015 - present by McLeod Moores Software Limited.
 *
 * Please see distribution for license.
 */
package com.opengamma.master.portfolio.impl;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.opengamma.id.ObjectId;
import com.opengamma.master.AbstractDocumentDataResource;
import com.opengamma.master.portfolio.PortfolioDocument;
import com.opengamma.master.portfolio.PortfolioHistoryRequest;
import com.opengamma.master.portfolio.PortfolioHistoryResult;
import com.opengamma.master.portfolio.PortfolioMaster;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.rest.RestUtils;

/**
 * RESTful resource for a portfolio.
 */
public class DataPortfolioResource
    extends AbstractDocumentDataResource<PortfolioDocument> {

  /**
   * The portfolios resource.
   */
  private final DataPortfolioMasterResource _portfoliosResource;
  /**
   * The identifier specified in the URI.
   */
  private ObjectId _urlResourceId;

  /**
   * Creates dummy resource for the purpose of url resolution.
   *
   */
  DataPortfolioResource() {
    _portfoliosResource = null;
  }

  /**
   * Creates the resource.
   *
   * @param portfoliosResource  the parent resource, not null
   * @param portfolioId  the portfolio unique identifier, not null
   */
  public DataPortfolioResource(final DataPortfolioMasterResource portfoliosResource, final ObjectId portfolioId) {
    ArgumentChecker.notNull(portfoliosResource, "portfoliosResource");
    ArgumentChecker.notNull(portfolioId, "portfolio");
    _portfoliosResource = portfoliosResource;
    _urlResourceId = portfolioId;
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the portfolios resource.
   *
   * @return the portfolios resource, not null
   */
  public DataPortfolioMasterResource getPortfoliosResource() {
    return _portfoliosResource;
  }

  /**
   * Gets the portfolio identifier from the URL.
   *
   * @return the unique identifier, not null
   */
  @Override
  public ObjectId getUrlId() {
    return _urlResourceId;
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the portfolio master.
   *
   * @return the portfolio master, not null
   */
  @Override
  public PortfolioMaster getMaster() {
    return getPortfoliosResource().getPortfolioMaster();
  }

  @GET
  @Path("versions")
  public Response history(@Context final UriInfo uriInfo) {
    final PortfolioHistoryRequest request = RestUtils.decodeQueryParams(uriInfo, PortfolioHistoryRequest.class);
    if (getUrlId().equals(request.getObjectId()) == false) {
      throw new IllegalArgumentException("Document objectId does not match URI");
    }
    final PortfolioHistoryResult result = getMaster().history(request);
    return responseOkObject(result);
  }

  @Override
  @GET
  public Response get(@QueryParam("versionAsOf") final String versionAsOf, @QueryParam("correctedTo") final String correctedTo) {
    return super.get(versionAsOf, correctedTo);
  }

  @Override
  @POST
  public Response update(@Context final UriInfo uriInfo, final PortfolioDocument request) {
    return super.update(uriInfo, request);
  }

  @Override
  @DELETE
  public void remove() {
    super.remove();
  }

  @Override
  @GET
  @Path("versions/{versionId}")
  public Response getVersioned(@PathParam("versionId") final String versionId) {
    return super.getVersioned(versionId);
  }


  @Override
  @PUT
  @Path("versions/{versionId}")
  public Response replaceVersion(@PathParam("versionId") final String versionId, final List<PortfolioDocument> replacementDocuments) {
    return super.replaceVersion(versionId, replacementDocuments);
  }

  @Override
  @PUT
  public Response replaceVersions(final List<PortfolioDocument> replacementDocuments) {
    return super.replaceVersions(replacementDocuments);
  }

  @Override
  @PUT
  @Path("all")
  public Response replaceAllVersions(final List<PortfolioDocument> replacementDocuments) {
    return super.replaceAllVersions(replacementDocuments);
  }

  @Override
  protected String getResourceName() {
    return "portfolios";
  }

}
