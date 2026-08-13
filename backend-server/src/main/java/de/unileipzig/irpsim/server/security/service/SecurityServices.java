package de.unileipzig.irpsim.server.security.service;

/**
 * Simple service registry for security bootstrap and test injection.
 */
public final class SecurityServices {

   private static volatile SessionService sessionService = new InMemorySessionService();
   private static volatile LdapAuthenticationService ldapService =
         new JndiLdapAuthenticationService(LdapConfiguration.fromEnvironment(System.getenv()));

   private SecurityServices() {
   }

   public static SessionService getSessionService() {
      return sessionService;
   }

   public static LdapAuthenticationService getLdapService() {
      return ldapService;
   }

   public static void setSessionService(final SessionService newSessionService) {
      if (newSessionService == null) {
         throw new IllegalArgumentException("session service must not be null");
      }
      sessionService = newSessionService;
   }

   public static void setLdapService(final LdapAuthenticationService newLdapService) {
      if (newLdapService == null) {
         throw new IllegalArgumentException("ldap service must not be null");
      }
      ldapService = newLdapService;
   }
}
