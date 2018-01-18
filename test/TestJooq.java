import jooq.generated.library.tables.Author;
import jooq.generated.library.tables.Book;
import jooq.generated.library.tables.records.AuthorRecord;
import junit.framework.TestCase;
import lombok.SneakyThrows;
import org.apache.log4j.xml.DOMConfigurator;
import org.jooq.*;
import org.jooq.conf.RenderNameStyle;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;

import static jooq.generated.library.Tables.AUTHOR;
import static jooq.generated.library.Tables.BOOK;
import static jooq.generated.library.Tables.LANGUAGE;
import static org.jooq.impl.DSL.*;
import static org.jooq.util.postgres.PostgresDataType.VARCHAR;

/*
 * Author: glaschenko
 * Created: 14.01.2018
 */
public class TestJooq extends TestCase {
    private Connection connection;
    private Settings settings;
    private Configuration configuration;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DOMConfigurator.configure("log4j-config.xml");
        connection = getConnection("library2");
        settings = createSettings();
        configuration = createConfiguration();
    }

    @Override
    protected void tearDown() throws Exception {
        connection.close();
    }

    @SneakyThrows
    public void testBasic() {
        AuthorRecord author =
                using(configuration).selectFrom(AUTHOR)
                        .where(AUTHOR.ID.eq(1))
                        .fetchOne();
        author.setLastName("Orwell");
        author.store();

        System.out.println(author);
    }

    public void testSelectWithJoin(){
        DSLContext create = using(configuration);


        Author a = AUTHOR.as("a");
        Book b = BOOK.as("b");
        Result<Record> result =create.select()
                .from(a)
                .join(b)
                .on(b.AUTHOR_ID.eq(a.ID))
                .where(a.LAST_NAME.like("%well%"))
                .orderBy(b.TITLE)
                .fetch();
        System.out.println(result);

    }

    public void testComplexSelect(){
        /*-- get all authors' first and last names, and the number
                -- of books they've written in German, if they have written
                -- more than five books in German in the last three years
                -- (from 2011), and sort those authors by last names
                -- limiting results to the second and third row, locking
        -- the rows for a subsequent update... whew!

                SELECT AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME, COUNT(*)
        FROM AUTHOR
        JOIN BOOK ON AUTHOR.ID = BOOK.AUTHOR_ID
        WHERE BOOK.LANGUAGE = 'DE'
        AND BOOK.PUBLISHED > '2008-01-01'
        GROUP BY AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME
        HAVING COUNT(*) > 5
        ORDER BY AUTHOR.LAST_NAME ASC NULLS FIRST
        LIMIT 2
        OFFSET 1
        FOR UPDATE */

        Result<Record3<String, String, Integer>> res = using(configuration).select(AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME, count())
                .from(AUTHOR)
                .join(BOOK).on(AUTHOR.ID.eq(BOOK.AUTHOR_ID))
                .join(LANGUAGE).on(BOOK.LANGUAGE_ID.eq(LANGUAGE.ID))
                .where(LANGUAGE.CD.eq("en").and(BOOK.PUBLISHED_IN.gt(1900)))
                .groupBy(AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                .having(count().gt(1))
                .orderBy(AUTHOR.LAST_NAME.asc().nullsFirst())
                .fetch();
        System.out.println(res);

    }

    public void testJoinOnKey(){
        Result<Record3<String, String, String>> res = using(configuration).select(AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME, BOOK.TITLE).from(AUTHOR)
                .join(BOOK).onKey()
                .fetch();
        System.out.println(res);
    }

    public void testSimpleAggregate(){
        Result<Record1<Integer>> res = using(configuration).selectCount().from(BOOK).fetch();
//        Result<Record1<Integer>> res = using(configuration).selectCount().from(BOOK).groupBy().fetch();
        System.out.println(res);
    }

    public void testUnion(){
        Result<Record> res = using(configuration).select().from(BOOK)
                .unionAll(
                        select().from(BOOK)).orderBy(BOOK.PUBLISHED_IN).fetch();
        System.out.println(res);
    }

    public void testInsert(){
        using(configuration).insertInto(AUTHOR, AUTHOR.ID, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME, AUTHOR.YEAR_OF_BIRTH)
                .values(1905, "Andrey", "Glaschenko", 1982)
                .execute();
        Result<Record> res = using(configuration).select().from(AUTHOR).where(AUTHOR.ID.eq(1905)).fetch();
        System.out.println(res);
    }

    public void testUpdate(){
        using(configuration).update(AUTHOR).set(AUTHOR.FIRST_NAME, "Viktor")
                .where(AUTHOR.ID.eq(1905)).execute();
    }

    public void testSubSelect(){
        using(configuration).update(AUTHOR).set(AUTHOR.FIRST_NAME,
                select(AUTHOR.FIRST_NAME).from(AUTHOR).where(AUTHOR.ID.eq(1)))
                .where(AUTHOR.ID.eq(1905)).execute();
    }

    public void testDelete(){
        using(configuration).deleteFrom(AUTHOR)
                .where(AUTHOR.ID.eq(1905)).execute();
    }

    public void testAlterTable(){
        using(configuration).alterTable(AUTHOR).add("nationality", VARCHAR.length(5).nullable(true))
                .execute();
    }

    public void testGenerateDDL(){
        Queries ddl = using(configuration).ddl(BOOK.getSchema());
        for (Query query : ddl) {
            System.out.println(query);
        }
    }

    public void testDerivedTable(){
        Table<Record1<Integer>> authorIds = using(configuration).select(AUTHOR.ID)
                .from(AUTHOR)
                .orderBy(AUTHOR.ID)
                .offset(1).limit(1).asTable();
        Result<Record> res = using(configuration).select()
                .from(BOOK)
                .join(authorIds).on(BOOK.AUTHOR_ID.eq(authorIds.field(0, Integer.class)))
                .fetch();
        System.out.println(res);

    }

    private Configuration createConfiguration() {
        Configuration conf = new DefaultConfiguration();
        conf.set(SQLDialect.POSTGRES_9_3);
        conf.set(connection);
        conf.set(settings);
        return conf;
    }

    @SneakyThrows
    private Connection getConnection(String dbName) {
        String userName = "root";
        String password = "root";
        String url = "jdbc:postgresql://localhost:5432/" + dbName;
        return DriverManager.getConnection(url, userName, password);
    }

    private Settings createSettings() {
        return new Settings()
                .withRenderCatalog(false)
                .withRenderSchema(false)
                .withRenderNameStyle(RenderNameStyle.AS_IS);

    }

}
