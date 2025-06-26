package models;

public class Student extends User {
    private String studentId;
    private String department;
    private String status;

    public Student() {
        super();
        setUserType("student");
    }

    public Student(String firstName, String lastName, String email, String password,
                  String studentId, String department) {
        super(firstName, lastName, email, password, "student");
        this.studentId = studentId;
        this.department = department;
        this.status = "pending";
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isApproved() {
        return "approved".equals(status);
    }

    public boolean isBlocked() {
        return "blocked".equals(status);
    }

    public boolean isPending() {
        return "pending".equals(status);
    }
} 