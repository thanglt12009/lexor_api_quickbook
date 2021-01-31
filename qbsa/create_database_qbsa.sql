CREATE TABLE public."CompanyConfig"
(
    id serial,
    "realmId" text,
    "accessToken" text,
    "accessTokenSecret" text,
    "webhooksSubscribedEntites" text,
    "lastCdcTimestamp" text,
    "oauth2BearerToken" character varying(1000),
    PRIMARY KEY (id)
);

ALTER TABLE public."CompanyConfig" OWNER to postgres;


CREATE TABLE public."PayloadQueue" (
	id serial NOT NULL,
	"source" varchar(255) null,
	"payload" text NULL,
	"status" int null,
	CONSTRAINT "PayloadQueue_pkey" PRIMARY KEY (id)
);

ALTER TABLE public."PayloadQueue" OWNER to postgres;

CREATE TABLE public."Configurations" (
	id serial NOT NULL,
	"key" varchar(255) null,
	"value" text NULL,
	CONSTRAINT "Configurations_pkey" PRIMARY KEY (id)
);

ALTER TABLE public."Configurations" OWNER to postgres;