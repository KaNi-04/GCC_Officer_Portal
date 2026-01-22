package in.gov.chennaicorporation.gccoffice.school.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import in.gov.chennaicorporation.gccoffice.school.Service.StudentInfoService;

@RestController
@RequestMapping("/gcc/api/gccschool/studentInfo")
public class StudentInfoController {
	@Autowired
	private StudentInfoService studentInfoService;
	
	@Autowired
    public StudentInfoController(StudentInfoService studentInfoService) {
        this.studentInfoService = studentInfoService;
    }
	
	@GetMapping("/getStandards")
	public ResponseEntity<?> getStandards(@RequestParam String udise){
		return studentInfoService.getStandards(udise);
	}
	
	@GetMapping("/getSections")
	public ResponseEntity<?> getSections(){
		return studentInfoService.getSections();
	}
	
	@PostMapping("/saveStudent")
	public ResponseEntity<?> saveStudent(@RequestParam String udise,
			@RequestParam String student_name,
			@RequestParam String emis_no,
			@RequestParam String gender,
			@RequestParam String std,
			@RequestParam String dob,
			@RequestParam String age,
			@RequestParam String parent_type,
			@RequestParam String section,
			@RequestParam String medium,
			@RequestParam(value = "father_name", required = false) String father_name,
			@RequestParam(value = "mother_name", required = false) String mother_name,
			@RequestParam(value = "guardian_name", required = false) String guardian_name,
			@RequestParam(value = "father_mobile", required = false) String father_mobile,
			@RequestParam(value = "mother_mobile", required = false) String mother_mobile,
			@RequestParam(value = "guardian_mobile", required = false) String guardian_mobile,
			@RequestParam(value = "father_aadhar", required = false) String father_aadhar,
			@RequestParam(value = "mother_aadhar", required = false) String mother_aadhar,
			@RequestParam(value = "guardian_aadhar", required = false) String guardian_aadhar,
			@RequestParam(value = "student_aadhar", required = false) String student_aadhar,
			@RequestParam(value = "father_aadhar_file", required = false) MultipartFile father_aadhar_file,
			@RequestParam(value = "mother_aadhar_file", required = false) MultipartFile mother_aadhar_file,
			@RequestParam(value = "guardian_aadhar_file", required = false) MultipartFile guardian_aadhar_file,
			@RequestParam(value = "student_aadhar_file", required = false) MultipartFile student_aadhar_file) {
		//return studentInfoService.saveStudent(udise, student_name, emis_no, gender, std, dob, age, parent_type, father_name, mother_name, guardian_name, father_mobile, mother_mobile, guardian_mobile, father_aadhar, mother_aadhar, guardian_aadhar, student_aadhar, father_aadhar_file, mother_aadhar_file, guardian_aadhar_file, student_aadhar_file);
		return studentInfoService.checkStudent(udise, student_name, emis_no, gender, std, dob, age, parent_type,section,medium, father_name, mother_name, guardian_name, father_mobile, mother_mobile, guardian_mobile, father_aadhar, mother_aadhar, guardian_aadhar, student_aadhar, father_aadhar_file, mother_aadhar_file, guardian_aadhar_file, student_aadhar_file);
	}
	
	@GetMapping("/getStudentsClass")
	public ResponseEntity<?> getStudentsClass(@RequestParam String std,
			                                  @RequestParam String udise){
		return studentInfoService.getStudentsClass(udise, std);
	}
	
	@GetMapping("/getStudentsDetails")
	public ResponseEntity<?> getStudentsDetails(@RequestParam String studentId){
		return studentInfoService.getStudentsDetails(studentId);
	}
	
	@GetMapping("/getMedium")
	public ResponseEntity<?> getMedium(){
		return studentInfoService.getMedium();
	}
	
	@PostMapping("/deleteStudent")
	public ResponseEntity<?> deleteStudent(@RequestParam String student_id,
			@RequestParam String user_id,
			@RequestParam String action_type) {
		return studentInfoService.deleteProcess(student_id, user_id, action_type);
	}
	
	@PostMapping("/updateStudent")
	public ResponseEntity<?> updateStudent(@RequestParam String student_id,
			@RequestParam String user_id,
			@RequestParam String action_type,
			@RequestParam String udise,
			@RequestParam String student_name,
			@RequestParam String emis_no,
			@RequestParam String gender,
			@RequestParam String mediums,
			@RequestParam String std,
			@RequestParam String section,
			@RequestParam String dob,
			@RequestParam String age,
			@RequestParam String parent_type,
			@RequestParam(value = "father_name", required = false) String father_name,
			@RequestParam(value = "mother_name", required = false) String mother_name,
			@RequestParam(value = "guardian_name", required = false) String guardian_name,
			@RequestParam(value = "father_mobile", required = false) String father_mobile,
			@RequestParam(value = "mother_mobile", required = false) String mother_mobile,
			@RequestParam(value = "guardian_mobile", required = false) String guardian_mobile,
			@RequestParam(value = "father_aadhar", required = false) String father_aadhar,
			@RequestParam(value = "mother_aadhar", required = false) String mother_aadhar,
			@RequestParam(value = "guardian_aadhar", required = false) String guardian_aadhar,
			@RequestParam(value = "student_aadhar", required = false) String student_aadhar,
			@RequestParam(value = "father_aadhar_file", required = false) MultipartFile father_aadhar_file,
			@RequestParam(value = "mother_aadhar_file", required = false) MultipartFile mother_aadhar_file,
			@RequestParam(value = "guardian_aadhar_file", required = false) MultipartFile guardian_aadhar_file,
			@RequestParam(value = "student_aadhar_file", required = false) MultipartFile student_aadhar_file,
			@RequestParam (value = "raw_father_aadhar", required = false) String raw_father_aadhar,
			@RequestParam (value = "raw_mother_aadhar", required = false) String raw_mother_aadhar,
			@RequestParam (value = "raw_guardian_aadhar", required = false) String raw_guardian_aadhar,
			@RequestParam (value = "raw_student_aadhar", required = false) String raw_student_aadhar) {
		return studentInfoService.updateProcess(student_id, user_id, action_type, udise, student_name, emis_no, gender, mediums, std, section, dob, age, parent_type, father_name, mother_name, guardian_name, father_mobile, mother_mobile, guardian_mobile, father_aadhar, mother_aadhar, guardian_aadhar, student_aadhar, father_aadhar_file, mother_aadhar_file, guardian_aadhar_file, student_aadhar_file, raw_father_aadhar, raw_mother_aadhar, raw_guardian_aadhar, raw_student_aadhar);
	}
	
	@GetMapping("/checkStudent")
	public ResponseEntity<?> checkStudent(@RequestParam String udise, @RequestParam String schoolId){
		return studentInfoService.checkStudent(udise, schoolId);
	}
	
 }
