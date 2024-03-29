package com.plohoy.bulls.dao;

import com.plohoy.bulls.domain.User;
import com.plohoy.bulls.exception.DaoException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDaoImpl implements UserDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);

//    private EntityManagerFactory factory;
//    private EntityManager entityManager;

    private SessionFactory factory;
    private Session session;

    private static final String ADD_USER_ROLE = "INSERT INTO user_role(user_id, role_id)" +
            "VALUES (? , '3')";

    private static final Map<String, String> jdbcUrlSettings = new HashMap<>();

    static {
        String jdbcDbUrl = System.getenv("JDBC_DATABASE_URL");
        if (null != jdbcDbUrl) {
            jdbcUrlSettings.put("hibernate.connection.url", System.getenv("JDBC_DATABASE_URL"));
        }
        String jdbcDbUserName = System.getenv("JDBC_DATABASE_USERNAME");
        if (null != jdbcDbUserName) {
            jdbcUrlSettings.put("hibernate.connection.username", System.getenv("JDBC_DATABASE_USERNAME"));
        }
        String jdbcDbPassword = System.getenv("JDBC_DATABASE_PASSWORD");
        if (null != jdbcDbPassword) {
            jdbcUrlSettings.put("hibernate.connection.password", System.getenv("JDBC_DATABASE_PASSWORD"));
        }
    }

    public UserDaoImpl() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
        }

        factory = buildSessionFactory();
//        factory = Persistence.createEntityManagerFactory("persistence", jdbcUrlSettings);

    }

//    @Override
//    public Long create(User user) throws DaoException {
//        try {
//            entityManager = factory.createEntityManager();
//            entityManager.getTransaction().begin();
//            entityManager.persist(user);
//            addSimpleRole(user);
//
//            entityManager.getTransaction().commit();
//            entityManager.close();
//
//        } catch (Exception ex) {
//            LOGGER.error(ex.getMessage(), ex);
//            entityManager.getTransaction().rollback();
//            throw new DaoException();
//        }
//
//        return user.getId();
//    }
//
//    @Override
//    public User findById(Long id) throws DaoException {
//        User user = null;
//
//        entityManager = factory.createEntityManager();
//        TypedQuery<User> userTypedQuery = entityManager.createQuery("FROM User WHERE id =:id", User.class)
//                .setParameter("id", id);
//
//        if (userTypedQuery.getResultList().size() > 0) {
//            user = userTypedQuery.getSingleResult();
//        }
//
//        entityManager.close();
//        return user;
//    }
//
//    @Override
//    public User findUser(String login, String password) throws DaoException {
//        User user = null;
//
//        entityManager = factory.createEntityManager();
//        TypedQuery<User> userTypedQuery = entityManager.createQuery("FROM User WHERE login =:login AND password =:password", User.class)
//                .setParameter("login", login)
//                .setParameter("password", password);
//
//        if (userTypedQuery.getResultList().size() > 0) {
//            user = userTypedQuery.getSingleResult();
//        }
//
//        entityManager.close();
//        return user;
//    }
//
//    @Override
//    public User findByLogin(String login) throws DaoException {
//        User user = null;
//
//        entityManager = factory.createEntityManager();
//        TypedQuery<User> userTypedQuery = entityManager.createQuery("FROM User WHERE login =:login", User.class)
//                .setParameter("login", login);
//
//        if (userTypedQuery.getResultList().size() > 0) {
//            user = userTypedQuery.getSingleResult();
//        }
//
//        entityManager.close();
//        return user;
//    }
//
//    @Override
//    public List<User> findAllUsers() throws DaoException {
//
//        entityManager = factory.createEntityManager();
//        List<User> userList = entityManager.createQuery("FROM User")
//                .getResultList();
//
//        entityManager.close();
//        return userList;
//    }
//
//    private void addSimpleRole(User user) {
//        entityManager.createNativeQuery(ADD_USER_ROLE)
//                .setParameter(1, user.getId())
//                .executeUpdate();
//
//    }

    @Override
    public Long create(User user) throws DaoException {
        try {
            session = factory.openSession();
            session.getTransaction().begin();
            session.save(user);
            addSimpleRole(user);

            session.getTransaction().commit();
            session.close();

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            session.getTransaction().rollback();
            throw new DaoException();
        }

        return user.getId();
    }

    @Override
    public User findById(Long id) throws DaoException {
        User user = null;

        session = factory.openSession();
        TypedQuery<User> userTypedQuery = session.createQuery("FROM User WHERE id =:id", User.class)
                .setParameter("id", id);

        if (userTypedQuery.getResultList().size() > 0) {
            user = userTypedQuery.getSingleResult();
        }

        session.close();
        return user;
    }

    @Override
    public User findUser(String login, String password) throws DaoException {
        User user = null;

        session = factory.openSession();
        TypedQuery<User> userTypedQuery = session.createQuery("FROM User WHERE login =:login AND password =:password", User.class)
                .setParameter("login", login)
                .setParameter("password", password);

        if (userTypedQuery.getResultList().size() > 0) {
            user = userTypedQuery.getSingleResult();
        }

        session.close();
        return user;
    }

    @Override
    public User findByLogin(String login) throws DaoException {
        User user = null;

        session = factory.openSession();
        TypedQuery<User> userTypedQuery = session.createQuery("FROM User WHERE login =:login", User.class)
                .setParameter("login", login);

        if (userTypedQuery.getResultList().size() > 0) {
            user = userTypedQuery.getSingleResult();
        }

        session.close();
        return user;
    }

    @Override
    public List<User> findAllUsers() throws DaoException {

        session = factory.openSession();
        List<User> userList = session.createQuery("FROM User")
                .getResultList();

        session.close();
        return userList;
    }

    private void addSimpleRole(User user) {
        session.createNativeQuery(ADD_USER_ROLE)
                .setParameter(1, user.getId())
                .executeUpdate();

    }

    private static SessionFactory buildSessionFactory() {
        try {
            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .configure("hibernate.cfg.xml")
                    .applySettings(jdbcUrlSettings)
                    .build();

            Metadata metadata = new MetadataSources(serviceRegistry).getMetadataBuilder().build();

            return metadata.getSessionFactoryBuilder().build();
        } catch (Throwable ex) {

            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
}
