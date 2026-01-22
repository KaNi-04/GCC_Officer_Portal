package in.gov.chennaicorporation.gccoffice.modeldata;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Employee {

    @JsonFormat(pattern = "yyyy/MM/dd")
    @JsonProperty("start_date")
    private Date startDate;
    private Integer id;
    private String position;
    private String name;
    private Double salary;
    private String office;
    private Integer extn;
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getSalary() {
		return salary;
	}
	public void setSalary(Double salary) {
		this.salary = salary;
	}
	public String getOffice() {
		return office;
	}
	public void setOffice(String office) {
		this.office = office;
	}
	public Integer getExtn() {
		return extn;
	}
	public void setExtn(Integer extn) {
		this.extn = extn;
	}

    
}
