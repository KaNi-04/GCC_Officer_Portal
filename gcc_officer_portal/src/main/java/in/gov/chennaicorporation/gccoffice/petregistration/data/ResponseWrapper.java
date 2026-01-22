package in.gov.chennaicorporation.gccoffice.petregistration.data;

import java.util.List;

public class ResponseWrapper {
    private List<RequestList> data;

    public List<RequestList> getData() {
        return data;
    }

    public void setData(List<RequestList> data) {
        this.data = data;
    }
}