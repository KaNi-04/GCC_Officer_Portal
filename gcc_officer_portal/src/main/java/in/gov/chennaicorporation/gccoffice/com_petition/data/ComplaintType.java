package in.gov.chennaicorporation.gccoffice.com_petition.data;

import java.util.Map;

public class ComplaintType {
	private int complaintTypeId;
    private String complaintType;

    public ComplaintType(Map<String, Object> map) {
        this.complaintTypeId = (int) map.get("complaint_typeid");
        this.complaintType = (String) map.get("complaint_type");
    }

    public int getComplaintTypeId() {
        return complaintTypeId;
    }

    public String getComplaintType() {
        return complaintType;
    }
}
