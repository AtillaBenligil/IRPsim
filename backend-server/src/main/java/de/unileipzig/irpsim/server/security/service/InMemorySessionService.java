package de.unileipzig.irpsim.server.security.service;

import de.unileipzig.irpsim.server.security.auth.AuthPrincipal;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory session storage for initial security integration.
 */
public class InMemorySessionService implements SessionService {

   private final Map<String, AuthPrincipal> sessions = new ConcurrentHashMap<>();

   @Override
   public String createSession(final AuthPrincipal principal) {
      final String token = "irpsim-" + UUID.randomUUID();
      sessions.put(token, principal);
      return token;
   }

   @Override
   public Optional<AuthPrincipal> resolve(final String token) {
      if (token == null || token.trim().isEmpty()) {
         return Optional.empty();
      }
      return Optional.ofNullable(sessions.get(token));
   }

   @Override
   public void invalidate(final String token) {
      if (token != null) {
         sessions.remove(token);
      }
   }

   @Override
   public int size() {
      return sessions.size();
   }
}
