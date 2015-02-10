package org.ohdsi.webapi.cohortresults;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ohdsi.webapi.service.CohortAnalysisService;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;

public class CohortAnalysisTasklet implements Tasklet {
	
	@Autowired
	private CohortAnalysisService analysisService;
    
    private static final Log log = LogFactory.getLog(CohortAnalysisTasklet.class);
    
    private final CohortAnalysisTask task;
    
    private final JdbcTemplate jdbcTemplate;
    
    public CohortAnalysisTasklet(final CohortAnalysisTask task, final JdbcTemplate jdbcTemplate) {
        this.task = task;
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public RepeatStatus execute(final StepContribution contribution, final ChunkContext chunkContext) throws Exception {
        String sql = this.analysisService.getRunCohortAnalysisSql(this.task);
    	log.debug("SQL: " + sql);
        sql = "\n--HERACLES\n\n--Patrick Ryan\n\n--last updated: 21 Jan 2015\n\n\n--chagnes for v4 to v5 that impact HERACLES\n\n--death :  cause_of_death_concept_id -> cause_concept_id\n--visit:  place_of_service_concept_id -> visit_concept_id\n--f/r:  associated_provider_id -> provider_id\n--\u0009prescribing_provider_id -> provider_id\n\n--remove:  disease_class_concept_id analyses\n\u0009\n--observation:  no more range_high / range_low...now from measurement\n--\u0009-options:  remove observation graphs in v5?   add new measurement?\n\u0009\n\n\n\n    --CDM_schema = omopv5_de\n   --results_schema = ohdsi\n  --cohort_schema = ohdsi\n  --cohort_table = cohort\n   --source_name = CDM_NAME\n    --smallcellcount = 0\n    --createTable = FALSE\n   --runHERACLESHeel = FALSE\n  --we support 4 or 5,   CDM_version = 5\n\n   --cohort_definition_id = \n\n--'2000002372'  1 large cohort\n--'2000003550,2000004386'     2 10k sized cohorts\n\n\n\n--list_of_analysis_ids = \n\n\n   --list of condition concepts to be used throughout\n--condition_concept_ids = \n   --list of drug concepts to be used throughout\n--drug_concept_ids = \n   --list of procedure concepts to be used throughout\n--procedure_concept_ids = \n   --list of observation concepts to be used throughout\n--observation_concept_ids = \n   --list of measurement concepts to be used throughout\n--measurement_concept_ids = \n\n\n--all: '0,1,2,3,4,5,6,7,8,9,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,200,201,202,203,204,205,206,207,208,209,210,211,220,400,401,402,403,404,405,406,407,408,409,410,411,412,413,414,415,416,417,418,419,420,500,501,502,503,504,505,506,509,510,511,512,513,514,515,600,601,602,603,604,605,606,607,608,609,610,611,612,613,614,615,616,617,618,619,620,700,701,702,703,704,705,706,707,708,709,710,711,712,713,714,715,716,717,717,718,719,720,800,801,802,803,804,805,806,807,808,809,810,811,812,813,814,815,816,817,818,819,820,900,901,902,903,904,905,906,907,908,909,910,911,912,913,914,915,916,917,918,919,920,1000,1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,1011,1012,1013,1014,1015,1016,1017,1018,1019,1020,1100,1101,1102,1103,1200,1201,1202,1203,1700,1701,1800,1801,1802,1803,1804,1805,1806,1807,1808,1809,1810,1811,1812,1813,1814,1815,1816,1817,1818,1819,1820,1821,1830,1831,1840,1841,1850,1851,1860,1861,1870,1871'\n--person: '0,1,2,3,4,5,6,7,8,9'\n--observation: '101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117'\n--visits: '200,201,202,203,204,205,206,207,208,209,210,211,220'\n--condition: '400,401,402,403,404,405,406,407,408,409,410,411,412,413,414,415,416,417,418,419,420'\n--death: '500,501,502,503,504,505,506,509,510,511,512,513,514,515'\n--procedure: '600,601,602,603,604,605,606,607,608,609,610,611,612,613,614,615,616,617,618,619,620'\n--drug: '700,701,702,703,704,705,706,707,708,709,710,711,712,713,714,715,716,717,717,718,719,720'\n--observation: '800,801,802,803,804,805,806,807,808,809,810,811,812,813,814,815,816,817,818,819,820'\n--drug era: '900,901,902,903,904,905,906,907,908,909,910,911,912,913,914,915,916,917,918,919,920'\n--condition era: '1000,1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,1011,1012,1013,1014,1015,1016,1017,1018,1019,1020'\n--location: '1100,1101,1102,1103'\n--care site: '1200,1201,1202,1203'\n--cohort: '1700,1701'\n--cohort-specific analyses: '1800,1801,1802,1803,1804,1805,1806,1807,1808,1809,1810,1811,1812,1813,1814,1815,1816,1817,1818,1819,1820,1821,1830,1831,1840,1841,1850,1851,1860,1861,1870,1871'\n\n\n\nEXECUTE IMMEDIATE 'ALTER SESSION SET current_schema =  ohdsi';\n\n\n--else if not createTable\ndelete from ohdsi.HERACLES_results where cohort_definition_id IN () and analysis_id IN ();\ndelete from ohdsi.HERACLES_results_dist where cohort_definition_id IN () and analysis_id IN ();\n\n\n\n--7. generate results for analysis_results\n\n\n\nBEGIN\n  EXECUTE IMMEDIATE 'TRUNCATE TABLE  HERACLES_cohort';\n  EXECUTE IMMEDIATE 'DROP TABLE  HERACLES_cohort';\nEXCEPTION\n  WHEN OTHERS THEN\n    IF SQLCODE != -942 THEN\n      RAISE;\n    END IF;\nEND;\n\nBEGIN\n  EXECUTE IMMEDIATE 'TRUNCATE TABLE  HERACLES_cohort';\n  EXECUTE IMMEDIATE 'DROP TABLE  HERACLES_cohort';\nEXCEPTION\n  WHEN OTHERS THEN\n    IF SQLCODE != -942 THEN\n      RAISE;\n    END IF;\nEND;\n  \nEXECUTE IMMEDIATE 'CREATE GLOBAL TEMPORARY TABLE HERACLES_cohort ON COMMIT PRESERVE ROWS AS SELECT subject_id, cohort_definition_id, cohort_start_date, cohort_end_date FROM ohdsi.cohort WHERE  cohort_definition_id in ()';  \n\n--\n-- 0\u0009Number of persons\ninsert into HERACLES_results (cohort_definition_id, analysis_id, stratum_1, count_value)\nselect c1.cohort_definition_id, 0 as analysis_id,  'CDM_NAME' as stratum_1, COUNT(distinct person_id) as count_value\nfrom omopv5_de.PERSON p1\ninner join (select subject_id, cohort_definition_id from HERACLES_cohort) c1\non p1.person_id = c1.subject_id\ngroup by c1.cohort_definition_id;\n\ninsert into HERACLES_results_dist (cohort_definition_id, analysis_id, stratum_1, count_value)\nselect c1.cohort_definition_id, 0 as analysis_id, 'CDM_NAME' as stratum_1, COUNT(distinct person_id) as count_value\nfrom omopv5_de.PERSON p1\ninner join (select subject_id, cohort_definition_id from HERACLES_cohort) c1\non p1.person_id = c1.subject_id\ngroup by c1.cohort_definition_id;\n\n--\n\n\n\n\n--HERACLES Analyses on PERSON table\n\n\n--\n-- 1\u0009Number of persons\ninsert into HERACLES_results (cohort_definition_id, analysis_id, count_value)\nselect c1.cohort_definition_id, 1 as analysis_id,  COUNT(distinct person_id) as count_value\nfrom omopv5_de.PERSON p1\ninner join (select subject_id, cohort_definition_id from HERACLES_cohort) c1\non p1.person_id = c1.subject_id\ngroup by c1.cohort_definition_id;\n--\n\n\n--\n-- 2\u0009Number of persons by gender\ninsert into HERACLES_results (cohort_definition_id, analysis_id, stratum_1, count_value)\nselect c1.cohort_definition_id, 2 as analysis_id,  gender_concept_id as stratum_1, COUNT(distinct person_id) as count_value\nfrom omopv5_de.PERSON p1\ninner join (select subject_id, cohort_definition_id from HERACLES_cohort) c1\non p1.person_id = c1.subject_id\ngroup by c1.cohort_definition_id, GENDER_CONCEPT_ID\n;\n--\n\n\n\n--\n-- 3\u0009Number of persons by year of birth\ninsert into HERACLES_results (cohort_definition_id, analysis_id, stratum_1, count_value)\nselect c1.cohort_definition_id, 3 as analysis_id,  year_of_birth as stratum_1, COUNT(distinct person_id) as count_value\nfrom omopv5_de.PERSON p1\ninner join (select subject_id, cohort_definition_id from HERACLES_cohort) c1\non p1.person_id = c1.subject_id\ngroup by c1.cohort_definition_id, YEAR_OF_BIRTH\n;\n--\n\n\n--\n-- 4\u0009Number of persons by race\ninsert into HERACLES_results (cohort_definition_id, analysis_id, stratum_1, count_value)\nselect c1.cohort_definition_id, 4 as analysis_id,  RACE_CONCEPT_ID as stratum_1, COUNT(distinct person_id) as count_value\nfrom omopv5_de.PERSON p1\ninner join (select subject_id, cohort_definition_id from HERACLES_cohort) c1\non p1.person_id = c1.subject_id\ngroup by c1.cohort_definition_id, RACE_CONCEPT_ID\n;\n--\n\n\n\n--\n-- 5\u0009Number of persons by ethnicity\ninsert into HERACLES_results (cohort_definition_id, analysis_id, stratum_1, count_value)\nselect c1.cohort_definition_id, 5 as analysis_id,  ETHNICITY_CONCEPT_ID as stratum_1, COUNT(distinct person_id) as count_value\nfrom omopv5_de.PERSON p1\ninner join (select subject_id, cohort_definition_id from HERACLES_cohort) c1\non p1.person_id = c1.subject_id\ngroup by c1.cohort_definition_id, ETHNICITY_CONCEPT_ID\n;\n--\n\n\n\n\n\n--\n-- 7\u0009Number of persons with invalid provider_id\ninsert into HERACLES_results (cohort_definition_id, analysis_id, count_value)\nSELECT   c1.cohort_definition_id, 7 as analysis_id,  COUNT(p1.person_id) as count_value\n FROM  omopv5_de.PERSON p1\ninner join (select subject_id, cohort_definition_id from HERACLES_cohort) c1\non p1.person_id = c1.subject_id\n\u0009left join omopv5_de.provider pr1\n\u0009on p1.provider_id = pr1.provider_id\n  WHERE  p1.provider_id is not null\n\u0009and pr1.provider_id is null\ngroup by c1.cohort_definition_id\n;\n--\n\n\n\n--\n-- 8\u0009Number of persons with invalid location_id\ninsert into HERACLES_results (cohort_definition_id, analysis_id, count_value)\nSELECT   c1.cohort_definition_id, 8 as analysis_id,  COUNT(p1.person_id) as count_value\n FROM  omopv5_de.PERSON p1\ninner join (select subject_id, cohort_definition_id from HERACLES_cohort) c1\non p1.person_id = c1.subject_id\n\u0009left join omopv5_de.location l1\n\u0009on p1.location_id = l1.location_id\n  WHERE  p1.location_id is not null\n\u0009and l1.location_id is null\ngroup by c1.cohort_definition_id\n;\n\n--\n\n\n--\n-- 9\u0009Number of persons with invalid care_site_id\ninsert into HERACLES_results (cohort_definition_id, analysis_id, count_value)\nSELECT   c1.cohort_definition_id, 9 as analysis_id,  COUNT(p1.person_id) as count_value\n FROM  omopv5_de.PERSON p1\ninner join (select subject_id, cohort_definition_id from HERACLES_cohort) c1\non p1.person_id = c1.subject_id\n\u0009left join omopv5_de.care_site cs1\n\u0009on p1.care_site_id = cs1.care_site_id\n  WHERE  p1.care_site_id is not null\n\u0009and cs1.care_site_id is null\ngroup by c1.cohort_definition_id\n;\n--\n\n\n\n\n\n\n\n----/********************************************\n\n--HERACLES Analyses on OBSERVATION_PERIOD table\n\n--*********************************************/\n\n--\n-- 101\u0009Number of persons by age, with age at first observation period\ninsert into HERACLES_results (cohort_definition_id, analysis_id, stratum_1, count_value)\nselect c1.cohort_definition_id, 101 as analysis_id,   EXTRACT(YEAR FROM op1.index_date) - p1.YEAR_OF_BIRTH as stratum_1, COUNT(p1.person_id) as count_value\nfrom omopv5_de.PERSON p1\ninner join (select subject_id, cohort_definition_id from HERACLES_cohort) c1\non p1.person_id = c1.subject_id\n\u0009inner join (select person_id, MIN(observation_period_start_date) as index_date from omopv5_de.OBSERVATION_PERIOD group by PERSON_ID) op1\n\u0009on p1.PERSON_ID = op1.PERSON_ID\ngroup by c1.cohort_definition_id, EXTRACT(YEAR FROM op1.index_date) - p1.YEAR_...";
        final String finalSql = "DECLARE BEGIN " + sql + " END;";
        log.debug("Tweaked SQL: " + sql);
        try {
            final List<SqlParameter> params = new LinkedList<SqlParameter>();
            final Map<String, Object> ret = this.jdbcTemplate.call(new CallableStatementCreator() {
                
                @Override
                public CallableStatement createCallableStatement(final Connection con) throws SQLException {
                    final CallableStatement cs = con.prepareCall(finalSql);
                    return cs;
                }
            }, params);
            log.debug(ret);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }
        return RepeatStatus.FINISHED;
    }
    
}
;