import java.time.LocalDateTime;

public class Reservation {
        private int id;
        private String guestName;
        private String email;
        private String phone;
        private LocalDateTime checkIn;
        private LocalDateTime checkOut;
        private int numberOfGuests;
        private String notes;
        private ReservationStatus status;
        private static int nextId = 1;

        public Reservation(String guestName, String email, String phone, LocalDateTime checkIn, LocalDateTime checkOut, int nOfGuests, String notes)
        {

            if(guestName == null || guestName.isBlank())
                throw new IllegalArgumentException("The name can't be empty");

            if(email == null || !email.contains("@"))
                throw new IllegalArgumentException("The email must be valid");

            if(checkOut == null || checkIn == null || !checkOut.isAfter(checkIn))
                throw new IllegalArgumentException("The checkOut should be after the checkIn and should not be null");

            if(nOfGuests < 1 || nOfGuests > 13)
                throw new IllegalArgumentException("The number of guests should be between 1 and 13");

            this.guestName=guestName;
            this.email=email;
            this.phone=phone;
            this.numberOfGuests=nOfGuests;
            this.checkIn=checkIn;
            this.checkOut= checkOut;
            this.notes=notes;
            this.status=ReservationStatus.PENDING;
            this.id= nextId++;
        }

        public int getId()
        {
            return this.id;
        }

        public String getGuestName()
        {
            return this.guestName;
        }


    public String getPhone()
    {
        return this.phone;
    }

    public LocalDateTime getCheckIn()
    {
        return this.checkIn;
    }

    public void setCheckIn(LocalDateTime checkIn)
    {
        this.checkIn = checkIn;
    }


    public LocalDateTime getCheckOut()
    {
        return this.checkOut;
    }

    public void setCheckOut(LocalDateTime checkOut)
    {
        this.checkOut = checkOut;
    }


    public int getNumberOfGuests()
    {
        return this.numberOfGuests;
    }

    public ReservationStatus getStatus()
    {
        return this.status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "Reservation #" + this.id + "\n" +
                "Guest:" + this.guestName+ "\n" +
                "Check-In: " + this.checkIn+ "\n" +
                "Check-Out: " + this.checkOut+ "\n" +
                "Guests: " + this.numberOfGuests + "\n" +
                "Status: " + this.status ;
    }
}
