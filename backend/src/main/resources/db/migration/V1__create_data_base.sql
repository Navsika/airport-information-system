CREATE TABLE airports
(
    airport_id 		INT GENERATED ALWAYS AS IDENTITY,
    iata_code 		VARCHAR(4) NOT NULL UNIQUE,
    airport_name 	VARCHAR(150) NOT NULL UNIQUE,
    city 			VARCHAR(50) NOT NULL,
    country 		VARCHAR(50) NOT NULL,

  	CONSTRAINT pk_airports PRIMARY KEY (airport_id)
);

CREATE TABLE airlines
(
    airline_id 		INT GENERATED ALWAYS AS IDENTITY,
    iata_code 		VARCHAR(3) NOT NULL UNIQUE,
    airline_name 	VARCHAR(100) NOT NULL UNIQUE,
    country 		VARCHAR(50) NOT NULL,

  	CONSTRAINT pk_airlines PRIMARY KEY (airline_id)
);

CREATE TABLE aircraft_models
(
    model_id 			INT GENERATED ALWAYS AS IDENTITY,
    model_name 			VARCHAR(30) NOT NULL UNIQUE,
    manufacturer 		VARCHAR(50) NOT NULL,
    passenger_capacity 	SMALLINT NOT NULL,
    cargo_capacity 		INTEGER NOT NULL,
    max_speed 			SMALLINT NOT NULL,

  	CONSTRAINT pk_models PRIMARY KEY (model_id),
  	CONSTRAINT chk_passenger_capacity_positive CHECK(passenger_capacity > 0),
  	CONSTRAINT chk_cargo_capacity_positive CHECK(cargo_capacity >= 0),
  	CONSTRAINT chk_speed_positive CHECK(max_speed > 0)
);

CREATE TABLE employees
(
    employee_id 	INT GENERATED ALWAYS AS IDENTITY,
    first_name 		VARCHAR(50) NOT NULL,
    last_name 		VARCHAR(50) NOT NULL,
    middle_name 	VARCHAR(50),
    category 		VARCHAR(20) NOT NULL,
    hire_date 		DATE NOT NULL,

  	CONSTRAINT pk_employees PRIMARY KEY (employee_id),
  	CONSTRAINT chk_category CHECK(category IN ('Pilot', 'FlightAttendant')),
  	CONSTRAINT chk_hire_date_correct CHECK(hire_date <= CURRENT_DATE)
);

CREATE TABLE passengers
(
  passenger_id 			INT GENERATED ALWAYS AS IDENTITY,
  last_name 			VARCHAR(50) NOT NULL,
  first_name 			VARCHAR(50) NOT NULL,
  middle_name 			VARCHAR(50),
  passport_number 		VARCHAR(10) NOT NULL UNIQUE,
  passport_expiry_date 	DATE NOT NULL,

  CONSTRAINT pk_passengers PRIMARY KEY (passenger_id),
  CONSTRAINT chk_passport_valid CHECK (passport_expiry_date > CURRENT_DATE)

);

CREATE TABLE aircrafts
(
    aircraft_id 			INT GENERATED ALWAYS AS IDENTITY,
    registration_number 	VARCHAR(8) NOT NULL UNIQUE,
    model_id 				INTEGER NOT NULL,
    airline_id 			INTEGER NOT NULL,
    manufacture_year 		SMALLINT NOT NULL,
    last_maintenance_date DATE,
    flight_hours 			INTEGER NOT NULL,

    CONSTRAINT pk_aircrafts PRIMARY KEY (aircraft_id),
    CONSTRAINT fk_aircraft_model
        FOREIGN KEY (model_id)
        REFERENCES aircraft_models(model_id)
        ON UPDATE CASCADE,

    CONSTRAINT fk_aircraft_airline
        FOREIGN KEY (airline_id)
        REFERENCES airlines(airline_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,

    CONSTRAINT chk_correct_year CHECK (manufacture_year <= EXTRACT(YEAR FROM CURRENT_DATE)),
    CONSTRAINT chk_maintenance_date CHECK (last_maintenance_date <= CURRENT_DATE),
    CONSTRAINT chk_flight_hours CHECK (flight_hours >= 0)
);

CREATE TABLE schedules
(
    schedule_id 		INT GENERATED ALWAYS AS IDENTITY,
    flight_number 		VARCHAR(10) NOT NULL UNIQUE,
    airline_id 			INTEGER NOT NULL,
    departure_airport 	INTEGER NOT NULL,
    arrival_airport 	INTEGER NOT NULL,
    departure_time 		TIME NOT NULL,
    arrival_time 		TIME NOT NULL,
    arrival_day_offset 	SMALLINT NOT NULL,

  	CONSTRAINT pk_schedules PRIMARY KEY (schedule_id),
  	CONSTRAINT fk_schedule_airline
  		FOREIGN KEY (airline_id)
  		REFERENCES airlines(airline_id)
  		ON DELETE CASCADE
  		ON UPDATE CASCADE,

  	CONSTRAINT fk_schedule_departure_airport
  		FOREIGN KEY (departure_airport)
  		REFERENCES airports(airport_id)
  		ON DELETE CASCADE
  		ON UPDATE CASCADE,

  	CONSTRAINT fk_schedule_arrival_airport
  		FOREIGN KEY (arrival_airport)
  		REFERENCES airports(airport_id)
  		ON DELETE CASCADE
  		ON UPDATE CASCADE,

    CONSTRAINT chk_arrival_day_positive CHECK (arrival_day_offset >= 0)
);

CREATE TABLE flights
(
    flight_id 			INT GENERATED ALWAYS AS IDENTITY,
    schedule_id 		INTEGER NOT NULL,
    scheduled_departure TIMESTAMPTZ NOT NULL,
    scheduled_arrival 	TIMESTAMPTZ NOT NULL,
    actual_departure 	TIMESTAMPTZ,
    actual_arrival 		TIMESTAMPTZ,
    aircraft_id 		INTEGER,
    gate 				VARCHAR(5),
    status 				VARCHAR(20) NOT NULL,

  	CONSTRAINT pk_flights PRIMARY KEY (flight_id),
	CONSTRAINT fk_flight_schedule
  		FOREIGN KEY (schedule_id)
  		REFERENCES schedules(schedule_id)
  		ON UPDATE CASCADE,

  	CONSTRAINT fk_flight_aircraft
  		FOREIGN KEY (aircraft_id)
  		REFERENCES aircrafts(aircraft_id)
  		ON DELETE SET NULL
  		ON UPDATE CASCADE,

    CONSTRAINT chk_flight_scheduled CHECK (scheduled_arrival > scheduled_departure),
    CONSTRAINT chk_flight_actual CHECK (actual_departure IS NULL OR actual_arrival IS NULL OR actual_arrival > actual_departure),
  	CONSTRAINT chk_flight_status CHECK (status IN ('Scheduled', 'Delayed', 'Cancelled', 'Check-in', 'Boarding', 'Departed', 'Arrived'))
);

CREATE TABLE tickets
(
    ticket_id 		INT GENERATED ALWAYS AS IDENTITY,
    ticket_number 	VARCHAR(20) NOT NULL UNIQUE,
    passenger_id 	INTEGER,
    flight_id 		INTEGER NOT NULL,
    ticket_class 	VARCHAR(10) NOT NULL,
    seat_number 	VARCHAR(10) NOT NULL,
    purchase_date 	DATE,

  	CONSTRAINT pk_tickets PRIMARY KEY (ticket_id),
  	CONSTRAINT fk_ticket_passenger
  		FOREIGN KEY (passenger_id)
  		REFERENCES passengers(passenger_id)
  		ON DELETE CASCADE
  		ON UPDATE CASCADE,

  	CONSTRAINT fk_ticket_flight
  		FOREIGN KEY (flight_id)
  		REFERENCES flights(flight_id)
  		ON DELETE CASCADE
  		ON UPDATE CASCADE,

    CONSTRAINT uq_ticket_flight_seat UNIQUE (flight_id, seat_number),
  	CONSTRAINT chk_ticket_class CHECK(ticket_class IN (
'Business', 'Economy'))
);

CREATE TABLE assignments
(
    assignment_id 	INT GENERATED ALWAYS AS IDENTITY,
    flight_id 		INTEGER NOT NULL,
    employee_id 	INTEGER,
    employee_role 	VARCHAR(60) NOT NULL,

  	CONSTRAINT pk_assignments PRIMARY KEY (assignment_id),
  	CONSTRAINT fk_assignment_flight
  		FOREIGN KEY (flight_id)
  		REFERENCES flights(flight_id)
  		ON DELETE CASCADE
  		ON UPDATE CASCADE,

  	CONSTRAINT fk_assignment_employee
  		FOREIGN KEY (employee_id)
  		REFERENCES employees(employee_id)
  		ON DELETE SET NULL
		ON UPDATE CASCADE,

  	CONSTRAINT chk_assignment_role CHECK(employee_role IN ('Commander', 'Co-pilot', 'Senior Flight Attendant', 'Flight Attendant')),
    CONSTRAINT uq_assignment_flight_employee UNIQUE (flight_id, employee_id)
);

CREATE TABLE qualifications
(
    qualification_id 	INT GENERATED ALWAYS AS IDENTITY,
    pilot_id			INTEGER NOT NULL,
    model_id 			INTEGER NOT NULL,
    qualification_date	DATE NOT NULL,
    valid_until 		DATE,

  	CONSTRAINT pk_qualifications PRIMARY KEY (qualification_id),
  	CONSTRAINT fk_qualification_pilot
  		FOREIGN KEY (pilot_id)
  		REFERENCES employees(employee_id)
  		ON DELETE CASCADE
  		ON UPDATE CASCADE,
  	CONSTRAINT fk_qualification_model
  		FOREIGN KEY (model_id)
  		REFERENCES aircraft_models(model_id)
  		ON UPDATE CASCADE,

    CONSTRAINT chk_qualification_valid CHECK (valid_until IS NULL OR valid_until > qualification_date),
  	CONSTRAINT chk_qualification_date CHECK (qualification_date <= CURRENT_DATE),
    CONSTRAINT uq_qualification_pilot_model UNIQUE (pilot_id, model_id)
);

CREATE TABLE status_histories
(
    status_history_id 	INT GENERATED ALWAYS AS IDENTITY,
    flight_id 			INTEGER NOT NULL,
    status 				VARCHAR(20) NOT NULL,
    change_time 		TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reason 				TEXT,

  	CONSTRAINT pk_status_histories PRIMARY KEY (status_history_id),
  	CONSTRAINT fk_status_history_flight
  		FOREIGN KEY (flight_id)
  		REFERENCES flights(flight_id),

	CONSTRAINT chk_status CHECK (status IN ('Scheduled', 'Delayed', 'Cancelled', 'Check-in', 'Boarding', 'Departed', 'Arrived'))
);

CREATE TABLE check_ins
(
    check_in_id 			INT GENERATED ALWAYS AS IDENTITY,
    ticket_id 				INTEGER NOT NULL UNIQUE,
    check_in_time			TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    counter_number 			VARCHAR(5),
    terminal 				VARCHAR(3) NOT NULL,
    baggage_count 			SMALLINT NOT NULL,
    total_baggage_weight 	DECIMAL(6,2) NOT NULL,

  	CONSTRAINT pk_check_ins PRIMARY KEY (check_in_id),
  	CONSTRAINT fk_check_in_ticket
  		FOREIGN KEY (ticket_id)
  		REFERENCES tickets(ticket_id)
  		ON UPDATE CASCADE,
  	CONSTRAINT chk_baggage_count CHECK (baggage_count >= 0),
  	CONSTRAINT chk_total_baggafe CHECK (total_baggage_weight >= 0)
);
