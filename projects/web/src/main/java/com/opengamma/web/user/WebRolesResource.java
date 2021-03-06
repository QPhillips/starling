/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.web.user;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.joda.beans.impl.flexi.FlexiBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.DataNotFoundException;
import com.opengamma.id.ObjectId;
import com.opengamma.master.user.ManageableRole;
import com.opengamma.master.user.RoleEventHistoryRequest;
import com.opengamma.master.user.RoleForm;
import com.opengamma.master.user.RoleFormError;
import com.opengamma.master.user.RoleFormException;
import com.opengamma.master.user.RoleSearchRequest;
import com.opengamma.master.user.RoleSearchResult;
import com.opengamma.master.user.RoleSearchSortOrder;
import com.opengamma.master.user.UserMaster;
import com.opengamma.util.paging.PagingRequest;
import com.opengamma.web.WebPaging;

/**
 * RESTful resource for all roles.
 * <p>
 * The roles resource represents the whole of a role master.
 */
@Path("/roles")
public class WebRolesResource extends AbstractWebRoleResource {

  /** Logger. */
  private static final Logger LOGGER = LoggerFactory.getLogger(WebRolesResource.class);
  /**
   * The ftl file.
   */
  private static final String ROLES_PAGE = HTML_DIR + "roles.ftl";
  /**
   * The ftl file.
   */
  private static final String ROLE_ADD_PAGE = HTML_DIR + "role-add.ftl";

  /**
   * Creates the resource.
   * 
   * @param userMaster
   *          the user master, not null
   */
  public WebRolesResource(final UserMaster userMaster) {
    super(userMaster);
  }

  // -------------------------------------------------------------------------
  /**
   * Produces an HTML GET request.
   *
   * @param pgIdx
   *          the paging first-item index, can be null
   * @param pgNum
   *          the paging page, can be null
   * @param pgSze
   *          the page size, can be null
   * @param sort
   *          the sorting type, can be null
   * @param rolename
   *          the role name, can be null
   * @param name
   *          the name, can be null
   * @param roleIdStrs
   *          the identifiers of the role, not null
   * @param uriInfo
   *          the URI info, not null
   * @return the Freemarker output
   */
  @GET
  @Produces(MediaType.TEXT_HTML)
  public String getHTML(
      @QueryParam("pgIdx") final Integer pgIdx,
      @QueryParam("pgNum") final Integer pgNum,
      @QueryParam("pgSze") final Integer pgSze,
      @QueryParam("sort") final String sort,
      @QueryParam("rolename") final String rolename,
      @QueryParam("name") final String name,
      @QueryParam("roleId") final List<String> roleIdStrs,
      @Context final UriInfo uriInfo) {
    final String trimmedSort = StringUtils.trimToNull(sort);
    final String trimmedRolename = StringUtils.trimToNull(rolename);
    final PagingRequest pr = buildPagingRequest(pgIdx, pgNum, pgSze);
    final RoleSearchSortOrder so = buildSortOrder(trimmedSort, RoleSearchSortOrder.NAME_ASC);
    final FlexiBean out = createSearchResultData(pr, so, trimmedRolename, roleIdStrs, uriInfo);
    return getFreemarker().build(ROLES_PAGE, out);
  }

  private FlexiBean createSearchResultData(
      final PagingRequest pr, final RoleSearchSortOrder so,
      final String rolename, final List<String> roleIdStrs, final UriInfo uriInfo) {
    final FlexiBean out = createRootData();

    final RoleSearchRequest searchRequest = new RoleSearchRequest();
    searchRequest.setPagingRequest(pr);
    searchRequest.setSortOrder(so);
    searchRequest.setRoleName(rolename);
    for (final String roleIdStr : roleIdStrs) {
      searchRequest.addObjectId(ObjectId.parse(roleIdStr));
    }
    out.put("searchRequest", searchRequest);

    if (data().getUriInfo().getQueryParameters().size() > 0) {
      final RoleSearchResult searchResult = data().getRoleMaster().search(searchRequest);
      out.put("searchResult", searchResult);
      out.put("paging", new WebPaging(searchResult.getPaging(), uriInfo));
    }
    return out;
  }

  // -------------------------------------------------------------------------
  /**
   * Creates an HTML POST response.
   *
   * @param roleName
   *          the role name, can be null
   * @param description
   *          the description, can be null
   * @param addRoles
   *          the roles to add, can be null
   * @param addPerms
   *          the permissions to add, can be null
   * @param addUsers
   *          the users to add, can be null
   * @return the Freemarket output
   */
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.TEXT_HTML)
  public Response postHTML(
      @FormParam("rolename") final String roleName,
      @FormParam("description") final String description,
      @FormParam("addroles") final String addRoles,
      @FormParam("addperms") final String addPerms,
      @FormParam("addusers") final String addUsers) {
    try {
      final RoleForm form = new RoleForm(roleName, description);
      form.setAddRoles(addRoles);
      form.setAddPermissions(addPerms);
      form.setAddUsers(addUsers);
      final ManageableRole added = form.add(data().getUserMaster());
      final URI uri = WebRoleResource.uri(data(), added.getRoleName());
      return Response.seeOther(uri).build();

    } catch (final RoleFormException ex) {
      ex.logUnexpected(LOGGER);
      final FlexiBean out = createRootData();
      out.put("rolename", roleName);
      out.put("description", description);
      out.put("addroles", addRoles);
      out.put("addperms", addPerms);
      out.put("addusers", addUsers);
      for (final RoleFormError error : ex.getErrors()) {
        out.put("err_" + error.toLowerCamel(), true);
      }
      return Response.ok(getFreemarker().build(ROLE_ADD_PAGE, out)).build();
    }
  }

  // -------------------------------------------------------------------------
  /**
   * Finds a role by name. If there is no role of that name, the history is searched if the master supports this functionality. If no value is found, an
   * exception is thrown.
   *
   * @param roleName
   *          the role name
   * @return the role
   */
  @Path("name/{roleName}")
  public WebRoleResource findRole(@PathParam("roleName") final String roleName) {
    data().setUriRoleName(roleName);
    try {
      final ManageableRole role = data().getRoleMaster().getByName(roleName);
      data().setRole(role);
    } catch (final DataNotFoundException ex) {
      final RoleEventHistoryRequest request = new RoleEventHistoryRequest(roleName);
      try {
        data().getRoleMaster().eventHistory(request);
        final ManageableRole role = new ManageableRole(roleName);
        data().setRole(role);
      } catch (final DataNotFoundException ex2) {
        throw ex;
      }
    }
    return new WebRoleResource(this);
  }

  // -------------------------------------------------------------------------
  /**
   * Creates the output root data.
   * 
   * @return the output root data, not null
   */
  @Override
  protected FlexiBean createRootData() {
    final FlexiBean out = super.createRootData();
    final RoleSearchRequest searchRequest = new RoleSearchRequest();
    out.put("searchRequest", searchRequest);
    return out;
  }

  // -------------------------------------------------------------------------
  /**
   * Builds a URI for roles.
   * 
   * @param data
   *          the data, not null
   * @return the URI, not null
   */
  public static URI uri(final WebRoleData data) {
    final UriBuilder builder = data.getUriInfo().getBaseUriBuilder().path(WebRolesResource.class);
    return builder.build();
  }

}
