import jakarta.persistence.*;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import java.util.List;
import java.util.Properties;

@Table(name = "students")
class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private int age;

    public Student() {}
    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }

    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', age=" + age + "}";
    }
}

public class HibernateCRUDApp {
    private static SessionFactory sessionFactory;

    static {
        Configuration config = new Configuration();

        Properties props = new Properties();
        props.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        props.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/testdb");
        props.put("hibernate.connection.username", "root");
        props.put("hibernate.connection.password", "yourpassword");
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        props.put("hibernate.hbm2ddl.auto", "update"); 
        props.put("hibernate.show_sql", "true");

        config.setProperties(props);
        config.addAnnotatedClass(Student.class);

        sessionFactory = config.buildSessionFactory();
    }

    public static void main(String[] args) {
        createStudent("Alice", 22);
        createStudent("Bob", 24);

        System.out.println("All Students:");
        readStudents();

        updateStudent(1, "Alicia", 23);
        deleteStudent(2);

        System.out.println("After Update & Delete:");
        readStudents();

        sessionFactory.close();
    }

    static void createStudent(String name, int age) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.save(new Student(name, age));
        tx.commit();
        session.close();
    }

    static void readStudents() {
        Session session = sessionFactory.openSession();
        List<Student> students = session.createQuery("FROM Student", Student.class).list();
        students.forEach(System.out::println);
        session.close();
    }

    static void updateStudent(int id, String name, int age) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        Student student = session.get(Student.class, id);
        if (student != null) {
            student.setName(name);
            student.setAge(age);
            session.update(student);
        }
        tx.commit();
        session.close();
    }

    static void deleteStudent(int id) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        Student student = session.get(Student.class, id);
        if (student != null) session.delete(student);
        tx.commit();
        session.close();
    }
}
