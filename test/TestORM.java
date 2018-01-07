import junit.framework.TestCase;
import lombok.SneakyThrows;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import orm_test.Family;
import orm_test.Person;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/*
 * Author: glaschenko
 * Created: 31.12.2017
 */
public class TestORM extends TestCase {
    private static final String PERSISTENCE_UNIT_NAME = "family";
    private EntityManagerFactory factory;

    @SneakyThrows
    public void testCreateFamily() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();
        em.getTransaction().begin();
        persistFamily(em, 1);
        persistFamily(em, 2);
        em.getTransaction().commit();
        em.close();
    }

    private void persistFamily(EntityManagerImpl em, int index) throws ParseException {
        DateFormat f = SimpleDateFormat.getDateInstance();
        Family family = new Family();
        family.setFamilyName(("The Glaschenkos " + index));
        Person kostya = new Person();
        kostya.setName("Kostya Glaschenko " + index);
        kostya.setDob(f.parse("17.05.1982"));
        Person andrey = new Person();
        andrey.setDob(f.parse("19.05.1982"));
        andrey.setMale(true);
        andrey.setName("Andrey Glaschenko " + index);
        ArrayList<Person> members = new ArrayList<>();
        members.add(kostya);
        members.add(andrey);
        family.setMembers(members);
        kostya.setFamily(family);
        andrey.setFamily(family);

        em.persist(kostya);
        em.persist(andrey);
        em.persist(family);
    }

    @SneakyThrows
    public void testLoadFamily(){
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();
        Family family = loadFamilyOnce(em);
        System.out.println(family);
        //testing L1 cache
//        em.clear();
        family = loadFamilyOnce(em);
        System.out.println(family);
        em.close();

        //testing L2 cache
        em = factory.createEntityManager();
        family = loadFamilyOnce(em);
        System.out.println(family);
    }

    public void testJoin(){
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();
        em.getTransaction().begin();
        TypedQuery<Person> query = em.createQuery("select p from Person p join p.family f where f.familyName like :name",
                Person.class);
        query.setParameter("name", "%2%");
        List<Person> list = query.getResultList();
        list.forEach(System.out::println);
    }

    public void testNativeQuery(){
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();
        Query query = em.createNativeQuery("select * from family");
        List res = query.getResultList();
        Object family = res.get(0);
    }

    public void testCriteriaAPI(){
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();

        em.getTransaction().begin();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Family> q = builder.createQuery(Family.class);
        Root<Family> from = q.from(Family.class);
        q.select(from);
        TypedQuery<Family> emQuery = em.createQuery(q);
        List<Family> resultList = emQuery.getResultList();
        Family family2 = resultList.get(0);
        System.out.println(family2);
        System.out.println("-------before getMembers----------");
        List<Person> members = family2.getMembers();
        System.out.println(members);
        System.out.println(family2);

        em.close();
    }

    public void testCriteriaAPI2(){
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();

        em.getTransaction().begin();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Family> query = builder.createQuery(Family.class);
        Root<Person> from = query.from(Person.class);
        query.select(from.get("family"));
        query.distinct(true);

        TypedQuery<Family> emQuery = em.createQuery(query);
        List<Family> resultList = emQuery.getResultList();
        System.out.println("-----------families from persons-------------------");
        for (Family family : resultList) {
            System.out.println(family);
        }
    }

    public void testCriteriaAPIWhere(){
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();

        em.getTransaction().begin();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Person> query = builder.createQuery(Person.class);
        Root<Family> from = query.from(Family.class);
        query.select(from.get("members"));
        query.where(builder.like(from.get("familyName"), "%Glasch%"));

        TypedQuery<Person> emQuery = em.createQuery(query);
        List<Person> resultList = emQuery.getResultList();
        System.out.println("-----------Kostya-------------------");
        for (Person person : resultList) {
            System.out.println(person.getName());
        }
    }


    public void testCriteriaAPIJoin(){
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();

        em.getTransaction().begin();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Person> query = builder.createQuery(Person.class);
        Root<Person> from = query.from(Person.class);
        Join<Person, Family> family = from.join("family");
        query.select(from);
        query.where(builder.like(family.get("familyName"), "%2%"));
        TypedQuery<Person> emQuery = em.createQuery(query);
        List<Person> resultList = emQuery.getResultList();
        System.out.println("-----------Kostya and Andrey-------------------");
        for (Person person : resultList) {
            System.out.println(person.getName());
        }
    }

    private Family loadFamilyOnce(EntityManager em) {
        Query query = em.createQuery("select f from Family f");
        List res = query.getResultList();
        Family family = (Family)res.get(0);
        List<Person> members = family.getMembers();
        assertEquals(2, members.size());
        return family;
    }

    public void testDelete(){
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();

        em.getTransaction().begin();
        TypedQuery<Person> query = em.createQuery("select p from Person p where p.name like :name", Person.class);
        query.setParameter("name", "Kostya%1");
        List<Person> res = query.getResultList();
        res.forEach(System.out::println);
        Person person = res.get(0);
        em.remove(person);
        em.getTransaction().commit();
        em.close();
    }
}
