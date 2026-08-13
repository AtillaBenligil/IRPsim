package de.unileipzig.irpsim.server.security.filter;

import de.unileipzig.irpsim.server.security.auth.AuthPrincipal;
import de.unileipzig.irpsim.server.security.authorization.RequiresAuthentication;
import de.unileipzig.irpsim.server.security.authorization.RequiresGroup;
import org.json.JSONObject;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.core.Context;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * Enforces endpoint-level authentication and group authorization annotations.
 */
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationRequestFilter implements ContainerRequestFilter {

   @Context
   ResourceInfo resourceInfo;

   public AuthorizationRequestFilter(final ResourceInfo resourceInfo) {
      this.resourceInfo = resourceInfo;
   }

   public AuthorizationRequestFilter() {
   }

   @Override
   public void filter(final ContainerRequestContext requestContext) {
      if (!requiresAuthorization()) {
         return;
      }

      final Object property = requestContext.getProperty(AuthRequestFilter.AUTH_PRINCIPAL_PROPERTY);
      if (!(property instanceof AuthPrincipal)) {
         requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
               .entity(new JSONObject().put("message", "authentication required").toString())
               .build());
         return;
      }

      final AuthPrincipal principal = (AuthPrincipal) property;
      final RequiresGroup requiredGroup = getAnnotation(RequiresGroup.class);
      if (requiredGroup != null && !principal.getGroups().contains(requiredGroup.value())) {
         requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
               .entity(new JSONObject().put("message", "insufficient privileges").toString())
               .build());
      }
   }

   private boolean requiresAuthorization() {
      return getAnnotation(RequiresAuthentication.class) != null || getAnnotation(RequiresGroup.class) != null;
   }

   private <T extends Annotation> T getAnnotation(final Class<T> annotationClass) {
      if (resourceInfo == null) {
         return null;
      }
      final T methodAnnotation = getAnnotation(resourceInfo.getResourceMethod(), annotationClass);
      if (methodAnnotation != null) {
         return methodAnnotation;
      }
      return getAnnotation(resourceInfo.getResourceClass(), annotationClass);
   }

   private static <T extends Annotation> T getAnnotation(final AnnotatedElement element, final Class<T> annotationClass) {
      if (element == null) {
         return null;
      }
      return element.getAnnotation(annotationClass);
   }
}
