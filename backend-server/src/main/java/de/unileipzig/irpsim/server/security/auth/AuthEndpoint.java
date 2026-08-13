package de.unileipzig.irpsim.server.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unileipzig.irpsim.server.security.service.LdapAuthenticationService;
import de.unileipzig.irpsim.server.security.service.SecurityServices;
import de.unileipzig.irpsim.server.security.service.SessionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Authentication entry points for LDAP-backed login/logout/password change.
 */
@Path("auth")
@Api(value = "/auth", tags = "Authentication")
@Produces(MediaType.APPLICATION_JSON)
public class AuthEndpoint {

   private static final ObjectMapper MAPPER = new ObjectMapper();

   private final SessionService sessionService;
   private final LdapAuthenticationService ldapService;

   public AuthEndpoint() {
      this(SecurityServices.getSessionService(), SecurityServices.getLdapService());
   }

   public AuthEndpoint(final SessionService sessionService, final LdapAuthenticationService ldapService) {
      this.sessionService = sessionService;
      this.ldapService = ldapService;
   }

   @POST
   @Path("login")
   @Consumes(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Login with LDAP credentials")
   @ApiResponses(value = {
         @ApiResponse(code = 200, message = "Logged in"),
         @ApiResponse(code = 400, message = "Invalid request"),
         @ApiResponse(code = 401, message = "Authentication failed")
   })
   public Response login(final String requestBody) {
      try {
         final LoginRequest request = MAPPER.readValue(requestBody, LoginRequest.class);
         if (isBlank(request.username) || isBlank(request.password)) {
            return Response.status(Response.Status.BAD_REQUEST)
                  .entity(new JSONObject().put("message", "username or password missing").toString())
                  .build();
         }

         final LdapAuthenticationService.AuthenticationResult authResult = ldapService.authenticate(request.username, request.password);
         if (!authResult.isAuthenticated()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                  .entity(new JSONObject().put("message", authResult.getMessage()).toString())
                  .build();
         }

         final AuthPrincipal principal = new AuthPrincipal(request.username, authResult.getGroups(), System.currentTimeMillis());
         final String token = sessionService.createSession(principal);

         return Response.ok(
               new JSONObject()
                     .put("token", token)
                     .put("username", principal.getUsername())
                     .put("groups", new JSONArray(principal.getGroups()))
                     .toString())
               .build();
      } catch (final Exception e) {
         return Response.status(Response.Status.BAD_REQUEST)
               .entity(new JSONObject().put("message", "request could not be parsed").toString())
               .build();
      }
   }

   @POST
   @Path("logout")
   @ApiOperation(value = "Logout and invalidate the session token")
   public Response logout(@HeaderParam("Authorization") final String authorizationHeader,
         @HeaderParam("X-Auth-Token") final String fallbackToken) {
      final String token = extractToken(authorizationHeader, fallbackToken);
      if (token == null) {
         return Response.status(Response.Status.BAD_REQUEST)
               .entity(new JSONObject().put("message", "no token supplied").toString())
               .build();
      }
      sessionService.invalidate(token);
      return Response.ok(new JSONObject().put("message", "logged out").toString()).build();
   }

   @POST
   @Path("change-password")
   @Consumes(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Change LDAP password")
   public Response changePassword(final String requestBody) {
      try {
         final ChangePasswordRequest request = MAPPER.readValue(requestBody, ChangePasswordRequest.class);
         if (isBlank(request.username) || isBlank(request.oldPassword) || isBlank(request.newPassword)) {
            return Response.status(Response.Status.BAD_REQUEST)
                  .entity(new JSONObject().put("message", "username, oldPassword and newPassword are required").toString())
                  .build();
         }
         final boolean changed = ldapService.changePassword(request.username, request.oldPassword, request.newPassword);
         if (!changed) {
            return Response.status(Response.Status.UNAUTHORIZED)
                  .entity(new JSONObject().put("message", "password change failed").toString())
                  .build();
         }
         return Response.ok(new JSONObject().put("message", "password changed").toString()).build();
      } catch (final Exception e) {
         return Response.status(Response.Status.BAD_REQUEST)
               .entity(new JSONObject().put("message", "request could not be parsed").toString())
               .build();
      }
   }

   private static String extractToken(final String authorizationHeader, final String fallbackToken) {
      if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
         return authorizationHeader.substring("Bearer ".length()).trim();
      }
      if (!isBlank(fallbackToken)) {
         return fallbackToken.trim();
      }
      return null;
   }

   private static boolean isBlank(final String value) {
      return value == null || value.trim().isEmpty();
   }

   static class LoginRequest {
      public String username;
      public String password;
   }

   static class ChangePasswordRequest {
      public String username;
      public String oldPassword;
      public String newPassword;
   }
}
