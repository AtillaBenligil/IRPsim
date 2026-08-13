package de.unileipzig.irpsim.server.security;

import de.unileipzig.irpsim.server.security.auth.AuthEndpoint;
import de.unileipzig.irpsim.server.security.service.InMemorySessionService;
import de.unileipzig.irpsim.server.security.service.LdapAuthenticationService;
import de.unileipzig.irpsim.server.security.service.SessionService;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.Collections;

public class AuthEndpointTest {

   @Test
   public void shouldLoginAndCreateSessionToken() {
      final SessionService sessionService = new InMemorySessionService();
      final AuthEndpoint endpoint = new AuthEndpoint(sessionService, new FakeLdapService());

      final Response response = endpoint.login("{\"username\":\"alice\",\"password\":\"secret\"}");
      Assert.assertEquals(200, response.getStatus());

      final JSONObject body = new JSONObject((String) response.getEntity());
      Assert.assertTrue(body.has("token"));
      Assert.assertEquals("alice", body.getString("username"));
      Assert.assertEquals(1, sessionService.size());
   }

   @Test
   public void shouldRejectInvalidLogin() {
      final AuthEndpoint endpoint = new AuthEndpoint(new InMemorySessionService(), new FakeLdapService());
      final Response response = endpoint.login("{\"username\":\"alice\",\"password\":\"wrong\"}");
      Assert.assertEquals(401, response.getStatus());
   }

   @Test
   public void shouldLogoutAndInvalidateToken() {
      final SessionService sessionService = new InMemorySessionService();
      final AuthEndpoint endpoint = new AuthEndpoint(sessionService, new FakeLdapService());

      final Response login = endpoint.login("{\"username\":\"alice\",\"password\":\"secret\"}");
      final JSONObject loginBody = new JSONObject((String) login.getEntity());
      final String token = loginBody.getString("token");

      final Response logout = endpoint.logout("Bearer " + token, null);
      Assert.assertEquals(200, logout.getStatus());
      Assert.assertEquals(0, sessionService.size());
   }

   @Test
   public void shouldChangePasswordWithValidCredentials() {
      final AuthEndpoint endpoint = new AuthEndpoint(new InMemorySessionService(), new FakeLdapService());
      final Response response = endpoint.changePassword("{\"username\":\"alice\",\"oldPassword\":\"secret\",\"newPassword\":\"newSecret\"}");
      Assert.assertEquals(200, response.getStatus());
   }

   private static class FakeLdapService implements LdapAuthenticationService {

      @Override
      public AuthenticationResult authenticate(final String username, final String password) {
         if ("alice".equals(username) && "secret".equals(password)) {
            return AuthenticationResult.success(Collections.singleton("analyst"));
         }
         return AuthenticationResult.failed("invalid credentials");
      }

      @Override
      public boolean changePassword(final String username, final String oldPassword, final String newPassword) {
         return "alice".equals(username) && "secret".equals(oldPassword) && newPassword != null && !newPassword.trim().isEmpty();
      }
   }
}
