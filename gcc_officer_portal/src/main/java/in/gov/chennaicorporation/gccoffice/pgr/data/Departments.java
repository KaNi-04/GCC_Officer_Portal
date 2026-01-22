package in.gov.chennaicorporation.gccoffice.pgr.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Departments {
	
	private String id;
	private String name;
	
	@JsonCreator
    public Departments(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name
    ) {
        this.id = id;
        this.name = name;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
