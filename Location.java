public class Location {
    private String facultyOrDorm;
    private String block;

    public Location(String facultyOrDorm, String block) {
        this.facultyOrDorm = facultyOrDorm;
        this.block = block;
    }

    public String getFacultyOrDorm() {
        return facultyOrDorm;
    }

    public String getBlock() {
        return block;
    }

    @Override
    public String toString() {
        return facultyOrDorm + " (" + block + ")";
    }
}