CREATE TABLE IF NOT EXISTS Customer (
                          customerId VARCHAR(50) PRIMARY KEY,   -- 顾客编号（唯一标识符）
                          customerName VARCHAR(100) NOT NULL,            -- 顾客姓名
                          idCard VARCHAR(18) NOT NULL,           -- 顾客身份证号码
                          phone VARCHAR(15) NOT NULL             -- 顾客联系方式
);

CREATE TABLE IF NOT EXISTS AccommodationOrder (
    order_id VARCHAR(50) PRIMARY KEY,  -- 订单ID，唯一标识符
    idCard VARCHAR(18) NOT NULL,      -- 顾客身份证号码
    room_id VARCHAR(50) NOT NULL,      -- 房间ID
    checkInDate TIMESTAMP NOT NULL,   -- 入住时间
    checkOutDate TIMESTAMP,           -- 离店时间
    ACFee DECIMAL(10, 2) DEFAULT 0,   -- 空调费用
    stayFee DECIMAL(10, 2) DEFAULT 0, -- 住宿费用
    totalfee DECIMAL(10, 2) DEFAULT 0, -- 总费用
    totalday INT DEFAULT 0                 -- 入住天数
    );

CREATE TABLE IF NOT EXISTS Room (
                                    roomId VARCHAR(50) PRIMARY KEY,  -- 房间号（主键）
    roomType INT NOT NULL,               -- 房型（1, 2, 3, 4, 5 等）
    pricePerDay DECIMAL(10, 2) NOT NULL,  -- 每日价格
    roomState VARCHAR(20) NOT NULL,      -- 状态（available, booked）
    deposit DECIMAL(10, 2) NOT NULL,  -- 押金（房价的1/5）
    temperature DECIMAL(5, 2) NOT NULL  -- 房间温度
    );



