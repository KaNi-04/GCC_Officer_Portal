package in.gov.chennaicorporation.gccoffice.works.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import javax.sql.DataSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class ERPEstimateService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplate oraclejdbcTemplate;

    @Autowired
    public void setDataSource(@Qualifier("mysqlWorksDataSource") DataSource dataSource,
                              @Qualifier("oracleDataSource") DataSource oracleDataSource) {

        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.oraclejdbcTemplate = new JdbcTemplate(oracleDataSource);
    }

    @Autowired
    private Environment environment;

    private String getUserAccess(String userid) {
    	String ward = "0";
    	String department="0";
    	
		String sql = "SELECT `id`, `userid`, `zone`, `ward`, `department` FROM `works_access` WHERE `userid`=? LIMIT 1";
		System.out.println("SELECT `id`, `userid`, `zone`, `ward` FROM `works_access` WHERE `userid`="+userid+" LIMIT 1");
		List<Map<String, Object>> taskresult = jdbcTemplate.queryForList(sql, userid);
		if(!taskresult.isEmpty()) {
			ward = ""+taskresult.get(0).get("ward");
			department = ""+taskresult.get(0).get("department");
		}
		return ward+"_"+department;
	}
    
    @Transactional
	public String getDepartmentName(String ids) {
    	String dataarray="0";
    	String sqlQuery ="SELECT GROUP_CONCAT(CONCAT(\"'\", `dept_name`, \"'\") SEPARATOR ',') AS `dept_name` FROM `department_master` WHERE `id` IN (?)";
    	List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQuery, ids);
    	if(!result.isEmpty()) {
    		dataarray = ""+result.get(0).get("dept_name");
    	}
    	return dataarray;
    }
    
    @Transactional
	public String getDepartmentId(String names) {
    	String ids="0";
    	String sqlQuery ="SELECT GROUP_CONCAT( `id` SEPARATOR ',') AS `id` FROM `department_master` WHERE `dept_name` IN (?)";
    	List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQuery, names);
    	if(!result.isEmpty()) {
    		ids = ""+result.get(0).get("id");
    	}
    	return ids;
    }
    
    public List<Map<String, Object>> getAllWorks(String LoginUserId) { 
    	
    	String accessData = getUserAccess(LoginUserId);
    	String where="";
    	
    	// Split the string using "_"
        String[] parts = accessData.split("_");
        
    	if(!parts[0].equals("all")) {
    		where =" cd.`WARD` IN ("+parts[0]+") ";
    	}
    	if(!parts[1].equals("all")) {
    		if(!where.equals(""))
    		{
    			where = where + "AND cd.`DEPARTMENT_NAME` IN ("+getDepartmentName(parts[1])+")";
    		}
    		else {
    			where = where + "cd.`DEPARTMENT_NAME` IN ("+getDepartmentName(parts[1])+")";
    		}
    	}
    	
    	if(!where.equals("")) {
    		where = " WHERE "+ where;
    	}
    	
    	System.out.println("where = " + where);
    	
        String sql = "select *, wt.code as category, tw.code as subcategory, sm.name as scheme, dm.dept_name as department, fm.name as fundsourcename " +
                "from works_data_erp cd " +
                "left join gcc_works_status.workstype_master wt on wt.id = cd.CATEGORY " +
                "left join gcc_works_status.typeofwork_master tw on tw.id = cd.SUB_CATEGORY " +
                "left join gcc_works_status.scheme_master sm on sm.id = cd.SCHEME " +
                "left join gcc_works_status.department_master dm on dm.id = cd.ZONE " +
                "left join gcc_works_status.fundsource_master fm on fm.id = cd.FUND_SOURCE"
                + where;
        return jdbcTemplate.queryForList(sql);
    }

    public Map<String, Object> getERPWorksDetails(String estNo) { // changed
        String sql = "select *, wt.code as category, tw.code as subcategory, sm.name as scheme, dm.dept_name as department, fm.name as fundsourcename " +
                "from works_data_erp cd " +
                "left join gcc_works_status.workstype_master wt on wt.id = cd.CATEGORY " +
                "left join gcc_works_status.typeofwork_master tw on tw.id = cd.SUB_CATEGORY " +
                "left join gcc_works_status.scheme_master sm on sm.id = cd.SCHEME " +
                "left join gcc_works_status.department_master dm on dm.id = cd.ZONE " +
                "left join gcc_works_status.fundsource_master fm on fm.id = cd.FUND_SOURCE " +
                "where cd.ESTIMATE_NO = ? LIMIT 1";
        return jdbcTemplate.queryForMap(sql, estNo);
    }

    public Map<String, Object> getERPWorksDetailslist(String estimateNo) {
        System.out.println(estimateNo);
        String sql = "select * from works_data_erp where `ESTIMATE_NO` = ?";
        return jdbcTemplate.queryForMap(sql, estimateNo);
    }

    public List<Map<String, Object>> getFilteredERpWorksList(
    		String loginId,
    		String finYear, String oValue) {
        StringBuilder sql = new StringBuilder("SELECT * FROM works_data_erp");
        System.out.println("hai");
        List<Object> params = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        // Load only status = 0 
        conditions.add(" `isuserupdate` = ?");
        params.add(0);
        //////////////////////
        
        String accessData = getUserAccess(loginId);
    	String where="";
    	
    	// Split the string using "_"
        String[] parts = accessData.split("_");
        
    	if(!parts[0].equals("all")) {
    		where =" `WARD` IN ("+parts[0]+") ";
    	}
    	
    	if(!parts[1].equals("all")) {
    		if(!where.equals(""))
    		{
    			where = where + "AND `DEPARTMENT_NAME` IN ("+getDepartmentName(parts[1])+")";
    		}
    		else {
    			where = where + "`DEPARTMENT_NAME` IN ("+getDepartmentName(parts[1])+")";
    		}
    	}
    	
    	if(!where.equals("")) {
    		conditions.add(" " + where + " ");
    	}
    	
    	System.out.println("where = " + where);
    	
        /*
         * if (zone != null && !zone.isEmpty()) { conditions.add("zone = ?");
         * params.add(zone); }
         */
    	
        if (finYear != null && !finYear.isEmpty()) {
        	conditions.add(" ESTIMATE_NO LIKE ? ");
            params.add("%" + finYear + "%");
        }
        
        if (oValue != null && !oValue.isEmpty()) {
            switch (oValue) {
                case "max50":
                    conditions.add(" EST_VAL_WITH_OH <= ?");
                    params.add(5000000);
                    break;
                case "50to75":
                    conditions.add(" EST_VAL_WITH_OH > ? AND EST_VAL_WITH_OH <= ?");
                    params.add(5000000);
                    params.add(7500000);
                    break;
                case "75to99":
                    conditions.add(" EST_VAL_WITH_OH > ? AND EST_VAL_WITH_OH <= ?");
                    params.add(7500000);
                    params.add(9900000);
                    break;
                case "min1c":
                    conditions.add(" EST_VAL_WITH_OH >= ?");
                    params.add(10000000);
                    break;
            }
        }
        /*
        if (fundsource != null && !fundsource.isEmpty()) {
            conditions.add(" fund_source = ?");
            params.add(fundsource);
        }
        if (fromDate != null && toDate != null) {
            conditions.add("date(asdate) between ? and ?");
            params.add(fromDate);
            params.add(toDate);
        }
        /*
         * if (toDate != null) { conditions.add("asdate <= ?"); params.add(toDate); }
         */

        /*
         * if (filterType != null && filterType.equals("erpWorks")) {
         * conditions.add("abs_est_number not like'M%'"); }
         *
         * if (filterType != null && filterType.equals("nonErpWorks")) {
         * conditions.add("abs_est_number like'M%'"); }
         */
        /*
        if (estno != null && !estno.isEmpty()) {
            conditions.add("abs_est_number = ?");
            params.add(estno);
        }
        */
        
        // Append conditions only if there are any
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }
       
        //System.out.println("SELECT * FROM works_data_erp WHERE  `isuserupdate` = 0 AND  ESTIMATE_NO LIKE '%" + finYear + "%'  AND  EST_VAL_WITH_OH >= 5000000");
        		
        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    public List<Map<String, Object>> getFilteredERpWorksListByLogin(
    		String loginId,
    		String finYear, String oValue) {
        StringBuilder sql = new StringBuilder("SELECT ew.`estid`, ew.`estimate_no`, DATE(ew.`estimate_date`) AS `estimate_date`,"
        		+ "ew.`zone`, ew.`ward`, dm.dept_name as `department`, wm.name as `category`, "
        		+ "trim(tm.description) as `sub_category`, ew.`project_name`, ew.`estimation_amount`, ew.`est_val_with_oh` FROM erp_works ew "
        		+ "LEFT JOIN department_master dm ON dm.id = ew.department "
        		+ "LEFT JOIN workstype_master wm ON wm.id = ew.category "
        		+ "LEFT JOIN typeofwork_master tm ON  tm.id = ew.sub_category");
        //System.out.println("hai");
        List<Object> params = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        // Load only status = 0 
        //conditions.add(" `isuserupdate` = ?");
        //params.add(0);
        //////////////////////
        
        String accessData = getUserAccess(loginId);
    	
        String where="ew.`nonerp`=0 ";
    	
    	// Split the string using "_"
        String[] parts = accessData.split("_");
        
    	if(!parts[0].equals("all")) {
    		where = where + " AND ew.`ward` IN ("+parts[0]+") ";
    	}
    	if(!parts[1].equals("all")) {
    		if(!where.equals(""))
    		{
    			where = where + " AND `dm`.`dept_name` IN ("+getDepartmentName(parts[1])+")";
    		}
    		else {
    			where = where + " AND`dm`.`dept_name` IN ("+getDepartmentName(parts[1])+")";
    		}
    	}
    	
    	if(!where.equals("")) {
    		conditions.add(" " + where + " ");
    	}
    	
    	System.out.println("where = " + where);
    	
        /*
         * if (zone != null && !zone.isEmpty()) { conditions.add("zone = ?");
         * params.add(zone); }
         */
    	if (finYear != null && !finYear.isEmpty()) {
        	conditions.add(" ew.estimate_no LIKE ? ");
            params.add("%" + finYear + "%");
        }
        
        if (oValue != null && !oValue.isEmpty()) {
            switch (oValue) {
                case "max50":
                    conditions.add(" ew.`est_val_with_oh` <= ?");
                    params.add(5000000);
                    break;
                case "50to75":
                    conditions.add(" ew.`est_val_with_oh` > ? AND ew.`est_val_with_oh` <= ?");
                    params.add(5000000);
                    params.add(7500000);
                    break;
                case "75to99":
                    conditions.add(" ew.`est_val_with_oh` > ? AND ew.`est_val_with_oh` <= ?");
                    params.add(7500000);
                    params.add(9900000);
                    break;
                case "min1c":
                    conditions.add(" ew.`est_val_with_oh` >= ?");
                    params.add(10000000);
                    break;
            }
        }
        /*
        if (ward != null && !ward.isEmpty()) {
            conditions.add(" wardname = ?");
            params.add(ward);
        }
        if (department != null && !department.isEmpty()) {
            conditions.add(" zonename = ?");
            params.add(department);
        }
        if (fundsource != null && !fundsource.isEmpty()) {
            conditions.add(" fund_source = ?");
            params.add(fundsource);
        }
        if (fromDate != null && toDate != null) {
            conditions.add("date(asdate) between ? and ?");
            params.add(fromDate);
            params.add(toDate);
        }
    
        if (estno != null && !estno.isEmpty()) {
            conditions.add("abs_est_number = ?");
            params.add(estno);
        }
        */
        // Append conditions only if there are any
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }
        
        
        //System.out.println("sql:  " + sql + "fromDate: " + fromDate + "toDate: " + toDate);
        //System.out.println(sql.toString());
        // return jdbcTemplate.queryForList(sql.toString()+" LIMIT 100", params.toArray());
        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }
    
    public Map<String, Object> getNonERPWorksDetails(String estimateNo) { // changed
        String sql = "select *, wt.code as category, tw.code as subcategory, sm.name as scheme, dm.dept_name as department, fm.name as fundsourcename " +
                "from tbl_cmdashboard cd " +
                "left join gcc_works_status.workstype_master wt on wt.id = cd.workcategory " +
                "left join gcc_works_status.typeofwork_master tw on tw.id = cd.typeofwork " +
                "left join gcc_works_status.scheme_master sm on sm.id = cd.schemename " +
                "left join gcc_works_status.department_master dm on dm.id = cd.zonename " +
                "left join gcc_works_status.fundsource_master fm on fm.id = cd.fundsource " +
                "where cd.abs_est_number = ? ";
        return jdbcTemplate.queryForMap(sql, estimateNo);
    }
}
