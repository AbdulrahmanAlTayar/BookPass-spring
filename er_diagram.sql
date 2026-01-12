CREATE TABLE "users" (
  "user_id" integer PRIMARY KEY,
  "email" varchar UNIQUE,
  "password_hash" varchar,
  "role" varchar,
  "created_at" timestamp
);

CREATE TABLE "books" (
  "book_id" integer PRIMARY KEY,
  "seller_id" integer,
  "title" varchar,
  "price" decimal,
  "status" varchar,
  "created_at" timestamp
);

CREATE TABLE "orders" (
  "order_id" integer PRIMARY KEY,
  "customer_id" integer,
  "total_amount" decimal,
  "created_at" timestamp
);

CREATE TABLE "order_items" (
  "order_item_id" integer PRIMARY KEY,
  "order_id" integer,
  "book_id" integer,
  "quantity" integer
);

CREATE TABLE "payments" (
  "payment_id" integer PRIMARY KEY,
  "order_id" integer,
  "user_id" integer,
  "amount" decimal,
  "transaction_id" varchar UNIQUE,
  "status" varchar,
  "payment_date" timestamp,
  "created_at" timestamp
);

COMMENT ON COLUMN "users"."role" IS 'customer, seller, reviewer, admin';

COMMENT ON COLUMN "books"."status" IS 'pending, available, sold';

ALTER TABLE "books" ADD FOREIGN KEY ("seller_id") REFERENCES "users" ("user_id");

ALTER TABLE "orders" ADD FOREIGN KEY ("customer_id") REFERENCES "users" ("user_id");

ALTER TABLE "order_items" ADD FOREIGN KEY ("order_id") REFERENCES "orders" ("order_id");

ALTER TABLE "order_items" ADD FOREIGN KEY ("book_id") REFERENCES "books" ("book_id");

ALTER TABLE "payments" ADD FOREIGN KEY ("order_id") REFERENCES "orders" ("order_id");

ALTER TABLE "payments" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
