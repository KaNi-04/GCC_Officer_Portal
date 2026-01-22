package in.gov.chennaicorporation.gccoffice.callcenter.controller;

import java.util.HashMap;
import java.util.List;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gov.chennaicorporation.gccoffice.callcenter.service.AddcategoryServices;


@RequestMapping("/gcc/api/category")
@RestController
public class CategoryAPIController {
	
	 @Autowired
	 private AddcategoryServices addCategoryServices; // Injecting the service

	 @PostMapping("/save")
	 public ResponseEntity<String> saveCategoryDetails(@RequestBody Map<String, Object> payload) {
	     try {
	         addCategoryServices.saveCategoryDetails(payload);
	         return ResponseEntity.ok("Category and columns saved successfully.");
	     } catch (IllegalArgumentException e) {
	         return ResponseEntity.badRequest().body("Validation Error: " + e.getMessage());
	     } catch (Exception e) {
	         return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
	     }
	 }

	
	 // Endpoint to save category details
//    @PostMapping("/save")
//    public ResponseEntity<String> saveCategoryDetails(@RequestBody Map<String, Object> payload) {
//        try {
//            addCategoryServices.saveCategoryDetails(payload); // Call the service method to save category details
//            return ResponseEntity.ok("Category details saved successfully.");
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Error saving category details: " + e.getMessage());
//        }
//    }
    
    // Endpoint to get all categories
    @GetMapping("/list")
    public List<Map<String, Object>> getAllCategories() {
        return addCategoryServices.getAllCategories();
    }
    
    @GetMapping("/showcategoryId")
    public ResponseEntity<?> getCategoryById(@RequestParam("category_id") int categoryId) {
        Map<String, Object> category = addCategoryServices.showCategory(categoryId);
        if (category.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found");
        }
        return ResponseEntity.ok(category);
    }
    
    @PostMapping("/deleteColumnById")
    public ResponseEntity<Map<String, Object>> deleteCategoryColumnById(@RequestParam("columnId") int columnId) {
        boolean updated = addCategoryServices.removeColumnById(columnId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", updated);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/updateCategoryColumns")
    public ResponseEntity<Map<String, Object>> updateCategoryColumnsById(@RequestBody Map<String, Object> requestData) {
        try {
            int categoryId = (int) requestData.get("categoryId");
            List<Map<String, Object>> columns = (List<Map<String, Object>>) requestData.get("columns");

            
            boolean allUpdated = true;

            for (Map<String, Object> column : columns) {
                int columnId = (int) column.get("id");
                String columnName = (String) column.get("name");
                            

                boolean updated = addCategoryServices.updateColumnById(categoryId,columnId, columnName);

                if (!updated) {
                    allUpdated = false;
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", allUpdated);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid data format"));
        }
    }
    
    @PostMapping("/addNewColumns")
    public ResponseEntity<Map<String, Object>> addNewCategoryColumnsById(@RequestBody Map<String, Object> requestData) {
    	
    	try {
            int categoryId = (int) requestData.get("categoryId");
            List<Map<String, Object>> columns = (List<Map<String, Object>>) requestData.get("columns");


            boolean allUpdated = true;

            for (Map<String, Object> column : columns) {
                
                String columnName = (String) column.get("name");
                

                boolean updated = addCategoryServices.addnewupdateColumn(categoryId, columnName);

                if (!updated) {
                    allUpdated = false;
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", allUpdated);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid data format"));
        }
    }



    @PutMapping("/update/{categoryId}")
    public ResponseEntity<String> updateCategoryWithColumns(
            @PathVariable("categoryId") int categoryId,
            @RequestBody Map<String, Object> payload) {

        String categoryName = (String) payload.get("categoryName");
        List<String> columns = (List<String>) payload.get("columns");

        try {
            int rowsAffected = addCategoryServices.updateCategoryWithColumns(categoryId, categoryName, columns);
            return rowsAffected > 0
                    ? ResponseEntity.ok("Category and columns updated successfully")
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found or update failed");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating category: " + e.getMessage());
        }
    }

    
// // Endpoint to get a category by its ID
//    @GetMapping("/showcategoryId")
//    public ResponseEntity<?> getCategoryById(@RequestParam("category_id") int categoryId) {
//        Map<String, Object> category = addCategoryServices.showCategory(categoryId);
//
//        if (category == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found");
//        }
//
//        return ResponseEntity.ok(category);
//    }
//
//    @PutMapping("/update/{categoryId}")
//    public ResponseEntity<String> updateCategoryWithColumns(
//            @PathVariable("categoryId") int categoryId,
//            @RequestBody Map<String, Object> payload) {
//
//        String categoryName = (String) payload.get("categoryName");
//        List<String> columns = (List<String>) payload.get("columns");
//
//        try {
//            int rowsAffected = addCategoryServices.updateCategoryWithColumns(categoryId, categoryName, columns);
//            if (rowsAffected > 0) {
//                return ResponseEntity.ok("Category and columns updated successfully");
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found or update failed");
//            }
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body("Validation Error: " + e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error updating category: " + e.getMessage());
//        }
//    }

//  @GetMapping("/showcategoryId")
//  public ResponseEntity<?> showCategoryId(@RequestParam("category_id") int categoryId) {
//      try {
//          Map<String, Object> category = addCategoryServices.showCategory(categoryId);
//          if (category != null && !category.isEmpty()) {
//              return ResponseEntity.ok(category);
//          } else {
//              return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found");
//          }
//      } catch (Exception e) {
//          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                  .body("Error retrieving category: " + e.getMessage());
//      }
//  }
    
//    @PutMapping("/update/{categoryId}")
//    public ResponseEntity<String> updateCategory(
//            @PathVariable("categoryId") int categoryId,
//            @RequestParam String categoryName) {
//
//        // Validate input
//        if (categoryName == null || categoryName.trim().isEmpty()) {
//            return ResponseEntity.badRequest().body("Category name cannot be empty");
//        }
//
//        try {
//            // Call the service method to update the category
//            int rowsAffected = addCategoryServices.updateCategory(categoryId, categoryName);
//            if (rowsAffected > 0) {
//                return ResponseEntity.ok("Category updated successfully");
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found or update failed");
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error updating category: " + e.getMessage());
//        }
//    }


 // Endpoint to delete a category by its ID
    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<String> deleteCategory(
            @PathVariable("categoryId") int categoryId) {

        try {
            // Call the service method to delete the category
            addCategoryServices.deleteCategory(categoryId);
            return ResponseEntity.ok("Category deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting category: " + e.getMessage());
        }
    }

    
//hari category api populate 
    
	@GetMapping("/getcategory")
	public List<Map<String, Object>> getcategory(){
		return addCategoryServices.getcategory();
	}
	
    // Get category-specific columns based on category_id
    @GetMapping("/getCategoryColumns")
    public List<Map<String, Object>> getCategoryColumns(@RequestParam int category_id) {
        return addCategoryServices.getCategoryColumns(category_id);
    }
	

	@GetMapping("/getcampaignbycategory")
	public List<Map<String, Object>> getcampaignbycategory(@RequestParam int id)
	{
		return addCategoryServices.getcampaignbycategory(id);
	}
	

    
}
