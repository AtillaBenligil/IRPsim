package de.unileipzig.irpsim.server.security;

import de.unileipzig.irpsim.server.security.service.LdapConfiguration;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LdapConfigurationTest {

   @Test
   public void shouldReadConfigurationFromPropertiesFileWhenEnvironmentIsEmpty() throws Exception {
      final File tempDirectory = Files.createTempDirectory("ldap-config-test").toFile();
      final File configFile = new File(tempDirectory, "ldap.properties");

      final Properties properties = new Properties();
      properties.setProperty(LdapConfiguration.ENV_URL, "ldap://localhost:1389");
      properties.setProperty(LdapConfiguration.ENV_BASE_DN, "dc=irpsim,dc=local");
      properties.setProperty(LdapConfiguration.ENV_BIND_DN, "cn=admin,dc=irpsim,dc=local");
      properties.setProperty(LdapConfiguration.ENV_BIND_PASSWORD, "admin");
      properties.setProperty(LdapConfiguration.ENV_USER_DN_PATTERN, "uid=%s,ou=people");
      try (FileWriter writer = new FileWriter(configFile)) {
         properties.store(writer, "test");
      }

      final String originalConfigFile = System.getProperty(LdapConfiguration.SYSTEM_PROPERTY_CONFIG_FILE);
      try {
         System.setProperty(LdapConfiguration.SYSTEM_PROPERTY_CONFIG_FILE, configFile.getAbsolutePath());

         final Map<String, String> env = new HashMap<>();
         final LdapConfiguration configuration = LdapConfiguration.fromEnvironment(env);

         Assert.assertTrue(configuration.isConfigured());
         Assert.assertEquals("ldap://localhost:1389", configuration.getUrl());
         Assert.assertEquals("dc=irpsim,dc=local", configuration.getBaseDn());
         Assert.assertEquals("cn=admin,dc=irpsim,dc=local", configuration.getBindDn());
         Assert.assertEquals("admin", configuration.getBindPassword());
         Assert.assertEquals("uid=alice,ou=people,dc=irpsim,dc=local", configuration.buildUserDn("alice"));
      } finally {
         if (originalConfigFile == null) {
            System.clearProperty(LdapConfiguration.SYSTEM_PROPERTY_CONFIG_FILE);
         } else {
            System.setProperty(LdapConfiguration.SYSTEM_PROPERTY_CONFIG_FILE, originalConfigFile);
         }
      }
   }
}
