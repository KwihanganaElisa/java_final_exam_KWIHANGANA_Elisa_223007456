-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Dec 19, 2025 at 09:07 PM
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
  `Role` varchar(50) DEFAULT 'Admin',
  `FullName` varchar(255) DEFAULT NULL,
  `Email` varchar(255) DEFAULT NULL,
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`AdminID`),
  UNIQUE KEY `Username` (`Username`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`AdminID`, `Username`, `Password`, `Role`, `FullName`, `Email`, `CreatedAt`) VALUES
(1, 'Aphrodis', 'hashed_password_here', 'Admin', NULL, NULL, '2025-12-19 15:28:52'),
(2, 'Admin1', 'hashed_password_here', 'Admin', NULL, NULL, '2025-12-19 15:28:52');

-- --------------------------------------------------------

--
-- Table structure for table `assignment`
--

DROP TABLE IF EXISTS `assignment`;
CREATE TABLE IF NOT EXISTS `assignment` (
  `AssignmentID` int NOT NULL AUTO_INCREMENT,
  `CourseID` int DEFAULT NULL,
  `Title` varchar(255) DEFAULT NULL,
  `Type` varchar(50) DEFAULT NULL,
  `MaxScore` int DEFAULT NULL,
  `ReferenceID` varchar(100) DEFAULT NULL,
  `Description` text,
  `DueDate` datetime DEFAULT NULL,
  `Status` varchar(50) DEFAULT NULL,
  `Remarks` text,
  `CreatedAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`AssignmentID`),
  KEY `CourseID` (`CourseID`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `assignment`
--

INSERT INTO `assignment` (`AssignmentID`, `CourseID`, `Title`, `Type`, `MaxScore`, `ReferenceID`, `Description`, `DueDate`, `Status`, `Remarks`, `CreatedAt`) VALUES
(1, 1, 'FrontEnd', 'homework', 10, '', 'FrontEnd', '2025-12-21 00:00:00', 'Active', '', '2025-12-19 15:42:29'),
(2, 7, 'backend', 'excercises', 12, '', 'backend', '2025-12-27 00:00:00', 'Active', '', '2025-12-19 17:58:24');

-- --------------------------------------------------------

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
CREATE TABLE IF NOT EXISTS `course` (
  `CourseID` int NOT NULL AUTO_INCREMENT,
  `Attribute1` varchar(255) DEFAULT NULL,
  `Attribute2` varchar(255) DEFAULT NULL,
  `Attribute3` varchar(255) DEFAULT NULL,
  `InstructorID` int DEFAULT NULL,
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`CourseID`),
  KEY `InstructorID` (`InstructorID`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `course`
--

INSERT INTO `course` (`CourseID`, `Attribute1`, `Attribute2`, `Attribute3`, `InstructorID`, `CreatedAt`) VALUES
(1, 'Sustainable Farming', 'Agri-Science', '1.0', 1, '2025-12-19 15:35:27'),
(2, 'Soil Health & Nutrients', 'Soil Management', '2.0', 1, '2025-12-19 15:35:27'),
(3, 'Irrigation Systems', 'Engineering', '4 Months', 3, '2025-12-19 15:35:27'),
(4, 'Market Logistics', 'Agribusiness', '4.0', 1, '2025-12-19 15:35:27'),
(6, 'Mathematics', '123', '4.5', 3, '2025-12-19 15:42:34'),
(7, 'English', '4556', '3.0', 1, '2025-12-19 16:37:50');

-- --------------------------------------------------------

--
-- Table structure for table `enrollment`
--

DROP TABLE IF EXISTS `enrollment`;
CREATE TABLE IF NOT EXISTS `enrollment` (
  `EnrollmentID` int NOT NULL AUTO_INCREMENT,
  `StudentID` int NOT NULL,
  `CourseID` int NOT NULL,
  `ReferenceID` varchar(255) DEFAULT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `EnrollDate` datetime DEFAULT NULL,
  `Status` varchar(50) DEFAULT NULL,
  `Remarks` text,
  PRIMARY KEY (`EnrollmentID`),
  KEY `StudentID` (`StudentID`),
  KEY `CourseID` (`CourseID`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `enrollment`
--

INSERT INTO `enrollment` (`EnrollmentID`, `StudentID`, `CourseID`, `ReferenceID`, `Description`, `EnrollDate`, `Status`, `Remarks`) VALUES
(1, 2, 7, '55667778', 'ghghhjdjhd', '2025-12-19 16:38:22', 'Active', 'hjdhhjdhj'),
(2, 1, 7, '445565', 'ghghhjhjjh', '2025-12-19 16:47:54', 'Active', 'gghhjhj'),
(3, 1, 1, NULL, NULL, '2025-12-19 19:49:56', 'Registered', NULL),
(4, 1, 7, NULL, NULL, '2025-12-19 19:50:33', 'Registered', NULL),
(5, 1, 2, NULL, NULL, '2025-12-19 19:51:37', 'Registered', NULL),
(6, 1, 7, NULL, NULL, '2025-12-19 19:52:38', 'Registered', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `grade`
--

DROP TABLE IF EXISTS `grade`;
CREATE TABLE IF NOT EXISTS `grade` (
  `GradeID` int NOT NULL AUTO_INCREMENT,
  `StudentID` int NOT NULL,
  `AssignmentID` int NOT NULL,
  `GradeType` varchar(50) DEFAULT NULL,
  `Score` decimal(5,2) DEFAULT NULL,
  `LetterGrade` varchar(5) DEFAULT NULL,
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`GradeID`),
  KEY `StudentID` (`StudentID`),
  KEY `AssignmentID` (`AssignmentID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
  `Name` varchar(255) NOT NULL,
  `Identifier` varchar(50) DEFAULT NULL,
  `Status` varchar(50) DEFAULT NULL,
  `Location` varchar(255) DEFAULT NULL,
  `Contact` varchar(255) DEFAULT NULL,
  `AssignedSince` datetime DEFAULT NULL,
  PRIMARY KEY (`InstructorID`),
  UNIQUE KEY `Username` (`Username`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `instructor`
--

INSERT INTO `instructor` (`InstructorID`, `Username`, `Password`, `Role`, `Name`, `Identifier`, `Status`, `Location`, `Contact`, `AssignedSince`) VALUES
(1, 'jdoe_agri', 'hash_pass_1', 'Instructor', 'John Doe', 'INST-001', 'Active', 'Kigali', '+250788111222', '2025-12-19 15:33:02'),
(2, 'asmith_tech', 'hash_pass_2', 'Instructor', 'Alice Smith', 'INST-002', 'Active', 'Musanze', '+250788333444', '2025-12-19 15:33:02'),
(3, 'mkarimu_edu', 'hash_pass_3', 'Senior Instructor', 'Musa Karimu', 'INST-003', 'On Leave', 'Huye', '+250788555666', '2025-12-19 15:33:02'),
(4, 'bnshuti_lead', 'hash_pass_4', 'Instructor', 'Béatrice Nshuti', 'INST-004', 'Active', 'Rubavu', '+250788777888', '2025-12-19 15:33:02');

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
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `student`
--

INSERT INTO `student` (`StudentID`, `Username`, `Password`, `Role`, `full_name`, `email`, `course_name`, `CreatedAt`) VALUES
(1, 'mugisha_01', 'pass123', 'Student', 'Mugisha Eric1', 'eric@example.com', 'Sustainable Farming', '2025-12-19 15:35:41'),
(2, 'uwineza_marie', 'secure789', 'Student', 'Uwineza Marie', 'marie@example.com', 'Market Logistics', '2025-12-19 15:35:41'),
(3, 'kwizera_john', 'study456', 'Student', 'Kwizera John', 'john.k@example.com', 'Soil Health', '2025-12-19 15:35:41'),
(4, 'ishimwe_hope', 'hope000', 'Student', 'Ishimwe Hope', 'hope@example.com', 'Irrigation Systems', '2025-12-19 15:35:41'),
(5, 'gasana_d', 'gasana11', 'Student', 'Gasana David', 'david@example.com', 'Advanced Crop Protection', '2025-12-19 15:35:41');

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
