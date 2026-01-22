package in.gov.chennaicorporation.gccoffice.greencommittee.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import in.gov.chennaicorporation.gccoffice.vendor.service.DateTimeUtil;

@Service
public class CreateMeetingService {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private  Environment environment;
	
	@Autowired
	public void setDataSource(@Qualifier("mysqlGreenCommitteeDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public String fileUpload(MultipartFile file, String name) {

        // Set the file path where you want to save it
        String uploadDirectory = environment.getProperty("file.upload.directory.mobile");
        String serviceFolderName = environment.getProperty("green_committee_foldername");
        var year = DateTimeUtil.getCurrentYear();
        var month = DateTimeUtil.getCurrentMonth();

        uploadDirectory = uploadDirectory + serviceFolderName + "/" + year + "/" + month;

        try {
            // Create directory if it doesn't exist
            Path directoryPath = Paths.get(uploadDirectory);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            // Datetime string
            String datetimetxt = DateTimeUtil.getCurrentDateTime();
           
            // File name
            String fileName = name + "_" + datetimetxt + "_" + file.getOriginalFilename();
            fileName = fileName.replaceAll("\\s+", ""); // Remove space on filename

            String filePath = uploadDirectory + "/" + fileName;

            String filepath_txt = "/" + serviceFolderName + "/" + year + "/" + month + "/" + fileName;

            // Create a new Path object
            Path path = Paths.get(filePath);

            // Get the bytes of the file
            byte[] bytes = file.getBytes();

			/*
			 * // Compress the image BufferedImage image = ImageIO.read(new
			 * ByteArrayInputStream(bytes)); byte[] compressedBytes = compressImage(image,
			 * 0.5f); // Compress with 50% quality
			 * 
			 * // Write the bytes to the file 
			 * Files.write(path, compressedBytes);
			 */
            
            Files.write(path, bytes);

            //System.out.println(filePath);
            return filepath_txt;

        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to save file " + file.getOriginalFilename();
        }
    }
	
	public String insertEvent(String eventName, LocalDate eventDate, String fileUrl,String cby) {
	    String sql = "INSERT INTO create_meeting (event_name, event_date, file_url,cby) VALUES (?, ?, ?,?)";
	    try {
	    	jdbcTemplate.update(sql, eventName, eventDate, fileUrl,cby);
	    	return "success";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();			
		}
	    return "error";
	}
	
}
