package com.plohoy.bulls.service;

import com.plohoy.bulls.domain.Player;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class PlayerServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerServiceTest.class);

    private static EntityManagerFactory emf;
    private static EntityManager em;

    @BeforeClass
    public static void init() {
        emf = DBInit.startUp();
    }

    @Test
    public void checkPersistData() {
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();

            Player p = getPlayer();
            LOGGER.info(p.toString());
            
            em.persist(p);
            em.flush();

            em.getTransaction().commit();
            em.close();
        } catch (Exception e) {
            em.getTransaction().rollback();
            LOGGER.error(e.getMessage(), e);
        }
    }

    private Player getPlayer() {
        Player player = new Player();
        player.setFirstName("Иван");
        player.setLastName("Тестовый");
        player.setLogin("Vano21");
        player.setPassword("qwerty");
        player.setScore(12);

        return player;
    }
}