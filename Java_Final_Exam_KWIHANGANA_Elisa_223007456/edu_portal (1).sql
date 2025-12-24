-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Dec 24, 2025 at 10:04 AM
-- Server version: 9.1.0
-- PHP Version: 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `edu_portal`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
CREATE TABLE IF NOT EXISTS `admin` (
  `AdminID` int NOT NULL AUTO_INCREMENT,
  `Username` varchar(50) NOT NULL,
  `Password` varchar(255) NOT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`AdminID`),
  UNIQUE KEY `Username` (`Username`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`AdminID`, `Username`, `Password`, `full_name`, `CreatedAt`) VALUES
(1, 'Elisa', 'admin123', 'Elisa Smith', '2025-12-24 12:03:39'),
(2, 'Admin1', 'admin123', 'System Administrator', '2025-12-24 12:03:39');

-- --------------------------------------------------------

--
-- Table structure for table `assignment`
--

DROP TABLE IF EXISTS `assignment`;
CREATE TABLE IF NOT EXISTS `assignment` (
  `AssignmentID` int NOT NULL AUTO_INCREMENT,
  `CourseID` int NOT NULL,
  `Title` varchar(255) DEFAULT NULL,
  `DueDate` datetime DEFAULT NULL,
  PRIMARY KEY (`AssignmentID`),
  KEY `CourseID` (`CourseID`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `assignment`
--

INSERT INTO `assignment` (`AssignmentID`, `CourseID`, `Title`, `DueDate`) VALUES
(1, 1, 'Basic Syntax Lab', '2025-01-15 23:59:59'),
(2, 1, 'Object Oriented Project', '2025-02-01 23:59:59'),
(3, 2, 'SQL Normalization Exercise', '2025-01-20 23:59:59'),
(4, 3, 'Logic Proofs Quiz', '2025-01-18 23:59:59'),
(5, 5, 'HTML/CSS Portfolio', '2025-01-25 23:59:59'),
(6, 6, 'Neural Network Draft', '2025-03-10 23:59:59'),
(7, 9, 'Binary Tree Implementation', '2025-02-15 23:59:59'),
(8, 10, 'SRS Documentation', '2025-01-30 23:59:59'),
(9, 4, 'Schrodinger Equation Set', '2025-02-05 23:59:59'),
(10, 7, 'Matrix Transformation HW', '2025-01-22 23:59:59');

-- --------------------------------------------------------

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
CREATE TABLE IF NOT EXISTS `course` (
  `CourseID` int NOT NULL AUTO_INCREMENT,
  `course_name` varchar(255) DEFAULT NULL,
  `course_code` varchar(255) DEFAULT NULL,
  `credits` int DEFAULT '3',
  `InstructorID` int DEFAULT NULL,
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`CourseID`),
  KEY `InstructorID` (`InstructorID`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `course`
--

INSERT INTO `course` (`CourseID`, `course_name`, `course_code`, `credits`, `InstructorID`, `CreatedAt`) VALUES
(1, 'Advanced Java', 'CS301', 4, 1, '2025-12-22 10:34:00'),
(2, 'Database Systems', 'DB202', 3, 1, '2025-12-22 10:34:00'),
(3, 'Introduction to Java', 'CS101', 4, 1, '2025-12-22 10:45:26'),
(4, 'Advanced Database Systems', 'CS302', 3, 2, '2025-12-22 10:45:26'),
(5, 'Discrete Mathematics', 'MATH201', 3, 2, '2025-12-22 10:45:26'),
(6, 'Quantum Physics', 'PHYS401', 4, 3, '2025-12-22 10:45:26'),
(7, 'Web Development', 'CS205', 3, 1, '2025-12-22 10:45:26'),
(8, 'Artificial Intelligence', 'CS410', 4, 1, '2025-12-22 10:45:26'),
(9, 'Linear Algebra', 'MATH105', 3, 2, '2025-12-22 10:45:26'),
(10, 'Thermodynamics', 'PHYS202', 3, 3, '2025-12-22 10:45:26'),
(11, 'Data Structures', 'CS201', 4, 1, '2025-12-22 10:45:26'),
(12, 'Software Engineering', 'CS305', 3, 1, '2025-12-22 10:45:26');

-- --------------------------------------------------------

--
-- Table structure for table `enrollment`
--

DROP TABLE IF EXISTS `enrollment`;
CREATE TABLE IF NOT EXISTS `enrollment` (
  `EnrollmentID` int NOT NULL AUTO_INCREMENT,
  `StudentID` int NOT NULL,
  `CourseID` int NOT NULL,
  `Status` varchar(50) DEFAULT 'Registered',
  `EnrollDate` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`EnrollmentID`),
  KEY `StudentID` (`StudentID`),
  KEY `CourseID` (`CourseID`)
) ENGINE=MyISAM AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `enrollment`
--

INSERT INTO `enrollment` (`EnrollmentID`, `StudentID`, `CourseID`, `Status`, `EnrollDate`) VALUES
(1, 1, 1, 'Registered', '2025-12-22 10:45:57'),
(2, 1, 2, 'Registered', '2025-12-22 10:45:57'),
(3, 1, 3, 'Registered', '2025-12-22 10:45:57'),
(4, 1, 5, 'Registered', '2025-12-22 10:45:57'),
(5, 1, 9, 'Registered', '2025-12-22 10:45:57'),
(6, 2, 1, 'Registered', '2025-12-22 10:45:57'),
(7, 2, 6, 'Registered', '2025-12-22 10:45:57'),
(8, 3, 1, 'Registered', '2025-12-22 10:45:57'),
(9, 3, 10, 'Registered', '2025-12-22 10:45:57'),
(10, 4, 4, 'Registered', '2025-12-22 10:45:57'),
(11, 1, 1, 'Registered', '2025-12-22 10:47:19'),
(12, 1, 2, 'Registered', '2025-12-22 10:47:19'),
(13, 2, 1, 'Registered', '2025-12-22 10:47:19'),
(14, 3, 1, 'Registered', '2025-12-22 10:47:19'),
(15, 4, 4, 'Registered', '2025-12-22 10:47:19'),
(16, 5, 3, 'Registered', '2025-12-22 10:47:19'),
(17, 6, 10, 'Registered', '2025-12-22 10:47:19'),
(18, 7, 7, 'Registered', '2025-12-22 10:47:19'),
(19, 8, 1, 'Registered', '2025-12-22 10:47:19'),
(20, 9, 8, 'Registered', '2025-12-22 10:47:19');

-- --------------------------------------------------------

--
-- Table structure for table `grade`
--

DROP TABLE IF EXISTS `grade`;
CREATE TABLE IF NOT EXISTS `grade` (
  `GradeID` int NOT NULL AUTO_INCREMENT,
  `StudentID` int NOT NULL,
  `AssignmentID` int NOT NULL,
  `Score` decimal(5,2) DEFAULT NULL,
  `LetterGrade` varchar(5) DEFAULT NULL,
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`GradeID`),
  KEY `StudentID` (`StudentID`),
  KEY `AssignmentID` (`AssignmentID`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `grade`
--

INSERT INTO `grade` (`GradeID`, `StudentID`, `AssignmentID`, `Score`, `LetterGrade`, `CreatedAt`) VALUES
(1, 1, 1, 85.50, 'B+', '2025-12-22 10:46:06'),
(2, 1, 3, 92.00, 'A', '2025-12-22 10:46:06'),
(3, 2, 1, 78.00, 'B-', '2025-12-22 10:46:06'),
(4, 3, 1, 95.00, 'A', '2025-12-22 10:46:06'),
(5, 1, 4, 88.00, 'B+', '2025-12-22 10:46:06'),
(6, 4, 9, 72.50, 'C+', '2025-12-22 10:46:06'),
(7, 2, 6, 81.00, 'B', '2025-12-22 10:46:06'),
(8, 1, 5, 99.00, 'A+', '2025-12-22 10:46:06'),
(9, 3, 10, 84.00, 'B', '2025-12-22 10:46:06'),
(10, 1, 8, 90.00, 'A-', '2025-12-22 10:46:06');

-- --------------------------------------------------------

--
-- Table structure for table `instructor`
--

DROP TABLE IF EXISTS `instructor`;
CREATE TABLE IF NOT EXISTS `instructor` (
  `InstructorID` int NOT NULL AUTO_INCREMENT,
  `Username` varchar(50) NOT NULL,
  `Password` varchar(255) NOT NULL,
  `Role` varchar(50) DEFAULT 'Instructor',
  `Name` varchar(255) DEFAULT NULL,
  `Identifier` varchar(50) DEFAULT NULL,
  `Status` varchar(50) DEFAULT 'Active',
  `Location` varchar(255) DEFAULT NULL,
  `Contact` varchar(255) DEFAULT NULL,
  `AssignedSince` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`InstructorID`),
  UNIQUE KEY `Username` (`Username`)
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `instructor`
--

INSERT INTO `instructor` (`InstructorID`, `Username`, `Password`, `Role`, `Name`, `Identifier`, `Status`, `Location`, `Contact`, `AssignedSince`) VALUES
(1, 'prof_smith', 'pass123', 'Instructor', 'Dr. Smith', 'EMP001', 'Active', NULL, NULL, '2025-12-22 10:34:00'),
(2, 'smith_j', 'password123', 'Instructor', 'Dr. Jane Smith', 'INST-001', 'Active', 'Science Building - Room 302', 'jane.smith@edu.com', '2025-12-22 10:45:10'),
(3, 'williams_r', 'pass456', 'Instructor', 'Prof. Robert Williams', 'INST-002', 'Active', 'Main Hall - Room 105', 'r.williams@edu.com', '2025-12-22 10:45:10'),
(4, 'brown_m', 'secure789', 'Instructor', 'Dr. Michael Brown', 'INST-003', 'Active', 'Lab Annex - Room 12', 'm.brown@edu.com', '2025-12-22 10:45:11'),
(5, 'dr_smith', 'admin123', 'Instructor', 'Dr. John Smith', 'INST-001', 'Active', 'Science Building Room 402', 'smith.j@eduportal.com', '2025-12-22 10:47:33'),
(6, 'prof_jones', 'admin123', 'Instructor', 'Prof. Sarah Jones', 'INST-002', 'Active', 'Tech Hub Office B', 'jones.s@eduportal.com', '2025-12-22 10:47:33'),
(7, 'dr_alan', 'admin123', 'Instructor', 'Dr. Alan Turing', 'INST-003', 'Active', 'CS Lab A', 'turing.a@eduportal.com', '2025-12-22 10:47:33'),
(8, 'mrs_davis', 'admin123', 'Instructor', 'Mrs. Emily Davis', 'INST-004', 'Active', 'Main Library Annex', 'davis.e@eduportal.com', '2025-12-22 10:47:33');

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
CREATE TABLE IF NOT EXISTS `student` (
  `StudentID` int NOT NULL AUTO_INCREMENT,
  `Username` varchar(50) NOT NULL,
  `Password` varchar(255) NOT NULL,
  `Role` varchar(50) DEFAULT 'Student',
  `full_name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `course_name` varchar(255) DEFAULT NULL,
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`StudentID`),
  UNIQUE KEY `Username` (`Username`)
) ENGINE=MyISAM AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `student`
--

INSERT INTO `student` (`StudentID`, `Username`, `Password`, `Role`, `full_name`, `email`, `course_name`, `CreatedAt`) VALUES
(1, 'student1', 'password123', 'Student', 'John Doe', 'john@edu.com', 'Advanced Java', '2025-12-22 10:34:00'),
(2, 'alice_w', 'pass123', 'Student', 'Alice Williams', 'alice@example.com', 'Computer Science', '2025-12-22 10:46:38'),
(3, 'bob_m', 'pass123', 'Student', 'Bob Miller', 'bob@example.com', 'Information Technology', '2025-12-22 10:46:38'),
(4, 'charlie_d', 'pass123', 'Student', 'Charlie Davis', 'charlie@example.com', 'Computer Science', '2025-12-22 10:46:38'),
(5, 'diana_p', 'pass123', 'Student', 'Diana Prince', 'diana@example.com', 'Physics', '2025-12-22 10:46:38'),
(6, 'ethan_h', 'pass123', 'Student', 'Ethan Hunt', 'ethan@example.com', 'Mathematics', '2025-12-22 10:46:38'),
(7, 'fiona_g', 'pass123', 'Student', 'Fiona Gallagher', 'fiona@example.com', 'Software Engineering', '2025-12-22 10:46:38'),
(8, 'george_k', 'pass123', 'Student', 'George Knight', 'george@example.com', 'Information Systems', '2025-12-22 10:46:38'),
(9, 'hannah_s', 'pass123', 'Student', 'Hannah Scott', 'hannah@example.com', 'Computer Science', '2025-12-22 10:46:38'),
(10, 'ian_m', 'pass123', 'Student', 'Ian Murray', 'ian@example.com', 'Physics', '2025-12-22 10:46:38'),
(11, 'jenny_l', 'pass123', 'Student', 'Jenny Lane', 'jenny@example.com', 'Mathematics', '2025-12-22 10:46:38');

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `UserID` int NOT NULL AUTO_INCREMENT,
  `Username` varchar(50) NOT NULL,
  `Password` varchar(255) NOT NULL,
  `FullName` varchar(100) DEFAULT NULL,
  `Role` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`UserID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
