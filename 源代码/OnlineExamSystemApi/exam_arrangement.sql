-- 考场编排系统相关表

-- ----------------------------
-- Table structure for exam_room
-- ----------------------------
DROP TABLE IF EXISTS `exam_room`;
CREATE TABLE `exam_room`  (
  `roomId` int(0) NOT NULL AUTO_INCREMENT COMMENT '考场ID',
  `roomName` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '考场名称',
  `roomLocation` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '考场位置',
  `roomCapacity` int(0) NULL DEFAULT NULL COMMENT '考场容量',
  `availableSeats` int(0) NULL DEFAULT NULL COMMENT '可用座位数',
  `equipment` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '考场设备',
  `status` varchar(1) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT '0' COMMENT '状态（0可用，1使用中，2维护中）',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`roomId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1001 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '考场表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for exam_seat
-- ----------------------------
DROP TABLE IF EXISTS `exam_seat`;
CREATE TABLE `exam_seat`  (
  `seatId` int(0) NOT NULL AUTO_INCREMENT COMMENT '座位ID',
  `roomId` int(0) NOT NULL COMMENT '考场ID',
  `seatNumber` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '座位号',
  `rowNumber` int(0) NULL DEFAULT NULL COMMENT '排号',
  `columnNumber` int(0) NULL DEFAULT NULL COMMENT '列号',
  `status` varchar(1) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT '0' COMMENT '状态（0可用，1已分配）',
  PRIMARY KEY (`seatId`) USING BTREE,
  INDEX `room_id`(`roomId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '座位表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for exam_arrange
-- ----------------------------
DROP TABLE IF EXISTS `exam_arrange`;
CREATE TABLE `exam_arrange`  (
  `arrangeId` int(0) NOT NULL AUTO_INCREMENT COMMENT '编排ID',
  `examCode` int(0) NOT NULL COMMENT '考试编号',
  `roomId` int(0) NOT NULL COMMENT '考场ID',
  `arrangeDate` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '考试日期',
  `session` varchar(2) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '考试场次（1:上午, 2:下午, 3:晚上）',
  `startTime` varchar(5) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '开始时间',
  `endTime` varchar(5) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '结束时间',
  `totalSeats` int(0) NOT NULL COMMENT '总座位数',
  `assignedSeats` int(0) DEFAULT 0 COMMENT '已分配座位数',
  `status` varchar(1) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT '0' COMMENT '状态（0待分配，1已分配，2进行中，3已结束）',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`arrangeId`) USING BTREE,
  INDEX `exam_code`(`examCode`) USING BTREE,
  INDEX `room_id`(`roomId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '考场编排表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for exam_student_room
-- ----------------------------
DROP TABLE IF EXISTS `exam_student_room`;
CREATE TABLE `exam_student_room`  (
  `assignId` int(0) NOT NULL AUTO_INCREMENT COMMENT '分配ID',
  `examCode` int(0) NOT NULL COMMENT '考试编号',
  `studentId` int(0) NOT NULL COMMENT '学生ID',
  `roomId` int(0) NOT NULL COMMENT '考场ID',
  `seatId` int(0) NOT NULL COMMENT '座位ID',
  `assignTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
  `status` varchar(1) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT '0' COMMENT '状态（0待考试，1考试中，2已交卷，3缺考）',
  PRIMARY KEY (`assignId`) USING BTREE,
  UNIQUE KEY `unique_student_exam` (`studentId`, `examCode`),
  INDEX `exam_code`(`examCode`) USING BTREE,
  INDEX `room_id`(`roomId`) USING BTREE,
  INDEX `seat_id`(`seatId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '学生考场分配表' ROW_FORMAT = Dynamic;