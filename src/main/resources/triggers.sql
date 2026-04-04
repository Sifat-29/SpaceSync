USE SpaceSync;

-- 1. Log Initial Creation
DROP TRIGGER IF EXISTS after_booking_insert;
DELIMITER //
CREATE TRIGGER after_booking_insert
AFTER INSERT ON Booking
FOR EACH ROW
BEGIN
    INSERT INTO Audit_Log (Booking_ID, Old_Status, New_Status, Timestamp, Changed_By_User_ID)
    VALUES (NEW.Booking_ID, NULL, NEW.Status, NOW(),
    (SELECT Admin_ID FROM Team_Group WHERE Group_ID = NEW.Group_ID));
END //
DELIMITER ;

-- 2. Log Status Changes
DROP TRIGGER IF EXISTS after_booking_update;
DELIMITER //
CREATE TRIGGER after_booking_update
AFTER UPDATE ON Booking
FOR EACH ROW
BEGIN
    IF OLD.Status <> NEW.Status THEN
        INSERT INTO Audit_Log (Booking_ID, Old_Status, New_Status, Timestamp, Changed_By_User_ID)
        VALUES (NEW.Booking_ID, OLD.Status, NEW.Status, NOW(),
        (SELECT Admin_ID FROM Team_Group WHERE Group_ID = NEW.Group_ID));
    END IF;
END //
DELIMITER ;

-- 3. Review Validation
DROP TRIGGER IF EXISTS before_review_insert;
DELIMITER //
CREATE TRIGGER before_review_insert
BEFORE INSERT ON Review
FOR EACH ROW
BEGIN
    DECLARE current_booking_status VARCHAR(20);
    SELECT Status INTO current_booking_status FROM Booking WHERE Booking_ID = NEW.Booking_ID;
    IF current_booking_status <> 'Completed' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error: Reviews can only be submitted for Completed bookings.';
    END IF;
END //
DELIMITER ;

-- 4. Double Booking Defence
DROP TRIGGER IF EXISTS prevent_double_booking;
DELIMITER //
CREATE TRIGGER prevent_double_booking
BEFORE INSERT ON Booking
FOR EACH ROW
BEGIN
    DECLARE overlap_count INT DEFAULT 0;
    IF NEW.Booked_Room_ID IS NOT NULL THEN
        SELECT COUNT(*) INTO overlap_count FROM Booking
        WHERE Space_ID = NEW.Space_ID AND Booked_Room_ID = NEW.Booked_Room_ID AND Status = 'Confirmed'
          AND (Start_Time < NEW.End_Time AND End_Time > NEW.Start_Time);
        IF overlap_count > 0 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error: This Room is already booked for the selected time.';
        END IF;
    END IF;
END //
DELIMITER ;

-- 5. Group Size Limit
DROP TRIGGER IF EXISTS enforce_max_group_size;
DELIMITER //
CREATE TRIGGER enforce_max_group_size
BEFORE INSERT ON Group_Membership
FOR EACH ROW
BEGIN
    IF (SELECT COUNT(*) FROM Group_Membership WHERE Group_ID = NEW.Group_ID) >= 15 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error: Group limit of 15 members reached.';
    END IF;
END //
DELIMITER ;