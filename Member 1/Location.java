class Location {
    private String FacultyorDorm;
    private String Block;

    public Location(String FacultyorDorm, String Block) {
        this.FacultyorDorm = FacultyorDorm;
        this.Block = Block;
    }

    public String getFacultyorDorm() {
        return FacultyorDorm;
    }

    public String getBlock() {
        return Block;
    }

    public String toString() {
        return "Location{" +
                "FacultyorDorm='" + FacultyorDorm + '\'' +
                ", Block='" + Block + '\'' +
                '}';
    }
}