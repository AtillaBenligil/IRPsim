package de.unileipzig.irpsim.server.security.service;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public interface LdapAuthenticationService {

   AuthenticationResult authenticate(String username, String password);

   boolean changePassword(String username, String oldPassword, String newPassword);

   class AuthenticationResult {
      private final boolean authenticated;
      private final Set<String> groups;
      private final String message;

      private AuthenticationResult(final boolean authenticated, final Set<String> groups, final String message) {
         this.authenticated = authenticated;
         this.groups = groups == null ? Collections.emptySet() : Collections.unmodifiableSet(new LinkedHashSet<>(groups));
         this.message = message;
      }

      public static AuthenticationResult success(final Set<String> groups) {
         return new AuthenticationResult(true, groups, null);
      }

      public static AuthenticationResult failed(final String message) {
         return new AuthenticationResult(false, Collections.emptySet(), message);
      }

      public boolean isAuthenticated() {
         return authenticated;
      }

      public Set<String> getGroups() {
         return groups;
      }

      public String getMessage() {
         return message;
      }
   }
}
