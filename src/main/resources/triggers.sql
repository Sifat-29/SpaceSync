USE SpaceSync;

-- ==============================================================================
-- TRIGGER 1: Automated Audit Logging
-- Purpose: Automatically tracks any changes to a Booking's status.
-- ==============================================================================

DROP TRIGGER IF EXISTS after_booking_status_update;

DELIMITER //

CREATE TRIGGER after_booking_status_update
AFTER UPDATE ON Booking
FOR EACH ROW
BEGIN
    -- Only log if the status actually changed
    IF OLD.Status <> NEW.Status THEN
        INSERT INTO Audit_Log (
            Booking_ID,
            Old_Status,
            New_Status,
            Timestamp,
            Changed_By_User_ID
        )
        VALUES (
            NEW.Booking_ID,
            OLD.Status,
            NEW.Status,
            NOW(),
            -- Assuming the Group Admin initiated the change for this trigger's context
            (SELECT Admin_ID FROM Team_Group WHERE Group_ID = NEW.Group_ID)
        );
    END IF;
END //

DELIMITER ;


-- ==============================================================================
-- TRIGGER 2: Review Validation
-- Purpose: Ensures reviews can only be submitted for 'Completed' bookings.
-- ==============================================================================

DROP TRIGGER IF EXISTS before_review_insert;

DELIMITER //

CREATE TRIGGER before_review_insert
BEFORE INSERT ON Review
FOR EACH ROW
BEGIN
    DECLARE current_booking_status VARCHAR(20);

    -- Get the status of the booking being reviewed
    SELECT Status INTO current_booking_status
    FROM Booking
    WHERE Booking_ID = NEW.Booking_ID;

    -- Check the condition: Is the booking completed?
    IF current_booking_status <> 'Completed' THEN
        -- Throw a SQL Error to abort the insert
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: Reviews can only be submitted for Completed bookings.';
    END IF;
END //

DELIMITER ;


-- ==============================================================================
-- TRIGGER 3: Double Booking Defence
-- Purpose: Prevents double booking
-- ==============================================================================

DELIMITER //

CREATE TRIGGER prevent_double_booking
BEFORE INSERT ON Booking
FOR EACH ROW
BEGIN
    DECLARE overlap_count INT DEFAULT 0;

    -- Only check for overlaps if they are booking a Room (not an open desk)
    IF NEW.Booked_Room_ID IS NOT NULL THEN
        SELECT COUNT(*) INTO overlap_count
        FROM Booking
        WHERE Space_ID = NEW.Space_ID
          AND Booked_Room_ID = NEW.Booked_Room_ID
          AND Status = 'Confirmed'
          -- Logic for overlapping time slots
          AND (Start_Time < NEW.End_Time AND End_Time > NEW.Start_Time);

        IF overlap_count > 0 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Error: This Meeting Room is already booked for the selected time slot.';
        END IF;
    END IF;
END //

DELIMITER ;


-- ==============================================================================
-- TRIGGER 4: Max Group Size Enforcement
-- Purpose: Automatically makes sure that group size does not exceed 15
-- ==============================================================================

DELIMITER //

CREATE TRIGGER enforce_max_group_size
BEFORE INSERT ON Group_Membership
FOR EACH ROW
BEGIN
    DECLARE current_members INT;

    -- Count how many members are currently in the group
    SELECT COUNT(*) INTO current_members
    FROM Group_Membership
    WHERE Group_ID = NEW.Group_ID;

    -- If the group already has 15 members, reject the new insertion
    IF current_members >= 15 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: A group cannot exceed the maximum limit of 15 members.';
    END IF;
END //

DELIMITER ;