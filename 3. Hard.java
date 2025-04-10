import jakarta.persistence.*;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import java.util.Date;

class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double balance;

    public Account() {}
    public Account(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}

@Entity
class TransactionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fromAccountId;
    private Long toAccountId;
    private double amount;
    private Date timestamp = new Date();

    public TransactionRecord() {}
    public TransactionRecord(Long from, Long to, double amount) {
        this.fromAccountId = from;
        this.toAccountId = to;
        this.amount = amount;
    }
}

public class SimpleBankingApp {
    private static SessionFactory sessionFactory;
    public static void main(String[] args) {
        sessionFactory = new Configuration()
                .configure() 
                .addAnnotatedClass(Account.class)
                .addAnnotatedClass(TransactionRecord.class)
                .buildSessionFactory();
        Long acc1 = createAccount("Alice", 500);
        Long acc2 = createAccount("Bob", 300);

        transferMoney(acc1, acc2, 200);
        transferMoney(acc1, acc2, 1000); 

        sessionFactory.close();
    }

    static Long createAccount(String name, double balance) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        Account acc = new Account(name, balance);
        session.persist(acc);
        tx.commit();
        session.close();
        return acc.getId();
    }

    static void transferMoney(Long fromId, Long toId, double amount) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Account from = session.get(Account.class, fromId);
            Account to = session.get(Account.class, toId);

            if (from.getBalance() < amount)
                throw new RuntimeException("Insufficient funds");

            from.setBalance(from.getBalance() - amount);
            to.setBalance(to.getBalance() + amount);

            session.update(from);
            session.update(to);

            TransactionRecord tr = new TransactionRecord(fromId, toId, amount);
            session.persist(tr);

            tx.commit();
            System.out.println("Transaction successful!");
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.out.println("Transaction failed: " + e.getMessage());
        } finally {
            session.close();
        }
    }
}
