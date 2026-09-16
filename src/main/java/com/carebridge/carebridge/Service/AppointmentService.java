package com.carebridge.carebridge.Service;

import com.carebridge.carebridge.Enum.AppointmentStatus;
import com.carebridge.carebridge.Exception.SlotAlreadyBookedException;
import com.carebridge.carebridge.Repository.AppointmentRepo;
import com.carebridge.carebridge.Repository.DoctorRepo;
import com.carebridge.carebridge.Repository.PatientRepo;
import com.carebridge.carebridge.Dto.AppointmentRequestDTO;
import com.carebridge.carebridge.entity.Appointments;
import com.carebridge.carebridge.entity.DoctorDetails;
import com.carebridge.carebridge.entity.PatientDetails;
import com.mongodb.DuplicateKeyException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepo appointmentRepo;
    @Autowired
    private PatientRepo patientRepo;
    @Autowired
    private DoctorRepo doctorRepo;
    public List<LocalTime> getBookedSlots(
            ObjectId doctorId,
            LocalDate date) {

        LocalDateTime start =
                date.atStartOfDay();

        LocalDateTime end =
                date.plusDays(1)
                        .atStartOfDay();

        List<Appointments> appointments =
                appointmentRepo
                        .findByDoctorIdAndAppointmentDateBetween(
                                doctorId,
                                start,
                                end
                        );

        return appointments.stream()
                .filter(appointment ->
                        appointment.getStatus()
                                == AppointmentStatus.PENDING
                                ||
                                appointment.getStatus()
                                        == AppointmentStatus.CONFIRMED
                )
                .map(appointment ->
                        appointment
                                .getAppointmentDate()
                                .toLocalTime()
                )
                .toList();
    }
    @Transactional
    public Appointments addAppointment(ObjectId id, AppointmentRequestDTO request) {
        PatientDetails patient=patientRepo.findByUserid(id);
        DoctorDetails doctor=doctorRepo.findById(request.getDoctorId())
                .orElseThrow(()->new RuntimeException("Doctor Not Found"));
        boolean booked=appointmentRepo.existsByDoctorIdAndAppointmentDate(request.getDoctorId(), request.getAppointmentDate());
        if(booked){
            throw new SlotAlreadyBookedException("This Time Slot is Already Booked");
        }

        Appointments appointment = new Appointments();
        appointment.setPatientId(patient.getId());
        appointment.setDoctorId(doctor.getId());
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setReason(request.getReason());
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setCreatedAt(LocalDateTime.now());
        try{
            return appointmentRepo.save(appointment);
        }catch(DuplicateKeyException e){
            throw new SlotAlreadyBookedException("This time slot was just booked by another patient");
        }
    }
    public List<Appointments> getappointmentsfordoctor(ObjectId userid) {
        DoctorDetails doctor=doctorRepo.findByUserid(userid);
        ObjectId doctorId=doctor.getId();
        return appointmentRepo.findByDoctorId(doctorId);
    }
    public List<Appointments> getappointmentsforpatient(ObjectId userid) {
        PatientDetails patient=patientRepo.findByUserid(userid);
        ObjectId patientId=patient.getId();
        return appointmentRepo.findByPatientId(patientId);
    }
    public Appointments confirmAppointment(ObjectId userid, ObjectId appointmentId) throws AccessDeniedException {
        DoctorDetails doctor=doctorRepo.findByUserid(userid);
        Appointments appointment = appointmentRepo.findById(appointmentId).orElseThrow(()->new RuntimeException("Appointment Not Found"));
        if(!appointment.getDoctorId().equals(doctor.getId())) {
            throw new AccessDeniedException("This appointment has already been approved by another doctor.");
        }
        if(appointment.getStatus().equals(AppointmentStatus.CANCELLED)) {
            throw new AccessDeniedException("Appointment was already cancelled by Patient");
        }
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        return appointmentRepo.save(appointment);
    }
    public Appointments cancelAppointment(ObjectId userid, ObjectId appointmentId) throws AccessDeniedException {
        PatientDetails patient=patientRepo.findByUserid(userid);
        Appointments appointment=appointmentRepo.findById(appointmentId).orElseThrow(()->new RuntimeException("Appointment Not Found"));
        if(!appointment.getPatientId().equals(patient.getId())) {
            throw  new AccessDeniedException("You cannot modify this appointment");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        return appointmentRepo.save(appointment);
    }


}
