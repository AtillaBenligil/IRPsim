package de.unileipzig.irpsim.server.security.service;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import java.util.Collections;
import java.util.Hashtable;

/**
 * LDAP authentication against a configured LDAP server using JNDI bind.
 */
public class JndiLdapAuthenticationService implements LdapAuthenticationService {

   private final LdapConfiguration config;

   public JndiLdapAuthenticationService(final LdapConfiguration config) {
      this.config = config;
   }

   @Override
   public AuthenticationResult authenticate(final String username, final String password) {
      if (!config.isConfigured()) {
         return AuthenticationResult.failed("LDAP is not configured");
      }
      if (isBlank(username) || isBlank(password)) {
         return AuthenticationResult.failed("username or password missing");
      }

      final String userDn = config.buildUserDn(username);
      try (DirContext ignored = new InitialDirContext(buildEnvironment(userDn, password))) {
         return AuthenticationResult.success(Collections.emptySet());
      } catch (final AuthenticationException e) {
         return AuthenticationResult.failed("invalid credentials");
      } catch (final NamingException e) {
         return AuthenticationResult.failed("ldap error: " + e.getMessage());
      }
   }

   @Override
   public boolean changePassword(final String username, final String oldPassword, final String newPassword) {
      if (!config.isConfigured() || isBlank(username) || isBlank(oldPassword) || isBlank(newPassword)) {
         return false;
      }

      final String userDn = config.buildUserDn(username);
      try (DirContext context = new InitialDirContext(buildEnvironment(userDn, oldPassword))) {
         final ModificationItem[] modifications = new ModificationItem[] {
               new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPassword", newPassword))
         };
         context.modifyAttributes(userDn, modifications);
         return true;
      } catch (final NamingException e) {
         return false;
      }
   }

   private Hashtable<String, String> buildEnvironment(final String principal, final String credentials) {
      final Hashtable<String, String> env = new Hashtable<>();
      env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
      env.put(Context.PROVIDER_URL, config.getUrl());
      env.put(Context.SECURITY_AUTHENTICATION, "simple");
      env.put(Context.SECURITY_PRINCIPAL, principal);
      env.put(Context.SECURITY_CREDENTIALS, credentials);
      return env;
   }

   private static boolean isBlank(final String value) {
      return value == null || value.trim().isEmpty();
   }
}
