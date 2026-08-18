package de.unileipzig.irpsim.server.security.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * LDAP configuration loaded from environment variables.
 */
public class LdapConfiguration {

   public static final String ENV_URL = "IRPSIM_LDAP_URL";
   public static final String ENV_BASE_DN = "IRPSIM_LDAP_BASE_DN";
   public static final String ENV_BIND_DN = "IRPSIM_LDAP_BIND_DN";
   public static final String ENV_BIND_PASSWORD = "IRPSIM_LDAP_BIND_PASSWORD";
   public static final String ENV_USER_DN_PATTERN = "IRPSIM_LDAP_USER_DN_PATTERN";
   public static final String SYSTEM_PROPERTY_CONFIG_FILE = "irpsim.ldap.config.file";

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
      final Properties fileProperties = loadPropertiesFromConfiguredFile();
      return new LdapConfiguration(
         firstNonBlank(env.get(ENV_URL), fileProperties.getProperty(ENV_URL)),
         firstNonBlank(env.get(ENV_BASE_DN), fileProperties.getProperty(ENV_BASE_DN)),
         firstNonBlank(env.get(ENV_BIND_DN), fileProperties.getProperty(ENV_BIND_DN)),
         firstNonBlank(env.get(ENV_BIND_PASSWORD), fileProperties.getProperty(ENV_BIND_PASSWORD)),
         firstNonBlank(env.get(ENV_USER_DN_PATTERN), fileProperties.getProperty(ENV_USER_DN_PATTERN)));
   }

   public boolean isConfigured() {
      return url != null && baseDn != null;
   }

   public String buildUserDn(final String username) {
      if (username == null || username.trim().isEmpty()) {
         throw new IllegalArgumentException("username must not be empty");
      }
      final String rdn = String.format(userDnPattern, username.trim());
      if (rdn.endsWith(baseDn)) {
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

   private static String firstNonBlank(final String preferred, final String fallback) {
      final String trimmedPreferred = trimToNull(preferred);
      if (trimmedPreferred != null) {
         return trimmedPreferred;
      }
      return trimToNull(fallback);
   }

   private static Properties loadPropertiesFromConfiguredFile() {
      final Properties properties = new Properties();
      final String configFile = trimToNull(System.getProperty(SYSTEM_PROPERTY_CONFIG_FILE));
      if (configFile == null) {
         return properties;
      }

      final File file = new File(configFile);
      if (!file.isFile()) {
         return properties;
      }

      try (FileInputStream inputStream = new FileInputStream(file)) {
         properties.load(inputStream);
      } catch (final IOException ignored) {
         // The LDAP config file is an optional build-time handoff.
      }
      return properties;
   }
}
