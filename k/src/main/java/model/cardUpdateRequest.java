package model;

public class cardUpdateRequest {
    int id;
    String addition;

    public cardUpdateRequest() {
    }

    public cardUpdateRequest(int id, String addition) {
        super();
        this.id = id;
        this.addition = addition;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddition() {
        return addition;
    }

    public void setAddition(String addition) {
        this.addition = addition;
    }
}
