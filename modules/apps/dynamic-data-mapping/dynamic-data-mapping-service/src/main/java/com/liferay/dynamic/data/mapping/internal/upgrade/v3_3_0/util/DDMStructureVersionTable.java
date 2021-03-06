/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v3_3_0.util;

import java.sql.Types;

import java.util.HashMap;
import java.util.Map;

/**
 * @author	  Preston Crary
 * @generated
 */
public class DDMStructureVersionTable {

	public static final String TABLE_NAME = "DDMStructureVersion";

	public static final Object[][] TABLE_COLUMNS = {
		{"mvccVersion", Types.BIGINT}, {"ctCollectionId", Types.BIGINT},
		{"structureVersionId", Types.BIGINT}, {"groupId", Types.BIGINT},
		{"companyId", Types.BIGINT}, {"userId", Types.BIGINT},
		{"userName", Types.VARCHAR}, {"createDate", Types.TIMESTAMP},
		{"structureId", Types.BIGINT}, {"version", Types.VARCHAR},
		{"parentStructureId", Types.BIGINT}, {"name", Types.VARCHAR},
		{"description", Types.CLOB}, {"definition", Types.CLOB},
		{"storageType", Types.VARCHAR}, {"type_", Types.INTEGER},
		{"status", Types.INTEGER}, {"statusByUserId", Types.BIGINT},
		{"statusByUserName", Types.VARCHAR}, {"statusDate", Types.TIMESTAMP}
	};

	public static final Map<String, Integer> TABLE_COLUMNS_MAP =
new HashMap<String, Integer>();

static {
TABLE_COLUMNS_MAP.put("mvccVersion", Types.BIGINT);

TABLE_COLUMNS_MAP.put("ctCollectionId", Types.BIGINT);

TABLE_COLUMNS_MAP.put("structureVersionId", Types.BIGINT);

TABLE_COLUMNS_MAP.put("groupId", Types.BIGINT);

TABLE_COLUMNS_MAP.put("companyId", Types.BIGINT);

TABLE_COLUMNS_MAP.put("userId", Types.BIGINT);

TABLE_COLUMNS_MAP.put("userName", Types.VARCHAR);

TABLE_COLUMNS_MAP.put("createDate", Types.TIMESTAMP);

TABLE_COLUMNS_MAP.put("structureId", Types.BIGINT);

TABLE_COLUMNS_MAP.put("version", Types.VARCHAR);

TABLE_COLUMNS_MAP.put("parentStructureId", Types.BIGINT);

TABLE_COLUMNS_MAP.put("name", Types.VARCHAR);

TABLE_COLUMNS_MAP.put("description", Types.CLOB);

TABLE_COLUMNS_MAP.put("definition", Types.CLOB);

TABLE_COLUMNS_MAP.put("storageType", Types.VARCHAR);

TABLE_COLUMNS_MAP.put("type_", Types.INTEGER);

TABLE_COLUMNS_MAP.put("status", Types.INTEGER);

TABLE_COLUMNS_MAP.put("statusByUserId", Types.BIGINT);

TABLE_COLUMNS_MAP.put("statusByUserName", Types.VARCHAR);

TABLE_COLUMNS_MAP.put("statusDate", Types.TIMESTAMP);

}
	public static final String TABLE_SQL_CREATE =
"create table DDMStructureVersion (mvccVersion LONG default 0 not null,ctCollectionId LONG default 0 not null,structureVersionId LONG not null,groupId LONG,companyId LONG,userId LONG,userName VARCHAR(75) null,createDate DATE null,structureId LONG,version VARCHAR(75) null,parentStructureId LONG,name STRING null,description TEXT null,definition TEXT null,storageType VARCHAR(75) null,type_ INTEGER,status INTEGER,statusByUserId LONG,statusByUserName VARCHAR(75) null,statusDate DATE null,primary key (structureVersionId, ctCollectionId))";

	public static final String TABLE_SQL_DROP =
"drop table DDMStructureVersion";

	public static final String[] TABLE_SQL_ADD_INDEXES = {
		"create index IX_9B5D9F76 on DDMStructureVersion (ctCollectionId)",
		"create index IX_5F887AE4 on DDMStructureVersion (structureId, ctCollectionId)",
		"create index IX_4E1647CA on DDMStructureVersion (structureId, status, ctCollectionId)",
		"create unique index IX_1F8A4EA0 on DDMStructureVersion (structureId, version[$COLUMN_LENGTH:75$], ctCollectionId)"
	};

}