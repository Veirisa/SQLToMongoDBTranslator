import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class SQLToMongoDBTranslatorTest {

    private SQLToMongoDBTranslator translator = new SQLToMongoDBTranslator();

    @Nullable
    private String getTranslationResult(@NotNull String sqlQuery) {
        try {
            return translator.translate(sqlQuery);
        } catch (TranslateException e) {
            return null;
        }
    }

    @Test
    public void columnNamesTest() {
        Assertions.assertEquals(
                "db.collection.find({})",
                getTranslationResult("SELECT * FROM collection")
        );
        Assertions.assertEquals(
                "db.collection.find({}, {column_name: 1})",
                getTranslationResult("SELECT column_name FROM collection")
        );
        Assertions.assertEquals(
                "db.collection.find({}, {a: 1, b: 1, c: 1, d: 1})",
                getTranslationResult("SELECT a, b, c, d FROM collection")
        );
    }

    @Test
    public void wherePartTest() {
        Assertions.assertEquals(
                "db.collection.find({age: {$gt: 22}})",
                getTranslationResult("SELECT * FROM collection WHERE age > 22")
        );
        Assertions.assertEquals(
                "db.collection.find({name: {$lt: 'abcd'}})",
                getTranslationResult("SELECT * FROM collection WHERE name < 'abcd'")
        );
        Assertions.assertEquals(
                "db.collection.find({income: {$eq: -11}})",
                getTranslationResult("SELECT * FROM collection WHERE income = -11")
        );
        Assertions.assertEquals(
                "db.collection.find({name: {$ne: 'abcd'}})",
                getTranslationResult("SELECT * FROM collection WHERE 'abcd' <> name")
        );
        Assertions.assertEquals(
                "db.collection.find({income: {$gt: -11}})",
                getTranslationResult("SELECT * FROM collection WHERE -11 < income")
        );
        Assertions.assertNull(getTranslationResult("SELECT * FROM collection WHERE income < age"));
        Assertions.assertNull(getTranslationResult("SELECT * FROM collection WHERE -11 <> 22"));
    }

    @Test
    public void skipLimitPartTest() {
        Assertions.assertEquals(
                "db.collection.find({}).limit(10)",
                getTranslationResult("SELECT * FROM collection LIMIT 10")
        );
        Assertions.assertEquals(
                "db.collection.find({}).skip(2)",
                getTranslationResult("SELECT * FROM collection SKIP 2")
        );
        Assertions.assertEquals(
                "db.collection.find({}).limit(10).skip(2)",
                getTranslationResult("SELECT * FROM collection LIMIT 10 SKIP 2")
        );
        Assertions.assertEquals(
                "db.collection.find({}).skip(2).limit(10)",
                getTranslationResult("SELECT * FROM collection SKIP 2 LIMIT 10")
        );
        Assertions.assertNull(getTranslationResult("SELECT * FROM collection LIMIT 10 LIMIT 2"));
        Assertions.assertNull(getTranslationResult("SELECT * FROM collection SKIP 2 SKIP 10"));
        Assertions.assertNull(getTranslationResult("SELECT * FROM collection LIMIT '10'"));
        Assertions.assertNull(getTranslationResult("SELECT * FROM collection SKIP -2"));
    }

    @Test
    public void combinationTest() {
        Assertions.assertEquals(
                "db.collection.find({age: {$ne: 22}}, {column_name: 1})",
                getTranslationResult("SELECT column_name FROM collection WHERE age <> 22")
        );
        Assertions.assertEquals(
                "db.collection.find({}, {a: 1, b: 1, c: 1, d: 1}).skip(2).limit(10)",
                getTranslationResult("SELECT a, b, c, d FROM collection SKIP 2 LIMIT 10")
        );
        Assertions.assertEquals(
                "db.collection.find({income: {$lt: -11}}).limit(10).skip(2)",
                getTranslationResult("SELECT * FROM collection WHERE income < -11 LIMIT 10 SKIP 2")
        );
        Assertions.assertEquals(
                "db.collection.find({name: {$eq: 'abcd'}}).skip(2).limit(10)",
                getTranslationResult("SELECT * FROM collection WHERE name = 'abcd' SKIP 2 LIMIT 10")
        );
        Assertions.assertEquals(
                "db.collection.find({age: {$lt: 22}}, {column_name: 1}).limit(10)",
                getTranslationResult("SELECT column_name FROM collection WHERE 22 > age LIMIT 10")
        );
        Assertions.assertEquals(
                "db.collection.find({income: {$ne: -11}}, {a: 1, b: 1, c: 1, d: 1}).skip(2)",
                getTranslationResult("SELECT a, b, c, d FROM collection WHERE -11 <> income SKIP 2")
        );
    }

    @Test
    public void parsingTest() {
        Assertions.assertEquals(
                "db.collection.find({age: {$gt: 22}}, {a: 1, b: 1, c: 1, d: 1}).skip(2).limit(10)",
                getTranslationResult("SeLeCt a, b, c, d fRoM collection whEre age > 22 SKip 2 liMIT 10")
        );
        Assertions.assertEquals(
                "db.collection.find({age: {$gt: 22}}, {a: 1, b: 1, c: 1, d: 1}).skip(2).limit(10)",
                getTranslationResult("   SELECT  a,b,   c  ,d    FROM  collection   " +
                        " WHERE age> 22    SKIP  2  LIMIT 10    ")
        );
        Assertions.assertEquals(
                "db.c0lL__EcTi0n.find({n23_ame: {$gt: ''}}, {a1_A2_: 1, _b_B: 1, C13: 1, _Dd_1_23_: 1})",
                getTranslationResult("SELECT a1_A2_, _b_B, C13, _Dd_1_23_ FROM c0lL__EcTi0n WHERE n23_ame > ''")
        );
        Assertions.assertEquals(
                "db.collection.find({name: {$gt: 'a\\'b\\\\\\cd\\''}}, {a: 1, b: 1, c: 1, d: 1})",
                getTranslationResult("SELECt a, b, c, d FROM collection WHERE name > 'a\\'b\\\\\\cd\\''")
        );
        Assertions.assertNull(getTranslationResult("SELECT * FROMcollection"));
        Assertions.assertNull(getTranslationResult("SELECT * FROM 0_nameStartFromDigit"));
        Assertions.assertNull(getTranslationResult("SELECT * FROM collection WHERE22 < age"));
        Assertions.assertNull(getTranslationResult("SELECT * FROM collection WHERE age > 22LIMIT 10"));
        Assertions.assertNull(getTranslationResult("SELECT * FROM collection WHERE name = 'ab'cd'"));
        Assertions.assertNull(getTranslationResult("SELECT * FROM collection WHERE 'abcd\\' = name"));
    }
}
