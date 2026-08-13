package de.unileipzig.irpsim.server.security.service;

import de.unileipzig.irpsim.server.security.auth.AuthPrincipal;

import java.util.Optional;

public interface SessionService {

   String createSession(AuthPrincipal principal);

   Optional<AuthPrincipal> resolve(String token);

   void invalidate(String token);

   int size();
}
