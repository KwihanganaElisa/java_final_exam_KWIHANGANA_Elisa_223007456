package com.eduportal.dao;

import com.eduportal.model.Course;
import com.eduportal.dao.DatabaseConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Data Access Object for the Course entity.
 * FINAL UPDATE: Matched to exact database table cases (enrollment vs User).
 */
public class CourseDAO implements BaseDAO<Course> {

    // --- SQL STATEMENTS ---
    private static final String INSERT_WITH_INSTRUCTOR_SQL = 
        "INSERT INTO Course (Attribute1, Attribute2, Attribute3, InstructorID, CreatedAt) VALUES (?, ?, ?, ?, ?)";
    
    private static final String UPDATE_WITH_INSTRUCTOR_SQL = 
        "UPDATE Course SET Attribute1=?, Attribute2=?, Attribute3=?, InstructorID=? WHERE CourseID=?";

    private static final String SELECT_ALL_SQL = "SELECT * FROM Course";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM Course WHERE CourseID = ?";
    private static final String DELETE_SQL = "DELETE FROM Course WHERE CourseID=?";
    private static final String SELECT_BY_INSTRUCTOR_SQL = "SELECT * FROM Course WHERE InstructorID = ?"; 

    /**
     * Helper to extract course from result set for CRUD operations.
     */
    private Course extractCourseFromResultSet(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("CreatedAt");
        Date date = (ts != null) ? new Date(ts.getTime()) : null;

        Course course = new Course(
            rs.getInt("CourseID"),
            rs.getString("Attribute1"), // Name
            rs.getString("Attribute2"), // Code
            rs.getString("Attribute3"), // Credits
            date
        );
        
        try {
            course.setCredits(Integer.parseInt(rs.getString("Attribute3")));
        } catch (Exception e) {
            course.setCredits(0);
        }

        course.setInstructorID(rs.getInt("InstructorID"));
        return course;
    }

    // --- CORE CRUD METHODS ---

    @Override
    public Course getById(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnector.getConnection();
            ps = conn.prepareStatement(SELECT_BY_ID_SQL);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return extractCourseFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, ps, rs);
        }
        return null; 
    }

    public boolean insertWithInstructor(Course course, int instructorID) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnector.getConnection();
            ps = conn.prepareStatement(INSERT_WITH_INSTRUCTOR_SQL);
            ps.setString(1, course.getAttribute1());
            ps.setString(2, course.getAttribute2());
            ps.setString(3, course.getAttribute3());
            ps.setInt(4, instructorID);
            ps.setTimestamp(5, new Timestamp(new Date().getTime()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, ps, null);
        }
    }

    public boolean updateWithInstructor(Course course, int instructorID) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnector.getConnection();
            ps = conn.prepareStatement(UPDATE_WITH_INSTRUCTOR_SQL);
            ps.setString(1, course.getAttribute1());
            ps.setString(2, course.getAttribute2());
            ps.setString(3, course.getAttribute3());
            ps.setInt(4, instructorID);
            ps.setInt(5, course.getCourseID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, ps, null);
        }
    }

    @Override public boolean insert(Course c) { return insertWithInstructor(c, 0); }
    @Override public boolean update(Course c) { return updateWithInstructor(c, c.getInstructorID()); }

    @Override
    public List<Course> getAll() {
        List<Course> list = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnector.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(SELECT_ALL_SQL);
            while (rs.next()) {
                list.add(extractCourseFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        return list;
    }

    @Override
    public boolean delete(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnector.getConnection();
            ps = conn.prepareStatement(DELETE_SQL);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, ps, null);
        }
    }

    // --- STUDENT & SPECIALTY METHODS ---

    public List<Course> searchCourses(String query) {
        List<Course> courses = new ArrayList<>();
        // Fixed: Table name matches 'User' and 'Course'
        String sql = "SELECT c.*, u.FullName AS InstructorName " +
                     "FROM Course c " +
                     "LEFT JOIN User u ON c.InstructorID = u.UserID " +
                     "WHERE c.Attribute1 LIKE ? OR c.Attribute2 LIKE ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String searchTerm = "%" + (query == null ? "" : query) + "%";
            ps.setString(1, searchTerm);
            ps.setString(2, searchTerm);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Course c = new Course(
                    rs.getInt("CourseID"),
                    rs.getString("Attribute2"), 
                    rs.getString("Attribute1"), 
                    rs.getString("InstructorName"),
                    0, 
                    "Open"
                );
                try {
                    c.setCredits(Integer.parseInt(rs.getString("Attribute3")));
                } catch (Exception e) { c.setCredits(0); }
                courses.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Search error: " + e.getMessage());
        }
        return courses;
    }

    public boolean enrollStudentInCourse(int studentID, int courseID) {
        // Fixed: Table name matches 'enrollment' (lowercase)
        String sql = "INSERT INTO enrollment (StudentID, CourseID, EnrollDate, Status) VALUES (?, ?, NOW(), 'Registered')";
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

    public List<Course> getCoursesByStudentID(int studentID) {
        List<Course> registered = new ArrayList<>();
        // Fixed: Joins 'enrollment' (lowercase), 'Course' and 'User' (Title case)
        String sql = "SELECT c.*, e.Status as EnrollmentStatus, u.FullName as InstructorName " +
                     "FROM enrollment e " +
                     "JOIN Course c ON e.CourseID = c.CourseID " +
                     "LEFT JOIN User u ON c.InstructorID = u.UserID " +
                     "WHERE e.StudentID = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, studentID);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Course course = new Course(
                    rs.getInt("CourseID"),
                    rs.getString("Attribute2"), 
                    rs.getString("Attribute1"), 
                    rs.getString("InstructorName"),
                    0, 
                    rs.getString("EnrollmentStatus")
                );
                try {
                    course.setCredits(Integer.parseInt(rs.getString("Attribute3")));
                } catch(Exception e) { course.setCredits(0); }
                
                registered.add(course);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching student courses: " + e.getMessage());
        }
        return registered;
    }

    public List<Course> getCoursesByInstructor(int instructorID) {
        List<Course> courses = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnector.getConnection();
            ps = conn.prepareStatement(SELECT_BY_INSTRUCTOR_SQL);
            ps.setInt(1, instructorID);
            rs = ps.executeQuery();
            while (rs.next()) {
                courses.add(extractCourseFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, ps, rs);
        }
        return courses;
    }

    private void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
}