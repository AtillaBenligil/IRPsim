package de.unileipzig.irpsim.server.security;

import de.unileipzig.irpsim.server.security.auth.AuthPrincipal;
import de.unileipzig.irpsim.server.security.service.InMemorySessionService;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class InMemorySessionServiceTest {

   @Test
   public void shouldCreateResolveAndInvalidateSession() {
      final InMemorySessionService service = new InMemorySessionService();
      final AuthPrincipal principal = new AuthPrincipal("alice", Collections.singleton("analyst"), System.currentTimeMillis());

      final String token = service.createSession(principal);
      Assert.assertNotNull(token);
      Assert.assertTrue(service.resolve(token).isPresent());
      Assert.assertEquals("alice", service.resolve(token).get().getUsername());

      service.invalidate(token);
      Assert.assertFalse(service.resolve(token).isPresent());
      Assert.assertEquals(0, service.size());
   }
}
