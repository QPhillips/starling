/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.security.user;

/**
 * A permission granted to users.
 * <p>
 * Permissions can include viewing a portfolio or a class of portfolios, modifying a portfolio,
 * viewing market data, etc.
 * <p>
 * Permissions can be regular expressions, for example <code>/MarketData/Bloomberg/&#42;/View</code>, which would
 * grant its holders the right to view any Bloomberg market data. See {@link PathMatcher}.
 * <p>
 * Note that <code>Authorities</code> are technically granted to {@link UserGroup}. Each
 * {@link User} belongs to a number of <code>UserGroups</code>. This reduces the need to modify individual
 * permissions when users move within a company.
 */
public class Authority {

  /**
   * The database id.
   */
  private Long _id;

  /**
   * A regular expression in Ant format
   **/
  private String _regex;

  /**
   * Creates an instance of an authority.
   * @param id  the database id
   * @param regex  the regex
   */
  public Authority(final Long id, final String regex) {
    _id = id;
    _regex = regex;
  }

  /**
   * Creates an instance of an authority.
   * @param regex  the regex
   */
  public Authority(final String regex) {
    this(null, regex);
  }

  /**
   * Restricted constructor for tools.
   */
  protected Authority() {
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the database id.
   *
   * @return  the id
   */
  public Long getId() {
    return _id;
  }

  /**
   * Sets the database id.
   *
   * @param id  the id
   */
  public void setId(final Long id) {
    _id = id;
  }

  /**
   * Gets the matcher regex.
   *
   * @return  the regex
   */
  public String getRegex() {
    return _regex;
  }

  /**
   * Sets the matcher regex.
   *
   * @param regex  the regex
   */
  public void setRegex(final String regex) {
    this._regex = regex;
  }

  //-------------------------------------------------------------------------
  /**
   * Returns whether this <code>Authority</code> can be used to grant the requested permission.
   *
   * @param requestedPermission The requested permission, for example /MarketData/Bloomberg/AAPL/View
   * @return Whether the regular expression pattern stored in this <code>Authority</code>,
   * for example <code>/MarketData/Bloomberg/&#42;/View</code>, matches the requested permission
   */
  public boolean matches(final String requestedPermission) {
    return PathMatcher.matches(requestedPermission, _regex);
  }

  //-------------------------------------------------------------------------
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Authority other = (Authority) obj;
    if (_id == null) {
      if (other._id != null) {
        return false;
      }
    } else if (!_id.equals(other._id)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (_id == null ? 0 : _id.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return _regex;
  }

}
