package de.unileipzig.irpsim.server.security.filter;

import de.unileipzig.irpsim.server.security.auth.AuthPrincipal;
import de.unileipzig.irpsim.server.security.service.SecurityServices;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.security.Principal;
import java.util.Optional;

/**
 * Resolves authentication token from request headers and exposes principal to downstream handlers.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthRequestFilter implements ContainerRequestFilter {

   public static final String AUTH_PRINCIPAL_PROPERTY = "irpsim.auth.principal";

   @Override
   public void filter(final ContainerRequestContext requestContext) {
      final String token = extractToken(requestContext);
      if (token == null) {
         return;
      }

      final Optional<AuthPrincipal> principal = SecurityServices.getSessionService().resolve(token);
      if (!principal.isPresent()) {
         return;
      }

      requestContext.setProperty(AUTH_PRINCIPAL_PROPERTY, principal.get());
      final SecurityContext currentContext = requestContext.getSecurityContext();
      requestContext.setSecurityContext(new SecurityContext() {
         @Override
         public Principal getUserPrincipal() {
            return () -> principal.get().getUsername();
         }

         @Override
         public boolean isUserInRole(final String role) {
            return principal.get().getGroups().contains(role);
         }

         @Override
         public boolean isSecure() {
            return currentContext != null && currentContext.isSecure();
         }

         @Override
         public String getAuthenticationScheme() {
            return "Bearer";
         }
      });
   }

   static String extractToken(final ContainerRequestContext requestContext) {
      final String authorization = requestContext.getHeaderString("Authorization");
      if (authorization != null && authorization.startsWith("Bearer ")) {
         return authorization.substring("Bearer ".length()).trim();
      }
      final String fallbackToken = requestContext.getHeaderString("X-Auth-Token");
      if (fallbackToken != null && !fallbackToken.trim().isEmpty()) {
         return fallbackToken.trim();
      }
      return null;
   }
}
