USE master;

/* Auto kill all connections before dropping database */
DECLARE @kill varchar(8000) = '';  
SELECT @kill = @kill + 'kill ' + CONVERT(varchar(5), session_id) + ';'  
FROM sys.dm_exec_sessions
WHERE database_id  = db_id('CleaningService')

EXEC(@kill);

GO

USE master;
/*Drops logins if they exist*/
DROP LOGIN defaultAppUser
DROP USER IF EXISTS defaultAppUser
DROP LOGIN DriverUser
DROP USER IF EXISTS DriverUser
DROP LOGIN CostumerUser
DROP USER IF EXISTS CostumerUser
DROP LOGIN DeliveryPointUser
DROP USER IF EXISTS DeliveryPointUser
DROP LOGIN LaundryAccountUser
DROP USER IF EXISTS LaundryAccountUser
/*Drop DB if exist*/
DROP DATABASE IF EXISTS CleaningService
/*Drop job if exists*/
EXEC msdb.dbo.sp_delete_job @job_name = N'BackupCleaningService';
GO

DECLARE @sql NVARCHAR(MAX) = N'';


/*Drop procedures of cleaningservice DB*/
SELECT @sql += N'DROP PROCEDURE dbo.'
  + QUOTENAME(name) + ';
' FROM sys.procedures
WHERE SCHEMA_NAME(schema_id) = N'dbo.CleaningService';

EXEC sp_executesql @sql;

/*Drop functions of cleaningservice DB*/
SELECT @sql += N'DROP FUNCTION dbo.'
  + QUOTENAME(name) + ';
' FROM sys.procedures
WHERE SCHEMA_NAME(schema_id) = N'dbo.CleaningService';

EXEC sp_executesql @sql;

/* Create Database */
CREATE DATABASE CleaningService

GO

/* Create Tables */
USE CleaningService
GO

CREATE TABLE [dbo].[tblOrderItem]
(

    [fldOrderItemID]   [int] IDENTITY (1,1) NOT NULL,
    [fldLaundryItemID] [int]                NOT NULL,
    [fldOrderID]       [int]                NOT NULL,
    [fldIsWashed]      [bit]                NOT NULL,
	[fldStartDateTime]    [datetime]				,
    [fldEndDateTime]      [datetime]                ,

    PRIMARY KEY (fldOrderItemID),

)

CREATE TABLE [dbo].[tblLaundryItem]
(
    [fldLaundryItemID]    [int] IDENTITY (1,1) NOT NULL,
    [fldName]             [nvarchar](20)       NOT NULL,
    [fldPrice]            [decimal](18, 0)     NOT NULL,
    [fldHandlingDuration] [tinyint]            NOT NULL,
    
    PRIMARY KEY (fldLaundryItemID)
)

CREATE TABLE [dbo].[tblOrder]
(

    [fldOrderID]         [int] IDENTITY (1,1) NOT NULL,
    [fldCostumerID]      [int]                NOT NULL,
    [fldStartDateTime]   [datetime]					  ,
    [fldEndDateTime]     [datetime]                   ,
    [fldDeliveryPointID] [int]						  ,
    [fldOrderStatusID]   [int]                NOT NULL,
    PRIMARY KEY (fldOrderID),
)

CREATE TABLE [dbo].[tblOrderStatus]
(
    [fldOrderStatusID] [int] IDENTITY (1,1) NOT NULL,
    [fldStatus]        [nvarchar](100)       NOT NULL,
    PRIMARY KEY (fldOrderStatusID)
)

CREATE TABLE [dbo].[tblDeliveryDay]
(
    [fldDeliveryDayID] [int] IDENTITY (1,1) NOT NULL,
    [fldDay]           [nvarchar](10)       NOT NULL,
    PRIMARY KEY (fldDeliveryDayID)
)

CREATE TABLE [dbo].[tblRouteDeliveryDay]
(
    [fldRouteDeliveryDayID] [int] IDENTITY (1,1) NOT NULL,
    [fldRouteID]            [int]                NOT NULL,
    [fldDeliveryDayID]      [int]                NOT NULL,
    PRIMARY KEY (fldRouteDeliveryDayID),
    FOREIGN KEY (fldDeliveryDayID) REFERENCES tblDeliveryDay (fldDeliveryDayID)
)

CREATE TABLE [dbo].[tblRole]
(
    [fldRoleID]   [int] IDENTITY (1,1) NOT NULL,
    [fldRoleName] [nvarchar](30)       NOT NULL,
    PRIMARY KEY (fldRoleID)
)

CREATE TABLE [dbo].[tblRoute]
(
    [fldRouteID] [int] IDENTITY (1,1) NOT NULL,
	[fldIsAssigned] [bit],
    PRIMARY KEY (fldRouteID)
)

CREATE TABLE [dbo].[tblLaundryAccount]
(
    [fldLaundryAccountID] [int] IDENTITY (1,1) NOT NULL,
    [fldUserName]         [nvarchar](30)       NOT NULL,
    [fldPasswordHash]     [nvarchar](150)      NOT NULL,
    [fldRoleID]           [int]          NOT NULL,
    PRIMARY KEY (fldLaundryAccountID),
    FOREIGN KEY (fldRoleID) REFERENCES tblRole (fldRoleID)
)
CREATE TABLE [dbo].[tblDriver]
(
 [fldDriverID]   [int] IDENTITY (1,1) NOT NULL,
    [fldFirstName]    [nvarchar](20)       NOT NULL,
    [fldLastName]     [nvarchar](25)       NOT NULL,
    [fldEmailAddress] [nvarchar](50)       NOT NULL,
    [fldPhoneNumber]  [nvarchar](50)       NOT NULL,
    [fldDateOfBirth]  [date]			   NULL,
    [fldCorporateIDNO][int]				   NOT NULL,
    PRIMARY KEY (fldDriverID)
	)

CREATE TABLE [dbo].[tblDriverAccount]
(
    [fldDriverAccountID] [int] IDENTITY (1,1) NOT NULL,
    [fldUsername]        [nvarchar](30)       NOT NULL,
    [fldPasswordHash]    [nvarchar](150)      NOT NULL,
    [fldRouteID]         [int]          ,
    [fldRoleID]          [int]          NOT NULL,
	[fldDriverID]		 [int]
    PRIMARY KEY (fldDriverAccountID),
    FOREIGN KEY (fldRouteID) REFERENCES tblRoute (fldRouteID),
    FOREIGN KEY (fldRoleID) REFERENCES tblRole (fldRoleID),
	FOREIGN KEY (fldDriverID) REFERENCES tblDriver (fldDriverID) 
)

CREATE TABLE [dbo].[tblDeliveryPoint]
(
    [fldDeliveryPointID] [int] IDENTITY (1,1) NOT NULL,
    [fldName]            [nvarchar](30)       NOT NULL,
	[fldAddress]         [nvarchar](30)       NOT NULL,
    [fldZipCode]         [smallint]           NOT NULL,
    [fldEmailAddress]    [nvarchar](50)       NOT NULL,
    [fldPhoneNumber]     [nvarchar](50)       NOT NULL,
    [fldRouteID]         [int]                NOT NULL,
    PRIMARY KEY (fldDeliveryPointID),
    FOREIGN KEY (fldRouteID) REFERENCES tblRoute (fldRouteID)
)

CREATE TABLE [dbo].[tblDeliveryPointAccount]
(
    [fldDeliveryPointAccountID] [int] IDENTITY (1,1) NOT NULL,
    [fldUsername]               [nvarchar](20)       NOT NULL,
    [fldPasswordHash]           [nvarchar](150)      NOT NULL,
    [fldDeliveryPointID]        [int]          NOT NULL,
    [fldRoleID]                 [int]          NOT NULL,
    PRIMARY KEY (fldDeliveryPointAccountID),
    FOREIGN KEY (fldDeliveryPointID) REFERENCES tblDeliveryPoint (fldDeliveryPointID),
    FOREIGN KEY (fldRoleID) REFERENCES tblRole (fldRoleID)
)

CREATE TABLE [dbo].[tblCostumer]
(
    [fldCostumerID]   [int] IDENTITY (1,1) NOT NULL,
    [fldFirstName]    [nvarchar](20)       NOT NULL,
    [fldLastName]     [nvarchar](25)       NOT NULL,
    [fldEmailAddress] [nvarchar](50)       NOT NULL,
    [fldPhoneNumber]  [nvarchar](50)       NOT NULL,
    [fldDateOfBirth]  [date]			   NULL,	
    [fldIsTemporary]  [bit]                NULL,
    PRIMARY KEY (fldCostumerID),
)

CREATE TABLE [dbo].[tblCostumerAccount]
(
    [fldCostumerAccountID] [int] IDENTITY (1,1) NOT NULL,
    [fldUserName]          [nvarchar](20)       NOT NULL,
    [fldPasswordHash]      [nvarchar](150)      NOT NULL,
    [fldCostumerID]        [int]          NOT NULL,
    [fldRoleID]            [int]          NOT NULL,
    PRIMARY KEY (fldCostumerAccountID),
    FOREIGN KEY (fldCostumerID) REFERENCES tblCostumer (fldCostumerID),
    FOREIGN KEY (fldRoleID) REFERENCES tblRole (fldRoleID)
)


/*Add Foreign keys after created tables due to dependecie orders*/
ALTER TABLE [dbo].[tblOrderItem]
    ADD CONSTRAINT FK_tblOrderItem_tblLaundryItem FOREIGN KEY (fldLaundryItemID) REFERENCES tblLaundryItem (fldLaundryItemID)
GO


ALTER TABLE [dbo].[tblOrder]
    ADD CONSTRAINT FK_tblOrder_tblCostumer FOREIGN KEY (fldCostumerID) REFERENCES tblCostumer (fldCostumerID)
GO

ALTER TABLE [dbo].[tblRouteDeliveryDay]
    ADD CONSTRAINT FK_tblRouteDeliveryDay_tblRoute  FOREIGN KEY (fldRouteID) REFERENCES tblRoute (fldRouteID)
GO

ALTER TABLE [dbo].[tblOrderItem]
    ADD CONSTRAINT FK_tblOrderItem_tblOrder FOREIGN KEY (fldOrderID) REFERENCES tblOrder (fldOrderID) ON DELETE CASCADE
GO

ALTER TABLE [dbo].[tblOrder]
    ADD CONSTRAINT FK_tblOrder_tblDeliveryPoint FOREIGN KEY (fldDeliveryPointID) REFERENCES tblDeliveryPoint (fldDeliveryPointID)
GO

ALTER TABLE [dbo].[tblOrder]
    ADD CONSTRAINT FK_tblOrder_tblOrderStatus FOREIGN KEY (fldOrderStatusID) REFERENCES tblOrderStatus (fldOrderStatusID)
GO

/* Create login+role for server access */
CREATE LOGIN defaultAppUser WITH PASSWORD = 'ItsAmeMar!o123';
CREATE USER defaultAppUser FOR LOGIN defaultAppUser;
CREATE ROLE defaultAppUserRole;
ALTER ROLE defaultAppUserRole ADD MEMBER defaultAppUser;

CREATE LOGIN DriverUser WITH PASSWORD = 'ImAdR!v3r';
CREATE USER DriverUser FOR LOGIN DriverUser;
CREATE ROLE DriverUserRole;
ALTER ROLE DriverUserRole ADD MEMBER DriverUser;

CREATE LOGIN CostumerUser WITH PASSWORD = 'ImAcUsT0m3r';
CREATE USER CostumerUser FOR LOGIN CostumerUser;
CREATE ROLE CostumerUserRole;
ALTER ROLE CostumerUserRole ADD MEMBER CostumerUser;

CREATE LOGIN DeliveryPointUser WITH PASSWORD = 'ImAd3l!veryP0!nt';
CREATE USER DeliveryPointUser FOR LOGIN DeliveryPointUser;
CREATE ROLE DeliveryPointUserRole;
ALTER ROLE DeliveryPointUserRole ADD MEMBER DeliveryPointUser;

CREATE LOGIN LaundryAccountUser WITH PASSWORD = 'ImAl4uNdRy!';
CREATE USER LaundryAccountUser FOR LOGIN LaundryAccountUser;
CREATE ROLE LaundryAccountUserRole;
ALTER ROLE LaundryAccountUserRole ADD MEMBER LaundryAccountUser;


/*Create stored procedures*/
USE CleaningService
GO

CREATE PROCEDURE [dbo].[createDriverUser](@userName Nvarchar(20), @password Nvarchar(150), @firstName nvarchar(20),
                                     @lastName nvarchar(25), @email nvarchar(50), @phoneNo nvarchar(50),
                                     @dateOfBirth date,@corporateID INT)
WITH EXECUTE AS OWNER
AS
BEGIN
    SET DATEFORMAT DMY
    INSERT INTO tblDriver(fldFirstName, fldLastName, fldEmailAddress, fldPhoneNumber, fldDateOfBirth, fldCorporateIDNO)
    VALUES (@firstName, @lastName, @email, @phoneNo, @dateOfBirth, @corporateID)
    DECLARE @costumerID int = @@Identity
    INSERT INTO tblDriverAccount(fldUserName, fldPasswordHash, fldDriverID, fldRoleID)
    VALUES (@userName, @password, @costumerID, (SELECT fldRoleID FROM tblRole WHERE fldRoleName = 'Driver'))
END
GO


CREATE PROCEDURE [dbo].[createUser](@userName Nvarchar(20), @password Nvarchar(150), @firstName nvarchar(20),
                                     @lastName nvarchar(25), @email nvarchar(50), @phoneNo nvarchar(50),
                                     @dateOfBirth date, @isTemporary bit)
WITH EXECUTE AS OWNER
AS
BEGIN
    SET DATEFORMAT DMY
    INSERT INTO tblCostumer (fldFirstName, fldLastName, fldEmailAddress, fldPhoneNumber, fldDateOfBirth, fldIsTemporary)
    VALUES (@firstName, @lastName, @email, @phoneNo, @dateOfBirth, @isTemporary)
    DECLARE @costumerID int = @@Identity
    IF (@isTemporary = 0)
        BEGIN
            INSERT INTO tblCostumerAccount (fldUserName, fldPasswordHash, fldCostumerID, fldRoleID)
            VALUES (@userName, @password, @costumerID, (SELECT fldRoleID FROM tblRole WHERE fldRoleName = 'Costumer'))
        END
END
GO

CREATE PROCEDURE [dbo].[createTempCustomer](@firstName nvarchar(20),
                                     @lastName nvarchar(25), @email nvarchar(50), @phoneNo nvarchar(50), @isTemporary bit, @costumerID INT OUTPUT)
WITH EXECUTE AS OWNER
AS 
BEGIN
INSERT INTO tblCostumer (fldFirstName, fldLastName, fldEmailAddress, fldPhoneNumber, fldIsTemporary)
    VALUES (@firstName, @lastName, @email, @phoneNo, @isTemporary)
	SELECT @costumerID = @@Identity
END
GO

CREATE PROCEDURE [dbo].[updateTempCustomer](@firstName nvarchar(20),
                                     @lastName nvarchar(25), @email nvarchar(50), @phoneNo nvarchar(50), @costumerID INT)
WITH EXECUTE AS OWNER
AS 
BEGIN
UPDATE tblCostumer
set fldFirstName = @firstName, fldLastName = @lastName, fldEmailAddress = @email, fldPhoneNumber = @phoneNo
WHERE fldCostumerID = @costumerID

END
GO
	

CREATE PROCEDURE [dbo].[createOrderAtDeliveryPoint](@statusID INT,@costumerID INT,@startDate dateTime,@deliverypointID INT, @orderID INT OUTPUT)
WITH EXECUTE AS OWNER
AS
BEGIN
	INSERT INTO tblOrder (fldOrderStatusID,fldCostumerID,fldStartDateTime,fldDeliveryPointID)
	VALUES (@statusID,@costumerID,@startDate,@deliverypointID)
	SELECT @orderID = @@IDENTITY
	END
GO

CREATE PROCEDURE [dbo].[createOrder](@statusID INT,@costumerID INT, @orderID INT OUTPUT)
WITH EXECUTE AS OWNER
AS
BEGIN
	INSERT INTO tblOrder (fldOrderStatusID,fldCostumerID)
	VALUES (@statusID,@costumerID)
	SELECT @orderID = @@IDENTITY
	END
GO


CREATE PROCEDURE [dbo].[createOrderItem](@orderID INT,@itemID INT,@isWashed BIT,@startDate DATETIME)
WITH EXECUTE AS OWNER
AS 
BEGIN
	INSERT INTO tblOrderItem(fldOrderID,fldLaundryItemID,fldIsWashed,fldStartDateTime)
	VALUES(@orderID, @itemID, @isWashed,@startDate)
	END
GO

CREATE PROCEDURE [dbo].[logInCostumer] @username NVARCHAR(40), @passHash NVARCHAR(80) OUTPUT, @customerID INT OUTPUT
WITH EXECUTE AS OWNER
AS
BEGIN
    SELECT @passHash = fldPasswordHash FROM tblCostumerAccount WHERE fldUserName = @username
	SELECT @customerID = fldCostumerID FROM tblCostumerAccount WHERE fldUserName = @username
END
GO

CREATE PROCEDURE [dbo].[logInDriver] @username NVARCHAR(40), @passHash NVARCHAR(80) OUTPUT
WITH EXECUTE AS OWNER
AS
BEGIN
    SELECT @passHash = fldPasswordHash FROM tblDriverAccount WHERE fldUserName = @username
END
GO

CREATE PROCEDURE [dbo].[logInDeliveryPoint] @username NVARCHAR(40), @passHash NVARCHAR(80) OUTPUT , @deliveryPointID INT OUTPUT
WITH EXECUTE AS OWNER
AS
BEGIN
    SELECT @passHash = fldPasswordHash FROM tblDeliveryPointAccount WHERE fldUserName = @username
	SELECT @deliveryPointID =fldDeliveryPointID from tblDeliveryPointAccount WHERE fldUsername = @username
END
GO

CREATE PROCEDURE [dbo].[logInLaundryAccount] @username NVARCHAR(40), @passHash NVARCHAR(80) OUTPUT,
                                             @role NVARCHAR(30) OUTPUT
WITH EXECUTE AS OWNER
AS
BEGIN
    DECLARE @roleID int = (SELECT fldRoleID
                           FROM tblLaundryAccount
                           WHERE fldLaundryAccountID =
                                 (SELECT fldLaundryAccountID FROM tblLaundryAccount WHERE fldUserName = @username))
    SELECT @passHash = fldPasswordHash FROM tblLaundryAccount WHERE fldUserName = @username
    SELECT @role = fldRoleName FROM tblRole WHERE fldRoleID = @roleID
END
GO

CREATE PROCEDURE [dbo].[updateOrder] @orderStatusID INT, @endDateTime DATETIME , @orderID INT
WITH EXECUTE AS OWNER
AS
BEGIN
    UPDATE tblOrder
	SET fldOrderStatusID = @orderStatusID, fldEndDateTime = @endDateTime
	WHERE fldOrderID = @orderID
END
GO

CREATE PROCEDURE [dbo].[deleteOrder] @orderID INT
WITH EXECUTE AS OWNER
as
BEGIN
	DELETE FROM tblOrder 
	WHERE fldOrderID = @orderID
END
GO

CREATE PROCEDURE [dbo].[deleteOrderItems] @orderItemID INT
WITH EXECUTE AS OWNER
AS 
BEGIN
	DELETE FROM tblOrderItem
	WHERE fldOrderItemID = @orderItemID
	END	
GO

CREATE PROCEDURE [dbo].[setWashedStatus] @orderItemID INT
WITH EXECUTE AS OWNER
AS
BEGIN
	IF((SELECT fldIsWashed FROM tblOrderItem WHERE fldOrderItemID = @orderItemID) = 0)
	BEGIN 
	 UPDATE tblOrderItem SET fldIsWashed = 1 WHERE fldOrderItemID = @orderItemID
	END
	ELSE
	BEGIN
	UPDATE tblOrderItem SET fldIsWashed = 0 WHERE fldOrderItemID = @orderItemID
	END
END
GO

CREATE PROCEDURE [dbo].[updateItem] @itemName NVARCHAR(50), @itemPrice Decimal(18,2), @itemDuration TINYINT, @itemID INT
WITH EXECUTE AS OWNER
AS
BEGIN
UPDATE tblLaundryItem SET fldName = @itemName, fldPrice = @itemPrice, fldHandlingDuration = @itemDuration WHERE fldLaundryItemID = @itemID
END
GO

CREATE PROCEDURE [dbo].[addItem] @itemName NVARCHAR(50), @itemPrice Decimal(18,2), @itemDuration TINYINT
WITH EXECUTE AS OWNER
AS
BEGIN
INSERT INTO tblLaundryItem (fldName, fldPrice, fldHandlingDuration) VALUES (@itemName, @itemPrice, @itemDuration)
END
GO

CREATE PROCEDURE [dbo].[getOrderByDeliveryPoint](@deliveryPointID INT, @amountOfOrders INT OUTPUT)
WITH EXECUTE AS OWNER
AS
BEGIN 
SELECT @amountOfOrders = COUNT(*) FROM tblOrder WHERE fldDeliveryPointID = @deliveryPointID
END
GO

CREATE PROCEDURE [dbo].[getAmountByItemType](@itemId INT, @amountOfItems INT OUTPUT)
WITH EXECUTE AS OWNER
AS
BEGIN 
SELECT @amountOfItems = COUNT(*) FROM tblOrderItem WHERE fldLaundryItemID = @itemId
END
GO

CREATE PROCEDURE [dbo].[confirmRouteAssignment](@routeID INT, @driverCorpID INT)
WITH EXECUTE AS OWNER
AS
BEGIN
UPDATE tblDriverAccount SET fldRouteID = @routeID WHERE fldDriverID = (SELECT fldDriverID FROM tblDriver WHERE fldCorporateIDNO = @driverCorpID)

if ((SELECT fldIsAssigned FROM tblRoute WHERE fldRouteID =@routeID)=0)
BEGIN
UPDATE tblRoute SET fldIsAssigned = 1 WHERE fldRouteID=@routeID
END
ELSE
BEGIN
UPDATE tblRoute SET fldIsAssigned = 0 WHERE fldRouteID = @routeID
END

END
GO

CREATE PROCEDURE [dbo].[getActualDuration](@laundryItemID INT,@actualDuration FLOAT OUTPUT)
WITH EXECUTE AS OWNER
AS
BEGIN
SELECT @actualDuration = AVG(DATEDIFF(day,fldStartDateTime,fldEndDateTime)) FROM tblOrderItem WHERE fldLaundryItemID = @laundryItemID 
END
GO


CREATE PROCEDURE [dbo].[getAge] (@costumerID INT,@Age INT OUTPUT)
WITH EXECUTE AS OWNER
AS
BEGIN
SELECT @Age = AVG(DATEDIFF(year,fldDateOfBirth,CURRENT_TIMESTAMP)) FROM tblCostumer WHERE fldDateOfBirth IS NOT NULL AND fldCostumerID = @costumerID
END
GO
/* Create Functions */

CREATE FUNCTION getCustomerID()
RETURNS TABLE
AS
RETURN 
SELECT fldCostumerID FROM tblCostumer
GO

CREATE FUNCTION getRoutes()
RETURNS TABLE
AS
RETURN
	SELECT * FROM tblRoute WHERE fldIsAssigned = 0
GO

CREATE FUNCTION getDrivers()
RETURNS TABLE
AS 
RETURN
	SELECT * FROM tblDriver where fldDriverID = (SELECT fldDriverID FROM tblDriverAccount WHERE fldRouteID IS NULL)
GO

CREATE FUNCTION getRouteOrder(@route INT, @status INT)
RETURNS TABLE
AS
RETURN 
	SELECT DISTINCT * FROM tblOrder WHERE fldDeliveryPointID IN (SELECT fldDeliveryPointID FROM tblDeliveryPoint WHERE fldRouteID = @route)
	AND fldOrderStatusID = @status;
	

GO

CREATE FUNCTION getDeliveryPointOrder(@deliveryPointID INT, @status INT)
RETURNS TABLE
AS
RETURN 
	SELECT * FROM tblOrder WHERE fldDeliveryPointID = @deliveryPointID AND fldOrderStatusID = @status;
	
GO

CREATE FUNCTION getOrderItem(@order INT)
RETURNS TABLE
AS
RETURN
	SELECT * FROM tblOrderItem WHERE fldOrderID = @order;

GO

CREATE FUNCTION getCentralOrder(@statusID INT)
RETURNS TABLE
AS
RETURN 
	SELECT DISTINCT * FROM tblOrder WHERE fldOrderStatusID = @statusID
GO

CREATE FUNCTION getRouteDeliveryPoints(@route INT)
RETURNS TABLE
AS
RETURN 
	SELECT * FROM tblDeliveryPoint WHERE fldRouteID = @route;
GO

CREATE FUNCTION getDeliveryPoints()
RETURNS TABLE
AS
RETURN 
	SELECT * FROM tblDeliveryPoint
GO


CREATE FUNCTION getDriverRoute(@userName VARCHAR(50))
RETURNS TABLE
AS
RETURN 
	SELECT fldRouteID FROM tblDriverAccount WHERE fldUsername = @userName
GO

CREATE FUNCTION getCostumerOrder(@userName VARCHAR(50))
RETURNS TABLE 
AS
RETURN
	SELECT fldOrderID, fldStatus, tblOrder.fldOrderStatusID
	FROM tblOrder
	INNER JOIN tblCostumerAccount
	ON tblOrder.fldCostumerID = tblCostumerAccount.fldCostumerID
	INNER JOIN tblOrderStatus
	ON tblOrder.fldOrderStatusID = tblOrderStatus.fldOrderStatusID
	WHERE tblCostumerAccount.fldUserName = @userName;
GO


CREATE FUNCTION getLaundryItems()
RETURNS TABLE
AS
RETURN
SELECT *
FROM tblLaundryItem
GO

CREATE FUNCTION getLaundryItem(@laundryItemID INT)
RETURNS TABLE
AS
RETURN 
SELECT fldName,fldPrice,fldHandlingDuration
FROM tblLaundryItem
WHERE fldLaundryItemID = @laundryItemID
GO

CREATE FUNCTION getLaundryOrderItems(@orderID INT)
RETURNS TABLE
AS
RETURN
SELECT tblOrderItem.fldLaundryItemID,fldName,fldPrice,fldHandlingDuration,fldOrderItemID
FROM tblOrderItem
INNER JOIN tblLaundryItem
ON tblOrderItem.fldLaundryItemID = tblLaundryItem.fldLaundryItemID
WHERE tblOrderItem.fldOrderID = @orderID
GO

CREATE FUNCTION getWashedStatus(@orderItemID int)
RETURNS TABLE
AS
RETURN 
SELECT fldIsWashed FROM tblOrderItem WHERE fldOrderItemID = @orderItemID
GO

CREATE FUNCTION getSearchOrder(@orderID int)
RETURNS TABLE
AS
RETURN
SELECT * FROM tblOrder WHERE fldOrderID = @orderID
GO

CREATE FUNCTION getOrderStatus(@statusID int)
RETURNS TABLE
AS
RETURN 
SELECT fldStatus FROM tblOrderStatus WHERE fldOrderStatusID = @statusID
GO
CREATE FUNCTION getOrdersByPhoneNumber(@phoneNr INT)
RETURNS TABLE
AS
RETURN
SELECT tblCostumer.fldCostumerID,fldDeliveryPointID,tblOrder.fldEndDateTime,tblOrder.fldOrderID,tblOrder.fldOrderStatusID,tblOrder.fldStartDateTime
FROM tblOrder
INNER JOIN tblCostumer
ON tblOrder.fldCostumerID = tblCostumer.fldCostumerID
WHERE tblCostumer.fldPhoneNumber = @phoneNr
GO

CREATE FUNCTION getCustomerIDByPhoneNumber(@phoneNr INT)
RETURNS TABLE
AS
RETURN
SELECT fldCostumerID from tblCostumer
WHERE fldPhoneNumber = @phoneNr
GO







/*deny overall permissions to all roles - should only be able to execute functions procedures, views.
"DENY ALL" is deprecated and support will eventually stop for that command, which is why i didn't simply use DENY ALL*/
DENY BACKUP DATABASE, BACKUP LOG, CREATE DEFAULT,
CREATE FUNCTION, CREATE PROCEDURE, CREATE RULE, CREATE TABLE, CREATE VIEW,UPDATE,ALTER, DELETE, INSERT, REFERENCES
ON DATABASE::CleaningService TO defaultAppUserRole,DriverUserRole,CostumerUserRole,DeliveryPointUserRole,LaundryAccountUserRole 

/*Grant execute on procedures to roles*/
USE CleaningService 
GRANT EXECUTE ON OBJECT::getActualDuration TO LaundryAccountUserRole
GRANT EXECUTE ON OBJECT::createDriverUser TO LaundryAccountUserRole,DriverUserRole
GRANT EXECUTE ON OBJECT::createUser TO CostumerUserRole
GRANT EXECUTE ON OBJECT::logInCostumer TO CostumerUserRole
GRANT EXECUTE ON OBJECT::logInDeliveryPoint TO DeliveryPointUserRole
GRANT EXECUTE ON OBJECT::logInDriver TO DriverUserRole
GRANT EXECUTE ON OBJECT::logInLaundryAccount TO LaundryAccountUserRole
GRANT EXECUTE ON OBJECT::updateOrder TO DriverUserRole,CostumerUserRole,DeliveryPointUserRole,LaundryAccountUserRole
GRANT EXECUTE ON OBJECT::createOrder TO CostumerUserRole,DeliveryPointUserRole
GRANT EXECUTE ON OBJECT::createOrderItem TO CostumerUserRole,DeliveryPointUserRole
GRANT EXECUTE ON OBJECT::deleteOrder TO CostumerUserRole,DeliveryPointUserRole
GRANT EXECUTE ON OBJECT::deleteOrderItems TO CostumerUserRole,DeliveryPointUserRole
GRANT EXECUTE ON OBJECT::setWashedStatus TO LaundryAccountUserRole
GRANT EXECUTE ON OBJECT::updateItem TO LaundryAccountUserRole
GRANT EXECUTE ON OBJECT::addItem TO LaundryAccountUserRole
GRANT EXECUTE ON OBJECT::getOrderByDeliveryPoint TO LaundryAccountUserRole
GRANT EXECUTE ON OBJECT::getAmountByItemType TO LaundryAccountUserRole
GRANT EXECUTE ON OBJECT::createOrderAtDeliveryPoint TO DeliveryPointUserRole
GRANT EXECUTE ON OBJECT::createTempCustomer TO DeliveryPointUserRole
GRANT EXECUTE ON OBJECT::updateTempCustomer TO DeliveryPointUserRole
GRANT EXECUTE ON OBJECT::confirmRouteAssignment TO LaundryAccountUserRole
GRANT EXECUTE ON OBJECT::getAge TO LaundryAccountUserRole
/*Grant SELECT on functions to roles*/
GRANT SELECT ON OBJECT::getOrderItem TO DriverUserRole,CostumerUserRole,DeliveryPointUserRole,LaundryAccountUserRole
GRANT SELECT ON OBJECT::getRouteOrder TO DriverUserRole
GRANT SELECT ON OBJECT::getCentralOrder TO LaundryAccountUserRole
GRANT SELECT ON OBJECT::getRouteDeliveryPoints TO DriverUserRole
GRANT SELECT ON OBJECT::getDeliveryPointOrder TO DriverUserRole
GRANT SELECT ON OBJECT::getDriverRoute TO DriverUserRole
GRANT SELECT ON OBJECT::getLaundryItems TO CostumerUserRole,DeliveryPointUserRole,LaundryAccountUserRole
GRANT SELECT ON OBJECT::getLaundryOrderItems TO CostumerUserRole, DeliveryPointUserRole
GRANT SELECT ON OBJECT::getCostumerOrder TO CostumerUserRole
GRANT SELECT ON OBJECT::getWashedStatus TO LaundryAccountUserRole
GRANT SELECT ON OBJECT::getSearchOrder TO LaundryAccountUserRole
GRANT SELECT ON OBJECT::getOrderStatus TO LaundryAccountUserRole,CostumerUserRole,DriverUserRole,DeliveryPointUserRole
GRANT SELECT ON OBJECT::getOrdersByPhoneNumber TO DeliveryPointUserRole
GRANT SELECT ON OBJECT::getDeliveryPoints TO LaundryAccountUserRole
GRANT SELECT ON OBJECT::getCustomerIDByPhoneNumber TO DeliveryPointUserRole
GRANT SELECT ON OBJECT::getLaundryItem TO DeliveryPointUserRole
GRANT SELECT ON OBJECT::getDrivers TO LaundryAccountUserRole
GRANT SELECT ON OBJECT::getRoutes TO LaundryAccountUserRole
GRANT SELECT ON OBJECT::getCustomerID TO LaundryAccountUserRole
GO

USE CleaningService

--> Check if there is existing data
IF (SELECT COUNT(*) FROM tblDeliveryPoint) <> 0 
BEGIN
    --> Clear previous data
	DELETE FROM tblOrderItem
	DELETE FROM tblOrder
	DELETE FROM tblOrderStatus
	DELETE FROM tblLaundryItem
	DELETE FROM tblDeliveryPoint 
	DELETE FROM tblRouteDeliveryDay 
	DELETE FROM tblRoute 
	DELETE FROM tblDeliveryDay 
	DELETE FROM tblCostumer
	

	--> Reset identity counter
	DBCC CHECKIDENT (tblOrderItem, RESEED, 0);
	DBCC CHECKIDENT (tblOrder, RESEED, 0);
	DBCC CHECKIDENT (tblOrderStatus, RESEED, 0);
	DBCC CHECKIDENT (tblLaundryItem, RESEED, 0);
	DBCC CHECKIDENT (tblDeliveryPoint, RESEED, 0);
	DBCC CHECKIDENT (tblRouteDeliveryDay, RESEED, 0);
	DBCC CHECKIDENT (tblRoute, RESEED, 0);
	DBCC CHECKIDENT (tblDeliveryDay, RESEED, 0);
	DBCC CHECKIDENT (tblCostumer, RESEED, 0);


END


--> Delivery Days
	INSERT INTO tblDeliveryDay VALUES('Monday');
	INSERT INTO tblDeliveryDay VALUES('Tuesday');
	INSERT INTO tblDeliveryDay VALUES('Wednesday');
	INSERT INTO tblDeliveryDay VALUES('Thursday');
	INSERT INTO tblDeliveryDay VALUES('Friday');
	INSERT INTO tblDeliveryDay VALUES('Saturday');
	INSERT INTO tblDeliveryDay VALUES('Sunday');

--> Routes
	INSERT INTO tblRoute VALUES (0);
	INSERT INTO tblRoute VALUES (1);
	INSERT INTO tblRoute VALUES (0);

--> Route delivery days
	--> Example: Route 1 is driven on Monday
	INSERT INTO tblRouteDeliveryDay VALUES(1,1);
	INSERT INTO tblRouteDeliveryDay VALUES(1,4);
	INSERT INTO tblRouteDeliveryDay VALUES(2,2);
	INSERT INTO tblRouteDeliveryDay VALUES(2,5);
	INSERT INTO tblRouteDeliveryDay VALUES(3,3);

--> Delivery Points
	--> Route 1 - Als
	INSERT INTO tblDeliveryPoint VALUES('Super Brugsen Hørup','Søndre Landevej 225',6470,'superbrugsen@mail.dk',11111111,1);
	INSERT INTO tblDeliveryPoint VALUES('Bilka','Grundtvigs Alle 195',6400,'bilka@mail.dk',22222222,1);
	INSERT INTO tblDeliveryPoint VALUES('Super Brugsen Augustenborg','Stationvej 1',6440,'sbrugsen6440@mail.dk',33333333,1);
	INSERT INTO tblDeliveryPoint VALUES('Fakta','Havnbjerg Center 15',6430,'fakta@mail.dk',44444444,1);
	INSERT INTO tblDeliveryPoint VALUES('Super Brugsen Broager','Storegade 10',6310,'sbrugsen6310@mail.dk',55555555,1);
	INSERT INTO tblDeliveryPoint VALUES('Selected Sønderborg','Kastanie Allé 3, 1.sal nr. 11',6400,'selectedsønderborg@mail.dk',66666666,1);
	--> Route 2 - Sønderjylland
	INSERT INTO tblDeliveryPoint VALUES('Cafe Butler','Store Pottergade 1',6200,'cafebutler@mail.dk',11112222,2);
	INSERT INTO tblDeliveryPoint VALUES('Rødekro Bibliotek','Vestergade 20',6230,'biblo@mail.dk',11113333,2);
	INSERT INTO tblDeliveryPoint VALUES('Imerco Løgumkloster','Markedsgade 33',6240,'imerco6240@mail.dk',11114444,2);
	INSERT INTO tblDeliveryPoint VALUES('Karens Blomster','Hovedgaden 83',6360,'karensblomster@mail.dk',11115555,2);
	INSERT INTO tblDeliveryPoint VALUES('Danske Bank Tønder','Vestergade 31',6270,'dbtønder@mail.dk',11116666,2);
	INSERT INTO tblDeliveryPoint VALUES('Davidsen Toftlund','Svinget 5',6520,'davidsen@mail.dk',11117777,2);
	--> Route 3 - Midtjylland
	INSERT INTO tblDeliveryPoint VALUES('MC Donalds Haderslev','Teaterstien 5',6100,'dengyldnemaage@mail.dk',22223333,3);
	INSERT INTO tblDeliveryPoint VALUES('Pizza og Kebab house Vojens','Vestergade 30',6500,'awesomekebab@mail.dk',22224444,3);
	INSERT INTO tblDeliveryPoint VALUES('Alabazar Gram','Kirkeallé 1',6510,'alabazar@mail.dk',22225555,3);
	INSERT INTO tblDeliveryPoint VALUES('Ribe Apotek','Tvedgade 19 a',6760,'apotekeren@mail.dk',22226666,3);
	INSERT INTO tblDeliveryPoint VALUES('Comwell Kolding','Skovbrynet 1',6000,'comwell@mail.dk',22227777,3);
	INSERT INTO tblDeliveryPoint VALUES('Sport 24 Esbjerg','Kongensgade 16',6700,'sport24@mail.dk',22228888,3);


--> Customers
	INSERT INTO tblCostumer VALUES('Jacob','Bonefeld','jacobbonefeld@live.dk',28282828,'1995-06-28',0);
	INSERT INTO tblCostumer VALUES('Robert','Skaar','robertskaar@live.dk',29292929,'2005-01-01',0);
	INSERT INTO tblCostumer VALUES('Kasper', 'Jørgensen','kasperj@live.dk',30303030,'1999-02-02',0);

--> Laundry Item
	INSERT INTO tblLaundryItem VALUES('Suit',238,3);
	INSERT INTO tblLaundryItem VALUES('Pants',100,2);
	INSERT INTO tblLaundryItem VALUES('Blazer',138,2);
	INSERT INTO tblLaundryItem VALUES('Dress, normal',239,3);
	INSERT INTO tblLaundryItem VALUES('Dress, long',309,5);
	INSERT INTO tblLaundryItem VALUES('Scarf',132,2);
	INSERT INTO tblLaundryItem VALUES('Coat',299,4);
	INSERT INTO tblLaundryItem VALUES('Cloth',110,2);
	INSERT INTO tblLaundryItem VALUES('Uniform',238,3);
	INSERT INTO tblLaundryItem VALUES('Carpet',500,7);
	INSERT INTO tblLaundryItem VALUES('Duvet',234,3);

--> Order status
	INSERT INTO tblOrderStatus VALUES('At delivery point - Ready for transit');
	INSERT INTO tblOrderStatus VALUES('In transit to cleaning central');
	INSERT INTO tblOrderStatus VALUES('At cleaning central');
	INSERT INTO tblOrderStatus VALUES('At cleaning central - Ready for transit');
	INSERT INTO tblOrderStatus VALUES('In transit to delivery point');
	INSERT INTO tblOrderStatus VALUES('Ready for pick-up');
	INSERT INTO tblOrderStatus VALUES('Completed');
	INSERT INTO tblOrderStatus VALUES('Ready to hand-in');


	INSERT INTO tblOrder(fldCostumerID, fldDeliveryPointID, fldEndDateTime, fldStartDateTime, fldOrderStatusID) VALUES (1,2,CAST('2020-05-10 13:13:41.000' as datetime),CAST('2020-05-04 13:13:41.000' as datetime),7)
--> Orders

	DECLARE @cnt INT = 0;
	DECLARE @FromDate DATETIME = '2020-05-01 00:00:00';
	DECLARE @ToDate   DATETIME = '2020-05-07 00:00:00';
	DECLARE @Seconds INT;
	DECLARE @Random INT;
	DECLARE @NumberOfOrders INT = 70;
	DECLARE @customerID INT;
	DECLARE @deliveryPointID INT;
	DECLARE @orderStatusID INT;
	DECLARE @startDate DATETIME;

	WHILE @cnt < @NumberOfOrders
	BEGIN

		-- Generates a random date
		SET @seconds = DATEDIFF(SECOND, @FromDate, @ToDate);
		SET @Random = ROUND(((@Seconds-1) * RAND()), 0);
		SET @startDate = DATEADD(SECOND, @Random, @FromDate);

		SET @customerID = FLOOR(RAND()*(3-1+1))+1;
		SET @deliveryPointID = FLOOR(RAND()*(18-1+1))+1;
		SET @orderStatusID = FLOOR(RAND()*(6-1+1))+1;

		INSERT INTO tblOrder VALUES(@customerID,@startDate,null,@deliveryPointID,@orderStatusID);

		SET @cnt = @cnt + 1;

	END;

--> Creating OrderItems

	DECLARE @count INT = 0;
	DECLARE @laundryItemID INT;
	DECLARE @orderID INT;
	DECLARE @isWashed BIT;
	DECLARE @startDateTime DATETIME;
	DECLARE @endDateTime DATETIME;

	--> On average put 4 laundry items on each order
	WHILE @count < @NumberOfOrders* 4
	BEGIN
		
		--> Inserting random laundry item to random order
		SET @laundryItemID = FLOOR(RAND()*(11-1+1))+1;
		SET @orderID = FLOOR(RAND()*(@NumberOfOrders-1+1))+1;

		--> Setting start day of washing to one day after order creation
		SET @startDateTime = DATEADD(day,1,(SELECT fldStartDateTime FROM tblOrder WHERE fldOrderID = @orderID));

		--> Sets item to washed, if the order has a status that requires it to be
		IF (SELECT fldOrderStatusID FROM tblOrder WHERE fldOrderID = @orderID) IN (4,5,6)
			BEGIN
				SET @isWashed = 1;
				SET @endDateTime =  DATEADD(day,3,(SELECT fldStartDateTime FROM tblOrder WHERE fldOrderID = @orderID));
			END
		ELSE
			BEGIN
				SET @isWashed = 0;
				SET @endDateTime = null;
			END


		INSERT INTO tblOrderItem VALUES(@laundryItemID,@orderID,@isWashed,@startDateTime,@endDateTime);

	    SET @count = @count + 1;

	END;
/*Create dummy roles*/
INSERT INTO tblRole Values('Costumer')
INSERT INTO tblRole Values('Driver')
INSERT INTO tblRole Values('Delivery Point')
INSERT INTO tblRole Values('Laundry Assistant')
INSERT INTO tblRole Values('Laundry Manager')

/*Create dummy users */
INSERT INTO tblDriver(fldFirstName,fldLastName,fldEmailAddress,fldPhoneNumber,fldDateOfBirth, fldCorporateIDNO) VALUES ('Marcus','Hansen','MH@Driving.com','50571425',CAST('09-29-1994' as date),2658)

INSERT INTO tblDriverAccount(fldUsername,fldPasswordHash,fldRouteID,fldRoleID,fldDriverID) VALUES ('MarcusHansen','$2a$12$F/3sBhRhhbQ0m69L4t3NVeGxBRGTyYrpJa7Puhu8vgP8R1AQf0LFO',null,2,1)

INSERT INTO tblDriver(fldFirstName,fldLastName,fldEmailAddress,fldPhoneNumber,fldDateOfBirth, fldCorporateIDNO) VALUES ('Jens','Jensen','JH@Driving.com','20845425',CAST('09-29-1994' as date),2808)

INSERT INTO tblDriverAccount(fldUsername,fldPasswordHash,fldRouteID,fldRoleID,fldDriverID) VALUES ('Driver','$2a$12$UyRA03kffKkHln78fRRRjO3LHMicRwVB4J44si2./SkMViMprFDbq',2,2,2)

INSERT INTO tblLaundryAccount(fldUsername,fldPasswordHash,fldRoleID) VALUES ('LaundryManager','$2a$12$4oYRfyClJXi6JHenX.c0GeTXmV40hffTvl8yk3z7qPD.iuz.uwGxS',5)

INSERT INTO tblLaundryAccount(fldUsername,fldPasswordHash,fldRoleID) VALUES ('LaundryAssistant','$2a$12$n/Q42pCmq9qIbeC2mooqzOJC6p8/eM909ZxsWdApLQHgI1QvnU8ri',4)

INSERT INTO tblDeliveryPointAccount(fldUserName,fldPasswordHash,fldRoleID,fldDeliveryPointID) VALUES ('DeliveryPoint','$2a$12$tmjWnVCTWsfwH/lGGU88xeQNeKtReYH1AqpOAv.JOi11v.gHIp1wK',3,1)

INSERT INTO tblCostumer(fldFirstName, fldLastName, fldEmailAddress, fldPhoneNumber, fldDateOfBirth,fldIsTemporary) VALUES('Lillian', 'Hansen', 'LH@costumer.com','98652530', CAST('08-25-1990' as date), 0)

INSERT INTO tblCostumerAccount(fldUserName, fldPasswordHash, fldCostumerID, fldRoleID) VALUES ('Customer','$2a$12$LLuleuXLOHnfd5KTFoQUbeipYERpWSFfZFx2JmLocqXEGWDnByD2C',4,1)



/*Insert dummy data to show handling duration will be red if avg. handling time is overdue for an item*/
DECLARE @i int = 0
WHILE @i < 300 
BEGIN
    SET @i = @i + 1
 INSERT INTO tblOrderItem(fldStartDateTime,fldEndDateTime,fldIsWashed,fldLaundryItemID,fldOrderID) VALUES (CAST('2020-05-04 13:13:41.000' as datetime),CAST('2020-05-10 13:13:41.000' as datetime),1,1,1)
END


/*Create backup Job*/
GO

USE [msdb]
GO

/****** Object:  Job [BackupCleaningService]    Script Date: 01-06-2020 13:26:35 ******/
BEGIN TRANSACTION
DECLARE @ReturnCode INT
SELECT @ReturnCode = 0
/****** Object:  JobCategory [[Uncategorized (Local)]]    Script Date: 01-06-2020 13:26:35 ******/
IF NOT EXISTS (SELECT name FROM msdb.dbo.syscategories WHERE name=N'[Uncategorized (Local)]' AND category_class=1)
BEGIN
EXEC @ReturnCode = msdb.dbo.sp_add_category @class=N'JOB', @type=N'LOCAL', @name=N'[Uncategorized (Local)]'
IF (@@ERROR <> 0 OR @ReturnCode <> 0) GOTO QuitWithRollback

END

DECLARE @jobId BINARY(16)
EXEC @ReturnCode =  msdb.dbo.sp_add_job @job_name=N'BackupCleaningService', 
		@enabled=1, 
		@notify_level_eventlog=0, 
		@notify_level_email=0, 
		@notify_level_netsend=0, 
		@notify_level_page=0, 
		@delete_level=0, 
		@description=N'backs up daily the cleaning service DB.', 
		@category_name=N'[Uncategorized (Local)]', 
		@owner_login_name=N'sa', @job_id = @jobId OUTPUT
IF (@@ERROR <> 0 OR @ReturnCode <> 0) GOTO QuitWithRollback
/****** Object:  Step [create folder DIR if non existent]    Script Date: 01-06-2020 13:26:35 ******/
EXEC @ReturnCode = msdb.dbo.sp_add_jobstep @job_id=@jobId, @step_name=N'create folder DIR if non existent', 
		@step_id=1, 
		@cmdexec_success_code=0, 
		@on_success_action=3, 
		@on_success_step_id=0, 
		@on_fail_action=2, 
		@on_fail_step_id=0, 
		@retry_attempts=0, 
		@retry_interval=0, 
		@os_run_priority=0, @subsystem=N'TSQL', 
		@command=N'EXECUTE master.dbo.xp_create_subdir ''C:\backups\''', 
		@database_name=N'master', 
		@flags=0
IF (@@ERROR <> 0 OR @ReturnCode <> 0) GOTO QuitWithRollback
/****** Object:  Step [initial full backup - remove after first run]    Script Date: 01-06-2020 13:26:35 ******/
EXEC @ReturnCode = msdb.dbo.sp_add_jobstep @job_id=@jobId, @step_name=N'initial full backup - remove after first run', 
		@step_id=2, 
		@cmdexec_success_code=0, 
		@on_success_action=3, 
		@on_success_step_id=0, 
		@on_fail_action=2, 
		@on_fail_step_id=0, 
		@retry_attempts=0, 
		@retry_interval=0, 
		@os_run_priority=0, @subsystem=N'TSQL', 
		@command=N'USE CleaningService
GO

DECLARE @backupfile sysname

SET @backupfile = N''C:\backups\CLEANINGSERVICE_DB_''+convert(nvarchar,getdate(),112)+N''.bak''

BACKUP DATABASE [CleaningService]
TO  DISK = @backupfile

GO', 
		@database_name=N'master', 
		@flags=0
IF (@@ERROR <> 0 OR @ReturnCode <> 0) GOTO QuitWithRollback
/****** Object:  Step [differential Backup]    Script Date: 01-06-2020 13:26:35 ******/
EXEC @ReturnCode = msdb.dbo.sp_add_jobstep @job_id=@jobId, @step_name=N'differential Backup', 
		@step_id=3, 
		@cmdexec_success_code=0, 
		@on_success_action=1, 
		@on_success_step_id=0, 
		@on_fail_action=2, 
		@on_fail_step_id=0, 
		@retry_attempts=0, 
		@retry_interval=0, 
		@os_run_priority=0, @subsystem=N'TSQL', 
		@command=N'USE CleaningService
GO

DECLARE @backupfile sysname

SET @backupfile = N''C:\backups\CLEANINGSERVICE_DB_''+convert(nvarchar,getdate(),112)+N''.bak''
 
BACKUP DATABASE [CleaningService]
   TO  DISK = @backupfile
   WITH DIFFERENTIAL;
GO', 
		@database_name=N'CleaningService', 
		@flags=0
IF (@@ERROR <> 0 OR @ReturnCode <> 0) GOTO QuitWithRollback
EXEC @ReturnCode = msdb.dbo.sp_update_job @job_id = @jobId, @start_step_id = 1
IF (@@ERROR <> 0 OR @ReturnCode <> 0) GOTO QuitWithRollback
EXEC @ReturnCode = msdb.dbo.sp_add_jobschedule @job_id=@jobId, @name=N'backupSchedule', 
		@enabled=1, 
		@freq_type=4, 
		@freq_interval=1, 
		@freq_subday_type=1, 
		@freq_subday_interval=0, 
		@freq_relative_interval=0, 
		@freq_recurrence_factor=0, 
		@active_start_date=20200601, 
		@active_end_date=99991231, 
		@active_start_time=220000, 
		@active_end_time=235959, 
		@schedule_uid=N'008a1abb-6d0a-49fc-890d-200955de9135'
IF (@@ERROR <> 0 OR @ReturnCode <> 0) GOTO QuitWithRollback
EXEC @ReturnCode = msdb.dbo.sp_add_jobserver @job_id = @jobId, @server_name = N'(local)'
IF (@@ERROR <> 0 OR @ReturnCode <> 0) GOTO QuitWithRollback
COMMIT TRANSACTION
GOTO EndSave
QuitWithRollback:
    IF (@@TRANCOUNT > 0) ROLLBACK TRANSACTION
EndSave:
GO






