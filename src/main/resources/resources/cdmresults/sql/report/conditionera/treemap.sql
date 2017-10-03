SELECT
  concept_hierarchy.concept_id                        AS conceptId,
  isNull(concept_hierarchy.level4_concept_name, 'NA') + '||' + isNull(concept_hierarchy.level3_concept_name, 'NA') + '||' +
  isNull(concept_hierarchy.level2_concept_name, 'NA') + '||' + isNull(concept_hierarchy.level1_concept_name, 'NA') + '||' +
  isNull(concept_hierarchy.concept_name, 'NA') AS conceptPath,
  ar1.count_value                                     AS numPersons,
  ROUND(1.0 * ar1.count_value / denom.count_value, 5) AS percentPersons,
  ROUND(ar2.avg_value, 5)                             AS lengthOfEra
FROM (SELECT *
      FROM @results_database_schema.ACHILLES_results WHERE analysis_id = 1000) ar1
  INNER JOIN
  (SELECT
     stratum_1,
     avg_value
   FROM @results_database_schema.ACHILLES_results_dist WHERE analysis_id = 1007) ar2
    ON ar1.stratum_1 = ar2.stratum_1
  INNER JOIN
  @results_database_schema.concept_hierarchy AS concept_hierarchy
    ON CAST(ar1.stratum_1 AS INT) = concept_hierarchy.concept_id
    AND concept_hierarchy.treemap='Condition'
  ,
  (SELECT count_value
   FROM @results_database_schema.ACHILLES_results WHERE analysis_id = 1) denom
ORDER BY ar1.count_value DESC