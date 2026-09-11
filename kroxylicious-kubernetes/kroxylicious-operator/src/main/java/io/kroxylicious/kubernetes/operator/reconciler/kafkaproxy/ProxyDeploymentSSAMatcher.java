/*
 * Copyright Kroxylicious Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.kroxylicious.kubernetes.operator.reconciler.kafkaproxy;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.SSABasedGenericKubernetesResourceMatcher;

/**
 * Extends the standard SSA matcher for the proxy Deployment's removable ServiceAccount setting.
 *
 * <p>The standard matcher intentionally ignores fields not owned by the operator. When a configured
 * ServiceAccount is removed from a KafkaProxy, the Deployment field must nevertheless be removed if
 * it is present. The KafkaProxy value is authoritative for the generated Deployment's workload
 * identity, so an explicit value is not preserved when the field is omitted. The dependent resource
 * performs that removal with a JSON merge patch.</p>
 */
public final class ProxyDeploymentSSAMatcher extends SSABasedGenericKubernetesResourceMatcher<Deployment> {

    /** Creates the matcher. */
    public ProxyDeploymentSSAMatcher() {
        super();
    }

    @Override
    public boolean matches(Deployment actual, Deployment desired, Context<?> context) {
        if (ProxyDeploymentDependentResource.hasExplicitServiceAccount(actual)
                && !ProxyDeploymentDependentResource.hasExplicitServiceAccount(desired)) {
            return false;
        }
        return super.matches(actual, desired, context);
    }
}
