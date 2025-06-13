CREATE TABLE role (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) UNIQUE NOT NULL
);

CREATE TABLE user_info (
    id UUID PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,

    CONSTRAINT fk_role
        FOREIGN KEY (role_id)
        REFERENCES role(id)
);

CREATE TABLE blog (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,

    thumbnail_url VARCHAR(2048),
    author_id UUID NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_author
        FOREIGN KEY(author_id)
        REFERENCES user_info(id)
        ON DELETE SET NULL
);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS
$$
BEGIN
   NEW.updated_at = NOW();
   RETURN NEW;
END;
$$
language 'plpgsql';

CREATE INDEX idx_blog_author_id ON Blog(author_id);

CREATE TRIGGER update_blog_updated_at
BEFORE UPDATE ON Blog
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TABLE blog_attachment (
    id UUID PRIMARY KEY,

    blog_id UUID NOT NULL,
    attachment_url VARCHAR(2048) NOT NULL,

    CONSTRAINT fk_blog
        FOREIGN KEY(blog_id)
        REFERENCES blog(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_blogattachment_blog_id ON BlogAttachment(blog_id);