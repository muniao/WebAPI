/*
 *   Copyright 2017 Observational Health Data Sciences and Informatics
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   Authors: Sergey Suvorov
 *
 */

package org.ohdsi.webapi.check.validator.estimation;

import org.ohdsi.analysis.ConceptSetCrossReference;
import org.ohdsi.analysis.estimation.design.EstimationAnalysis;
import org.ohdsi.webapi.check.validator.Rule;
import org.ohdsi.webapi.check.validator.RuleValidator;
import org.ohdsi.webapi.check.validator.ValueAccessor;
import org.ohdsi.webapi.check.validator.common.PredicateValidator;

import java.util.Collection;

import static org.ohdsi.webapi.estimation.EstimationServiceImpl.CONCEPT_SET_XREF_KEY_NEGATIVE_CONTROL_OUTCOMES;

public class EstimationSpecificationValidator<T extends EstimationAnalysis> extends RuleValidator<T> {
    @Override
    protected void buildInternal() {
        // Analysis settings
        prepareAnalysisSettingsRule();

        // Positive control synthesis
        preparePositiveSynthesisRule();

        // Negative control outcome
        prepareNegativeControlOutcomeRule();

        // Negative control
        prepareNegativeControlRule();
    }

    private void prepareNegativeControlRule() {
        PredicateValidator<Collection<? extends ConceptSetCrossReference>> validator = new PredicateValidator<>();
        validator.setPredicate(v -> {
            if (v != null) {
                return v.stream()
                        .anyMatch(r -> CONCEPT_SET_XREF_KEY_NEGATIVE_CONTROL_OUTCOMES.equalsIgnoreCase(r.getTargetName()));
            }
            return false;
        });
        Rule<T> rule =
                createRule(createPath("negative control"), reporter)
                        .setErrorTemplate("must be present")
                        .setValueAccessor(EstimationAnalysis::getConceptSetCrossReference)
                        .addValidator(validator);
        rules.add(rule);
    }

    private void prepareNegativeControlOutcomeRule() {
        Rule<T> rule =
                createRule(createPath("negative control outcome"), reporter)
                        .setValueAccessor(EstimationAnalysis::getNegativeControlOutcomeCohortDefinition)
                        .addValidator(new NegativeControlOutcomeCohortExpressionValidator());
        rules.add(rule);
    }

    private void preparePositiveSynthesisRule() {
        ValueAccessor<T> positiveControlValueAccessor =
                value -> Boolean.TRUE.equals(value.getDoPositiveControlSynthesis()) ?
                        value.getPositiveControlSynthesisArgs() :
                        null;

        Rule<T> rule =
                createRule(createPath("positive control synthesis"), reporter)
                        .setValueAccessor(positiveControlValueAccessor)
                        .addValidator(new PositiveControlSynthesisArgsValidator());
        rules.add(rule);
    }

    private void prepareAnalysisSettingsRule() {
        Rule<T> rule =
                createRuleWithDefaultValidator(createPath(), reporter)
                        .setValueAccessor(EstimationAnalysis::getEstimationAnalysisSettings)
                        .addValidator(new EstimationSettingsValidator());
        rules.add(rule);
    }
}
