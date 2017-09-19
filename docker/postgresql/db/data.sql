CREATE TABLE "pogues" (id VARCHAR PRIMARY KEY, data jsonb);
-- Initialize ddi root ids
CREATE TABLE "ddi_group" (id VARCHAR PRIMARY KEY);
INSERT INTO "ddi_group" VALUES ('41c2eca1-8b18-4932-809c-9330ca00fef3');