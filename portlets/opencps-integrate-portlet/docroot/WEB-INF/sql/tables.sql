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
	createDate DATE null,
	verifyCode VARCHAR(75) null,
	verifyDate DATE null,
	expiredDate DATE null
);

create table opencps_verifycode (
	id_ LONG not null primary key,
	createDate DATE null,
	verifyCode VARCHAR(75) null,
	verifyDate DATE null,
	expiredDate DATE null,
	userid LONG,
	inused BOOLEAN
);