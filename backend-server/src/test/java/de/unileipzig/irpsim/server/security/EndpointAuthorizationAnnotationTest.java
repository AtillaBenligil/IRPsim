package de.unileipzig.irpsim.server.security;

import de.unileipzig.irpsim.server.data.simulationparameters.ScenarioEndpoint;
import de.unileipzig.irpsim.server.optimisation.endpoints.OptimisationJobEndpoint;
import de.unileipzig.irpsim.server.security.authorization.RequiresAuthentication;
import de.unileipzig.irpsim.server.security.authorization.RequiresGroup;
import de.unileipzig.irpsim.server.standingdata.endpoints.StammdatumEndpoint;
import de.unileipzig.irpsim.core.standingdata.data.Stammdatum;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;

public class EndpointAuthorizationAnnotationTest {

   @Test
   public void shouldRequireAuthenticationOnScenarioEndpoint() {
      Assert.assertTrue(ScenarioEndpoint.class.isAnnotationPresent(RequiresAuthentication.class));
   }

   @Test
   public void shouldRestrictScenarioMutationsToAdmins() throws Exception {
      Assert.assertTrue(hasGroupRestriction(ScenarioEndpoint.class, "deleteParameter", int.class));
      Assert.assertTrue(hasGroupRestriction(ScenarioEndpoint.class, "createNewSimulationParameters", String.class));
      Assert.assertTrue(hasGroupRestriction(ScenarioEndpoint.class, "createNewSimulationParameters", int.class, String.class));
   }

   @Test
   public void shouldRequireAuthenticationOnOptimisationJobs() {
      Assert.assertTrue(OptimisationJobEndpoint.class.isAnnotationPresent(RequiresAuthentication.class));
      Assert.assertTrue(hasGroupRestriction(OptimisationJobEndpoint.class, "killSimulation", long.class, boolean.class));
   }

   @Test
   public void shouldRestrictStandingDataWritesToAdmins() {
      Assert.assertTrue(StammdatumEndpoint.class.isAnnotationPresent(RequiresAuthentication.class));
      Assert.assertTrue(hasGroupRestriction(StammdatumEndpoint.class, "putStammdatum", Stammdatum.class));
   }

   private static boolean hasGroupRestriction(final Class<?> endpointClass, final String methodName, final Class<?>... parameterTypes) {
      try {
         final Method method = endpointClass.getDeclaredMethod(methodName, parameterTypes);
         final RequiresGroup requiresGroup = method.getAnnotation(RequiresGroup.class);
         return requiresGroup != null && "admin".equals(requiresGroup.value());
      } catch (final NoSuchMethodException e) {
         throw new AssertionError("Missing method " + endpointClass.getSimpleName() + "." + methodName, e);
      }
   }
}