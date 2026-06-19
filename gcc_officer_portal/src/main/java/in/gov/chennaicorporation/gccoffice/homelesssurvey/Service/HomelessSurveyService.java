package in.gov.chennaicorporation.gccoffice.homelesssurvey.Service;

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
public class HomelessSurveyService {
	@Autowired
	@Value("${fileBaseUrl}")

	private String fileBaseUrl;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private Environment environment;

	@Autowired
	public void setDataSource(@Qualifier("mysqlHomelessSurveyDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public HomelessSurveyService(Environment environment) {

		this.environment = environment;

		this.fileBaseUrl = environment.getProperty("fileBaseUrl");
	}

	public List<Map<String, Object>> getHomelessSurveyList(
			String zone,
			String ward,
			String fromDate,
			String toDate) {

		StringBuilder sql = new StringBuilder("""

												SELECT

												    DATE(MAX(hr.cdate)) AS survey_date,

												    hr.survey_id,

												    MAX(CASE
												            WHEN hr.qid = 2
												            THEN hr.answer
												        END) AS name,

												   MAX(
				    CASE
				        WHEN hr.qid = 7
				        THEN CASE
				                WHEN hr.others_answer IS NOT NULL
				                     AND TRIM(hr.others_answer) <> ''
				                THEN hr.others_answer
				                ELSE gm.english_name
				             END
				    END
				) AS gender,

												    sld.zone AS zone,

												    sld.ward AS ward

												FROM homeless_survey_response hr

												LEFT JOIN gender_master gm
												    ON hr.qid = 7
												    AND gm.id = hr.answer

												LEFT JOIN surveyor_location_details sld
												    ON sld.survey_id = hr.survey_id




												WHERE hr.isactive = 1
												AND hr.isdelete = 0
												AND NOT EXISTS (
								    SELECT 1
								    FROM homeless_survey_response hr2
								    INNER JOIN homeless_survey_questions_master hqm2
								        ON hqm2.qid = hr2.qid
								    WHERE hr2.survey_id = hr.survey_id
								    AND hqm2.flag = 'N'
								)

												""");

		List<Object> params = new ArrayList<>();

		if (zone != null && !zone.isBlank()) {

			sql.append("""
					 AND CAST(sld.zone AS UNSIGNED) = ?
					""");

			params.add(zone);
		}

		if (ward != null && !ward.isBlank()) {

			sql.append("""
					AND CAST(sld.ward AS UNSIGNED) = ?
					""");

			params.add(ward);
		}

		if (fromDate != null && !fromDate.isBlank()
				&& toDate != null && !toDate.isBlank()) {

			sql.append("""
					AND DATE(hr.cdate) BETWEEN ? AND ?
					""");

			params.add(fromDate);
			params.add(toDate);
		}

		sql.append("""

				GROUP BY

				    hr.survey_id,
				    sld.zone,
				    sld.ward

				ORDER BY

				    MAX(hr.cdate) DESC

				""");

		return jdbcTemplate.queryForList(
				sql.toString(),
				params.toArray());
	}

	public List<Map<String, Object>> getZones() {

		String sql = """

				SELECT

				    id,
				    zone_name
				FROM gcc_street_vendor.zone_master
				ORDER BY zone_name

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public List<Map<String, Object>> getWards(String zoneId) {

		String sql = """

				SELECT
				    id,
				    ward_name
				FROM gcc_street_vendor.ward_master
				WHERE zone_id = ?
				ORDER BY ward_name

				""";

		return jdbcTemplate.queryForList(
				sql,
				zoneId);
	}

	public List<Map<String, Object>> getHomelessNonSurveyedList(
			String zone,
			String ward,
			String fromDate,
			String toDate) {

		StringBuilder sql = new StringBuilder(

				"SELECT " +

						"DATE(MAX(hr.cdate)) AS survey_date, " +
						"hr.survey_id, " +

						"MAX(CASE " +
						"WHEN hr.qid = 2 THEN hr.answer " +
						"END) AS name, " +

						"MAX(CASE " +
						"WHEN hr.qid = 78 THEN hr.answer " +
						"END) AS age, " +

						"MAX(CASE " +
						"WHEN hr.qid = 8 THEN hr.answer " +
						"END) AS dob, " +

						"MAX(CASE " +
						"WHEN hr.qid = 79 THEN hr.answer " +
						"END) AS remarks, " +

						"IFNULL(MAX(CASE " +
						"WHEN hr.qid = 77 " +
						"THEN CONCAT('" + fileBaseUrl + "/gccofficialapp/files', hr.answer) " +
						"END),'') AS image_url, " +

						"sld.zone AS zone, " +
						"sld.ward AS ward " +

						"FROM homeless_survey_response hr " +

						"LEFT JOIN surveyor_location_details sld " +
						"ON sld.survey_id = hr.survey_id " +

						"WHERE hr.isactive = 1 " +
						"AND hr.isdelete = 0 " +
						"AND EXISTS ( " +
						"SELECT 1 " +
						"FROM homeless_survey_response hr2 " +
						"INNER JOIN homeless_survey_questions_master hqm2 " +
						"ON hqm2.qid = hr2.qid " +
						"WHERE hr2.survey_id = hr.survey_id " +
						"AND hqm2.flag = 'N')");

		List<Object> params = new ArrayList<>();

		if (zone != null && !zone.isBlank()) {

			sql.append(
					" AND CAST(sld.zone AS UNSIGNED) = ? ");

			params.add(Integer.parseInt(zone));
		}

		if (ward != null && !ward.isBlank()) {

			sql.append(
					" AND CAST(sld.ward AS UNSIGNED) = ? ");

			params.add(Integer.parseInt(ward));
		}

		if (fromDate != null
				&& !fromDate.isBlank()
				&& toDate != null
				&& !toDate.isBlank()) {

			sql.append(
					" AND DATE(hr.cdate) BETWEEN ? AND ? ");

			params.add(fromDate);
			params.add(toDate);
		}

		sql.append(

				" GROUP BY " +
						" hr.survey_id, " +
						" sld.zone, " +
						" sld.ward " +

						" ORDER BY MAX(hr.cdate) DESC ");

		return jdbcTemplate.queryForList(
				sql.toString(),
				params.toArray());
	}

	public List<Map<String, Object>> getHomelessSurveyDetails(
			String surveyId) {

		String sql =

				"SELECT " +

						"hqm.cid, " +
						"qcm.english_name AS category_name, " +

						"hqm.qid, " +
						"hqm.q_english AS question, " +
						"hqm.question_type, " +
						"hqm.master_table_name, " +

						"CASE " +

						"WHEN hqm.question_type = 'image' " +
						"THEN CONCAT('" + fileBaseUrl + "/gccofficialapp/files', hr.answer) " +

						"WHEN hr.others_answer IS NOT NULL " +
						"AND TRIM(hr.others_answer) <> '' " +
						"THEN hr.others_answer " +

						"WHEN hqm.question_type = 'radio' " +
						"THEN ham.answer " +

						"ELSE hr.answer " +

						"END AS answer " +

						"FROM homeless_survey_response hr " +

						"INNER JOIN homeless_survey_questions_master hqm " +
						"ON hqm.qid = hr.qid " +

						"LEFT JOIN question_category_master qcm " +
						"ON qcm.id = hqm.cid " +

						"LEFT JOIN homeless_survey_answer_master ham " +
						"ON ham.qid = hr.qid " +
						"AND ham.id = CAST(hr.answer AS UNSIGNED) " +

						"WHERE hr.survey_id = ? " +
						"AND hr.isactive = 1 " +
						"AND hr.isdelete = 0 " +

						"ORDER BY hqm.cid, hqm.orderby";

		List<Map<String, Object>> result = jdbcTemplate.queryForList(
				sql,
				surveyId);

		for (Map<String, Object> row : result) {

			String questionType = row.get("question_type") == null
					? ""
					: row.get("question_type").toString();

			String masterTable = row.get("master_table_name") == null
					? ""
					: row.get("master_table_name").toString();

			/*
			 * String answer = row.get("answer") == null
			 * ? ""
			 * : row.get("answer").toString();
			 */
			String answer = row.get("answer") == null
					? ""
					: row.get("answer").toString().trim();

			try {

				// DROPDOWN
				if ("dropdown".equalsIgnoreCase(questionType)
						&& !masterTable.isBlank()
						&& answer.matches("\\d+")) {

					String valueSql = "SELECT english_name FROM "
							+ masterTable
							+ " WHERE id = ?";

					String value = jdbcTemplate.queryForObject(
							valueSql,
							String.class,
							Integer.parseInt(answer));

					row.put("answer", value);
				}

				// CHECKBOX / MULTICHECK
				else if (("checkbox".equalsIgnoreCase(questionType)
						|| "multicheck".equalsIgnoreCase(questionType))
						&& !masterTable.isBlank()
						&& !answer.isBlank()) {

					String[] ids = answer.split(",");

					StringBuilder placeholders = new StringBuilder();

					for (int i = 0; i < ids.length; i++) {

						placeholders.append("?");

						if (i < ids.length - 1) {
							placeholders.append(",");
						}
					}

					String checkboxSql = "SELECT english_name FROM "
							+ masterTable
							+ " WHERE id IN ("
							+ placeholders
							+ ") ORDER BY id";

					List<String> values = jdbcTemplate.query(
							checkboxSql,
							ids,
							(rs, rowNum) -> rs.getString("english_name"));

					row.put(
							"answer",
							String.join(", ", values));
				}

			} catch (Exception e) {

				System.out.println(
						"Master value fetch failed for qid : "
								+ row.get("qid"));
			}
		}

		return result;
	}

	public List<Map<String, Object>> getSurveyDocuments(String surveyId) {

		String sql =

				"SELECT " +

						"hr.survey_id, " +

						"(" +
						"   SELECT q2.q_english " +
						"   FROM homeless_survey_questions_master q2 " +
						"   WHERE q2.cid = hqm.cid " +
						"   AND q2.question_type = 'radio' " +
						"   AND q2.orderby < hqm.orderby " +
						"   ORDER BY q2.orderby DESC " +
						"   LIMIT 1 " +
						") AS document_name, " +

						"CONCAT('" + fileBaseUrl +
						"/gccofficialapp/files', hr.answer) AS file_url " +

						"FROM homeless_survey_response hr " +

						"INNER JOIN homeless_survey_questions_master hqm " +
						"ON hqm.qid = hr.qid " +

						"WHERE hr.survey_id = ? " +
						"AND hqm.cid = 5 " +
						"AND hqm.question_type = 'image' " +

						"ORDER BY hqm.orderby";

		return jdbcTemplate.queryForList(
				sql,
				surveyId);
	}

	/*
	 * public List<Map<String, Object>> getSurveyDocuments(String surveyId) {
	 * 
	 * String sql = "SELECT " +
	 * "hr.survey_id, " +
	 * "hqm.q_english, " +
	 * "CONCAT('" + fileBaseUrl +
	 * "/gccofficialapp/files', hr.answer) AS file_url " +
	 * 
	 * "FROM homeless_survey_response hr " +
	 * 
	 * "INNER JOIN homeless_survey_questions_master hqm " +
	 * "ON hqm.qid = hr.qid " +
	 * 
	 * "WHERE hr.survey_id = ? " +
	 * "AND hqm.cid = 5 " +
	 * "AND hqm.question_type = 'image'";
	 * 
	 * return jdbcTemplate.queryForList(sql, surveyId);
	 * }
	 */
	public List<Map<String, Object>> getCategories() {

		String sql =

				"SELECT " +
						"id, " +
						"english_name, " +
						"orderby " +

						"FROM question_category_master " +

						"WHERE isactive = 1 " +
						"AND isdelete = 0 " +

						"ORDER BY orderby";

		return jdbcTemplate.queryForList(sql);
	}

	public List<Map<String, Object>> getSurveyorsList(

			String zone,
			String ward,
			String fromDate,
			String toDate) {

		StringBuilder sql = new StringBuilder("""

				    SELECT

				        d.user_name,
				        d.mobile_no,
				        sl.loginId,

				        sl.zone,
				        sl.ward,

				        COUNT(DISTINCT hs.survey_id)
				            AS total_survey_done,

				        DATE(hs.cdate)
				            AS survey_date

				    FROM homeless_survey_response hs

				    INNER JOIN surveyor_location_details sl
				        ON sl.survey_id = hs.survey_id

				    INNER JOIN login_details d
				        ON d.uid = sl.loginId

				    WHERE 1 = 1

				""");

		List<Object> params = new ArrayList<>();

		if (zone != null && !zone.isEmpty()) {

			sql.append("""
					    AND CAST(sl.zone AS UNSIGNED) = ?
					""");

			params.add(zone);
		}

		if (ward != null && !ward.isEmpty()) {

			sql.append("""
					    AND CAST(sl.ward AS UNSIGNED) = ?
					""");

			params.add(ward);
		}

		if (fromDate != null
				&& toDate != null
				&& !fromDate.isEmpty()
				&& !toDate.isEmpty()) {

			sql.append("""
					    AND DATE(hs.cdate)
					    BETWEEN ? AND ?
					""");

			params.add(fromDate);
			params.add(toDate);
		}

		sql.append("""

				    GROUP BY

				        d.user_name,
				        d.mobile_no,
				        sl.loginId,
				        sl.zone,
				        sl.ward,
				        DATE(hs.cdate)

				    ORDER BY DATE(hs.cdate) DESC

				""");

		return jdbcTemplate.queryForList(
				sql.toString(),
				params.toArray());
	}

	public List<Map<String, Object>> getSurveyorWiseSurveyList(
			String loginId,
			String zone,
			String ward,
			String surveyDate) {

		StringBuilder sql = new StringBuilder("""

				    SELECT

				        DATE(MAX(hr.cdate)) AS survey_date,

				        hr.survey_id,

				        MAX(CASE
				                WHEN hr.qid = 2
				                THEN hr.answer
				            END) AS name,
							   MAX(
				    CASE
				        WHEN hr.qid = 7
				        THEN CASE
				                WHEN hr.others_answer IS NOT NULL
				                     AND TRIM(hr.others_answer) <> ''
				                THEN hr.others_answer
				                ELSE gm.english_name
				             END
				    END
				) AS gender,



				        sld.zone AS zone,

				        sld.ward AS ward

				    FROM homeless_survey_response hr

				    LEFT JOIN gender_master gm
				        ON hr.qid = 7
				        AND gm.id = hr.answer

				    LEFT JOIN surveyor_location_details sld
				        ON sld.survey_id = hr.survey_id

				    WHERE hr.isactive = 1
				    AND hr.isdelete = 0
				    AND sld.loginId = ?

				""");

		List<Object> params = new ArrayList<>();
		params.add(loginId);
		/*
		 * MAX(CASE
		 * WHEN hr.qid = 7
		 * THEN gm.english_name
		 * END) AS gender,
		 */

		if (zone != null && !zone.isBlank()) {

			sql.append("""
					    AND sld.zone = ?
					""");

			params.add(zone);
		}

		if (ward != null && !ward.isBlank()) {

			sql.append("""
					    AND sld.ward = ?
					""");

			params.add(ward);
		}

		if (surveyDate != null && !surveyDate.isBlank()) {

			sql.append("""
					    AND DATE(hr.cdate) = ?
					""");

			params.add(surveyDate);
		}

		sql.append("""

				    GROUP BY

				        hr.survey_id,
				        sld.zone,
				        sld.ward

				    ORDER BY

				        MAX(hr.cdate) DESC

				""");

		return jdbcTemplate.queryForList(
				sql.toString(),
				params.toArray());
	}

	public List<Map<String, Object>> getBulkExcelData(String zone,
			String ward,
			String fromDate,
			String toDate) {

		String sql = "SELECT " +
				" hs.survey_id, " +
				" qm.q_english, " +
				" hs.answer " +
				"FROM homeless_survey_response hs " +
				"INNER JOIN homeless_survey_questions_master qm " +
				" ON qm.qid = hs.qid " +
				"INNER JOIN surveyor_location_details sl " +
				" ON sl.survey_id = hs.survey_id " +
				"WHERE hs.isactive = 1 " +
				"AND hs.isdelete = 0 ";

		List<Object> params = new ArrayList<>();

		if (zone != null && !zone.isEmpty()) {
			sql += " AND sl.zone = ? ";
			params.add(zone);
		}

		if (ward != null && !ward.isEmpty()) {
			sql += " AND sl.ward = ? ";
			params.add(ward);
		}

		if (fromDate != null && !fromDate.isEmpty()) {
			sql += " AND DATE(hs.cdate) >= ? ";
			params.add(fromDate);
		}

		if (toDate != null && !toDate.isEmpty()) {
			sql += " AND DATE(hs.cdate) <= ? ";
			params.add(toDate);
		}

		sql += " ORDER BY hs.survey_id,qm.orderby ";

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params.toArray());

		Map<String, Map<String, Object>> surveyMap = new LinkedHashMap<>();

		for (Map<String, Object> row : rows) {

			String surveyId = String.valueOf(row.get("survey_id"));

			String question = String.valueOf(row.get("q_english"));

			String answer = row.get("answer") == null ? "" : String.valueOf(row.get("answer"));

			surveyMap.putIfAbsent(
					surveyId,
					new LinkedHashMap<>());

			surveyMap.get(surveyId)
					.put("Survey ID", surveyId);

			surveyMap.get(surveyId)
					.put(question, answer);
		}

		return new ArrayList<>(surveyMap.values());
	}

	public List<Map<String, Object>> getGenderList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM gender_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveGender(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

					UPDATE gender_master
					SET english_name = ?
					WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

							SELECT IFNULL(MAX(orderby),0) + 1
							FROM gender_master

							""",
					Integer.class);

			String insertSql = """

					INSERT INTO gender_master
					(
					    english_name,
					    orderby,

					    isactive,
					    isdelete
					)
					VALUES
					(
					    ?,
					    ?,

					    1,
					    0
					)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
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

	public List<Map<String, Object>> getRelationTypeList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM relation_type_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveRelationType(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

					UPDATE relation_type_master
					SET english_name = ?
					WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

							SELECT IFNULL(MAX(orderby),0) + 1
							FROM relation_type_master

							""",
					Integer.class);

			String insertSql = """

					INSERT INTO relation_type_master
					(
					    english_name,
					    orderby,

					    isactive,
					    isdelete
					)
					VALUES
					(
					    ?,
					    ?,

					    1,
					    0
					)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateRelationTypeStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """

				UPDATE relation_type_master
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

	public List<Map<String, Object>> getCurrentlyLivingList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM current_living_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveCurrentlyLiving(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

					UPDATE current_living_master
					SET english_name = ?
					WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

							SELECT IFNULL(MAX(orderby),0) + 1
							FROM current_living_master

							""",
					Integer.class);

			String insertSql = """

					INSERT INTO current_living_master
					(
					    english_name,
					    orderby,

					    isactive,
					    isdelete
					)
					VALUES
					(
					    ?,
						    ?,

					    1,
					    0
					)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateCurrentlyLivingStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """

				UPDATE current_living_master
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

	public List<Map<String, Object>> getMartialList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM martial_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveMartial(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

					UPDATE martial_master
					SET english_name = ?
					WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

							SELECT IFNULL(MAX(orderby),0) + 1
							FROM martial_master

							""",
					Integer.class);

			String insertSql = """

					INSERT INTO martial_master
					(
					    english_name,
					    orderby,

					    isactive,
					    isdelete
					)
					VALUES
					(
					    ?,
					    ?,

					    1,
					    0
					)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateMartialStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """

				UPDATE martial_master
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

	public List<Map<String, Object>> getDisabilityTypeList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM disability_type_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveDisabilityType(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

					UPDATE disability_type_master
					SET english_name = ?
					WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

							SELECT IFNULL(MAX(orderby),0) + 1
							FROM disability_type_master

							""",
					Integer.class);

			String insertSql = """

					INSERT INTO disability_type_master
					(
					    english_name,
					    orderby,

					    isactive,
					    isdelete
					)
					VALUES
					(
					    ?,
					    ?,

					    1,
					    0
					)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateDisabilityTypeStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """

				UPDATE disability_type_master
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

	public List<Map<String, Object>> getDisabilityCertificateMasterList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM disable_cert_no_reason_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveDisabilityCertificateMaster(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

					UPDATE disable_cert_no_reason_master
					SET english_name = ?
					WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

								SELECT IFNULL(MAX(orderby),0) + 1
								FROM disable_cert_no_reason_master

							""",
					Integer.class);

			String insertSql = """

					INSERT INTO disable_cert_no_reason_master
					(
					    english_name,
					    orderby,

					    isactive,
					    isdelete
					)
					VALUES
					(
					    ?,
					    ?,

					    1,
					    0
					)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateDisabilityCertificateMasterStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """

				UPDATE disable_cert_no_reason_master
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

	public List<Map<String, Object>> getHealthConditionList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM health_condition_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveHealthCondition(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

					UPDATE health_condition_master
					SET english_name = ?
					WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

								SELECT IFNULL(MAX(orderby),0) + 1
								FROM health_condition_master

							""",
					Integer.class);

			String insertSql = """

					INSERT INTO health_condition_master
					(
					    english_name,
					    orderby,

					    isactive,
					    isdelete
					)
					VALUES
					(
					    ?,
					    ?,

					    1,
					    0
					)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateHealthConditionStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """

				UPDATE health_condition_master
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

	public List<Map<String, Object>> getEducationMasterList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM education_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveEducationMaster(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

					UPDATE education_master
					SET english_name = ?
					WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

								SELECT IFNULL(MAX(orderby),0) + 1
								FROM education_master

							""",
					Integer.class);

			String insertSql = """

					INSERT INTO education_master
					(
					    english_name,
					    orderby,

					    isactive,
					    isdelete
					)
					VALUES
					(
					    ?,
					    ?,

					    1,
					    0
					)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateEducationMasterStatus(
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

	public List<Map<String, Object>> getDiscontinuedList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM discontinued_reason_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveDiscontinuedMaster(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

					UPDATE discontinued_reason_master
					SET english_name = ?
					WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

								SELECT IFNULL(MAX(orderby),0) + 1
								FROM discontinued_reason_master

							""",
					Integer.class);

			String insertSql = """

					INSERT INTO discontinued_reason_master
					(
					    english_name,
					    orderby,

					    isactive,
					    isdelete
					)
					VALUES
					(
					    ?,
					    ?,

					    1,
					    0
					)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateDiscontinuedMasterStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """

				UPDATE discontinued_reason_master
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

	public List<Map<String, Object>> getEmploymentTypeList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM employment_type_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveEmploymentType(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

						UPDATE employment_type_master
						SET english_name = ?
						WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

								SELECT IFNULL(MAX(orderby),0) + 1
								FROM employment_type_master

							""",
					Integer.class);

			String insertSql = """

						INSERT INTO employment_type_master
						(
						    english_name,
						    orderby,

						    isactive,
						    isdelete
						)
						VALUES
						(
						    ?,
						    ?,

						    1,
						    0
						)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateEmploymentTypeStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """

				UPDATE employment_type_master
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

	public List<Map<String, Object>> getOccupationList() {

		String sql = """

					SELECT
					    id,
					    english_name,
					    isactive,
					    isdelete
					FROM occupation_master
					WHERE isactive = 1
					AND isdelete = 0
					ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveOccupation(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

						UPDATE occupation_master
						SET english_name = ?
						WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

								SELECT IFNULL(MAX(orderby),0) + 1
								FROM occupation_master

							""",
					Integer.class);

			String insertSql = """

						INSERT INTO occupation_master
						(
						    english_name,
						    orderby,

						    isactive,
						    isdelete
						)
						VALUES
						(
						    ?,
						    ?,

						    1,
						    0
						)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateOccupationStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """

				UPDATE occupation_master
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

	public List<Map<String, Object>> getNotEmployedList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM not_employed_reason_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveNotEmployed(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

						UPDATE not_employed_reason_master
						SET english_name = ?
						WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

								SELECT IFNULL(MAX(orderby),0) + 1
								FROM not_employed_reason_master

							""",
					Integer.class);

			String insertSql = """

						INSERT INTO not_employed_reason_master
						(
						    english_name,
						    orderby,

						    isactive,
						    isdelete
						)
						VALUES
						(
						    ?,
						    ?,

						    1,
						    0
						)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateNotEmployedStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """

				UPDATE not_employed_reason_master
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

	public List<Map<String, Object>> getReasonHomelessList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM reason_homeless_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveHomelessReason(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

						UPDATE reason_homeless_master
						SET english_name = ?
						WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

								SELECT IFNULL(MAX(orderby),0) + 1
								FROM reason_homeless_master

							""",
					Integer.class);

			String insertSql = """

						INSERT INTO reason_homeless_master
						(
						    english_name,
						    orderby,

						    isactive,
						    isdelete
						)
						VALUES
						(
						    ?,
						    ?,

						    1,
						    0
						)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateHomelessReasonStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """

				UPDATE reason_homeless_master
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

	public List<Map<String, Object>> getSleepingSpaceList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM sleepingspace_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveSleepingSpace(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

						UPDATE sleepingspace_master
						SET english_name = ?
						WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

								SELECT IFNULL(MAX(orderby),0) + 1
								FROM sleepingspace_master

							""",
					Integer.class);

			String insertSql = """

						INSERT INTO sleepingspace_master
						(
						    english_name,
						    orderby,

						    isactive,
						    isdelete
						)
						VALUES
						(
						    ?,
						    ?,

						    1,
						    0
						)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateSleepingSpaceStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """

				UPDATE sleepingspace_master
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

	public List<Map<String, Object>> getMovingReasonList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM reason_moving_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveMovingReason(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

						UPDATE reason_moving_master
						SET english_name = ?
						WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

								SELECT IFNULL(MAX(orderby),0) + 1
								FROM reason_moving_master

							""",
					Integer.class);

			String insertSql = """

						INSERT INTO reason_moving_master
						(
						    english_name,
						    orderby,

						    isactive,
						    isdelete
						)
						VALUES
						(
						    ?,
						    ?,

						    1,
						    0
						)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateMovingReasonStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """

				UPDATE reason_moing_master
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

	public List<Map<String, Object>> getComeOutShelterList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM reason_comeoutshelter_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveComeOutShelter(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

						UPDATE reason_comeoutshelter_master
						SET english_name = ?
						WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

								SELECT IFNULL(MAX(orderby),0) + 1
								FROM reason_comeoutshelter_master

							""",
					Integer.class);

			String insertSql = """

						INSERT INTO reason_comeoutshelter_master
						(
						    english_name,
						    orderby,

						    isactive,
						    isdelete
						)
						VALUES
						(
						    ?,
						    ?,

						    1,
						    0
						)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateComeOutShelterStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """

				UPDATE reason_comeoutshelter_master
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

	public List<Map<String, Object>> getGCCShelterMasterList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM gccshelter_notstayed_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveGCCShelterMaster(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

						UPDATE gccshelter_notstayed_master
						SET english_name = ?
						WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

								SELECT IFNULL(MAX(orderby),0) + 1
								FROM gccshelter_notstayed_master
							""",
					Integer.class);

			String insertSql = """

						INSERT INTO gccshelter_notstayed_master
						(
						    english_name,
						    orderby,

						    isactive,
						    isdelete
						)
						VALUES
						(
						    ?,
						    ?,

						    1,
						    0
						)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateGCCShelterMasterStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """

				UPDATE gccshelter_notstayed_master
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

	public List<Map<String, Object>> getBirthCertificateNoReasonMasterList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM birth_cert_no_reason_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveBirthCertificateNoReasonMaster(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

						UPDATE birth_cert_no_reason_master
						SET english_name = ?
						WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

								SELECT IFNULL(MAX(orderby),0) + 1
								FROM birth_cert_no_reason_master
							""",
					Integer.class);

			String insertSql = """

						INSERT INTO birth_cert_no_reason_master
						(
						    english_name,
						    orderby,

						    isactive,
						    isdelete
						)
						VALUES
						(
						    ?,
						    ?,

						    1,
						    0
						)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateBirthCertificateNoReasonMasterStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """

				UPDATE birth_cert_no_reason_master
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

	public List<Map<String, Object>> getCommunityMasterList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM commu_cert_no_reason_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveCommunityMaster(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

						UPDATE commu_cert_no_reason_master
						SET english_name = ?
						WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

								SELECT IFNULL(MAX(orderby),0) + 1
								FROM commu_cert_no_reason_master
							""",
					Integer.class);

			String insertSql = """

						INSERT INTO commu_cert_no_reason_master
						(
						    english_name,
						    orderby,

						    isactive,
						    isdelete
						)
						VALUES
						(
						    ?,
						    ?,

						    1,
						    0
						)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateCommunityMasterStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """

				UPDATE commu_cert_no_reason_master
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

	public List<Map<String, Object>> getVoterIdMasterList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM voter_id_no_reason_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveVoterIdMaster(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

						UPDATE voter_id_no_reason_master
						SET english_name = ?
						WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

								SELECT IFNULL(MAX(orderby),0) + 1
								FROM voter_id_no_reason_master
							""",
					Integer.class);

			String insertSql = """

						INSERT INTO voter_id_no_reason_master
						(
						    english_name,
						    orderby,

						    isactive,
						    isdelete
						)
						VALUES
						(
						    ?,
						    ?,

						    1,
						    0
						)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateVoterIdMasterStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """

				UPDATE voter_id_no_reason_master
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

	public List<Map<String, Object>> getAadharCertificateNoReasonMasterList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM adhar_card_no_reason_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveAadharCertificateNoReasonMaster(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

						UPDATE adhar_card_no_reason_master
						SET english_name = ?
						WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

								SELECT IFNULL(MAX(orderby),0) + 1
								FROM adhar_card_no_reason_master
							""",
					Integer.class);

			String insertSql = """

						INSERT INTO adhar_card_no_reason_master
						(
						    english_name,
						    orderby,

						    isactive,
						    isdelete
						)
						VALUES
						(
						    ?,
						    ?,

						    1,
						    0
						)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateAadharCertificateNoReasonMasterStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """

				UPDATE adhar_card_no_reason_master
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

	public List<Map<String, Object>> getSmartCardNoReasonMasterList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM smart_card_no_reason_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveSmartCardNoReasonMaster(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

						UPDATE smart_card_no_reason_master
						SET english_name = ?
						WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

								SELECT IFNULL(MAX(orderby),0) + 1
								FROM smart_card_no_reason_master
							""",
					Integer.class);

			String insertSql = """

						INSERT INTO smart_card_no_reason_master
						(
						    english_name,
						    orderby,

						    isactive,
						    isdelete
						)
						VALUES
						(
						    ?,
						    ?,

						    1,
						    0
						)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateSmartCardNoReasonMasterStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """

				UPDATE smart_card_no_reason_master
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

	public List<Map<String, Object>> getDriverLicenseNoReasonMasterList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM dri_license_no_reason_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveDriverLicenseNoReasonMaster(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

						UPDATE dri_license_no_reason_master
						SET english_name = ?
						WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

								SELECT IFNULL(MAX(orderby),0) + 1
								FROM dri_license_no_reason_master
							""",
					Integer.class);

			String insertSql = """

						INSERT INTO dri_license_no_reason_master
						(
						    english_name,
						    orderby,

						    isactive,
						    isdelete
						)
						VALUES
						(
						    ?,
						    ?,

						    1,
						    0
						)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateDriverLicenseNoReasonMasterStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """

				UPDATE dri_license_no_reason_master
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

	public List<Map<String, Object>> getCmInsuranceNoReasonMasterList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM cm_insurance_no_reason_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveCmInsuranceNoReasonMaster(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

						UPDATE cm_insurance_no_reason_master
						SET english_name = ?
						WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

								SELECT IFNULL(MAX(orderby),0) + 1
								FROM cm_insurance_no_reason_master
							""",
					Integer.class);

			String insertSql = """

						INSERT INTO cm_insurance_no_reason_master
						(
						    english_name,
						    orderby,

						    isactive,
						    isdelete
						)
						VALUES
						(
						    ?,
						    ?,

						    1,
						    0
						)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateCmInsuranceNoReasonMasterStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """

				UPDATE cm_insurance_no_reason_master
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

	public List<Map<String, Object>> getBankPassbookNoReasonMasterList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM bank_passbook_no_reason_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveBankPassbookNoReasonMaster(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

						UPDATE bank_passbook_no_reason_master
						SET english_name = ?
						WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

								SELECT IFNULL(MAX(orderby),0) + 1
								FROM bank_passbook_no_reason_master
							""",
					Integer.class);

			String insertSql = """

						INSERT INTO bank_passbook_no_reason_master
						(
						    english_name,
						    orderby,

						    isactive,
						    isdelete
						)
						VALUES
						(
						    ?,
						    ?,

						    1,
						    0
						)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateBankPassbookNoReasonMasterStatus(
			Map<String, Object> req) {

		Integer id = Integer.parseInt(
				req.get("id").toString());

		Integer status = Integer.parseInt(
				req.get("status").toString());

		String sql = """

				UPDATE bank_passbook_no_reason_master
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

	public List<Map<String, Object>> getReligionMasterList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM religion_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveReligionMaster(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

						UPDATE religion_master
						SET english_name = ?
						WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

								SELECT IFNULL(MAX(orderby),0) + 1
								FROM religion_master
							""",
					Integer.class);

			String insertSql = """

						INSERT INTO religion_master
						(
						    english_name,
						    orderby,

						    isactive,
						    isdelete
						)
						VALUES
						(
						    ?,
						    ?,

						    1,
						    0
						)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateReligionMasterStatus(
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

	public List<Map<String, Object>> getCasteMasterList() {

		String sql = """

				SELECT
				    id,
				    english_name,
				    isactive,
				    isdelete
				FROM caste_master
				WHERE isactive = 1
				AND isdelete = 0
				ORDER BY orderby

				""";

		return jdbcTemplate.queryForList(sql);
	}

	public void saveCasteMaster(Map<String, Object> req) {

		Integer id = req.get("id") != null
				? Integer.parseInt(req.get("id").toString())
				: 0;

		// UPDATE
		if (id > 0) {

			String updateSql = """

						UPDATE caste_master
						SET english_name = ?
						WHERE id = ?

					""";

			jdbcTemplate.update(
					updateSql,
					req.get("english_name"),
					id);
		}

		// INSERT
		else {

			Integer nextOrder = jdbcTemplate.queryForObject(
					"""

								SELECT IFNULL(MAX(orderby),0) + 1
								FROM caste_master
							""",
					Integer.class);

			String insertSql = """

						INSERT INTO caste_master
						(
						    english_name,
						    orderby,

						    isactive,
						    isdelete
						)
						VALUES
						(
						    ?,
						    ?,

						    1,
						    0
						)

					""";

			jdbcTemplate.update(
					insertSql,
					req.get("english_name"),
					nextOrder);
		}
	}

	public void updateCasteMasterStatus(
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

	public List<Map<String, Object>> getZoneWiseCount() {

		String sql =

				"SELECT " +

						"zm.zone_name AS zone, " +

						"COUNT(DISTINCT sld.survey_id) AS total " +

						"FROM gcc_street_vendor.zone_master zm " +

						"LEFT JOIN surveyor_location_details sld " +
						"ON sld.zone = zm.zone_name " +

						"GROUP BY zm.zone_name " +

						"ORDER BY CAST(zm.zone_name AS UNSIGNED)";

		return jdbcTemplate.queryForList(sql);
	}

	public List<Map<String, Object>> getWardWiseCount(
			String zone) {

		String sql =

				"SELECT " +

						"wm.ward_name AS ward, " +

						"COUNT(DISTINCT sld.survey_id) AS total " +

						"FROM gcc_street_vendor.ward_master wm " +

						"LEFT JOIN surveyor_location_details sld " +
						"ON sld.zone = wm.zone_id " +
						"AND sld.ward = wm.ward_name " +

						"WHERE wm.zone_id = ? " +

						"GROUP BY wm.ward_name " +

						"ORDER BY CAST(wm.ward_name AS UNSIGNED)";

		return jdbcTemplate.queryForList(
				sql,
				zone);
	}

	public Map<String, Object> getDashboard() {

		Map<String, Object> response = new HashMap<>();

		response.put(
				"totalHomeless",
				getTotalHomeless());

		response.put(
				"zonesCovered",
				getZonesCovered());

		response.put(
				"maleCount",
				getMaleCount());

		response.put(
				"femaleCount",
				getFemaleCount());

		response.put(
				"othersCount",
				getOthersCount());

		response.put(
				"zoneWise",
				getZoneWiseCount());

		response.put(
				"casteWise",
				getCasteWiseCount());
		response.put(
				"employedCount",
				getEmployedCount());

		response.put(
				"unEmployedCount",
				getUnEmployedCount());

		response.put(
				"shelterStayedYes",
				getShelterStayedYesCount());

		response.put(
				"shelterStayedNo",
				getShelterStayedNoCount());
		response.put(
				"youngAdults",
				getYoungAdultsCount());

		response.put(
				"middleAgedAdults",
				getMiddleAgedCount());

		response.put(
				"olderAdults",
				getOlderAdultsCount());

		return response;
	}

	private int getShelterStayedNoCount() {

		String sql =

				"SELECT COUNT(DISTINCT survey_id) " +

						"FROM homeless_survey_response " +

						"WHERE qid = 44 " +

						"AND answer = '17' " +

						"AND isactive = 1 " +

						"AND isdelete = 0";

		return jdbcTemplate.queryForObject(
				sql,
				Integer.class);
	}

	private int getShelterStayedYesCount() {

		String sql =

				"SELECT COUNT(DISTINCT survey_id) " +

						"FROM homeless_survey_response " +

						"WHERE qid = 44 " +

						"AND answer = '16' " +

						"AND isactive = 1 " +

						"AND isdelete = 0";

		return jdbcTemplate.queryForObject(
				sql,
				Integer.class);
	}

	private int getUnEmployedCount() {

		String sql =

				"SELECT COUNT(DISTINCT survey_id) " +

						"FROM homeless_survey_response " +

						"WHERE qid = 27 " +

						"AND answer = '13' " +

						"AND isactive = 1 " +

						"AND isdelete = 0";

		return jdbcTemplate.queryForObject(
				sql,
				Integer.class);
	}

	private int getEmployedCount() {

		String sql =

				"SELECT COUNT(DISTINCT survey_id) " +

						"FROM homeless_survey_response " +

						"WHERE qid = 27 " +

						"AND answer = '12' " +

						"AND isactive = 1 " +

						"AND isdelete = 0";

		return jdbcTemplate.queryForObject(
				sql,
				Integer.class);
	}

	private int getOlderAdultsCount() {

		String sql =

				"SELECT COUNT(DISTINCT survey_id) " +

						"FROM homeless_survey_response " +

						"WHERE qid = 78 " +

						"AND CAST(answer AS UNSIGNED) >= 55 " +

						"AND isactive = 1 " +

						"AND isdelete = 0";

		return jdbcTemplate.queryForObject(
				sql,
				Integer.class);
	}

	private int getYoungAdultsCount() {

		String sql =

				"SELECT COUNT(DISTINCT survey_id) " +

						"FROM homeless_survey_response " +

						"WHERE qid = 78 " +

						"AND CAST(answer AS UNSIGNED) BETWEEN 18 AND 34 " +

						"AND isactive = 1 " +

						"AND isdelete = 0";

		return jdbcTemplate.queryForObject(
				sql,
				Integer.class);
	}

	public Integer getDocumentCount(String surveyId) {

		String sql = """

				SELECT COUNT(*)

				FROM homeless_survey_response

				WHERE survey_id = ?

				AND qid IN (50,53,56,59,62,65,68,71)

				AND answer IS NOT NULL

				AND TRIM(answer) <> ''

				AND isactive = 1

				AND isdelete = 0

				""";

		return jdbcTemplate.queryForObject(
				sql,
				Integer.class,
				surveyId);
	}

	private int getMiddleAgedCount() {

		String sql =

				"SELECT COUNT(DISTINCT survey_id) " +

						"FROM homeless_survey_response " +

						"WHERE qid = 78 " +

						"AND CAST(answer AS UNSIGNED) BETWEEN 35 AND 54 " +

						"AND isactive = 1 " +

						"AND isdelete = 0";

		return jdbcTemplate.queryForObject(
				sql,
				Integer.class);
	}

	private int getMiddleAgedCount1() {

		String sql =

				"SELECT COUNT(DISTINCT survey_id) " +

						"FROM homeless_survey_response " +

						"WHERE qid = 78 " +

						"AND CAST(answer AS UNSIGNED) BETWEEN 35 AND 54 " +

						"AND isactive = 1 " +

						"AND isdelete = 0";

		return jdbcTemplate.queryForObject(
				sql,
				Integer.class);
	}

	private int getTotalHomeless() {

		String sql =

				"SELECT COUNT(DISTINCT survey_id) " +
						"FROM homeless_survey_response " +
						"WHERE isactive = 1 " +
						"AND isdelete = 0";

		return jdbcTemplate.queryForObject(
				sql,
				Integer.class);
	}

	private int getZonesCovered() {

		String sql =

				"SELECT COUNT(DISTINCT zone) " +
						"FROM surveyor_location_details";

		return jdbcTemplate.queryForObject(
				sql,
				Integer.class);
	}

	public List<Map<String, Object>> getCasteWiseCount() {

		String sql =

				"SELECT " +

						"cm.id, " +

						"cm.english_name AS caste, " +

						"COALESCE(COUNT(DISTINCT hr.survey_id), 0) AS total " +

						"FROM caste_master cm " +

						"LEFT JOIN homeless_survey_response hr " +

						"ON hr.qid = 13 " +

						"AND CAST(hr.answer AS UNSIGNED) = cm.id " +

						"AND hr.isactive = 1 " +

						"AND hr.isdelete = 0 " +

						"WHERE cm.isactive = 1 " +

						"AND cm.isdelete = 0 " +

						"GROUP BY cm.id, cm.english_name " +

						"ORDER BY cm.orderby";

		return jdbcTemplate.queryForList(sql);
	}

	private int getMaleCount() {

		String sql =

				"SELECT COUNT(DISTINCT survey_id) " +
						"FROM homeless_survey_response " +
						"WHERE qid = 7 " +
						"AND answer = '1'";

		return jdbcTemplate.queryForObject(
				sql,
				Integer.class);
	}

	private int getFemaleCount() {

		String sql =

				"SELECT COUNT(DISTINCT survey_id) " +
						"FROM homeless_survey_response " +
						"WHERE qid = 7 " +
						"AND answer = '2'";

		return jdbcTemplate.queryForObject(
				sql,
				Integer.class);
	}

	private int getOthersCount() {

		String sql =

				"SELECT COUNT(DISTINCT survey_id) " +
						"FROM homeless_survey_response " +
						"WHERE qid = 7 " +
						"AND answer = '3'";

		Integer count = jdbcTemplate.queryForObject(
				sql,
				Integer.class);

		return count == null ? 0 : count;
	}

}
