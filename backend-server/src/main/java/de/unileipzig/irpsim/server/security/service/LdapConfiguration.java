package de.unileipzig.irpsim.server.security.service;

import java.util.Map;

/**
 * LDAP configuration loaded from environment variables.
 */
public class LdapConfiguration {

   public static final String ENV_URL = "IRPSIM_LDAP_URL";
   public static final String ENV_BASE_DN = "IRPSIM_LDAP_BASE_DN";
   public static final String ENV_BIND_DN = "IRPSIM_LDAP_BIND_DN";
   public static final String ENV_BIND_PASSWORD = "IRPSIM_LDAP_BIND_PASSWORD";
   public static final String ENV_USER_DN_PATTERN = "IRPSIM_LDAP_USER_DN_PATTERN";

   private final String url;
   private final String baseDn;
   private final String bindDn;
   private final String bindPassword;
   private final String userDnPattern;

   public LdapConfiguration(
         final String url,
         final String baseDn,
         final String bindDn,
         final String bindPassword,
         final String userDnPattern) {
      this.url = trimToNull(url);
      this.baseDn = trimToNull(baseDn);
      this.bindDn = trimToNull(bindDn);
      this.bindPassword = trimToNull(bindPassword);
      this.userDnPattern = trimToNull(userDnPattern) == null ? "uid=%s" : userDnPattern;
   }

   public static LdapConfiguration fromEnvironment(final Map<String, String> env) {
      return new LdapConfiguration(
            env.get(ENV_URL),
            env.get(ENV_BASE_DN),
            env.get(ENV_BIND_DN),
            env.get(ENV_BIND_PASSWORD),
            env.get(ENV_USER_DN_PATTERN));
   }

   public boolean isConfigured() {
      return url != null && baseDn != null;
   }

   public String buildUserDn(final String username) {
      if (username == null || username.trim().isEmpty()) {
         throw new IllegalArgumentException("username must not be empty");
      }
      final String rdn = String.format(userDnPattern, username.trim());
      if (rdn.contains(",")) {
         return rdn;
      }
      return rdn + "," + baseDn;
   }

   public String getUrl() {
      return url;
   }

   public String getBaseDn() {
      return baseDn;
   }

   public String getBindDn() {
      return bindDn;
   }

   public String getBindPassword() {
      return bindPassword;
   }

   private static String trimToNull(final String value) {
      if (value == null) {
         return null;
      }
      final String trimmed = value.trim();
      return trimmed.isEmpty() ? null : trimmed;
   }
}
