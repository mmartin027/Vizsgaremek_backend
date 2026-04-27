-- phpMyAdmin SQL Dump
-- version 5.1.2
-- https://www.phpmyadmin.net/
--
-- Gép: localhost:3306
-- Létrehozás ideje: 2026. Ápr 27. 10:29
-- Kiszolgáló verziója: 5.7.24
-- PHP verzió: 8.3.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Adatbázis: `parkolo_projekt`
--

DELIMITER $$
--
-- Eljárások
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `addBooking` (IN `p_user_id` INT, IN `p_parking_space_id` INT, IN `p_start_time` DATETIME, IN `p_end_time` DATETIME, IN `p_hours` INT, IN `p_total_price` INT, IN `p_license_plate` VARCHAR(20))   BEGIN
    INSERT INTO bookings(user_id, parking_space_id, start_time, end_time, hours, total_price, license_plate)
    VALUES(p_user_id, p_parking_space_id, p_start_time, p_end_time, p_hours, p_total_price, p_license_plate);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `addParkingSpace` (IN `p_parking_spot_id` INT, IN `p_code` VARCHAR(20), IN `p_type` VARCHAR(50), IN `p_size` VARCHAR(20))   BEGIN
    INSERT INTO parking_spaces(parking_spot_id, code, type, size)
    VALUES(p_parking_spot_id, p_code, p_type, p_size);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `addParkingSpot` (IN `p_city_id` INT, IN `p_name` VARCHAR(200), IN `p_address` VARCHAR(255), IN `p_hourly_rate` INT, IN `p_daily_rate` INT, IN `p_monthly_rate` INT, IN `p_capacity` INT)   BEGIN
    INSERT INTO parking_spots(city_id, name, address, hourly_rate, daily_rate, monthly_rate, capacity)
    VALUES (p_city_id, p_name, p_address, p_hourly_rate, p_daily_rate, p_monthly_rate, p_capacity);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `addRole` (IN `p_name` VARCHAR(100))   BEGIN
    INSERT INTO role(name) VALUES(p_name);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `addUser` (IN `p_first_name` VARCHAR(100), IN `p_last_name` VARCHAR(100), IN `p_email` VARCHAR(100), IN `p_password` MEDIUMTEXT, IN `p_phone` VARCHAR(30))   BEGIN
    INSERT INTO user(first_name, last_name, email, password, auth_secret, phone, guid, reg_token)
    VALUES(p_first_name, p_last_name, p_email, SHA2(p_password,256), UUID(), p_phone, UUID(), UUID());
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `addVehicle` (IN `p_user_id` INT, IN `p_license_plate` VARCHAR(20), IN `p_brand` VARCHAR(255), IN `p_model` VARCHAR(255), IN `p_color` VARCHAR(50), IN `p_year` INT)   BEGIN
    INSERT INTO vehicles(user_id, license_plate, brand, model, color, year)
    VALUES(p_user_id, p_license_plate, p_brand, p_model, p_color, p_year);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteBooking` (IN `p_id` INT)   BEGIN
    DELETE FROM bookings WHERE id = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteParkingSpace` (IN `p_id` INT)   BEGIN
    DELETE FROM parking_spaces WHERE id = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteParkingSpot` (IN `p_id` INT)   BEGIN
    DELETE FROM parking_spots WHERE id = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteRole` (IN `p_id` INT)   BEGIN
    UPDATE role SET is_deleted = 1, deleted_at = CURRENT_TIMESTAMP WHERE id = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteUser` (IN `p_id` INT)   BEGIN
    UPDATE user SET is_deleted = 1, deleted_at = CURRENT_TIMESTAMP WHERE id = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteVehicle` (IN `p_id` INT)   BEGIN
    DELETE FROM vehicles WHERE id = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getActiveBookingsByUser` (IN `p_user_id` INT)   BEGIN
    SELECT *
    FROM bookings
    WHERE user_id = p_user_id
      AND NOW() BETWEEN start_time AND end_time;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllBookings` ()   BEGIN
    SELECT b.id, u.first_name, u.last_name, ps.name AS parking_spot_name, b.start_time, b.end_time, b.total_price
    FROM bookings b
    JOIN user u ON b.user_id = u.id
    JOIN parking_spaces pspace ON b.parking_space_id = pspace.id
    JOIN parking_spots ps ON pspace.parking_spot_id = ps.id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllParkingSpaces` ()   BEGIN
    SELECT * FROM parking_spaces;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllParkingSpots` ()   BEGIN
    SELECT * FROM parking_spots;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllRoles` ()   BEGIN
    SELECT * FROM role WHERE is_deleted = 0;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllUsers` ()   BEGIN
    SELECT * FROM user WHERE is_deleted = 0;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllVehicles` ()   BEGIN
    SELECT * FROM vehicles;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getBookingCountByParkingSpot` ()   BEGIN
    SELECT ps.name AS parking_spot_name, COUNT(b.id) AS booking_count
    FROM parking_spots ps
    LEFT JOIN parking_spaces pspace ON ps.id = pspace.parking_spot_id
    LEFT JOIN bookings b ON pspace.id = b.parking_space_id
    GROUP BY ps.name;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getBookingsByUser` (IN `p_user_id` INT)   BEGIN
    SELECT b.id, ps.name AS parking_spot_name, pspace.code AS parking_space_code, b.start_time, b.end_time, b.total_price
    FROM bookings b
    JOIN parking_spaces pspace ON b.parking_space_id = pspace.id
    JOIN parking_spots ps ON pspace.parking_spot_id = ps.id
    WHERE b.user_id = p_user_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getParkingSpaceById` (IN `p_id` INT)   BEGIN
    SELECT * FROM parking_spaces WHERE id = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getParkingSpotById` (IN `p_id` INT)   BEGIN
    SELECT * FROM parking_spots WHERE id = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getRoleById` (IN `p_id` INT)   BEGIN
    SELECT * FROM role WHERE id = p_id AND is_deleted = 0;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getUserById` (IN `p_id` INT)   BEGIN
    SELECT * FROM user WHERE id = p_id AND is_deleted = 0;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getVehicleById` (IN `p_id` INT)   BEGIN
    SELECT * FROM vehicles WHERE id = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateParkingSpace` (IN `p_id` INT, IN `p_parking_spot_id` INT, IN `p_code` VARCHAR(20), IN `p_type` VARCHAR(50), IN `p_size` VARCHAR(20))   BEGIN
    UPDATE parking_spaces
    SET parking_spot_id = p_parking_spot_id,
        code = p_code,
        type = p_type,
        size = p_size
    WHERE id = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateParkingSpot` (IN `p_id` INT, IN `p_city_id` INT, IN `p_name` VARCHAR(200), IN `p_address` VARCHAR(255), IN `p_hourly_rate` INT, IN `p_daily_rate` INT, IN `p_monthly_rate` INT, IN `p_capacity` INT)   BEGIN
    UPDATE parking_spots
    SET city_id = p_city_id,
        name = p_name,
        address = p_address,
        hourly_rate = p_hourly_rate,
        daily_rate = p_daily_rate,
        monthly_rate = p_monthly_rate,
        capacity = p_capacity
    WHERE id = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateRole` (IN `p_id` INT, IN `p_name` VARCHAR(100))   BEGIN
    UPDATE role SET name = p_name WHERE id = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateUser` (IN `p_id` INT, IN `p_first_name` VARCHAR(100), IN `p_last_name` VARCHAR(100), IN `p_email` VARCHAR(100), IN `p_phone` VARCHAR(30))   BEGIN
    UPDATE user
    SET first_name = p_first_name,
        last_name = p_last_name,
        email = p_email,
        phone = p_phone
    WHERE id = p_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateVehicle` (IN `p_id` INT, IN `p_license_plate` VARCHAR(20), IN `p_brand` VARCHAR(255), IN `p_model` VARCHAR(255), IN `p_color` VARCHAR(50), IN `p_year` INT)   BEGIN
    UPDATE vehicles
    SET license_plate = p_license_plate,
        brand = p_brand,
        model = p_model,
        color = p_color,
        year = p_year
    WHERE id = p_id;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `bookings`
--

CREATE TABLE `bookings` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `parking_spot_id` int(11) DEFAULT NULL,
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `end_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `hours` int(11) DEFAULT NULL,
  `total_price` int(11) DEFAULT NULL,
  `license_plate` varchar(20) DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `qr_code` mediumtext,
  `access_code` varchar(10) DEFAULT NULL,
  `note` tinytext,
  `cancellation_reason` tinytext,
  `is_extended` tinyint(1) DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `cancelled_at` timestamp NULL DEFAULT NULL,
  `check_in_time` datetime(6) DEFAULT NULL,
  `check_out_time` datetime(6) DEFAULT NULL,
  `parking_type` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `cities`
--

CREATE TABLE `cities` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `latitude` decimal(10,8) DEFAULT NULL,
  `longitude` decimal(11,8) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- A tábla adatainak kiíratása `cities`
--

INSERT INTO `cities` (`id`, `name`, `latitude`, `longitude`, `is_active`, `created_at`) VALUES
(1, 'Budapest', NULL, NULL, 1, '2026-01-05 19:20:54'),
(2, 'Pécs', NULL, NULL, 1, '2026-04-01 22:05:04'),
(3, 'Debrecen', NULL, NULL, 1, '2026-04-01 22:07:15'),
(4, 'Győr', NULL, NULL, 1, '2026-04-14 10:33:14');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `forgot_password`
--

CREATE TABLE `forgot_password` (
  `fpid` bigint(20) NOT NULL,
  `expiration_time` datetime(6) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `otp_hash` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `parking_spots`
--

CREATE TABLE `parking_spots` (
  `id` int(11) NOT NULL,
  `city_id` int(11) DEFAULT NULL,
  `zone_id` int(11) DEFAULT NULL,
  `name` varchar(200) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `latitude` decimal(10,8) DEFAULT NULL,
  `longitude` decimal(11,8) DEFAULT NULL,
  `hourly_rate` int(11) DEFAULT NULL,
  `daily_rate` int(11) DEFAULT NULL,
  `monthly_rate` int(11) DEFAULT NULL,
  `parking_type` varchar(255) DEFAULT 'OUTDOOR',
  `features` tinytext,
  `capacity` int(11) DEFAULT NULL,
  `occupied_spaces` int(11) DEFAULT '0',
  `main_image_url` tinytext,
  `description` tinytext,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `uuid` varchar(36) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- A tábla adatainak kiíratása `parking_spots`
--

INSERT INTO `parking_spots` (`id`, `city_id`, `zone_id`, `name`, `address`, `latitude`, `longitude`, `hourly_rate`, `daily_rate`, `monthly_rate`, `parking_type`, `features`, `capacity`, `occupied_spaces`, `main_image_url`, `description`, `is_active`, `created_at`, `updated_at`, `uuid`) VALUES
(7, 1, NULL, 'Központi Garázs', 'Fő utca 123, Budapest', '46.07477501', '18.23680344', 1473, 10300, 58900, 'COVERED', 'CCTV, 0-24, Töltőállomás', 60, 46, 'parkolo1.jpg', NULL, 1, '2026-01-05 19:21:11', '2026-04-25 20:08:33', 'fb29d353-9d7f-4a3e-b080-98ee43ec0dc9'),
(8, 1, 1, 'Központi Parkoló', 'Deák Ferenc tér, Budapest', '46.07271231', '18.22393306', 600, 4200, 24000, 'OUTDOOR', 'Parkolóóra, Jól megvilágított', NULL, 14, 'a1b146f7-0aaf-4450-a027-b54854174c62.png', NULL, 1, '2026-01-05 19:21:11', '2026-04-25 20:08:33', '45b7ba3b-e9a3-4697-9b5c-62b1f4e16d14'),
(10, 2, NULL, 'Főtér Parkoló', '7621 Pécs, Széchenyi tér 5.', '46.06585890', '18.21739399', 450, 3200, 18000, 'COVERED', 'kamerás megfigyelés, 0-24 nyitva, mozgáskorlátozott helyek', 120, 57, 'parkolo4.jpg', 'Belvárosi, könnyen megközelíthető parkoló a Széchenyi tér mellett.', 1, '2026-01-16 23:33:28', '2026-04-25 20:08:33', '85cdc420-0f02-4d26-82e0-a0f7574d84f7'),
(60, 2, 2, 'Nyugati Zóna', 'Pécs', '46.07110675', '18.21732469', 400, 2800, 16000, 'OUTDOOR', '0-24', NULL, 0, 'parkolo14.jpg', NULL, 1, '2026-03-08 22:06:37', '2026-04-25 20:08:33', '9189f3fd-2fc3-4f90-81ba-3ef9af1b3cbf'),
(61, 2, 5, 'Északi Pihenő', 'Pécs', '46.07869616', '18.24586519', 400, 2800, 16000, 'OUTDOOR', 'CCTV, 0-24', NULL, 0, 'parkolo11.jpg', NULL, 1, '2026-03-08 22:06:37', '2026-04-25 20:08:33', '7780eece-c9ef-4517-bc50-a1e1a804054f'),
(62, 2, 3, 'Déli Lakóövezet', 'Pécs', '46.07102404', '18.21763445', 430, 3000, 17200, 'OUTDOOR', 'CCTV, 0-24', NULL, 0, 'parkolo7.jpg', NULL, 1, '2026-03-08 22:06:37', '2026-04-25 20:08:33', '2291183e-9ed6-4ed2-8739-a67a6064bc9c'),
(63, 1, 11, 'Külső-Zugló', 'Budapest', '47.50768363', '19.10679927', 550, 3900, 22000, 'OUTDOOR', '0-24, Rendszámfelismerő', NULL, 0, 'parkolo12.jpg', NULL, 1, '2026-03-08 22:06:37', '2026-04-25 20:08:33', 'fe06bc92-c6af-409d-884f-9dd6d4576f30'),
(64, 1, 8, 'Belső-Erzsébetváros', 'Budapest', '47.49713252', '19.07081322', 550, 3900, 22000, 'OUTDOOR', 'CCTV, Utcai parkolás', NULL, 0, 'parkolo18.jpg', NULL, 1, '2026-03-08 22:06:37', '2026-04-25 20:08:33', 'a8cdfb7b-a444-46bf-a70f-27a7cd8a562a'),
(65, 1, 9, 'Újlipótváros', 'Budapest', '47.51055551', '19.05561026', 660, 4600, 26400, 'OUTDOOR', '0-24, Rendszámfelismerő', NULL, 0, 'parkolo8.jpg', NULL, 1, '2026-03-08 22:06:37', '2026-04-25 20:08:33', '1747d383-c6d4-4994-88fd-1549396222ec'),
(69, 1, 10, 'Angyalföld', 'Budapest', '47.51602385', '19.06637006', 560, 3900, 22400, 'OUTDOOR', 'CCTV, 0-24', NULL, 0, 'parkolo7.jpg', NULL, 1, '2026-03-08 22:06:37', '2026-04-25 20:08:33', '2c732c0b-6d87-4d3d-be28-923a72d9cb8c'),
(70, 1, 7, 'Duna-parti Zóna', 'Budapest', '47.49824494', '19.03973496', 650, 4600, 26000, 'OUTDOOR', 'CCTV, Utcai parkolás', NULL, 0, 'parkolo16.jpg', NULL, 0, '2026-03-08 22:06:37', '2026-04-25 20:08:33', '607cc48e-e256-4ba4-a945-abe8a7cf2ee1'),
(71, 1, 7, 'Duna-parti Zóna', 'Budapest', '47.49959889', '19.05500139', 660, 4600, 26400, 'OUTDOOR', '0-24, Rendszámfelismerő', NULL, 0, 'parkolo8.jpg', NULL, 0, '2026-03-08 22:06:37', '2026-04-25 20:08:33', '2fa9c672-5e4c-4f2f-8577-cff469737c58'),
(72, 1, 7, 'Duna-parti Zóna', 'Budapest', '47.50823985', '19.02724903', 660, 4600, 26400, 'OUTDOOR', '0-24, Rendszámfelismerő', NULL, 0, 'parkolo19.jpg', NULL, 0, '2026-03-08 22:06:37', '2026-04-25 20:08:33', '05fe7d4b-464f-40e8-bfbf-e68b67bc45a2'),
(73, 3, 31, 'Debrecen Egyetem', 'Debrecen', '47.51939199', '21.65867528', 550, 3900, 22000, 'OUTDOOR', 'CCTV, Utcai parkolás', NULL, 0, 'parkolo5.jpg', NULL, 1, '2026-03-08 22:06:37', '2026-04-25 20:08:33', 'cded848e-eefa-490e-8bed-9c5645cfbd53'),
(74, 3, 30, 'Debrecen Nyugat', 'Debrecen', '47.52475112', '21.64118936', 500, 3500, 20000, 'OUTDOOR', 'CCTV, Utcai parkolás', NULL, 0, 'parkolo9.jpg', NULL, 1, '2026-03-08 22:06:37', '2026-04-25 20:08:33', '3cc37339-1f64-489a-b755-be947278e8e2'),
(75, 3, 26, 'Debrecen Belváros', 'Debrecen', '47.52165409', '21.62140159', 650, 4600, 26000, 'OUTDOOR', '0-24, Rendszámfelismerő', NULL, 0, 'parkolo11.jpg', NULL, 1, '2026-03-08 22:06:37', '2026-04-25 20:08:33', '8228c64e-cc0b-4331-a2f2-b21d8a83f901'),
(78, 1, NULL, 'Fedett Parkoló', 'Budapest, Bajcsy-Zsilinszky út 15.', '47.50925805', '19.05811380', 850, 6000, 34000, 'COVERED', 'Mélygarázs, Őrzött, Autómosó', NULL, 0, 'parkolo7.jpg', NULL, 1, '2026-03-08 22:06:37', '2026-04-25 20:08:33', '23760c1c-8d46-4cf3-ab98-a10c67602947'),
(107, 1, 7, 'Duna-parti Zóna', 'Budapest', NULL, NULL, 800, 5600, 32000, 'OUTDOOR', 'CCTV, 0-24', NULL, 0, 'parkolo15.jpg', NULL, 0, '2026-03-08 22:10:20', '2026-04-25 20:08:33', 'fec3a7f1-bd1d-44b1-9cfb-39f3d4407b64'),
(108, 1, 12, 'Rákosrendező', 'Budapest', NULL, NULL, 500, 3500, 20000, 'OUTDOOR', '0-24, Rendszámfelismerő', NULL, 0, 'parkolo18.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', 'deb29a77-0352-4eea-9e8e-3007c31be232'),
(109, 1, 13, 'Kőbánya Alsó', 'Budapest', NULL, NULL, 550, 3900, 22000, 'OUTDOOR', '0-24, Rendszámfelismerő', NULL, 0, 'parkolo10.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', '46118d42-721b-406b-868e-52ac3c7fe675'),
(110, 1, 18, 'Tabán', 'Budapest', NULL, NULL, 660, 4600, 26400, 'OUTDOOR', 'CCTV, Utcai parkolás', NULL, 0, 'parkolo8.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', '480aed6a-5129-4ea4-958e-d3ff0141c78d'),
(111, 1, 19, 'Krisztinaváros', 'Budapest', NULL, NULL, 750, 5300, 30000, 'OUTDOOR', '0-24, Rendszámfelismerő', NULL, 0, 'parkolo19.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', '613f639a-93cb-4214-b679-d4643ae520b1'),
(112, 1, 22, 'Óbuda', 'Budapest', '47.54763294', '19.02902625', 700, 4900, 28000, 'OUTDOOR', 'CCTV, 0-24', NULL, 0, 'parkolo5.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', '5b371ae8-5175-403a-bdbf-ef4321faabba'),
(113, 3, 28, 'Debrecen Kelet', 'Debrecen', '47.52164151', '21.63452961', 600, 4200, 24000, 'OUTDOOR', '0-24', NULL, 0, 'parkolo11.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', '35d4a45a-5bcc-4e2f-9acd-9eebb7340f92'),
(114, 3, 29, 'Debrecen Dél', 'Debrecen', '47.54548592', '21.64889220', 550, 3900, 22000, 'OUTDOOR', 'CCTV, 0-24', NULL, 0, 'parkolo18.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', '4988a165-f5da-417f-b1f1-f2b1c9186045'),
(115, 3, 32, 'Debrecen Ipari', 'Debrecen', '47.52882582', '21.60869339', 550, 3900, 22000, 'OUTDOOR', 'CCTV, Utcai parkolás', NULL, 0, 'parkolo7.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', '18f9f9db-c3a3-4912-8d85-16a19b73114f'),
(116, 1, NULL, 'Fedett Parkoló (5)', 'Budapest, Bem rakpart 22.', '47.51169132', '19.03414143', 800, 5600, 32000, 'COVERED', 'Őrzött, CCTV, Töltőállomás', 50, 0, 'parkolo9.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', '8636a525-fd62-48a9-8291-c0fad1e0444c'),
(117, 1, NULL, 'Fedett Parkoló (6)', 'Budapest, Andrássy út 45.', '47.51558386', '19.06075492', 850, 6000, 34000, 'COVERED', '0-24, Sorompós beléptetés, CCTV', 50, 0, 'parkolo19.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', '891bbea2-4330-4cd5-b63c-98de14eda3b6'),
(118, 1, NULL, 'Fedett Parkoló (7)', 'Budapest, Dózsa György út 10.', '47.50186039', '19.08450863', 750, 5300, 30000, 'COVERED', 'CCTV, Elektromos töltő, Biztonsági őr', 50, 0, 'parkolo18.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', '71d0f92c-14c3-4433-976d-8d3f5a86c28e'),
(119, 1, NULL, 'Fedett Parkoló (8)', 'Budapest, Podmaniczky utca 33.', '47.50851301', '19.06969585', 800, 5600, 32000, 'COVERED', 'Őrzött, CCTV, Töltőállomás', 50, 0, 'parkolo13.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', 'ed3c20e7-1149-43d9-841d-eaaf263d0685'),
(120, 1, NULL, 'Fedett Parkoló (9)', 'Budapest, Váci út 80.', '47.54429721', '19.07914032', 700, 4900, 28000, 'COVERED', 'CCTV, Elektromos töltő, Biztonsági őr', 50, 0, 'parkolo9.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', 'f7e4d2d0-d3ea-4318-be99-d88c8a705a61'),
(121, 1, NULL, 'Fedett Parkoló (10)', 'Budapest, Róbert Károly körút 44.', '47.53976999', '19.09049973', 700, 4900, 28000, 'COVERED', 'CCTV, Elektromos töltő, Biztonsági őr', 50, 0, 'parkolo14.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', '9b2f2e89-de6f-433b-8d7c-8f1c97f51fb5'),
(122, 1, NULL, 'Fedett Parkoló (11)', 'Budapest, Kerepesi út 20.', '47.52567423', '19.12001568', 660, 4600, 26400, 'COVERED', 'CCTV, Elektromos töltő, Biztonsági őr', 50, 0, 'parkolo9.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', '44771d6c-0ff1-4e21-a197-032cee9a2c01'),
(123, 1, NULL, 'Fedett Parkoló (12)', 'Budapest, Margit körút 55.', '47.53772176', '19.03805787', 850, 6000, 34000, 'COVERED', 'Őrzött, CCTV, Töltőállomás', 50, 0, 'parkolo14.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', 'a25d4beb-7e3c-4063-9eb5-99e9f980d877'),
(124, 1, NULL, 'Fedett Parkoló (13)', 'Budapest, Pacsirtamező utca 12.', '47.54425216', '19.03367214', 800, 5600, 32000, 'COVERED', 'CCTV, Elektromos töltő, Biztonsági őr', 50, 0, 'parkolo8.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', '8a7d34bf-474e-4b12-8a4f-2f8105bfa5dc'),
(125, 3, NULL, 'Fedett Parkoló (14)', 'Debrecen, Piac utca 20.', '47.53139223', '21.62771970', 800, 5600, 32000, 'COVERED', 'Mélygarázs, Őrzött, Autómosó', 50, 0, 'parkolo8.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', 'a30a8237-a6db-4c3d-bcd3-f36b917f5bd0'),
(126, 3, NULL, 'Fedett Parkoló (15)', 'Debrecen, Kassai út 26.', '47.53668028', '21.62587954', 800, 5600, 32000, 'COVERED', 'Mélygarázs, Őrzött, Autómosó', 50, 0, 'parkolo10.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', 'df4d3a6a-d410-4100-ac1e-043d16bddf49'),
(127, 3, NULL, 'Fedett Parkoló (16)', 'Debrecen, Egyetem tér 1.', '47.53050439', '21.61878103', 800, 5600, 32000, 'COVERED', 'CCTV, Elektromos töltő, Biztonsági őr', 50, 0, 'parkolo8.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', '62974f60-df6e-4cb5-a06f-6a6a8f1fe351'),
(128, 3, NULL, 'Fedett Parkoló (17)', 'Debrecen, Böszörményi út 42.', '47.53286447', '21.63969992', 770, 5400, 30800, 'COVERED', 'Mélygarázs, Őrzött, Autómosó', 50, 0, 'parkolo8.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', 'f0eddc37-1851-4608-a134-5c7d0d9a27ed'),
(129, 3, NULL, 'Fedett Parkoló (18)', 'Debrecen, Füredi út 15.', '47.54165474', '21.62186658', 750, 5300, 30000, 'COVERED', '0-24, Sorompós beléptetés, CCTV', 50, 0, 'parkolo8.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', '946a96db-d68e-4939-bdaf-ba7013e5fd0e'),
(130, 3, NULL, 'Fedett Parkoló (19)', 'Debrecen, Csapó utca 30.', '47.50795594', '21.64175233', 700, 4900, 28000, 'COVERED', 'Őrzött, CCTV, Töltőállomás', 50, 0, 'parkolo11.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', 'ea0a0cf4-c702-4b98-a6ea-bd72bf7c1065'),
(131, 3, NULL, 'Fedett Parkoló (20)', 'Debrecen, Rakovszky Dániel utca 11.', '47.51700175', '21.64531380', 700, 4900, 28000, 'COVERED', 'Mélygarázs, Őrzött, Autómosó', 50, 0, 'parkolo12.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', '426621c3-822d-49da-8771-3c039ebd55ca'),
(132, 3, NULL, 'Fedett Parkoló (21)', 'Debrecen, Vágóhíd utca 4.', '47.53783652', '21.65148138', 750, 5300, 30000, 'COVERED', 'Őrzött, CCTV, Töltőállomás', 50, 0, 'parkolo7.jpg', NULL, 1, '2026-03-08 22:10:20', '2026-04-25 20:08:33', '88638884-4145-4766-b225-358411b6cb2a'),
(133, 2, NULL, 'Szigeti ut', 'szigeti ', '46.06967290', '18.19676376', 1, 0, 0, NULL, 'Közvilágítás, 0-24', 45, 0, 'parkolo12.jpg', NULL, 1, '2026-03-20 23:17:36', '2026-04-25 20:08:33', NULL),
(142, 4, 35, 'Dunakapu Mélygarázs', '9022 Győr, Dunakapu tér', '47.69001234', '17.63212345', 500, 3500, 20000, 'COVERED', '0-24, CCTV, Elektromos töltő, Rendszámfelismerő', NULL, 45, NULL, NULL, 1, '2026-04-23 13:48:32', '2026-04-25 20:08:33', '1dfe4e89-3f1b-11f1-9e4e-acf23cd1462a'),
(143, 4, 35, 'Jókai Parkolóház', '9022 Győr, Jókai u. 13.', '47.68812345', '17.63412345', 500, 3500, 20000, 'COVERED', '0-24, CCTV, Akadálymentes', NULL, 112, NULL, NULL, 1, '2026-04-23 13:48:32', '2026-04-25 20:08:33', '1dfe7790-3f1b-11f1-9e4e-acf23cd1462a'),
(144, 4, 36, 'Vásárcsarnok Parkoló', '9023 Győr, Hermann Ottó u.', '47.68312345', '17.62512345', 450, 3200, 18000, 'OUTDOOR', 'CCTV, Utcai parkolás', NULL, 20, NULL, NULL, 1, '2026-04-23 13:48:32', '2026-04-25 20:08:33', '1dfe78ed-3f1b-11f1-9e4e-acf23cd1462a'),
(145, 4, 37, 'Egyetemi Parkoló', '9026 Győr, Egyetem tér 1.', '47.68012345', '17.62712345', 400, 2800, 16000, 'OUTDOOR', '0-24, Rendszámfelismerő, Tágas helyek', NULL, 60, NULL, NULL, 1, '2026-04-23 13:48:32', '2026-04-25 20:08:33', '1dfe87e7-3f1b-11f1-9e4e-acf23cd1462a'),
(146, 4, 38, 'Árkád Parkolóház', '9027 Győr, Budai út 1.', '47.68112345', '17.64012345', 430, 3000, 17200, 'COVERED', '0-24, CCTV, Bevásárlóközpont', NULL, 150, '73b6b3e4-38ab-4c98-848f-fc7aef874320.png', NULL, 1, '2026-04-23 13:48:32', '2026-04-26 08:52:39', '1dfe89e2-3f1b-11f1-9e4e-acf23cd1462a'),
(148, 3, 42, 'Kertvárosi Parkoló övezet', '7632 Pécs, Kertváros', '46.05230500', '18.21732500', 100, 700, 4000, 'OUTDOOR', 'Kamera, Aszfaltozott', NULL, 0, NULL, NULL, 1, '2026-04-23 17:56:05', '2026-04-25 23:08:34', 'b330c68b-3f3d-11f1-9e4e-acf23cd1462a');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `payments`
--

CREATE TABLE `payments` (
  `id` int(11) NOT NULL,
  `booking_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `amount` int(11) DEFAULT NULL,
  `payment_method` varchar(20) DEFAULT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'pending',
  `transaction_id` varchar(255) NOT NULL,
  `card_last4` varchar(4) DEFAULT NULL,
  `card_type` varchar(20) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `refresh_token`
--

CREATE TABLE `refresh_token` (
  `id` bigint(20) NOT NULL,
  `token` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `expiry_date` datetime(6) NOT NULL,
  `user_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- A tábla adatainak kiíratása `refresh_token`
--

INSERT INTO `refresh_token` (`id`, `token`, `expiry_date`, `user_id`) VALUES
(1, '5237b8ac-9d42-4bef-b2fe-29fe100c5ee3', '2026-05-04 00:02:47.248874', 15),
(2, 'c88d3623-6692-43d3-86a5-23f5dba8026c', '2026-05-04 00:15:56.509614', 61),
(4, '87f890ce-fd8b-4c8a-91ae-4a80f2b88b25', '2026-05-04 00:28:51.078618', 62);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `role`
--

CREATE TABLE `role` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted_at` datetime DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- A tábla adatainak kiíratása `role`
--

INSERT INTO `role` (`id`, `name`, `created_at`, `deleted_at`, `is_deleted`) VALUES
(1, 'ROLE_USER', '2026-03-17 10:32:53', NULL, 0),
(2, 'ROLE_ADMIN', '2026-03-17 10:32:53', NULL, 0);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` tinytext,
  `auth_secret` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `guid` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted_at` datetime DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `last_login` datetime DEFAULT NULL,
  `register_finished_at` datetime DEFAULT NULL,
  `reg_token` varchar(255) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  `provider` varchar(255) DEFAULT NULL,
  `is_verified` bit(1) DEFAULT NULL,
  `otp_expiration` datetime(6) DEFAULT NULL,
  `registrationotphash` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- A tábla adatainak kiíratása `user`
--

INSERT INTO `user` (`id`, `first_name`, `last_name`, `email`, `password`, `auth_secret`, `phone`, `guid`, `created_at`, `deleted_at`, `is_deleted`, `last_login`, `register_finished_at`, `reg_token`, `username`, `provider`, `is_verified`, `otp_expiration`, `registrationotphash`) VALUES
(15, 'sf', 'df', 'kecske@gmail.com', '$2a$12$waG2JNlI4Y/b6417Xca5Z.m79go33OHreuYbRV1cVrLqNO/j2viJW', '81f31c18-23ed-441c-b88e-57e3b2ce303b', '+3631455675', 'bdec3afa-0fb9-492b-99b2-cf57934a3a16', '2026-03-09 16:43:00', NULL, 0, NULL, '2026-03-09 16:43:00', NULL, 'tibi', 'LOCAL', b'1', NULL, NULL),
(61, 'sdgsdg', 'martin', 'malajmartin1@gmail.com', '$2a$12$5HDpj7dgP7An1Lt0cdVVdeRJm66ZXwzaTKNm8.FHXb/SAmnJ8x1Km', '951d6ed6-826c-4a49-9cae-42573ee7c1ba', '+36346346', '89283585-0396-4575-8295-92107352271e', '2026-04-27 00:15:20', NULL, 0, NULL, '2026-04-27 00:15:21', NULL, 'martin09', 'LOCAL', b'1', NULL, NULL),
(62, 'maláj', 'martin', 'malajmartin3@gmail.com', NULL, '1b70f2b6-6a3e-4b01-8d5a-1d6028ab8c19', NULL, 'cc1e8fef-6544-4db3-aca1-78a7e092359c', '2026-04-27 00:16:45', NULL, 0, '2026-04-27 00:28:51', '2026-04-27 00:16:45', NULL, 'malajmartin3@gmail.com', 'GOOGLE', b'0', NULL, NULL);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `user_x_role`
--

CREATE TABLE `user_x_role` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- A tábla adatainak kiíratása `user_x_role`
--

INSERT INTO `user_x_role` (`id`, `user_id`, `role_id`) VALUES
(1, 15, 2),
(35, 61, 1),
(36, 62, 1);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `vehicles`
--

CREATE TABLE `vehicles` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `license_plate` varchar(20) DEFAULT NULL,
  `brand` varchar(255) DEFAULT NULL,
  `model` varchar(255) DEFAULT NULL,
  `color` varchar(50) DEFAULT NULL,
  `year` int(11) DEFAULT NULL,
  `is_default` tinyint(1) DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `zones`
--

CREATE TABLE `zones` (
  `id` int(11) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `zone_code` varchar(25) DEFAULT NULL,
  `hourly_rate` int(25) DEFAULT NULL,
  `polygon_data` text,
  `features` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- A tábla adatainak kiíratása `zones`
--

INSERT INTO `zones` (`id`, `name`, `zone_code`, `hourly_rate`, `polygon_data`, `features`) VALUES
(1, 'Központi Parkoló', 'A1', 500, '{\"type\":\"Polygon\",\"coordinates\":[[[18.22393306435862,46.07271231093344],[18.22559087993767,46.0727638520371],[18.22718647294056,46.07292074962342],[18.2288816998622,46.07313498271188],[18.229658708613783,46.07322416394004],[18.23004423051097,46.07330148033424],[18.230460547546983,46.07337963015098],[18.232610654545596,46.073798679325364],[18.234483414693386,46.07424909412137],[18.23995062472531,46.0747922483163],[18.239300424537504,46.07623985664932],[18.238865553548237,46.07690617063844],[18.23878627714288,46.07754216239309],[18.23810602739536,46.07757128405592],[18.23810602739536,46.07757128405592],[18.235216826332163,46.07787467064878],[18.234973525425175,46.07921278304016],[18.234573816793983,46.080020456796575],[18.233617991805033,46.08043031656885],[18.2324710018176,46.080731682106176],[18.226910752809317,46.079863214290356],[18.226694630210716,46.07983397525584],[18.22663473082096,46.07975954720081],[18.22585852769467,46.079655717092805],[18.224217865476504,46.079397051029076],[18.22320296013507,46.07927265345302],[18.222004397726067,46.079199849867905],[18.220994538348776,46.07902955968689],[18.220979819911435,46.07878112376281],[18.221054872245247,46.07854396287064],[18.221094121412023,46.07833296071129],[18.22107496746534,46.07811491207673],[18.221038816447475,46.0777298617501],[18.220880447613354,46.07733692924273],[18.2207478799547,46.07688809119165],[18.220757525220932,46.07671245612991],[18.220825042078474,46.076544731801135],[18.221089301316965,46.0758357451698],[18.221139938970765,46.07566177950787],[18.221238802959306,46.075486140543006],[18.221433888473825,46.0751322378199],[18.221129091999074,46.075058692819994],[18.221305785607825,46.074690966350005],[18.221465165210446,46.0742446669658],[18.221711127537475,46.073800989704864],[18.222201265317807,46.073506244134535],[18.222668911557065,46.07325251362701],[18.22316993390112,46.07300881977392],[18.223554713588385,46.07285070877353],[18.22393306435862,46.07271231093344]]]}', NULL),
(2, 'Nyugati Zóna', 'A2', 400, '{\"type\":\"Polygon\",\"coordinates\":[[[18.217324693858046,46.07110675294649],[18.21627134877525,46.07089958543506],[18.21487688909332,46.0707757105275],[18.214087101324083,46.070896079011106],[18.21335747327339,46.07117866088697],[18.212338088376413,46.07115361822821],[18.21127802395864,46.071095511284284],[18.209874084507135,46.07099483604503],[18.208899858684276,46.07090348393638],[18.207238762224478,46.075852474245494],[18.210467655973787,46.076349623517956],[18.21130312347671,46.076338495221876],[18.214255802231804,46.0770695221988],[18.214599677266705,46.07711208574713],[18.215227739054114,46.07477958038123],[18.216068000402345,46.07316540634278],[18.217324693858046,46.07110675294649]]]}', NULL),
(3, 'Déli Lakóövezet', 'A3', 430, '{\"type\":\"Polygon\",\"coordinates\":[[[18.217634454667802,46.071024042939285],[18.217324693858046,46.07110675294649],[18.217289923375347,46.070891669419495],[18.218274585478326,46.069188042796526],[18.21859064985574,46.06869043895949],[18.2191674309976,46.0682864189734],[18.219842751049327,46.068007280622],[18.22331945921377,46.067214468989306],[18.22548328457526,46.06778799343746],[18.229862943227204,46.06792366750426],[18.230360061045985,46.068489019115844],[18.230882782902995,46.06851432120641],[18.23087062658047,46.06872517150987],[18.230749192391244,46.069045393868606],[18.2304829156611,46.0695514283762],[18.229935881160685,46.071162274001125],[18.217634454667802,46.071024042939285]]]}', NULL),
(4, 'Kereskedelmi Zóna', 'A4', 450, '{\"type\":\"Polygon\",\"coordinates\":[[[18.23970411608161,46.071949172507146],[18.23970755442224,46.07183538949383],[18.23942124250405,46.071711831741],[18.239177764651373,46.07164301464516],[18.238882299460784,46.071622635273684],[18.23787908053322,46.07159761084486],[18.237366595118658,46.0715805629859],[18.23680749782818,46.0715836910411],[18.236406210257996,46.07148672124677],[18.23618753107587,46.07141946790247],[18.23607977961683,46.07440771720446],[18.23995062472531,46.0747922483163],[18.23995062472531,46.0747922483163],[18.23924500286509,46.07468964425553],[18.239387031611557,46.07466305727661],[18.239472699745335,46.07457547655065],[18.23959357102862,46.07443734501288],[18.239713055529734,46.07429033382746],[18.239785197114884,46.074218392041075],[18.239775482108,46.07413743740457],[18.239806340541037,46.07356075731394],[18.23970411608161,46.071949172507146]]]}', NULL),
(5, 'Északi Pihenő', 'A5', 400, '{\"type\":\"Polygon\",\"coordinates\":[[[18.245865195988557,46.078696160986965],[18.24354364900867,46.078277221130406],[18.242330606616008,46.0781514479323],[18.24063691031708,46.077743199206964],[18.239743522873965,46.07754245198828],[18.23878627714288,46.07754216239309],[18.23810602739536,46.07757128405592],[18.235216826332163,46.07787467064878],[18.234973525425175,46.07921278304016],[18.234573816793983,46.080020456796575],[18.234973525425175,46.07921278304016],[18.236010585342513,46.07933946104268],[18.237736419523088,46.079315471425616],[18.239743051437642,46.079797775464186],[18.24071607879921,46.08028982974349],[18.241490188121247,46.080517230576675],[18.24434963114473,46.08200669244542],[18.245865195988557,46.078696160986965]]]}', NULL),
(6, 'Egyetemi Negyed', 'A6', 400, '{\"type\":\"Polygon\",\"coordinates\":[[[18.217393997346534,46.06585890883113],[18.21341363813025,46.06490986394951],[18.213482029181733,46.06718754425577],[18.217352962715495,46.06732989615443],[18.217393997346534,46.06585890883113]]]}', NULL),
(7, 'Duna-parti Zóna', 'B1', 800, '{\"type\":\"Polygon\",\"coordinates\":[[[19.046475815401806,47.51372455404518],[19.048963745408088,47.51289942672611],[19.049924418577575,47.512541677430306],[19.050902761871328,47.51217157984928],[19.05190046600123,47.511838446758986],[19.05245542468225,47.5116286694558],[19.05285535472467,47.51148522146161],[19.053814720650394,47.511166570644946],[19.054851780622073,47.51080515228412],[19.05561026863367,47.510555511345046],[19.0556236309437,47.51033442087592],[19.056515572753,47.509963122016245],[19.056878183513817,47.50977177027113],[19.05702266123484,47.509672267088035],[19.05714164289043,47.5095823313566],[19.058062032980814,47.5088640971386],[19.06029629110435,47.507304436932884],[19.062393171376385,47.50578065118955],[19.063356182531432,47.505064069925936],[19.063356182531432,47.505064069925936],[19.0633326857449,47.50508155432756],[19.063180764594193,47.505088223033766],[19.062987280073827,47.50497112287232],[19.062573456817603,47.50470121331358],[19.06146348643955,47.503952475052586],[19.059330156202606,47.50248238658767],[19.05500139340569,47.49959889492206],[19.054421809064763,47.49926062704313],[19.053681203445052,47.49910836049284],[19.051777427470427,47.49899415550294],[19.048285645154493,47.49872239922587],[19.047447578581227,47.50081580885424],[19.046887150785267,47.50184984258712],[19.045889905576303,47.501671395271785],[19.045599929749187,47.50241713994885],[19.045095303904986,47.50387223731289],[19.045070242507677,47.50481370610743],[19.04618880716413,47.50487789285174],[19.04612061451934,47.5055592146326],[19.048057293760195,47.50578765153929],[19.047933989778073,47.50705539225356],[19.047788037604732,47.508576268128394],[19.04536517886865,47.508957857898196],[19.046475815401806,47.51372455404518]]]}', NULL),
(8, 'Belső-Erzsébetváros', 'B3', 550, '{\"type\":\"Polygon\",\"coordinates\":[[[19.070813220638655,47.49713252573085],[19.07039999322788,47.497196953608125],[19.070376153185066,47.49745466432586],[19.070296686375826,47.497937868512366],[19.069939085732756,47.498764674250936],[19.06866738143097,47.500437288397734],[19.06769523869138,47.501397951000065],[19.06571853321097,47.503068176215365],[19.063356182531432,47.505064069925936],[19.062393171376385,47.50578065118955],[19.061357022903042,47.50651835907615],[19.06029629110435,47.507304436932884],[19.058062032980814,47.5088640971386],[19.058889343762104,47.509369189871194],[19.059711351268362,47.50988502424008],[19.059774990559873,47.50991368155627],[19.06152906880763,47.51098405529771],[19.06516890801757,47.51316359132849],[19.06620362960291,47.51378617046521],[19.0671559317731,47.51433060706984],[19.06764824191717,47.51463606383487],[19.067892192531986,47.514772173625175],[19.068178569340574,47.514911864884965],[19.06921831199847,47.515480130163866],[19.073417125715537,47.51761290699736],[19.07541208230282,47.51587805753485],[19.07557172318633,47.5157074426445],[19.077493397246087,47.51403799163822],[19.081286568120134,47.510649889478145],[19.088814980523125,47.50425605417294],[19.090165789214318,47.50289701878325],[19.091111347617982,47.501945650434635],[19.090822217115914,47.501832745931495],[19.087220302362283,47.50188113606163],[19.086074238578448,47.501874223188594],[19.085409112273993,47.501874223188594],[19.084989571066984,47.50181892017184],[19.084508633585983,47.501860397439884],[19.0806556693974,47.50006682771112],[19.070813220638655,47.49713252573085]]]}', NULL),
(9, 'Újlipótváros', 'B4', 660, '{\"type\":\"Polygon\",\"coordinates\":[[[19.05561026863367,47.510555511345046],[19.054851780622073,47.51080515228412],[19.053814720650394,47.511166570644946],[19.05285535472467,47.51148522146161],[19.05245542468225,47.5116286694558],[19.052286451817878,47.51168940205436],[19.052117478953505,47.511750134652914],[19.05190046600123,47.511838446758986],[19.050902761871328,47.51217157984928],[19.049924418577575,47.512541677430306],[19.048963745408088,47.51289942672611],[19.048006333282274,47.51321775491715],[19.050807744033676,47.517369271983824],[19.05157199298128,47.51840153155962],[19.05208692789293,47.51947135725632],[19.05325161968068,47.52158273905704],[19.061214384628897,47.5196185206662],[19.0600872092798,47.51718292814556],[19.059563714467714,47.5160424247787],[19.059335740922023,47.51550637963035],[19.058964228476782,47.514987437085466],[19.05561026863367,47.510555511345046]]]}', NULL),
(10, 'Angyalföld', 'B5', 560, '{\"type\":\"Polygon\",\"coordinates\":[[[19.066370060493966,47.51602385107813],[19.06368619109884,47.517921958832716],[19.063680217065894,47.51792519768338],[19.063538471430604,47.51785027907525],[19.06326302934653,47.51771165405546],[19.062757675340436,47.51750354570066],[19.061829173526206,47.51699880544061],[19.06135254469001,47.51697078455081],[19.0609642849044,47.517145597399406],[19.0600872092798,47.51718292814556],[19.061214384628897,47.5196185206662],[19.05896985753631,47.52017117720146],[19.05666311145876,47.52074129337315],[19.05325161968068,47.52158273905704],[19.05325161968068,47.52158273905704],[19.05325161968068,47.52158273905704],[19.05208692789293,47.51947135725632],[19.05157199298128,47.51840153155962],[19.050807744033676,47.517369271983824],[19.049303827252825,47.51514065070786],[19.048006333282274,47.51321775491715],[19.046475815401806,47.51372455404518],[19.046475815401806,47.51372455404518],[19.047204673935113,47.51496979708821],[19.048733540143076,47.5172156789414],[19.050311496034396,47.51944435722308],[19.052153999875088,47.521934794566306],[19.053530813994797,47.524206046701806],[19.05449208906279,47.5264463302845],[19.055671863160683,47.5289003931461],[19.056597854997676,47.531368896111104],[19.058039002571405,47.536344102887114],[19.061981049341426,47.535395740764244],[19.063185554189978,47.53475132102378],[19.06425955003658,47.53417670062447],[19.065762644027156,47.53316049948443],[19.066506221620934,47.53265776499568],[19.066877265450763,47.53240689602268],[19.067269887828157,47.53214143351399],[19.068718250510102,47.53116212188021],[19.07032088345764,47.53018651542472],[19.07343646164884,47.52828973235842],[19.078041835310778,47.52528996132235],[19.082359579769616,47.522477062235964],[19.074850993685857,47.51960250494247],[19.072750393548375,47.518557199312255],[19.071317526606407,47.5181332089642],[19.068802334337306,47.51708787406304],[19.066370060493966,47.51602385107813]]]}', NULL),
(11, 'Külső-Zugló', 'B7', 550, '{\"type\":\"Polygon\",\"coordinates\":[[[19.106799270267402,47.50768363550273],[19.106610289610643,47.50753796431235],[19.105669730205364,47.508279190812516],[19.104482833813023,47.50922461758549],[19.102196833904173,47.51098886732521],[19.100029019906998,47.51237536264114],[19.097912761245084,47.5139635459326],[19.084261920553537,47.52323295915658],[19.08634260858997,47.524059661568344],[19.088021815092404,47.524726800232685],[19.089535757606313,47.5253282449515],[19.091374017744755,47.526569522772235],[19.095361954785552,47.531548510377625],[19.09649778585944,47.53295476528316],[19.097383515586444,47.53403829746696],[19.09942264210179,47.536523720033685],[19.103483329418026,47.54149892968974],[19.143339656719434,47.5251348913356],[19.106799270267402,47.50768363550273]]]}', NULL),
(12, 'Rákosrendező', 'B8', 500, '{\"type\":\"Polygon\",\"coordinates\":[[[19.0858624934053,47.50674608189195],[19.08627661295111,47.507195698467655],[19.087595383776318,47.5080095783284],[19.08984310350064,47.509888699770954],[19.092448282296687,47.511825335225694],[19.093810027190244,47.51288715816629],[19.092529283247615,47.51402859974715],[19.090782576271863,47.51505837012013],[19.08699586116552,47.51768713150008],[19.086047320954208,47.518431104982454],[19.086288727127624,47.51889033376614],[19.08660929951634,47.519198628744874],[19.088710709517503,47.520212608668714],[19.09090313982905,47.518723943162236],[19.09200588804716,47.51797512671706],[19.092547034958343,47.51760765154465],[19.092820886528383,47.5174216849665],[19.093119355779294,47.5172189988189],[19.097912761245084,47.5139635459326],[19.100029019906998,47.51237536264114],[19.101228351124682,47.51160830577459],[19.102196833904173,47.51098886732521],[19.104482833813023,47.50922461758549],[19.106610289610643,47.50753796431235],[19.113990321612107,47.500778783416365],[19.12183607112425,47.48963829481025],[19.1190894666355,47.489014855427115],[19.11577973882558,47.48874744863119],[19.112613912228028,47.48872313885337],[19.11129481781026,47.488439523966036],[19.109520036231004,47.48842331735486],[19.10860100272606,47.4883510748341],[19.107876337991115,47.487139734779106],[19.10747421325715,47.48649149272924],[19.10747421325715,47.48649149272924],[19.10747421325715,47.48649149272924],[19.10747421325715,47.48649149272924],[19.10747421325715,47.48649149272924],[19.105175056393236,47.48710548261315],[19.10336484554003,47.48758885698322],[19.10336484554003,47.48758885698322],[19.101215829905385,47.48811073772123],[19.09802585019881,47.48861673525644],[19.094634760975467,47.488940189915134],[19.09243505171139,47.48914995073565],[19.091243019602018,47.489354159485174],[19.081417031747193,47.49928729745448],[19.10263599181536,47.49955490734024],[19.10252220610721,47.50031180686585],[19.095225344058406,47.50146457694569],[19.091111347617982,47.501945650434635],[19.088814980523125,47.50425605417294],[19.088814980523125,47.50425605417294],[19.087305162208253,47.50553841621078],[19.0858624934053,47.50674608189195]]]}', NULL),
(13, 'Kőbánya Alsó', 'B9', 550, '{\"type\":\"Polygon\",\"coordinates\":[[[19.08203357134394,47.4815554914789],[19.088888910635774,47.479157004390004],[19.088888910635774,47.479157004390004],[19.088888910635774,47.479157004390004],[19.088888910635774,47.479157004390004],[19.088888910635774,47.479157004390004],[19.098534859701545,47.475543992863805],[19.100566351042772,47.47824472156884],[19.102849899397228,47.481018021856585],[19.10747421325715,47.48649149272924],[19.10336484554003,47.48758885698322],[19.101215829905385,47.48811073772123],[19.09802585019881,47.48861673525644],[19.09243505171139,47.48914995073565],[19.091243019602018,47.489354159485174],[19.091243019602018,47.489354159485174],[19.08924949110238,47.491369798715674],[19.084948396265982,47.49571795343441],[19.08279798401363,47.49789156386848],[19.081417031747193,47.49928729745448],[19.0806556693974,47.50006682771112],[19.075385404955426,47.49850626421372],[19.073128441783354,47.49782286144715],[19.07081399741128,47.49712137320833],[19.070813220638655,47.49713252573085],[19.070606606933268,47.497164739669486],[19.07039999322788,47.497196953608125],[19.070594374628655,47.49685758030398],[19.070651313868268,47.49668721334902],[19.070748923994614,47.49631350325282],[19.07079772905709,47.49560454590923],[19.07112384349702,47.49418091874557],[19.071125739880216,47.4928617821879],[19.070647680247703,47.490076964221544],[19.070184980602136,47.48714865943154],[19.070060855045398,47.48647868294523],[19.069981346725143,47.486166420248935],[19.069926731480507,47.48600872541962],[19.069891976325238,47.48591142411826],[19.069797396445068,47.485835019427725],[19.07180949316092,47.48513143812476],[19.08203357134394,47.4815554914789]]]}', NULL),
(14, 'Ferencváros', 'B10', 500, '{\"type\":\"Polygon\",\"coordinates\":[[[19.069797396445068,47.485835019427725],[19.088888910635774,47.479157004390004],[19.084786674827257,47.47735908177157],[19.083714945130254,47.477182975053154],[19.068815214121145,47.4749753161862],[19.06773411180896,47.4775044370526],[19.067036419447476,47.478744211233874],[19.066520646599884,47.479458395142586],[19.065393065670662,47.47984714009496],[19.066737484222728,47.480524972835894],[19.067446371958937,47.4808452812446],[19.067979158124274,47.48125978457145],[19.06858432982858,47.48239439392722],[19.069038481501025,47.48353199947752],[19.069797396445068,47.485835019427725]]]}', NULL),
(16, 'Kelenföld', 'B12', 670, '{\"type\":\"Polygon\",\"coordinates\":[[[19.056569598353775,47.48664475040485],[19.058945119436657,47.48457620611802],[19.059526754225004,47.48429636694692],[19.06022336333092,47.48435121309777],[19.061055236338746,47.48387234585181],[19.06177163673405,47.4833511701209],[19.062224770812776,47.48303579796908],[19.062617036134128,47.482994662331436],[19.062904245661116,47.48290334910416],[19.0632491686155,47.48261539823835],[19.06375640825638,47.48230916304618],[19.06456760593943,47.481623468499436],[19.065352868692145,47.48091882355942],[19.065704554842654,47.48060800764205],[19.06594764767752,47.4802722322643],[19.065555382355114,47.48012596437931],[19.06521045940076,47.479947699843706],[19.065393065670662,47.47984714009496],[19.066737484222728,47.480524972835894],[19.067446371958937,47.4808452812446],[19.067979158124274,47.48125978457145],[19.06858432982858,47.48239439392722],[19.069038481501025,47.48353199947752],[19.069797396445068,47.485835019427725],[19.06846670539062,47.48642379131121],[19.067146238954365,47.48701806783964],[19.064197842244965,47.48844034370492],[19.064197842244965,47.48844034370492],[19.064197842244965,47.48844034370492],[19.063852164491635,47.48860957097892],[19.063145816892757,47.48895957651186],[19.063145816892757,47.48895957651186],[19.06275479223234,47.48915705045931],[19.06178824542124,47.48962262029184],[19.06178824542124,47.48962262029184],[19.06178824542124,47.48962262029184],[19.06178824542124,47.48962262029184],[19.06162991093339,47.489697331991266],[19.06159229520742,47.49067697245982],[19.060953586937586,47.49234161823634],[19.059813036459047,47.494252813132675],[19.059407101067762,47.494955971233566],[19.058855039341637,47.49559904575108],[19.056879283569685,47.496543273244356],[19.05509757128567,47.49735367167935],[19.05496101879436,47.49746266688754],[19.05484041999989,47.49757928787028],[19.054600632949104,47.497797752417995],[19.054600632949104,47.497797752417995],[19.054569056651246,47.497901914012346],[19.054302543620253,47.49785280650087],[19.05377436324656,47.49780042510531],[19.053256289166,47.497661148459486],[19.05210691348094,47.49749260634621],[19.051709566778555,47.49746968933485],[19.050726447898768,47.497347323044266],[19.050576471429025,47.497668927538655],[19.04994736583734,47.497591439684584],[19.049530040008335,47.49737116252092],[19.049295294229154,47.49713766771856],[19.048763336020528,47.49701662601268],[19.047570044976652,47.49660249964742],[19.04876858836593,47.4946578701622],[19.049825646892316,47.49309466799309],[19.05050375990794,47.492211977246996],[19.05077301066538,47.49175378078081],[19.050953064478108,47.49151676281767],[19.052113861987124,47.49011174595057],[19.056569598353775,47.48664475040485]]]}', NULL),
(17, 'Lágymányos', 'B13', 550, '{\"type\":\"Polygon\",\"coordinates\":[[[19.048152667842658,47.47784423995648],[19.04819576357383,47.47768641019215],[19.048241865982618,47.47761934933371],[19.048353113099495,47.47757667419697],[19.055331046944104,47.475660960085634],[19.055618918516416,47.47589236227614],[19.056079472824052,47.475933321316376],[19.056758184434415,47.47599885571336],[19.05786109080296,47.47650674452058],[19.059351832377274,47.477252185330855],[19.06088866051462,47.477701534656234],[19.057800368166397,47.48030751025459],[19.055727578856505,47.48214126593052],[19.055277526798278,47.482537561830014],[19.05478339608365,47.48294266249769],[19.054946548031154,47.48318039029752],[19.054804545886924,47.48328507673901],[19.054538000516118,47.483536241136164],[19.054348752606074,47.48364857841031],[19.053936080883915,47.48397289260092],[19.053274967050356,47.48331532648413],[19.053027285353295,47.482890969699355],[19.052704220667124,47.48204410867481],[19.052533536320595,47.481617223126364],[19.05239324746111,47.48120350094794],[19.05193742148566,47.48037707488575],[19.051437359425307,47.47980921246295],[19.051158217653693,47.47953414781199],[19.05101489929595,47.47939732128606],[19.05083850746979,47.4792747189972],[19.05022178144182,47.478909956452696],[19.04991510019954,47.47872909936453],[19.049596966942147,47.47854795076407],[19.049207626473617,47.47838767388953],[19.048811906453352,47.47823429710766],[19.048340096702475,47.47804871781298],[19.048154466635822,47.47795723482065],[19.048152667842658,47.47784423995648]]]}', NULL),
(18, 'Tabán', 'B14', 660, '{\"type\":\"Polygon\",\"coordinates\":[[[19.040528716827026,47.493125532295096],[19.04191585807601,47.49262117377734],[19.042792929448552,47.49260436174339],[19.04447242782271,47.49307509666144],[19.040132216894534,47.498105750162324],[19.039734961878395,47.49824494679737],[19.039221170556004,47.4990523863828],[19.03861838761469,47.49983072809323],[19.036851678547805,47.500671683462485],[19.036851678547805,47.500671683462485],[19.036137337959843,47.50122724163879],[19.03599456547667,47.501340229829424],[19.035945614911014,47.501549670710375],[19.035789328548873,47.502214823205065],[19.035902280162134,47.50234752875028],[19.03583107153642,47.50250511614942],[19.035929290333115,47.502647773807496],[19.034896915225204,47.50426780848073],[19.03418443808573,47.50412627421014],[19.03406316352425,47.50488181847038],[19.033869954608207,47.50524173027176],[19.032942464635425,47.50547865115675],[19.032380159434865,47.50563121293462],[19.031854726707593,47.505642735009445],[19.030340092957204,47.50552520211815],[19.030340092957204,47.50552520211815],[19.030340092957204,47.50552520211815],[19.029254647240407,47.505998630095974],[19.02813501426465,47.506454733492916],[19.027733313881356,47.506731857675874],[19.027348707131438,47.50706094074306],[19.02704102173115,47.50733228838661],[19.026895725847027,47.50758631469651],[19.02653187777767,47.50755616595703],[19.02578708585392,47.506913336921286],[19.024899452557406,47.506144222326185],[19.024611092007405,47.505488586642116],[19.024544256277267,47.50482364270002],[19.02688544824383,47.50235079853934],[19.028356299179222,47.50407523078803],[19.030105504317305,47.50473791101621],[19.03195629226661,47.503355278642765],[19.032357616806472,47.50305545533867],[19.033508948217758,47.50219528681865],[19.033508948217758,47.50219528681865],[19.03368902368007,47.5017714647781],[19.03359608150643,47.501500687793765],[19.03359608150643,47.501500687793765],[19.032672468652436,47.500966978344735],[19.0314173946835,47.501644744080124],[19.030838279442747,47.50206318654634],[19.029810030476625,47.50270992346205],[19.028356299179222,47.50407523078803],[19.026885934355334,47.502351279200894],[19.03169944482778,47.49877085131658],[19.033986003710027,47.497055044272855],[19.037062836920796,47.49494360116569],[19.038732374041757,47.49389827854341],[19.040528716827026,47.493125532295096]]]}', NULL),
(19, 'Krisztinaváros', 'B15', 750, '{\"type\":\"Polygon\",\"coordinates\":[[[19.032672468652436,47.500966978344735],[19.03359608150643,47.501500687793765],[19.03368902368007,47.5017714647781],[19.033508948217758,47.50219528681865],[19.030105504317305,47.50473791101621],[19.028356299179222,47.50407523078803],[19.029810030476625,47.50270992346205],[19.030838279442747,47.50206318654634],[19.0314173946835,47.501644744080124],[19.03248111399853,47.50106512011476],[19.032672468652436,47.500966978344735]]]}', NULL),
(20, 'Várkerület', 'B16', 650, '{\"type\":\"Polygon\",\"coordinates\":[[[19.039734961878395,47.49824494679737],[19.040091623841505,47.49839149863982],[19.040305934998855,47.498576976876194],[19.04069665376207,47.49874074445563],[19.04123411428361,47.49884142907371],[19.04100477416472,47.49927273023209],[19.040661607062333,47.49975882460353],[19.04042971864945,47.500284759558355],[19.039589846241682,47.50227939218587],[19.039378966400932,47.50407157037833],[19.03904567197992,47.505971264307135],[19.03919148828905,47.50691404984099],[19.03924275322268,47.50758441168645],[19.039280285383086,47.5077724447718],[19.037897237670904,47.50775676142246],[19.037090419842055,47.50774760466189],[19.036300114782378,47.50773862981905],[19.035502612362816,47.507729567734586],[19.034701940257687,47.50772046406759],[19.03331835332977,47.50770471949296],[19.031369873837065,47.5077718596246],[19.02753788769013,47.50799231213489],[19.02716067716409,47.507799926778375],[19.026895725847027,47.50758631469651],[19.02704102173115,47.50733228838661],[19.027348707131438,47.50706094074306],[19.027733313881356,47.506731857675874],[19.02813501426465,47.506454733492916],[19.029254647240407,47.505998630095974],[19.030340092957204,47.50552520211815],[19.031391351407535,47.50560025805814],[19.031854726707593,47.505642735009445],[19.032380159434865,47.50563121293462],[19.032942464635425,47.50547865115675],[19.033869954608207,47.50524173027176],[19.03406316352425,47.50488181847038],[19.03418443808573,47.50412627421014],[19.034896915225204,47.50426780848073],[19.035929290333115,47.502647773807496],[19.03583107153642,47.50250511614942],[19.035902280162134,47.50234752875028],[19.035789328548873,47.502214823205065],[19.035803442262363,47.50213066219905],[19.035945614911014,47.501549670710375],[19.03599456547667,47.501340229829424],[19.036137337959843,47.50122724163879],[19.036851678547805,47.500671683462485],[19.03861838761469,47.49983072809323],[19.039221170556004,47.4990523863828],[19.039734961878395,47.49824494679737]]]}', NULL),
(21, 'Budaörsi út', 'B17', 660, '{\"type\":\"Polygon\",\"coordinates\":[[[19.027249035780784,47.508239857759406],[19.027253279509637,47.508148138723584],[19.02753788769013,47.50799231213489],[19.029269699360594,47.50789269780717],[19.031369873837065,47.5077718596246],[19.03331835332977,47.50770471949296],[19.039280285383086,47.5077724447718],[19.039253686803164,47.5103543544125],[19.039359930800458,47.51245594299834],[19.039734804825088,47.51451130014695],[19.037500809678022,47.51446537904303],[19.03575973830732,47.514450303058226],[19.034141434789234,47.511691324908384],[19.031300374932414,47.510667322781416],[19.029726714269884,47.51013208393158],[19.028688767875963,47.509581762239094],[19.027249035780784,47.508239857759406]]]}', NULL),
(22, 'Óbuda', 'B19', 700, '{\"type\":\"Polygon\",\"coordinates\":[[[19.029026253444414,47.54763294042965],[19.029856361141526,47.54361691048845],[19.031327181883228,47.5397846191139],[19.032297103532528,47.53777357503907],[19.032787376852582,47.5355231759992],[19.033258844843317,47.53383035234401],[19.032337339225762,47.533569913084904],[19.033516009201406,47.53326606564801],[19.03428922065055,47.53248966643494],[19.035553612078502,47.53095591074694],[19.033689170481153,47.53123083251896],[19.031711729244762,47.52994335305766],[19.030741505806617,47.53024159145656],[19.02980563645363,47.53023769767864],[19.029830549676205,47.52947728823136],[19.030615029051887,47.52930072495025],[19.032109122154935,47.52858957390018],[19.031410022434784,47.52847820316924],[19.031253667057427,47.528313975365705],[19.03102782040321,47.527809558183606],[19.031062566042323,47.526401856667945],[19.03398548544027,47.52540181793867],[19.034958363339513,47.524791790585766],[19.035705394583175,47.52502641733031],[19.035827004320055,47.52395885716706],[19.038940091631616,47.5235295776275],[19.04109067878821,47.527926527078904],[19.043116810800058,47.530378769118215],[19.044935710853053,47.53276820876414],[19.046912776125794,47.535077452006504],[19.04726751929519,47.5383417147394],[19.047394176860024,47.53877967563756],[19.04609882715326,47.539390973388436],[19.04316282063735,47.5401063824923],[19.040869608447736,47.5407279595257],[19.038319068318174,47.54237140262131],[19.034217828349142,47.545080369273165],[19.032323487806963,47.54647545793094],[19.03122900017084,47.547308033761084],[19.029265871554173,47.54866827004378],[19.029026253444414,47.54763294042965]]]}', NULL),
(23, 'Margitsziget', 'B2', 660, '{\"type\":\"Polygon\",\"coordinates\":[[[19.06368619109884,47.517921958832716],[19.06533291196672,47.51675737531315],[19.066576458938357,47.51573369332101],[19.067892192531986,47.514772173625175],[19.06620362960291,47.51378617046521],[19.06308593723803,47.511916344494146],[19.06152906880763,47.51098405529771],[19.06152906880763,47.51098405529771],[19.059711351268362,47.50988502424008],[19.058062032980814,47.5088640971386],[19.05714164289043,47.5095823313566],[19.056878183513817,47.50977177027113],[19.056515572753,47.509963122016245],[19.0556236309437,47.51033442087592],[19.05561026863367,47.510555511345046],[19.056857647018543,47.51220389718782],[19.057693530296625,47.51330843488154],[19.058964228476782,47.514987437085466],[19.059335740922023,47.51550637963035],[19.059563714467714,47.5160424247787],[19.0600872092798,47.51718292814556],[19.0600872092798,47.51718292814556],[19.0600872092798,47.51718292814556],[19.0600872092798,47.51718292814556],[19.0600872092798,47.51718292814556],[19.0600872092798,47.51718292814556],[19.0609642849044,47.517145597399406],[19.0609642849044,47.517145597399406],[19.06135254469001,47.51697078455081],[19.061829173526206,47.51699880544061],[19.062757675340436,47.51750354570066],[19.06326302934653,47.51771165405546],[19.06368619109884,47.517921958832716]]]}', NULL),
(24, 'Terézváros', 'B6', 500, '{\"type\":\"Polygon\",\"coordinates\":[[[19.082359579769616,47.522477062235964],[19.07343646164884,47.52828973235842],[19.068718250510102,47.53116212188021],[19.06425955003658,47.53417670062447],[19.061981049341426,47.535395740764244],[19.058039002571405,47.536344102887114],[19.060156929176372,47.541532805022285],[19.061471786072673,47.54238620827488],[19.063977774820927,47.54304252901002],[19.065866801830538,47.54381330836844],[19.06588452903557,47.54544070375667],[19.06320772106932,47.54570395414544],[19.064659269411948,47.54730425091961],[19.06650012794094,47.549323347700295],[19.067519174627023,47.54907928518932],[19.07146387147671,47.54919022283522],[19.072581535583566,47.55214107801973],[19.07384793929708,47.553907746264315],[19.0758066640455,47.55565367031332],[19.0779132170762,47.56001822593777],[19.079058886268797,47.55941967982079],[19.080018540638832,47.55797466761442],[19.081004714851247,47.5567323608324],[19.083305788013547,47.55438077092208],[19.089229245534312,47.55014713630271],[19.09533410284004,47.545834137721585],[19.09686896650706,47.54469994163816],[19.098581551237146,47.54376203018748],[19.100075148399043,47.54299498859939],[19.10167122898963,47.54225121885318],[19.103483329418026,47.54149892968974],[19.09882277542772,47.535798864910994],[19.09649778585944,47.53295476528316],[19.09418403091287,47.53009004950938],[19.092222505381585,47.52763260804935],[19.091374017744755,47.526569522772235],[19.089535757606313,47.5253282449515],[19.082359579769616,47.522477062235964]]]}', NULL),
(25, 'Józsefváros', 'B18', 660, '{\"type\":\"Polygon\",\"coordinates\":[[[19.040107660508028,47.514776366007766],[19.034993664211015,47.51458855267606],[19.03591156566017,47.51699254629142],[19.035851775918673,47.51819048118125],[19.036090934886317,47.51944222662024],[19.036213765645556,47.52161500575835],[19.035827004320055,47.52395885716706],[19.038940091631616,47.5235295776275],[19.038713116907388,47.521224222992885],[19.039031995530422,47.519057277525576],[19.039609412660496,47.51690310793711],[19.040107660508028,47.514776366007766]]]}', NULL),
(26, 'Debrecen Belváros', 'D1', 650, '{\"type\":\"Polygon\",\"coordinates\":[[[21.621401597014568,47.52165409908508],[21.617220576109897,47.52581121358946],[21.61565577481867,47.527295020173945],[21.61298178519573,47.52952651681417],[21.61209450460356,47.529522646574094],[21.61126208896613,47.530556647237376],[21.611177527693513,47.53172711608204],[21.611510576447017,47.53267965849429],[21.61174948330583,47.53430715080424],[21.612625737736693,47.5343094144867],[21.61377744879556,47.533910424286205],[21.616188470648723,47.5341327071051],[21.621177931323984,47.53455323574954],[21.623691402853694,47.53459031797428],[21.62617003953028,47.5345212479572],[21.627406649233762,47.534448850879215],[21.628707269815635,47.53412955201759],[21.629608524737137,47.5331751858657],[21.629846473790508,47.53294988080762],[21.629873961615004,47.53292341510209],[21.630062478458257,47.53272900966434],[21.630404132331336,47.53235724309599],[21.631693087691133,47.53021618127042],[21.63452961814834,47.52164151775721],[21.632372343562338,47.52070597415363],[21.631747806063657,47.52138636113574],[21.628269068827933,47.52155662868665],[21.62482204871216,47.52158863533049],[21.621401597014568,47.52165409908508]]]}', NULL),
(27, 'Debrecen Észak', 'D2', 600, '{\"type\":\"Polygon\",\"coordinates\":[[[21.609744343014057,47.5503497013282],[21.60976341173682,47.5434476979232],[21.609546341031006,47.54178659029375],[21.61017620021022,47.54028552939556],[21.61104126091766,47.536847570079374],[21.61174948330583,47.53430715080424],[21.612625737736693,47.5343094144867],[21.61377744879556,47.533910424286205],[21.61377744879556,47.533910424286205],[21.615525943744984,47.53408999235188],[21.618060162711895,47.53429048578129],[21.621177931323984,47.53455323574954],[21.623691402853694,47.53459031797428],[21.627406649233762,47.534448850879215],[21.628707269815635,47.53412955201759],[21.628707269815635,47.53412955201759],[21.628707269815635,47.53412955201759],[21.628707269815635,47.53412955201759],[21.628707269815635,47.53412955201759],[21.628707269815635,47.53412955201759],[21.628707269815635,47.53412955201759],[21.628707269815635,47.53412955201759],[21.628707269815635,47.53412955201759],[21.628707269815635,47.53412955201759],[21.629608524737137,47.5331751858657],[21.629608524737137,47.5331751858657],[21.629608524737137,47.5331751858657],[21.629608524737137,47.5331751858657],[21.629608524737137,47.5331751858657],[21.629846473790508,47.53294988080762],[21.629873961615004,47.53292341510209],[21.630058669807056,47.53287965840602],[21.630086044208298,47.53288949352452],[21.630326423718884,47.53299900704519],[21.630746387165942,47.53320269746911],[21.63151219404952,47.533571284927376],[21.633689904109303,47.534697940285895],[21.638011219993444,47.53703759220019],[21.63855974252627,47.53763960684606],[21.638716239868472,47.53861796369071],[21.639917539088515,47.5401072417049],[21.641429461776028,47.542079859178955],[21.64220520289541,47.54313082914507],[21.642802632082294,47.5432372215551],[21.644471337948776,47.5438534783271],[21.64889220395861,47.54548592691711],[21.649315324746624,47.54861098429336],[21.64970439956946,47.55038071360465],[21.64995751842318,47.552361373898975],[21.649921883674494,47.553162683889894],[21.648884865066236,47.5536288141102],[21.646007505428088,47.551488361916626],[21.642803598743313,47.5493105829367],[21.641309704907172,47.54848139844921],[21.6407795995338,47.54884599642625],[21.634466304234905,47.548281197077955],[21.633214955103114,47.54829063090949],[21.631977090619955,47.54841182928277],[21.62899610659977,47.549063493658565],[21.62704798782312,47.54954069126927],[21.625176268957944,47.54995520516485],[21.62255039647289,47.55143193408216],[21.621509964203256,47.55115685141726],[21.620440829821234,47.55176116781803],[21.62033822371228,47.55254977979234],[21.619186036442755,47.55348346299897],[21.614361324804747,47.55619434898105],[21.61276329254102,47.55466995293989],[21.61188768280479,47.55381414931148],[21.611143868651197,47.5529221276214],[21.60985364363421,47.55226459883036],[21.609744343014057,47.5503497013282]]]}', NULL),
(28, 'Debrecen Kelet', 'D3', 600, '{\"type\":\"Polygon\",\"coordinates\":[[[21.63452961814834,47.52164151775721],[21.639875691097075,47.52392032476254],[21.639608265996685,47.52380136989197],[21.639878325107958,47.52392304301037],[21.640007862417804,47.52399896113164],[21.640115666295372,47.52408591315077],[21.641189360774263,47.52475112244173],[21.642553975804663,47.525878445518494],[21.644505823861692,47.527490775798015],[21.644505823861692,47.527490775798015],[21.644505823861692,47.527490775798015],[21.644505823861692,47.527490775798015],[21.644505823861692,47.527490775798015],[21.645540748413765,47.52883729165231],[21.64591061256047,47.529994611906204],[21.646173291287454,47.5309411747144],[21.64723014636138,47.53601176154666],[21.64889220395861,47.54548592691711],[21.642802632082294,47.5432372215551],[21.64220520289541,47.54313082914507],[21.641429461776028,47.542079859178955],[21.639917539088515,47.5401072417049],[21.638716239868472,47.53861796369071],[21.63855974252627,47.53763960684606],[21.638011219993444,47.53703759220019],[21.633689904109303,47.534697940285895],[21.631610032523213,47.53361880150223],[21.630597292056194,47.533124866681675],[21.630245620081098,47.53295114690471],[21.630058669807056,47.53287965840602],[21.629873961615004,47.53292341510209],[21.630062478458257,47.53272900966434],[21.630404132331336,47.53235724309599],[21.630404132331336,47.53235724309599],[21.631693087691133,47.53021618127042],[21.63278730555564,47.52690876589456],[21.63452961814834,47.52164151775721]]]}', NULL),
(29, 'Debrecen Dél', 'D4', 550, '{\"type\":\"Polygon\",\"coordinates\":[[[21.64889220395861,47.54548592691711],[21.65640445034728,47.54778760442929],[21.661377770492834,47.549857524291866],[21.66691005523623,47.55007846919915],[21.671999031447484,47.55027974169394],[21.67627356908602,47.549800242196284],[21.675945085167115,47.53635039491448],[21.666858238265235,47.53010056722886],[21.66724579204765,47.53002992760784],[21.664978454180012,47.52994154928285],[21.662992638440784,47.52986410747059],[21.661002855812455,47.52975296785286],[21.661002855812455,47.52975296785286],[21.661002855812455,47.52975296785286],[21.66044305809291,47.52937066657526],[21.659639565482706,47.52889935735041],[21.659639565482706,47.52889935735041],[21.65843225637113,47.52895119520133],[21.65723988672275,47.52890479732358],[21.653870239384474,47.528845472816926],[21.651417625892094,47.52878415642235],[21.64991727320134,47.52880118700767],[21.648665652028143,47.52880635355126],[21.647961387742413,47.52886086698942],[21.646989487954343,47.52894061118424],[21.645647933833487,47.52904804909801],[21.645540748413765,47.52883729165231],[21.645540748413765,47.52883729165231],[21.64591061256047,47.529994611906204],[21.646173291287454,47.5309411747144],[21.646375949295503,47.53191356548955],[21.646576151562105,47.532874137411476],[21.646980758181975,47.53481533441627],[21.64723014636138,47.53601176154666],[21.64723014636138,47.53601176154666],[21.64889220395861,47.54548592691711]]]}', NULL),
(30, 'Debrecen Nyugat', 'D5', 500, '{\"type\":\"Polygon\",\"coordinates\":[[[21.641189360774263,47.52475112244173],[21.644505823861692,47.527490775798015],[21.645540748413765,47.52883729165231],[21.645540748413765,47.52883729165231],[21.645540748413765,47.52883729165231],[21.645540748413765,47.52883729165231],[21.645594341123626,47.52894267037516],[21.645647933833487,47.52904804909801],[21.648665652028143,47.52880635355126],[21.651417625892094,47.52878415642235],[21.65843225637113,47.52895119520133],[21.659639565482706,47.52889935735041],[21.66044305809291,47.52937066657526],[21.661002855812455,47.52975296785286],[21.662992638440784,47.52986410747059],[21.66724579204765,47.53002992760784],[21.678197159815994,47.528111855874926],[21.685854952798906,47.52617997691871],[21.669641470208916,47.49032866286376],[21.632079214390103,47.493820262921304],[21.629886998517037,47.51090997386436],[21.62943943444185,47.513608120801365],[21.629309339878574,47.514777581504745],[21.62939028796211,47.51612922411624],[21.630481976823177,47.517357828305535],[21.630741473907648,47.51722715355655],[21.630997933869907,47.51711561305831],[21.631574314964325,47.51680031042329],[21.632549875720173,47.516215004259465],[21.634445377991796,47.51503995332092],[21.634495047379545,47.515005164307695],[21.634963475884234,47.514720264292976],[21.63546331507584,47.51440435843247],[21.6359909677127,47.5140807875751],[21.63649899045805,47.51374901456592],[21.63649899045805,47.51374901456592],[21.63649899045805,47.51374901456592],[21.637412088509706,47.513153963711346],[21.637412088509706,47.513153963711346],[21.637817024900784,47.51285543271598],[21.638397428300692,47.512377691907346],[21.640404787502433,47.51111695709025],[21.64250442077423,47.509981112847555],[21.64542173443116,47.50848551364195],[21.650516663527952,47.50592248752673],[21.658675287906703,47.51939199254363],[21.656840109817153,47.520238666662806],[21.654605147397376,47.52087487551145],[21.650616782794373,47.52172196520749],[21.64597810184736,47.523285384267176],[21.643049192289737,47.52427239839133],[21.641189360774263,47.52475112244173]]]}', NULL),
(31, 'Debrecen Egyetem', 'D6', 550, '{\"type\":\"Polygon\",\"coordinates\":[[[21.658675287906703,47.51939199254363],[21.654508293441324,47.51253053706321],[21.650516663527952,47.50592248752673],[21.64674945174346,47.507804789163146],[21.64250442077423,47.509981112847555],[21.640404787502433,47.51111695709025],[21.638397428300692,47.512377691907346],[21.637817024900784,47.51285543271598],[21.637412088509706,47.513153963711346],[21.63649899045805,47.51374901456592],[21.6359909677127,47.5140807875751],[21.63546331507584,47.51440435843247],[21.634963475884234,47.514720264292976],[21.634495047379545,47.515005164307695],[21.634445377991796,47.51503995332092],[21.632549875720173,47.516215004259465],[21.631574314964325,47.51680031042329],[21.630997933869907,47.51711561305831],[21.630741473907648,47.51722715355655],[21.63060317350775,47.517296072770534],[21.630481976823177,47.517357828305535],[21.630739865317395,47.51763159036372],[21.631091222322567,47.51797175412193],[21.63189593925358,47.51866578377005],[21.632193361017812,47.519768095261696],[21.6323481328931,47.52039185840576],[21.632372343562338,47.52070597415363],[21.632835896441264,47.520918612837676],[21.637364279101945,47.522851291708974],[21.639875691097075,47.52392032476254],[21.641189360774263,47.52475112244173],[21.643049192289737,47.52427239839133],[21.650616782794373,47.52172196520749],[21.654605147397376,47.52087487551145],[21.656840109817153,47.520238666662806],[21.658675287906703,47.51939199254363]]]}', NULL),
(32, 'Debrecen Ipari', 'D7', 550, '{\"type\":\"Polygon\",\"coordinates\":[[[21.608693395658236,47.52882582433432],[21.607986155950215,47.52737500389472],[21.60763541248113,47.525892132136335],[21.60717142387793,47.523056040723674],[21.604952488031415,47.510497400972184],[21.624192230953412,47.518364018959716],[21.62709167002646,47.5197968602298],[21.631747806063657,47.52138636113574],[21.628269068827933,47.52155662868665],[21.62482204871216,47.52158863533049],[21.621401597014568,47.52165409908508],[21.619344805067115,47.52369923196748],[21.617220576109897,47.52581121358946],[21.61565577481867,47.527295020173945],[21.61298178519573,47.52952651681417],[21.61209450460356,47.529522646574094],[21.611231271595727,47.52982569092168],[21.60919894563736,47.52938771815846],[21.608693395658236,47.52882582433432]]]}', NULL),
(35, 'Győr Belváros', 'E1', 500, '{\"type\":\"Polygon\",\"coordinates\":[[[17.624961011068564,47.694095836401544],[17.627067288369858,47.69170748309375],[17.627325838261015,47.6913509145094],[17.628155106750512,47.69125123572613],[17.629490798462626,47.6911376270356],[17.631226234113853,47.6910230718365],[17.63276615895259,47.69145227364578],[17.635701640678064,47.69229447070296],[17.63795137462168,47.69327431746953],[17.63901007294899,47.693719696277014],[17.639475566659513,47.69421757251729],[17.63851446288504,47.69549563557726],[17.63794873496363,47.696261399727376],[17.637466453080236,47.69707024530072],[17.636472097020743,47.698707306663465],[17.63599650208303,47.69960150833484],[17.63538302286645,47.70054689161387],[17.63404888414587,47.702381934157245],[17.633808374180205,47.70276284219918],[17.63370005385829,47.703134615925734],[17.63350313193635,47.70384754590742],[17.63365478037332,47.70494096772731],[17.633888424523718,47.70605235688467],[17.634025467976215,47.70694100242139],[17.634113388321936,47.70740537758911],[17.634200044580354,47.707820852221715],[17.633636778918515,47.707973920984585],[17.632878536660655,47.708163434072446],[17.632163622532715,47.70836752431987],[17.63124289979197,47.70879756901073],[17.631102083372554,47.708608058228634],[17.631307891985387,47.70824361247949],[17.63158953563206,47.70764937620726],[17.631936171472148,47.70650118340379],[17.631053152602675,47.70608899167834],[17.630940548208258,47.705785889537424],[17.631069238944008,47.70539618419522],[17.630763598446435,47.704194574381376],[17.630329267205497,47.70335018334819],[17.63010405841763,47.702451649661924],[17.628688460313043,47.700928440278915],[17.6271073346704,47.699554684091254],[17.625814395427682,47.697964676109194],[17.624961011068564,47.694095836401544]]]}', NULL),
(36, 'Győr Dél', 'E2', 450, '{\"type\":\"Polygon\",\"coordinates\":[[[17.62435767672042,47.68960549168807],[17.627641126128168,47.6880534747516],[17.62797092441457,47.685504954833476],[17.627417945851306,47.68470723812115],[17.625469354722526,47.684281784219394],[17.623231108156432,47.68300540169494],[17.61758822439134,47.67816143362464],[17.610454390611864,47.68106289249525],[17.61080227711207,47.68194017874015],[17.611504809500076,47.68259211572885],[17.6135934193031,47.684177183440596],[17.615776965913568,47.68586446066203],[17.618470452833094,47.68712212107019],[17.621584380173573,47.68822135187736],[17.62435767672042,47.68960549168807]]]}', NULL),
(37, 'Győr Nyugat', 'E3', 400, '{\"type\":\"Polygon\",\"coordinates\":[[[17.62052821532791,47.67712226601492],[17.629249597615853,47.68022273460758],[17.635588536699345,47.68242819714581],[17.637213274531234,47.68293953061152],[17.63804396397702,47.68315989543889],[17.638873653080747,47.68339716623368],[17.640332195107824,47.68381425385612],[17.642107280362183,47.684321826875816],[17.644729475842354,47.68527973848154],[17.64618543098942,47.68564511728161],[17.649288070368925,47.6857043708649],[17.64916704542742,47.686096924155294],[17.648462900320823,47.68638578223667],[17.64526489976356,47.68748840308197],[17.64023863101096,47.69321066483363],[17.63630308966711,47.69172589717991],[17.634174307716563,47.691006204411394],[17.63210640652227,47.69035888872031],[17.629157088727936,47.689362902892725],[17.629215448743338,47.6889071945092],[17.62943721680324,47.688545767787474],[17.629754752595915,47.68819207519704],[17.630139928700174,47.6877835000729],[17.630411175881136,47.687057724562834],[17.630314085979165,47.68620639989018],[17.630017927292357,47.68545264768369],[17.629200887071732,47.68482403622994],[17.627110588986568,47.68401548914753],[17.624932190485282,47.682864589207384],[17.62287506735936,47.68167668543131],[17.622081371145526,47.6810951780088],[17.62141161875286,47.68046428378929],[17.620968082633112,47.67989847972029],[17.620769658580002,47.6795134151086],[17.62066669971523,47.678274261100455],[17.620446575229266,47.677641003791166],[17.620226450744553,47.67699426498933],[17.62052821532791,47.67712226601492]]]}', NULL),
(38, 'Győr Kelet', 'E4', 430, '{\"type\":\"Polygon\",\"coordinates\":[[[17.64765569722502,47.676368035365044],[17.644707979812495,47.680239640294246],[17.643208836016328,47.68217780119414],[17.642947870406914,47.682583903188714],[17.64297629648823,47.68282631094479],[17.64274888783254,47.68308785489148],[17.642376119256966,47.68365015129896],[17.64215171403856,47.68392524214056],[17.641856972856033,47.68418680257926],[17.63654103501605,47.68273007377189],[17.637009050517975,47.682128130156656],[17.637490098543054,47.68150096445936],[17.637635531457306,47.68131187006813],[17.637700395800465,47.68121737376126],[17.63774627531967,47.68115071339474],[17.637787181491575,47.681084721611114],[17.63815924903679,47.680272809230615],[17.63857556929284,47.67933059434506],[17.6389822650369,47.67838531005623],[17.63940306711538,47.67747401669732],[17.639998441599317,47.67617981793393],[17.640195410791165,47.67575156412991],[17.64040134720409,47.675301538935486],[17.640447042530724,47.67518472046402],[17.64049064501799,47.67509419685041],[17.64050598977306,47.6750636238356],[17.640519914487555,47.675035628176886],[17.640523264613137,47.67502857305888],[17.640524739693234,47.67502694800439],[17.640528321193727,47.675027826117855],[17.640535627877597,47.67502958442503],[17.640543751177972,47.67503099328718],[17.640563011674686,47.67503494647903],[17.644106996713333,47.675734368376],[17.64765569722502,47.676368035365044]]]}', NULL);
INSERT INTO `zones` (`id`, `name`, `zone_code`, `hourly_rate`, `polygon_data`, `features`) VALUES
(40, 'Józsefváros Külső', 'B11', 660, '{\"type\":\"Polygon\",\"coordinates\":[[[19.05500139340569,47.49959889492206],[19.059330156202606,47.50248238658767],[19.06146348643955,47.503952475052586],[19.062573456817603,47.50470121331358],[19.062987280073827,47.50497112287232],[19.063180764594193,47.505088223033766],[19.0633326857449,47.50508155432756],[19.063389449235007,47.505039315516825],[19.063356182531432,47.505064069925936],[19.06769523869138,47.501397951000065],[19.06866738143097,47.500437288397734],[19.069939085732756,47.498764674250936],[19.070296686375826,47.497937868512366],[19.07039999322788,47.497196953608125],[19.070580599528256,47.497005528286536],[19.07060239948413,47.49682141950436],[19.070695049297512,47.49648265765833],[19.070791468707398,47.49574426694355],[19.070890021868905,47.49500917641399],[19.070999021649186,47.494261662328654],[19.071125739880216,47.4928617821879],[19.07083639775172,47.491442217959644],[19.070797796726197,47.49111804594264],[19.07073591116287,47.49077003294093],[19.070647680247703,47.490076964221544],[19.07043154681267,47.488541138503365],[19.070184980602136,47.48714865943154],[19.070060855045398,47.48647868294523],[19.069981346725143,47.486166420248935],[19.069926731480507,47.48600872541962],[19.069891976325238,47.48591142411826],[19.069797396445068,47.485835019427725],[19.067146238954365,47.48701806783964],[19.064197842244965,47.48844034370492],[19.063145816892757,47.48895957651186],[19.06275479223234,47.48915705045931],[19.06178824542124,47.48962262029184],[19.06162991093339,47.489697331991266],[19.06159229520742,47.49067697245982],[19.060953586937586,47.49234161823634],[19.059813036459047,47.494252813132675],[19.059407101067762,47.494955971233566],[19.058855039341637,47.49559904575108],[19.056879283569685,47.496543273244356],[19.05598842742768,47.49694847246185],[19.05556127556852,47.497136274142576],[19.05509757128567,47.49735367167935],[19.05496101879436,47.49746266688754],[19.05490201649218,47.497516599565245],[19.05484041999989,47.49757928787028],[19.054600632949104,47.497797752417995],[19.054569056651246,47.497901914012346],[19.054870356962795,47.498069759327535],[19.05500139340569,47.49959889492206]]]}', NULL),
(42, 'Pécs Dél (Kertváros)', 'A8', 100, '{\"type\":\"Polygon\",\"coordinates\":[[[18.217325448796544,46.05230546797176],[18.2153222409283,46.05161043747273],[18.214318305559885,46.051238130302835],[18.213818669309973,46.05107676863827],[18.213583865304628,46.051002367457755],[18.21335856265506,46.050916809708156],[18.213205526893034,46.050792898249455],[18.213073746097848,46.050627682538675],[18.213250644336654,46.050164286732326],[18.213436508462962,46.049690632087845],[18.21360743631365,46.04919916365685],[18.213796647395895,46.04871608735826],[18.214170911870838,46.04771076336249],[18.2142622142359,46.04749220945769],[18.214290120824472,46.04738325929537],[18.214335469029464,46.047252518816066],[18.21442927593722,46.047060794290616],[18.214499042407652,46.04691552622464],[18.214662993611228,46.04684289204866],[18.21603104797802,46.047043407022386],[18.216717654461917,46.0471313856456],[18.217370830871772,46.04724176516689],[18.218069966626672,46.04739426774904],[18.21871546519884,46.047549067380714],[18.219381988022178,46.04771965541502],[18.219973366666125,46.04789175650896],[18.219847816311017,46.04814559512698],[18.219682383898572,46.04842551064996],[18.219361672453715,46.0489700501501],[18.218660115918624,46.050086592127144],[18.217325448796544,46.05230546797176]]]}', NULL);

--
-- Indexek a kiírt táblákhoz
--

--
-- A tábla indexei `bookings`
--
ALTER TABLE `bookings`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `fk_booking_parking_spot` (`parking_spot_id`);

--
-- A tábla indexei `cities`
--
ALTER TABLE `cities`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- A tábla indexei `forgot_password`
--
ALTER TABLE `forgot_password`
  ADD PRIMARY KEY (`fpid`),
  ADD UNIQUE KEY `UKss96nm4ed1jmllpxib14p1r7v` (`user_id`);

--
-- A tábla indexei `parking_spots`
--
ALTER TABLE `parking_spots`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uuid` (`uuid`),
  ADD KEY `city_id` (`city_id`),
  ADD KEY `idx_parking_type` (`parking_type`),
  ADD KEY `zone_id` (`zone_id`);

--
-- A tábla indexei `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `booking_id` (`booking_id`),
  ADD KEY `user_id` (`user_id`);

--
-- A tábla indexei `refresh_token`
--
ALTER TABLE `refresh_token`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_refresh_token_token` (`token`),
  ADD KEY `FK_refresh_token_user_id` (`user_id`);

--
-- A tábla indexei `role`
--
ALTER TABLE `role`
  ADD PRIMARY KEY (`id`);

--
-- A tábla indexei `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- A tábla indexei `user_x_role`
--
ALTER TABLE `user_x_role`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `role_id` (`role_id`);

--
-- A tábla indexei `vehicles`
--
ALTER TABLE `vehicles`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- A tábla indexei `zones`
--
ALTER TABLE `zones`
  ADD PRIMARY KEY (`id`);

--
-- A kiírt táblák AUTO_INCREMENT értéke
--

--
-- AUTO_INCREMENT a táblához `bookings`
--
ALTER TABLE `bookings`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `cities`
--
ALTER TABLE `cities`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT a táblához `forgot_password`
--
ALTER TABLE `forgot_password`
  MODIFY `fpid` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `parking_spots`
--
ALTER TABLE `parking_spots`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=149;

--
-- AUTO_INCREMENT a táblához `payments`
--
ALTER TABLE `payments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `refresh_token`
--
ALTER TABLE `refresh_token`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT a táblához `role`
--
ALTER TABLE `role`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT a táblához `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=63;

--
-- AUTO_INCREMENT a táblához `user_x_role`
--
ALTER TABLE `user_x_role`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- AUTO_INCREMENT a táblához `vehicles`
--
ALTER TABLE `vehicles`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `zones`
--
ALTER TABLE `zones`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=43;

--
-- Megkötések a kiírt táblákhoz
--

--
-- Megkötések a táblához `bookings`
--
ALTER TABLE `bookings`
  ADD CONSTRAINT `bookings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `fk_booking_parking_spot` FOREIGN KEY (`parking_spot_id`) REFERENCES `parking_spots` (`id`);

--
-- Megkötések a táblához `forgot_password`
--
ALTER TABLE `forgot_password`
  ADD CONSTRAINT `FK95rqabtnw8wouua80mbixrq4` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- Megkötések a táblához `parking_spots`
--
ALTER TABLE `parking_spots`
  ADD CONSTRAINT `FKfq2r1m5lubo2k87f8pdvvyojh` FOREIGN KEY (`zone_id`) REFERENCES `zones` (`id`),
  ADD CONSTRAINT `parking_spots_ibfk_1` FOREIGN KEY (`city_id`) REFERENCES `cities` (`id`);

--
-- Megkötések a táblához `payments`
--
ALTER TABLE `payments`
  ADD CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
  ADD CONSTRAINT `payments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- Megkötések a táblához `refresh_token`
--
ALTER TABLE `refresh_token`
  ADD CONSTRAINT `FK_refresh_token_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;

--
-- Megkötések a táblához `user_x_role`
--
ALTER TABLE `user_x_role`
  ADD CONSTRAINT `user_x_role_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `user_x_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`);

--
-- Megkötések a táblához `vehicles`
--
ALTER TABLE `vehicles`
  ADD CONSTRAINT `vehicles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
