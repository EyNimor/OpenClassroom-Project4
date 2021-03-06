package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class UpdateTicketTest {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static DataBasePrepareService dataBasePrepareService;
    private static TicketDAO ticketDAO;

    private static Ticket ticket;
    private static Ticket controlTicket;

    private final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    @BeforeAll
    private static void setUp() {
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() {
        dataBasePrepareService.clearDataBaseEntries();

        ticket = new Ticket();
        controlTicket = new Ticket();
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - ( 60 * 60 * 1000));
        ticket.setInTime(inTime);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setParkingSpot(parkingSpot);
        ticket.setId(1);
        ticket.setPrice(0);
        ticket.setOutTime(null);
    }

    @Test
    public void updatingTicketInDB() {
        ticketDAO.saveTicket(ticket);
        Date outTime = new Date();
        ticket.setOutTime(outTime);
        ticket.setPrice(Fare.CAR_RATE_PER_HOUR);
        ticketDAO.updateTicket(ticket);
        controlTicket = ticketDAO.getTicket("ABCDEF");
        assertNotNull(controlTicket.getOutTime());
        assertEquals(ticket.getPrice(), controlTicket.getPrice());
    }

}
