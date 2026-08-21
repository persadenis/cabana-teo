import java.time.LocalDateTime;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
            Reservation r1 = new Reservation("Andrei", "andrei@email.com", "0712345678",LocalDateTime.of(2026, 8, 20, 15, 0), LocalDateTime.of(2026,8,22,12,0),8,"Salut vrem sa inchiriem cabana pentru 2 nopti! Multumesc!");
            Reservation r2 = new Reservation("Vlad", "vlad@email.com", "0712345378",LocalDateTime.of(2026, 8, 15, 15, 0), LocalDateTime.of(2026,8,17,12,0),8,"Salut vrem sa inchiriem cabana pentru 2 nopti! Multumesc!");
            Reservation r3 = new Reservation("Luca", "luca@email.com", "0712342178",LocalDateTime.of(2026, 8, 12, 15, 0), LocalDateTime.of(2026,8,13,12,0),8,"Salut vrem sa inchiriem cabana pentru 2 nopti! Multumesc!");

            ReservationManager manager = new ReservationManager();
            manager.addReservation(r1);
            manager.addReservation(r2);
            manager.addReservation(r3);
            try {
                Reservation bad = new Reservation(
                        "Alex",
                        "goodemail@email.com",
                        "",
                        LocalDateTime.of(2026, 8, 15, 15, 0),
                        LocalDateTime.of(2026, 8, 15, 15, 1),
                        1,
                        ""
                );
                manager.addReservation(bad);
            }
            catch (IllegalArgumentException e)
            {
                System.out.println("Reason: " + e.getMessage());
            }

            /*
            Reservation found = manager.findReservationById(3);

            System.out.println(found.getStatus());

            manager.updateStatus(found.getId(), ReservationStatus.CONFIRMED);

            System.out.println(found.getStatus());

            manager.deleteReservation(3);
            */

            for (Reservation r : manager.getAllReservations()) {

                System.out.println(r);
                System.out.println();
            }
    }
}
