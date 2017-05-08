create table opencps_apikey (
	id_ LONG not null primary key,
	companyId LONG,
	groupId LONG,
	userId LONG,
	createDate DATE null,
	modifiedDate DATE null,
	lastLogin DATE null,
	apiKey VARCHAR(75) null,
	agency VARCHAR(75) null
);

create table opencps_apikey_log (
	id_ LONG not null primary key,
	companyId LONG,
	groupId LONG,
	userId LONG,
	createDate DATE null,
	modifiedDate DATE null,
	actionCode DATE null,
	apiKey VARCHAR(75) null
);