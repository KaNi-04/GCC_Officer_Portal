package in.gov.chennaicorporation.gccoffice.greencommittee.service;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(@Qualifier("mysqlGreenCommitteeDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public int getApplicationsCount()
	{
		String sql="SELECT COUNT(*) FROM reg_details WHERE is_active=1";
		
		try {
			return jdbcTemplate.queryForObject(sql,Integer.class);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return 0;
		}
		
		
	}
	
	
	public int getDGCcompletedCount() {

	    try {

	        String sql =
	            "SELECT COUNT(*) AS total " +
	            "FROM ( " +
	            "   SELECT ref_id " +
	            "   FROM ( " +
	            "       SELECT ref_id, inspection_by, MAX(cdate) AS latest_date " +
	            "       FROM inspection_data " +
	            "       WHERE isactive = 1 " +
	            "       GROUP BY ref_id, inspection_by " +
	            "   ) AS t " +
	            "   GROUP BY ref_id " +
	            "   HAVING COUNT(DISTINCT inspection_by) = 3 " +
	            ") AS final";

	        return jdbcTemplate.queryForObject(sql, Integer.class);

	    } catch (Exception e) {
	        System.err.println("Error in getCompletedRefIdCount(): " + e.getMessage());
	        e.printStackTrace();
	        return 0;
	    }
	}
	
	public int getCommitteeCompleted()
	{
		String sql="SELECT COUNT(*) FROM committee_data WHERE isactive=1";
		
		try {
			return jdbcTemplate.queryForObject(sql,Integer.class);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return 0;
		}
	}

}
