CREATE TABLE category (
                          id          UUID PRIMARY KEY,
                          name        VARCHAR(255) NOT NULL,
                          description TEXT,
                          image       VARCHAR(512), -- S3保存先URLを格納
                          created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE item (
                      id          UUID PRIMARY KEY,
                      category_id UUID         NOT NULL,
                      name        VARCHAR(255) NOT NULL,
                      image       VARCHAR(512), -- S3保存先URLを格納
                      description TEXT,
                      created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE CASCADE
);

CREATE TABLE user_tier (
                           id           UUID PRIMARY KEY,
                           anonymous_id VARCHAR(255) NOT NULL,
                           category_id  UUID         NOT NULL,
                           name         VARCHAR(255) NOT NULL,
                           is_public    BOOLEAN      NOT NULL DEFAULT FALSE,
                           access_url   VARCHAR(255) NOT NULL,
                           created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE CASCADE
);

CREATE TABLE user_tier_level (
                                 id           UUID PRIMARY KEY,
                                 user_tier_id UUID         NOT NULL,
                                 name         VARCHAR(255) NOT NULL,
                                 order_index  INT          NOT NULL,
                                 created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 CONSTRAINT fk_user_tier FOREIGN KEY (user_tier_id) REFERENCES user_tier (id) ON DELETE CASCADE,
                                 UNIQUE (user_tier_id, name),
                                 UNIQUE (user_tier_id, order_index)
);

CREATE TABLE user_tier_level_item (
                                      id                 UUID PRIMARY KEY,
                                      user_tier_id       UUID      NOT NULL,
                                      user_tier_level_id UUID      NOT NULL,
                                      item_id            UUID      NOT NULL,
                                      order_index        INT       NOT NULL,
                                      created_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      updated_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      CONSTRAINT fk_user_tier_item FOREIGN KEY (user_tier_id) REFERENCES user_tier (id) ON DELETE CASCADE,
                                      CONSTRAINT fk_user_tier_level_item FOREIGN KEY (user_tier_level_id) REFERENCES user_tier_level (id) ON DELETE CASCADE,
                                      CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES item (id) ON DELETE CASCADE,
                                      UNIQUE (user_tier_level_id, order_index)
);
