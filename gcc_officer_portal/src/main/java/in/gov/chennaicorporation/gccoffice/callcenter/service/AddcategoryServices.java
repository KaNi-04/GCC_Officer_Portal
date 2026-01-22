package in.gov.chennaicorporation.gccoffice.callcenter.service;

import java.sql.ResultSet;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Locale.Category;
import java.util.Map;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import java.sql.PreparedStatement;
import java.sql.Statement;


@Service
public class AddcategoryServices {
	
private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(@Qualifier("mysql1913CampaignDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	    
//	public void saveCategoryDetails(Map<String, Object> categoryData) {
//	    String sql = "INSERT INTO category (category_name, isactive, isdelete, created_date) VALUES (?, 1, 0, CURRENT_TIMESTAMP)";
//
//	    // Ensure that the key "categoryname" matches exactly what is being sent from the frontend
//	    String categoryName = (String) categoryData.get("categoryname");
//
//	    // Check if categoryName is null or empty and handle it properly
//	    if (categoryName == null || categoryName.trim().isEmpty()) {
//	        throw new IllegalArgumentException("Category name cannot be null or empty");
//	    }
//
//	    jdbcTemplate.update(sql, categoryName);
//	}
	
	
	@Transactional
	public void saveCategoryDetails(Map<String, Object> categoryData) {
	    String categoryName = (String) categoryData.get("categoryname");
	    List<String> columns = (List<String>) categoryData.get("columns");

	    if (categoryName == null || categoryName.trim().isEmpty()) {
	        throw new IllegalArgumentException("Category name cannot be null or empty");
	    }

	    if (columns == null || columns.isEmpty() || columns.contains("")) {
	        throw new IllegalArgumentException("At least one column name is required");
	    }

	    // Insert into category table and get the generated category_id
	    String categorySql = "INSERT INTO category (category_name, isactive, isdelete, created_date) VALUES (?, 1, 0, CURRENT_TIMESTAMP)";
	    KeyHolder keyHolder = new GeneratedKeyHolder();

	    jdbcTemplate.update(connection -> {
	        PreparedStatement ps = connection.prepareStatement(categorySql, Statement.RETURN_GENERATED_KEYS);
	        ps.setString(1, categoryName);
	        return ps;
	    }, keyHolder);

	    Integer categoryId = keyHolder.getKey().intValue(); // Extract generated category_id

	    // Insert columns into category_columns table
	    String columnSql = "INSERT INTO category_columns (category_id, column_name, isactive, isdelete, created_date) VALUES (?, ?, 1, 0, CURRENT_TIMESTAMP)";
	    jdbcTemplate.batchUpdate(columnSql, columns, columns.size(),
	            (PreparedStatement ps, String column) -> {
	                ps.setInt(1, categoryId);
	                ps.setString(2, column);
	            });
	}

	
	@Transactional
	public List<Map<String, Object>> getAllCategories() {
		
		String SqlQuery = "SELECT  category_id,category_name FROM category where isactive=1 and isdelete=0";

		List<Map<String, Object>> result = jdbcTemplate.queryForList(SqlQuery);
		return result;	
	}
	
	public Map<String, Object> showCategory(int categoryId) {
	    String categorySql = "SELECT category_id, category_name FROM category WHERE category_id = ? AND isactive = 1 AND isdelete = 0";
	    Map<String, Object> category;
	    try {
	        category = jdbcTemplate.queryForMap(categorySql, categoryId);
	    } catch (EmptyResultDataAccessException e) {
	        return Collections.emptyMap();
	    }

	    String columnsSql = "SELECT id,column_name FROM category_columns WHERE category_id = ? AND isactive = 1 AND isdelete = 0";
	    List<Map<String,Object>> columns = jdbcTemplate.queryForList(columnsSql,categoryId);
	    category.put("columns", columns);
	    return category;
	}
	
	@Transactional
	public int updateCategoryWithColumns(int categoryId, String categoryName, List<String> columns) {
	    if (categoryName == null || categoryName.trim().isEmpty()) {
	        throw new IllegalArgumentException("Category name cannot be empty");
	    }
	    if (columns == null || columns.size() < 2 || columns.contains("")) {
	        throw new IllegalArgumentException("At least two column names are required");
	    }

	    String updateCategorySql = "UPDATE category SET category_name = ? WHERE category_id = ? AND isactive = 1 AND isdelete = 0";
	    int categoryUpdateCount = jdbcTemplate.update(updateCategorySql, categoryName, categoryId);

	    if (categoryUpdateCount == 0) {
	        throw new IllegalArgumentException("Category not found or update failed.");
	    }

	    String deleteColumnsSql = "DELETE FROM category_columns WHERE category_id = ? AND isactive = 1 AND isdelete = 0";
	    jdbcTemplate.update(deleteColumnsSql, categoryId);

	    String insertColumnsSql = "INSERT INTO category_columns (category_id, column_name, isactive, isdelete, created_date) VALUES (?, ?, 1, 0, CURRENT_TIMESTAMP)";
	    jdbcTemplate.batchUpdate(insertColumnsSql, columns, columns.size(), (PreparedStatement ps, String column) -> {
	        ps.setInt(1, categoryId);
	        ps.setString(2, column);
	    });

	    return categoryUpdateCount;
	}


//	@Transactional
//	public int updateCategoryWithColumns(int categoryId, String categoryName, List<String> columns) {
//	    if (categoryName == null || categoryName.trim().isEmpty()) {
//	        throw new IllegalArgumentException("Category name cannot be empty");
//	    }
//	    if (columns == null || columns.isEmpty() || columns.contains("")) {
//	        throw new IllegalArgumentException("At least one column name is required");
//	    }
//
//	    String updateCategorySql = "UPDATE category SET category_name = ? WHERE category_id = ? AND isactive = 1 AND isdelete = 0";
//	    int categoryUpdateCount = jdbcTemplate.update(updateCategorySql, categoryName, categoryId);
//
//	    if (categoryUpdateCount == 0) {
//	        throw new IllegalArgumentException("Category not found or update failed.");
//	    }
//
//	    String deleteColumnsSql = "DELETE FROM category_columns WHERE category_id = ? AND isactive = 1 AND isdelete = 0";
//	    jdbcTemplate.update(deleteColumnsSql, categoryId);
//
//	    String insertColumnsSql = "INSERT INTO category_columns (category_id, column_name, isactive, isdelete, created_date) VALUES (?, ?, 1, 0, CURRENT_TIMESTAMP)";
//	    jdbcTemplate.batchUpdate(insertColumnsSql, columns, columns.size(), (PreparedStatement ps, String column) -> {
//	        ps.setInt(1, categoryId);
//	        ps.setString(2, column);
//	    });
//
//	    return categoryUpdateCount;
//	}

	

	
//	// Method to show a category by its ID
//    public Map<String, Object> showCategory(int categoryId) {
//        // Fetch category details
//        String categorySql = "SELECT category_id, category_name FROM category WHERE category_id = ? AND isactive = 1 AND isdelete = 0";
//        Map<String, Object> category;
//        try {
//            category = jdbcTemplate.queryForMap(categorySql, categoryId);
//        } catch (EmptyResultDataAccessException e) {
//            return null; // If no category found
//        }
//
//        // Fetch category columns
//        String columnsSql = "SELECT column_name FROM category_columns WHERE category_id = ? AND isactive = 1 AND isdelete = 0";
//        List<String> columns = jdbcTemplate.queryForList(columnsSql, String.class, categoryId);
//
//        // Add columns to response
//        category.put("columns", columns);
//        return category;
//    }
//	
//
	
//	@Transactional
//	public int updateCategoryWithColumns(int categoryId, String categoryName, List<String> columns) {
//	    // Validate input
//	    if (categoryName == null || categoryName.trim().isEmpty()) {
//	        throw new IllegalArgumentException("Category name cannot be empty");
//	    }
//	    if (columns == null || columns.isEmpty() || columns.contains("")) {
//	        throw new IllegalArgumentException("At least one column name is required");
//	    }
//
//	    // Update category name
//	    String updateCategorySql = "UPDATE category SET category_name = ? WHERE category_id = ? AND isactive = 1 AND isdelete = 0";
//	    int categoryUpdateCount = jdbcTemplate.update(updateCategorySql, categoryName, categoryId);
//
//	    if (categoryUpdateCount == 0) {
//	        throw new IllegalArgumentException("Category not found or update failed.");
//	    }
//
//	    // Remove existing category columns before inserting new ones
//	    String deleteColumnsSql = "DELETE FROM category_columns WHERE category_id = ?";
//	    jdbcTemplate.update(deleteColumnsSql, categoryId);
//
//	    // Insert updated columns
//	    String insertColumnsSql = "INSERT INTO category_columns (category_id, column_name, isactive, isdelete, created_date) VALUES (?, ?, 1, 0, CURRENT_TIMESTAMP)";
//	    jdbcTemplate.batchUpdate(insertColumnsSql, columns, columns.size(),
//	            (PreparedStatement ps, String column) -> {
//	                ps.setInt(1, categoryId);
//	                ps.setString(2, column);
//	            });
//
//	    return categoryUpdateCount;
//	}

	
//	// Method to update a category's name by its ID
//	public int updateCategory(int categoryId, String categoryName) {
//	    String sql = "UPDATE category SET category_name = ? WHERE category_id = ? AND isactive = 1 AND isdelete = 0";
//	    return jdbcTemplate.update(sql, categoryName, categoryId);
//	}



	
    // Method to delete a category by its name
    public void deleteCategory(int categoryId) {
       // String sql = "DELETE FROM category WHERE category_id = ?";
        
        String sql = "UPDATE category SET isactive=0 , isdelete=1 WHERE category_id = ?";
        jdbcTemplate.update(sql, categoryId);
    }
    
    
    //hari populate category services
    @Transactional
	public List<Map<String, Object>> getcategory() {
		String sqlQuery = "SELECT category_id,category_name FROM category where isactive='1' and isdelete='0'";
		return jdbcTemplate.queryForList(sqlQuery);
	}
    
    public List<Map<String, Object>> getCategoryColumns(int categoryId) {
        String sqlQuery = "SELECT column_name FROM category_columns WHERE category_id = ? AND isactive = 1";
        return jdbcTemplate.queryForList(sqlQuery, categoryId);
    }

    // Fetch category with columns
    public List<Map<String, Object>> getCategoriesWithColumns() {
        String sqlQuery = "SELECT c.category_id, c.category_name, cc.column " +
                          "FROM gcc_1913_campaign.category c " +
                          "INNER JOIN gcc_1913_campaign.category_columns cc ON c.category_id = cc.category_id " +
                          "WHERE c.isactive = 1 AND c.isdelete = 0 " +
                          "AND cc.isactive = 1 AND cc.isdelete = 0";
        return jdbcTemplate.queryForList(sqlQuery);
    }

	@Transactional

	public List<Map<String, Object>> getcampaignbycategory(int id) {

		String sqlQuery = "SELECT cr.campaign_id,cr.campaign_name FROM campaign_request cr JOIN category c ON cr.category_id=c.category_id where cr.category_id=? and cr.isactive='1'";

		return jdbcTemplate.queryForList(sqlQuery,id);

	}


	public boolean removeColumnById(int columnId) {
	    String sql = "UPDATE category_columns SET isactive = 0, isdelete = 1 WHERE id = ?";

	    int rowsAffected = jdbcTemplate.update(sql, columnId);

	    return rowsAffected > 0; // Returns true if at least one row was updated, otherwise false
	}
	
	public boolean updateColumnById(int categoryId,int columnId, String columnName) {
	    String sql = "UPDATE category_columns SET column_name = ? WHERE id = ? and category_id=?";
	    int rowsAffected = jdbcTemplate.update(sql, columnName, columnId,categoryId);
	    return rowsAffected > 0;
	}


	public boolean addnewupdateColumn(int categoryId, String columnName) {
		
		//String sql = "UPDATE category_columns SET column_name = ? WHERE id = ? and category_id=?";
		String sql = "INSERT INTO category_columns (category_id, column_name) VALUES (?, ?)";

	    int rowsAffected = jdbcTemplate.update(sql, categoryId,columnName);
	    return rowsAffected > 0;
	}

    
}
