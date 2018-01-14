import junit.framework.TestCase;
import lombok.SneakyThrows;
import lombok_test.*;
import org.eclipse.persistence.internal.jpa.EntityGraphImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.jpa.parsing.IntegerLiteralNode;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.sessions.CopyGroup;
import orm_test.*;
import orm_test.Person;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        EntityManagerImpl em = (EntityManagerImpl) factory.createEntityManager();
        em.getTransaction().begin();
        persistFamily(em, 1);
        persistFamily(em, 2);
        em.getTransaction().commit();
        em.close();
    }

    public void testFind() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl) factory.createEntityManager();
        Person kostya = em.find(Person.class, 404);
        System.out.println(kostya);
        em.close();
    }

    public void testPersist() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl) factory.createEntityManager();
        em.getTransaction().begin();
        Person kostya = em.find(Person.class, 404);
        kostya.setName("Arina");
        em.persist(kostya);
        em.getTransaction().commit();
        System.out.println(kostya);

        kostya = em.find(Person.class, 404);
        System.out.println(kostya);

        em.close();
    }

    public void testMerge() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl) factory.createEntityManager();
        Person kostya = em.find(Person.class, 404);
        em.close();
        //----new EM
        em = (EntityManagerImpl) factory.createEntityManager();
        kostya.setName("Kostya Merged 2");

        em.getTransaction().begin();
        em.merge(kostya);
        em.getTransaction().commit();
        System.out.println(kostya);

        kostya = em.find(Person.class, 404);
        System.out.println(kostya);
        em.close();
    }

    public void testVersion() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl) factory.createEntityManager();
        em.getTransaction().begin();
        Person kostya = em.find(Person.class, 404);
        kostya.setName(kostya.getName() + "!");
        em.merge(kostya);
        em.getTransaction().commit();
        System.out.println(kostya);

        kostya = em.find(Person.class, 404);
        System.out.println(kostya);

        em.close();

    }

    public void testDeleteCascade() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl) factory.createEntityManager();
        em.getTransaction().begin();
        Family family = em.find(Family.class, 356);
        em.remove(family);
        em.getTransaction().commit();
        em.close();
    }

    public void testOptimisticLock() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl) factory.createEntityManager();
        em.getTransaction().begin();
        Person kostya = em.find(Person.class, 404);

        EntityManagerImpl em2 = (EntityManagerImpl) factory.createEntityManager();
        em2.getTransaction().begin();
        Person kostya2 = em2.find(Person.class, 404);


        kostya.setName(kostya.getName() + "!");
        em.merge(kostya);
        em.getTransaction().commit();
        System.out.println(kostya);

        kostya2.setName(kostya2.getName() + "+");
        em2.merge(kostya2);
        em2.getTransaction().commit();
        System.out.println(kostya2);


        em.close();
        em2.close();
    }


//    public void testSelectDistict(){
//        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
//        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();
//        em.getTransaction().begin();
//
//    }

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
    public void testRollbackTransaction() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl) factory.createEntityManager();
        em.getTransaction().begin();
        persistFamily(em, 3);
        em.getTransaction().rollback();
        em.close();
    }

    @SneakyThrows
    public void testLoadFamily() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();
        Family family = loadFamilyByNameLike(em, "%1%");
        System.out.println(family);
        //testing L1 cache
//        em.clear();
        family = loadFamilyByNameLike(em, "%1%");
        System.out.println(family);
        em.close();

        //testing L2 cache
        em = factory.createEntityManager();
        family = loadFamilyByNameLike(em, "%1%");
        System.out.println(family);
    }

    //testing @BatchFetch
    public void testLoadAllFamilies() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();
        List<Family> families = loadAllFamilies((EntityManagerImpl) em);
        families.forEach(f -> {
            System.out.println(f);
            f.getMembers().forEach(m -> System.out.println("    " + m));
        });
    }

    public void testJoin() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl) factory.createEntityManager();
        em.getTransaction().begin();
        TypedQuery<Person> query = em.createQuery("select p from Person p join p.family f where f.familyName like :name",
                Person.class);
        query.setParameter("name", "%2%");
        List<Person> list = query.getResultList();
        list.forEach(System.out::println);
    }

    public void testNativeQuery() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl) factory.createEntityManager();
        Query query = em.createNativeQuery("select * from family");
        List res = query.getResultList();
        Object family = res.get(0);
    }

    public void testCriteriaAPI() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl) factory.createEntityManager();

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

    public void testCriteriaAPI2() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl) factory.createEntityManager();

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

    public void testCriteriaAPIWhere() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl) factory.createEntityManager();

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


    public void testCriteriaAPIJoin() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl) factory.createEntityManager();

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

    private Family loadFamilyByNameLike(EntityManager em, String pattern) {
        Query query = em.createQuery("select f from Family f where f.familyName like :pattern");
        query.setParameter("pattern", pattern);
        List res = query.getResultList();
        Family family = (Family) res.get(0);
//        List<Person> members = family.getMembers();//cache test
//        members.forEach(System.out::println);
        return family;
    }

    private List<Family> loadAllFamilies(EntityManagerImpl em) {
        TypedQuery<Family> query = em.createQuery("select f from Family f", Family.class);
        return query.getResultList();
    }

    public void testDelete() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl) factory.createEntityManager();

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

    public void testCreateManyToMany() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl) factory.createEntityManager();

        em.getTransaction().begin();
        Person kostya = em.find(Person.class, 301);
        Person andrey = em.find(Person.class, 302);
        Person kostyaM = em.find(Person.class, 404);

        kostya.getFriends().add(andrey);
        kostya.getFriends().add(kostyaM);
        andrey.getFriends().add(kostya);
//        kostyaM.getFriends().add

        em.persist(kostya);
        em.getTransaction().commit();
        em.close();

    }

    public void testLoadManyToMany() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();
        Person kostya = em.find(Person.class, 301);
        Person andrey = em.find(Person.class, 302);
        Person kostyaM = em.find(Person.class, 404);
        assertTrue(kostya.getFriends().contains(kostyaM));
        assertTrue(andrey.getFriends().contains(kostya));
    }

    public void testInheritance_MappedSuperclass() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        Person kostya = em.find(Person.class, 301);
        kostya.setWeightKG(30);
        kostya.setHeightCM(120);
        kostya.setNationality(Nationality.RUSSIAN);
        em.persist(kostya);
        em.getTransaction().commit();
        em.close();

        em = factory.createEntityManager();
        kostya = em.find(Person.class, 301);
        assertEquals(Nationality.RUSSIAN, kostya.getNationality());
        assertEquals(30, kostya.getWeightKG());
    }

    public void testInheritanceDiscriminator() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        Dog dog = new Dog();
        dog.setName("Tuzik");
        dog.setTailLength(30);
        dog.setWeightKG(12);
        dog.setHeightCM(45);
        em.persist(dog);
        em.getTransaction().commit();
        em.close();
    }

    public void testInheritanceEntity_SingleTable(){
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        Airplane airplane = new Airplane();
        airplane.setModel("SSJ 100");
        airplane.setCapacity(110);
        airplane.setMaxSpeed(900);
        airplane.setMaxAltitude(10000);
        airplane.setWingsSpan(28);

        Bus bus = new Bus();
        bus.setModel("PAZ");
        bus.setCapacity(60);
        bus.setMaxSpeed(80);
//        bus.setDoorsCount(2);
//        bus.setPassengerSeats(40);

        em.persist(airplane);
        em.persist(bus);
        em.getTransaction().commit();
        em.close();
    }

    public void testJPQLHierarchySelect(){
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();
        TypedQuery<Bus> query = em.createQuery("select b from Bus b", Bus.class);
        List<Bus> res = query.getResultList();
        res.forEach(System.out::println);
        em.close();
    }

    public void testJPQLManyToManySelect(){
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();
        TypedQuery<Person> query = em.createQuery("select p.friends from Person p where p.id = 301", Person.class);
        List<Person> res = query.getResultList();
        res.forEach(System.out::println);
    }

    public void testJPQLManyToManySelectField(){
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();
        TypedQuery<Object[]> query = em.createQuery("select f.name, f.dob from Person p " +
                " join p.friends f"
                + " where p.id = 301", Object[].class);
        List<Object[]> res = query.getResultList();
        res.forEach(r -> {
            System.out.println(r[0] + ", dob: " + r[1]);
        });
    }

    public void testJPQLType(){
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();
        TypedQuery<Transport> query = em.createQuery("select t from Transport t " +
                "where TYPE(t) <> Bus", Transport.class);
        List<Transport> res = query.getResultList();
        res.forEach(System.out::println);
    }

    public void testJoinFetch(){
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();
        TypedQuery<Person> query = em.createQuery("select p from Person p " +
                "join fetch p.family", Person.class);
        List<Person> res = query.getResultList();
        res.forEach(System.out::println);

    }

    public void testNamedQuery(){
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();
        TypedQuery<Family> query = em.createNamedQuery("familyByName", Family.class);
        query.setParameter("name", "%aschen%");
        List<Family> res = query.getResultList();
        res.forEach(System.out::println);

    }

    public void testJPQLWhereBetween(){
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();
        TypedQuery<Transport> query = em.createQuery("select t from Transport t " +
                "where t.capacity between 1 and 100", Transport.class);
        List<Transport> res = query.getResultList();
        res.forEach(System.out::println);
    }

    public void testJPQLIn(){
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();
        TypedQuery<Transport> query = em.createQuery("select t from Transport t " +
                "where t.model in (\"PAZ\")", Transport.class);
        List<Transport> res = query.getResultList();
        res.forEach(System.out::println);
    }

    public void testJPQLGroupBy(){
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();
        TypedQuery<Object[]> query = em.createQuery("select f.familyName, count(f.members) from Family f " +
                "group by f.familyName", Object[].class);
        List<Object[]> res = query.getResultList();
        res.forEach(r -> {
            System.out.println(r[0] + ", members: " + r[1]);
        });
    }

    public void testJPQLGroupBy2(){
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();
        TypedQuery<Object[]> query = em.createQuery("select f, count(f.members) from Family f " +
                "group by f order by f.familyName", Object[].class);
        List<Object[]> res = query.getResultList();
        res.forEach(r -> {
            System.out.println(r[0] + ", members: " + r[1]);
        });
    }

    public void testJPQLAggregate(){
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();
        TypedQuery<Object[]> query = em.createQuery("select MAX(f.id), COUNT(distinct f.familyName) from Family f", Object[].class);
        List<Object[]> res = query.getResultList();
        res.forEach(r -> {
            System.out.println("max id: " + r[0] + ", distinct names: " + r[1]);
        });
    }



    public void testJPQLDelete(){
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();
        em.getTransaction().begin();
        int deleted = em.createQuery("delete from Transport t where t.id = 101").executeUpdate();
        System.out.println("deleted = " + deleted);
        em.getTransaction().commit();
        em.close();
    }

    public void testJPQLUpdate(){
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();
        em.getTransaction().begin();
        int updated = em.createQuery("update Family f set f.familyName = \"Smirnovs\" where f.id = 406").executeUpdate();
        System.out.println("updated = " + updated);
        em.getTransaction().commit();
        em.close();
    }

    public void testJPQLFunction(){
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();
        TypedQuery<Double> query =
                em.createQuery("select distinct FUNCTION('date_part', 'YEAR', p.dob) from Person p", Double.class);
        List<Double> res = query.getResultList();
        res.forEach(System.out::println);
    }
    //requires weaving = true and vm param  -javaagent:eclipselink.jar
    //todo отключить lazy load после detach нельзя??
    public void testEntityGraph(){
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();

        EntityGraph<Family> graph = em.createEntityGraph(Family.class);
        graph.addAttributeNodes("id");
        Subgraph<Person> members = graph.addSubgraph("members", Person.class);
        members.addAttributeNodes("name");

        em.getTransaction().begin();
        TypedQuery<Family> query = em.createQuery("select f from Family f join fetch f.members where f.id = 406", Family.class);
        query.setHint("javax.persistence.fetchgraph", graph);
        Family family = query.getSingleResult();

        em.detach(family);
        System.out.println("BEFORE DETACH");
        for (Person person : family.getMembers()) {
            em.detach(person);
        }
        CopyGroup group = new CopyGroup();
        group.addAttribute("id");
        group.addAttribute("members.name");
        Family fCopy = (Family) em.unwrap(JpaEntityManager.class).copy(family, group);

        System.out.println("DETACHED");
        em.getTransaction().commit();
        em.close();
        factory.close();
        System.out.println("EM CLOSED");

        System.out.println(fCopy.getMembers().get(0).getName());
    }

    public void testDetached(){
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManagerImpl em = (EntityManagerImpl)factory.createEntityManager();
        Family family = em.find(Family.class, 406);
        em.detach(family);
        family.setMembers(null);
        em.close();
        System.out.println("EM CLOSED");

        System.out.println(family);
    }
}
