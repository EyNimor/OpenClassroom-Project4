package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static DataBasePrepareService dataBasePrepareService;
    private static Ticket ticket;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;

    public ParkingType parkingType = ParkingType.CAR;
    public static String vehicleRegNumber = "ABCDEF";

    @BeforeAll
    private static void setUp() {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
        ticket = new Ticket();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    public void testParkingACar() {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        ticket = ticketDAO.getTicket(vehicleRegNumber);
        assertNotEquals(null, ticket);
        assertEquals(2, parkingSpotDAO.getNextAvailableSlot(parkingType));
    }

    @Test
    public void testParkingLotExit(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        long initTime = System.currentTimeMillis();
        long currentTime = System.currentTimeMillis();
        while(currentTime - initTime != 1000)
        {
            currentTime = System.currentTimeMillis();
        }
        parkingService.processExitingVehicle();
        ticket = ticketDAO.getTicket(vehicleRegNumber);
        assertNotEquals(null, ticket.getOutTime());
        assertNotEquals(0, ticket.getPrice());
    }

}
