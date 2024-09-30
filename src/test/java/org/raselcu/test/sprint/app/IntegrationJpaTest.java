package org.raselcu.test.sprint.app;

import org.junit.jupiter.api.Test;
import org.raselcu.test.sprint.app.models.Cuenta;
import org.raselcu.test.sprint.app.repositories.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class IntegrationJpaTest {
    @Autowired
    CuentaRepository cuentaRepository;

    @Test
    void testFindById() {
        Optional<Cuenta> cuenta = cuentaRepository.findById(1L);

        assertTrue(cuenta.isPresent());
        assertEquals("andres", cuenta.orElseThrow().getPersona());
    }

    @Test
    void testFindByPersona() {
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("andres");

        assertTrue(cuenta.isPresent());
        assertEquals("andres", cuenta.orElseThrow().getPersona());
        assertEquals("1000.00", cuenta.orElseThrow().getSaldo().toPlainString());
    }

    @Test
    void testFindByPersonaThrowException() {
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("rod");

        assertThrows(NoSuchElementException.class, ()->{
            cuenta.orElseThrow() ;
        });

        assertFalse(cuenta.isPresent());
    }

    @Test
    void testFindAll() {
        List<Cuenta> cuentas = cuentaRepository.findAll();

        assertFalse(cuentas.isEmpty());
        assertEquals(2, cuentas.size());

    }

    @Test
    void testSave() {
        Cuenta cuentaPepe = new Cuenta(null, "pepe", new BigDecimal("3000"));
//        Cuenta save = cuentaRepository.save(cuentaPepe);
        Cuenta cuenta = cuentaRepository.save(cuentaPepe);

//        Cuenta cuenta = cuentaRepository.findByPersona("pepe").orElseThrow();
//        Cuenta cuenta = cuentaRepository.findById(save.getId()).orElseThrow();

        assertEquals("pepe", cuenta.getPersona());
        assertEquals("3000", cuenta.getSaldo().toPlainString());

//        assertEquals(3, cuenta.getId());
    }

    @Test
    void testUpdate() {
        Cuenta cuentaPepe = new Cuenta(null, "pepe", new BigDecimal("3000"));
//        Cuenta save = cuentaRepository.save(cuentaPepe);
        Cuenta cuenta = cuentaRepository.save(cuentaPepe);

//        Cuenta cuenta = cuentaRepository.findByPersona("pepe").orElseThrow();
//        Cuenta cuenta = cuentaRepository.findById(save.getId()).orElseThrow();

        assertEquals("pepe", cuenta.getPersona());
        assertEquals("3000", cuenta.getSaldo().toPlainString());

        cuenta.setSaldo(new BigDecimal("3800"));
        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);

        assertEquals("pepe", cuentaActualizada.getPersona());
        assertEquals("3800", cuentaActualizada.getSaldo().toPlainString());

    }

    @Test
    void testDelete() {
        Cuenta cuenta = cuentaRepository.findById(2L).orElseThrow();
        assertEquals("john", cuenta.getPersona());

        cuentaRepository.delete(cuenta);

        assertThrows(NoSuchElementException.class, ()->{
//           cuentaRepository.findByPersona("john").orElseThrow();
           cuentaRepository.findById(2L).orElseThrow();
        });
        assertEquals(1, cuentaRepository.findAll().size());
    }


}

