# --- !Ups

CREATE TABLE "user_account" (
	"user_id" SERIAL PRIMARY KEY,
	"username" VARCHAR(20) NOT NULL,
	"password" VARCHAR(100) NOT NULL,
	"first_name" VARCHAR(20) NOT NULL,
	"last_name" VARCHAR(20) NOT NULL,
);

# --- !Downs
DROP TABLE user_account;