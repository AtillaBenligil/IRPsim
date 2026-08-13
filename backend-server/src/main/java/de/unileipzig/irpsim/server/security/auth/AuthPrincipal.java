package de.unileipzig.irpsim.server.security.auth;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Authenticated user identity resolved from session token.
 */
public class AuthPrincipal {

   private final String username;
   private final Set<String> groups;
   private final long issuedAtEpochMillis;

   public AuthPrincipal(final String username, final Set<String> groups, final long issuedAtEpochMillis) {
      this.username = username;
      this.groups = groups == null ? Collections.emptySet() : Collections.unmodifiableSet(new LinkedHashSet<>(groups));
      this.issuedAtEpochMillis = issuedAtEpochMillis;
   }

   public String getUsername() {
      return username;
   }

   public Set<String> getGroups() {
      return groups;
   }

   public long getIssuedAtEpochMillis() {
      return issuedAtEpochMillis;
   }
}
