package de.unileipzig.irpsim.server.security;

import de.unileipzig.irpsim.server.security.auth.AuthPrincipal;
import de.unileipzig.irpsim.server.security.authorization.RequiresAuthentication;
import de.unileipzig.irpsim.server.security.authorization.RequiresGroup;
import de.unileipzig.irpsim.server.security.filter.AuthRequestFilter;
import de.unileipzig.irpsim.server.security.filter.AuthorizationRequestFilter;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Response;
import java.lang.reflect.Method;
import java.util.Collections;

public class AuthorizationRequestFilterTest {

   @Test
   public void shouldRejectWhenAuthenticationIsRequiredButPrincipalIsMissing() throws Exception {
      final ResourceInfo resourceInfo = mockResourceInfo(TestResource.class, "authOnly");
      final AuthorizationRequestFilter filter = new AuthorizationRequestFilter(resourceInfo);
      final ContainerRequestContext requestContext = Mockito.mock(ContainerRequestContext.class);

      filter.filter(requestContext);

      final Response response = captureAbortResponse(requestContext);
      Assert.assertEquals(401, response.getStatus());
   }

   @Test
   public void shouldRejectWhenRequiredGroupIsMissing() throws Exception {
      final ResourceInfo resourceInfo = mockResourceInfo(TestResource.class, "adminOnly");
      final AuthorizationRequestFilter filter = new AuthorizationRequestFilter(resourceInfo);
      final ContainerRequestContext requestContext = Mockito.mock(ContainerRequestContext.class);
      final AuthPrincipal principal = new AuthPrincipal("alice", Collections.singleton("analyst"), System.currentTimeMillis());
      Mockito.when(requestContext.getProperty(AuthRequestFilter.AUTH_PRINCIPAL_PROPERTY)).thenReturn(principal);

      filter.filter(requestContext);

      final Response response = captureAbortResponse(requestContext);
      Assert.assertEquals(403, response.getStatus());
   }

   @Test
   public void shouldPassWhenRequiredGroupIsPresent() throws Exception {
      final ResourceInfo resourceInfo = mockResourceInfo(TestResource.class, "adminOnly");
      final AuthorizationRequestFilter filter = new AuthorizationRequestFilter(resourceInfo);
      final ContainerRequestContext requestContext = Mockito.mock(ContainerRequestContext.class);
      final AuthPrincipal principal = new AuthPrincipal("alice", Collections.singleton("admin"), System.currentTimeMillis());
      Mockito.when(requestContext.getProperty(AuthRequestFilter.AUTH_PRINCIPAL_PROPERTY)).thenReturn(principal);

      filter.filter(requestContext);

      Mockito.verify(requestContext, Mockito.never()).abortWith(Mockito.any(Response.class));
   }

   private static ResourceInfo mockResourceInfo(final Class<?> clazz, final String methodName) throws Exception {
      final Method method = clazz.getDeclaredMethod(methodName);
      final ResourceInfo resourceInfo = Mockito.mock(ResourceInfo.class);
      Mockito.when(resourceInfo.getResourceClass()).thenReturn(clazz);
      Mockito.when(resourceInfo.getResourceMethod()).thenReturn(method);
      return resourceInfo;
   }

   private static Response captureAbortResponse(final ContainerRequestContext requestContext) {
      return Mockito.mockingDetails(requestContext)
            .getInvocations()
            .stream()
            .filter(invocation -> "abortWith".equals(invocation.getMethod().getName()))
            .map(invocation -> (Response) invocation.getArguments()[0])
            .findFirst()
            .orElseThrow(() -> new AssertionError("Expected abortWith to be invoked"));
   }

   @RequiresAuthentication
   private static class TestResource {

      public void authOnly() {
      }

      @RequiresGroup("admin")
      public void adminOnly() {
      }
   }
}
