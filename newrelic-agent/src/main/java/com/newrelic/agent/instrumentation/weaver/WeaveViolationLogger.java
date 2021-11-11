/*
 *
 *  * Copyright 2020 New Relic Corporation. All rights reserved.
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.newrelic.agent.instrumentation.weaver;

import com.newrelic.agent.Agent;
import com.newrelic.agent.bridge.jfr.events.supportability.instrumentation.WeaveViolationEvent;
import com.newrelic.agent.logging.IAgentLogger;
import com.newrelic.weave.violation.WeaveViolation;
import com.newrelic.weave.weavepackage.PackageValidationResult;
import org.objectweb.asm.commons.Method;

import java.util.List;
import java.util.logging.Level;

public class WeaveViolationLogger {
    private final IAgentLogger logger;

    public WeaveViolationLogger(IAgentLogger logger) {
        this.logger = logger;
    }

    public void logWeaveViolations(PackageValidationResult packageResult, ClassLoader classloader, boolean isCustom) {
        logger.log(Level.FINEST, "Skipping instrumentation module {0}. "
                + "The most likely cause is that {0} shouldn''t apply to this application.", packageResult.getWeavePackage().getName());

        if (!isCustom && !Agent.isDebugEnabled()) {
            return;
        }

        Level actualLevel = isCustom ? Level.INFO : Level.FINEST;

        List<WeaveViolation> violations = packageResult.getViolations();
        logger.log(actualLevel, "{0} - {1} violations against classloader {2}",
                packageResult.getWeavePackage().getName(), violations.size(), classloader);

//        WeaveViolationEvent weaveViolationEvent;

        for (WeaveViolation violation : violations) {
            logger.log(actualLevel, "WeaveViolation: {0}", violation.getType().name());
            logger.log(actualLevel, "\t\tClass: {0}", violation.getClazz());

            WeaveViolationEvent weaveViolationEvent = new WeaveViolationEvent();
            weaveViolationEvent.begin();
            weaveViolationEvent.custom = isCustom;
            if (packageResult.getWeavePackage() != null) {
                weaveViolationEvent.weavePackage = packageResult.getWeavePackage().getName();
            }
            weaveViolationEvent.weaveViolationSize = violations.size();
            if (classloader != null) {
                weaveViolationEvent.classloader = classloader.toString();
            }
            if (violation.getType() != null) {
                weaveViolationEvent.weaveViolationName = violation.getType().name();
                weaveViolationEvent.weaveViolationReason = violation.getType().getMessage();
            }
            weaveViolationEvent.weaveViolationClass = violation.getClazz();

            Method violationMethod = violation.getMethod();
            if (violationMethod != null) {
                logger.log(actualLevel, "\t\tMethod: {0}", violationMethod);
                weaveViolationEvent.weaveViolationMethod = violationMethod.getName();
            }
            String violationField = violation.getField();
            if (violationField != null) {
                logger.log(actualLevel, "\t\tField: {0}", violationField);
                weaveViolationEvent.weaveViolationField = violationField;
            }
            logger.log(actualLevel, "\t\tReason: {0}", violation.getType().getMessage());
            weaveViolationEvent.commit();
        }

    }
}
