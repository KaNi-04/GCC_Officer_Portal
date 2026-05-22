package in.gov.chennaicorporation.gccoffice.childrensurvey.Service;

import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.nio.file.Files;
import javax.sql.DataSource;
import java.nio.file.Paths;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.sql.Statement;
import in.gov.chennaicorporation.gccoffice.controller.DateTimeUtil;
import in.gov.chennaicorporation.gccoffice.nulm.repository.NulmRepository;

@Service
public class ChildrenSurveyService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(@Qualifier("mysqlChildrenSurveyDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<Map<String, Object>> getGender() {

		String sql = "SELECT id, english_name FROM gender_master";

		return jdbcTemplate.queryForList(sql);
	}

	public List<Map<String, Object>> getLocation() {
		return jdbcTemplate.queryForList(
				"SELECT id, english_name FROM location_master");
	}

	public List<Map<String, Object>> getEducation() {
		return jdbcTemplate.queryForList(
				"SELECT id, english_name FROM education_master");
	}

	public List<Map<String, Object>> getSurveyList(
			String name,
			String gender,
			String age,
			String area,
			String education,
			String fromDate,
			String toDate) {
		System.out.println("name: " + name);
		System.out.println("gender: " + gender);
		System.out.println("age: " + age);
		System.out.println("area: " + area);
		System.out.println("education: " + education);
		System.out.println("fromDate: " + fromDate);
		System.out.println("toDate: " + toDate);

		StringBuilder sql = new StringBuilder("""
				    SELECT
				        csr.survey_id,

				        MAX(CASE WHEN cqm.field_name = 'q6' THEN csr.answer END) AS child_name,
				        MAX(CASE WHEN cqm.field_name = 'q7' THEN gm.english_name END) AS gender,
				        MAX(CASE WHEN cqm.field_name = 'q10' THEN csr.answer END) AS age,
				        MAX(CASE WHEN cqm.field_name = 'q24' THEN csr.answer END) AS mobile_number,
				        MAX(CASE WHEN cqm.field_name = 'q1' THEN lm.english_name END) AS area,
				        MAX(CASE WHEN cqm.field_name = 'q25' THEN em.english_name END) AS education,
				        MAX(csr.cby) AS cby,
				        DATE(MAX(csr.cdate)) AS survey_date

				    FROM child_survey_response csr

				    INNER JOIN child_survey_questions_master cqm
				        ON csr.qid = cqm.qid

				    LEFT JOIN gender_master gm
				        ON gm.id = csr.answer AND cqm.field_name = 'q7'

				    LEFT JOIN location_master lm
				        ON lm.id = csr.answer AND cqm.field_name = 'q1'

				    LEFT JOIN education_master em
				        ON em.id = csr.answer AND cqm.field_name = 'q25'

				    WHERE csr.isactive = 1
				    AND csr.isdelete = 0
				""");

		List<Object> params = new ArrayList<>();

		// 🔍 NAME
		if (name != null && !name.isEmpty()) {
			sql.append(
					" AND csr.survey_id IN (SELECT survey_id FROM child_survey_response WHERE answer LIKE ?)");
			params.add("%" + name + "%");
		}

		// 🔍 GENDER
		if (gender != null && !gender.isEmpty()) {
			sql.append(
					" AND csr.survey_id IN (SELECT survey_id FROM child_survey_response WHERE qid = 7 AND answer = ?)");
			params.add(gender);
		}

		// 🔍 AREA
		if (area != null && !area.isEmpty()) {
			sql.append(
					" AND csr.survey_id IN (SELECT survey_id FROM child_survey_response WHERE qid = 1 AND answer = ?)");
			params.add(area);
		}

		// 🔍 EDUCATION
		if (education != null && !education.isEmpty()) {
			sql.append(
					" AND csr.survey_id IN (SELECT survey_id FROM child_survey_response WHERE qid = 25 AND answer = ?)");
			params.add(education);
		}

		// 🔍 AGE RANGE
		if (age != null && !age.isEmpty()) {

			String[] range = age.split("-");

			sql.append("""
					    AND csr.survey_id IN (
					        SELECT survey_id
					        FROM child_survey_response
					        WHERE qid = 10
					        AND CAST(answer AS UNSIGNED) BETWEEN ? AND ?
					    )
					""");

			params.add(Integer.parseInt(range[0]));
			params.add(Integer.parseInt(range[1]));
		}

		// 🔍 DATE FILTER
		if (fromDate != null && toDate != null && !fromDate.isEmpty() && !toDate.isEmpty()) {
			sql.append(" AND DATE(csr.cdate) BETWEEN ? AND ?");
			params.add(fromDate);
			params.add(toDate);
		}

		sql.append(" GROUP BY csr.survey_id ORDER BY MAX(csr.cdate) DESC");

		return jdbcTemplate.queryForList(sql.toString(), params.toArray());
	}

	public Map<String, Object> getSurveyDetails(String surveyId) {

		String sql = """
				SELECT
				    csr.qid,
				    csr.answer,
				    csr.others_answer,
				    csr.cby,
				    csr.survey_id,

				    cqm.q_english,
				    cqm.question_type,
				    cqm.master_table_name,
				    cqm.field_name, cqm.Flag


				FROM child_survey_response csr

				INNER JOIN child_survey_questions_master cqm
				    ON csr.qid = cqm.qid

				WHERE csr.isactive = 1
				AND csr.isdelete = 0
				AND csr.survey_id = ?

				ORDER BY cqm.orderby
				""";

		List<Map<String, Object>> responseList = jdbcTemplate.queryForList(sql, surveyId);

		Map<String, Object> finalResponse = new LinkedHashMap<>();

		Map<String, Object> personalInfo = new LinkedHashMap<>();
		Map<String, Object> locationInfo = new LinkedHashMap<>();
		Map<String, Object> educationInfo = new LinkedHashMap<>();
		Map<String, Object> documentInfo = new LinkedHashMap<>();
		Map<String, Object> healthInfo = new LinkedHashMap<>();
		Map<String, Object> vulnerabilityInfo = new LinkedHashMap<>();

		for (Map<String, Object> row : responseList) {

			Integer qid = row.get("qid") != null
					? Integer.parseInt(row.get("qid").toString())
					: 0;
			String flag = row.get("Flag") != null
					? row.get("Flag").toString()
					: "";

			String question = row.get("q_english") != null
					? row.get("q_english").toString()
					: "";

			String questionType = row.get("question_type") != null
					? row.get("question_type").toString()
					: "";

			String masterTable = row.get("master_table_name") != null
					? row.get("master_table_name").toString()
					: "";

			String rawAnswer = row.get("answer") != null
					? row.get("answer").toString()
					: "";

			String finalAnswer = resolveAnswer(
					questionType,
					masterTable,
					rawAnswer);

			// PERSONAL INFORMATION
			/*
			 * if (qid >= 6 && qid <= 24) {
			 * 
			 * personalInfo.put(question, finalAnswer);
			 * }
			 * 
			 * // LOCATION DETAILS
			 * else if (qid >= 1 && qid <= 5) {
			 * 
			 * locationInfo.put(question, finalAnswer);
			 * }
			 * 
			 * // EDUCATION DETAILS
			 * else if (qid >= 25 && qid <= 38) {
			 * 
			 * educationInfo.put(question, finalAnswer);
			 * }
			 * 
			 * // HEALTH
			 * else if (qid >= 39 && qid <= 40) {
			 * 
			 * healthInfo.put(question, finalAnswer);
			 * }
			 * 
			 * // DOCUMENT
			 * else if (qid >= 41 && qid <= 47) {
			 * 
			 * documentInfo.put(question, finalAnswer);
			 * }
			 * 
			 * // VULNERABILITY
			 * else if (qid >= 48 && qid <= 52) {
			 * 
			 * vulnerabilityInfo.put(question, finalAnswer);
			 * }
			 */
			if ("A".equalsIgnoreCase(flag)) {

				locationInfo.put(question, finalAnswer);
			}

			// PERSONAL INFORMATION
			else if ("P".equalsIgnoreCase(flag)) {

				personalInfo.put(question, finalAnswer);
			}

			// EDUCATION DETAILS
			else if ("E".equalsIgnoreCase(flag)) {

				educationInfo.put(question, finalAnswer);
			}

			// HEALTH DETAILS
			else if ("H".equalsIgnoreCase(flag)) {

				healthInfo.put(question, finalAnswer);
			}

			// DOCUMENT DETAILS
			else if ("D".equalsIgnoreCase(flag)) {

				documentInfo.put(question, finalAnswer);
			}

			// VULNERABILITY DETAILS
			else if ("V".equalsIgnoreCase(flag)) {

				vulnerabilityInfo.put(question, finalAnswer);
			}
		}

		finalResponse.put("personalInformation", personalInfo);
		finalResponse.put("locationDetails", locationInfo);
		finalResponse.put("educationDetails", educationInfo);
		finalResponse.put("healthDetails", healthInfo);
		finalResponse.put("documentDetails", documentInfo);
		finalResponse.put("vulnerabilityDetails", vulnerabilityInfo);

		return finalResponse;
	}

	private String resolveAnswer(
			String questionType,
			String masterTable,
			String rawAnswer) {

		try {

			if (rawAnswer == null || rawAnswer.trim().isEmpty()) {
				return "";
			}

			// DIRECT TEXT TYPES
			if ("text".equalsIgnoreCase(questionType)
					|| "textarea".equalsIgnoreCase(questionType)
					|| "number".equalsIgnoreCase(questionType)
					|| "number_text".equalsIgnoreCase(questionType)
					|| "number_mobile".equalsIgnoreCase(questionType)
					|| "number_aadhar".equalsIgnoreCase(questionType)
					|| "date".equalsIgnoreCase(questionType)) {

				return rawAnswer;
			}

			// CHECK WHETHER VALUE IS NUMERIC
			boolean isNumeric = rawAnswer.matches("\\d+(,\\d+)*");

			// RADIO
			if ("radio".equalsIgnoreCase(questionType)) {

				// Already stored as text
				if (!isNumeric) {
					return rawAnswer;
				}

				String sql = """
						SELECT english_name
						FROM child_survey_answer_master
						WHERE aid = ?
						""";

				return jdbcTemplate.queryForObject(
						sql,
						String.class,
						Integer.parseInt(rawAnswer));
			}

			// MULTICHECK
			if ("multicheck".equalsIgnoreCase(questionType)) {

				// Already stored as text
				if (!isNumeric) {
					return rawAnswer;
				}

				String[] ids = rawAnswer.split(",");

				List<String> values = new ArrayList<>();

				String sql = """
						SELECT english_name
						FROM child_survey_answer_master
						WHERE aid = ?
						""";

				for (String id : ids) {

					String value = jdbcTemplate.queryForObject(
							sql,
							String.class,
							Integer.parseInt(id.trim()));

					values.add(value);
				}

				return String.join(", ", values);
			}

			// DROPDOWN
			if (masterTable != null
					&& !masterTable.isEmpty()) {

				// Already stored as text
				if (!isNumeric) {
					return rawAnswer;
				}

				String dynamicSql = "SELECT english_name FROM "
						+ masterTable
						+ " WHERE id = ?";

				return jdbcTemplate.queryForObject(
						dynamicSql,
						String.class,
						Integer.parseInt(rawAnswer));
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return rawAnswer;
	}

	/*
	 * public Map<String, Object> getSurveyDetails(String surveyId) {
	 * 
	 * String sql = """
	 * SELECT
	 * csr.qid,
	 * csr.answer,
	 * csr.others_answer,
	 * csr.cby,
	 * csr.survey_id,
	 * 
	 * cqm.q_english,
	 * cqm.question_type,
	 * cqm.master_table_name,
	 * cqm.field_name
	 * 
	 * FROM child_survey_response csr
	 * 
	 * INNER JOIN child_survey_questions_master cqm
	 * ON csr.qid = cqm.qid
	 * 
	 * WHERE csr.isactive = 1
	 * AND csr.isdelete = 0
	 * AND csr.survey_id = ?
	 * 
	 * ORDER BY cqm.orderby
	 * """;
	 * 
	 * List<Map<String, Object>> responseList = jdbcTemplate.queryForList(sql,
	 * surveyId);
	 * 
	 * Map<String, Object> finalResponse = new LinkedHashMap<>();
	 * 
	 * for (Map<String, Object> row : responseList) {
	 * 
	 * String question = row.get("q_english") != null
	 * ? row.get("q_english").toString()
	 * : "";
	 * 
	 * String questionType = row.get("question_type") != null
	 * ? row.get("question_type").toString()
	 * : "";
	 * 
	 * String masterTable = row.get("master_table_name") != null
	 * ? row.get("master_table_name").toString()
	 * : "";
	 * 
	 * String rawAnswer = row.get("answer") != null
	 * ? row.get("answer").toString()
	 * : "";
	 * 
	 * String finalAnswer = resolveAnswer(
	 * questionType,
	 * masterTable,
	 * rawAnswer);
	 * 
	 * finalResponse.put(question, finalAnswer);
	 * }
	 * 
	 * return finalResponse;
	 * }
	 */

	// =========================================
	// ANSWER RESOLVER
	// =========================================

	/*
	 * private String resolveAnswer(
	 * String questionType,
	 * String masterTable,
	 * String rawAnswer) {
	 * 
	 * try {
	 * 
	 * if (rawAnswer == null || rawAnswer.isEmpty()) {
	 * return "";
	 * }
	 * 
	 * // TEXT
	 * if ("text".equalsIgnoreCase(questionType)
	 * || "textarea".equalsIgnoreCase(questionType)
	 * || "number".equalsIgnoreCase(questionType)) {
	 * 
	 * return rawAnswer;
	 * }
	 * 
	 * // RADIO
	 * if ("radio".equalsIgnoreCase(questionType)) {
	 * 
	 * String sql = """
	 * SELECT english_name
	 * FROM child_survey_answer_master
	 * WHERE aid = ?
	 * """;
	 * 
	 * return jdbcTemplate.queryForObject(
	 * sql,
	 * String.class,
	 * Integer.parseInt(rawAnswer));
	 * }
	 * 
	 * // MULTICHECK
	 * if ("multicheck".equalsIgnoreCase(questionType)) {
	 * 
	 * String[] ids = rawAnswer.split(",");
	 * 
	 * List<String> values = new ArrayList<>();
	 * 
	 * String sql = """
	 * SELECT english_name
	 * FROM child_survey_answer_master
	 * WHERE aid = ?
	 * """;
	 * 
	 * for (String id : ids) {
	 * 
	 * String value = jdbcTemplate.queryForObject(
	 * sql,
	 * String.class,
	 * Integer.parseInt(id.trim()));
	 * 
	 * values.add(value);
	 * }
	 * 
	 * return String.join(", ", values);
	 * }
	 * 
	 * // DROPDOWN
	 * if (masterTable != null
	 * && !masterTable.isEmpty()) {
	 * 
	 * String dynamicSql = "SELECT english_name FROM "
	 * + masterTable
	 * + " WHERE id = ?";
	 * 
	 * return jdbcTemplate.queryForObject(
	 * dynamicSql,
	 * String.class,
	 * Integer.parseInt(rawAnswer));
	 * }
	 * 
	 * } catch (Exception e) {
	 * 
	 * e.printStackTrace();
	 * }
	 * 
	 * return rawAnswer;
	 * }
	 */
	/*
	 * public List<Map<String, Object>> getSurveyorsList(
	 * String name,
	 * String area,
	 * String fromDate,
	 * String toDate) {
	 * // ld.survey_id AS surveyor_id,
	 * StringBuilder sql = new StringBuilder("""
	 * 
	 * SELECT
	 * 
	 * 
	 * d.user_name,
	 * d.mobile_no,
	 * ld.loginId,
	 * lm.id as location_id,
	 * 
	 * 
	 * MAX(lm.english_name) AS area,
	 * 
	 * COUNT(DISTINCT ld.survey_id) AS total_survey_done,
	 * 
	 * DATE(MAX(sr.cdate)) AS survey_date
	 * 
	 * FROM login_details d
	 * 
	 * INNER JOIN surveyor_location_details ld
	 * ON d.uid = ld.loginId
	 * 
	 * INNER JOIN child_survey_response sr
	 * ON sr.survey_id = ld.survey_id
	 * 
	 * LEFT JOIN child_survey_response csr
	 * ON csr.survey_id = ld.survey_id
	 * AND csr.qid = 1
	 * 
	 * LEFT JOIN location_master lm
	 * ON lm.id = csr.answer
	 * 
	 * WHERE sr.isactive = 1
	 * AND sr.isdelete = 0
	 * 
	 * """);
	 * 
	 * List<Object> params = new ArrayList<>();
	 * 
	 * // NAME FILTER
	 * if (name != null && !name.isEmpty()) {
	 * 
	 * sql.append("""
	 * AND d.user_name LIKE ?
	 * """);
	 * 
	 * params.add("%" + name + "%");
	 * }
	 * 
	 * // AREA FILTER
	 * if (area != null && !area.isEmpty()) {
	 * 
	 * sql.append("""
	 * AND csr.answer = ?
	 * """);
	 * 
	 * params.add(area);
	 * }
	 * 
	 * // DATE FILTER
	 * if (fromDate != null
	 * && toDate != null
	 * && !fromDate.isEmpty()
	 * && !toDate.isEmpty()) {
	 * 
	 * sql.append("""
	 * AND DATE(sr.cdate) BETWEEN ? AND ?
	 * """);
	 * 
	 * params.add(fromDate);
	 * params.add(toDate);
	 * }
	 * // ld.survey_id,
	 * sql.append("""
	 * 
	 * GROUP BY
	 * 
	 * d.user_name,
	 * d.mobile_no,
	 * ld.loginId,
	 * lm.id
	 * 
	 * ORDER BY
	 * MAX(sr.cdate) DESC
	 * 
	 * """);
	 * 
	 * return jdbcTemplate.queryForList(
	 * sql.toString(),
	 * params.toArray());
	 * }
	 */
	public List<Map<String, Object>> getSurveyorsList(
			String name,
			String area,
			String fromDate,
			String toDate) {

		StringBuilder sql = new StringBuilder("""

				SELECT

				    d.user_name,
				    d.mobile_no,
				    ld.loginId,
				    lm.id AS location_id,

				    MAX(lm.english_name) AS area,

				    COUNT(DISTINCT ld.survey_id) AS total_survey_done,

				    DATE(sr.cdate) AS survey_date

				FROM login_details d

				INNER JOIN surveyor_location_details ld
				    ON d.uid = ld.loginId

				INNER JOIN (
				    SELECT DISTINCT
				        survey_id,
				        DATE(cdate) AS cdate
				    FROM child_survey_response
				    WHERE isactive = 1
				    AND isdelete = 0
				) sr
				    ON sr.survey_id = ld.survey_id

				LEFT JOIN child_survey_response csr
				    ON csr.survey_id = ld.survey_id
				    AND csr.qid = 1

				LEFT JOIN location_master lm
				    ON lm.id = csr.answer

				WHERE 1 = 1

				""");

		List<Object> params = new ArrayList<>();

		// NAME FILTER
		if (name != null && !name.isEmpty()) {

			sql.append("""
					    AND d.user_name LIKE ?
					""");

			params.add("%" + name + "%");
		}

		// AREA FILTER
		if (area != null && !area.isEmpty()) {

			sql.append("""
					    AND csr.answer = ?
					""");

			params.add(area);
		}

		// DATE FILTER
		if (fromDate != null
				&& toDate != null
				&& !fromDate.isEmpty()
				&& !toDate.isEmpty()) {

			sql.append("""
					    AND DATE(sr.cdate) BETWEEN ? AND ?
					""");

			params.add(fromDate);
			params.add(toDate);
		}

		sql.append("""

				GROUP BY

				    d.user_name,
				    d.mobile_no,
				    ld.loginId,
				    lm.id,
				    DATE(sr.cdate)

				ORDER BY
				    DATE(sr.cdate) DESC

				""");

		return jdbcTemplate.queryForList(
				sql.toString(),
				params.toArray());
	}

	public List<Map<String, Object>> getSurveyBreakup(String loginId, String locationId, String surveyDate) {
		System.out.println(".......................................................");
		System.out.println(loginId + " " + locationId + " " + surveyDate);
		System.out.println(".......................................................");
		System.out.println("surveyDate=" + surveyDate);
		System.out.println("locationId=" + locationId);
		System.out.println("loginId=" + loginId);
		String sql = """

								    SELECT

								        sr.survey_id,

								        MAX(CASE
								            WHEN cqm.field_name = 'q6'
								            THEN csr.answer
								        END) AS child_name,

								        MAX(CASE
								            WHEN cqm.field_name = 'q7'
								            THEN gm.english_name
								        END) AS gender,

								        MAX(CASE
								            WHEN cqm.field_name = 'q10'
								            THEN csr.answer
								        END) AS age,

								        MAX(CASE
								            WHEN cqm.field_name = 'q24'
								            THEN csr.answer
								        END) AS mobile_number,

								        MAX(CASE
								            WHEN cqm.field_name = 'q1'
								            THEN lm.english_name
								        END) AS area,

								        MAX(CASE
								            WHEN cqm.field_name = 'q25'
								            THEN em.english_name
								        END) AS education,

								        DATE(MAX(sr.cdate)) AS survey_date

								    FROM surveyor_location_details sld

								    INNER JOIN child_survey_response sr
								        ON sr.survey_id = sld.survey_id

								    INNER JOIN child_survey_response csr
								        ON csr.survey_id = sr.survey_id

								    INNER JOIN child_survey_questions_master cqm
								        ON cqm.qid = csr.qid

								    LEFT JOIN gender_master gm
								        ON gm.id = csr.answer
								        AND cqm.field_name = 'q7'

								    LEFT JOIN location_master lm
								        ON lm.id = csr.answer
								        AND cqm.field_name = 'q1'

								    LEFT JOIN education_master em
								        ON em.id = csr.answer
								        AND cqm.field_name = 'q25'

										where sld.loginId = ?
										AND sr.survey_id IN (

				    SELECT survey_id
				    FROM child_survey_response
				    WHERE qid = 1
				    AND answer = ?



				)
										and  DATE(sr.cdate) = ?


								    AND sr.isactive = 1
								    AND sr.isdelete = 0

								    GROUP BY sr.survey_id

								    ORDER BY MAX(sr.cdate) DESC

								""";

		return jdbcTemplate.queryForList(sql, loginId, locationId, surveyDate);
	}

	// dashboard

	public Map<String, Object> getDashboardData() {

		Map<String, Object> response = new LinkedHashMap<>();

		// TOTAL CHILDREN

		String totalSql = """
				    SELECT COUNT(DISTINCT survey_id)
				    FROM child_survey_response
				    WHERE isactive = 1
				    AND isdelete = 0
				""";

		Integer totalChildren = jdbcTemplate.queryForObject(totalSql, Integer.class);

		response.put("totalChildren", totalChildren);

		// AREAS COVERED

		String areaCountSql = """
				    SELECT COUNT(*)
				    FROM location_master
				""";

		Integer areasCovered = jdbcTemplate.queryForObject(areaCountSql, Integer.class);

		response.put("areasCovered", areasCovered);

		// MALE

		String maleSql = """
				    SELECT COUNT(DISTINCT survey_id)
				    FROM child_survey_response
				    WHERE qid = 7
				    AND answer = 1
				""";

		response.put(
				"maleCount",
				jdbcTemplate.queryForObject(maleSql, Integer.class));

		// FEMALE

		String femaleSql = """
				    SELECT COUNT(DISTINCT survey_id)
				    FROM child_survey_response
				    WHERE qid = 7
				    AND answer = 2
				""";

		response.put(
				"femaleCount",
				jdbcTemplate.queryForObject(femaleSql, Integer.class));

		// OTHERS

		String othersSql = """
					SELECT COUNT(DISTINCT survey_id)
					FROM child_survey_response
					WHERE qid = 7
					AND answer = 3
				""";

		response.put(
				"othersCount",
				jdbcTemplate.queryForObject(othersSql, Integer.class));

		// CURRENTLY STUDYING

		/*
		 * String studyingSql = """
		 * SELECT COUNT(DISTINCT survey_id)
		 * FROM child_survey_response
		 * WHERE qid = 25
		 * AND answer = 1
		 * """;
		 * 
		 * response.put(
		 * "currentlyStudying",
		 * jdbcTemplate.queryForObject(studyingSql, Integer.class));
		 */
		String studyingSql = """

				    SELECT

				        ROUND(
				            (
				                COUNT(DISTINCT CASE
				                    WHEN qid = 25
				                    AND answer = 9
				                    THEN survey_id
				                END)
				                * 100.0
				            )
				            /
				            COUNT(DISTINCT survey_id),
				        0) AS studying_percentage

				    FROM child_survey_response

				    WHERE isactive = 1
				    AND isdelete = 0

				""";

		response.put(
				"currentlyStudying",
				jdbcTemplate.queryForObject(
						studyingSql,
						Integer.class));

		// AGE 4-8

		String age4to8Sql = """
				    SELECT COUNT(DISTINCT survey_id)
				    FROM child_survey_response
				    WHERE qid = 10
				    AND CAST(answer AS UNSIGNED) BETWEEN 4 AND 8
				""";

		response.put(
				"age4to8",
				jdbcTemplate.queryForObject(age4to8Sql, Integer.class));

		// AGE 9-13

		String age9to13Sql = """
				    SELECT COUNT(DISTINCT survey_id)
				    FROM child_survey_response
				    WHERE qid = 10
				    AND CAST(answer AS UNSIGNED) BETWEEN 9 AND 13
				""";

		response.put(
				"age9to13",
				jdbcTemplate.queryForObject(age9to13Sql, Integer.class));

		// AGE 14-18

		String age14to18Sql = """
				    SELECT COUNT(DISTINCT survey_id)
				    FROM child_survey_response
				    WHERE qid = 10
				    AND CAST(answer AS UNSIGNED) BETWEEN 14 AND 18
				""";

		response.put(
				"age14to18",
				jdbcTemplate.queryForObject(age14to18Sql, Integer.class));

		// AREA WISE

		String areaWiseSql = """
				    SELECT

				        lm.english_name AS area,

				        COUNT(DISTINCT csr.survey_id) AS total_children

				    FROM location_master lm

				    LEFT JOIN child_survey_response csr
				        ON lm.id = csr.answer
				        AND csr.qid = 1
				        AND csr.isactive = 1
				        AND csr.isdelete = 0

				    GROUP BY lm.id, lm.english_name

				    ORDER BY lm.orderby
				""";

		response.put(
				"areaWise",
				jdbcTemplate.queryForList(areaWiseSql));

		// VULNERABILITY

		String vulnerabilitySql = """
				    SELECT

				        vm.english_name AS vulnerability,

				        COUNT(DISTINCT csr.survey_id) AS total

				    FROM vulnerability_master vm

				    LEFT JOIN child_survey_response csr
				        ON FIND_IN_SET(vm.id, csr.answer)
				        AND csr.qid = 48
				        AND csr.isactive = 1
				        AND csr.isdelete = 0

				    GROUP BY vm.id, vm.english_name

				    ORDER BY vm.orderby
				""";

		response.put(
				"vulnerability",
				jdbcTemplate.queryForList(vulnerabilitySql));

		// EDUCATION STATUS

		// EDUCATION STATUS

		String educationSql = """
				SELECT

				    (
				        SELECT COUNT(DISTINCT survey_id)
				        FROM child_survey_response
				        WHERE qid = 25
				        AND answer = 1
				        AND isactive = 1
				        AND isdelete = 0
				    ) AS currently_studying,

				    (
				        SELECT COUNT(DISTINCT survey_id)
				        FROM child_survey_response
				        WHERE qid = 25
				        AND answer = 2
				        AND isactive = 1
				        AND isdelete = 0
				    ) AS not_studying,

				    (
				        SELECT COUNT(DISTINCT survey_id)
				        FROM child_survey_response
				        WHERE qid = 34
				        AND answer = 1
				        AND isactive = 1
				        AND isdelete = 0
				    ) AS continue_education_intent
				""";

		response.put(
				"educationStatus",
				jdbcTemplate.queryForMap(educationSql));
		// HEALTH OVERVIEW

		String healthSql = """

				    SELECT

				        CASE

				            WHEN LOWER(cam.english_name) = 'yes'
				            THEN 'Health Issue'

				            WHEN LOWER(cam.english_name) = 'no'
				            THEN 'No Health Issue'

				            ELSE cam.english_name

				        END AS status,

				        COUNT(DISTINCT csr.survey_id) AS total

				    FROM child_survey_response csr

				    INNER JOIN child_survey_answer_master cam
				        ON cam.aid = csr.answer
				        AND csr.qid = 39

				    WHERE csr.qid = 39
				    AND csr.isactive = 1
				    AND csr.isdelete = 0

				    GROUP BY status

				""";

		response.put(
				"healthOverview",
				jdbcTemplate.queryForList(healthSql));

		// CERTIFICATE DETAILS

		String certificateSql = """

				    SELECT

				        (
				            SELECT COUNT(DISTINCT survey_id)
				            FROM child_survey_response
				            WHERE qid = 42
				            AND answer = 1
				            AND isactive = 1
				            AND isdelete = 0
				        ) AS aadhar_yes,

				        (
				            SELECT COUNT(DISTINCT survey_id)
				            FROM child_survey_response
				            WHERE qid = 42
				            AND answer = 2
				            AND isactive = 1
				            AND isdelete = 0
				        ) AS aadhar_no,

				        (
				            SELECT COUNT(DISTINCT survey_id)
				            FROM child_survey_response
				            WHERE qid = 44
				            AND answer = 1
				            AND isactive = 1
				            AND isdelete = 0
				        ) AS community_yes,

				        (
				            SELECT COUNT(DISTINCT survey_id)
				            FROM child_survey_response
				            WHERE qid = 44
				            AND answer = 2
				            AND isactive = 1
				            AND isdelete = 0
				        ) AS community_no,

				        (
				            SELECT COUNT(DISTINCT survey_id)
				            FROM child_survey_response
				            WHERE qid = 45
				            AND answer = 1
				            AND isactive = 1
				            AND isdelete = 0
				        ) AS birth_yes,

				        (
				            SELECT COUNT(DISTINCT survey_id)
				            FROM child_survey_response
				            WHERE qid = 45
				            AND answer = 2
				            AND isactive = 1
				            AND isdelete = 0
				        ) AS birth_no

				""";

		response.put(
				"certificateDetails",
				jdbcTemplate.queryForMap(certificateSql));

		return response;
	}

	public List<Map<String, Object>> getEducationList() {

		String sql = """
				    SELECT
				        id,
				        english_name,
				        isactive,
				        isdelete
				    FROM education_master
				    ORDER BY orderby
				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveEducation(Map<String, Object> req) {

		Integer nextOrder = jdbcTemplate.queryForObject(
				"""
						SELECT IFNULL(MAX(orderby),0) + 1
						FROM education_master
						""",
				Integer.class);

		String sql = """
				INSERT INTO education_master
				(
				    english_name,
				    orderby,
				    cdate,
				    isactive,
				    isdelete
				)
				VALUES
				(
				    ?,
				    ?,
				    NOW(),
				    1,
				    0
				)
				""";

		jdbcTemplate.update(
				sql,
				req.get("english_name"),
				nextOrder);
	}

	public void updateStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """
				    UPDATE education_master
				    SET
				        isactive = ?,
				        isdelete = ?
				    WHERE id = ?
				""";

		jdbcTemplate.update(
				sql,
				status,
				status == 1 ? 0 : 1,
				id);
	}

	public List<Map<String, Object>> getLocationList() {

		String sql = """
					SELECT
						id,
						english_name,
						isactive,
						isdelete
					FROM location_master
					ORDER BY orderby
				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveLocation(Map<String, Object> req) {

		Integer nextOrder = jdbcTemplate.queryForObject(
				"""
						SELECT IFNULL(MAX(orderby),0) + 1
						FROM location_master
						""",
				Integer.class);

		String sql = """
				INSERT INTO location_master
				(
				    english_name,
				    orderby,
				    cdate,
				    isactive,
				    isdelete
				)
				VALUES
				(
				    ?,
				    ?,
				    NOW(),
				    1,
				    0
				)
				""";

		jdbcTemplate.update(
				sql,
				req.get("english_name"),
				nextOrder);
	}

	public void updateLocationStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """
				    UPDATE location_master
				    SET
				        isactive = ?,
				        isdelete = ?
				    WHERE id = ?
				""";

		jdbcTemplate.update(
				sql,
				status,
				status == 1 ? 0 : 1,
				id);
	}

	public List<Map<String, Object>> getGenderList() {

		String sql = """
					SELECT
						id,
						english_name,
						isactive,
						isdelete
					FROM gender_master
					ORDER BY orderby
				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveGender(Map<String, Object> req) {

		Integer nextOrder = jdbcTemplate.queryForObject(
				"""
							SELECT IFNULL(MAX(orderby),0) + 1
							FROM gender_master
						""",
				Integer.class);

		String sql = """
					INSERT INTO gender_master
					(
					    english_name,
					    orderby,
					    cdate,
					    isactive,
					    isdelete
					)
					VALUES
					(
					    ?,
					    ?,
					    NOW(),
					    1,
					    0
					)
				""";

		jdbcTemplate.update(
				sql,
				req.get("english_name"),
				nextOrder);
	}

	public void updateGenderStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """
				    UPDATE gender_master
				    SET
				        isactive = ?,
				        isdelete = ?
				    WHERE id = ?
				""";

		jdbcTemplate.update(
				sql,
				status,
				status == 1 ? 0 : 1,
				id);
	}

	public List<Map<String, Object>> getCasteList() {

		String sql = """
					SELECT
						id,
						english_name,
						isactive,
						isdelete
					FROM caste_master
					ORDER BY orderby
				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveCaste(Map<String, Object> req) {

		Integer nextOrder = jdbcTemplate.queryForObject(
				"""
							SELECT IFNULL(MAX(orderby),0) + 1
							FROM caste_master
						""",
				Integer.class);

		String sql = """
					INSERT INTO caste_master
					(
					    english_name,
					    orderby,
					    cdate,
					    isactive,
					    isdelete
					)
					VALUES
					(
					    ?,
					    ?,
					    NOW(),
					    1,
					    0
					)
				""";

		jdbcTemplate.update(
				sql,
				req.get("english_name"),
				nextOrder);
	}

	public void updateCasteStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """
				    UPDATE caste_master
				    SET
				        isactive = ?,
				        isdelete = ?
				    WHERE id = ?
				""";

		jdbcTemplate.update(
				sql,
				status,
				status == 1 ? 0 : 1,
				id);
	}

	public List<Map<String, Object>> getLivingList() {

		String sql = """
					SELECT
						id,
						english_name,
						isactive,
						isdelete
					FROM child_living_master
					ORDER BY orderby
				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveLiving(Map<String, Object> req) {

		Integer nextOrder = jdbcTemplate.queryForObject(
				"""
							SELECT IFNULL(MAX(orderby),0) + 1
							FROM child_living_master
						""",
				Integer.class);

		String sql = """
					INSERT INTO child_living_master
					(
					    english_name,
					    orderby,
					    cdate,
					    isactive,
					    isdelete
					)
					VALUES
					(
					    ?,
					    ?,
					    NOW(),
					    1,
					    0
					)
				""";

		jdbcTemplate.update(
				sql,
				req.get("english_name"),
				nextOrder);
	}

	public void updateLivingStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """
				    UPDATE child_living_master
				    SET
				        isactive = ?,
				        isdelete = ?
				    WHERE id = ?
				""";

		jdbcTemplate.update(
				sql,
				status,
				status == 1 ? 0 : 1,
				id);
	}

	public List<Map<String, Object>> getDocumentCreationList() {

		String sql = """
					SELECT
						id,
						english_name,
						isactive,
						isdelete
					FROM document_correction_master
					ORDER BY orderby
				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveDocumentCreation(Map<String, Object> req) {

		Integer nextOrder = jdbcTemplate.queryForObject(
				"""
							SELECT IFNULL(MAX(orderby),0) + 1
							FROM document_correction_master
						""",
				Integer.class);

		String sql = """
					INSERT INTO document_correction_master
					(
					    english_name,
					    orderby,
					    cdate,
					    isactive,
					    isdelete
					)
					VALUES
					(
					    ?,
					    ?,
					    NOW(),
					    1,
					    0
					)
				""";

		jdbcTemplate.update(
				sql,
				req.get("english_name"),
				nextOrder);
	}

	public void updateDocumentCreationStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """
				    UPDATE document_correction_master
				    SET
				        isactive = ?,
				        isdelete = ?
				    WHERE id = ?
				""";

		jdbcTemplate.update(
				sql,
				status,
				status == 1 ? 0 : 1,
				id);
	}

	public List<Map<String, Object>> getDropoutReasonList() {

		String sql = """
					SELECT
						id,
						english_name,
						isactive,
						isdelete
					FROM dropout_reason_master
					ORDER BY orderby
				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveDropoutReason(Map<String, Object> req) {

		Integer nextOrder = jdbcTemplate.queryForObject(
				"""
							SELECT IFNULL(MAX(orderby),0) + 1
							FROM dropout_reason_master
						""",
				Integer.class);

		String sql = """
					INSERT INTO dropout_reason_master
					(
					    english_name,
					    orderby,
					    cdate,
					    isactive,
					    isdelete
					)
					VALUES
					(
					    ?,
					    ?,
					    NOW(),
					    1,
					    0
					)
				""";

		jdbcTemplate.update(
				sql,
				req.get("english_name"),
				nextOrder);
	}

	public void updateDropoutReasonStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """
				    UPDATE dropout_reason_master
				    SET
				        isactive = ?,
				        isdelete = ?
				    WHERE id = ?
				""";

		jdbcTemplate.update(
				sql,
				status,
				status == 1 ? 0 : 1,
				id);
	}

	public List<Map<String, Object>> getIncomeList() {

		String sql = """
					SELECT
						id,
						english_name,
						isactive,
						isdelete
					FROM income_master
					ORDER BY orderby
				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveIncome(Map<String, Object> req) {

		Integer nextOrder = jdbcTemplate.queryForObject(
				"""
							SELECT IFNULL(MAX(orderby),0) + 1
							FROM income_master
						""",
				Integer.class);

		String sql = """
					INSERT INTO income_master
					(
					    english_name,
					    orderby,
					    cdate,
					    isactive,
					    isdelete
					)
					VALUES
					(
					    ?,
					    ?,
					    NOW(),
					    1,
					    0
					)
				""";

		jdbcTemplate.update(
				sql,
				req.get("english_name"),
				nextOrder);
	}

	public void updateIncomeStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """
				    UPDATE income_master
				    SET
				        isactive = ?,
				        isdelete = ?
				    WHERE id = ?
				""";

		jdbcTemplate.update(
				sql,
				status,
				status == 1 ? 0 : 1,
				id);
	}

	public List<Map<String, Object>> getInterestFieldList() {

		String sql = """
					SELECT
						id,
						english_name,
						isactive,
						isdelete
					FROM interest_field_master
					ORDER BY orderby
				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveInterestField(Map<String, Object> req) {

		Integer nextOrder = jdbcTemplate.queryForObject(
				"""
							SELECT IFNULL(MAX(orderby),0) + 1
							FROM interest_field_master
						""",
				Integer.class);

		String sql = """
					INSERT INTO interest_field_master
					(
					    english_name,
					    orderby,
					    cdate,
					    isactive,
					    isdelete
					)
					VALUES
					(
					    ?,
					    ?,
					    NOW(),
					    1,
					    0
					)
				""";

		jdbcTemplate.update(
				sql,
				req.get("english_name"),
				nextOrder);
	}

	public void updateInterestFieldStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """
				    UPDATE interest_field_master
				    SET
				        isactive = ?,
				        isdelete = ?
				    WHERE id = ?
				""";

		jdbcTemplate.update(
				sql,
				status,
				status == 1 ? 0 : 1,
				id);
	}

	public List<Map<String, Object>> getOwnerList() {

		String sql = """
					SELECT
						id,
						english_name,
						isactive,
						isdelete
					FROM ownership_master
					ORDER BY orderby
				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveOwner(Map<String, Object> req) {

		Integer nextOrder = jdbcTemplate.queryForObject(
				"""
							SELECT IFNULL(MAX(orderby),0) + 1
							FROM ownership_master
						""",
				Integer.class);

		String sql = """
					INSERT INTO ownership_master
					(
					    english_name,
					    orderby,
					    cdate,
					    isactive,
					    isdelete
					)
					VALUES
					(
					    ?,
					    ?,
					    NOW(),
					    1,
					    0
					)
				""";

		jdbcTemplate.update(
				sql,
				req.get("english_name"),
				nextOrder);
	}

	public void updateOwnerStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """
				    UPDATE ownership_master
				    SET
				        isactive = ?,
				        isdelete = ?
				    WHERE id = ?
				""";

		jdbcTemplate.update(
				sql,
				status,
				status == 1 ? 0 : 1,
				id);
	}

	public List<Map<String, Object>> getReligionList() {

		String sql = """
					SELECT
						id,
						english_name,
						isactive,
						isdelete
					FROM religion_master
					ORDER BY orderby
				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveReligion(Map<String, Object> req) {

		Integer nextOrder = jdbcTemplate.queryForObject(
				"""
							SELECT IFNULL(MAX(orderby),0) + 1
							FROM religion_master
						""",
				Integer.class);

		String sql = """
					INSERT INTO religion_master
					(
					    english_name,
					    orderby,
					    cdate,
					    isactive,
					    isdelete
					)
					VALUES
					(
					    ?,
					    ?,
					    NOW(),
					    1,
					    0
					)
				""";

		jdbcTemplate.update(
				sql,
				req.get("english_name"),
				nextOrder);
	}

	public void updateReligionStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """
				    UPDATE religion_master
				    SET
				        isactive = ?,
				        isdelete = ?
				    WHERE id = ?
				""";

		jdbcTemplate.update(
				sql,
				status,
				status == 1 ? 0 : 1,
				id);
	}

	public List<Map<String, Object>> getVulnerabilityList() {

		String sql = """
					SELECT
						id,
						english_name,
						isactive,
						isdelete
					FROM vulnerability_master
					ORDER BY orderby
				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveVulnerability(Map<String, Object> req) {

		Integer nextOrder = jdbcTemplate.queryForObject(
				"""
							SELECT IFNULL(MAX(orderby),0) + 1
							FROM vulnerability_master
						""",
				Integer.class);

		String sql = """
					INSERT INTO vulnerability_master
					(
					    english_name,
					    orderby,
					    cdate,
					    isactive,
					    isdelete
					)
					VALUES
					(
					    ?,
					    ?,
					    NOW(),
					    1,
					    0
					)
				""";

		jdbcTemplate.update(
				sql,
				req.get("english_name"),
				nextOrder);
	}

	public void updateVulnerabilityStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """
				    UPDATE vulnerability_master
				    SET
				        isactive = ?,
				        isdelete = ?
				    WHERE id = ?
				""";

		jdbcTemplate.update(
				sql,
				status,
				status == 1 ? 0 : 1,
				id);
	}

	public List<Map<String, Object>> getWhereaboutList() {

		String sql = """
					SELECT
						id,
						english_name,
						isactive,
						isdelete
					FROM where_about_master
					ORDER BY orderby
				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveWhereabout(Map<String, Object> req) {

		Integer nextOrder = jdbcTemplate.queryForObject(
				"""
							SELECT IFNULL(MAX(orderby),0) + 1
							FROM where_about_master
						""",
				Integer.class);

		String sql = """
					INSERT INTO where_about_master
					(
					    english_name,
					    orderby,
					    cdate,
					    isactive,
					    isdelete
					)
					VALUES
					(
					    ?,
					    ?,
					    NOW(),
					    1,
					    0
					)
				""";

		jdbcTemplate.update(
				sql,
				req.get("english_name"),
				nextOrder);
	}

	public void updateWhereaboutStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """
				    UPDATE where_about_master
				    SET
				        isactive = ?,
				        isdelete = ?
				    WHERE id = ?
				""";

		jdbcTemplate.update(
				sql,
				status,
				status == 1 ? 0 : 1,
				id);
	}

	public List<Map<String, Object>> getDocumentReasonList() {

		String sql = """
					SELECT
						id,
						english_name,
						isactive,
						isdelete
					FROM document_reason_master
					ORDER BY orderby
				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveDocumentReason(Map<String, Object> req) {

		Integer nextOrder = jdbcTemplate.queryForObject(
				"""
							SELECT IFNULL(MAX(orderby),0) + 1
							FROM document_reason_master
						""",
				Integer.class);

		String sql = """
					INSERT INTO document_reason_master
					(
					    english_name,
					    orderby,
					    cdate,
					    isactive,
					    isdelete
					)
					VALUES
					(
					    ?,
					    ?,
					    NOW(),
					    1,
					    0
					)
				""";

		jdbcTemplate.update(
				sql,
				req.get("english_name"),
				nextOrder);
	}

	public void updateDocumentReasonStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """
				    UPDATE document_reason_master
				    SET
				        isactive = ?,
				        isdelete = ?
				    WHERE id = ?
				""";

		jdbcTemplate.update(
				sql,
				status,
				status == 1 ? 0 : 1,
				id);
	}

}
