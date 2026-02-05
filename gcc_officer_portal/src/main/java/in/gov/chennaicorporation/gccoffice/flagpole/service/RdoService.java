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

    // @Autowired
    // private WhatsappServiceFlagPole whatsappService;

    @Autowired
    private FlagpoleSMSService smsService;

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

    public Long getAppUserIdByGccUserId(String gccUserId) {

        String sql = """
                    SELECT id
                    FROM appusers
                    WHERE userid = ?
                      AND isactive = 1
                      AND isdelete = 0
                    LIMIT 1
                """;

        List<Long> list = mysqlAppJdbcTemplate.queryForList(sql, Long.class, gccUserId);

        return list.isEmpty() ? null : list.get(0);
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

    // public List<Map<String, Object>> getOfficerFeedbackHistoryByRefId(
    // String refid,
    // String username) {

    // Integer deptId = getDepartmentIdByUsername(username);
    // String departmentName = getDepartmentNameByUsername(username);
    // boolean isRdo = isRdoDepartment(departmentName);

    // StringBuilder sql = new StringBuilder("""
    // SELECT
    // ofb.refid,
    // dl.department_name,
    // ofb.status,
    // ofb.remarks,
    // ofb.cby,
    // DATE_FORMAT(ofb.cdate, '%d-%m-%Y %h:%i %p') AS action_date
    // FROM officer_feedback ofb
    // JOIN department_login dl
    // ON ofb.dept_id = dl.dpid
    // WHERE ofb.refid = ?
    // AND ofb.is_active = 1
    // AND ofb.is_delete = 0
    // """);

    // List<Object> params = new ArrayList<>();
    // params.add(refid);

    // // 🔥 NON-RDO → show ONLY own department history
    // if (!isRdo) {
    // sql.append(" AND ofb.dept_id = ? ");
    // params.add(deptId);
    // }

    // sql.append(" ORDER BY ofb.cdate ASC ");

    // return mysqlFlagPoleJdbcTemplate.queryForList(sql.toString(),
    // params.toArray());
    // }

    private boolean hasValue(String s) {
        return s != null && !s.trim().isEmpty();
    }

    // old
    public List<Map<String, Object>> getOfficerFeedbackHistoryByRefId(
            String refid,
            String gccUserId) {

        /* 🔥 Resolve appuser_id from gccuserid (gcc_apps) */
        Long appUserId = getAppUserIdByGccUserId(gccUserId);
        if (appUserId == null) {
            throw new RuntimeException("Invalid gccUserId: " + gccUserId);
        }

        /* 🔥 Resolve department id (gcc_flag_pole) */
        Integer deptId = getDepartmentIdByUserId(gccUserId);

        /* 🔥 Resolve access (gcc_apps) */
        List<Map<String, Object>> access = getUserAccess(appUserId);

        String zone = null;
        String zoneArray = null;
        String wardArray = null;

        boolean isRdo = false;
        boolean isZonalOfficer = false;

        if (!access.isEmpty()) {

            Map<String, Object> row = access.get(0);

            zone = row.get("zone") != null ? row.get("zone").toString() : null;
            // zoneArray = row.get("zonearray") != null ? row.get("zonearray").toString() :
            // null;
            // wardArray = row.get("wardarray") != null ? row.get("wardarray").toString() :
            // null;

            /* ✅ RDO → zonearray + wardarray */
            // if (hasValue(zoneArray) && hasValue(wardArray)) {
            // isRdo = true;
            // }

            /* ✅ Zonal Officer → zone 01–15 */
            if (hasValue(zone) && zone.matches("^(0[1-9]|1[0-5])$")) {
                isZonalOfficer = true;
            }
        }

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

        /* 🔥 Only NON-RDO & NON-ZONAL are restricted */
        if (!isRdo && !isZonalOfficer) {
            sql.append(" AND ofb.dept_id = ? ");
            params.add(deptId);
        }

        sql.append(" ORDER BY ofb.cdate ASC ");

        return mysqlFlagPoleJdbcTemplate.queryForList(sql.toString(),
                params.toArray());
    }

    // to show for all departments
    // public List<Map<String, Object>> getOfficerFeedbackHistoryByRefId(
    // String refid,
    // String gccUserId) {

    // /* 🔥 Just validate gccUserId exists */
    // Long appUserId = getAppUserIdByGccUserId(gccUserId);
    // if (appUserId == null) {
    // throw new RuntimeException("Invalid gccUserId: " + gccUserId);
    // }

    // String sql = """
    // SELECT
    // ofb.refid,
    // dl.department_name,
    // ofb.status,
    // ofb.remarks,
    // ofb.cby,
    // DATE_FORMAT(ofb.cdate, '%d-%m-%Y %h:%i %p') AS action_date
    // FROM officer_feedback ofb
    // JOIN department_login dl
    // ON ofb.dept_id = dl.dpid
    // WHERE ofb.refid = ?
    // AND ofb.is_active = 1
    // AND ofb.is_delete = 0
    // ORDER BY ofb.cdate ASC
    // """;

    // return mysqlFlagPoleJdbcTemplate.queryForList(sql, refid);
    // }

    // public int insertOfficerFeedback(
    // String refId,
    // Integer deptId,
    // String status,
    // String remarks,
    // String createdBy) {

    // String sql = """
    // INSERT INTO officer_feedback
    // (
    // refid,
    // dept_id,
    // status,
    // remarks,
    // cby,
    // cdate,
    // is_active,
    // is_delete
    // )
    // VALUES (?, ?, ?, ?, ?, NOW(), 1, 0)
    // """;

    // return mysqlFlagPoleJdbcTemplate.update(
    // sql,
    // refId,
    // deptId,
    // status,
    // remarks,
    // createdBy);
    // }

    public int insertOfficerFeedback(
            String refId,
            Integer deptId,
            Integer gccUserId, // login user id
            String status,
            String remarks,
            String username // display username
    ) {

        String sql = """
                    INSERT INTO officer_feedback
                    (
                        refid,
                        dept_id,
                        gcc_user_id,
                        status,
                        remarks,
                        cby,
                        cdate,
                        is_active,
                        is_delete
                    )
                    VALUES (?, ?, ?, ?, ?, ?, NOW(), 1, 0)
                """;

        return mysqlFlagPoleJdbcTemplate.update(
                sql,
                refId,
                deptId,
                gccUserId,
                status,
                remarks,
                username);
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
                    SELECT lmu.zone,lmu.ward,lmu.zonearray, lmu.wardarray
                    FROM login_mapping_user lmu
                    WHERE lmu.appuser_id = ?
                """;

        return mysqlAppJdbcTemplate.queryForList(sql, appUserId);
    }

    public String resolveGccUserId(String input) {

        // already numeric
        if (input.matches("\\d+")) {
            return input;
        }

        // username → userid
        String sql = """
                    SELECT userid
                    FROM appusers
                    WHERE username = ?
                      AND isactive = 1
                      AND isdelete = 0
                    LIMIT 1
                """;

        List<Integer> list = mysqlAppJdbcTemplate.queryForList(sql, Integer.class, input);

        if (list.isEmpty()) {
            throw new RuntimeException("Invalid login user: " + input);
        }

        return String.valueOf(list.get(0));
    }

    public String getDepartmentNameByUserId(String gccUserId) {

        String sql = """
                    SELECT department_name
                    FROM department_login
                    WHERE gcc_user_id = ?
                      AND is_active = 1
                      AND is_delete = 0
                """;

        List<String> list = mysqlFlagPoleJdbcTemplate.queryForList(sql, String.class, gccUserId);

        return list.isEmpty() ? null : list.get(0);
    }

    public Integer getDepartmentIdByUserId(String gccUserId) {

        String sql = """
                    SELECT dpid
                    FROM department_login
                    WHERE gcc_user_id = ?
                      AND is_active = 1
                      AND is_delete = 0
                """;

        List<Integer> list = mysqlFlagPoleJdbcTemplate.queryForList(sql, Integer.class, gccUserId);

        return list.isEmpty() ? null : list.get(0);
    }

    public String getUsernameByUserId(String gccUserId) {

        String sql = """
                    SELECT user_name
                    FROM department_login
                    WHERE gcc_user_id = ?
                      AND is_active = 1
                      AND is_delete = 0
                """;

        List<String> list = mysqlFlagPoleJdbcTemplate.queryForList(sql, String.class, gccUserId);

        return list.isEmpty() ? null : list.get(0);
    }

    public List<Map<String, Object>> getAllRequestDetailsByUserLogin(
            String gccUserId,
            String startDate,
            String endDate) {

        /* 🔥 Resolve appuser_id */
        Long appUserId = getAppUserIdByGccUserId(gccUserId);
        if (appUserId == null) {
            throw new RuntimeException("Invalid gccUserId: " + gccUserId);
        }

        /* 🔥 Resolve department id */
        Integer deptId = getDepartmentIdByUserId(gccUserId);
        if (deptId == null) {
            throw new RuntimeException("Department mapping missing for gccUserId: " + gccUserId);
        }

        /* 🔥 Resolve access */
        List<Map<String, Object>> access = getUserAccess(appUserId);

        String zone = null;
        String zoneArray = null;
        String wardArray = null;

        boolean isRdo = false;
        boolean isZonalOfficer = false;

        if (!access.isEmpty()) {

            zone = (String) access.get(0).get("zone");
            zoneArray = (String) access.get(0).get("zonearray");
            wardArray = (String) access.get(0).get("wardarray");

            // RDO → zonearray + wardarray
            if (hasValue(zoneArray) && hasValue(wardArray)) {
                isRdo = true;
            }
            // Zonal → single zone
            else if (hasValue(zone)) {
                isZonalOfficer = true;
            }
        }

        StringBuilder sql = new StringBuilder("""
                    SELECT
                        urd.id,
                        urd.refid,
                        urd.applicant_name,
                        urd.mobile_number,
                        urd.applicant_address,
                        urd.event_desc,
                        urd.cdate,
                        urd.event_date,
                        urd.ae_status,
                        urd.rdo_status,
                        urd.final_status,
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

        /* 🔥 1. REMOVE RDO-FINALIZED */
        // sql.append(" AND (urd.rdo_status IS NULL OR urd.rdo_status = '') ");
        sql.append(" AND (urd.final_status IS NULL OR urd.final_status = '') ");

        /* 🔥 2. REMOVE ALREADY DECIDED BY SAME DEPT */
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

        /* 🔥 3a. RDO → multi zone + ward */
        if (isRdo) {
            sql.append(" AND FIND_IN_SET(sd.zone, ?) ");
            sql.append(" AND FIND_IN_SET(sd.ward, ?) ");
            params.add(zoneArray);
            params.add(wardArray);
        }

        /* 🔥 3b. ZONAL → single zone */
        else if (isZonalOfficer) {
            sql.append(" AND sd.zone = ? ");
            params.add(zone);
        }

        /* 🔹 DATE FILTER */
        if (hasValue(startDate) && hasValue(endDate)) {

            sql.append(" AND DATE(urd.event_date) BETWEEN ? AND ? ");
            params.add(startDate);
            params.add(endDate);

        } else if (hasValue(startDate)) {

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

        /* 🔥 GROUP BY REFID */
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
                map.put("final_status", row.get("final_status"));
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

    // old with rdo and zonal officer final
    // @Transactional
    // public int updateOfficerDecision(
    // String refId,
    // String status,
    // String remarks,
    // String gccUserId,
    // String username) {

    // /* 🔹 Resolve appuser_id */
    // Long appUserId = getAppUserIdByGccUserId(gccUserId);
    // if (appUserId == null) {
    // throw new RuntimeException("Invalid gccUserId");
    // }

    // /* 🔹 Resolve department */
    // Integer deptId = getDepartmentIdByUserId(gccUserId);
    // if (deptId == null) {
    // throw new RuntimeException("User not mapped to department");
    // }

    // /* 🔹 Access */
    // List<Map<String, Object>> access = getUserAccess(appUserId);

    // String zone = null;
    // String zoneArray = null;
    // String wardArray = null;

    // boolean isRdo = false;
    // boolean isZonalOfficer = false;

    // if (!access.isEmpty()) {

    // Map<String, Object> row = access.get(0);

    // zone = row.get("zone") != null ? row.get("zone").toString() : null;
    // zoneArray = row.get("zonearray") != null ? row.get("zonearray").toString() :
    // null;
    // wardArray = row.get("wardarray") != null ? row.get("wardarray").toString() :
    // null;

    // /* ✅ RDO → zonearray + wardarray */
    // if (hasValue(zoneArray) && hasValue(wardArray)) {
    // isRdo = true;
    // }

    // /* ✅ Zonal Officer → zone 01–15 */
    // if (hasValue(zone) && zone.matches("^(0[1-9]|1[0-5])$")) {
    // isZonalOfficer = true;
    // }
    // }

    // /*
    // * =====================================================
    // * 🔹 INSERT FEEDBACK (ALL USERS)
    // * =====================================================
    // */

    // insertOfficerFeedback(
    // refId,
    // deptId,
    // Integer.valueOf(gccUserId),
    // status,
    // remarks,
    // username);

    // /*
    // * =====================================================
    // * 🔹 RDO FLOW
    // * =====================================================
    // */

    // if (isRdo) {

    // String historySql = """
    // INSERT INTO user_request_details_history
    // (
    // refid, applicant_name, mobile_number, applicant_address,
    // event_id, event_date, no_of_days, total_poles, total_cost,
    // ae_status, rdo_status, rdo_remarks,
    // approved_by, approved_date,
    // payment_status, refund_status, booking_status, cdate
    // )
    // SELECT
    // refid, applicant_name, mobile_number, applicant_address,
    // event_id, event_date, no_of_days, total_poles, total_cost,
    // ae_status, ?, ?, ?, NOW(),
    // payment_status, refund_status, booking_status, NOW()
    // FROM user_request_details
    // WHERE refid = ?
    // """;

    // mysqlFlagPoleJdbcTemplate.update(historySql, status, remarks, gccUserId,
    // refId);

    // String updateSql = """
    // UPDATE user_request_details
    // SET
    // rdo_status = ?,
    // rdo_remarks = ?,
    // approved_by = ?,
    // rdo_updated_date = NOW()
    // WHERE refid = ?
    // """;

    // return mysqlFlagPoleJdbcTemplate.update(updateSql, status, remarks,
    // gccUserId, refId);
    // }

    // /*
    // * =====================================================
    // * 🔹 ZONAL OFFICER FLOW (FINAL)
    // * =====================================================
    // */

    // if (isZonalOfficer) {

    // String historySql = """
    // INSERT INTO user_request_details_history
    // (
    // refid, applicant_name, mobile_number, applicant_address,
    // event_id, event_date, no_of_days, total_poles, total_cost,
    // ae_status, rdo_status, rdo_remarks,
    // final_status, final_remarks, final_approved_by, final_approved_date,
    // payment_status, refund_status, booking_status, cdate
    // )
    // SELECT
    // refid, applicant_name, mobile_number, applicant_address,
    // event_id, event_date, no_of_days, total_poles, total_cost,
    // ae_status, rdo_status, rdo_remarks,
    // ?, ?, ?, NOW(),
    // payment_status, refund_status, booking_status, NOW()
    // FROM user_request_details
    // WHERE refid = ?
    // """;

    // mysqlFlagPoleJdbcTemplate.update(historySql, status, remarks, gccUserId,
    // refId);

    // String updateSql = """
    // UPDATE user_request_details
    // SET
    // final_status = ?,
    // final_remarks = ?,
    // final_approved_by = ?,
    // final_approved_date = NOW()
    // WHERE refid = ?
    // """;

    // return mysqlFlagPoleJdbcTemplate.update(updateSql, status, remarks,
    // gccUserId, refId);
    // }

    // /*
    // * =====================================================
    // * 🔹 OTHER DEPARTMENTS → FEEDBACK ONLY
    // * =====================================================
    // */

    // return 1;
    // }

    @Transactional
    public int updateOfficerDecision(
            String refId,
            String status,
            String remarks,
            String gccUserId,
            String username) {

        /* 🔹 Resolve appuser_id */
        Long appUserId = getAppUserIdByGccUserId(gccUserId);
        if (appUserId == null) {
            throw new RuntimeException("Invalid gccUserId");
        }

        /* 🔹 Resolve department */
        Integer deptId = getDepartmentIdByUserId(gccUserId);
        if (deptId == null) {
            throw new RuntimeException("User not mapped to department");
        }

        /* 🔹 Access */
        List<Map<String, Object>> access = getUserAccess(appUserId);

        String zone = null;
        String zoneArray = null;
        String wardArray = null;

        boolean isRdo = false;
        boolean isZonalOfficer = false;

        if (!access.isEmpty()) {

            Map<String, Object> row = access.get(0);

            zone = row.get("zone") != null ? row.get("zone").toString() : null;
            zoneArray = row.get("zonearray") != null ? row.get("zonearray").toString() : null;
            wardArray = row.get("wardarray") != null ? row.get("wardarray").toString() : null;

            if (hasValue(zoneArray) && hasValue(wardArray)) {
                isRdo = true;
            }

            if (hasValue(zone) && zone.matches("^(0[1-9]|1[0-5])$")) {
                isZonalOfficer = true;
            }
        }

        /*
         * =====================================================
         * 🔐 Check if already FINALIZED
         * =====================================================
         */

        String finalStatus = mysqlFlagPoleJdbcTemplate.queryForObject(
                "SELECT final_status FROM user_request_details WHERE refid = ?",
                String.class,
                refId);

        if (finalStatus != null && !finalStatus.isBlank() && !isZonalOfficer) {
            throw new RuntimeException("Request already finalized by Zonal Officer");
        }

        /*
         * =====================================================
         * 🔹 INSERT FEEDBACK (ALL USERS)
         * =====================================================
         */

        insertOfficerFeedback(
                refId,
                deptId,
                Integer.valueOf(gccUserId),
                status,
                remarks,
                username);

        /*
         * =====================================================
         * 🔹 ZONAL OFFICER FLOW (ONLY FINAL AUTHORITY)
         * =====================================================
         */

        if (isZonalOfficer) {

            String historySql = """
                        INSERT INTO user_request_details_history
                        (
                            refid, applicant_name, mobile_number, applicant_address,
                            event_id, event_date, no_of_days, total_poles, total_cost,
                            ae_status, rdo_status, rdo_remarks,
                            final_status, final_remarks, final_approved_by, final_approved_date,
                            payment_status, refund_status, booking_status, cdate
                        )
                        SELECT
                            refid, applicant_name, mobile_number, applicant_address,
                            event_id, event_date, no_of_days, total_poles, total_cost,
                            ae_status, rdo_status, rdo_remarks,
                            ?, ?, ?, NOW(),
                            payment_status, refund_status, booking_status, NOW()
                        FROM user_request_details
                        WHERE refid = ?
                    """;

            mysqlFlagPoleJdbcTemplate.update(historySql, status, remarks, gccUserId, refId);

            String updateSql = """
                        UPDATE user_request_details
                        SET
                            final_status = ?,
                            final_remarks = ?,
                            final_approved_by = ?,
                            final_approved_date = NOW()
                        WHERE refid = ?
                    """;

            // return mysqlFlagPoleJdbcTemplate.update(updateSql, status, remarks,
            // gccUserId, refId);

            int updated = mysqlFlagPoleJdbcTemplate.update(updateSql, status, remarks, gccUserId, refId);

            // ================= SEND SMS AFTER FINAL APPROVAL =================
            if ("APPROVED".equalsIgnoreCase(status)) {

                try {

                    String mobileNo = mysqlFlagPoleJdbcTemplate.queryForObject("""
                            SELECT mobile_number
                            FROM user_request_details
                            WHERE refid = ?
                            """, String.class, refId);

                    if (mobileNo != null && !mobileNo.isBlank()) {
                        smsService.userrequestApprovedsms(mobileNo, refId);
                    }

                } catch (Exception e) {
                    System.err.println("Approved SMS failed for requestId: " + refId);
                    e.printStackTrace();
                }
            }

            if ("REJECTED".equalsIgnoreCase(status)) {

                try {
                    System.out.println("Rejected RefID = " + refId);

                    String mobileNo = mysqlFlagPoleJdbcTemplate.queryForObject("""
                            SELECT mobile_number
                            FROM user_request_details
                            WHERE refid = ?
                            """, String.class, refId);

                    if (mobileNo != null && !mobileNo.isBlank()) {
                        System.out.println("Rejected Mob Num = " + mobileNo);
                        smsService.userrequestRejectedsms(mobileNo, refId);
                    }

                } catch (Exception e) {
                    System.err.println("Approved SMS failed for requestId: " + refId);
                    e.printStackTrace();
                }
            }
            // ================================================================

            return updated;

        }

        /*
         * =====================================================
         * RDO FLOW (ONLY RDO FIELDS)
         * =====================================================
         */

        if (isRdo) {

            String historySql = """
                        INSERT INTO user_request_details_history
                        (
                            refid, applicant_name, mobile_number, applicant_address,
                            event_id, event_date, no_of_days, total_poles, total_cost,
                            ae_status, rdo_status, rdo_remarks,
                            approved_by, approved_date,
                            payment_status, refund_status, booking_status, cdate
                        )
                        SELECT
                            refid, applicant_name, mobile_number, applicant_address,
                            event_id, event_date, no_of_days, total_poles, total_cost,
                            ae_status, ?, ?, ?, NOW(),
                            payment_status, refund_status, booking_status, NOW()
                        FROM user_request_details
                        WHERE refid = ?
                    """;

            mysqlFlagPoleJdbcTemplate.update(historySql, status, remarks, gccUserId, refId);

            String updateSql = """
                        UPDATE user_request_details
                        SET
                            rdo_status = ?,
                            rdo_remarks = ?,
                            approved_by = ?,
                            rdo_updated_date = NOW()
                        WHERE refid = ?
                    """;

            return mysqlFlagPoleJdbcTemplate.update(updateSql, status, remarks, gccUserId, refId);
        }

        return 1;
    }

    public List<Map<String, Object>> getRdoApprovedRequestDetailsByUserLogin(
            String gccUserId,
            String startDate,
            String endDate,
            String status) {

        /* 🔥 Resolve appuser_id */
        Long appUserId = getAppUserIdByGccUserId(gccUserId);
        if (appUserId == null) {
            throw new RuntimeException("Invalid gccUserId: " + gccUserId);
        }

        /* 🔥 Resolve department */
        Integer deptId = getDepartmentIdByUserId(gccUserId);
        if (deptId == null) {
            throw new RuntimeException("Department mapping missing for gccUserId: " + gccUserId);
        }

        /* 🔥 Resolve access */
        List<Map<String, Object>> access = getUserAccess(appUserId);

        String zone = null;
        String zoneArray = null;
        String wardArray = null;

        boolean isRdo = false;
        boolean isZonalOfficer = false;

        if (!access.isEmpty()) {
            zone = (String) access.get(0).get("zone");
            zoneArray = (String) access.get(0).get("zonearray");
            wardArray = (String) access.get(0).get("wardarray");

            if (hasValue(zoneArray) && hasValue(wardArray)) {
                isRdo = true;
            } else if (hasValue(zone)) {
                isZonalOfficer = true;
            }
        }

        StringBuilder sql = new StringBuilder("""
                    SELECT
                        urd.id,
                        urd.refid,
                        urd.applicant_name,
                        urd.mobile_number,
                        urd.applicant_address,
                        urd.event_desc,
                        urd.cdate,
                        urd.event_date,
                        urd.ae_status,
                        urd.rdo_status,
                        urd.rdo_remarks,
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

                        ofb.status   AS officer_status,
                        ofb.remarks AS officer_remarks

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

        /* 🔹 Optional status */
        if (hasValue(status)) {
            sql.append(" AND ofb.status = ? ");
            params.add(status);
        }

        /* 🔹 RDO → zone + ward */
        if (isRdo) {
            sql.append(" AND FIND_IN_SET(sd.zone, ?) ");
            sql.append(" AND FIND_IN_SET(sd.ward, ?) ");
            params.add(zoneArray);
            params.add(wardArray);
        }
        /* 🔹 Zonal → only zone */
        else if (isZonalOfficer) {
            sql.append(" AND sd.zone = ? ");
            params.add(zone);
        }

        /* 🔹 Date */
        if (hasValue(startDate) && hasValue(endDate)) {

            sql.append(" AND DATE(urd.event_date) BETWEEN ? AND ? ");
            params.add(startDate);
            params.add(endDate);

        } else if (hasValue(startDate)) {

            sql.append(" AND DATE(urd.event_date) = ? ");
            params.add(startDate);
        }

        sql.append(" ORDER BY ofb.cdate DESC, urd.refid, sd.id ");

        List<Map<String, Object>> rows = mysqlFlagPoleJdbcTemplate.queryForList(sql.toString(), params.toArray());

        /* 🔥 GROUP BY REFID */
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
                map.put("officer_status", row.get("officer_status"));
                map.put("officer_remarks", row.get("officer_remarks"));
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
