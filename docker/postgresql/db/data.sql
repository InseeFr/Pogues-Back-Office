CREATE TABLE "pogues" (id VARCHAR PRIMARY KEY, data jsonb);
-- Initialize ddi root ids
CREATE TABLE "ddi_group" (id VARCHAR PRIMARY KEY);
INSERT INTO "ddi_group" VALUES ('5696739a-751c-4603-a0bc-e4c05bd41c83');