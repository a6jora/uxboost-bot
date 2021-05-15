package botapi.handlers.fillinfad;

public class UserAd {
    private String adText;
    private String glads;
    private String timeFor;
    private String timeSlots;
    private String rest;
    private String contacts;
    private String deadline;
    private String toPost;

    public UserAd() {
    }

    @Override
    public String toString() {

        return String.format("1. %s\n2. %s\n3. %s\n4. %s\n5. %s\n6. %s\n7. %s\n",
                adText,glads,timeFor,timeSlots,rest,contacts,deadline);
    }

    public String getAdText() {
        return adText;
    }

    public void setAdText(String adText) {
        this.adText = adText;
    }

    public String getGlads() {
        return glads;
    }

    public void setGlads(String glads) {
        this.glads = glads;
    }

    public String getTimeFor() {
        return timeFor;
    }

    public void setTimeFor(String timeFor) {
        this.timeFor = timeFor;
    }

    public String getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(String timeSlots) {
        this.timeSlots = timeSlots;
    }

    public String getRest() {
        return rest;
    }

    public void setRest(String rest) {
        this.rest = rest;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getToPost() {
        return toPost;
    }

    public void setToPost(String toPost) {
        this.toPost = toPost;
    }
}
