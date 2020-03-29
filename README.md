# SQL for MongoDB

*`SQLToMongoDBTranslator`* - транслятор из SQL в команды MongoDB.

## Запуск

* *`mvn test`* - запуск тестов.
* *`mvn compile exec:java`* - запуск Main класса (читает из входного потока линии с *`sql query`* и пишет в выходной поток *`sql query -> mongoDB query`*, если запрос корректный и успешно транслирован, или *`sql query -> X`* иначе). 

## Поддерживаемые SQL запросы
### Описание
* Поддерживаются только SELECT запросы.
* Запрос может содержать WHERE, LIMIT, SKIP блоки.
* WHERE блок должен идти перед SKIP и LIMIT блоками.
* SKIP и LIMIT блоки могут идти в любом порядке относительно друг друга.
* Блок WHERE может содержать только один предикат - сравнение двух аргументов с использованием одного из операторов: <, >, =, <>.
* Аргументы в предикате WHERE блока должны содержать название колонки и значение поля (необходимо для трансляции в соответствующие предикаты команды find).
* Поддерживаются типы значений полей - строки, целые числа.
### LL(1)-грамматика
Заглавными буквами указаны нетерминалы, строчными - терминалы.<br>
* *`QUERY`* -> *`select`* *`COLUMN_NAMES`* *`from`* *`name`* *`WHERE_PART`* *`SKIP_LIMIT_PART`*<br>
* *`COLUMN_NAMES`* -> *`star`* | *`name`* *`COLUMN_NAMES_CONT`*<br>
* *`COLUMN_NAMES_CONT`* -> *`comma`* *`name`* *`COLUMN_NAMES_CONT`* | *`ε`*<br>
* *`WHERE_PART`* -> *`where`* *`CONDITION`* | *`ε`*<br>
* *`CONDITION`* -> *`name`* *`comparing_op`* *`FIELD_VALUE`* | *`FIELD_VALUE`* *`comparing_op`* *`name`*<br>
* *`FIELD_VALUE`* -> *`string`* | *`neg_int`* | *`pos_int`* <br>
* *`SKIP_LIMIT_PART`* -> *`ABS_SKIP_PART`* *`LIMIT_PART`* | *`ABS_LIMIT_PART`* *`SKIP_PART`* | *`ε`*<br>
* *`SKIP_PART`* -> *`ABS_SKIP_PART`* | *`ε`*<br>
* *`ABS_SKIP_PART`* -> *`skip`* *`pos_int`*<br>
* *`LIMIT_PART`* -> *`ABS_LIMIT_PART`* | *`ε`*<br>
* *`ABS_LIMIT_PART`* -> *`limit`* *`pos_int`*<br>

Строкое представление терминалов (приведены регулярные выражения):

* *`select`*, *`from`*, *`where`*, *`skip`*, *`limit`* - соответствующие ключевым словам строки, каждая буква в которых может быть в любом регистре.<br>
* *`name`* - *`[_a-zA-Z][_a-zA-Z0-9]*`* (кроме строк, соответствующих ключевым словам)
* *`string`* - *`'(\\.|[^\\'])*'`*<br>
* *`neg_int`* - *`-[0-9]+`*
* *`pos_int`* - *`[0-9]+`*
* *`comparing_op`* - *`<|>|=|<>`*
* *`star`* - *`*`*
* *`comma`* - *`,`*

Множества  FIRST и FOLLOW:

Нетерминал | Описание | FIRST | FOLLOW
-|-|-|-
*`QUERY`* | Запрос | `select` | `$`
*`COLUMN_NAMES`* | Список колонок | `star`, `name` | `from`
*`COLUMN_NAMES_CONT`* | Продолжение списка колонок | `comma`, `from` | `from`
*`WHERE_PART`* | Часть с WHERE блоком | `where`, `skip`, `limit`, `$` | `skip`, `limit`, `$`
*`CONDITION`* | Предикат WHERE блока | `name`, `string`, `neg_int`, `pos_int` | `skip`, `limit`, `$`
*`FIELD_VALUE`* | Значение поля |  `string`, `neg_int`, `pos_int` | `comparing_op`, `skip`, `limit`, `$` 
*`SKIP_LIMIT_PART`* | Часть со SKIP и LIMIT блоками | `skip`, `limit`, `$` | `$`
*`SKIP_PART`* | Часть со SKIP блоком | `skip`, `$` | `$`
*`ABS_SKIP_PART`* | SKIP блок | `skip` | `limit`, `$`
*`LIMIT_PART`* | Часть с LIMIT блоком | `limit`, `$` | `$`
*`ABS_LIMIT_PART`* | LIMIT блок | `limit` | `skip`, `$`

Часть с (???) блоком ~ блок в ней может как быть, так и не быть.
