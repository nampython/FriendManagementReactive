create schema if not exists friendsmanagement;
use friendsmanagement;

-- create the user table
create table if not exists user
(
    user_id int primary key auto_increment,
    email   varchar(255) not null unique
);

-- create the friendship table
create table if not exists friendship
(
    friendship_id int primary key auto_increment,
    user_id       int                          not null,
    friend_id     int                          not null,
    status        enum ('pending', 'accepted') not null,
    foreign key (user_id) references user (user_id),
    foreign key (friend_id) references user (user_id)
);

-- create the subscription table
create table if not exists subscription
(
    subscription_id int primary key auto_increment,
    subscriber_id   int not null,
    target_id       int not null,
    foreign key (subscriber_id) references user (user_id),
    foreign key (target_id) references user (user_id)
);

-- create the block table
create table if not exists block
(
    block_id   int primary key auto_increment,
    blocker_id int not null,
    blocked_id int not null,
    foreign key (blocker_id) references user (user_id),
    foreign key (blocked_id) references user (user_id)
);

-- create the updatemention table
create table if not exists updatemention
(
    mention_id   int primary key auto_increment,
    sender_id    int not null,
    mentioned_id int not null,
    foreign key (sender_id) references user (user_id),
    foreign key (mentioned_id) references user (user_id)
);

-- Insert sample records into the User table
INSERT INTO user (email)
VALUES ('andy@example.com'),
       ('john@example.com'),
       ('lisa@example.com'),
       ('kate@example.com'),
       ('tom@example.com');

select * from user;

-- Insert sample records into the Friendship table
INSERT INTO friendship (user_id, friend_id, status)
VALUES (1, 2, 'accepted'),
       (1, 3, 'accepted'),
       (2, 3, 'accepted'),
       (3, 4, 'accepted'),
       (4, 5, 'accepted');
update friendship set status = 'accepted' where user_id = 2;
delete from friendship where friendship_id = 13;
select * from friendship;


-- Insert sample records into the Subscription table
INSERT INTO subscription (subscriber_id, target_id)
VALUES (2, 1),
       (4, 2),
       (5, 3);
INSERT INTO subscription (subscriber_id, target_id)
VALUES (1, 2);
delete from subscription where subscription_id = 21;
select * from subscription;


-- Insert sample records into the Block table
INSERT INTO block (blocker_id, blocked_id)
VALUES (1, 4),
       (3, 5);
select * from block;
delete from block where  block_id = 3;
insert into block(block_id, blocker_id, blocked_id) values (3, 1, 2);


-- Insert sample records into the UpdateMention table
INSERT INTO updatemention (sender_id, mentioned_id)
VALUES (1, 3),
       (2, 4),
       (3, 5);





