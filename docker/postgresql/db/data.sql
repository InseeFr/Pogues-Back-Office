CREATE TABLE "pogues" (id VARCHAR PRIMARY KEY, data jsonb);
-- Initialize ddi root ids
CREATE TABLE "ddi_group" (id VARCHAR PRIMARY KEY);
INSERT INTO "ddi_group" VALUES ('391e505c-dc05-4042-8b9d-c602ff72690d');