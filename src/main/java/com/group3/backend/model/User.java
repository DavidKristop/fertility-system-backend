package com.group3.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    @Column(nullable = false)
    private String fullName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    private String address;

    @Column(nullable = false)
    private String passwordHashed;

    @Column(nullable = false)
    private String passwordSecret;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private PatientProfile patientProfile;

    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private DoctorProfile doctorProfile;

    @OneToMany(mappedBy = "sendTo")
    @JsonIgnore
    private List<Reminder> reminders;


    @OneToMany(mappedBy = "patient")
    @JsonIgnore
    private List<RequestAppointment> requestAppointments;

    @OneToMany(mappedBy = "author")
    @JsonIgnore
    private List<Blog> blogs;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "patient")
    private List<Schedule> patientSchedules;

    @OneToMany(mappedBy = "doctor")
    private List<Schedule> doctorSchedules;

    @OneToMany(mappedBy = "patient")
    @JsonIgnore
    private List<Treatment> treatments;

    @OneToMany(mappedBy = "doctor")
    @JsonIgnore
    private List<RequestAppointment> doctorRequestAppointments;

    @OneToMany(mappedBy = "patient")
    private List<RequestAppointment> patientRequestAppointments;

    @OneToMany(mappedBy = "user")
    private List<Payment> payments;

    @OneToMany(mappedBy = "user")
    private List<Refund> refunds;

    
}
