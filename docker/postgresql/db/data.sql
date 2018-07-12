CREATE TABLE "pogues" (id VARCHAR PRIMARY KEY, data jsonb);
CREATE TABLE "ddi_item"(label character varying,parent character varying,id character varying NOT NULL,groupid character varying,subgroupid character varying,studyunitid character varying,datacollectionid character varying,resourcepackageid character varying,type character varying,name character varying,CONSTRAINT pk_id PRIMARY KEY (id));

DELETE FROM "ddi_item" ;
INSERT INTO "ddi_item" VALUES (null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','937b77d6-fc9b-436e-af23-428797c381b6',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list-scheme',null);
INSERT INTO "ddi_item" VALUES ('Annee de collecte','937b77d6-fc9b-436e-af23-428797c381b6','9b52eb5c-f242-4c04-afcb-f06fd4f8b30d',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Act. eco. NA08 en 5 divisions','937b77d6-fc9b-436e-af23-428797c381b6','974e52c5-dd48-4f95-8500-1de294701ff7',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Act. eco. NAF03 en 17 sections','937b77d6-fc9b-436e-af23-428797c381b6','0fe38c81-17d8-444e-8e31-fc0468b3e8a6',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Act. eco. NAF08 en 21 sections','937b77d6-fc9b-436e-af23-428797c381b6','cd080bbd-440f-4a21-96f4-eee6978729ae',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Act. eco. NES03 en 5 secteurs','937b77d6-fc9b-436e-af23-428797c381b6','1e5110a8-e83f-4f0d-9454-e3e1e4375a3d',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Etat matrimonial des conjoints','937b77d6-fc9b-436e-af23-428797c381b6','5ed3a6be-3bee-45c6-90a0-283a938ecc26',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Nbre enfants 11 ans ou moins','937b77d6-fc9b-436e-af23-428797c381b6','eb2ff61d-46d5-404c-b264-0b8354ba7a99',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Nbre enfants 16 ans ou moins','937b77d6-fc9b-436e-af23-428797c381b6','4d162f5a-d923-44a7-b86a-53cbcf6ce099',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Nbre enfants 18 ans ou moins','937b77d6-fc9b-436e-af23-428797c381b6','3b26ac23-3820-4092-9d95-aa0dde30f0b0',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Nb enfts 24 ans ou moins famille regroup','937b77d6-fc9b-436e-af23-428797c381b6','93288f37-ae7a-448a-88a3-bb9d0493ab50',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Nbre enfants 2 ans ou moins','937b77d6-fc9b-436e-af23-428797c381b6','2d7d76a7-a544-484a-8c3e-b0d6a3252949',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Nbre enfants 3 ans ou moins','937b77d6-fc9b-436e-af23-428797c381b6','5a003970-2c3e-4250-8543-df5058a58371',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Nbre enfants 6 ans ou moins','937b77d6-fc9b-436e-af23-428797c381b6','293efbb5-401e-4d8f-805d-4b3941af62a0',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Nombre d''enfants de la famille','937b77d6-fc9b-436e-af23-428797c381b6','d49e3650-264a-449b-aa20-c371fe7ff93e',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Nombre immigres de la famille','937b77d6-fc9b-436e-af23-428797c381b6','0fa5cea6-eb9e-4938-82d7-cbacec0be881',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Nombre pers. nees dans DOM-TOM','937b77d6-fc9b-436e-af23-428797c381b6','6e1ae917-4641-4b10-93d4-e610413b4bad',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Nbre pers. originaires DOM-TOM','937b77d6-fc9b-436e-af23-428797c381b6','fbd1c0fd-bf77-4a47-80d7-7d40c4a0193c',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Nombre d''actifs de la famille','937b77d6-fc9b-436e-af23-428797c381b6','16973ef8-fc7f-4c5f-91c8-f484205dffc4',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Nbre de chomeurs de la famille','937b77d6-fc9b-436e-af23-428797c381b6','f13ccae2-fc94-4f30-b7c5-a2abaf8557b3',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Nombre personnes de la famille','937b77d6-fc9b-436e-af23-428797c381b6','5924935f-2b54-4de4-b808-2586ac2deabd',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Nombre d''actifs occupes','937b77d6-fc9b-436e-af23-428797c381b6','ec1baf66-04ba-4a31-9a7f-27eb15fe2314',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Type de commune','937b77d6-fc9b-436e-af23-428797c381b6','bd7cc987-ecef-4c59-8463-d722e52fcfef',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Type de famille condense','937b77d6-fc9b-436e-af23-428797c381b6','e950f307-840c-4650-be43-45fd2708577e',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Type de famille en 4 postes','937b77d6-fc9b-436e-af23-428797c381b6','c4cfe332-7d41-4ebb-82c3-6c7c9fb445d7',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Age condense (3 tranches)','937b77d6-fc9b-436e-af23-428797c381b6','f689d7e7-b2c1-4111-999b-010dbc97c322',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Age revolu regroupe autour de 18 ans','937b77d6-fc9b-436e-af23-428797c381b6','0433d5b1-f203-4aaf-983b-135d22ad271b',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Age revolu regroupe autour de 20 ans','937b77d6-fc9b-436e-af23-428797c381b6','33f031f7-9838-49b2-957a-aa71fb547e14',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Activite eco de la pers ref 17 sections','937b77d6-fc9b-436e-af23-428797c381b6','6a31a447-f36a-49d9-ae79-bfd9cdf4423a',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('ANTIPOL','5696739a-751c-4603-a0bc-e4c05bd41c83','41c2eca1-8b18-4932-809c-9330ca00fef3','41c2eca1-8b18-4932-809c-9330ca00fef3',null,null,null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','group',null);
INSERT INTO "ddi_item" VALUES ('Investissements et depenses courantes pour proteger l''environnement','41c2eca1-8b18-4932-809c-9330ca00fef3','5a7204a5-a91b-4aca-a383-feff8930213c','41c2eca1-8b18-4932-809c-9330ca00fef3','5a7204a5-a91b-4aca-a383-feff8930213c',null,null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','sub-group',null);
INSERT INTO "ddi_item" VALUES ('Investissements et depenses courantes pour proteger l''environnement 2016','5a7204a5-a91b-4aca-a383-feff8930213c','f8a4332c-9bc1-4a7b-958a-05eada5f9ce1','41c2eca1-8b18-4932-809c-9330ca00fef3','5a7204a5-a91b-4aca-a383-feff8930213c','f8a4332c-9bc1-4a7b-958a-05eada5f9ce1',null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','study-unit',null);
INSERT INTO "ddi_item" VALUES ('Investissements et depenses courantes pour proteger l''environnement 2016','f8a4332c-9bc1-4a7b-958a-05eada5f9ce1','d5225468-f2c0-4a1b-a662-7b892d9bd734','41c2eca1-8b18-4932-809c-9330ca00fef3','5a7204a5-a91b-4aca-a383-feff8930213c','f8a4332c-9bc1-4a7b-958a-05eada5f9ce1','d5225468-f2c0-4a1b-a662-7b892d9bd734','1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','data-collection',null);
INSERT INTO "ddi_item" VALUES (null,'d5225468-f2c0-4a1b-a662-7b892d9bd734','c84d5a0d-f5d7-434d-b346-7a06464ecf5d','41c2eca1-8b18-4932-809c-9330ca00fef3','5a7204a5-a91b-4aca-a383-feff8930213c','f8a4332c-9bc1-4a7b-958a-05eada5f9ce1','d5225468-f2c0-4a1b-a662-7b892d9bd734','1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','instrument-scheme',null);
INSERT INTO "ddi_item" VALUES ('Investissements et depenses courantes pour proteger l''environnement 2016','c84d5a0d-f5d7-434d-b346-7a06464ecf5d','37ef898c-2945-443c-bbb6-929cd4f638f9','41c2eca1-8b18-4932-809c-9330ca00fef3','5a7204a5-a91b-4aca-a383-feff8930213c','f8a4332c-9bc1-4a7b-958a-05eada5f9ce1','d5225468-f2c0-4a1b-a662-7b892d9bd734','1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','instrument','v1');
INSERT INTO "ddi_item" VALUES ('Investissements et depenses courantes pour proteger l''environnement 2017','5a7204a5-a91b-4aca-a383-feff8930213c','b16c08f4-41da-4adb-87e1-413bf6f99cf3','41c2eca1-8b18-4932-809c-9330ca00fef3','5a7204a5-a91b-4aca-a383-feff8930213c','b16c08f4-41da-4adb-87e1-413bf6f99cf3',null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','study-unit',null);
INSERT INTO "ddi_item" VALUES ('Investissements et depenses courantes pour proteger l''environnement 2017','b16c08f4-41da-4adb-87e1-413bf6f99cf3','990f2e7b-94d9-4b56-b672-ed352e0d9035','41c2eca1-8b18-4932-809c-9330ca00fef3','5a7204a5-a91b-4aca-a383-feff8930213c','b16c08f4-41da-4adb-87e1-413bf6f99cf3','990f2e7b-94d9-4b56-b672-ed352e0d9035','1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','data-collection',null);
INSERT INTO "ddi_item" VALUES (null,'990f2e7b-94d9-4b56-b672-ed352e0d9035','057fa11e-e414-4b38-b040-536aa34691a9','41c2eca1-8b18-4932-809c-9330ca00fef3','5a7204a5-a91b-4aca-a383-feff8930213c','b16c08f4-41da-4adb-87e1-413bf6f99cf3','990f2e7b-94d9-4b56-b672-ed352e0d9035','1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','instrument-scheme',null);
INSERT INTO "ddi_item" VALUES ('Investissements et depenses courantes pour proteger l''environnement 2017','057fa11e-e414-4b38-b040-536aa34691a9','62bbfbc7-c352-440f-9de3-d87c758f1a90','41c2eca1-8b18-4932-809c-9330ca00fef3','5a7204a5-a91b-4aca-a383-feff8930213c','b16c08f4-41da-4adb-87e1-413bf6f99cf3','990f2e7b-94d9-4b56-b672-ed352e0d9035','1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','instrument','v2');
INSERT INTO "ddi_item" VALUES (null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','8a18935d-678c-44f0-b60e-ac82dd265926',null,'5a7204a5-a91b-4aca-a383-feff8930213c',null,null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','code-list-scheme',null);
INSERT INTO "ddi_item" VALUES ('Oui - Non','8a18935d-678c-44f0-b60e-ac82dd265926','e5c67ac8-d438-43f7-83fa-cacf6092b28b',null,'5a7204a5-a91b-4aca-a383-feff8930213c',null,null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','code-list',null);
INSERT INTO "ddi_item" VALUES (null,'8a18935d-678c-44f0-b60e-ac82dd265926','ed45d27c-ad76-43f8-aa2c-f6fe3170b014',null,'5a7204a5-a91b-4aca-a383-feff8930213c',null,null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','code-list',null);
INSERT INTO "ddi_item" VALUES (null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','942e565e-0866-496b-b5e2-6181533385d5',null,'5a7204a5-a91b-4aca-a383-feff8930213c',null,null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','code-list-scheme',null);
INSERT INTO "ddi_item" VALUES (null,'942e565e-0866-496b-b5e2-6181533385d5','0bb9b25c-dfb5-4c93-836b-c17c0f768843',null,'5a7204a5-a91b-4aca-a383-feff8930213c',null,null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','code-list',null);
INSERT INTO "ddi_item" VALUES ('roster','942e565e-0866-496b-b5e2-6181533385d5','4790707e-40f7-4130-a156-480cf1b6e3f2',null,'5a7204a5-a91b-4aca-a383-feff8930213c',null,null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','code-list',null);
INSERT INTO "ddi_item" VALUES ('L_autoris','942e565e-0866-496b-b5e2-6181533385d5','9374665f-e99b-4068-b7c4-1b307c1af2ca',null,'5a7204a5-a91b-4aca-a383-feff8930213c',null,null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','code-list',null);
INSERT INTO "ddi_item" VALUES ('ANTIPOL-A7bis-1','942e565e-0866-496b-b5e2-6181533385d5','43a7579c-a27b-462b-9d78-8e2f73eb8c67',null,'5a7204a5-a91b-4aca-a383-feff8930213c',null,null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','code-list',null);
INSERT INTO "ddi_item" VALUES ('ANTIPOL-B2-1bis','942e565e-0866-496b-b5e2-6181533385d5','83fd3859-e7d4-470f-8de9-5633ca8797d3',null,'5a7204a5-a91b-4aca-a383-feff8930213c',null,null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','code-list',null);
INSERT INTO "ddi_item" VALUES ('DOMAINE','942e565e-0866-496b-b5e2-6181533385d5','a42051dd-e2f5-4e83-8a3f-8ed97c4d82e8',null,'5a7204a5-a91b-4aca-a383-feff8930213c',null,null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','code-list',null);
INSERT INTO "ddi_item" VALUES ('Investissement','942e565e-0866-496b-b5e2-6181533385d5','b9b2094d-b9ce-468c-9bdc-51a8cceb381d',null,'5a7204a5-a91b-4aca-a383-feff8930213c',null,null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','code-list',null);
INSERT INTO "ddi_item" VALUES ('antipol-D1-v','942e565e-0866-496b-b5e2-6181533385d5','171d0fd7-90fa-4e42-94a4-587c6fb371c5',null,'5a7204a5-a91b-4aca-a383-feff8930213c',null,null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','code-list',null);
INSERT INTO "ddi_item" VALUES ('antipol-D1-h','942e565e-0866-496b-b5e2-6181533385d5','1729f1e8-cdb1-4832-b5f7-4c4904fa3b23',null,'5a7204a5-a91b-4aca-a383-feff8930213c',null,null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','code-list',null);
INSERT INTO "ddi_item" VALUES ('antipol-D2A','942e565e-0866-496b-b5e2-6181533385d5','91c7fa13-7dd2-477c-918b-819f7ed57726',null,'5a7204a5-a91b-4aca-a383-feff8930213c',null,null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','code-list',null);
INSERT INTO "ddi_item" VALUES ('antipol-D2B','942e565e-0866-496b-b5e2-6181533385d5','e6a92073-c3d6-48bf-a726-76795e06c863',null,'5a7204a5-a91b-4aca-a383-feff8930213c',null,null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','code-list',null);
INSERT INTO "ddi_item" VALUES (null,'942e565e-0866-496b-b5e2-6181533385d5','8664de5f-47c9-449c-a99f-6c2ceebdf1ce',null,'5a7204a5-a91b-4aca-a383-feff8930213c',null,null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','code-list',null);
INSERT INTO "ddi_item" VALUES ('antipol-D2C','942e565e-0866-496b-b5e2-6181533385d5','97c8e7df-2a7b-4b63-bf29-8b102bdb73c4',null,'5a7204a5-a91b-4aca-a383-feff8930213c',null,null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','code-list',null);
INSERT INTO "ddi_item" VALUES ('antipol-D3','942e565e-0866-496b-b5e2-6181533385d5','36bca191-0623-4088-85aa-e85656403a32',null,'5a7204a5-a91b-4aca-a383-feff8930213c',null,null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','code-list',null);
INSERT INTO "ddi_item" VALUES ('antipol-D4','942e565e-0866-496b-b5e2-6181533385d5','457471f1-e7ff-4be4-94fb-d8459d344091',null,'5a7204a5-a91b-4aca-a383-feff8930213c',null,null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','code-list',null);
INSERT INTO "ddi_item" VALUES ('Liste des codes de la nomenclature NAF','942e565e-0866-496b-b5e2-6181533385d5','c5f8a4cf-a037-4ac9-8a31-bfe2ff77a8c6',null,'5a7204a5-a91b-4aca-a383-feff8930213c',null,null,'1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b','code-list',null);
INSERT INTO "ddi_item" VALUES ('Age revolu condense - 3 tranches','937b77d6-fc9b-436e-af23-428797c381b6','6ef7d67e-f0bd-4a22-9cdb-1cda397c98e0',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);
INSERT INTO "ddi_item" VALUES ('Act eco du cj de la pers ref 17 sections','937b77d6-fc9b-436e-af23-428797c381b6','508b32bc-e783-41d8-8a49-cf9c37cb01a3',null,null,null,null,'d574ed7e-2a7c-491f-b25c-f84141d4e96e','code-list',null);

INSERT INTO "ddi_item" VALUES ('ESA',null,'esa-g','esa-g',null,null,null,'rp','group',null);
INSERT INTO "ddi_item" VALUES ('Enquête sectorielle annuelle','esa-g','esa-sg','esa-g','esa-sg',null,null,'rp','sub-group',null);
INSERT INTO "ddi_item" VALUES ('Enquête sectorielle annuelle 2018','esa-sg', 'esa-su-2018','esa-g','esa-sg','esa-su-2018',null,'rp','study-unit',null);
INSERT INTO "ddi_item" VALUES ('Enquête sectorielle annuelle 2018','esa-su-2018','esa-dc-2018','esa-g','esa-sg','esa-su-2018','esa-dc-2018','rp','data-collection',null);

INSERT INTO "ddi_item" VALUES ('Artisanat',null,'art-g','art-g',null,null,null,'rp','group',null);
INSERT INTO "ddi_item" VALUES ('Enquête trimestrielle de conjoncture dans l''artisanat du bâtiment','art-g','art-sg','art-g','art-sg',null,null,'rp','sub-group',null);
INSERT INTO "ddi_item" VALUES ('Enquête trimestrielle de conjoncture dans l''artisanat du bâtiment 2018','art-sg', 'art-su-2018','art-g','art-sg','art-su-2018',null,'rp','study-unit',null);
INSERT INTO "ddi_item" VALUES ('Enquête trimestrielle de conjoncture dans l''artisanat du bâtiment 1er trimestre 2018','art-su-2018','art-dc-2018','art-g','art-sg','art-su-2018','art-dc-2018','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête trimestrielle de conjoncture dans l''artisanat du bâtiment 2ème trimestre 2018','art-su-2018','art-dc2-2018','art-g','art-sg','art-su-2018','art-dc2-2018','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête trimestrielle de conjoncture dans l''artisanat du bâtiment 3ème trimestre 2018','art-su-2018','art-dc3-2018','art-g','art-sg','art-su-2018','art-dc3-2018','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête trimestrielle de conjoncture dans l''artisanat du bâtiment 4ème trimestre 2018','art-su-2018','art-dc4-2018','art-g','art-sg','art-su-2018','art-dc4-2018','rp','data-collection',null);

INSERT INTO "ddi_item" VALUES ('Immo',null,'immo-g','immo-g',null,null,null,'rp','group',null);
INSERT INTO "ddi_item" VALUES ('Enquête trimestrielle de conjoncture dans la promotion immobilière','immo-g','immo-sg','immo-g','immo-sg',null,null,'rp','sub-group',null);
INSERT INTO "ddi_item" VALUES ('Enquête trimestrielle de conjoncture dans la promotion immobilière 2018','immo-sg', 'immo-su-2018','immo-g','immo-sg','immo-su-2018',null,'rp','study-unit',null);
INSERT INTO "ddi_item" VALUES ('Enquête trimestrielle de conjoncture dans la promotion immobilière 1er trimestre 2018','immo-su-2018','immo-dc-2018','immo-g','immo-sg','immo-su-2018','immo-dc-2018','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête trimestrielle de conjoncture dans la promotion immobilière 2ème trimestre 2018','immo-su-2018','immo-dc2-2018','immo-g','immo-sg','immo-su-2018','immo-dc2-2018','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête trimestrielle de conjoncture dans la promotion immobilière 3ème trimestre 2018','immo-su-2018','immo-dc3-2018','immo-g','immo-sg','immo-su-2018','immo-dc3-2018','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête trimestrielle de conjoncture dans la promotion immobilière 4ème trimestre 2018','immo-su-2018','immo-dc4-2018','immo-g','immo-sg','immo-su-2018','immo-dc4-2018','rp','data-collection',null);


INSERT INTO "ddi_item" VALUES ('FPE',null,'fpe-g','fpe-g',null,null,null,'rp','group',null);
INSERT INTO "ddi_item" VALUES ('Enquête auprès des salariés de l’État','fpe-g','fpe-sg','fpe-g','fpe-sg',null,null,'rp','sub-group',null);
INSERT INTO "ddi_item" VALUES ('Enquête auprès des salariés de l’État 2019','fpe-sg', 'fpe-su-2019','fpe-g','fpe-sg','fpe-su-2019',null,'rp','study-unit',null);
INSERT INTO "ddi_item" VALUES ('Enquête auprès des salariés de l’État 2019','fpe-su-2019','fpe-dc-2019','fpe-g','fpe-sg','fpe-su-2019','fpe-dc-2019','rp','data-collection',null);

INSERT INTO "ddi_item" VALUES ('EEC',null,'eec-g','eec-g',null,null,null,'rp','group',null);
INSERT INTO "ddi_item" VALUES ('Enquête Emploi en continu','eec-g','eec-sg','eec-g','eec-sg',null,null,'rp','sub-group',null);
INSERT INTO "ddi_item" VALUES ('Enquête Emploi en continu 2017','eec-sg', 'eec-su-2017','eec-g','eec-sg','eec-su-2018',null,'rp','study-unit',null);
INSERT INTO "ddi_item" VALUES ('Enquête Emploi en continu 2017 - Vague 1','eec-su-2017','eec-dc1-2017','eec-g','eec-sg','eec-su-2017','eec-dc1-2017','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête Emploi en continu 2017 - Vague 2','eec-su-2017','eec-dc2-2017','eec-g','eec-sg','eec-su-2017','eec-dc2-2017','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête Emploi en continu 2017 - Vague 3','eec-su-2017','eec-dc3-2017','eec-g','eec-sg','eec-su-2017','eec-dc3-2017','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête Emploi en continu 2017 - Vague 4','eec-su-2017','eec-dc4-2017','eec-g','eec-sg','eec-su-2017','eec-dc4-2017','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête Emploi en continu 2017 - Vague 5','eec-su-2017','eec-dc5-2017','eec-g','eec-sg','eec-su-2017','eec-dc5-2017','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête Emploi en continu 2017 - Vague 6','eec-su-2017','eec-dc6-2017','eec-g','eec-sg','eec-su-2017','eec-dc6-2017','rp','data-collection',null);

INSERT INTO "ddi_item" VALUES ('EAP',null,'eap-g','eap-g',null,null,null,'rp','group',null);
INSERT INTO "ddi_item" VALUES ('Enquête Achats-Production','eap-g','eap-sg','eap-g','eap-sg',null,null,'rp','sub-group',null);
INSERT INTO "ddi_item" VALUES ('Enquête Achats-Production 2018','eap-sg', 'eap-su-2018','eap-g','eap-sg','eap-su-2018',null,'rp','study-unit',null);
INSERT INTO "ddi_item" VALUES ('Enquête Achats-Production 2018','eap-su-2018','eap-dc-2018','eap-g','eap-sg','eap-su-2018','eap-dc-2018','rp','data-collection',null);

INSERT INTO "ddi_item" VALUES ('EMAGSA',null,'emagsa-g','emagsa-g',null,null,null,'rp','group',null);
INSERT INTO "ddi_item" VALUES ('Enquête mensuelle sur l''activité des grandes surfaces alimentaires','emagsa-g','emagsa-sg','emagsa-g','emagsa-sg',null,null,'rp','sub-group',null);
INSERT INTO "ddi_item" VALUES ('Enquête mensuelle sur l''activité des grandes surfaces alimentaires 2018','emagsa-sg', 'emagsa-su-2018','emagsa-g','emagsa-sg','emagsa-su-2018',null,'rp','study-unit',null);
INSERT INTO "ddi_item" VALUES ('Enquête mensuelle sur l''activité des grandes surfaces alimentaires janvier 2018','emagsa-su-2018','emagsa-dc1-2018','emagsa-g','emagsa-sg','emagsa-su-2018','emagsa-dc1-2018','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête mensuelle sur l''activité des grandes surfaces alimentaires février 2018','emagsa-su-2018','emagsa-dc2-2018','emagsa-g','emagsa-sg','emagsa-su-2018','emagsa-dc2-2018','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête mensuelle sur l''activité des grandes surfaces alimentaires mars 2018','emagsa-su-2018','emagsa-dc3-2018','emagsa-g','emagsa-sg','emagsa-su-2018','emagsa-dc3-2018','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête mensuelle sur l''activité des grandes surfaces alimentaires avril 2018','emagsa-su-2018','emagsa-dc4-2018','emagsa-g','emagsa-sg','emagsa-su-2018','emagsa-dc4-2018','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête mensuelle sur l''activité des grandes surfaces alimentaires mai 2018','emagsa-su-2018','emagsa-dc5-2018','emagsa-g','emagsa-sg','emagsa-su-2018','emagsa-dc5-2018','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête mensuelle sur l''activité des grandes surfaces alimentaires juin 2018','emagsa-su-2018','emagsa-dc6-2018','emagsa-g','emagsa-sg','emagsa-su-2018','emagsa-dc6-2018','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête mensuelle sur l''activité des grandes surfaces alimentaires juillet 2018','emagsa-su-2018','emagsa-dc7-2018','emagsa-g','emagsa-sg','emagsa-su-2018','emagsa-dc7-2018','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête mensuelle sur l''activité des grandes surfaces alimentaires août 2018','emagsa-su-2018','emagsa-dc8-2018','emagsa-g','emagsa-sg','emagsa-su-2018','emagsa-dc8-2018','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête mensuelle sur l''activité des grandes surfaces alimentaires septembre 2018','emagsa-su-2018','emagsa-dc9-2018','emagsa-g','emagsa-sg','emagsa-su-2018','emagsa-dc9-2018','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête mensuelle sur l''activité des grandes surfaces alimentaires octobre 2018','emagsa-su-2018','emagsa-dc10-2018','emagsa-g','emagsa-sg','emagsa-su-2018','emagsa-dc10-2018','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête mensuelle sur l''activité des grandes surfaces alimentaires novembre 2018','emagsa-su-2018','emagsa-dc11-2018','emagsa-g','emagsa-sg','emagsa-su-2018','emagsa-dc11-2018','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête mensuelle sur l''activité des grandes surfaces alimentaires décembre 2018','emagsa-su-2018','emagsa-dc12-2018','emagsa-g','emagsa-sg','emagsa-su-2018','emagsa-dc12-2018','rp','data-collection',null);

INSERT INTO "ddi_item" VALUES ('EAMTIC',null,'eamtic-g','eamtic-g',null,null,null,'rp','group',null);
INSERT INTO "ddi_item" VALUES ('Enquête annuelle auprès des ménages sur les technologies de l''information et de la communication','eamtic-g','eamtic-sg','eamtic-g','eamtic-sg',null,null,'rp','sub-group',null);
INSERT INTO "ddi_item" VALUES ('Enquête annuelle auprès des ménages sur les technologies de l''information et de la communication 2018','eamtic-sg', 'eamtic-su-2018','eamtic-g','eamtic-sg','eamtic-su-2018',null,'rp','study-unit',null);
INSERT INTO "ddi_item" VALUES ('Enquête annuelle auprès des ménages sur les technologies de l''information et de la communication 2018','eamtic-su-2018','eamtic-dc-2018','eamtic-g','eamtic-sg','eamtic-su-2018','eamtic-dc-2018','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête annuelle auprès des ménages sur les technologies de l''information et de la communication 2019','eamtic-sg', 'eamtic-su-2019','eamtic-g','eamtic-sg','eamtic-su-2019',null,'rp','study-unit',null);
INSERT INTO "ddi_item" VALUES ('Enquête annuelle auprès des ménages sur les technologies de l''information et de la communication 2019','eamtic-su-2019','eamtic-dc-2019','eamtic-g','eamtic-sg','eamtic-su-2019','eamtic-dc-2019','rp','data-collection',null);

INSERT INTO "ddi_item" VALUES ('ETICE',null,'etice-g','etice-g',null,null,null,'rp','group',null);
INSERT INTO "ddi_item" VALUES ('Enquête sur les technologies de l''information et de la communication dans les entreprises','etice-g','etice-sg','etice-g','etice-sg',null,null,'rp','sub-group',null);
INSERT INTO "ddi_item" VALUES ('Enquête sur les technologies de l''information et de la communication dans les entreprises 2018','etice-sg', 'etice-su-2018','etice-g','etice-sg','etice-su-2018',null,'rp','study-unit',null);
INSERT INTO "ddi_item" VALUES ('Enquête sur les technologies de l''information et de la communication dans les entreprises 2018','etice-su-2018','etice-dc-2018','etice-g','etice-sg','etice-su-2018','etice-dc-2018','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête sur les technologies de l''information et de la communication dans les entreprises 2019','etice-sg', 'etice-su-2019','etice-g','etice-sg','etice-su-2019',null,'rp','study-unit',null);
INSERT INTO "ddi_item" VALUES ('Enquête sur les technologies de l''information et de la communication dans les entreprises 2019','etice-su-2019','etice-dc-2019','etice-g','etice-sg','etice-su-2019','etice-dc-2019','rp','data-collection',null);

INSERT INTO "ddi_item" VALUES ('RECME',null,'recme-g','recme-g',null,null,null,'rp','group',null);
INSERT INTO "ddi_item" VALUES ('Répertoire des entreprises contrôlées majoritairement par l''État','recme-g','recme-sg','recme-g','recme-sg',null,null,'rp','sub-group',null);
INSERT INTO "ddi_item" VALUES ('Répertoire des entreprises contrôlées majoritairement par l''État 2018','recme-sg', 'recme-su-2018','recme-g','recme-sg','recme-su-2018',null,'rp','study-unit',null);
INSERT INTO "ddi_item" VALUES ('Répertoire des entreprises contrôlées majoritairement par l''État 2018','recme-su-2018','recme-dc-2018','recme-g','recme-sg','recme-su-2018','recme-dc-2018','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Répertoire des entreprises contrôlées majoritairement par l''État 2019','recme-sg', 'recme-su-2019','recme-g','recme-sg','recme-su-2019',null,'rp','study-unit',null);
INSERT INTO "ddi_item" VALUES ('Répertoire des entreprises contrôlées majoritairement par l''État 2019','recme-su-2019','recme-dc-2019','recme-g','recme-sg','recme-su-2019','recme-dc-2019','rp','data-collection',null);

INSERT INTO "ddi_item" VALUES ('Enquête sur les investissements dans l''industrie pour protéger l''environnement 2018','eiipe-dc-2018','eiipe-i-2018','eiipe-g','eiipe-sg','eiipe-su-2018','eiipe-dc-2018','rp','instrument','v1');
INSERT INTO "ddi_item" VALUES ('Enquête sur les investissements dans l''industrie pour protéger l''environnement 2019','eiipe-dc-2019','eiipe-i-2019','eiipe-g','eiipe-sg','eiipe-su-2019','eiipe-dc-2019','rp','instrument','v1');
INSERT INTO "ddi_item" VALUES ('Enquête sur les investissements dans l''industrie pour protéger l''environnement 2020','eiipe-dc-2019','eiipe-i-2020','eiipe-g','eiipe-sg','eiipe-su-2020','eiipe-dc-2020','rp','instrument','v1');


INSERT INTO "ddi_item" VALUES ('EIIPE',null,'eiipe-g','eiipe-g',null,null,null,'rp','group',null);
INSERT INTO "ddi_item" VALUES ('Enquête sur les investissements dans l''industrie pour protéger l''environnement','eiipe-g','eiipe-sg','eiipe-g','eiipe-sg',null,null,'rp','sub-group',null);
INSERT INTO "ddi_item" VALUES ('Enquête sur les investissements dans l''industrie pour protéger l''environnement 2018','eiipe-sg', 'eiipe-su-2018','eiipe-g','eiipe-sg','eiipe-su-2018',null,'rp','study-unit',null);
INSERT INTO "ddi_item" VALUES ('Enquête sur les investissements dans l''industrie pour protéger l''environnement 2018','eiipe-su-2018','eiipe-dc-2018','eiipe-g','eiipe-sg','eiipe-su-2018','eiipe-dc-2018','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête sur les investissements dans l''industrie pour protéger l''environnement 2019','eiipe-sg', 'eiipe-su-2019','eiipe-g','eiipe-sg','eiipe-su-2019',null,'rp','study-unit',null);
INSERT INTO "ddi_item" VALUES ('Enquête sur les investissements dans l''industrie pour protéger l''environnement 2019','eiipe-su-2019','eiipe-dc-2019','eiipe-g','eiipe-sg','eiipe-su-2019','eiipe-dc-2019','rp','data-collection',null);
INSERT INTO "ddi_item" VALUES ('Enquête sur les investissements dans l''industrie pour protéger l''environnement 2020','eiipe-sg', 'eiipe-su-2020','eiipe-g','eiipe-sg','eiipe-su-2020',null,'rp','study-unit',null);
INSERT INTO "ddi_item" VALUES ('Enquête sur les investissements dans l''industrie pour protéger l''environnement 2020','eiipe-su-2020','eiipe-dc-2020','eiipe-g','eiipe-sg','eiipe-su-2020','eiipe-dc-2020','rp','data-collection',null);

INSERT INTO "ddi_item" VALUES ('EACEI',null,'eacei-g','eacei-g',null,null,null,'rp','group',null);
INSERT INTO "ddi_item" VALUES ('Enquête sur les consommations d''énergie dans l''industrie','eacei-g','eacei-sg','eacei-g','eacei-sg',null,null,'rp','sub-group',null);
INSERT INTO "ddi_item" VALUES ('Enquête sur les consommations d''énergie dans l''industrie 2018','eacei-sg', 'eacei-su-2018','eacei-g','eacei-sg','eacei-su-2018',null,'rp','study-unit',null);
INSERT INTO "ddi_item" VALUES ('Enquête sur les consommations d''énergie dans l''industrie 2018','eacei-su-2018','eacei-dc-2018','eacei-g','eacei-sg','eacei-su-2018','eacei-dc-2018','rp','data-collection',null);

INSERT INTO "ddi_item" VALUES ('EFASGSO',null,'efasgso-g','efasgso-g',null,null,null,'rp','group',null);
INSERT INTO "ddi_item" VALUES ('Enquête sur la filière aéronautique et Spatiale dans le Grand Sud-Ouest','efasgso-g','efasgso-sg','efasgso-g','efasgso-sg',null,null,'rp','sub-group',null);
INSERT INTO "ddi_item" VALUES ('Enquête sur la filière aéronautique et Spatiale dans le Grand Sud-Ouest 2018','efasgso-sg', 'efasgso-su-2018','efasgso-g','efasgso-sg','efasgso-su-2018',null,'rp','study-unit',null);
INSERT INTO "ddi_item" VALUES ('Enquête sur la filière aéronautique et Spatiale dans le Grand Sud-Ouest 2018','efasgso-su-2018','efasgso-dc-2018','efasgso-g','efasgso-sg','efasgso-su-2018','efasgso-dc-2018','rp','data-collection',null);

 

