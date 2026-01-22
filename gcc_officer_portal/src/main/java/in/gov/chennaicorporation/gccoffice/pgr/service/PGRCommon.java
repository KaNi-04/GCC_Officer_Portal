package in.gov.chennaicorporation.gccoffice.pgr.service;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PGRCommon {
	private JdbcTemplate jdbcTemplate;
	private JdbcTemplate jdbcPGRTemplate;
	
	@Autowired
	public void setDataSource(@Qualifier("oracleDataSource") DataSource dataSource,@Qualifier("mysqlPGRMasterDataSource") DataSource pgrDataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.jdbcPGRTemplate = new JdbcTemplate(pgrDataSource);
	}
	 
	@Transactional
	public List<Map<String, Object>> getDeaprtmentList() {
		
		String SqlQuery = ""; 
		
		SqlQuery="SELECT Distinct id_dept,dept_name "
		+ "FROM EG_DEPARTMENT@ERP "
		+ "WHERE NVL(dept_name, '0') NOT LIKE 'F%' AND dept_name IS NOT NULL "
		+ "ORDER BY id_dept";
		 
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		return result;
	}
	 
	@Transactional
	public List<Map<String, Object>> getZoneList() {
		
		String SqlQuery = ""; 
		
		SqlQuery="SELECT Distinct id_dept,dept_name "
		+ "FROM EG_DEPARTMENT@ERP "
		+ "WHERE NVL(dept_name, '0') NOT LIKE 'F%' AND dept_name IS NOT NULL AND (id_dept >= 32 AND id_dept <= 46)"
		+ "ORDER BY id_dept";
		 
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		return result;
	}
	 
	@Transactional
	public List<Map<String, Object>> getPendingComplanitCount() {
		
		String SqlQuery = "";
		
		SqlQuery = "SELECT c.dept_name, a.deptid, a.days_90, a.days_90_pen, a.days_90_reopen, "
	        + "a.days_61_90, a.days_61_90_pen, a.days_61_90_reopen, "
	        + "a.days_31_60, a.days_31_60_pen, a.days_31_60_reopen, "
	        + "a.days_16_30, a.days_16_30_pen, a.days_16_30_reopen, "
	        + "a.days_0_15, a.days_0_15_pen, a.days_0_15_reopen, "
	        + "a.Total, a.Total_pen, a.Total_reopen "
	        + "FROM ( "
	        + "SELECT "
	        + "a.deptid, "
	        + "nvl(sum(case when sysdate - a.complaintdate > 90 then 1 end), '0') AS days_90, "
	        + "nvl(sum(case when sysdate - a.complaintdate > 90 AND b.COMPLAINTSTATUSID IN (2, 3, 4) then 1 end), '0') AS days_90_pen, "
	        + "nvl(sum(case when sysdate - a.complaintdate > 90 AND b.COMPLAINTSTATUSID IN (9) then 1 end), '0') AS days_90_reopen, "
	        + "nvl(sum(case when sysdate - a.complaintdate > 60 AND sysdate - a.complaintdate <= 90 then 1 end), '0') AS days_61_90, "
	        + "nvl(sum(case when sysdate - a.complaintdate > 60 AND sysdate - a.complaintdate <= 90 AND b.COMPLAINTSTATUSID IN (2, 3, 4) then 1 end), '0') AS days_61_90_pen, "
	        + "nvl(sum(case when sysdate - a.complaintdate > 60 AND sysdate - a.complaintdate <= 90 AND b.COMPLAINTSTATUSID IN (9) then 1 end), '0') AS days_61_90_reopen, "
	        + "nvl(sum(case when sysdate - a.complaintdate > 30 AND sysdate - a.complaintdate <= 60 then 1 end), '0') AS days_31_60, "
	        + "nvl(sum(case when sysdate - a.complaintdate > 30 AND sysdate - a.complaintdate <= 60 AND b.COMPLAINTSTATUSID IN (2, 3, 4) then 1 end), '0') AS days_31_60_pen, "
	        + "nvl(sum(case when sysdate - a.complaintdate > 30 AND sysdate - a.complaintdate <= 60 AND b.COMPLAINTSTATUSID IN (9) then 1 end), '0') AS days_31_60_reopen, "
	        + "nvl(sum(case when sysdate - a.complaintdate > 15 AND sysdate - a.complaintdate <= 30 then 1 end), '0') AS days_16_30, "
	        + "nvl(sum(case when sysdate - a.complaintdate > 15 AND sysdate - a.complaintdate <= 30 AND b.COMPLAINTSTATUSID IN (2, 3, 4) then 1 end), '0') AS days_16_30_pen, "
	        + "nvl(sum(case when sysdate - a.complaintdate > 15 AND sysdate - a.complaintdate <= 30 AND b.COMPLAINTSTATUSID IN (9) then 1 end), '0') AS days_16_30_reopen, "
	        + "nvl(sum(case when sysdate - a.complaintdate > 0 AND sysdate - a.complaintdate <= 15 then 1 end), '0') AS days_0_15, "
	        + "nvl(sum(case when sysdate - a.complaintdate > 0 AND sysdate - a.complaintdate <= 15 AND b.COMPLAINTSTATUSID IN (2, 3, 4) then 1 end), '0') AS days_0_15_pen, "
	        + "nvl(sum(case when sysdate - a.complaintdate > 0 AND sysdate - a.complaintdate <= 15 AND b.COMPLAINTSTATUSID IN (9) then 1 end), '0') AS days_0_15_reopen, "
	        + "nvl(count(*), '0') AS Total, "
	        + "nvl(count(case when b.COMPLAINTSTATUSID IN (2, 3, 4) then 1 end), '0') AS Total_pen, "
	        + "nvl(count(case when b.COMPLAINTSTATUSID IN (9) then 1 end), '0') AS Total_reopen "
	        + "FROM eggr_complaintdetails@erp a, eggr_redressaldetails@erp b "
	        + "WHERE a.complaintid = b.complaintid ";
		//25-oct-2010
		SqlQuery += " AND (a.complaintdate >= '01-apr-2009' AND a.complaintdate < '01-apr-2009') GROUP BY a.deptid) a, EG_DEPARTMENT@ERP c "
				+ "WHERE a.deptid = c.id_dept AND (NVL(DEPT_NAME, 0) NOT LIKE 'F%' AND DEPT_NAME IS NOT NULL)";
	
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		return result;
	}
	 
	@Transactional
	public List<Map<String, Object>> getZoneWisePendingComplanitCount() {
		
		String SqlQuery = ""; 
		
		SqlQuery = "SELECT deptid, DEPT_NAME"
		 		+ ", NVL(SUM(CASE WHEN complainttype IN (52,146) THEN 1 END), 0) AS NonBURSTLT"
		 		+ ", NVL(SUM(CASE WHEN complainttype IN (1,97) THEN 1 END), 0) AS REOFGAR"
		 		+ ", NVL(SUM(CASE WHEN complainttype IN (22,32,57,59,70,113,114,130,145) THEN 1 END), 0) AS ROAD"
		 		+ ", NVL(SUM(CASE WHEN complainttype IN (18,118) THEN 1 END), 0) AS MOSQUITE"
		 		+ ", NVL(SUM(CASE WHEN complainttype IN (69,138) THEN 1 END), 0) AS WATER"
		 		+ ", NVL(SUM(CASE WHEN complainttype IN (7,99) THEN 1 END), 0) AS REOFDEB"
		 		+ ", NVL(SUM(CASE WHEN complainttype IN (71,152) THEN 1 END), 0) AS TREE"
		 		+ ", NVL(SUM(CASE WHEN complainttype IN (19,119) THEN 1 END), 0) AS DOGS"
		 		+ ", NVL(SUM(CASE WHEN complainttype IN (75,181) THEN 1 END), 0) AS ENCMTN"
		 		+ ", NVL(SUM(CASE WHEN complainttype IN (58,112) THEN 1 END), 0) AS POT"
		 		+ ", NVL(SUM(CASE WHEN complainttype IN (43,166) THEN 1 END), 0) AS PTAX"
		 		+ ", NVL(SUM(CASE WHEN complainttype NOT IN (52,146,1,97,22,32,57,59,70,113,114,130,145,18,118,69,138,7,99,71,152,19,119,75,181,58,112,43,166) THEN 1 END), 0) AS OTHERS"
		 		+ ", COUNT(*) AS Total FROM "
		 		+ "  eggr_complaintdetails@erp a "
		 		+ ", eggr_redressaldetails@erp b "
		 		+ ", EG_DEPARTMENT@ERP c "
		 		+ "WHERE a.complaintdate >= '01-apr-2009' "
		 		+ "AND a.complaintid = b.complaintid "
		 		+ "AND b.COMPLAINTSTATUSID IN (2,3,4,9) "
		 		+ "AND a.deptid = c.id_dept "
		 		+ "AND (NVL(DEPT_NAME, 0) NOT LIKE 'F%' "
		 		+ "AND DEPT_NAME IS NOT NULL) "
		 		+ "GROUP BY a.deptid,c.DEPT_NAME ORDER BY a.deptid";
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		return result;
	}
	
	@Transactional
	public List<Map<String, Object>> getZoneWiseReopenComplanitCount() {
		
		String SqlQuery = ""; 
		
		SqlQuery = "SELECT deptid, DEPT_NAME"
		 		+ ", NVL(SUM(CASE WHEN complainttype IN (52,146) THEN 1 END), 0) AS NonBURSTLT"
		 		+ ", NVL(SUM(CASE WHEN complainttype IN (1,97) THEN 1 END), 0) AS REOFGAR"
		 		+ ", NVL(SUM(CASE WHEN complainttype IN (22,32,57,59,70,113,114,130,145) THEN 1 END), 0) AS ROAD"
		 		+ ", NVL(SUM(CASE WHEN complainttype IN (18,118) THEN 1 END), 0) AS MOSQUITE"
		 		+ ", NVL(SUM(CASE WHEN complainttype IN (69,138) THEN 1 END), 0) AS WATER"
		 		+ ", NVL(SUM(CASE WHEN complainttype IN (7,99) THEN 1 END), 0) AS REOFDEB"
		 		+ ", NVL(SUM(CASE WHEN complainttype IN (71,152) THEN 1 END), 0) AS TREE"
		 		+ ", NVL(SUM(CASE WHEN complainttype IN (19,119) THEN 1 END), 0) AS DOGS"
		 		+ ", NVL(SUM(CASE WHEN complainttype IN (75,181) THEN 1 END), 0) AS ENCMTN"
		 		+ ", NVL(SUM(CASE WHEN complainttype IN (58,112) THEN 1 END), 0) AS POT"
		 		+ ", NVL(SUM(CASE WHEN complainttype IN (43,166) THEN 1 END), 0) AS PTAX"
		 		+ ", NVL(SUM(CASE WHEN complainttype NOT IN (52,146,1,97,22,32,57,59,70,113,114,130,145,18,118,69,138,7,99,71,152,19,119,75,181,58,112,43,166) THEN 1 END), 0) AS OTHERS"
		 		+ ", COUNT(*) AS Total FROM "
		 		+ "eggr_complaintdetails@erp a"
		 		+ ", eggr_redressaldetails@erp b"
		 		+ ", EG_DEPARTMENT@ERP c "
		 		+ "WHERE a.complaintdate >= '01-apr-2009' "
		 		+ "AND a.complaintid = b.complaintid "
		 		+ "AND b.COMPLAINTSTATUSID IN (9) "
		 		+ "AND a.deptid = c.id_dept "
		 		+ "AND (NVL(DEPT_NAME, 0) NOT LIKE 'F%' "
		 		+ "AND DEPT_NAME IS NOT NULL) "
		 		+ "GROUP BY a.deptid,c.DEPT_NAME ORDER BY a.deptid";
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		return result;
	}
	
	@Transactional
	public List<Map<String, Object>> getComplaintsList() {
		
		String SqlQuery = ""; 
		
		SqlQuery ="SELECT z.name AS zone_name, "
				+ "       dn.name AS division_name, "
				+ "       location.name AS location, "
				+ "       d.dept_name, "
				+ "       TO_CHAR(cd.complaintdate, 'dd-mm-yyyy') AS COMPDATE, "
				+ "       TO_CHAR(cd.complaintdate, 'HH24:MI:SS') AS Time, "
				+ "       cs.statusname AS status, "
				+ "       usr.user_name, "
				+ "       cd.complaintnumber, "
				+ "		  cd.ID_MODE AS \"MODE\", "
				+ "       COALESCE(ct.complainttypename, 'Unspecified') AS complainttypename, "
				+ "       cg.complaintgroupname "
				+ "FROM eggr_complaintdetails@erp cd "
				+ "INNER JOIN eggr_redressaldetails@erp rd ON cd.complaintid = rd.complaintid "
				+ "INNER JOIN eggr_complaintstatus@erp cs ON rd.complaintstatusid = cs.complaintstatusid "
				+ "INNER JOIN eggr_complainttypes@erp ct ON TRIM(cd.complainttype) = ct.complainttypeid "
				+ "INNER JOIN eg_user@erp usr ON rd.redressalofficerid = usr.id_user "
				+ "INNER JOIN eg_department@erp d ON cd.deptid = d.id_dept "
				+ "INNER JOIN eggr_complaintgroup@erp cg ON cg.id_complaintgroup = ct.complaintgroup_id "
				+ "INNER JOIN eg_boundary@erp dn ON dn.id_bndry = cd.bndry "
				+ "INNER JOIN eg_boundary@erp z ON dn.parent = z.id_bndry "
				+ "INNER JOIN eg_boundary@ERP location ON location.id_bndry = cd.locbndryid "
				+ "WHERE (cd.deptid IS NULL OR (cd.deptid < 21 OR cd.deptid > 30)) "
				+ "  AND cd.complaintdate >= TO_DATE('2024-02-23', 'yyyy-MM-dd') "
				+ "  AND cd.complaintdate < (TO_DATE('2024-02-23', 'yyyy-MM-dd') + 1) "
				+ "ORDER BY zone_name, division_name, cd.complaintdate"
				+ "";
		
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		return result;
	}
	
	@Transactional
	public List<Map<String, Object>> getQaQcList() {
		
		String SqlQuery = ""; 
		
		SqlQuery ="select cd.COMPLAINTNUMBER, "
				+ "dep.DEPT_NAME,ct.COMPLAINTTYPENAME,  "
				+ "TO_CHAR(cd.COMPLAINTDATE,'DD-MM-RRRR') COMPLAINTDATE, "
				+ "TO_CHAR(cd.COMPLAINTDATE,'HH:MI:SS AM') COMPLAINTTIME,  "
				+ "cd.COMPLAINTDETAILS,cd.COMPLAINANTFIRSTNAME,  "
				+ "cd.MOBILENUMBER AS COMPLAINANTMOBILENUMBER, usr.USER_NAME, usr.extrafield2 as Mobile_number,  "
				+ "TO_CHAR(c.compl_date,'DD-MM-RRRR HH:MI:SS AM') as COMPLAINTCOMPLETIONDATE,  DECODE ( qa.STATUS,null,'Not Attended','Attended') STATUS   "
				+ "from EGGR_COMPLAINTDETAILS@erp cd ,  "
				+ "eg_department@erp dep,  "
				+ "EGGR_COMPLAINTTYPES@erp ct,  "
				+ "eg_user@erp usr,  "
				+ "eggr_qams_store@erp qa, "
				+ "EGGR_REDRESSALDETAILS@erp rd,  "
				+ "(SELECT c.redressalid, MAX(c.TIMESTAMP) compl_date   "
				+ "FROM eggr_status_tracker@erp c,eggr_redressaldetails@ERP d  "
				+ "WHERE c.statusid in(5,10)  "
				+ "AND TRUNC(c.TIMESTAMP) between TO_DATE('2024-02-23', 'yyyy-MM-dd') and TO_DATE('2024-02-24', 'yyyy-MM-dd')  "
				+ "AND d.COMPLAINTSTATUSID in(5,10)   "
				+ "AND d.REDRESSALID=c.redressalid "
				+ "GROUP BY c.redressalid) c   "
				+ "where cd.DEPTID=dep.ID_DEPT  "
				+ "and cd.COMPLAINTTYPE=ct.COMPLAINTTYPEID   "
				+ "AND rd.COMPLAINTID =cd.COMPLAINTID   "
				+ "AND rd.REDRESSALOFFICERID =usr.id_user  "
				+ "and qa.FDBK_OBJ_REF =cd.COMPLAINTID(+)  "
				+ "and  rd.COMPLAINTSTATUSID not  in(2,3,4,9) and rd.redressalid=c.redressalid(+)  and   "
				+ "TRUNC(cd.COMPLAINTDATE) between TO_DATE('2024-02-23', 'yyyy-MM-dd') and TO_DATE('2024-02-24', 'yyyy-MM-dd') "
				+ "order by dept_name";
		
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		return result;
	}
	
	@Transactional
	public List<Map<String, Object>> getZoneWiseComplaintCountList() {
		
		String SqlQuery = ""; 
		
		SqlQuery ="select * from (SELECT deptid , "
				+ "nvl(sum( case when complainttype in (52,146) then 1 end),0) as \"NonBURSTLT\" , "
				+ "nvl(sum( case when complainttype in (1,97) then 1 end),0) as \"REOFGAR\" ,"
				+ "nvl(sum( case when complainttype in (22,32,57,59,70,113,114,130,145) then 1 end),0) as \"ROAD\" ,"
				+ "nvl(sum( case when complainttype in (18,118) then 1 end),0) as  \"MOSQUITE\" , "
				+ "nvl(sum( case when complainttype in (69,138) then 1 end),0) as  \"WATER\" , "
				+ "nvl(sum( case when complainttype in (7,99) then 1 end),0) as \"REOFDEB\" ,"
				+ "nvl(sum( case when complainttype in (71,152) then 1 end),0) as \"TREE\" , "
				+ "nvl(sum( case when complainttype in (19,119) then 1 end),0) as \"DOGS\" , "
				+ "nvl(sum( case when complainttype in (75,181) then 1 end),0) as \"ENCMTN\" ,"
				+ "nvl(sum( case when complainttype in (58,112) then 1 end),0) as \"POT\" , "
				+ "nvl(sum( case when complainttype in (43,166) then 1 end),0) as \"PTAX\" , "
				+ "nvl(sum( case when complainttype not in (52,1,22,32,57,59,70,18,69,7,71,19,75,58,43,146,97,113,114,130,145,118,138,99,152,119,181,112,166) then 1 end),0) as \"OTHERS\" , "
				+ "COUNT(*) Total  FROM eggr_complaintdetails@erp a, eggr_redressaldetails@erp b "
				+ "WHERE a.complaintid=b.complaintid "
				+ "and b.COMPLAINTSTATUSID IN(2,3,4,9) "
				+ "group by a.deptid ) a "
				+ "inner join EG_DEPARTMENT@ERP c on a.deptid=c.id_dept  "
				+ "left outer join (  SELECT deptid , "
				+ "nvl(sum( case when sysdate-complaintdate>90 and b.COMPLAINTSTATUSID IN(2,3,4,9) then 1 end),'0') as days_90_pen , "
				+ "nvl(sum( case when sysdate-complaintdate>60 and sysdate-complaintdate<=90 and b.COMPLAINTSTATUSID IN(2,3,4,9) then 1 end),'0') as days_61_90_pen ,"
				+ "nvl(sum( case when sysdate-complaintdate>30 and sysdate-complaintdate<=60 and b.COMPLAINTSTATUSID IN(2,3,4,9) then 1 end),'0') as days_31_60_pen , "
				+ "nvl(sum( case when sysdate-complaintdate>15 and sysdate-complaintdate<=30 and b.COMPLAINTSTATUSID IN(2,3,4,9) then 1 end),'0') as days_16_30_pen , "
				+ "nvl(sum( case when sysdate-complaintdate<=15 and b.COMPLAINTSTATUSID IN(2,3,4,9) then 1 end),'0') as days_0_15_pen , "
				+ "nvl(count(case when b.COMPLAINTSTATUSID IN(2,3,4,9) then 1 end),'0') as Total_pen "
				+ "FROM eggr_complaintdetails@erp a, eggr_redressaldetails@erp b "
				+ " WHERE a.complaintid=b.complaintid  "
				+ "AND EXISTS (SELECT 1 FROM EG_DEPARTMENT@ERP c "
				+ "WHERE a.deptid = c.id_dept "
				+ "AND (NVL(c.id_dept, 0) NOT LIKE 'F%' "
				+ "AND c.id_dept IS NOT NULL)) group by a.deptid ) d on d.deptid=c.id_dept "
				+ "where (NVL(DEPT_NAME,0) NOT LIKE 'F%' AND DEPT_NAME IS NOT NULL) AND (a.deptid>=32 AND a.deptid<=46)"
				+ "order by a.deptid";
		
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		return result;
	}
	
	@Transactional
	public List<Map<String, Object>> getZoneWiseOfficerComplaintCountList(String deptid, String complainttype) {
		
		String SqlQuery = ""; 
		String where_1="";
		if(!complainttype.equals("all")) {
			where_1 = "and cd.complainttype in ("+complainttype+") ";
		}
		
		SqlQuery ="Select c.dept_name,a.iduser,a.user_name,nvl(b.extrafield2,' ') MOBILENO,a.nos,a.deptid from "
				+ "(SELECT  cd.deptid, u.user_name, COUNT (cd.complaintid) nos, max(u.id_user) iduser "
				+ "FROM eggr_complaintdetails@erp cd, eggr_redressaldetails@erp rd, eg_user@erp u "
				+ "WHERE cd.complaintid = rd.complaintid "
				+ "AND rd.redressalofficerid = u.id_user and complaintdate >= '25-oct-2010' "
				+ "and rd.COMPLAINTSTATUSID IN(2,3,4,9) "
				+ where_1 +""
				+ "AND cd.deptid = "+deptid+" "
				+ "GROUP BY  cd.deptid, u.user_name "
				+ "ORDER BY cd.deptid,u.user_name) a , eg_user@erp b,eg_department@erp c "
				+ "where  a.deptid="+deptid+" AND(a.deptid=c.id_dept(+) and a.user_name=b.user_name ) "
				+ "order by c.dept_name,a.user_name";
		
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		return result;
	}
	
	@Transactional
	public List<Map<String, Object>> getZoneWiseOfficerComplaintList(String deptid, String complainttype, String redressalofficerid) {
		
		String SqlQuery = ""; 
		String where_1="";
		if(!complainttype.equals("all")) {
			where_1 = "and a.complainttype in ("+complainttype+") ";
		}
		
		SqlQuery ="SELECT a.*, NVL(TO_CHAR(a.complaintdate, 'dd-mm-rrrr'), ' ') compdate,c.DEPT_NAME, u.user_name, ct.COMPLAINTTYPEName "
				+ "FROM eggr_complaintdetails@erp a, "
				+ "EG_DEPARTMENT@ERP c, "
				+ "eggr_redressaldetails@erp b, "
				+ "eg_user@erp u, "
				+ "EGGR_COMPLAINTTYPES@erp ct "
				+ "WHERE a.deptid = c.id_dept "
				+ "AND a.complainttype = ct.COMPLAINTTYPEID "
				+ "AND (NVL(DEPT_NAME, '0') NOT LIKE 'F%' AND DEPT_NAME IS NOT NULL) AND a.complaintid = b.complaintid "
				+ "AND complaintdate >= TO_DATE('25-Oct-2010', 'DD-Mon-YYYY') "
				+ "AND u.id_user = b.redressalofficerid "
				+ "AND b.COMPLAINTSTATUSID IN (2, 3, 4, 9) "
				+ where_1 +""
				+ "AND a.deptid = "+deptid+" "
				+ "AND b.redressalofficerid = "+redressalofficerid+" "
				+ "ORDER BY user_name, a.complaintdate";
		
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		
		return result;
	}
	
	
	@Transactional
	public List<Map<String, Object>> getZoneWiseWithoutReopenComplaintCountList() {
		
		String SqlQuery = ""; 
		
		SqlQuery ="select * from (SELECT deptid , "
				+ "nvl(sum( case when complainttype in (52,146) then 1 end),0) as \"NonBURSTLT\" , "
				+ "nvl(sum( case when complainttype in (1,97) then 1 end),0) as \"REOFGAR\" ,"
				+ "nvl(sum( case when complainttype in (22,32,57,59,70,113,114,130,145) then 1 end),0) as \"ROAD\" ,"
				+ "nvl(sum( case when complainttype in (18,118) then 1 end),0) as  \"MOSQUITE\" , "
				+ "nvl(sum( case when complainttype in (69,138) then 1 end),0) as  \"WATER\" , "
				+ "nvl(sum( case when complainttype in (7,99) then 1 end),0) as \"REOFDEB\" ,"
				+ "nvl(sum( case when complainttype in (71,152) then 1 end),0) as \"TREE\" , "
				+ "nvl(sum( case when complainttype in (19,119) then 1 end),0) as \"DOGS\" , "
				+ "nvl(sum( case when complainttype in (75,181) then 1 end),0) as \"ENCMTN\" ,"
				+ "nvl(sum( case when complainttype in (58,112) then 1 end),0) as \"POT\" , "
				+ "nvl(sum( case when complainttype in (43,166) then 1 end),0) as \"PTAX\" , "
				+ "nvl(sum( case when complainttype not in (52,1,22,32,57,59,70,18,69,7,71,19,75,58,43,146,97,113,114,130,145,118,138,99,152,119,181,112,166) then 1 end),0) as \"OTHERS\" , "
				+ "COUNT(*) Total  FROM eggr_complaintdetails@erp a, eggr_redressaldetails@erp b "
				+ "WHERE a.complaintid=b.complaintid "
				+ "and b.COMPLAINTSTATUSID IN(2,3,4) "
				+ "group by a.deptid ) a "
				+ "inner join EG_DEPARTMENT@ERP c on a.deptid=c.id_dept  "
				+ "left outer join (  SELECT deptid , "
				+ "nvl(sum( case when sysdate-complaintdate>90 and b.COMPLAINTSTATUSID IN(2,3,4) then 1 end),'0') as days_90_pen , "
				+ "nvl(sum( case when sysdate-complaintdate>60 and sysdate-complaintdate<=90 and b.COMPLAINTSTATUSID IN(2,3,4) then 1 end),'0') as days_61_90_pen ,"
				+ "nvl(sum( case when sysdate-complaintdate>30 and sysdate-complaintdate<=60 and b.COMPLAINTSTATUSID IN(2,3,4) then 1 end),'0') as days_31_60_pen , "
				+ "nvl(sum( case when sysdate-complaintdate>15 and sysdate-complaintdate<=30 and b.COMPLAINTSTATUSID IN(2,3,4) then 1 end),'0') as days_16_30_pen , "
				+ "nvl(sum( case when sysdate-complaintdate<=15 and b.COMPLAINTSTATUSID IN(2,3,4) then 1 end),'0') as days_0_15_pen , "
				+ "nvl(count(case when b.COMPLAINTSTATUSID IN(2,3,4) then 1 end),'0') as Total_pen "
				+ "FROM eggr_complaintdetails@erp a, eggr_redressaldetails@erp b "
				+ " WHERE a.complaintid=b.complaintid  "
				+ "AND EXISTS (SELECT 1 FROM EG_DEPARTMENT@ERP c "
				+ "WHERE a.deptid = c.id_dept "
				+ "AND (NVL(c.id_dept, 0) NOT LIKE 'F%' "
				+ "AND c.id_dept IS NOT NULL)) group by a.deptid ) d on d.deptid=c.id_dept "
				+ "where (NVL(DEPT_NAME,0) NOT LIKE 'F%' AND DEPT_NAME IS NOT NULL) AND (a.deptid>=32 AND a.deptid<=46)"
				+ "order by a.deptid";
		
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		return result;
	}
	
	@Transactional
	public List<Map<String, Object>> getZoneWiseOfficerWithoutReopenComplaintCountList(String deptid, String complainttype) {
		
		String SqlQuery = ""; 
		String where_1="";
		if(!complainttype.equals("all")) {
			where_1 = "and cd.complainttype in ("+complainttype+") ";
		}
		
		SqlQuery ="Select c.dept_name,a.iduser,a.user_name,nvl(b.extrafield2,' ') MOBILENO,a.nos,a.deptid from "
				+ "(SELECT  cd.deptid, u.user_name, COUNT (cd.complaintid) nos, max(u.id_user) iduser "
				+ "FROM eggr_complaintdetails@erp cd, eggr_redressaldetails@erp rd, eg_user@erp u "
				+ "WHERE cd.complaintid = rd.complaintid "
				+ "AND rd.redressalofficerid = u.id_user and complaintdate >= '25-oct-2010' "
				+ "and rd.COMPLAINTSTATUSID IN(2,3,4) "
				+ where_1 +""
				+ "AND cd.deptid = "+deptid+" "
				+ "GROUP BY  cd.deptid, u.user_name "
				+ "ORDER BY cd.deptid,u.user_name) a , eg_user@erp b,eg_department@erp c "
				+ "where  a.deptid="+deptid+" AND(a.deptid=c.id_dept(+) and a.user_name=b.user_name ) "
				+ "order by c.dept_name,a.user_name";
		
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		return result;
	}
	
	@Transactional
	public List<Map<String, Object>> getZoneWiseOfficerWithoutReopenComplaintList(String deptid, String complainttype, String redressalofficerid) {
		
		String SqlQuery = ""; 
		String where_1="";
		if(!complainttype.equals("all")) {
			where_1 = "and a.complainttype in ("+complainttype+") ";
		}
		
		SqlQuery ="SELECT a.*, NVL(TO_CHAR(a.complaintdate, 'dd-mm-rrrr'), ' ') compdate,c.DEPT_NAME, u.user_name, ct.COMPLAINTTYPEName "
				+ "FROM eggr_complaintdetails@erp a, "
				+ "EG_DEPARTMENT@ERP c, "
				+ "eggr_redressaldetails@erp b, "
				+ "eg_user@erp u, "
				+ "EGGR_COMPLAINTTYPES@erp ct "
				+ "WHERE a.deptid = c.id_dept "
				+ "AND a.complainttype = ct.COMPLAINTTYPEID "
				+ "AND (NVL(DEPT_NAME, '0') NOT LIKE 'F%' AND DEPT_NAME IS NOT NULL) AND a.complaintid = b.complaintid "
				+ "AND complaintdate >= TO_DATE('25-Oct-2010', 'DD-Mon-YYYY') "
				+ "AND u.id_user = b.redressalofficerid "
				+ "AND b.COMPLAINTSTATUSID IN (2, 3, 4) "
				+ where_1 +""
				+ "AND a.deptid = "+deptid+" "
				+ "AND b.redressalofficerid = "+redressalofficerid+" "
				+ "ORDER BY user_name, a.complaintdate";
		
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		
		return result;
	}
	
	@Transactional
	public List<Map<String, Object>> getComplaintDetails(String complaintNumber) {
		
		String SqlQuery = ""; 
		
		SqlQuery ="SELECT a.*, NVL(TO_CHAR(a.complaintdate, 'dd-mm-rrrr'), ' ') compdate,c.DEPT_NAME, u.user_name, ct.COMPLAINTTYPEName "
				+ "FROM eggr_complaintdetails@erp a, "
				+ "EG_DEPARTMENT@ERP c, "
				+ "eggr_redressaldetails@erp b, "
				+ "eg_user@erp u, "
				+ "EGGR_COMPLAINTTYPES@erp ct "
				+ "WHERE a.deptid = c.id_dept "
				+ "AND a.complainttype = ct.COMPLAINTTYPEID "
				+ "AND (NVL(DEPT_NAME, '0') NOT LIKE 'F%' AND DEPT_NAME IS NOT NULL) AND a.complaintid = b.complaintid "
				+ "AND complaintdate >= TO_DATE('25-Oct-2010', 'DD-Mon-YYYY') "
				+ "AND u.id_user = b.redressalofficerid "
				+ "AND a.COMPLAINTNUMBER = '"+complaintNumber+"' "
				+ "ORDER BY user_name, a.complaintdate";
		
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		
		// Iterate over the result list
		for (Map<String, Object> row : result) {
		    // Iterate over each key-value pair in the map
		    for (Map.Entry<String, Object> entry : row.entrySet()) {
		        // Check if the value is null
		        if (entry.getValue() == null) {
		            // If the value is null, replace it with an empty string
		            entry.setValue("");
		        }
		    }
		}
		
		return result;
	}
	
	
	@Transactional
	public List<Map<String, Object>> syncERPtoLocal() {
		
		String SqlQuery = ""; 
		
		SqlQuery ="SELECT count(cd.complaintid) total FROM eggr_complaintdetails@erp cd";
		
		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		//List<Map<String, Object>> result = jdbcPGRTemplate.queryForList(SqlQuery);
		return result;
	}
	
}
