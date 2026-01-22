package in.gov.chennaicorporation.gccoffice.flagpole.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class RdoService {

    private JdbcTemplate mysqlAppJdbcTemplate;

    @Autowired
    private WhatsappServiceFlagPole whatsappService;

    @Autowired
    public void setMysqlAppJdbcTemplate(
            @Qualifier("mysqlAppDataSource") DataSource dataSource) {
        this.mysqlAppJdbcTemplate = new JdbcTemplate(dataSource);
    }

    private JdbcTemplate mysqlFlagPoleJdbcTemplate;

    @Autowired
    public void setMysqlFlagPoleJdbcTemplate(
            @Qualifier("mysqlFlagPoleManagerSystemDataSource") DataSource dataSource) {
        this.mysqlFlagPoleJdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Long getAppUserIdByUsername(String username) {

        String sql = """
                    SELECT id
                    FROM appusers
                    WHERE username = ?
                """;

        return mysqlAppJdbcTemplate.queryForObject(sql, Long.class, username);
    }

    public String getDepartmentNameByUsername(String username) {

        String sql = """
                    SELECT department_name
                    FROM department_login
                    WHERE user_name = ?
                      AND is_active = 1
                      AND is_delete = 0
                """;

        List<String> list = mysqlFlagPoleJdbcTemplate.queryForList(sql, String.class, username);
        return list.isEmpty() ? null : list.get(0);
    }

    public Integer getDepartmentIdByUsername(String username) {

        String sql = """
                    SELECT dpid
                    FROM department_login
                    WHERE user_name = ?
                      AND is_active = 1
                      AND is_delete = 0
                """;

        List<Integer> list = mysqlFlagPoleJdbcTemplate.queryForList(sql, Integer.class, username);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Map<String, Object>> getOfficerFeedbackHistoryByRefId(
            String refid,
            String username) {

        Integer deptId = getDepartmentIdByUsername(username);
        String departmentName = getDepartmentNameByUsername(username);
        boolean isRdo = isRdoDepartment(departmentName);

        StringBuilder sql = new StringBuilder("""
                    SELECT
                        ofb.refid,
                        dl.department_name,
                        ofb.status,
                        ofb.remarks,
                        ofb.cby,
                        DATE_FORMAT(ofb.cdate, '%d-%m-%Y %h:%i %p') AS action_date
                    FROM officer_feedback ofb
                    JOIN department_login dl
                      ON ofb.dept_id = dl.dpid
                    WHERE ofb.refid = ?
                      AND ofb.is_active = 1
                      AND ofb.is_delete = 0
                """);

        List<Object> params = new ArrayList<>();
        params.add(refid);

        // 🔥 NON-RDO → show ONLY own department history
        if (!isRdo) {
            sql.append(" AND ofb.dept_id = ? ");
            params.add(deptId);
        }

        sql.append(" ORDER BY ofb.cdate ASC ");

        return mysqlFlagPoleJdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    public List<Map<String, Object>> getRdoFeedbackHistoryByRefId(String refid) {

        String sql = """
                    SELECT
                        ofb.refid,
                        dl.department_name,
                        ofb.status,
                        ofb.remarks,
                        ofb.cby,
                        DATE_FORMAT(ofb.cdate, '%d-%m-%Y %h:%i %p') AS action_date
                    FROM officer_feedback ofb
                    JOIN department_login dl
                      ON ofb.dept_id = dl.dpid
                    WHERE ofb.refid = ?
                      AND dl.department_name IN ('RDO_CENTRAL','RDO_NORTH','RDO_SOUTH')
                      AND ofb.is_active = 1
                      AND ofb.is_delete = 0
                    ORDER BY ofb.cdate ASC
                """;

        return mysqlFlagPoleJdbcTemplate.queryForList(sql, refid);
    }

    public int insertOfficerFeedback(
            String refId,
            Integer deptId,
            String status,
            String remarks,
            String createdBy) {

        String sql = """
                    INSERT INTO officer_feedback
                    (
                        refid,
                        dept_id,
                        status,
                        remarks,
                        cby,
                        cdate,
                        is_active,
                        is_delete
                    )
                    VALUES (?, ?, ?, ?, ?, NOW(), 1, 0)
                """;

        return mysqlFlagPoleJdbcTemplate.update(
                sql,
                refId,
                deptId,
                status,
                remarks,
                createdBy);
    }

    //
    // public List<Map<String, Object>> getUserAccess(String userLogin) {

    // String sql = """
    // SELECT lmu.zonearray, lmu.wardarray
    // FROM login_mapping_user lmu
    // JOIN appusers au ON au.id = lmu.appuser_id
    // WHERE au.username = ?
    // """;

    // return mysqlAppJdbcTemplate.queryForList(sql, userLogin);
    // }

    public List<Map<String, Object>> getUserAccess(Long appUserId) {

        String sql = """
                    SELECT lmu.zonearray, lmu.wardarray
                    FROM login_mapping_user lmu
                    WHERE lmu.appuser_id = ?
                """;

        return mysqlAppJdbcTemplate.queryForList(sql, appUserId);
    }

    // public List<Map<String, Object>> getUserAccess(String username) {

    // String sql = """
    // SELECT lmu.zonearray, lmu.wardarray
    // FROM login_mapping_user lmu
    // WHERE lmu.appuser_id = (
    // SELECT au.id
    // FROM appusers au
    // WHERE au.username = ?
    // )
    // """;

    // return mysqlAppJdbcTemplate.queryForList(sql, username);
    // }

    // public List<Map<String, Object>> getAllRequestDetailsByUserLogin(String
    // userLogin) {

    // List<Map<String, Object>> rows = mysqlFlagPoleJdbcTemplate.queryForList(
    // """
    // SELECT
    // urd.id,
    // urd.applicant_name,
    // urd.mobile_number,
    // urd.applicant_address,
    // urd.event_desc,
    // urd.event_date,
    // urd.ae_status,
    // urd.rdo_status,
    // urd.refid,
    // sd.street_name,
    // sd.zone,
    // sd.ward,
    // sd.no_of_poles,
    // sd.height,
    // sd.street_cost,
    // urd.total_cost,
    // et.event_name
    // FROM user_request_details urd
    // LEFT JOIN street_details sd
    // ON urd.refid = sd.refid
    // AND sd.is_active = 1
    // AND sd.is_delete = 0
    // LEFT JOIN event_type et
    // ON urd.event_id = et.id
    // WHERE urd.ae_status = 'APPROVED'
    // AND (urd.rdo_status IS NULL OR urd.rdo_status = '' OR urd.rdo_status =
    // 'NULL')
    // AND FIND_IN_SET(sd.zone, ?)
    // AND FIND_IN_SET(sd.ward, ?)
    // ORDER BY urd.refid, sd.id
    // """,
    // (String) getUserAccess(userLogin).get(0).get("zonearray"),
    // (String) getUserAccess(userLogin).get(0).get("wardarray"));

    // // 🔥 GROUP BY REFID
    // Map<String, Map<String, Object>> resultMap = new LinkedHashMap<>();

    // for (Map<String, Object> row : rows) {

    // String refid = (String) row.get("refid");

    // // Create parent object once per refid
    // Map<String, Object> request = resultMap.computeIfAbsent(refid, k -> {
    // Map<String, Object> map = new LinkedHashMap<>();
    // map.put("refid", row.get("refid"));
    // map.put("applicant_name", row.get("applicant_name"));
    // map.put("mobile_number", row.get("mobile_number"));
    // map.put("applicant_address", row.get("applicant_address"));
    // map.put("event_date", row.get("event_date"));
    // map.put("ae_status", row.get("ae_status"));
    // map.put("rdo_status", row.get("rdo_status"));
    // map.put("event_name", row.get("event_name"));
    // map.put("total_cost", row.get("total_cost"));
    // map.put("streets", new ArrayList<Map<String, Object>>());
    // return map;
    // });

    // // Add street details
    // Map<String, Object> street = new LinkedHashMap<>();
    // street.put("street_name", row.get("street_name"));
    // street.put("zone", row.get("zone"));
    // street.put("ward", row.get("ward"));
    // street.put("no_of_poles", row.get("no_of_poles"));
    // street.put("street_cost", row.get("street_cost"));

    // ((List<Map<String, Object>>) request.get("streets")).add(street);
    // }

    // return new ArrayList<>(resultMap.values());
    // }

    // for rdo login list
    // public List<Map<String, Object>> getAllRequestDetailsByUserLogin(
    // String username,
    // String startDate,
    // String endDate) {

    // // List<Map<String, Object>> access = getUserAccess(userLogin);

    // // 🔹 1. Get appuser_id from username
    // Long appUserId = getAppUserIdByUsername(username);

    // if (appUserId == null) {
    // throw new RuntimeException("Invalid session user: " + username);
    // }

    // // 🔹 2. Get zone & ward using appuser_id
    // List<Map<String, Object>> access = getUserAccess(appUserId);

    // if (access.isEmpty()) {
    // throw new RuntimeException("No zone/ward mapping for userId: " + appUserId);
    // }

    // String zoneArray = (String) access.get(0).get("zonearray");
    // String wardArray = (String) access.get(0).get("wardarray");

    // StringBuilder sql = new StringBuilder("""
    // SELECT
    // urd.id,
    // urd.applicant_name,
    // urd.mobile_number,
    // urd.applicant_address,
    // urd.event_desc,
    // urd.cdate,
    // urd.event_date,
    // urd.ae_status,
    // urd.rdo_status,
    // urd.refid,
    // urd.no_of_days,

    // sd.street_name,
    // sd.zone,
    // sd.ward,
    // sd.no_of_poles,
    // sd.height,
    // sd.street_cost,

    // fm.flag_material_name AS flag_material,
    // pm.pole_material_name AS pole_material,

    // urd.total_cost,
    // et.event_name

    // FROM user_request_details urd

    // LEFT JOIN street_details sd
    // ON urd.refid = sd.refid
    // AND sd.is_active = 1
    // AND sd.is_delete = 0

    // LEFT JOIN event_type et
    // ON urd.event_id = et.id

    // LEFT JOIN flag_material fm
    // ON sd.flag_material_id = fm.id

    // LEFT JOIN pole_material pm
    // ON sd.pole_material_id = pm.id

    // WHERE urd.ae_status = 'APPROVED'
    // AND (urd.rdo_status IS NULL OR urd.rdo_status = '' OR urd.rdo_status =
    // 'NULL')
    // AND FIND_IN_SET(sd.zone, ?)
    // AND FIND_IN_SET(sd.ward, ?)
    // """);

    // List<Object> params = new ArrayList<>();
    // params.add(zoneArray);
    // params.add(wardArray);

    // // 🔹 DATE FILTER
    // if (startDate != null && !startDate.isEmpty()
    // && endDate != null && !endDate.isEmpty()) {

    // sql.append(" AND DATE(urd.event_date) BETWEEN ? AND ? ");
    // params.add(startDate);
    // params.add(endDate);

    // } else if (startDate != null && !startDate.isEmpty()) {

    // sql.append(" AND DATE(urd.event_date) = ? ");
    // params.add(startDate);
    // }

    // // sql.append(" ORDER BY urd.refid, sd.id ,cdate ASC");
    // sql.append("ORDER BY " + //
    // " urd.event_date ASC, " + //
    // " urd.cdate ASC, " + //
    // " urd.refid ASC, " + //
    // " sd.id ASC");

    // List<Map<String, Object>> rows = mysqlFlagPoleJdbcTemplate.queryForList(
    // sql.toString(), params.toArray());

    // // 🔥 GROUP BY REFID
    // Map<String, Map<String, Object>> resultMap = new LinkedHashMap<>();

    // for (Map<String, Object> row : rows) {

    // String refid = (String) row.get("refid");

    // Map<String, Object> request = resultMap.computeIfAbsent(refid, k -> {
    // Map<String, Object> map = new LinkedHashMap<>();
    // map.put("refid", row.get("refid"));
    // map.put("applicant_name", row.get("applicant_name"));
    // map.put("mobile_number", row.get("mobile_number"));
    // map.put("applicant_address", row.get("applicant_address"));
    // map.put("event_desc", row.get("event_desc"));
    // map.put("event_date", row.get("event_date"));
    // map.put("cdate", row.get("cdate"));
    // map.put("ae_status", row.get("ae_status"));
    // map.put("rdo_status", row.get("rdo_status"));
    // map.put("no_of_days", row.get("no_of_days"));
    // map.put("event_name", row.get("event_name"));
    // map.put("total_cost", row.get("total_cost"));
    // map.put("streets", new ArrayList<Map<String, Object>>());
    // return map;
    // });

    // Map<String, Object> street = new LinkedHashMap<>();
    // street.put("street_name", row.get("street_name"));
    // street.put("zone", row.get("zone"));
    // street.put("ward", row.get("ward"));
    // street.put("no_of_poles", row.get("no_of_poles"));
    // street.put("height", row.get("height"));
    // street.put("street_cost", row.get("street_cost"));
    // street.put("flag_material", row.get("flag_material"));
    // street.put("pole_material", row.get("pole_material"));

    // ((List<Map<String, Object>>) request.get("streets")).add(street);
    // }

    // return new ArrayList<>(resultMap.values());
    // }

    public List<Map<String, Object>> getAllRequestDetailsByUserLogin(
            String username,
            String startDate,
            String endDate) {

        // 🔹 1. App user
        Long appUserId = getAppUserIdByUsername(username);
        if (appUserId == null) {
            throw new RuntimeException("Invalid session user: " + username);
        }

        // 🔹 2. Department
        String departmentName = getDepartmentNameByUsername(username);
        boolean isRdo = isRdoDepartment(departmentName);
        Integer deptId = getDepartmentIdByUsername(username);

        String zoneArray = null;
        String wardArray = null;

        // 🔹 3. Zone/Ward ONLY for RDO
        if (isRdo) {
            List<Map<String, Object>> access = getUserAccess(appUserId);
            if (access.isEmpty()) {
                throw new RuntimeException("No zone/ward mapping for RDO userId: " + appUserId);
            }
            zoneArray = (String) access.get(0).get("zonearray");
            wardArray = (String) access.get(0).get("wardarray");
        }

        // 🔹 4. Base query
        StringBuilder sql = new StringBuilder("""
                    SELECT
                        urd.id,
                        urd.applicant_name,
                        urd.mobile_number,
                        urd.applicant_address,
                        urd.event_desc,
                        urd.cdate,
                        urd.event_date,
                        urd.ae_status,
                        urd.rdo_status,
                        urd.refid,
                        urd.no_of_days,

                        sd.street_name,
                        sd.zone,
                        sd.ward,
                        sd.no_of_poles,
                        sd.height,
                        sd.street_cost,

                        fm.flag_material_name AS flag_material,
                        pm.pole_material_name AS pole_material,

                        urd.total_cost,
                        et.event_name

                    FROM user_request_details urd
                    LEFT JOIN street_details sd
                           ON urd.refid = sd.refid
                          AND sd.is_active = 1
                          AND sd.is_delete = 0
                    LEFT JOIN event_type et ON urd.event_id = et.id
                    LEFT JOIN flag_material fm ON sd.flag_material_id = fm.id
                    LEFT JOIN pole_material pm ON sd.pole_material_id = pm.id
                    WHERE 1 = 1
                """);

        List<Object> params = new ArrayList<>();

        // 🔥 1. REMOVE IF RDO HAS DECIDED (FINAL AUTHORITY)
        sql.append("""
                    AND (urd.rdo_status IS NULL OR urd.rdo_status = '')
                """);

        // 🔥 2. REMOVE IF LOGGED-IN DEPARTMENT HAS DECIDED
        sql.append("""
                    AND urd.refid NOT IN (
                        SELECT ofb.refid
                        FROM officer_feedback ofb
                        WHERE ofb.dept_id = ?
                          AND ofb.status IN ('APPROVED','REJECTED')
                          AND ofb.is_active = 1
                          AND ofb.is_delete = 0
                    )
                """);
        params.add(deptId);

        // 🔹 5. Apply zone/ward filter ONLY for RDO
        if (isRdo) {
            sql.append(" AND FIND_IN_SET(sd.zone, ?) ");
            sql.append(" AND FIND_IN_SET(sd.ward, ?) ");
            params.add(zoneArray);
            params.add(wardArray);
        }

        // 🔹 6. Date filter
        if (startDate != null && !startDate.isEmpty()
                && endDate != null && !endDate.isEmpty()) {

            sql.append(" AND DATE(urd.event_date) BETWEEN ? AND ? ");
            params.add(startDate);
            params.add(endDate);

        } else if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND DATE(urd.event_date) = ? ");
            params.add(startDate);
        }

        sql.append("""
                    ORDER BY urd.event_date ASC,
                             urd.cdate ASC,
                             urd.refid ASC,
                             sd.id ASC
                """);

        List<Map<String, Object>> rows = mysqlFlagPoleJdbcTemplate.queryForList(sql.toString(), params.toArray());

        // 🔥 GROUP BY REFID
        Map<String, Map<String, Object>> resultMap = new LinkedHashMap<>();

        for (Map<String, Object> row : rows) {

            String refid = (String) row.get("refid");

            Map<String, Object> request = resultMap.computeIfAbsent(refid, k -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("refid", row.get("refid"));
                map.put("applicant_name", row.get("applicant_name"));
                map.put("mobile_number", row.get("mobile_number"));
                map.put("applicant_address", row.get("applicant_address"));
                map.put("event_desc", row.get("event_desc"));
                map.put("event_date", row.get("event_date"));
                map.put("cdate", row.get("cdate"));
                map.put("ae_status", row.get("ae_status"));
                map.put("rdo_status", row.get("rdo_status"));
                map.put("no_of_days", row.get("no_of_days"));
                map.put("event_name", row.get("event_name"));
                map.put("total_cost", row.get("total_cost"));
                map.put("streets", new ArrayList<>());
                return map;
            });

            Map<String, Object> street = new LinkedHashMap<>();
            street.put("street_name", row.get("street_name"));
            street.put("zone", row.get("zone"));
            street.put("ward", row.get("ward"));
            street.put("no_of_poles", row.get("no_of_poles"));
            street.put("height", row.get("height"));
            street.put("street_cost", row.get("street_cost"));
            street.put("flag_material", row.get("flag_material"));
            street.put("pole_material", row.get("pole_material"));

            ((List<Map<String, Object>>) request.get("streets")).add(street);
        }

        return new ArrayList<>(resultMap.values());
    }

    public List<Map<String, Object>> getRequestHistoryByRefId(String refid) {

        String sql = """
                    SELECT
                        DATE_FORMAT(cdate, '%d-%m-%Y %h:%i %p') AS action_date,
                        event_date,
                        ae_status,
                        rdo_status,
                        rdo_remarks,
                        booking_status
                    FROM user_request_details_history
                    WHERE refid = ?
                    ORDER BY cdate ASC
                """;

        return mysqlFlagPoleJdbcTemplate.queryForList(sql, refid);
    }

    private boolean isRdoDepartment(String deptName) {
        return deptName != null &&
                (deptName.equalsIgnoreCase("rdo_north")
                        || deptName.equalsIgnoreCase("rdo_central")
                        || deptName.equalsIgnoreCase("rdo_south"));
    }

    // old
    // @Transactional
    // public int updateRdoDecision(
    // String refId,
    // String status, // APPROVED / REJECTED
    // String remarks,
    // String approvedBy) {

    // // 1️⃣ INSERT HISTORY WITH NEW DECISION
    // String historySql = """
    // INSERT INTO user_request_details_history
    // (
    // refid,
    // applicant_name,
    // mobile_number,
    // applicant_address,
    // event_id,
    // event_date,
    // no_of_days,
    // total_poles,
    // total_cost,
    // ae_status,
    // rdo_status,
    // rdo_remarks,
    // approved_by,
    // approved_date,
    // payment_status,
    // refund_status,
    // booking_status,
    // cdate
    // )
    // SELECT
    // refid,
    // applicant_name,
    // mobile_number,
    // applicant_address,
    // event_id,
    // event_date,
    // no_of_days,
    // total_poles,
    // total_cost,
    // ae_status,
    // ?, -- NEW rdo_status
    // ?, -- NEW rdo_remarks
    // ?, -- approved_by
    // NOW(), -- approved_date
    // payment_status,
    // refund_status,
    // booking_status,
    // NOW()
    // FROM user_request_details
    // WHERE refid = ?
    // """;

    // int historyInserted = mysqlFlagPoleJdbcTemplate.update(
    // historySql,
    // status,
    // remarks,
    // approvedBy,
    // refId);

    // if (historyInserted == 0) {
    // return 0;
    // }

    // // 2️⃣ UPDATE MAIN TABLE
    // String updateSql = """
    // UPDATE user_request_details
    // SET
    // rdo_status = ?,
    // rdo_remarks = ?,
    // approved_by = ?,
    // approved_date = NOW()
    // WHERE refid = ?
    // """;

    // return mysqlFlagPoleJdbcTemplate.update(
    // updateSql,
    // status,
    // remarks,
    // approvedBy,
    // refId);
    // }

    @Transactional
    public int updateRdoDecision(
            String refId,
            String status,
            String remarks,
            String approvedBy) {

        // 🔹 1. Resolve department
        Integer deptId = getDepartmentIdByUsername(approvedBy);
        if (deptId == null) {
            throw new RuntimeException("User not mapped to any department");
        }

        String departmentName = getDepartmentNameByUsername(approvedBy);
        boolean isRdo = isRdoDepartment(departmentName);

        // 🔹 2. Insert officer feedback (ALL departments)
        insertOfficerFeedback(
                refId,
                deptId,
                status,
                remarks,
                approvedBy);

        // 🔹 3. ONLY RDO can update main request table
        if (isRdo) {

            // 🔹 3a. Insert history snapshot
            String historySql = """
                        INSERT INTO user_request_details_history
                        (
                            refid,
                            applicant_name,
                            mobile_number,
                            applicant_address,
                            event_id,
                            event_date,
                            no_of_days,
                            total_poles,
                            total_cost,
                            ae_status,
                            rdo_status,
                            rdo_remarks,
                            approved_by,
                            approved_date,
                            payment_status,
                            refund_status,
                            booking_status,
                            cdate
                        )
                        SELECT
                            refid,
                            applicant_name,
                            mobile_number,
                            applicant_address,
                            event_id,
                            event_date,
                            no_of_days,
                            total_poles,
                            total_cost,
                            ae_status,
                            ?, ?, ?, NOW(),
                            payment_status,
                            refund_status,
                            booking_status,
                            NOW()
                        FROM user_request_details
                        WHERE refid = ?
                    """;

            mysqlFlagPoleJdbcTemplate.update(
                    historySql,
                    status,
                    remarks,
                    approvedBy,
                    refId);

            // 🔹 3b. Update final RDO decision
            String updateSql = """
                        UPDATE user_request_details
                        SET
                            rdo_status = ?,
                            rdo_remarks = ?,
                            approved_by = ?,
                            rdo_updated_date = NOW()
                        WHERE refid = ?
                    """;

            return mysqlFlagPoleJdbcTemplate.update(
                    updateSql,
                    status,
                    remarks,
                    approvedBy,
                    refId);
        }

        // 🔹 4. Non-RDO users → only feedback saved
        return 1;
    }

    // public List<Map<String, Object>> getRdoApprovedRequestDetailsByUserLogin(
    // String username,
    // String startDate,
    // String endDate,
    // String status) {

    // // 🔹 USER ACCESS
    // // List<Map<String, Object>> access = getUserAccess(userLogin);

    // // 🔹 1. Get appuser_id from username
    // Long appUserId = getAppUserIdByUsername(username);

    // if (appUserId == null) {
    // throw new RuntimeException("Invalid session user: " + username);
    // }

    // // 🔹 2. Get zone & ward using appuser_id
    // List<Map<String, Object>> access = getUserAccess(appUserId);

    // if (access.isEmpty()) {
    // throw new RuntimeException("No zone/ward mapping for userId: " + appUserId);
    // }
    // String zoneArray = (String) access.get(0).get("zonearray");
    // String wardArray = (String) access.get(0).get("wardarray");

    // // 🔹 BASE QUERY
    // StringBuilder sql = new StringBuilder("""
    // SELECT
    // urd.id,
    // urd.applicant_name,
    // urd.mobile_number,
    // urd.applicant_address,
    // urd.rdo_remarks,
    // urd.event_date,
    // urd.event_desc,
    // urd.ae_status,
    // urd.rdo_status,
    // urd.refid,
    // urd.no_of_days,

    // sd.street_name,
    // sd.zone,
    // sd.ward,
    // sd.no_of_poles,
    // sd.street_cost,
    // sd.height,

    // fm.flag_material_name AS flag_material,
    // pm.pole_material_name AS pole_material,

    // urd.total_cost,
    // et.event_name

    // FROM user_request_details urd

    // LEFT JOIN street_details sd
    // ON urd.refid = sd.refid
    // AND sd.is_active = 1
    // AND sd.is_delete = 0

    // LEFT JOIN event_type et
    // ON urd.event_id = et.id

    // LEFT JOIN flag_material fm
    // ON sd.flag_material_id = fm.id

    // LEFT JOIN pole_material pm
    // ON sd.pole_material_id = pm.id

    // WHERE urd.ae_status = 'APPROVED'
    // AND FIND_IN_SET(sd.zone, ?)
    // AND FIND_IN_SET(sd.ward, ?)
    // """);

    // List<Object> params = new ArrayList<>();
    // params.add(zoneArray);
    // params.add(wardArray);

    // // 🔹 STATUS FILTER
    // if (status != null && !status.isEmpty()) {
    // sql.append(" AND urd.rdo_status = ? ");
    // params.add(status);
    // } else {
    // sql.append(" AND urd.rdo_status IN ('APPROVED','REJECTED') ");
    // }

    // // 🔹 DATE FILTER
    // if (startDate != null && !startDate.isEmpty()
    // && endDate != null && !endDate.isEmpty()) {

    // sql.append(" AND DATE(urd.event_date) BETWEEN ? AND ? ");
    // params.add(startDate);
    // params.add(endDate);

    // } else if (startDate != null && !startDate.isEmpty()) {

    // sql.append(" AND DATE(urd.event_date) = ? ");
    // params.add(startDate);
    // }

    // sql.append(" ORDER BY urd.refid, sd.id ");

    // // 🔹 EXECUTE QUERY
    // List<Map<String, Object>> rows =
    // mysqlFlagPoleJdbcTemplate.queryForList(sql.toString(), params.toArray());

    // // 🔥 GROUP BY REFID (INLINE – NO SEPARATE METHOD)
    // Map<String, Map<String, Object>> resultMap = new LinkedHashMap<>();

    // for (Map<String, Object> row : rows) {

    // String refid = (String) row.get("refid");

    // Map<String, Object> request = resultMap.computeIfAbsent(refid, k -> {
    // Map<String, Object> map = new LinkedHashMap<>();
    // map.put("refid", row.get("refid"));
    // map.put("applicant_name", row.get("applicant_name"));
    // map.put("mobile_number", row.get("mobile_number"));
    // map.put("applicant_address", row.get("applicant_address"));
    // map.put("event_desc", row.get("event_desc"));
    // map.put("rdo_remarks", row.get("rdo_remarks"));
    // map.put("event_date", row.get("event_date"));
    // map.put("ae_status", row.get("ae_status"));
    // map.put("rdo_status", row.get("rdo_status"));
    // map.put("no_of_days", row.get("no_of_days"));
    // map.put("event_name", row.get("event_name"));
    // map.put("total_cost", row.get("total_cost"));
    // map.put("streets", new ArrayList<Map<String, Object>>());
    // return map;
    // });

    // // 🔹 STREET DETAILS
    // Map<String, Object> street = new LinkedHashMap<>();
    // street.put("street_name", row.get("street_name"));
    // street.put("zone", row.get("zone"));
    // street.put("ward", row.get("ward"));
    // street.put("no_of_poles", row.get("no_of_poles"));
    // street.put("height", row.get("height"));
    // street.put("street_cost", row.get("street_cost"));
    // street.put("flag_material", row.get("flag_material"));
    // street.put("pole_material", row.get("pole_material"));

    // ((List<Map<String, Object>>) request.get("streets")).add(street);
    // }

    // return new ArrayList<>(resultMap.values());
    // }

    public List<Map<String, Object>> getRdoApprovedRequestDetailsByUserLogin(
            String username,
            String startDate,
            String endDate,
            String status) {

        Long appUserId = getAppUserIdByUsername(username);
        if (appUserId == null) {
            throw new RuntimeException("Invalid session user: " + username);
        }

        String departmentName = getDepartmentNameByUsername(username);
        boolean isRdo = isRdoDepartment(departmentName);
        Integer deptId = getDepartmentIdByUsername(username);

        String zoneArray = null;
        String wardArray = null;

        if (isRdo) {
            List<Map<String, Object>> access = getUserAccess(appUserId);
            if (access.isEmpty()) {
                throw new RuntimeException("No zone/ward mapping for RDO userId: " + appUserId);
            }
            zoneArray = (String) access.get(0).get("zonearray");
            wardArray = (String) access.get(0).get("wardarray");
        }

        // 🔥 IMPORTANT: JOIN officer_feedback
        StringBuilder sql = new StringBuilder("""
                    SELECT
                        urd.id,
                        urd.applicant_name,
                        urd.mobile_number,
                        urd.applicant_address,
                        urd.event_desc,
                        urd.cdate,
                        urd.event_date,
                        urd.ae_status,
                        urd.rdo_status,
                        urd.rdo_remarks,
                        urd.refid,
                        urd.no_of_days,

                        sd.street_name,
                        sd.zone,
                        sd.ward,
                        sd.no_of_poles,
                        sd.street_cost,
                        sd.height,

                        fm.flag_material_name AS flag_material,
                        pm.pole_material_name AS pole_material,

                        urd.total_cost,
                        et.event_name,

                        ofb.status   AS my_status,
                        ofb.remarks AS my_remarks

                    FROM officer_feedback ofb
                    JOIN user_request_details urd
                         ON urd.refid = ofb.refid
                    LEFT JOIN street_details sd
                           ON urd.refid = sd.refid
                          AND sd.is_active = 1
                          AND sd.is_delete = 0
                    LEFT JOIN event_type et ON urd.event_id = et.id
                    LEFT JOIN flag_material fm ON sd.flag_material_id = fm.id
                    LEFT JOIN pole_material pm ON sd.pole_material_id = pm.id
                    WHERE ofb.dept_id = ?
                      AND ofb.status IN ('APPROVED','REJECTED')
                      AND ofb.is_active = 1
                      AND ofb.is_delete = 0
                """);

        List<Object> params = new ArrayList<>();
        params.add(deptId);

        // 🔹 Optional status filter
        if (status != null && !status.isEmpty()) {
            sql.append(" AND ofb.status = ? ");
            params.add(status);
        }

        // 🔹 RDO zone/ward filter
        if (isRdo) {
            sql.append(" AND FIND_IN_SET(sd.zone, ?) ");
            sql.append(" AND FIND_IN_SET(sd.ward, ?) ");
            params.add(zoneArray);
            params.add(wardArray);
        }

        // 🔹 Date filter
        if (startDate != null && !startDate.isEmpty()
                && endDate != null && !endDate.isEmpty()) {

            sql.append(" AND DATE(urd.event_date) BETWEEN ? AND ? ");
            params.add(startDate);
            params.add(endDate);

        } else if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND DATE(urd.event_date) = ? ");
            params.add(startDate);
        }

        sql.append(" ORDER BY ofb.cdate DESC, urd.refid, sd.id ");

        List<Map<String, Object>> rows = mysqlFlagPoleJdbcTemplate.queryForList(sql.toString(), params.toArray());

        // 🔥 GROUP BY REFID
        Map<String, Map<String, Object>> resultMap = new LinkedHashMap<>();

        for (Map<String, Object> row : rows) {

            String refid = (String) row.get("refid");

            Map<String, Object> request = resultMap.computeIfAbsent(refid, k -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("refid", row.get("refid"));
                map.put("applicant_name", row.get("applicant_name"));
                map.put("mobile_number", row.get("mobile_number"));
                map.put("applicant_address", row.get("applicant_address"));
                map.put("event_desc", row.get("event_desc"));
                map.put("cdate", row.get("cdate"));
                map.put("event_date", row.get("event_date"));
                map.put("ae_status", row.get("ae_status"));
                map.put("rdo_status", row.get("rdo_status"));
                map.put("rdo_remarks", row.get("rdo_remarks"));
                map.put("no_of_days", row.get("no_of_days"));
                map.put("event_name", row.get("event_name"));
                map.put("total_cost", row.get("total_cost"));

                // 🔥 department decision
                map.put("my_status", row.get("my_status"));
                map.put("my_remarks", row.get("my_remarks"));

                map.put("streets", new ArrayList<>());
                return map;
            });

            Map<String, Object> street = new LinkedHashMap<>();
            street.put("street_name", row.get("street_name"));
            street.put("zone", row.get("zone"));
            street.put("ward", row.get("ward"));
            street.put("no_of_poles", row.get("no_of_poles"));
            street.put("height", row.get("height"));
            street.put("street_cost", row.get("street_cost"));
            street.put("flag_material", row.get("flag_material"));
            street.put("pole_material", row.get("pole_material"));

            ((List<Map<String, Object>>) request.get("streets")).add(street);
        }

        return new ArrayList<>(resultMap.values());
    }

}
