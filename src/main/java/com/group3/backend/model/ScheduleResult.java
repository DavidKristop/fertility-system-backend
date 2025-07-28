package com.group3.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

import org.apache.tomcat.util.bcel.Const;

import com.group3.backend.utils.Constants;

@Entity
@Table(name = "schedule_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleResult {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "doctors_note", length = Constants.MAX_CONTENT_UPLOAD_LENGTH)
    private String doctorsNote;

    @OneToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @OneToMany(mappedBy = "scheduleResult")
    private List<ScheduleResultAttachment> attachments;
}
