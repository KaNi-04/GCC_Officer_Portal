package in.gov.chennaicorporation.gccoffice.petregistration.data;

import java.util.Map;

public class PetCountInfo {

    private Map<String, Integer> dog;
    private Map<String, Integer> cat;
    private int userCount;
    private int licCount;

    // Getters and setters

    public Map<String, Integer> getDog() {
        return dog;
    }

    public void setDog(Map<String, Integer> dog) {
        this.dog = dog;
    }

    public Map<String, Integer> getCat() {
        return cat;
    }

    public void setCat(Map<String, Integer> cat) {
        this.cat = cat;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public int getLicCount() {
        return licCount;
    }

    public void setLicCount(int licCount) {
        this.licCount = licCount;
    }
}

