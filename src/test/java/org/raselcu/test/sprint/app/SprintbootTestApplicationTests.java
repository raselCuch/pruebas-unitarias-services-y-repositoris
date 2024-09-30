package org.raselcu.test.sprint.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.raselcu.test.sprint.app.exceptions.DineroInsuficienteException;
import org.raselcu.test.sprint.app.models.Banco;
import org.raselcu.test.sprint.app.models.Cuenta;
import org.raselcu.test.sprint.app.repositories.BancoRepository;
import org.raselcu.test.sprint.app.repositories.CuentaRepository;
import org.raselcu.test.sprint.app.services.CuentaService;
import org.raselcu.test.sprint.app.services.CuentaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

@SpringBootTest
class SprintbootTestApplicationTests {
    // los test no han de tacarse unos con otros, deben ser individuales
    @MockBean
    CuentaRepository cuentaRepository;
    @MockBean
    BancoRepository bancoRepository;
    @Autowired // tendra que ser el '...impl'
    CuentaService service;

    @BeforeEach
    void setup() {
//        cuentaRepository = mock(CuentaRepository.class);
//        bancoRepository = mock(BancoRepository.class);
//
//        service = new CuentaServiceImpl(cuentaRepository, bancoRepository);

//        Datos.CUENTA_001.setSaldo(new BigDecimal("1000")); // reiniciando los datos
//        Datos.CUENTA_002.setSaldo(new BigDecimal("2000"));
//        Datos.BANCO.setTotalTransferencias(0);
    }

    @Test
    void contextLoads() {
        when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());
        when(cuentaRepository.findById(2L)).thenReturn(Datos.crearCuenta002());
        when(bancoRepository.findById(1L)).thenReturn(Datos.crearBanco());

        // 'revisarSaldo' usa el metodo 'findById' 1 vez
        BigDecimal saldoOrigen = service.revisarSaldo(1L);
        BigDecimal saldoDestino = service.revisarSaldo(2L);

        assertEquals("1000", saldoOrigen.toPlainString()); // verifica saldo
        assertEquals("2000", saldoDestino.toPlainString());

        // usar un 'findById' para cada cuenta y otro en banco
        // usa 'update' 2 en cuenta y 1 en banco
        service.transferir(1L, 2L, new BigDecimal("100"), 1L); // transfiere 100 de 1 a 2 en el banco 1

        saldoOrigen = service.revisarSaldo(1L);
        saldoDestino = service.revisarSaldo(2L);

        assertEquals("900", saldoOrigen.toPlainString()); // verifica saldo
        assertEquals("2100", saldoDestino.toPlainString());

        // 'revisarTotalTransferencias' tiene 1 'findById' de banco
        int total = service.revisarTotalTransferencias(1L);

        assertEquals(1, total);

        verify(cuentaRepository, times(3)).findById(1L);
        verify(cuentaRepository, times(3)).findById(2L);
        verify(cuentaRepository, times(2)).save(any(Cuenta.class)); // son 2 en cuenta

        verify(bancoRepository, times(2)).findById(1L); // por defecto prueba en 1
        verify(bancoRepository).save(any(Banco.class));

        verify(cuentaRepository, times(6)).findById(anyLong());
        verify(cuentaRepository, never()).findAll();
    }

    @Test
    void contextLoads2() {
        when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());
        when(cuentaRepository.findById(2L)).thenReturn(Datos.crearCuenta002());
        when(bancoRepository.findById(1L)).thenReturn(Datos.crearBanco());

        // 'revisarSaldo' usa el metodo 'findById' 1 vez
        BigDecimal saldoOrigen = service.revisarSaldo(1L);
        BigDecimal saldoDestino = service.revisarSaldo(2L);

        assertEquals("1000", saldoOrigen.toPlainString()); // verifica saldo
        assertEquals("2000", saldoDestino.toPlainString());

        // usar un 'findById' para cada cuenta y otro en banco
        // usa 'update' 2 en cuenta y 1 en banco
        assertThrows(DineroInsuficienteException.class, ()->{
            service.transferir(1L, 2L, new BigDecimal("1200"), 1L); // transfiere 100 de 1 a 2 en el banco 1
        });

        saldoOrigen = service.revisarSaldo(1L);
        saldoDestino = service.revisarSaldo(2L);

        assertEquals("1000", saldoOrigen.toPlainString()); // verifica saldo
        assertEquals("2000", saldoDestino.toPlainString());

        // 'revisarTotalTransferencias' tiene 1 'findById' de banco
        int total = service.revisarTotalTransferencias(1L);

        assertEquals(0, total);

        verify(cuentaRepository, times(3)).findById(1L);
        verify(cuentaRepository, times(2)).findById(2L);
        verify(cuentaRepository, never()).save(any(Cuenta.class)); // son 2 en cuenta

        verify(bancoRepository, times(1)).findById(1L); // por defecto prueba en 1
        verify(bancoRepository, never()).save(any(Banco.class));

        verify(cuentaRepository, times(5)).findById(anyLong());
        verify(cuentaRepository, never()).findAll();
    }

    @Test
    void contextLoad3(){
        when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());

        Cuenta cuenta1 = service.findById(1L);
        Cuenta cuenta2 = service.findById(1L);

        assertSame(cuenta1, cuenta2); // es el mismo objeto
        assertTrue(cuenta1 == cuenta2);
        assertEquals("Andres", cuenta1.getPersona());
        assertEquals("Andres", cuenta2.getPersona());

        verify(cuentaRepository, times(2)).findById(1L);

    }


}
