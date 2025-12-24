package com.eduportal.dao;

import com.eduportal.model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class CourseDAO implements BaseDAO<Course> {

    private static final String INSERT_SQL = 
        "INSERT INTO course (course_name, course_code, credits, InstructorID) VALUES (?, ?, ?, ?)";
    
    private static final String UPDATE_SQL = 
        "UPDATE course SET course_name=?, course_code=?, credits=?, InstructorID=? WHERE CourseID=?";

    private static final String SELECT_ALL_SQL = "SELECT * FROM course";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM course WHERE CourseID = ?";
    private static final String DELETE_SQL = "DELETE FROM course WHERE CourseID=?";
    private static final String SELECT_BY_INSTRUCTOR_SQL = "SELECT * FROM course WHERE InstructorID = ?"; 

    private Course extractCourseFromResultSet(ResultSet rs) throws SQLException {
        Course course = new Course(
            rs.getInt("CourseID"),
            rs.getString("course_name"),
            rs.getString("course_code"),
            String.valueOf(rs.getInt("credits")), 
            new Date() 
        );
        course.setCredits(rs.getInt("credits"));
        course.setInstructorID(rs.getInt("InstructorID"));
        return course;
    }

    @Override
    public Course getById(int id) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractCourseFromResultSet(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null; 
    }

    public boolean insertWithInstructor(Course course, int instructorID) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            ps.setString(1, course.getCourseName());
            ps.setString(2, course.getCourseCode());
            ps.setInt(3, course.getCredits());
            ps.setInt(4, instructorID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override public boolean insert(Course c) { return insertWithInstructor(c, c.getInstructorID()); }
    
    @Override public boolean update(Course c) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, c.getCourseName());
            ps.setString(2, c.getCourseCode());
            ps.setInt(3, c.getCredits());
            ps.setInt(4, c.getInstructorID());
            ps.setInt(5, c.getCourseID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Course> getAll() {
        List<Course> list = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {
            while (rs.next()) {
                list.add(extractCourseFromResultSet(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public boolean delete(int id) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // --- FIXED FOR STUDENT DASHBOARD (Uses 'Name' instead of 'full_name') ---

    public List<Course> searchCourses(String query) {
        List<Course> courses = new ArrayList<>();
        // FIXED: Changed i.full_name to i.Name to match our Instructor table
        String sql = "SELECT c.*, i.Name AS InstructorName " +
                     "FROM course c " +
                     "LEFT JOIN instructor i ON c.InstructorID = i.InstructorID " +
                     "WHERE c.course_name LIKE ? OR c.course_code LIKE ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String searchTerm = "%" + (query == null ? "" : query) + "%";
            ps.setString(1, searchTerm);
            ps.setString(2, searchTerm);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    courses.add(new Course(
                        rs.getInt("CourseID"),
                        rs.getString("course_code"), 
                        rs.getString("course_name"), 
                        rs.getString("InstructorName"), // Maps to Name column
                        rs.getInt("credits"), 
                        "Open"
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Search error: " + e.getMessage());
        }
        return courses;
    }

    public List<Course> getCoursesByStudentID(int studentID) {
        List<Course> courses = new ArrayList<>();
        // FIXED: Changed i.full_name to i.Name
        String sql = "SELECT c.CourseID, c.course_name, c.course_code, " +
                     "i.Name AS InstructorName, c.credits, e.Status " +
                     "FROM enrollment e " +
                     "JOIN course c ON e.CourseID = c.CourseID " +
                     "LEFT JOIN instructor i ON c.InstructorID = i.InstructorID " +
                     "WHERE e.StudentID = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    courses.add(new Course(
                        rs.getInt("CourseID"),
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getString("InstructorName"),
                        rs.getInt("credits"),
                        rs.getString("Status")
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return courses;
    }
    
    /**

     * Updates course details and assigns an instructor.

     * This matches your panel logic: courseDAO.updateWithInstructor(currentCourse, instructorID)

     */

    public boolean updateWithInstructor(Course course, int instructorID) {

        // We map attribute1 -> name, attribute2 -> code, attribute3 -> credits

        String sql = "UPDATE Course SET course_name = ?, course_code = ?, credits = ?, InstructorID = ? WHERE CourseID = ?";

        

        try (Connection conn = DatabaseConnector.getConnection();

             PreparedStatement ps = conn.prepareStatement(sql)) {

            

            ps.setString(1, course.getAttribute1()); // Name

            ps.setString(2, course.getAttribute2()); // Code

            

            // Handle credits conversion (String to Int) safely

            int creditVal = 0;

            try {

                creditVal = Integer.parseInt(course.getAttribute3());

            } catch (NumberFormatException e) {

                creditVal = 0; 

            }

            

            ps.setInt(3, creditVal);

            ps.setInt(4, instructorID);

            ps.setInt(5, course.getCourseID());

            

            return ps.executeUpdate() > 0;

            

        } catch (SQLException e) {

            System.err.println("Error in updateWithInstructor: " + e.getMessage());

            return false;

        }

    }
    public boolean enrollStudentInCourse(int studentID, int courseID) {

        String sql = "INSERT INTO Enrollment (StudentID, CourseID, EnrollDate, Status) VALUES (?, ?, NOW(), 'Registered')";

        try (Connection conn = DatabaseConnector.getConnection();

             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentID);

            ps.setInt(2, courseID);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.err.println("Enrollment error: " + e.getMessage());

            return false;

        }

    }

    public List<Course> getCoursesByInstructor(int instructorID) {
        List<Course> courses = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_INSTRUCTOR_SQL)) {
            ps.setInt(1, instructorID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    courses.add(extractCourseFromResultSet(rs));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return courses;
    }
}