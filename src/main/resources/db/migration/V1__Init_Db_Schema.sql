create sequence hibernate_sequence start 1 increment 1;

create table menu_item (
    id int8 not null,
    name varchar(255) not null,
    source varchar(255) not null,
    urn varchar(255) not null,
    primary key (id)
);

create table product (
    id int8 not null,
    name varchar(255) not null,
    sku varchar(255) not null,
    urn varchar(255) not null,
    menu_item_id int8,
    primary key (id)
);

create table product_price (
    id int8 not null,
    date timestamp,
    price int4,
    product_id int8,
    primary key (id)
);

create index sku_indx on product (sku);

alter table product
    add constraint unique_sku unique (sku);

alter table product
    add constraint menu_item_product_fk
    foreign key (menu_item_id)
    references menu_item;

alter table product_price
    add constraint product_price_product_fk
    foreign key (product_id)
    references product;