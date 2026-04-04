-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 04, 2026 at 12:09 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `assignment`
--

-- --------------------------------------------------------

--
-- Table structure for table `course`
--

CREATE TABLE `course` (
  `id` int(11) NOT NULL,
  `name` varchar(150) NOT NULL,
  `code` varchar(150) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `course`
--

INSERT INTO `course` (`id`, `name`, `code`) VALUES
(1, 'Intro to Comp & Prob Solving', 'INFS 1101'),
(2, 'English Communication I', 'COMM 1010'),
(3, 'Pre-Calculus', 'MATH 1020'),
(4, 'English Communication II', 'COMM 1020'),
(5, 'Calculus I', 'MATH 1030'),
(6, 'Mathematics for IT', 'INFT 2102');

-- --------------------------------------------------------

--
-- Table structure for table `enrollment`
--

CREATE TABLE `enrollment` (
  `id` int(11) NOT NULL,
  `student_username` varchar(32) NOT NULL,
  `course_id` int(11) NOT NULL,
  `enrolled_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `grad` int(32) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `enrollment`
--

INSERT INTO `enrollment` (`id`, `student_username`, `course_id`, `enrolled_at`, `grad`) VALUES
(2, 'z', 2, '2026-04-04 06:05:40', 100),
(5, 'z', 5, '2026-04-04 07:49:29', 80),
(6, 'z', 1, '2026-04-04 09:16:33', 100),
(7, 'mohammed', 1, '2026-04-04 09:30:47', 0),
(8, 'mohammed', 5, '2026-04-04 09:30:51', 33);

-- --------------------------------------------------------

--
-- Table structure for table `payment`
--

CREATE TABLE `payment` (
  `id` int(11) NOT NULL,
  `student_username` varchar(32) NOT NULL,
  `description` varchar(500) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `method` varchar(100) NOT NULL,
  `status` varchar(50) NOT NULL DEFAULT 'paid',
  `paid_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `payment`
--

INSERT INTO `payment` (`id`, `student_username`, `description`, `amount`, `method`, `status`, `paid_at`) VALUES
(1, 'z', 'Spring 2025 tuition (partial)', 1200.00, 'Bank Transfer', 'paid', '2026-04-04 06:05:40');

-- --------------------------------------------------------

--
-- Table structure for table `transcript`
--

CREATE TABLE `transcript` (
  `id` int(11) NOT NULL,
  `student` varchar(150) NOT NULL,
  `transcript` varchar(150) NOT NULL,
  `status` varchar(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transcript`
--

INSERT INTO `transcript` (`id`, `student`, `transcript`, `status`) VALUES
(1, 'Ahmed Ali', 'Fall 2025 Transcript', 'Approved'),
(2, 'Sara Mohamed', 'Spring 2025 Transcript', 'Pending'),
(3, 'Khalid Hassan', 'Fall 2024 Transcript', 'Rejected'),
(4, 'Mariam Saleh', 'Summer 2025 Transcript', 'Pending'),
(5, 'Yousef Ahmed', 'Spring 2024 Transcript', 'Pending');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(32) NOT NULL,
  `username` varchar(32) NOT NULL,
  `password` varchar(256) NOT NULL,
  `role` varchar(32) NOT NULL,
  `email` varchar(150) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `role`, `email`) VALUES
(1, 'admin', '$2a$12$Y4HeTLDn1cmzV5qrOb7Un.xYcQl/s3nGamRPQ9dSpWV3WBIu9U9qC', 'admin', 'adminlast'),
(2, 'h', '$2a$12$yzVMEb74rMtczGNLyZqy.udMPnV804n4IYhIwvcVTIcQM.vBy3C9q', 'instructor', 'h'),
(3, 'q', '$2a$12$nGZhn8dz.CCPaiOr0ai8LuaOtSxS6089i3zPYsM9r3pTcVoCXy9DW', 'admin', 'q@gmail.com'),
(4, 'y', '$2a$12$zRXzcbctnH33lZkVlk3wY.GFbb4zg3mJEmVy9VxbEK/xy2r3OYcQK', 'y', 'y'),
(5, 'z', '$2a$12$L1hLPyS1EfNos725CUDenehqKeb6k1BXdw5ebQiBpUcJdmn8MnXlS', 'student', 'z'),
(6, 'mohammed', '$2a$12$f381.6vWYZ9/GPrLjBCUfO0VKzlm//PMWXAs/TCvR6pfib.ihL.NG', 'student', 'a@gmail.com');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `course`
--
ALTER TABLE `course`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `enrollment`
--
ALTER TABLE `enrollment`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_enrollment` (`student_username`,`course_id`),
  ADD KEY `course_id` (`course_id`);

--
-- Indexes for table `payment`
--
ALTER TABLE `payment`
  ADD PRIMARY KEY (`id`),
  ADD KEY `student_username` (`student_username`);

--
-- Indexes for table `transcript`
--
ALTER TABLE `transcript`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `course`
--
ALTER TABLE `course`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `enrollment`
--
ALTER TABLE `enrollment`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `payment`
--
ALTER TABLE `payment`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `transcript`
--
ALTER TABLE `transcript`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(32) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `enrollment`
--
ALTER TABLE `enrollment`
  ADD CONSTRAINT `enrollment_ibfk_1` FOREIGN KEY (`student_username`) REFERENCES `users` (`username`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `enrollment_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `payment`
--
ALTER TABLE `payment`
  ADD CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`student_username`) REFERENCES `users` (`username`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
