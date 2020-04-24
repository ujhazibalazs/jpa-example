package person;

import com.github.javafaker.Faker;
import org.hibernate.internal.build.AllowSysOut;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Main {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-example");

    public static Person randomPerson() {
        Person person = new Person();
        Faker faker = new Faker();

        String name = faker.name().fullName();
        Date date = faker.date().birthday();
        LocalDate dob = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Person.Gender gender = faker.options().option(Person.Gender.class);

        Address address = new Address();
        address.country = faker.address().country();
        address.state = faker.address().state();
        address.city = faker.address().city();
        address.streetAddress = faker.address().streetAddress();
        address.zip = faker.address().zipCode();

        String email = faker.internet().emailAddress();
        String profession = faker.company().profession();

        person.setName(name);
        person.setDob(dob);
        person.setGender(gender);
        person.setAddress(address);
        person.setEmail(email);
        person.setProfession(profession);

        return person;
    }

    private static List<Person> getPersons() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT l FROM Person l", Person.class).getResultList();
        } finally {
            em.close();
        }
    }

    public static void main(String[] args) {
        EntityManager em = emf.createEntityManager();
        int darab = 1000;
        try {
            em.getTransaction().begin();
            for (int i=0; i<darab; i++) {
                Person person = randomPerson();
                em.persist(person);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        log.info("People:");
        getPersons().forEach(log::info);
    }
}
